package top.chenzhimeng.fu_community.controller;

import com.alibaba.fastjson.JSONArray;
import com.aliyuncs.exceptions.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.chenzhimeng.fu_community.model.News;
import top.chenzhimeng.fu_community.model.Organization;
import top.chenzhimeng.fu_community.service.INewsService;
import top.chenzhimeng.fu_community.service.IOrganizationService;
import top.chenzhimeng.fu_community.service.IUserService;
import top.chenzhimeng.fu_community.util.FileUtil;
import top.chenzhimeng.fu_community.util.MonitorUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("/news")
@Slf4j
public class NewsController {

    @Autowired
    private INewsService newsService;
    @Autowired
    private IOrganizationService organizationService;
    @Autowired
    private IUserService userService;
    @Autowired
    private ExecutorService threadPool;
    @Value(("${protocol-address}"))
    private String protocolAddress;

    /**
     * 发布动态 文字需同步审核 多媒体异步审核
     *
     * @param request 用于获取用户id
     * @param news    发布的内容
     * @param files   发布的媒体
     * @return {
     * "result":true|false,
     * "msg":"错误信息"
     * }
     */
    @PostMapping()
    public Map<String, Object> publish(HttpServletRequest request, News news, MultipartFile[] files) throws ClientException {
        Integer userId = (Integer) request.getAttribute("userId");
        String text = news.getText();
        log.info("publish news {publisherId: {}, organizationId: {}, text: {}}", userId, news.getOrganizationId(), text);
        news.setPublisherId(userId);

        Map<String, Object> map = new HashMap<>();
        map.put("result", false);

        if ((text == null || text.isBlank()) && (files == null || files.length == 0)) {
            map.put("msg", "无内容");
            return map;
        }

        if (!(news.getOrganizationId() == null || organizationService.checkAuth(news))) {
            map.put("msg", "无发布权限");
            return map;
        }

        if (text != null && !text.isBlank() && MonitorUtil.textIsSpam(text)) {
            map.put("msg", "言论违规");
            return map;
        }

        String[] urls = null;
        String today = LocalDate.now().toString();

        if (files != null && files.length > 0) {
            urls = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                String originalFilename = files[i].getOriginalFilename();
                assert originalFilename != null;
                String suffix = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
                if (!suffix.matches("^(?i)JPG|PNG|JPEG|BMP|GIF|WEBP|AVI|flv|MP4|MPG|ASF|WMV|MOV|WMA|RMVB|RM|FLASH|TS$")) {
                    map.put("msg", "存在不支持的文件格式");
                    return map;
                }
                urls[i] = protocolAddress + today + '/' + UUID.randomUUID().toString() + '.' + suffix;
            }

            news.setMedia(JSONArray.toJSONString(urls));
        }

        newsService.publish(news);
        if (news.getMedia() != null) {
            final String[] finalUrls = urls;
            threadPool.submit(() -> {
                assert files != null;
                for (int i = 0; i < files.length; i++) {
                    try {
                        FileUtil.transferTo(files[i], today, finalUrls[i].substring(finalUrls[i].lastIndexOf('/') + 1));
                        if (finalUrls[i].substring(finalUrls[i].lastIndexOf('.') + 1).matches("(?i)JPG|PNG|JPEG|BMP|GIF|WEBP")) {
                            MonitorUtil.imgScan(finalUrls[i]);
                        } else {
                            MonitorUtil.videoScan(finalUrls[i]);
                        }
                    } catch (IOException | ClientException e) {
                        log.info(String.valueOf(e));
                    }
                }
            });
        }
        map.put("result", true);
        return map;
    }

    /**
     * 图片视频异步审核的回调函数
     * 若违规则删除对应资源
     *
     * @param checksum 用于验证请求是否被篡改
     * @param content  请求内容
     * @param response 用于返回错误码
     */
    @PostMapping("/callback")
    public void monitorCallback(String checksum, String content, HttpServletResponse response) throws NoSuchAlgorithmException, IOException {
        log.info("{checksum : {}, content : {}}", checksum, content);
        Map<String, Object> map = MonitorUtil.mediaAnalyze(checksum, content);
        if (map == null) {
            response.sendError(500);
            return;
        }
        if ((boolean) map.get("result")) {
            if (!FileUtil.delete((String) map.get("url"))) {
                response.sendError(500);
                log.error("资源删除失败");
            }
        }
    }

    /**
     * 分页获取所有动态|分页获取关注动态
     *
     * @param request        用于获取用户id来判断是否关注发动态者，以及是否点赞
     * @param pageNum,length 分页查询
     * @return news
     */
    @GetMapping({"/{pageNum}/{length}", "/subscribed/{pageNum}/{length}"})
    public List<News> getNews(HttpServletRequest request, @PathVariable("pageNum") Integer pageNum,
                              @PathVariable("length") Integer length) {
        String requestURI = request.getRequestURI();
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("{requestURI : {}, userId : {}, pageNum : {}, length : {}}", requestURI, userId, pageNum, length);
        List<News> news;
        if (requestURI.matches("/fu-community/news/\\d+/\\d+")) {
            news = newsService.findPart(userId, pageNum * length, length);
        } else {
            news = newsService.findSubscribedPart(userId, pageNum * length, length);
        }
        for (News oneNew : news) {
            Organization organization = oneNew.getOrganization();
            if (organization == null) {
                oneNew.getPublisher().setPassword(null);
            } else {
                oneNew.setPublisher(null);
                organization.setSlogan(null);
                organization.setIntro(null);
                organization.setContact(null);
            }
        }
        return news;
    }

    /**
     * 根据组织id分页获取组织动态
     *
     * @param organizationId 组织id
     * @param pageNum        页数
     * @param length         长度
     * @param request        用于获取用户id
     * @return news
     */
    @GetMapping("/organization/{organizationId}/{pageNum}/{length}")
    public List<News> getNewsByOrganizationId(@PathVariable Integer organizationId, @PathVariable Integer pageNum,
                                              @PathVariable Integer length, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("{userId : {}, organizationId : {}, pageNum : {}, length : {}}", userId, organizationId, pageNum, length);

        Map<String, Integer> map = Map.of("userId", userId, "organizationId", organizationId,
                "offset", pageNum * length, "length", length);
        List<News> news = newsService.findPartByOrganizationId(map);
        for (News oneNews : news) {
            oneNews.getPublisher().setPassword(null);
            oneNews.getOrganization().setContact(null);
        }
        return news;
    }

    /**
     * 确定用户权限后（组织管理员或者创建者）
     * 删除某组织动态（同时删除硬盘上的多媒体资源）
     *
     * @param request 用于获取操作用户id便于查询权限
     * @param newsId  待删除的动态id
     * @return true|false
     */
    @DeleteMapping("/organization/{newsId}")
    public boolean deleteOrganizationNews(HttpServletRequest request, @PathVariable Integer newsId) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("{userId : {}, newsId : {}}", userId, newsId);

        News news = newsService.findById(newsId);
        String identity = organizationService.findIdentity(userId, news.getOrganizationId());
        log.info("{identity : {}}", identity);

        if (identity.equals("ADMIN") || identity.equals("FOUNDER")) {
            newsService.deleteById(newsId, news.getMedia());
            return true;
        }
        return false;
    }

    /**
     * 删除个人动态（需是用户本人）
     *
     * @param request 获取用户id
     * @param newsId  动态id
     * @return true|false
     */
    @DeleteMapping
    public boolean deleteById(HttpServletRequest request, Integer newsId) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("{userId : {}, newsId : {}}", userId, newsId);
        News news = newsService.findById(newsId);
        if (news.getPublisherId().equals(userId)) {
            newsService.deleteById(newsId, news.getMedia());
            return true;
        }
        return false;
    }

    /**
     * 确定用户权限后（组织管理员或者创建者）
     * 对组织动态内容进行更改
     * <p>
     * 或修改个人动态
     *
     * @param request 用于获取修改者id
     * @param news    待修改内容
     * @param files   新增的多媒体内容
     * @return {
     * "result":true|false,
     * "msg":"错误信息"
     * }
     */
    @PutMapping({"/organization", ""})
    public Map<String, Object> updateNews(HttpServletRequest request, News news, MultipartFile[] files)
            throws ClientException {
        Integer userId = (Integer) request.getAttribute("userId");
        //通过查询获取organizationId而不是从前端获取id是为了避免用户通过自己在其他组织的权限修改该动态
        news.setOrganizationId(newsService.findById(news.getNewsId()).getOrganizationId());

        String text = news.getText();
        log.info("updateNews {userId: {}, organizationId: {}, text: {}}", userId, news.getOrganizationId(), text);
        news.setPublisherId(userId);

        Map<String, Object> map = new HashMap<>();
        map.put("result", false);

        News oldNews = newsService.findById(news.getNewsId());
        if (request.getRequestURI().equals("/fu-community/news")) {
            if (!oldNews.getPublisherId().equals(userId)) {
                map.put("msg", "非本人操作");
                return map;
            }
        } else {
            String identity = organizationService.findIdentity(userId, news.getOrganizationId());
            if (identity.equals("VISITOR") || identity.equals("MEMBER")) {
                map.put("msg", "无修改权限");
                return map;
            }
        }

        news.setPublisherId(null);
        news.setOrganizationId(null);
        news.setPublishTime(null);
        news.setHasCheck(null);

        if (text != null && !text.isBlank() && MonitorUtil.textIsSpam(text)) {
            map.put("msg", "言论违规，修改失败");
            return map;
        }

        List<String> urls = new ArrayList<>();
        String today = LocalDate.now().toString();

        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                String originalFilename = file.getOriginalFilename();
                assert originalFilename != null;
                String suffix = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
                if (!suffix.matches("^(?i)JPG|PNG|JPEG|BMP|GIF|WEBP|AVI|flv|MP4|MPG|ASF|WMV|MOV|WMA|RMVB|RM|FLASH|TS$")) {
                    map.put("msg", "存在不支持的文件格式,修改失败");
                    return map;
                }
                urls.add(protocolAddress + today + '/' + UUID.randomUUID().toString() + '.' + suffix);
            }
            List<String> media = (List<String>) JSONArray.parse(news.getMedia());
            media.addAll(urls);
            news.setMedia(JSONArray.toJSONString(media));
        }

        newsService.updateById(news, oldNews.getMedia());
        if (news.getMedia() != null) {
            final List<String> finalUrls = urls;
            threadPool.submit(() -> {
                assert files != null;
                for (int i = 0; i < files.length; i++) {
                    try {
                        FileUtil.transferTo(files[i], today, finalUrls.get(i).substring(finalUrls.get(i).lastIndexOf('/') + 1));
                        if (finalUrls.get(i).substring(finalUrls.get(i).lastIndexOf('.') + 1).matches("(?i)JPG|PNG|JPEG|BMP|GIF|WEBP")) {
                            MonitorUtil.imgScan(finalUrls.get(i));
                        } else {
                            MonitorUtil.videoScan(finalUrls.get(i));
                        }
                    } catch (IOException | ClientException e) {
                        log.info(String.valueOf(e));
                    }
                }
            });
        }
        map.put("result", true);
        return map;
    }

    /**
     * 分页获取某用户的动态
     *
     * @param userId         待获取的用户
     * @param pageNum,length limit
     * @param request        查询用户
     * @return news
     */
    @GetMapping("/user/{userId}/{pageNum}/{length}")
    public List<News> getByUserId(@PathVariable Integer userId, @PathVariable Integer pageNum,
                                  @PathVariable Integer length, HttpServletRequest request) {
        Map<String, Integer> map = Map.of("myId", (Integer) request.getAttribute("userId"),
                "userId", userId, "offset", pageNum * length, "length", length);
        log.info(map.toString());
        List<News> news = newsService.findByUserId(map);
        for (News oneNews : news) {
            oneNews.getPublisher().setPassword(null);
        }
        return news;
    }

    /**
     * 获取动态详情
     *
     * @param request 获取本人id
     * @param newsId  动态id
     * @return news
     */
    @GetMapping("/{newsId}")
    public News getById(HttpServletRequest request, @PathVariable Integer newsId) {
        News news = newsService.findDetailById(Map.of("myId", (Integer) request.getAttribute("userId"), "newsId", newsId));
        Organization organization = news.getOrganization();
        if (organization == null) {
            news.getPublisher().setPassword(null);
        } else {
            news.setPublisher(null);
            organization.setSlogan(null);
            organization.setIntro(null);
            organization.setContact(null);
        }
        return news;
    }
}
