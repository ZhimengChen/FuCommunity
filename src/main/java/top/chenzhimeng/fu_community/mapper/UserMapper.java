package top.chenzhimeng.fu_community.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Repository;
import top.chenzhimeng.fu_community.model.User;

import java.util.List;
import java.util.Map;

@Repository
public interface UserMapper {

    String baseColumnList = " user_id, phone_no, secret_key, password, student_no, student_name, " +
            " student_card, has_check, auditor_id, audit_time, avatar ";

    int deleteByPrimaryKey(Integer userId);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer userId);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT " + baseColumnList + " FROM t_user WHERE phone_no=#{phoneNo}")
    @ResultMap("BaseResultMap")
    User selectByPhoneNo(String phoneNo);

    /**
     * 查找所有已处理的实名用户申请
     *
     * @return users{auditor(User)}
     */
    @Select("SELECT " + baseColumnList + " FROM t_user WHERE has_check IS NOT NULL ORDER BY audit_time DESC")
    @Results(id = "userAuditor", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "phoneNo", column = "phone_no"),
            @Result(property = "studentNo", column = "student_no"),
            @Result(property = "studentName", column = "student_name"),
            @Result(property = "studentCard", column = "student_card"),
            @Result(property = "hasCheck", column = "has_check"),
            @Result(property = "auditor", column = "auditor_id", one = @One(
                    select = "top.chenzhimeng.fu_community.mapper.AdminMapper.selectByPrimaryKey", fetchType = FetchType.LAZY
            )),
            @Result(property = "auditTime", column = "audit_time")
    })
    List<User> selectHistory();

    /**
     * 查询某用户，并获取查询者是否关注该用户
     *
     * @param map {userId, publisherId}
     * @return user{hasSubscribed(Boolean)}
     */
    @Select("SELECT #{userId} as fans_id, " + baseColumnList + " FROM t_user WHERE user_id=#{publisherId}")
    @Results(id = "userFans", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "studentName", column = "student_name"),
            @Result(property = "hasSubscribed", column = "{userId=user_id, fansId=fans_id}", one = @One(
                    select = "top.chenzhimeng.fu_community.mapper.FansMapper.selectCountByIds", fetchType = FetchType.LAZY
            ))
    })
    User selectByIds(Map<String, Integer> map);

    /**
     * 获取某用户信息
     * 及其所有组织
     * 及其最新发布的10条动态
     *
     * @param myId   查询者id
     * @param userId 被查询者id
     * @return user
     */
    @Select("SELECT #{1} as my_id, 0 as offset, 10 as length, " + baseColumnList + " FROM t_user WHERE user_id=#{2}")
    @Results(id = "userOrganizationsNews", value = {
            @Result(property = "userId", column = "user_id"),
            @Result(property = "studentName", column = "student_name"),
            @Result(property = "organizations", column = "user_id", many = @Many(
                    select = "top.chenzhimeng.fu_community.mapper.OrganizationMapper.selectByUserId", fetchType = FetchType.LAZY
            )),
            @Result(property = "newsList", column = "{myId=my_id,userId=user_id,offset=offset,length=length}", many = @Many(
                    select = "top.chenzhimeng.fu_community.mapper.NewsMapper.selectPartByUserId", fetchType = FetchType.LAZY
            )),
            @Result(property = "hasSubscribed", column = "{userId=user_id, fansId=my_id}", one = @One(
                    select = "top.chenzhimeng.fu_community.mapper.FansMapper.selectCountByIds", fetchType = FetchType.LAZY
            )),
            @Result(property = "hasCheck", column = "has_check")
    })
    User selectUserOrganizationsNewsByIds(@Param("1") Integer myId, @Param("2") Integer userId);

    @Select("SELECT user_id,student_name,avatar FROM t_user " +
            "WHERE user_id " +
            "IN (SELECT user_id FROM (SELECT user_id FROM t_user_organization_fans WHERE fans_id=#{userId} ORDER BY id DESC LIMIT #{offset},#{length}) as a)")
    @ResultMap("BaseResultMap")
    List<User> selectSubscribedById(Map<String, Integer> map);

    @Update("UPDATE t_user SET has_check=#{hasCheck} WHERE user_id=#{userId}")
    int updateAuditStateById(Map<String, Object> map);

    @Select("SELECT user_id,avatar,student_name FROM t_user " +
            "WHERE user_id IN " +
            "(SELECT member_id FROM t_organization_member WHERE organization_id=#{organizationId} AND has_check=0)")
    @ResultMap("BaseResultMap")
    List<User> selectApplicationsByOrganizationId(Integer organizationId);

    @Select("SELECT " + baseColumnList + " FROM t_user WHERE student_name IS NOT NULL ORDER BY FIELD(has_check,null,0,1),user_id LIMIT #{offset},#{length}")
    @ResultMap("userAuditor")
    List<User> selectPart(Map<String, Integer> map);

    @Select("SELECT COUNT(user_id) FROM t_user")
    int selectTotalCount();

    @Select("SELECT " + baseColumnList + " FROM t_user WHERE user_id=#{userId}")
    @ResultMap("userAuditor")
    User selectUserAuditorById(Integer userId);

    @Select("SELECT " + baseColumnList + " FROM t_user WHERE user_id IN (SELECT fans_id FROM t_user_organization_fans WHERE organization_id#{organizationId})")
    @ResultMap("BaseResultMap")
    List<User> selectFansByOrganizationId(Integer organizationId);

    @Select("SELECT " + baseColumnList + " FROM t_user WHERE user_id IN (SELECT fans_id FROM t_user_organization_fans WHERE user_id#{userId})")
    @ResultMap("BaseResultMap")
    List<User> selectFansById(Integer userId);

    @Select("SELECT " + baseColumnList + " FROM t_user " +
            "WHERE user_id IN (SELECT admin_id FROM t_organization_admin WHERE organization_id=#{organizationId}) " +
            "OR user_id=(SELECT founder_id FROM t_organization WHERE organization_id=#{organizationId})")
    List<User> selectAdminsAndFounderByOrganizationId(Integer organizationId);

    @Select("SELECT user_id,avatar,student_name FROM t_user " +
            "WHERE user_id IN (SELECT admin_id FROM t_organization_admin WHERE organization_id=#{organizationId})")
    @ResultMap("BaseResultMap")
    List<User> selectAdminsByOrganizationId(Integer organizationId);

    @Select("SELECT user_id,avatar,student_name FROM t_user " +
            "WHERE user_id IN (SELECT member_id FROM t_organization_member WHERE organization_id=#{organizationId} AND has_check=1)")
    @ResultMap("BaseResultMap")
    List<User> selectMembersByOrganizationId(Integer organizationId);
}