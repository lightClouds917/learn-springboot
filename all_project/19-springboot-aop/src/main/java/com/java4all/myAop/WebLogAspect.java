package com.java4all.myAop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * Author: yunqing
 * Date: 2018/8/29
 * Description:切面类
 */

//定义一个切面类
@Aspect
@Component
public class WebLogAspect {

    private Logger logger = Logger.getLogger(getClass());
    
    /**记录方法调用开始时间*/
    ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 定义一个切入点，可以是规则表达式，也可以是某个package下的所有函数，也可以是一个注解,其实就是执行条件，满足此条件的就切入
     * 这里是：com.java4all.controller包以及子包下的所有类的所有方法，返回类型任意，方法参数任意
     */
    @Pointcut("execution(* com.java4all.controller..*.*(..))")
    public void webLog(){}

    /**
     * 在切入点之前执行
     * @param joinPoint 连接点，实在应用执行过程中能够插入切面的一个点，这个点可以是调用方法时，抛出异常时，甚至是修改一个字段时，
     *                  切面代码可以利用这些连接点插入到应用的正常流程中，并添加新的行为，如日志、安全、事务、缓存等。
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint){
        //记录调用开始时间
        startTime.set(System.currentTimeMillis());

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();

        //记录请求日志
        logger.info("======>url；"+request.getRequestURL().toString());
        logger.info("======>httpMethod:"+request.getMethod());
        logger.info("======>ip:"+ request.getRemoteAddr());
        logger.info("======>classMethod : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info("======>args："+ Arrays.toString(joinPoint.getArgs()));

    }


    /**
     * 在切入点之后执行
     * @param response
     */
    @AfterReturning(returning = "response",pointcut = "webLog()")
    public void doAfterReturn(JoinPoint joinPoint,Object response){

        //获取调用事件
        logger.info("======>"+joinPoint.getSignature().getDeclaringTypeName()+"."
                    +joinPoint.getSignature().getName()+"方法耗时："
                    +(System.currentTimeMillis()-startTime.get())/1000+"s");

        //记录返回值
        logger.info("======>response:"+response);
    }


}


