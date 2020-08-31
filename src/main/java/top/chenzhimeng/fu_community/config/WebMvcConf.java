package top.chenzhimeng.fu_community.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import top.chenzhimeng.fu_community.interceptor.AdminInterceptor;
import top.chenzhimeng.fu_community.interceptor.TokenInterceptor;
import top.chenzhimeng.fu_community.interceptor.VisitorInterceptor;

@Configuration
public class WebMvcConf extends WebMvcConfigurationSupport {

    @Autowired
    private TokenInterceptor tokenInterceptor;
    @Autowired
    private AdminInterceptor adminInterceptor;
    @Autowired
    private VisitorInterceptor visitorInterceptor;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .excludePathPatterns("/user/auth/{type}",
                        "/user",
                        "/user/login/**",
                        "/user/token",
                        "/user/password/{phoneNo}",
                        "/news/callback",
                        "/admin",
                        "/admin/**")
                .addPathPatterns("/**")
                .order(1);

        registry.addInterceptor(adminInterceptor)
                .excludePathPatterns("/admin")
                .addPathPatterns("/admin/**")
                .order(2);

        registry.addInterceptor(visitorInterceptor)
                .addPathPatterns("/organization",
                        "/organization/**",
                        "/message",
                        "/message/**",
                        "/news",
                        "/comment",
                        "/user/like",
                        "/user/fans")
                .order(3);
    }

}
