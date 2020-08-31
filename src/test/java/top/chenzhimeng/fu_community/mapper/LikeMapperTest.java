package top.chenzhimeng.fu_community.mapper;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class LikeMapperTest {

    @Autowired
    private LikeMapper likeMapper;

    @Test
    void insert() {
        likeMapper.insert(6, null, 8);
    }

    @Test
    void delete() {
        likeMapper.delete(6,null,8);
    }
}