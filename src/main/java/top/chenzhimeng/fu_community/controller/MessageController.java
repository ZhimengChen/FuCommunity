package top.chenzhimeng.fu_community.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chenzhimeng.fu_community.model.Message;
import top.chenzhimeng.fu_community.service.IMessageService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message")
@Slf4j
public class MessageController {

    @Autowired
    private IMessageService messageService;

    /**
     * 分页获取消息
     *
     * @param request        用于获取用户id
     * @param pageNum,length limit
     * @return messages
     */
    @GetMapping()
    public List<Message> getAll(HttpServletRequest request, Integer pageNum, Integer length) {
        Map<String, Integer> map = Map.of("receiverId", (Integer) request.getAttribute("userId"), "offset", pageNum * length, "length", length);
        log.info("getMessages " + map.toString());
        return messageService.findByUserId(map);
    }

    /**
     * '读'消息
     *
     * @param request   用户id，用于验证是否为本人
     * @param messageId 消息id
     */
    @PutMapping(value = "/read")
    public void read(HttpServletRequest request, Integer messageId) {
        Integer receiverId = (Integer) request.getAttribute("userId");
        log.info("{messageId : {}, receiverId : {}}", messageId, receiverId);
        Message message = new Message();
        message.setReceiverId(receiverId);
        message.setMessageId(messageId);
        message.setHasRead(true);
        messageService.updateById(message);
    }

    /**
     * 获取各种消息类型的未读数量
     *
     * @param request 用于获取用户id
     * @return {
     * type : count
     * }
     */
    @GetMapping("/unread/count")
    public Map<Integer, Integer> getCounts(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("{userId : {}}", userId);
        return messageService.findUnreadCountsByUserId(userId);
    }
}