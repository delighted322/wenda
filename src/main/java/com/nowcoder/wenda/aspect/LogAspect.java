package com.nowcoder.wenda.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;


@Aspect
@Component
public class LogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAspect.class);

    /**
     * 在执行....之前 先调用下面定义的方法
     * 第一个*是返回值 com.nowcoder.wenda.controller是包 第二个*是类 第三个*是方法 (..)是各种各样的参数
     */
    @Before("execution(* com.nowcoder.wenda.controller.*.*(..))")
    public void beforeMethod(JoinPoint joinPoint) { //JoinPoint 切点 里面有很多参数
        StringBuilder stringBuilder = new StringBuilder();
        for (Object arg : joinPoint.getArgs()) {
            stringBuilder.append("arg:" + arg.toString() + "|");
        }

        //可以对性能进行关注
        LOGGER.info("before method: " + stringBuilder);
    }

    @After("execution(* com.nowcoder.wenda.controller.*.*(..))")
    public void afterMethod() {
        LOGGER.info("after method" + new Date());
    }
}
