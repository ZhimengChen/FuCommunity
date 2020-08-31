package top.chenzhimeng.fu_community.service;

public interface ILikeService {
    int insert(Integer newsId, Integer commentId, Integer userId);

    int delete(Integer newsId, Integer commentId, Integer userId);
}
