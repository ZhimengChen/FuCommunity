package top.chenzhimeng.fu_community.mapper;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface LikeMapper {
    /**
     * 插入一条点赞记录
     *
     * @param newsId    被点赞的动态id
     * @param commentId 被点赞的评论id
     * @param userId    点赞的用户id
     * @return 0|1
     */
    @Insert("INSERT INTO t_like(news_id,comment_id,user_id) VALUES(#{1},#{2},#{3})")
    int insert(@Param("1") Integer newsId, @Param("2") Integer commentId, @Param("3") Integer userId);

    /**
     * 删除一条点赞记录
     *
     * @param newsId    被点赞的动态id
     * @param commentId 被点赞的评论id
     * @param userId    点赞用户id
     * @return 0|1
     */
    @DeleteProvider(value = SqlProvider.class, method = "delete")
    int delete(@Param("1") Integer newsId, @Param("2") Integer commentId, @Param("3") Integer userId);

    /**
     * 获取某条动态或某条评论的点赞数
     *
     * @param newsId    被赞动态
     * @param commentId 被赞评论
     * @return 点赞数
     */
    @SelectProvider(value = SqlProvider.class, method = "selectLikesNum")
    int selectLikesNum(@Param("1") Integer newsId, @Param("2") Integer commentId);

    /**
     * 获取某用户是否点赞了某动态或某评论
     *
     * @param map {newsId, commentId, userId}
     * @return true|false
     */
    @SelectProvider(value = SqlProvider.class, method = "selectHasLiked")
    boolean selectHasLiked(Map<String, Object> map);

    class SqlProvider {
        public String delete(@Param("1") Integer newsId) {
            return new SQL() {{
                DELETE_FROM("t_like");
                if (newsId != null) {
                    WHERE("news_id=#{1}");
                } else {
                    WHERE("comment_id=#{2}");
                }
                WHERE("user_id=#{3}");
            }}.toString();
        }

        public String selectLikesNum(@Param("1") Integer newsId) {
            return new SQL() {{
                SELECT("COUNT(1)");
                FROM("t_like");
                if (newsId != null) {
                    WHERE("news_id=#{1}");
                } else {
                    WHERE("comment_id=#{2}");
                }
            }}.toString();
        }

        public String selectHasLiked(Map<String, Object> map) {
            return new SQL() {{
                SELECT("COUNT(1)>0");
                FROM("t_like");
                if (map.get("newsId") != null) {
                    WHERE("news_id=#{newsId}");
                } else {
                    WHERE("comment_id=#{commentId}");
                }
                WHERE("user_id=#{userId}");
            }}.toString();
        }
    }
}
