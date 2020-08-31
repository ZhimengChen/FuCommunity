package top.chenzhimeng.fu_community.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import top.chenzhimeng.fu_community.model.Admin;
import top.chenzhimeng.fu_community.model.Message;
import top.chenzhimeng.fu_community.model.Organization;
import top.chenzhimeng.fu_community.model.User;
import top.chenzhimeng.fu_community.service.IAdminService;
import top.chenzhimeng.fu_community.service.IMessageService;
import top.chenzhimeng.fu_community.service.IOrganizationService;
import top.chenzhimeng.fu_community.service.IUserService;
import top.chenzhimeng.fu_community.util.EncryptUtil;
import top.chenzhimeng.fu_community.util.TokenUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {

    @Autowired
    private IAdminService adminService;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;
    @Autowired
    private IOrganizationService organizationService;
    @Autowired
    private IUserService userService;

    /**
     * 管理员登录
     *
     * @param admin {"adminName","password":}
     * @return {
     * "result": true|false,
     * "msg":"错误信息",
     * "accessToken":"token"
     * }
     */
    @PostMapping()
    public Map<String, Object> login(Admin admin) throws InvalidKeyException, NoSuchAlgorithmException {
        Map<String, Object> map = new HashMap<>();
        map.put("result", false);

        Admin foundAdmin = adminService.findByAdminName(admin.getAdminName());
        if (foundAdmin == null) {
            map.put("msg", "用户不存在");
            return map;
        }

        if (EncryptUtil.encrypt(foundAdmin.getSalt(), admin.getPassword()).equals(foundAdmin.getPassword())) {
            map.put("result", true);
            ValueOperations<String, Serializable> opsForValue = redisTemplate.opsForValue();
            Long tokenVersion = opsForValue.increment("tokenVersion_" + foundAdmin.getAdminName());
            map.put("accessToken", TokenUtil.signToken(foundAdmin.getAdminId(), foundAdmin.getAdminName(), tokenVersion));
            return map;
        }

        map.put("msg", "密码错误");
        return map;
    }

    /**
     * 用于检查登录状态
     */
    @GetMapping("/check")
    public String check(HttpServletRequest request) {
        return adminService.findById((Integer) request.getAttribute("adminId")).getAdminName();
    }

    /**
     * 用于获取所有组织
     *
     * @return 返回所有组织
     * 排序：未审核>未通过>已通过
     * 先申请>后申请
     */
    @GetMapping("/organization")
    public List<Organization> getOrganizations() {
        return organizationService.findAll();
    }

    /**
     * 获取单个组织
     *
     * @param organizationId 组织id
     */
    @GetMapping("/organization/{organizationId}")
    public Organization getOrganizationById(@PathVariable Integer organizationId) {
        return organizationService.findById(organizationId);
    }

    /**
     * 审核组织
     *
     * @param request        获取管理员id
     * @param organizationId 组织id
     * @param hasCheck       审核是否通过 false|truw
     */
    @PutMapping("/organization/{organizationId}")
    public void audit(HttpServletRequest request, @PathVariable Integer organizationId, boolean hasCheck) {
        Integer adminId = (Integer) request.getAttribute("adminId");
        Organization organization = new Organization();
        organization.setOrganizationId(organizationId);
        organization.setAuditorId(adminId);
        organization.setHasCheck(hasCheck);
        organizationService.audit(organization);
    }

    /**
     * 获取历史操作
     *
     * @param typeCode 1(用户认证)|2(组织审核)
     * @return List<User | organization>
     */
    @GetMapping("/history/{typeCode}")
    public List<?> getHistory(@PathVariable Integer typeCode) {
        if (typeCode == 1) {
            List<User> users = userService.findHistory();
            for (User user : users) {
                user.setSecretKey(null);
                user.setPassword(null);
                Admin auditor = user.getAuditor();
                if (auditor != null) {
                    auditor.setSalt(null);
                    auditor.setPassword(null);
                }
            }
            return users;
        } else {
            List<Organization> organizations = organizationService.findHistory();
            for (Organization organization : organizations) {
                organization.setFounder(null);
                Admin auditor = organization.getAuditor();
                auditor.setSalt(null);
                auditor.setPassword(null);
                organization.setAuditor(auditor);
            }
            return organizations;
        }
    }

    /**
     * 分页获取所有用户
     *
     * @param pageNumber,pageSize limit
     * @return {
     * "users":此页用户,
     * "totalNum":总数据条数
     * }
     */
    @GetMapping("/user")
    public Map<String, Object> getAll(Integer pageNumber, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        List<User> users = userService.findPart(Map.of("offset", (pageNumber - 1) * pageSize, "length", pageSize));
        for (User user : users) {
            user.setPhoneNo(null);
            user.setPassword(null);
            user.setAvatar(null);
            Admin auditor = user.getAuditor();
            if (auditor != null) {
                auditor.setSalt(null);
                auditor.setPassword(null);
            }
        }
        map.put("users", users);
        map.put("totalNum", userService.findTotalCount());
        return map;
    }

    /**
     * 根据用户id获取用户
     *
     * @return user
     */
    @GetMapping("/user/{userId}")
    public User getById(@PathVariable Integer userId) {
        User user = userService.findUserAuditorById(userId);
        user.setPhoneNo(null);
        user.setPassword(null);
        user.setAvatar(null);
        Admin auditor = user.getAuditor();
        if (auditor != null) {
            auditor.setSalt(null);
            auditor.setPassword(null);
        }
        return user;
    }

    /**
     * 审核个人认证
     *
     * @param request 获取审核人id
     * @param user    {
     *                "userId":被审核用户id,
     *                "hasCheck":true|false
     *                }
     */
    @PutMapping("/user")
    public void updateStatusById(HttpServletRequest request, User user) {
        Integer adminId = (Integer) request.getAttribute("adminId");
        user.setAuditorId(adminId);
        log.info("{userId : {}, hasCheck : {}, auditorId : {}}", user.getUserId(), user.getHasCheck(), adminId);
        userService.updateByIdSelective(user);
    }
}
