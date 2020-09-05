package top.chenzhimeng.fu_community.controller;

import com.aliyuncs.exceptions.ClientException;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;
import top.chenzhimeng.fu_community.model.MessageTemplate;
import top.chenzhimeng.fu_community.model.User;
import top.chenzhimeng.fu_community.service.IFansService;
import top.chenzhimeng.fu_community.service.ILikeService;
import top.chenzhimeng.fu_community.service.IOrganizationService;
import top.chenzhimeng.fu_community.service.IUserService;
import top.chenzhimeng.fu_community.util.EncryptUtil;
import top.chenzhimeng.fu_community.util.FileUtil;
import top.chenzhimeng.fu_community.util.SMS_Util;
import top.chenzhimeng.fu_community.util.TokenUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IFansService fansService;
    @Autowired
    private ILikeService likeService;
    @Autowired
    private IOrganizationService organizationService;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;
    @Autowired
    private ExecutorService threadPool;
    @Value(("${protocol-address}"))
    private String protocolAddress;

    /**
     * 发送手机验证码
     *
     * @param phoneNo 目标号码
     * @param type    确定短信类型：register|login|verify
     * @return {
     * "result": true|false,
     * "key": "用于获取验证码的key",
     * "msg": "错误提示消息"
     * }
     */
    @PostMapping("/auth/{type}")
    public Map<String, Object> getAuthCode(String phoneNo, @PathVariable String type) throws ClientException {
        log.info("auth code {phoneNo: {}, type: {}}", phoneNo, type);
        Map<String, Object> map = new HashMap<>();
        map.put("result", false);

        if (!phoneNo.matches("^1[3-9]\\d{9}$")) {
            map.put("msg", "该手机号不合法");
            return map;
        }

        MessageTemplate messageTemplate;
        switch (type) {
            case "register":
                messageTemplate = MessageTemplate.REGISTER;
                break;
            case "login":
                messageTemplate = MessageTemplate.LOGIN;
                break;
            case "verify":
                messageTemplate = MessageTemplate.VERIFY;
                break;
            default:
                map.put("msg", "短信类型不合法");
                return map;
        }

        Integer code = (int) (Math.random() * 9000 + 1000);
        if (!SMS_Util.send(messageTemplate, phoneNo, code)) {
            map.put("msg", "操作过频繁，请稍后再试");
            return map;
        }

        String key = UUID.randomUUID().toString();
//        redisService.setStr(phoneNo + '_' + key, String.valueOf(code), 300);
        redisTemplate.opsForValue().set(phoneNo + '_' + key, code, 5, TimeUnit.MINUTES);
        map.put("result", true);
        map.put("key", key);
        return map;
    }

    /**
     * 注册
     *
     * @param user     {"phoneNo":"手机号","password":"密码"}
     * @param key      用户查询验证码
     * @param authCode 用户输入的验证码
     * @return {
     * "result": true|false,
     * "msg": "错误提示消息",
     * "tokens": ["accessToken","refreshToken"]
     * }
     */
    @PostMapping()
    public Map<String, Object> register(User user, String key, Integer authCode) throws InvalidKeyException,
            NoSuchAlgorithmException {
        log.info("[phoneNo: {}; authCode: {}; password: {}; key: {}]", user.getPhoneNo(), authCode, user.getPassword(), key);
        ValueOperations<String, Serializable> stringSerializableValueOperations = redisTemplate.opsForValue();
        Map<String, Object> map = new HashMap<>();
        map.put("result", false);

        if (!user.getPhoneNo().matches("^1[3-9]\\d{9}$")) {
            map.put("msg", "该手机号不合法");
            return map;
        }

        Integer code = (Integer) stringSerializableValueOperations.get(user.getPhoneNo() + '_' + key);
        if (code == null || !code.equals(authCode)) {
            map.put("msg", "验证码错误或过期");
            return map;
        }

        redisTemplate.delete(user.getPhoneNo() + '_' + key);

        String[] keyAndSecret = EncryptUtil.encrypt(user.getPassword());
        user.setSecretKey(keyAndSecret[0]);
        user.setPassword(keyAndSecret[1]);

        try {
            userService.insert(user);
            String[] accessAndRefreshToken = TokenUtil.signTokens(user.getUserId(), 1L);
            threadPool.submit(() -> stringSerializableValueOperations.increment("tokenVersion_" + user.getUserId().toString()));
            map.put("result", true);
            map.put("tokens", accessAndRefreshToken);
        } catch (Exception e) {
            map.put("msg", "该手机已被注册");
        }
        return map;
    }

    /**
     * 密码登录
     *
     * @param phoneNo  手机号
     * @param password 密码
     * @return {
     * "result": true|false,
     * "msg": "错误提示消息",
     * "tokens": ["accessToken","refreshToken"]
     * }
     */
    @PostMapping("/login/password/{phoneNo}")
    public Map<String, Object> loginWithPwd(@PathVariable String phoneNo, String password) throws InvalidKeyException, NoSuchAlgorithmException {
        log.info("[phoneNo: {}; password: {};]", phoneNo, password);
        ValueOperations<String, Serializable> serializableValueOperations = redisTemplate.opsForValue();
        Map<String, Object> map = new HashMap<>();
        map.put("result", false);
        User user = userService.findByPhoneNo(phoneNo);

        if (user == null) {
            map.put("msg", "账号不存在");
            return map;
        }

        if (EncryptUtil.encrypt(user.getSecretKey(), password).equals(user.getPassword())) {
            //防止多地登录
            Long tokenVersion = serializableValueOperations.increment("tokenVersion_" + user.getUserId());
            map.put("result", true);
            map.put("tokens", TokenUtil.signTokens(user.getUserId(), tokenVersion));
            return map;
        }

        map.put("msg", "密码错误");
        return map;
    }

    /**
     * 验证码登录（未注册设置密码）
     *
     * @param phoneNo  手机号
     * @param key      查询验证码
     * @param authCode 用户输入的验证码
     * @return {
     * "result": true|false,
     * "msg": "错误提示消息",
     * "tokens": ["accessToken","refreshToken"]
     * }
     */
    @PostMapping("/login/auth-code/{phoneNo}")
    public Map<String, Object> loginWithAuthCode(@PathVariable String phoneNo, String key, Integer authCode) {
        log.info("[phoneNo: {}; key: {}; authCode: {}]", phoneNo, key, authCode);
        ValueOperations<String, Serializable> serializableValueOperations = redisTemplate.opsForValue();
        Map<String, Object> map = new HashMap<>();
        map.put("result", false);

        Integer code = (Integer) serializableValueOperations.get(phoneNo + '_' + key);
        if (code != null && code.equals(authCode)) {
            map.put("result", true);

            User user = userService.findByPhoneNo(phoneNo);
            if (user == null) {
                map.put("msg", "请设置密码");
            } else {
                threadPool.submit(() -> redisTemplate.delete(phoneNo + '_' + key));
                Long tokenVersion = serializableValueOperations.increment("tokenVersion_" + user.getUserId());
                map.put("tokens", TokenUtil.signTokens(user.getUserId(), tokenVersion));
            }
            return map;
        }
        map.put("msg", "验证码错误或过期");
        return map;
    }

    /**
     * 若refreshToken未过期且与accessToken相匹配
     * 授予新的token对
     *
     * @param accessToken,refreshToken 待验证的token对
     * @return 返回新的token对   ["accessToken","refreshToken"]
     */
    @PutMapping("/token")
    public String[] refreshToken(String accessToken, String refreshToken) {
        if (!(accessToken == null || refreshToken == null || accessToken.isEmpty() || refreshToken.isEmpty())) {
            Object[] userIdAndTokenVersionAndRefreshToken = TokenUtil.verifyRefreshToken(refreshToken);
            if (userIdAndTokenVersionAndRefreshToken != null && accessToken.equals(userIdAndTokenVersionAndRefreshToken[2])) {
                return TokenUtil.signTokens((Integer) userIdAndTokenVersionAndRefreshToken[0],
                        (Long) userIdAndTokenVersionAndRefreshToken[1]);
            }
        }
        return null;
    }

    /**
     * 验证码重置密码
     *
     * @param phoneNo  手机号
     * @param key      用于查询验证码
     * @param authCode 用户输入验证码
     * @param password 用户新密码
     * @return {
     * "result": true|false,
     * "msg": "错误提示消息"
     * }
     */
    @PutMapping("/password/{phoneNo}")
    public Map<String, Object> resetPwd(@PathVariable String phoneNo, String key, Integer authCode, String password) {
        log.info("{phoneNo : {}, key : {}, authCode : {}, password : {}}", phoneNo, key, authCode, password);
        Map<String, Object> map = new HashMap<>();
        map.put("result", false);

        User user = userService.findByPhoneNo(phoneNo);
        if (user == null) {
            map.put("msg", "用户不存在");
            return map;
        }

        ValueOperations<String, Serializable> valueOperations = redisTemplate.opsForValue();
        Integer code = (Integer) valueOperations.get(phoneNo + '_' + key);
        if (code == null || !code.equals(authCode)) {
            map.put("msg", "验证码错误或过期");
            return map;
        }

        threadPool.submit(() -> {
            try {
                String[] keyAndSecret = EncryptUtil.encrypt(password);
                User newUser = new User();
                newUser.setUserId(user.getUserId());
                newUser.setSecretKey(keyAndSecret[0]);
                newUser.setPassword(keyAndSecret[1]);
                userService.updateByIdSelective(newUser);
                redisTemplate.delete(phoneNo + '_' + key);
                valueOperations.increment("tokenVersion_" + user.getUserId());
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                log.error(String.valueOf(e));
            }
        });
        map.put("result", true);
        return map;

    }

    /**
     * 上传认证信息
     *
     * @param request 用于获取userId
     * @param user    {"studentNo","studentName"}
     * @param card    学生卡照片
     * @return {
     * "result": true|false,
     * "msg": "错误提示消息"
     * }
     */
    @PutMapping("/identity")
    public Map<String, Object> identityAuth(HttpServletRequest request, User user, MultipartFile card) {
        Map<String, Object> map = new HashMap<>();
        map.put("result", false);
        Integer studentNo = user.getStudentNo();
        String studentName = user.getStudentName();

        if (studentNo == null || studentName == null || card == null) {
            map.put("msg", "请填写完整信息");
            return map;
        }

        Integer userId = (Integer) request.getAttribute("userId");
        String originalFilename = card.getOriginalFilename();
        log.info("update identity {userId: {}, studentNo: {}, studentName: {}, studentCardFileName: {}}",
                userId, studentNo, studentName, originalFilename);

        if (studentName.length() < 2 || studentName.length() > 5) {
            map.put("msg", "姓名长度应在2-5个字符内");
            return map;
        }

        if (studentNo < 1 || studentNo > 999999999) {
            map.put("msg", "学号不合法");
            return map;
        }

        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.indexOf('.') + 1);
        if (!suffix.matches("^(?i)JPG|PNG|JPEG|BMP|WEBP$")) {
            map.put("msg", "不支持的图片格式");
            return map;
        }

        String dateNow = LocalDate.now().toString();
        String fileName = UUID.randomUUID().toString() + '.' + suffix;

        User oldUser = userService.findById(userId);
        oldUser.setUserId(userId);
        oldUser.setStudentNo(studentNo);
        oldUser.setStudentName(HtmlUtils.htmlEscape(studentName));
        oldUser.setStudentCard(protocolAddress + dateNow + '/' + fileName);
        oldUser.setAuditorId(null);
        oldUser.setHasCheck(null);
        oldUser.setAuditTime(null);
        String oldCard = oldUser.getStudentCard();

        if (userService.updateById(oldUser) > 0) {
            map.put("result", true);
            threadPool.submit(() -> {
                if (oldCard != null) FileUtil.delete(oldCard);
                try {
                    userService.updateAuditStateById(Map.of("userId", userId));
                    FileUtil.transferTo(card, dateNow, fileName);
                } catch (IOException e) {
                    log.error(String.valueOf(e));
                }
            });
            return map;
        }

        map.put("msg", "更新失败");
        return map;
    }

    /**
     * 关注或取关某人或某组织
     *
     * @param request        用于请求方法和关注者
     * @param userId         被关注者
     * @param organizationId 被关注组织
     */
    @RequestMapping(value = "/fans", method = {RequestMethod.POST, RequestMethod.DELETE})
    public void subscribe(HttpServletRequest request, Integer userId, Integer organizationId) {
        String method = request.getMethod();
        Integer fansId = (Integer) request.getAttribute("userId");
        log.info("subscribed or deSubscribed {method : {}, subscribedUserId : {}, subscribedOrganizationId : {}, fansId : {}}", method, userId,
                organizationId, fansId);
        if (method.equalsIgnoreCase("post")) {
            fansService.insert(userId, organizationId, fansId);
        } else {
            fansService.delete(userId, organizationId, fansId);
        }
    }

    /**
     * 点赞或取消点赞某动态或评论
     * 需实名认证
     *
     * @param request 用于获取用户id和请求方式
     */
    @RequestMapping(value = "/like", method = {RequestMethod.POST, RequestMethod.DELETE})
    public void like(HttpServletRequest request, Integer newsId, Integer commentId) {
        String method = request.getMethod();
        Integer userId = (Integer) request.getAttribute("userId");
        log.info("like {method : {}, newsId : {}, commentId : {}, userId : {}}", method, newsId, commentId, userId);

        if (method.equalsIgnoreCase("post")) {
            likeService.insert(newsId, commentId, userId);
        } else {
            likeService.delete(newsId, commentId, userId);
        }
    }

    /**
     * 获取某组织所有成员信息（包括创建者和管理员）
     * 需有权限（自身是成员之一）
     *
     * @param request        用于获取用户id
     * @param organizationId 组织id
     * @return {
     * "result" : true|false,
     * "msg" : "查询失败才有此信息"
     * "founder":user
     * "admins":List<User>
     * "members":List<User>
     * }
     */
    @GetMapping("/organization/{organizationId}")
    public Map<String, Object> getOrganizationUsers(HttpServletRequest request, @PathVariable Integer organizationId) {
        Integer userId = (Integer) request.getAttribute("userId");
        String identity = organizationService.findIdentity(userId, organizationId);
        log.info("{userId : {}, organizationId : {}, identity : {}}", userId, organizationId, identity);

        Map<String, Object> map = new HashMap<>();
        map.put("result", false);
        if (identity.equals("VISITOR")) {
            map.put("msg", "无此权限");
            return map;
        }

        User founder = userService.findById(organizationService.findById(organizationId).getFounderId());
        List<User> admins = userService.findAdminsByOrganizationId(organizationId);
        List<User> members = userService.findMembersByOrganizationId(organizationId);

        User modifiedFounder = new User();
        modifiedFounder.setUserId(founder.getUserId());
        modifiedFounder.setAvatar(founder.getAvatar());
        modifiedFounder.setStudentName(founder.getStudentName());

        map.put("founder", modifiedFounder);
        map.put("admins", admins);
        map.put("members", members);
        map.put("result", true);
        return map;
    }

    /**
     * 获取某人个人信息
     * 头像
     * 姓名
     * 是否关注
     *
     * @param request 用于获取查看者id
     * @param userId  被查看者id
     * @return map{
     * "avatar":"头像url",
     * "studentName":"姓名",
     * "hasSubscribed":true|false,
     * "hasCheck":true|false|null
     * }
     */
    @GetMapping({"/{userId}"})
    public Map<String, Object> getUser(HttpServletRequest request, @PathVariable Integer userId) {
        Integer myId = (Integer) request.getAttribute("userId");

        if (userId == -1) userId = myId;
        log.info("get user info {userId : {}}",userId);
        User user = userService.findUserOrganizationsNewsByIds(myId, userId);

        if (user == null) return null;

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("avatar", user.getAvatar());
        map.put("studentName", user.getStudentName());
        map.put("hasSubscribed", user.getHasSubscribed());
        map.put("hasCheck", user.getHasCheck());
        return map;
    }

    /**
     * 获取本人关注列表
     *
     * @param request        用于获取用户id
     * @param pageNum,length limit
     * @param type           organization|user
     * @return List<User | Organization>
     */
    @GetMapping("/subscribed/{type}/{pageNum}/{length}")
    public Object getSubscribed(HttpServletRequest request, @PathVariable Integer pageNum, @PathVariable Integer length,
                                @PathVariable String type) {
        log.info("{type : {}, pageNum : {}, length : {}}", type, pageNum, length);
        if (!type.equals("user") && !type.equals("organization")) return null;

        Integer userId = (Integer) request.getAttribute("userId");
        log.info("{userId : {}}", userId);

        Map<String, Integer> map = Map.of("userId", userId, "offset", pageNum * length, "length", length);
        return type.equals("user") ? userService.findSubscribedById(map) : organizationService.findSubscribedByUserId(map);
    }

    /**
     * 更改本人头像
     *
     * @param request 获取用户id
     * @param avatar  新头像
     * @return {
     * "result":true|false,
     * "msg":"错误信息"
     * }
     */
    @PutMapping("/avatar")
    public Map<String, Object> changeAvatar(HttpServletRequest request, MultipartFile avatar) {
        Integer userId = (Integer) request.getAttribute("userId");
        String originalFilename = avatar.getOriginalFilename();
        log.info("change avatar {userId : {}, originalFileName : {}}", userId, originalFilename);
        Map<String, Object> map = new HashMap<>();
        map.put("result", false);

        assert originalFilename != null;
        String suffix = originalFilename.substring(originalFilename.indexOf('.') + 1);
        if (!suffix.matches("^(?i)JPG|PNG|JPEG|BMP|WEBP$")) {
            map.put("msg", "不支持的图片格式");
            return map;
        }

        String dateNow = LocalDate.now().toString();
        String fileName = UUID.randomUUID().toString() + '.' + suffix;
        User user = new User();
        user.setUserId(userId);
        user.setAvatar(protocolAddress + dateNow + '/' + fileName);
        if (userService.updateAvatarById(user) > 0) {
            map.put("result", true);
            threadPool.submit(() -> {
                try {
                    FileUtil.transferTo(avatar, dateNow, fileName);
                } catch (IOException e) {
                    log.error(String.valueOf(e));
                }
            });
            return map;
        }
        return map;
    }
}
