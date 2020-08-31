package top.chenzhimeng.fu_community.controller;

import com.aliyuncs.exceptions.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.chenzhimeng.fu_community.model.Comment;
import top.chenzhimeng.fu_community.model.User;
import top.chenzhimeng.fu_community.service.ICommentService;
import top.chenzhimeng.fu_community.service.IUserService;
import top.chenzhimeng.fu_community.util.FileUtil;
import top.chenzhimeng.fu_community.util.MonitorUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentController {

    @Autowired
    private ICommentService commentService;
    @Autowired
    private IUserService userService;
    @Value(("${protocol-address}"))
    private String protocolAddress;
    @Autowired
    private ExecutorService threadPool;

    /**
     * 评论动态或者评论回复
     * 需实名认证
     *
     * @param request 用于获取用户id
     * @param comment 将要插入的评论信息
     * @param picFile 评论图片
     * @return {
     * "result" : true|false,
     * "msg" : ""
     * }
     */
    @PostMapping
    public Map<String, Object> comment(HttpServletRequest request, Comment comment, MultipartFile picFile) throws ClientException {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("{userId : {}, newsId : {}, replyId : {}, text : {}, pic : {}}", userId, comment.getNewsId(),
                comment.getReplyId(), comment.getText(), picFile == null ? null : picFile.getOriginalFilename());
        comment.setUserId(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("result", false);

        if (MonitorUtil.textIsSpam(comment.getText())) {
            map.put("msg", "言论违规");
            return map;
        }

        String url = null;
        String today = LocalDate.now().toString();

        if (picFile != null) {
            String originalFilename = picFile.getOriginalFilename();
            assert originalFilename != null;
            String suffix = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
            if (!suffix.matches("^(?i)JPG|PNG|JPEG|BMP|GIF|WEBP$")) {
                map.put("msg", "存在不支持的文件格式");
                return map;
            }
            url = protocolAddress + today + '/' + UUID.randomUUID().toString() + '.' + suffix;
            comment.setPic(url);
        }

        commentService.comment(comment);
        if (comment.getPic() != null) {
            final String finalUrl = url;
            threadPool.submit(() -> {
                assert picFile != null;
                try {
                    FileUtil.transferTo(picFile, today, finalUrl.substring(finalUrl.lastIndexOf('/') + 1));
                    MonitorUtil.imgScan(finalUrl);
                } catch (IOException | ClientException e) {
                    log.info(String.valueOf(e));
                }
            });
        }

        map.put("result", true);
        return map;
    }

    /**
     * 获取某动态或某评论的回复
     *
     * @param request 用于获取用户id查询是否点赞
     * @param pageNum 页数
     * @param length  长度
     * @param newsId  动态id
     * @param replyId 评论id
     * @param orderBy 排序方式（"likesNum"|"time"） DESC
     * @return comments
     */
    @GetMapping("/{pageNum}/{length}")
    public List<Comment> getAll(HttpServletRequest request, @PathVariable Integer pageNum, @PathVariable Integer length,
                                Integer newsId, Integer replyId, String orderBy) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("{userId : {}, pageNum : {}, length : {}, newsId : {}, replyId : {}, orderBy : {}}", userId, pageNum,
                length, newsId, replyId, orderBy);

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("offset", pageNum * length);
        map.put("length", length);
        map.put("newsId", newsId);
        map.put("replyId", replyId);
        map.put("orderBy", orderBy);
        List<Comment> comments = commentService.findPart(map);
        //处理敏感数据
        for (Comment comment : comments) {
            User newUser = new User();
            User oldUser = comment.getUser();
            newUser.setUserId(oldUser.getUserId());
            newUser.setStudentName(oldUser.getStudentName());
            newUser.setAvatar(oldUser.getAvatar());
            comment.setUser(newUser);
        }
        return comments;
    }

    /**
     * 删除评论
     *
     * @param commentId 评论id
     */
    @DeleteMapping
    public void delete(Integer commentId, HttpServletRequest request) {
        commentService.delete(Map.of("userId", (Integer) request.getAttribute("userId"), "commentId", commentId));
    }
}
