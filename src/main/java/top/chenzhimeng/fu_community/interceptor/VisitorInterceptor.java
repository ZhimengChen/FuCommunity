package top.chenzhimeng.fu_community.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import top.chenzhimeng.fu_community.service.IUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class VisitorInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equalsIgnoreCase("get")) return true;

        Boolean hasCheck = userService.findById((Integer) request.getAttribute("userId")).getHasCheck();
        if (hasCheck == null || hasCheck.equals(false)) {
            response.sendError(2387, "功能尚未解锁");
            return false;
        }
        return true;

    }
}
