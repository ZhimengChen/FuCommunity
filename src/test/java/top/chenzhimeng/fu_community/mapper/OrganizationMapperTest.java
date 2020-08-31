package top.chenzhimeng.fu_community.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.chenzhimeng.fu_community.model.News;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
class OrganizationMapperTest {

    @Autowired
    private OrganizationMapper organizationMapper;

    @Test
    void selectByFounderId() {
        System.out.println(organizationMapper.selectByFounderId(8));
    }

    @Test
    void selectByAdminId() {
        System.out.println(organizationMapper.selectByAdminId(8));
    }

    @Test
    void selectByMemberId() {
        System.out.println(organizationMapper.selectByMemberId(8));
    }

    @Test
    void selectHot() {
        System.out.println(organizationMapper.selectHot(List.of(1, 5)));
    }

    @Test
    void isFounder() {
        log.info(String.valueOf(organizationMapper.isFounder(Map.of("userId", 1, "organizationId", 9))));
    }

    @Test
    void selectAll() {
        System.out.println(organizationMapper.selectAll());
    }
}