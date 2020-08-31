package top.chenzhimeng.fu_community.service;

import top.chenzhimeng.fu_community.model.Comment;

import java.util.List;
import java.util.Map;

public interface ICommentService {
    int comment(Comment comment);

    List<Comment> findPart(Map<String, Object> map);

    int delete(Map<String, Integer> map);
}
