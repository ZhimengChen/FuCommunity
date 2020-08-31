package top.chenzhimeng.fu_community.mapper;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import top.chenzhimeng.fu_community.model.Message;

import java.util.List;
import java.util.Map;

@Repository
public interface MessageMapper {

    String baseColumnList = " message_id, receiver_id, type, content, time, has_read ";

    int deleteByPrimaryKey(Integer messageId);

    int insert(Message record);

    int insertSelective(Message record);

    Message selectByPrimaryKey(Integer messageId);

    int updateByPrimaryKeySelective(Message record);

    int updateByPrimaryKey(Message record);

    @Select("SELECT " + baseColumnList + " FROM t_message WHERE receiver_id=#{receiverId} ORDER BY time DESC limit #{offset},#{length}")
    @ResultMap("BaseResultMap")
    List<Message> selectByUserId(Map<String, Integer> map);
}