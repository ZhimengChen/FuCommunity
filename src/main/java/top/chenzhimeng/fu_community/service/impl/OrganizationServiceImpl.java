package top.chenzhimeng.fu_community.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.chenzhimeng.fu_community.mapper.MessageMapper;
import top.chenzhimeng.fu_community.mapper.OrganizationMapper;
import top.chenzhimeng.fu_community.mapper.UserMapper;
import top.chenzhimeng.fu_community.model.Message;
import top.chenzhimeng.fu_community.model.News;
import top.chenzhimeng.fu_community.model.Organization;
import top.chenzhimeng.fu_community.model.User;
import top.chenzhimeng.fu_community.service.IOrganizationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Service("organizationService")
public class OrganizationServiceImpl implements IOrganizationService {

    @Autowired
    private OrganizationMapper organizationMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ExecutorService threadPool;

    @Override
    public int insert(Organization organization) {
        return organizationMapper.insert(organization);
    }

    @Override
    public List<Organization> findByFounderId(Integer founderId) {
        return organizationMapper.selectByFounderId(founderId);
    }

    @Override
    public List<Organization> findByAdminId(Integer adminId) {
        return organizationMapper.selectByAdminId(adminId);
    }

    @Override
    public List<Organization> findByMemberId(Integer memberId) {
        return organizationMapper.selectByMemberId(memberId);
    }

    @Override
    public List<Organization> findHot(List<Integer> excludeIds) {
        return organizationMapper.selectHot(excludeIds);
    }

    @Override
    public List<Organization> findByFuzzyName(String fuzzyName) {
        return organizationMapper.selectByFuzzyName(fuzzyName);
    }

    /**
     * 查看某人是否是某组织的创建者或者管理员
     *
     * @param news 用于获取userId和founderId
     * @return 1（是）|0（不是）
     */
    @Override
    public boolean checkAuth(News news) {
        Map<String, Integer> map = Map.of("organizationId", news.getOrganizationId(), "userId", news.getPublisherId());
        return organizationMapper.isFounder(map) > 0 || organizationMapper.isAdmin(map) > 0;
    }

    @Override
    public List<Organization> findAll() {
        return organizationMapper.selectAll();
    }

    @Override
    public Organization findById(Integer organizationId) {
        return organizationMapper.selectByPrimaryKey(organizationId);
    }

    @Override
    public List<Organization> findHistory() {
        return organizationMapper.selectHistory();
    }

    @Override
    public String findIdentity(Integer userId, Integer organizationId) {
        Map<String, Integer> map = Map.of("userId", userId, "organizationId", organizationId);
        if (organizationMapper.isFounder(map) > 0) {
            return "FOUNDER";
        }
        if (organizationMapper.isAdmin(map) > 0) {
            return "ADMIN";
        }
        if (organizationMapper.isMember(map) > 0) {
            return "MEMBER";
        }
        return "VISITOR";
    }

    @Override
    public int insertMember(Integer memberId, Integer organizationId) {
        threadPool.execute(() -> {
            List<User> adminsAndFounder = userMapper.selectAdminsAndFounderByOrganizationId(organizationId);
            Message message = new Message();
            message.setType(5);
            message.setContent("有用户申请加入组织'" + organizationMapper.selectByPrimaryKey(organizationId).getOrganizationName() + "',快去处理吧！");
            for (User user : adminsAndFounder) {
                message.setReceiverId(user.getUserId());
                messageMapper.insertSelective(message);
            }
        });
        return organizationMapper.insertMember(memberId, organizationId);
    }

    @Override
    public int deleteMember(Integer organizationId, Integer memberId) {
        return organizationMapper.deleteMember(organizationId, memberId);
    }

    @Override
    public int deleteAdmin(Integer organizationId, Integer adminId) {
        return organizationMapper.deleteAdmin(organizationId, adminId);
    }

    @Override
    public List<Organization> findSubscribedByUserId(Map<String, Integer> map) {
        return organizationMapper.selectSubscribedByUserId(map);
    }

    @Override
    public int deleteMemberApplication(Map<String, Integer> map) {
        threadPool.execute(() -> {
            Message message = new Message();
            message.setReceiverId(map.get("memberId"));
            message.setType(2);
            message.setContent("组织'" + organizationMapper.selectByPrimaryKey(map.get("organizationId")).getOrganizationName() + "'拒绝了您的申请。");
            messageMapper.insertSelective(message);
        });
        return organizationMapper.deleteMemberApplication(map);
    }

    @Override
    public int agreeMemberApplication(Map<String, Integer> map) {
        threadPool.execute(() -> {
            Message message = new Message();
            message.setReceiverId(map.get("memberId"));
            message.setType(2);
            message.setContent("组织'" + organizationMapper.selectByPrimaryKey(map.get("organizationId")).getOrganizationName() + "'同意了您的申请。您已经是该组织的一员了！");
            messageMapper.insertSelective(message);
        });
        return organizationMapper.updateMember(map);
    }

    @Override
    public int audit(Organization organization) {
        threadPool.execute(() -> {
            Organization oldOrganization = organizationMapper.selectByPrimaryKey(organization.getOrganizationId());
            Message message = new Message();
            message.setReceiverId(oldOrganization.getFounderId());
            message.setType(3);
            message.setContent("您的组织'" + oldOrganization.getOrganizationName() + "'创建审核" + (organization.getHasCheck() ? "已" : "未") + "通过");
            messageMapper.insertSelective(message);
        });
        return organizationMapper.updateByPrimaryKeySelective(organization);
    }

    @Override
    @Transactional
    public boolean grantAdmin(Map<String, Integer> map) {
        organizationMapper.deleteMember(map.get("organizationId"), map.get("adminId"));
        return organizationMapper.insertAdmin(map) > 0;
    }

}
