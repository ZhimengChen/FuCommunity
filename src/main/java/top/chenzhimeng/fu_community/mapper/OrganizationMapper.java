package top.chenzhimeng.fu_community.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.DeleteMapping;
import top.chenzhimeng.fu_community.model.News;
import top.chenzhimeng.fu_community.model.Organization;
import top.chenzhimeng.fu_community.model.User;

import java.util.List;
import java.util.Map;

@Repository
public interface OrganizationMapper {

    String baseColumnList = " organization_id, organization_name, logo, slogan, intro, founder_id, contact, auditor_id, has_check, audit_time";

    int deleteByPrimaryKey(Integer organizationId);

    int insert(Organization record);

    int insertSelective(Organization record);

    Organization selectByPrimaryKey(Integer organizationId);

    /**
     * 获取该用户创建的组织
     *
     * @param founderId 用户id
     * @return organizations
     */
    @Select("SELECT " + baseColumnList + " FROM t_organization WHERE has_check=1 AND founder_id=#{founderId}")
    @ResultMap("BaseResultMap")
    List<Organization> selectByFounderId(Integer founderId);

    /**
     * 获取该用户管理的组织
     *
     * @param adminId 用户id
     * @return organizations
     */
    @Select("SELECT " + baseColumnList + " FROM t_organization " +
            "WHERE " +
            "organization_id " +
            "IN (SELECT organization_id FROM t_organization_admin WHERE admin_id=#{adminId})")
    @ResultMap("BaseResultMap")
    List<Organization> selectByAdminId(Integer adminId);

    /**
     * 获取该用户加入的组织
     *
     * @param memberId 用户id
     * @return organizations
     */
    @Select("SELECT " + baseColumnList + " FROM t_organization " +
            "WHERE " +
            "organization_id " +
            "IN (SELECT organization_id FROM t_organization_member WHERE member_id=#{memberId} AND has_check=1)")
    @ResultMap("BaseResultMap")
    List<Organization> selectByMemberId(Integer memberId);

    /**
     * 获取该用户的所有组织
     *
     * @param userId 用户id
     * @return organizations
     */
    @Select("SELECT " + baseColumnList + " FROM t_organization " +
            "WHERE has_check=1 " +
            "AND (founder_id=#{userId} " +
            "OR organization_id IN (SELECT organization_id FROM t_organization_admin WHERE admin_id=#{userId}) " +
            "OR organization_id IN (SELECT organization_id FROM t_organization_member WHERE member_id=#{userId}) " +
            ")")
    @ResultMap("BaseResultMap")
    List<Organization> selectByUserId(Integer userId);

    /**
     * 获取热门前三组织
     *
     * @param excludeIds 排除掉的组织ids（一般为用户已加入的组织ids）
     * @return organizations
     */
    @Select("<script>" +
            "SELECT " + baseColumnList + " FROM t_organization " +
            "WHERE has_check=1 " +
            "<if test='list!=null'>" +
            "AND organization_id NOT IN " +
            "<foreach item='item' index='index' collection='list' open='(' separator=',' close=')'>" +
            "#{item}" +
            "</foreach>" +
            "</if> " +
            "ORDER BY getOrganizationScore(organization_id) DESC " +
            "LIMIT 3" +
            "</script>")
    @ResultMap("BaseResultMap")
    List<Organization> selectHot(List<Integer> excludeIds);

    /**
     * 模糊名查询（组织名或组织简介）组织 并排序（名称排序>简介排序，关键词位置在前>后）
     *
     * @param fuzzyName 关键词
     * @return organizations
     */
    @Select("SELECT " + baseColumnList + " FROM t_organization " +
            "WHERE has_check=1 AND (INSTR(organization_name,#{fuzzyName})>0 OR INSTR(intro,#{fuzzyName})>0) " +
            "ORDER BY FIELD(INSTR(organization_name,#{fuzzyName}),0), INSTR(organization_name,#{fuzzyName}), INSTR(intro,#{fuzzyName})"
    )
    @ResultMap("BaseResultMap")
    List<Organization> selectByFuzzyName(String fuzzyName);

    int updateByPrimaryKeySelective(Organization record);

    int updateByPrimaryKey(Organization record);

    /**
     * 发布组织动态时检查该用户是否是组织创建者
     *
     * @param map {organizationId, publisherId}
     * @return 0|1
     */
    @Select("SELECT COUNT(organization_id) FROM t_organization WHERE organization_id=#{organizationId} AND founder_id=#{userId}")
    int isFounder(Map<String, Integer> map);

    /**
     * 发布组织动态时检查该用户是否是组织管理员
     *
     * @param map {organizationId, publisherId}
     * @return 0|1
     */
    @Select("SELECT COUNT(1) FROM t_organization_admin " +
            "WHERE organization_id=#{organizationId} AND admin_id=#{userId}")
    int isAdmin(Map<String, Integer> map);

    /**
     * 查询某用户是否为某组织成员
     *
     * @param map {userId, organizationId}
     * @return 0|1
     */
    @Select("SELECT COUNT(1) FROM t_organization_member " +
            "WHERE organization_id=#{organizationId} AND member_id=#{userId} AND has_check=1")
    int isMember(Map<String, Integer> map);

    /**
     * 管理员用
     * 获取所有组织 并排序（未审核>审核失败>审核通过，先创建>后创建）
     *
     * @return organizations{founder(User), auditor(User)}
     */
    @Select("SELECT " + baseColumnList + " FROM t_organization ORDER BY FIELD(has_check,null,0,1),organization_id")
    @Results(id = "organizationFounderAuditor", value = {
            @Result(column = "organization_id", property = "organizationId"),
            @Result(column = "organization_name", property = "organizationName"),
            @Result(column = "founder_id", property = "founder", one = @One(
                    select = "top.chenzhimeng.fu_community.mapper.UserMapper.selectByPrimaryKey", fetchType = FetchType.LAZY
            )),
            @Result(column = "auditor_id", property = "auditor", one = @One(
                    select = "top.chenzhimeng.fu_community.mapper.AdminMapper.selectByPrimaryKey", fetchType = FetchType.LAZY
            )),
            @Result(column = "has_check", property = "hasCheck"),
            @Result(column = "audit_time", property = "auditTime")
    })
    List<Organization> selectAll();

    /**
     * 管理员用
     * 获取所有已审核（包括通过与不通过）的组织并排序（审核时间倒序）
     *
     * @return organizations{founder(User), auditor(User)}
     */
    @Select("SELECT " + baseColumnList + " FROM t_organization WHERE has_check IS NOT NULL ORDER BY audit_time DESC")
    @ResultMap("organizationFounderAuditor")
    List<Organization> selectHistory();

    /**
     * 查询某组织 并获取查询这是否关注该组织
     *
     * @param map {userId, organizationId}
     * @return organization{hasSubscribed(Boolean)}
     */
    @Select("SELECT #{userId} as fans_id, " + baseColumnList + " FROM t_organization WHERE organization_id=#{organizationId}")
    @Results(id = "organizationFans", value = {
            @Result(property = "organizationId", column = "organization_id"),
            @Result(property = "organizationName", column = "organization_name"),
            @Result(property = "hasSubscribed", column = "{organizationId=organization_id, fansId=fans_id}", one = @One(
                    select = "top.chenzhimeng.fu_community.mapper.FansMapper.selectCountByIds", fetchType = FetchType.LAZY
            ))
    })
    Organization selectByIds(Map<String, Integer> map);

    @Insert("INSERT INTO t_organization_member(member_id,organization_id) VALUES(#{1},#{2})")
    int insertMember(@Param("1") Integer memberId, @Param("2") Integer organizationId);

    @Delete("DELETE FROM t_organization_member WHERE organization_id=#{1} AND member_id=#{2}")
    int deleteMember(@Param("1") Integer organizationId, @Param("2") Integer memberId);

    @Delete("DELETE FROM t_organization_admin WHERE organization_id=#{1} AND admin_id=#{2}")
    int deleteAdmin(@Param("1") Integer organizationId, @Param("2") Integer adminId);

    @Select("SELECT organization_id,organization_name,logo FROM t_organization " +
            "WHERE organization_id " +
            "IN (SELECT organization_id FROM (SELECT organization_id FROM t_user_organization_fans WHERE fans_id=#{userId} ORDER BY id DESC LIMIT #{offset},#{length}) as a)")
    @ResultMap("BaseResultMap")
    List<Organization> selectSubscribedByUserId(Map<String, Integer> map);

    @Delete("DELETE FROM t_organization_member " +
            "WHERE member_id=#{memberId} AND organization_id=#{organizationId} AND has_check=0")
    int deleteMemberApplication(Map<String, Integer> map);

    @Update("UPDATE t_organization_member SET has_check=1 WHERE organization_id=#{organizationId} AND member_id=#{memberId}")
    int updateMember(Map<String, Integer> map);

    @Insert("INSERT INTO t_organization_admin(organization_id,admin_id) VALUES(#{organizationId},#{adminId})")
    int insertAdmin(Map<String, Integer> map);
}