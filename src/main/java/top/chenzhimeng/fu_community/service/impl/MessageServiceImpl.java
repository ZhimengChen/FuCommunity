package top.chenzhimeng.fu_community.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chenzhimeng.fu_community.mapper.MessageMapper;
import top.chenzhimeng.fu_community.model.Message;
import top.chenzhimeng.fu_community.service.IMessageService;

import java.util.List;
import java.util.Map;

@Service("messageService")
public class MessageServiceImpl implements IMessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public List<Message> findByUserId(Map<String, Integer> map) {
        return messageMapper.selectByUserId(map);
    }

    @Override
    public int updateById(Message message) {
        return messageMapper.updateByPrimaryKeySelective(message);
    }
}
