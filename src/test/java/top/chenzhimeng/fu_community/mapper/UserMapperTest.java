package top.chenzhimeng.fu_community.mapper;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void selectUserOrganizationsNewsByIds() {
        System.out.println(userMapper.selectUserOrganizationsNewsByIds(8, 9));
    }

    @Test
    void updateAuditStateById() {
        userMapper.updateAuditStateById(Map.of("userId", 9, "hasCheck", true));
    }
}