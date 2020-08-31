package top.chenzhimeng.fu_community.service;

import top.chenzhimeng.fu_community.model.News;

import java.util.List;
import java.util.Map;

public interface INewsService {
    int publish(News news);

    List<News> findPart(Integer userId, Integer offset, Integer length);

    List<News> findSubscribedPart(Integer userId, Integer offset, Integer length);

    List<News> findPartByOrganizationId(Map<String, Integer> map);

    News findById(Integer newsId);

    void deleteById(Integer newsId, String media);

    int updateById(News news, String oldMedia);

    List<News> findByUserId(Map<String, Integer> map);

    News findDetailById(Map<String, Integer> map);
}
