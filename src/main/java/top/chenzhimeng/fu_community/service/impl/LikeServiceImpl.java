package top.chenzhimeng.fu_community.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chenzhimeng.fu_community.mapper.LikeMapper;
import top.chenzhimeng.fu_community.service.ILikeService;

@Service("likeService")
public class LikeServiceImpl implements ILikeService {

    @Autowired
    private LikeMapper likeMapper;

    @Override
    public int insert(Integer newsId, Integer commentId, Integer userId) {
        return likeMapper.insert(newsId, commentId, userId);
    }

    @Override
    public int delete(Integer newsId, Integer commentId, Integer userId) {
        return likeMapper.delete(newsId, commentId, userId);
    }
}
