package top.chenzhimeng.fu_community.service;

import top.chenzhimeng.fu_community.model.Admin;

public interface IAdminService {
    Admin findByAdminName(String adminName);

    Admin findById(Integer adminId);
}
