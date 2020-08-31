package top.chenzhimeng.fu_community.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chenzhimeng.fu_community.mapper.AdminMapper;
import top.chenzhimeng.fu_community.model.Admin;
import top.chenzhimeng.fu_community.service.IAdminService;

@Service("adminService")
public class AdminServiceImpl implements IAdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin findByAdminName(String adminName) {
        return adminMapper.selectByAdminName(adminName);
    }

    @Override
    public Admin findById(Integer adminId) {
        return adminMapper.selectByPrimaryKey(adminId);
    }
}
