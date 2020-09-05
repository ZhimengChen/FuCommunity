package top.chenzhimeng.fu_community.service;

import top.chenzhimeng.fu_community.model.Message;

import java.util.List;
import java.util.Map;

public interface IMessageService {
    List<Message> findByTypeAndUserId(Map<String, Integer> map);

    int updateById(Message message);

    Map<Integer, Integer> findUnreadCountsByUserId(Integer userId);
}
