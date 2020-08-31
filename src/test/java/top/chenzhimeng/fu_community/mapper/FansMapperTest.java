package top.chenzhimeng.fu_community.mapper;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class FansMapperTest {

    @Autowired
    private FansMapper fansMapper;

    @Test
    void insert() {
        fansMapper.insert(13,null,8);
    }

    @Test
    void delete() {
        fansMapper.delete(null,1,8);
    }
}