package top.chenzhimeng.fu_community.mapper;

import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import top.chenzhimeng.fu_community.model.Admin;

@Repository
public interface AdminMapper {

    String baseColumnList = " admin_id, admin_name, salt, password ";

    int deleteByPrimaryKey(Integer adminId);

    int insert(Admin record);

    int insertSelective(Admin record);

    Admin selectByPrimaryKey(Integer adminId);

    int updateByPrimaryKeySelective(Admin record);

    int updateByPrimaryKey(Admin record);

    /**
     * 管理员用
     * 根据用户名查询管理员信息
     *
     * @param adminName 管理员用户名
     * @return admin
     */
    @Select("SELECT " + baseColumnList + " FROM t_admin WHERE admin_name=#{adminName}")
    @ResultMap("BaseResultMap")
    Admin selectByAdminName(String adminName);
}