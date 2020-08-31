package top.chenzhimeng.fu_community.mapper;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.chenzhimeng.fu_community.model.News;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class NewsMapperTest {

    @Autowired
    private NewsMapper newsMapper;

    @Test
    void selectPart() {
        News news = newsMapper.selectPart(8, 0, 10).get(0);
        System.out.println(news);
    }
}