package top.chenzhimeng.fu_community.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface FansMapper {

    /**
     * 添加一条关注记录
     *
     * @param subscribedUserId         被关注的用户id
     * @param subscribedOrganizationId 被关注的组织id
     * @param fansId                   关注者id
     * @return 0|1
     */
    @Insert("INSERT INTO t_user_organization_fans(user_id,organization_id,fans_id) VALUES (#{1},#{2},#{3})")
    int insert(@Param("1") Integer subscribedUserId, @Param("2") Integer subscribedOrganizationId,
               @Param("3") Integer fansId);

    /**
     * 删除一条关注记录
     *
     * @param subscribedUserId         被关注的用户id
     * @param subscribedOrganizationId 被关注的组织id
     * @param fansId                   关注者id
     * @return 0|1
     */
    @DeleteProvider(value = SqlProvider.class, method = "delete")
    int delete(@Param("1") Integer subscribedUserId, @Param("2") Integer subscribedOrganizationId,
               @Param("3") Integer fansId);

    /**
     * 查询某用户是否关注某组织或某用户
     *
     * @param map {userId, organizationId, fansId}
     * @return true|false
     */
    @SelectProvider(value = SqlProvider.class, method = "selectCountByIds")
    boolean selectCountByIds(Map<String, Integer> map);

    class SqlProvider {
        public String delete(@Param("1") Integer subscribedUserId) {
            return new SQL() {{
                DELETE_FROM("t_user_organization_fans");
                if (subscribedUserId != null) {
                    WHERE("user_id=#{1}");
                } else {
                    WHERE("organization_id=#{2}");
                }
                WHERE("fans_id=#{3}");
            }}.toString();
        }

        public String selectCountByIds(Map<String, Integer> map) {
            return new SQL() {{
                SELECT("COUNT(1)>0");
                FROM("t_user_organization_fans");
                if (map.get("userId") != null) {
                    WHERE("user_id=#{userId}");
                } else {
                    WHERE("organization_id=#{organizationId}");
                }
                WHERE("fans_id=#{fansId}");
            }}.toString();
        }
    }
}