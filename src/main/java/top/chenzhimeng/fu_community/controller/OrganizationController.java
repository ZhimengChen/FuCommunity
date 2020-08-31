package top.chenzhimeng.fu_community.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;
import top.chenzhimeng.fu_community.model.Organization;
import top.chenzhimeng.fu_community.service.IOrganizationService;
import top.chenzhimeng.fu_community.service.IUserService;
import top.chenzhimeng.fu_community.util.FileUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

@RestController
@Slf4j
@RequestMapping("/organization")
public class OrganizationController {

    @Autowired
    private IOrganizationService organizationService;
    @Autowired
    private IUserService userService;
    @Autowired
    private ExecutorService threadPool;
    @Value(("${protocol-address}"))
    private String protocolAddress;

    /**
     * 创建组织
     *
     * @param request      获取用户id
     * @param organization {"organizationName","logo","slogan","intro",founderId}
     * @param logoImg      logo图片
     * @return {
     * "result": true|false,
     * "msg": "错误提示消息"
     * }
     */
    @PostMapping()
    public Map<String, Object> found(HttpServletRequest request, Organization organization, MultipartFile logoImg) {
        Map<String, Object> map = new HashMap<>();
        map.put("result", false);
        Integer userId = (Integer) request.getAttribute("userId");

        String originalFilename = logoImg.getOriginalFilename();
        organization.setFounderId(userId);
        organization.setOrganizationName(HtmlUtils.htmlEscape(organization.getOrganizationName()));
        organization.setSlogan(HtmlUtils.htmlEscape(organization.getSlogan()));
        organization.setIntro(HtmlUtils.htmlEscape(organization.getIntro()));
        organization.setContact(HtmlUtils.htmlEscape(organization.getContact()));

        log.info("{organizationName : {}, logoOriginalFileName : {}, slogan : {}, intro : {}, founderId : {}, contact : {}}",
                organization.getOrganizationName(), originalFilename, organization.getSlogan(), organization.getIntro(),
                organization.getFounderId(), organization.getContact());

        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.indexOf('.') + 1);
        if (!suffix.matches("^(?i)JPG|BPM|PNG|TIFF|RAW$")) {
            map.put("msg", "不支持的图片格式");
            return map;
        }

        String dateNow = LocalDate.now().toString();
        String fileName = UUID.randomUUID().toString() + '.' + suffix;
        organization.setLogo(protocolAddress + dateNow + '/' + fileName);
        try {
            organizationService.insert(organization);
            map.put("result", true);
            threadPool.submit(() -> {
                try {
                    FileUtil.transferTo(logoImg, dateNow, fileName);
                } catch (IOException e) {
                    log.error(String.valueOf(e));
                }
            });
        } catch (Exception e) {
            map.put("msg", "该组织已存在");
        }
        return map;
    }

    /**
     * 查找用户创建、管理、加入的组织（并返回3个推荐组织）
     *
     * @param request 用于获取用户id
     * @return {"my":[organizations] , "recommend:[organizations]}
     */
    @GetMapping("/my")
    public Map<String, Object> getMy(HttpServletRequest request) throws ExecutionException, InterruptedException {
        Map<String, Object> map = new HashMap<>();
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("{userId: {}}", userId);

        List<Organization> organizations = new ArrayList<>();

        CompletableFuture<List<Organization>> queryByFounderId = CompletableFuture.supplyAsync(() -> organizationService.findByFounderId(userId));
        CompletableFuture<List<Organization>> queryByAdminId = CompletableFuture.supplyAsync(() -> organizationService.findByAdminId(userId));
        CompletableFuture<List<Organization>> queryByMemberId = CompletableFuture.supplyAsync(() -> organizationService.findByMemberId(userId));
        CompletableFuture.allOf(queryByFounderId, queryByAdminId, queryByMemberId).join();

        organizations.addAll(queryByFounderId.get());
        organizations.addAll(queryByAdminId.get());
        organizations.addAll(queryByMemberId.get());

        List<Integer> myOrganizationIds = new ArrayList<>();
        for (var organization : organizations) {
            myOrganizationIds.add(organization.getOrganizationId());
            organization.setContact(null);
            organization.setAuditorId(null);
            organization.setAuditTime(null);
        }
        map.put("my", organizations);

        List<Organization> hotOrganizations = organizationService.findHot(myOrganizationIds.size() > 0 ? myOrganizationIds : null);
        for (var organization : hotOrganizations) {
            organization.setContact(null);
            organization.setAuditorId(null);
            organization.setAuditTime(null);
        }
        map.put("recommend", hotOrganizations);
        return map;
    }

    /**
     * 模糊名查找组织
     *
     * @param word 关键字
     * @return [organizations]
     */
    @GetMapping("/{word}")
    public List<Organization> searchByName(@PathVariable String word) {
        List<Organization> byFuzzyName = organizationService.findByFuzzyName(word);
        for (Organization organization : byFuzzyName) {
            organization.setContact(null);
            organization.setAuditorId(null);
            organization.setAuditTime(null);
        }
        return byFuzzyName;
    }

    /**
     * 获取某组织，并查询该用户在该组织的权限
     *
     * @param request        用户获取用户id
     * @param organizationId 待查询的组织id
     * @return {
     * "organization" : organization,
     * "identity" : "FOUNDER|ADMIN|MEMBER|VISITOR"
     * }
     */
    @GetMapping("/{organizationId}/detail")
    public Map<String, Object> getById(HttpServletRequest request, @PathVariable Integer organizationId) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("{userId : {}, organizationId : {}}", userId, organizationId);

        Map<String, Object> map = new HashMap<>();
        Organization organization = organizationService.findById(organizationId);
        organization.setContact(null);
        organization.setAuditorId(null);
        organization.setAuditor(null);
        organization.setAuditTime(null);
        map.put("organization", organization);

        String identity = organizationService.findIdentity(userId, organizationId);
        map.put("identity", identity);
        return map;
    }

    /**
     * 申请成为某组织的成员
     *
     * @param request        用于申请者id
     * @param organizationId 被申请组织id
     */
    @PostMapping("/member")
    public void applyForMember(HttpServletRequest request, Integer organizationId) {
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("{userId : {}, organizationId : {}}", userId, organizationId);
        organizationService.insertMember(userId, organizationId);
    }

    /**
     * 退出组织（仅普通成员以及管理员可操作）
     *
     * @param request        用于获取用户id
     * @param organizationId 组织id
     */
    @DeleteMapping("/member")
    public void quit(HttpServletRequest request, Integer organizationId) {
        Integer userId = (Integer) request.getAttribute("userId");
        String identity = organizationService.findIdentity(userId, organizationId);
        if (identity.equals("MEMBER")) {
            organizationService.deleteMember(organizationId, userId);
        } else if (identity.equals("ADMIN")) {
            organizationService.deleteAdmin(organizationId, userId);
        }
    }

    /**
     * 组织管理员或创建者获取组织成员申请列表
     *
     * @param request        用于获取用户id查看权限
     * @param organizationId 组织id
     * @return users
     */
    @GetMapping("/member/application/{organizationId}")
    public Map<String, Object> getApplications(HttpServletRequest request, @PathVariable Integer organizationId) {
        Map<String, Object> map = new HashMap<>();
        map.put("result", false);

        Integer userId = (Integer) request.getAttribute("userId");
        String identity = organizationService.findIdentity(userId, organizationId);
        log.info("{userId : {}, organizationId : {}, identity : {}}", userId, organizationId, identity);

        if (!identity.equals("ADMIN") && !identity.equals("FOUNDER")) {
            map.put("msg", "权限不足");
            return map;
        }

        map.put("result", true);
        map.put("applications", userService.findApplicationsByOrganizationId(organizationId));
        return map;
    }

    /**
     * 组织管理员或创建者审核成员申请
     *
     * @param request               获取审核者id
     * @param organizationId,userId 申请
     */
    @RequestMapping(value = "/member/application", method = {RequestMethod.DELETE, RequestMethod.PUT})
    public void rejectApplication(HttpServletRequest request, Integer organizationId, Integer userId) {
        Integer myId = (Integer) request.getAttribute("userId");
        String identity = organizationService.findIdentity(myId, organizationId);
        String method = request.getMethod();
        log.info("{myId : {}, organizationId : {}, memberId : {}, identity : {}, method : {}}",
                myId, organizationId, userId, identity, method);
        Map<String, Integer> map = Map.of("memberId", userId, "organizationId", organizationId);

        if (identity.equals("ADMIN") || identity.equals("FOUNDER")) {
            if (method.equalsIgnoreCase("delete")) {
                organizationService.deleteMemberApplication(map);
            } else {
                organizationService.agreeMemberApplication(map);
            }
        }
    }

    /**
     * 组织管理员或创建者移除普通用户
     *
     * @param organizationId 组织id
     * @param memberId       成员id
     * @param request        用于或者操作者id
     * @return {
     * "result":true|false,
     * "msg":""
     * }
     */
    @DeleteMapping("/member/remove")
    public Map<String, Object> removeMember(Integer organizationId, Integer memberId, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        String identity = organizationService.findIdentity(userId, organizationId);
        log.info("removeMember {userId : {}, organizationId : {}, memberId : {}, identity : {}}", userId,
                organizationId, memberId, identity);
        Map<String, Object> map = new HashMap<>();
        map.put("result", false);

        if ((identity.equals("ADMIN") || identity.equals("FOUNDER")) && organizationService.findIdentity(memberId, organizationId).equals("MEMBER")) {
            organizationService.deleteMember(organizationId, memberId);
            map.put("result", true);
        } else {
            map.put("msg", "无此操作权限");
        }
        return map;
    }
}
