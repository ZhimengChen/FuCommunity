package top.chenzhimeng.fu_community.service;

import top.chenzhimeng.fu_community.model.News;
import top.chenzhimeng.fu_community.model.Organization;
import top.chenzhimeng.fu_community.model.User;

import java.util.List;
import java.util.Map;

public interface IOrganizationService {
    int insert(Organization organization);

    List<Organization> findByFounderId(Integer founderId);

    List<Organization> findByAdminId(Integer adminId);

    List<Organization> findByMemberId(Integer memberId);

    List<Organization> findHot(List<Integer> excludeIds);

    List<Organization> findByFuzzyName(String fuzzyName);

    boolean checkAuth(News news);

    List<Organization> findAll();

    Organization findById(Integer organizationId);

    int updateById(Organization organization);

    List<Organization> findHistory();

    String findIdentity(Integer userId, Integer organizationId);

    int insertMember(Integer memberId, Integer organizationId);

    int deleteMember(Integer organizationId, Integer memberId);

    int deleteAdmin(Integer organizationId, Integer adminId);

    List<Organization> findSubscribedByUserId(Map<String, Integer> map);

    int deleteMemberApplication(Map<String, Integer> map);

    int agreeMemberApplication(Map<String, Integer> map);

    int audit(Organization organization);

}
