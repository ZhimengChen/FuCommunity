package top.chenzhimeng.fu_community.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chenzhimeng.fu_community.mapper.CommentMapper;
import top.chenzhimeng.fu_community.mapper.MessageMapper;
import top.chenzhimeng.fu_community.mapper.NewsMapper;
import top.chenzhimeng.fu_community.model.Comment;
import top.chenzhimeng.fu_community.model.Message;
import top.chenzhimeng.fu_community.model.News;
import top.chenzhimeng.fu_community.service.ICommentService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Service("commentService")
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private NewsMapper newsMapper;
    @Autowired
    private ExecutorService threadPool;

    @Override
    public int comment(Comment comment) {
        threadPool.execute(() -> {
            News news = newsMapper.selectByPrimaryKey(comment.getNewsId());
            Integer publisherId = news.getPublisherId();
            Message message = new Message();
            message.setReceiverId(publisherId);
            message.setType(1);
            message.setContent(String.valueOf(news.getNewsId()));
            messageMapper.insertSelective(message);
        });
        return commentMapper.insertSelective(comment);
    }

    @Override
    public List<Comment> findPart(Map<String, Object> map) {
        return commentMapper.selectPart(map);
    }

    @Override
    public int delete(Map<String, Integer> map) {
        return commentMapper.deleteByIdAndUserId(map);
    }
}
