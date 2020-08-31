package top.chenzhimeng.fu_community.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import top.chenzhimeng.fu_community.util.TokenUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class TokenInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 用于身份验证
     * response.sendError(2385, "accessToken过期");
     * response.sendError(2386, "账号已在别处登录");
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String accessToken = request.getHeader("accessToken");
        if (!(accessToken == null || accessToken.isEmpty())) {
            Object[] userIdAndTokenVersion = TokenUtil.verifyAccessToken(accessToken);
            if (userIdAndTokenVersion == null) {
                response.sendError(2385, "accessToken过期");
                return false;
            }
            Integer tokenVersion = (Integer) redisTemplate.opsForValue().get("tokenVersion_" + userIdAndTokenVersion[0]);
            if (tokenVersion != null && userIdAndTokenVersion[1].equals(tokenVersion.longValue())) {
                request.setAttribute("userId", userIdAndTokenVersion[0]);
                return true;
            }
            response.sendError(2386, "账号已在别处登录");
        }
        return false;
    }
}
