package top.chenzhimeng.fu_community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 连接池
 */
@Configuration
public class ThreadPoolConf {

    private ExecutorService threadPool;

    @Bean("threadPool")
    public ExecutorService getCachedThreadPool() {
        if (this.threadPool == null) {
            ExecutorService threadPool = Executors.newCachedThreadPool();
            this.threadPool = threadPool;
            return threadPool;
        } else {
            return this.threadPool;
        }
    }
}
