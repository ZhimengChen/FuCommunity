package top.chenzhimeng.fu_community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@MapperScan("top/chenzhimeng/fu_community/mapper")
@ConfigurationPropertiesScan("top.chenzhimeng.fu_community.config")
public class FuCommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(FuCommunityApplication.class, args);
    }
}
