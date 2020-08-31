package top.chenzhimeng.fu_community.service;

import top.chenzhimeng.fu_community.model.User;

import java.util.List;
import java.util.Map;

public interface IUserService {
    int insert(User user);

    User findByPhoneNo(String phoneNo);

    int updateByIdSelective(User user);

    List<User> findHistory();

    User findById(Integer userId);

    List<User> findByOrganizationId(Integer organizationId);

    User findUserOrganizationsNewsByIds(Integer myId, Integer userId);

    List<User> findSubscribedById(Map<String, Integer> map);

    int updateAuditStateById(Map<String, Object> map);

    int updateAvatarById(User user);

    List<User> findApplicationsByOrganizationId(Integer organizationId);

    List<User> findPart(Map<String, Integer> map);

    int findTotalCount();

    User findUserAuditorById(Integer userId);
}
