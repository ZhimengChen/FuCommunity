package top.chenzhimeng.fu_community.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.chenzhimeng.fu_community.service.INewsService;

@RunWith(SpringRunner.class)
@SpringBootTest
class NewsServiceImplTest {

    @Autowired
    private INewsService newsService;

    @Test
    void deleteById() {
        newsService.deleteById(6, "[http://www.chenzhimeng.top/fu-community/media/2020-07-16/43ed8026-68e9-43cc-bf2e-4bcb92dca5ce.png, http://www.chenzhimeng.top/fu-community/media/2020-07-16/7c9dd94b-8660-4408-8333-3e8b03f1f0e0.png, http://www.chenzhimeng.top/fu-community/media/2020-07-16/2c71eaef-3842-4759-9368-a6eaedd46cf1.png]");
    }
}