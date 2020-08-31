package top.chenzhimeng.fu_community.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chenzhimeng.fu_community.mapper.FansMapper;
import top.chenzhimeng.fu_community.service.IFansService;

@Service("fansService")
public class FansServiceImpl implements IFansService {

    @Autowired
    private FansMapper fansMapper;

    @Override
    public int insert(Integer subscribedUserId, Integer subscribedOrganizationId, Integer fansId) {
        return fansMapper.insert(subscribedUserId, subscribedOrganizationId, fansId);
    }

    @Override
    public int delete(Integer subscribedUserId, Integer subscribedOrganizationId, Integer fansId) {
        return fansMapper.delete(subscribedUserId, subscribedOrganizationId, fansId);
    }
}
