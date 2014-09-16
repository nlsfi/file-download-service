package fi.nls.fileservice.security.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class AllowFromLocalhostInterceptor implements HandlerInterceptor {

    public AllowFromLocalhostInterceptor() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception exception)
            throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler, ModelAndView mav)
            throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {

        if (!"127.0.0.1".equals(request.getRemoteAddr())) {
            return false;
        }

        return true;
    }

}
