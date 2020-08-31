package top.chenzhimeng.fu_community.util;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class FileUtilTest {

    @Test
    void delete() {
        FileUtil.delete("http://www.chenzhimeng.top:10001/media/2020-07-16/2c71eaef-3842-4759-9368-a6eaedd46cf1.png");
    }
}