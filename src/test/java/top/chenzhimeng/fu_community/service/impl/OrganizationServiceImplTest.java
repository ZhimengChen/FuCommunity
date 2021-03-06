package top.chenzhimeng.fu_community.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.chenzhimeng.fu_community.service.IOrganizationService;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class OrganizationServiceImplTest {

    @Autowired
    private IOrganizationService organizationService;

    @Test
    void findByFuzzyName() {
        System.out.println(organizationService.findByFuzzyName("c"));
    }
}