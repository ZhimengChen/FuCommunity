package top.chenzhimeng.fu_community.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.chenzhimeng.fu_community.model.Message;
import top.chenzhimeng.fu_community.service.IMessageService;
import top.chenzhimeng.fu_community.service.INewsService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/message")
@Slf4j
public class MessageController {

    @Autowired
    private IMessageService messageService;

    @GetMapping()
    public List<Message> getAll(HttpServletRequest request, Integer pageNum, Integer length) {
        Map<String, Integer> map = Map.of("receiverId", (Integer) request.getAttribute("userId"), "offset", pageNum * length, "length", length);
        log.info("getMessages " + map.toString());
        return messageService.findByUserId(map);
    }

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
}