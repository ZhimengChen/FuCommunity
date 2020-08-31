package top.chenzhimeng.fu_community.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Repository;
import top.chenzhimeng.fu_community.model.Comment;

import java.util.List;
import java.util.Map;

@Repository
public interface CommentMapper {

    String baseColumnList = " comment_id, user_id, news_id, reply_id, text, pic, comment_time ";

    int deleteByPrimaryKey(Integer commentId);

    int insert(Comment record);

    int insertSelective(Comment record);

    Comment selectByPrimaryKey(Integer commentId);

    /**
     * 查询某条动态的总评论数（包括评论的评论）
     *
     * @param newsId 待查询的动态id
     * @return 评论数
     */
    @Select("SELECT COUNT(comment_id) FROM t_comment WHERE news_id=#{newsId}")
    int selectCountByNewsId(Integer newsId);

    int updateByPrimaryKeySelective(Comment record);

    int updateByPrimaryKey(Comment record);

    /**
     * 分页查询某条动态的评论或某条评论的评论，并按指定规则排序（按时间或按点赞数）
     *
     * @param map {userId, replyId, newsId, orderBy("time"|"likesNum"), offset, length}
     * @return comments{user, hasLiked(Boolean)}
     */
    @SelectProvider(value = SqlProvider.class, method = "selectPart")
    @Results(id = "showedComment", value = {
            @Result(property = "commentId", column = "comment_id"),
            @Result(property = "user", column = "user_id", one = @One(
                    select = "top.chenzhimeng.fu_community.mapper.UserMapper.selectByPrimaryKey", fetchType = FetchType.LAZY
            )),
            @Result(property = "newsId", column = "news_id"),
            @Result(property = "replyId", column = "reply_id"),
            @Result(property = "commentTime", column = "comment_time"),
            @Result(property = "hasLiked", column = "{userId=my_id, commentId=comment_id}", one = @One(
                    select = "top.chenzhimeng.fu_community.mapper.LikeMapper.selectHasLiked", fetchType = FetchType.LAZY
            )),
            @Result(property = "likesNum", column = "likes_num"),
            @Result(property = "commentsNum", column = "comments_num")
    })
    List<Comment> selectPart(Map<String, Object> map);

    @Delete("DELETE FROM t_comment WHERE user_id=#{userId} AND comment_id=#{commentId}")
    int deleteByIdAndUserId(Map<String, Integer> map);

    class SqlProvider {
        public String selectPart(Map<String, Object> map) {
            return new SQL() {{
                SELECT(baseColumnList);
                SELECT("(SELECT COUNT(1) FROM t_like WHERE comment_id=t.comment_id) as likes_num");
                SELECT("#{userId} as my_id");

                //若查询的是动态的评论，则需要获取某条评论的评论数
                if (map.get("replyId") == null) {
                    SELECT("(SELECT COUNT(comment_id) FROM t_comment WHERE reply_id=t.comment_id) as comments_num");
                }

                FROM("t_comment t");

                WHERE("news_id=#{newsId}");
                if (map.get("replyId") == null) {
                    WHERE("reply_id IS NULL");
                } else {
                    WHERE("reply_id=#{replyId}");
                }

                String orderBy = (String) map.get("orderBy");
                if (orderBy != null) {
                    if (orderBy.equals("likesNum")) {
                        ORDER_BY("likes_num DESC");
                    } else if (orderBy.equals("time")) {
                        ORDER_BY("comment_time DESC");
                    }
                }
                LIMIT("#{offset},#{length}");
            }}.toString();
        }
    }
}