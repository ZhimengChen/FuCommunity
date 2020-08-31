package top.chenzhimeng.fu_community.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Repository;
import top.chenzhimeng.fu_community.model.News;

import java.util.List;
import java.util.Map;

@Repository
public interface NewsMapper {

    String baseColumnList = " news_id, publisher_id, organization_id, text, media, publish_time, has_check ";

    int deleteByPrimaryKey(Integer newsId);

    int insert(News record);

    int insertSelective(News record);

    News selectByPrimaryKey(Integer newsId);

    int updateByPrimaryKeySelective(News record);

    int updateByPrimaryKey(News record);

    /**
     * 分页查询所有动态 并按发布时间倒序排序
     *
     * @param userId 该用户id
     * @param offset 偏移量
     * @param length 数据长度
     * @return news{publisher(User), organization, likesNum(Integer), hasLiked(Boolean), commentsNum(Integer)}
     */
    @Select("SELECT #{1} as user_id, " + baseColumnList + " FROM t_news ORDER BY publish_time DESC LIMIT #{2}, #{3}")
    @Results(id = "showedNews", value = {
            @Result(property = "newsId", column = "news_id"),
            @Result(property = "publisher", column = "{publisherId=publisher_id, userId=user_id}", one = @One(
                    select = "top.chenzhimeng.fu_community.mapper.UserMapper.selectByIds", fetchType = FetchType.LAZY
            )),
            @Result(property = "organization", column = "{organizationId=organization_id, userId=user_id}", one = @One(
                    select = "top.chenzhimeng.fu_community.mapper.OrganizationMapper.selectByIds", fetchType = FetchType.LAZY
            )),
            @Result(property = "publishTime", column = "publish_time"),
            @Result(property = "likesNum", column = "news_id", one = @One(
                    select = "top.chenzhimeng.fu_community.mapper.LikeMapper.selectLikesNum", fetchType = FetchType.LAZY
            )),
            @Result(property = "hasLiked", column = "{newsId=news_id, userId=user_id}", one = @One(
                    select = "top.chenzhimeng.fu_community.mapper.LikeMapper.selectHasLiked", fetchType = FetchType.LAZY
            )),
            @Result(property = "commentsNum", column = "news_id", one = @One(
                    select = "top.chenzhimeng.fu_community.mapper.CommentMapper.selectCountByNewsId", fetchType = FetchType.LAZY
            ))
    })
    List<News> selectPart(@Param("1") Integer userId, @Param("2") Integer offset, @Param("3") Integer length);

    /**
     * 分页查询关注者发布的个人动态或关注组织发布的组织动态
     * 并按时间倒序排序
     *
     * @param userId 该用户id
     * @param offset 偏移量
     * @param length 数据长度
     * @return news{publisher(User), organization, likesNum(Integer), hasLiked(Boolean), commentsNum(Integer)}
     */
    @Select("SELECT #{1} as user_id, " + baseColumnList + " FROM t_news " +
            "WHERE " +
            "(publisher_id IN (SELECT user_id FROM t_user_organization_fans WHERE fans_id=#{1} AND organization_id IS NULL) AND organization_id IS NULL) " +
            "OR " +
            "organization_id IN (SELECT organization_id FROM t_user_organization_fans WHERE fans_id=#{1} AND user_id IS NULL) " +
            "ORDER BY publish_time DESC " +
            "LIMIT #{2}, #{3}")
    @ResultMap("showedNews")
    List<News> selectSubscribedPart(@Param("1") Integer userId, @Param("2") Integer offset, @Param("3") Integer length);

    /**
     * 分页查询组织动态
     *
     * @param map {userId, organizationId, offset, length}
     * @return news{publisher(User), organization, likesNum(Integer), hasLiked(Boolean), commentsNum(Integer)}
     */
    @Select("SELECT #{userId} as user_id, " + baseColumnList + " FROM t_news " +
            "WHERE organization_id=#{organizationId} " +
            "ORDER BY publish_time DESC " +
            "LIMIT #{offset}, #{length}")
    @ResultMap("showedNews")
    List<News> selectPartByOrganizationId(Map<String, Integer> map);

    /**
     * 分页获取该用户发布的动态
     *
     * @param map {"myId" : 查询者的id, "userId" : 被查询的用户id, "offset" : 偏移量, "length" : 动态长度}
     * @return newsList
     */
    @Select("SELECT #{myId} as user_id, " + baseColumnList + " FROM t_news " +
            "WHERE publisher_id=#{userId} AND organization_id IS NULL " +
            "ORDER BY publish_time DESC LIMIT #{offset}, #{length}")
    @ResultMap("showedNews")
    List<News> selectPartByUserId(Map<String, Integer> map);

    @Update("UPDATE t_news SET has_check=#{hasCheck} WHERE news_id=#{newsId}")
    int updateAuditState(Map<String, Integer> newsId);

    @Select("SELECT #{myId} as user_id, " + baseColumnList + " FROM t_news " +
            "WHERE news_id=#{newsId}")
    @ResultMap("showedNews")
    News selectById(Map<String, Integer> map);
}