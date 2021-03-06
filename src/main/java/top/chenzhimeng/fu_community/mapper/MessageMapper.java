package top.chenzhimeng.fu_community.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import top.chenzhimeng.fu_community.model.Message;

import java.sql.ResultSet;
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

    @Select("SELECT " + baseColumnList + " FROM t_message WHERE receiver_id=#{receiverId} AND type=#{type} " +
            "ORDER BY time DESC limit #{offset},#{length}")
    @ResultMap("BaseResultMap")
    List<Message> selectByTypeAndUserId(Map<String, Integer> map);

    @Select("SELECT type,COUNT(message_id) AS count FROM t_message WHERE receiver_id=#{userId} AND has_read=0 GROUP BY type")
    List<TypeCountMap> selectUnreadCountsByUserId(Integer userId);

    @Delete("DELETE FROM t_message WHERE content=#{content}")
    int deleteByContent(String content);

    class TypeCountMap {
        private Integer type;
        private Integer count;

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }
}