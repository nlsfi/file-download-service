package fi.nls.fileservice.security.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import fi.nls.fileservice.security.AuthorizationContextHolder;

public abstract class BaseAuthorizationInterceptor extends
        HandlerInterceptorAdapter {

    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception throwable)
            throws Exception {

        // clean up ThreadLocal after use IMPORTANT!!!
        AuthorizationContextHolder.unset();

    }

    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler, ModelAndView mav)
            throws Exception {
        // noop

    }

}
