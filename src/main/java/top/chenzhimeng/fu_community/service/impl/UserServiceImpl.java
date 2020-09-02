package top.chenzhimeng.fu_community.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chenzhimeng.fu_community.mapper.UserMapper;
import top.chenzhimeng.fu_community.model.User;
import top.chenzhimeng.fu_community.service.IUserService;
import top.chenzhimeng.fu_community.util.FileUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Service("userService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ExecutorService threadPool;


    /**
     * 创建一个新用户并为其插入默认labels
     *
     * @param user 用户
     * @return 1|0
     */
    @Override
    public int insert(User user) {
        return userMapper.insertSelective(user);
    }

    @Override
    public User findByPhoneNo(String phoneNo) {
        return userMapper.selectByPhoneNo(phoneNo);
    }

    @Override
    public int updateByIdSelective(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public List<User> findHistory() {
        return userMapper.selectHistory();
    }

    @Override
    public User findById(Integer userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public User findUserOrganizationsNewsByIds(Integer myId, Integer userId) {
        return userMapper.selectUserOrganizationsNewsByIds(myId, userId);
    }

    @Override
    public List<User> findSubscribedById(Map<String, Integer> map) {
        return userMapper.selectSubscribedById(map);
    }

    @Override
    public int updateAuditStateById(Map<String, Object> map) {
        return userMapper.updateAuditStateById(map);
    }

    @Override
    public int updateAvatarById(User user) {
        String oldAvatar = userMapper.selectByPrimaryKey(user.getUserId()).getAvatar();
        if (!oldAvatar.contains("default_avatar")) {
            FileUtil.delete(oldAvatar);
        }
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public List<User> findApplicationsByOrganizationId(Integer organizationId) {
        return userMapper.selectApplicationsByOrganizationId(organizationId);
    }

    @Override
    public List<User> findPart(Map<String, Integer> map) {
        return userMapper.selectPart(map);
    }

    @Override
    public int findTotalCount() {
        return userMapper.selectTotalCount();
    }

    @Override
    public User findUserAuditorById(Integer userId) {
        return userMapper.selectUserAuditorById(userId);
    }

    @Override
    public List<User> findAdminsByOrganizationId(Integer organizationId) {
        return userMapper.selectAdminsByOrganizationId(organizationId);
    }

    @Override
    public List<User> findMembersByOrganizationId(Integer organizationId) {
        return userMapper.selectMembersByOrganizationId(organizationId);
    }
}
