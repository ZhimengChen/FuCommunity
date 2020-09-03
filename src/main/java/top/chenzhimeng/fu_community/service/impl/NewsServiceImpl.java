package top.chenzhimeng.fu_community.service.impl;

import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.chenzhimeng.fu_community.mapper.MessageMapper;
import top.chenzhimeng.fu_community.mapper.NewsMapper;
import top.chenzhimeng.fu_community.mapper.OrganizationMapper;
import top.chenzhimeng.fu_community.mapper.UserMapper;
import top.chenzhimeng.fu_community.model.Message;
import top.chenzhimeng.fu_community.model.News;
import top.chenzhimeng.fu_community.model.User;
import top.chenzhimeng.fu_community.service.INewsService;
import top.chenzhimeng.fu_community.util.FileUtil;
import top.chenzhimeng.fu_community.util.MonitorUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Service("newsService")
@Slf4j
public class NewsServiceImpl implements INewsService {

    @Autowired
    private NewsMapper newsMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ExecutorService threadPoor;

    @Override
    public int publish(News news) {
        int i = newsMapper.insertSelective(news);
        threadPoor.execute(() -> {
            List<User> fans = news.getOrganizationId() == null ? userMapper.selectFansById(news.getPublisherId()) : userMapper.selectFansByOrganizationId(news.getOrganizationId());
            Message message = new Message();
            message.setType(4);
            message.setContent(String.valueOf(news.getNewsId()));
            for (User oneFans : fans) {
                message.setReceiverId(oneFans.getUserId());
                messageMapper.insertSelective(message);
            }
        });
        return i;
    }

    @Override
    public List<News> findPart(Integer userId, Integer offset, Integer length) {
        return newsMapper.selectPart(userId, offset, length);
    }

    @Override
    public List<News> findSubscribedPart(Integer userId, Integer offset, Integer length) {
        return newsMapper.selectSubscribedPart(userId, offset, length);
    }

    @Override
    public List<News> findPartByOrganizationId(Map<String, Integer> map) {
        return newsMapper.selectPartByOrganizationId(map);
    }

    @Override
    public News findById(Integer newsId) {
        return newsMapper.selectByPrimaryKey(newsId);
    }

    @Override
    public void deleteById(Integer newsId, String media) {
        newsMapper.deleteByPrimaryKey(newsId);
        messageMapper.deleteByContent(newsId.toString());
        threadPoor.execute(() -> {
            log.info("{media : {}}", media);
            String[] mediaArray = JSONArray.parseArray(media, String.class).toArray(String[]::new);
            for (String url : mediaArray) {
                FileUtil.delete(url);
            }
        });
    }

    @Override
    public int updateById(News news, String oldMedia) {
        threadPoor.execute(() -> {
            String[] newMediaArray = JSONArray.parseArray(news.getMedia(), String.class).toArray(String[]::new);
            String[] oldMediaArray = JSONArray.parseArray(oldMedia, String.class).toArray(String[]::new);
            for (String oldUrl : oldMediaArray) {
                if (Arrays.binarySearch(newMediaArray, oldUrl) < 0) {
                    FileUtil.delete(oldUrl);
                }
            }
        });
        newsMapper.updateByPrimaryKeySelective(news);
        return newsMapper.updateAuditState(Map.of("newsId", news.getNewsId()));
    }

    @Override
    public List<News> findByUserId(Map<String, Integer> map) {
        return newsMapper.selectPartByUserId(map);
    }

    @Override
    public News findDetailById(Map<String, Integer> map) {
        return newsMapper.selectById(map);
    }

}
