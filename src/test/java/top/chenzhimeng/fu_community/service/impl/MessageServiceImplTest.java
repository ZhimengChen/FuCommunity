package top.chenzhimeng.fu_community.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.chenzhimeng.fu_community.service.IMessageService;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class MessageServiceImplTest {

    @Autowired
    private IMessageService messageService;

    @Test
    void findUnreadCountsByUserId() {
        messageService.findUnreadCountsByUserId(8);
    }
}