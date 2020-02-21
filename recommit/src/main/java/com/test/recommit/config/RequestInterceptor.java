package com.test.recommit.config;

import com.test.recommit.annotations.RequestLimit;
import com.test.recommit.util.ToolUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 防刷处理类
 * @author zqk
 * @since 2020/2/20
 */
@Component
public class RequestInterceptor extends HandlerInterceptorAdapter {

    @Resource(name = "stringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 方法是进行处理器拦截用的
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     * @author zqk
     * @since 2020/2/20
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod hm = (HandlerMethod) handler;
            RequestLimit requestLimit = hm.getMethodAnnotation(RequestLimit.class);
            if (null == requestLimit) {
                return true;
            }
            int seconds = requestLimit.reqSec();
            int maxCount = requestLimit.reqSecCount();
            String ip = ToolUtils.getRemoteAddr(request);
            String key = request.getServletPath() + ":" + request.getMethod() + ":" + ip;
            String countStr = redisTemplate.opsForValue().get(key);
            Integer count = Integer.parseInt(countStr == null ? "0" : countStr);
            if (count == null || -1 == count) {
                redisTemplate.opsForValue().set(key, "1");
                redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
                return true;
            }
            if (count < maxCount) {
                redisTemplate.opsForValue().increment(key, 1);
                redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
                return true;
            }
            if (count >= maxCount) {
                //可以不抛出异常，通过OutputStream输出信息
                throw new Exception("请求过于频繁，请" + seconds + "秒后再试");
            }
        }
        return true;
    }

    /**
     * 进行处理器拦截用的，它的执行时间是在处理器进行处理之后，也就是在Controller的方法调用之后执行
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param modelAndView
     * @author zqk
     * @since 2020/2/20
     */
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) {}

    /**
     * 调用前提：preHandle返回true,调用时间：DispatcherServlet进行视图的渲染之后,多用于清理资源(preHandle与postHandle执行之后)
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @param e
     * @author zqk
     * @since 2020/2/20
     */
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) { }

}