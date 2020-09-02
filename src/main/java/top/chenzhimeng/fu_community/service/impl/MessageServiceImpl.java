package top.chenzhimeng.fu_community.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chenzhimeng.fu_community.mapper.MessageMapper;
import top.chenzhimeng.fu_community.model.Message;
import top.chenzhimeng.fu_community.service.IMessageService;

import java.sql.ResultSet;
import java.util.HashMap;
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

    @Override
    public Map<Integer, Integer> findUnreadCountsByUserId(Integer userId) {
        List<MessageMapper.TypeCountMap> typeCountMaps = messageMapper.selectUnreadCountsByUserId(userId);
        Map<Integer, Integer> map = new HashMap<>();

        for (MessageMapper.TypeCountMap typeCountMap : typeCountMaps) {
            map.put(typeCountMap.getType(), typeCountMap.getCount());
        }
        return map;
    }
}
