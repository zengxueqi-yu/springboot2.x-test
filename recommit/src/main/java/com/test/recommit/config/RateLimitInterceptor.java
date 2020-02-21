package com.test.recommit.config;

import com.test.recommit.util.ToolUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 接口限流处理类
 * @author zqk
 * @since 2020/2/20
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    @Resource(name = "stringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;
    /**
     * 自定义请求接口最大次数限制
     */
    @Value("${request.max-count}")
    public int maxCount;
    /**
     * 自定义请求接口时间限时
     */
    @Value("${request.seconds}")
    private int seconds;

    /**
     * 方法是进行处理器拦截用的
     * @param httpServletRequest
     * @param httpServletResponse
     * @param o
     * @return
     * @throws Exception
     * @author zqk
     * @since 2020/2/20
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        //获取电脑的mac地址作为redis key
        String string = redisTemplate.opsForValue().get(ToolUtils.getMACAddress());
        Integer count = Integer.valueOf(string == null ? "0" : string);
        if (count > maxCount) {
            throw new Exception("当前服务器访问量太大，请稍后再试");
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
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        redisTemplate.opsForValue().increment(ToolUtils.getMACAddress(), 1);
        redisTemplate.expire(ToolUtils.getMACAddress(), seconds, TimeUnit.SECONDS);
    }
}
