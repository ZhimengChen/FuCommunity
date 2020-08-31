package top.chenzhimeng.fu_community.service;

public interface IFansService {
    int insert(Integer subscribedUserId, Integer subscribedOrganizationId, Integer fansId);

    int delete(Integer subscribedUserId, Integer subscribedOrganizationId, Integer fansId);
}
