package top.chenzhimeng.fu_community.mapper;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
class CommentMapperTest {

    @Autowired
    private CommentMapper commentMapper;

    @Test
    void selectPart() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", 8);
        map.put("offset", 0);
        map.put("length", 5);
        map.put("newsId", 6);
        map.put("replyId",1);
        map.put("orderBy", "time");
        System.out.println(commentMapper.selectPart(map));
    }
}