package top.chenzhimeng.fu_community.util;

import com.aliyuncs.exceptions.ClientException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
class MonitorUtilTest {

    //    @Test
//    void textScan() throws ClientException {
//        System.out.println(MonitorUtil.textIsSpam(" "));
//    }
//
    @Test
    void imgScan() throws ClientException {
        MonitorUtil.imgScan("http://www.chenzhimeng.top/media/%E9%81%BF%E5%85%8D%E9%AB%98%E5%B3%B0bg.JPG");
    }
}