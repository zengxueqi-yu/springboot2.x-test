package com.test.recommit.annotations;

import com.test.recommit.common.SysConfig;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * 接口放刷限流注解
 *
 * @author zqk
 * @since 2020/2/20
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RequestLimit {

    /**
     * 请求限时
     * @return
     */
    int reqSec() default SysConfig.seconds;

    /**
     * 请求限时内的最大请求数
     * @return
     */
    int reqSecCount() default SysConfig.maxCount;

}
