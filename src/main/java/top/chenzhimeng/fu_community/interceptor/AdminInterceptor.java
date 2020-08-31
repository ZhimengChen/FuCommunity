package top.chenzhimeng.fu_community.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import top.chenzhimeng.fu_community.util.TokenUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;

@Component
public class AdminInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 管理员拦截器
     *
     * @param request  用于获取请求头
     * @param response 用于发送错误码
     * @return true|false
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = request.getHeader("accessToken");
        if (!(accessToken == null || accessToken.isEmpty())) {
            Object[] adminIdAdminNameTokenVersion = TokenUtil.verifyAdminToken(accessToken);
            if (adminIdAdminNameTokenVersion == null) {
                response.sendError(2385, "accessToken过期");
                return false;
            }
            Integer tokenVersion = (Integer) redisTemplate.opsForValue().get("tokenVersion_" + adminIdAdminNameTokenVersion[1]);
            if (tokenVersion != null && adminIdAdminNameTokenVersion[2].equals(tokenVersion.longValue())) {
                request.setAttribute("adminId", adminIdAdminNameTokenVersion[0]);
                return true;
            }
            response.sendError(2386, "账号已在别处登录");
        }
        response.sendError(2384, "请先登录");
        return false;
    }
}
