导读：本文主要讲解Spring Boot的加载原理和常用的注解分析，分为以下几个部分：

- 1.@SpringBootApplication:注解分析
- 2.加载过程分析
- 3.常用的注解解析

如果只是想先学会如何在开发中迅速上手使用，可以跳过1，2两个部分，直接熟悉一下3部分即可。

### 1. @SpringBootApplication注解分析

我们先看一下Spring Boot的启动类代码：

```
package com.java4all;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

```

在Spring Boot的启动类中，只有一个注解：@SpringBootApplication，为什么这一个注解就能启动项目呢？

我们点击进入此注解，看一下@SpringBootApplication的源码：

```
//省略导包

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
public @interface SpringBootApplication {
    @AliasFor(
        annotation = EnableAutoConfiguration.class
    )
    Class<?>[] exclude() default {};

    @AliasFor(
        annotation = EnableAutoConfiguration.class
    )
    String[] excludeName() default {};

    @AliasFor(
        annotation = ComponentScan.class,
        attribute = "basePackages"
    )
    String[] scanBasePackages() default {};

    @AliasFor(
        annotation = ComponentScan.class,
        attribute = "basePackageClasses"
    )
    Class<?>[] scanBasePackageClasses() default {};
}

```

观察源码可发现@SpringBootApplication是一个复合注解，包含了@ComponentScan，和@SpringBootConfiguration，@EnableAutoConfiguration，我们分析一下这三个重要的注解：

#### 1.1@SpringBootConfiguration注解

此注解的源码是：

```
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
public @interface SpringBootConfiguration {
}
```

可以看到，这个注解类中有个@Configuration，这个注解很熟悉，在Spring中（3.0之后），这个注解标注的类，会作为配置类，可以替换xml配置文件，它会将当前类中声明的以@Bean注解标记的方法的实例纳入到Spring容器中，并且实例名就是方法名；@SpringBootConfiguration注解的功能和这个一致；

#### 1.2@EnableAutoConfiguration注解

这个注解，是Spring Boot自动化配置的关键，字面意思：可以自动化配置;Spring Boot有个spring.factories文件，里面预先配置了大量配置好的东西，我们通过注解和配置文件，再结合此注解，会进行有选择的将我们需要的实例加入到Spring 容器中。

这个注解的详细分析需要追踪大量源码，不再这里展开，我单独写了另外2篇文章；对源码原理感兴趣的，可以查看下面2篇文章，这两篇文章详细的追踪解释了这个自动化配置过程：（个人认为这个过程在早期研究意义不大，还是先把基本功能跑起来后再研究）

1.SpringBoot---核心原理：自动化配置1

2.SpringBoot---核心原理：自动化配置2

#### 1.3@ComponentScan

组件扫描。相当于xml中的<context:component-scan>，如果扫描到有@Component @Controller @Service，@Repository等这些注解的类，则把这些类注册为bean。

### 2.加载过程分析

这个我们追一下启动类中的main方法，main中有个run方法，把当前的主类当作参数传入run方法。
我们进入run方法：

```
    public static ConfigurableApplicationContext run(Class<?> primarySource, String... args) {
        return run(new Class[]{primarySource}, args);
    }
```

继续进入run方法中的run方法

```
    public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
        return (new SpringApplication(primarySources)).run(args);
    }
```

继续进入run方法

```
public ConfigurableApplicationContext run(String... args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ConfigurableApplicationContext context = null;
        Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList();
        this.configureHeadlessProperty();
        SpringApplicationRunListeners listeners = this.getRunListeners(args);
        listeners.starting();

        Collection exceptionReporters;
        try {
            ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
            ConfigurableEnvironment environment = this.prepareEnvironment(listeners, applicationArguments);
            this.configureIgnoreBeanInfo(environment);
            Banner printedBanner = this.printBanner(environment);
            context = this.createApplicationContext();
            exceptionReporters = this.getSpringFactoriesInstances(SpringBootExceptionReporter.class, new Class[]{ConfigurableApplicationContext.class}, new Object[]{context});
            this.prepareContext(context, environment, listeners, applicationArguments, printedBanner);
            this.refreshContext(context);
            this.afterRefresh(context, applicationArguments);
            stopWatch.stop();
            if(this.logStartupInfo) {
                (new StartupInfoLogger(this.mainApplicationClass)).logStarted(this.getApplicationLog(), stopWatch);
            }

            listeners.started(context);
            this.callRunners(context, applicationArguments);
        } catch (Throwable var10) {
            this.handleRunFailure(context, var10, exceptionReporters, listeners);
            throw new IllegalStateException(var10);
        }

        try {
            listeners.running(context);
            return context;
        } catch (Throwable var9) {
            this.handleRunFailure(context, var9, exceptionReporters, (SpringApplicationRunListeners)null);
            throw new IllegalStateException(var9);
        }
    }
```

这个run方法，是启动的关键步骤，我们再仔细分析一下启动日志：

```
2018-07-08 16:31:04.887  INFO 728 --- [           main] com.java4all.Application                 : Starting Application on wang with PID 728 (F:\java4all\learn-springboot\all_project\01-new-project\target\classes started by wangzhongxiang in F:\java4all\learn-springboot)
2018-07-08 16:31:04.899  INFO 728 --- [           main] com.java4all.Application                 : No active profile set, falling back to default profiles: default
2018-07-08 16:31:05.064  INFO 728 --- [           main] ConfigServletWebServerApplicationContext : Refreshing org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext@8f2ef19: startup date [Sun Jul 08 16:31:05 CST 2018]; root of context hierarchy
2018-07-08 16:31:06.961  INFO 728 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8080 (http)
2018-07-08 16:31:06.993  INFO 728 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2018-07-08 16:31:06.993  INFO 728 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet Engine: Apache Tomcat/8.5.31
2018-07-08 16:31:07.000  INFO 728 --- [ost-startStop-1] o.a.catalina.core.AprLifecycleListener   : The APR based Apache Tomcat Native library which allows optimal performance in production environments was not found on the java.library.path: [C:\tools_develop_install\jdk1.8.0_65\bin;C:\WINDOWS\Sun\Java\bin;C:\WINDOWS\system32;C:\WINDOWS;C:\ProgramData\Oracle\Java\javapath;C:\tools_develop_install\jdk1.8.0_65\bin;C:\tools_develop_install\apache-maven-3.3.9\bin;C:\Program Files (x86)\Intel\iCLS Client\;C:\Program Files\Intel\iCLS Client\;C:\Windows\system32;C:\Windows;C:\Windows\System32\Wbem;C:\Windows\System32\WindowsPowerShell\v1.0\;C:\Program Files (x86)\Intel\Intel(R) Management Engine Components\DAL;C:\Program Files\Intel\Intel(R) Management Engine Components\DAL;C:\Program Files (x86)\Intel\Intel(R) Management Engine Components\IPT;C:\Program Files\Intel\Intel(R) Management Engine Components\IPT;C:\Program Files\Intel\WiFi\bin\;C:\Program Files\Common Files\Intel\WirelessCommon\;C:\Program Files (x86)\NVIDIA Corporation\PhysX\Common;C:\tools_develop_install\mysql\bin;E:\wechat_jump_game-master\Tools\adb;C:\WINDOWS\system32;C:\WINDOWS;C:\WINDOWS\System32\Wbem;C:\WINDOWS\System32\WindowsPowerShell\v1.0\;C:\tools_develop_install\Git\cmd;C:\Users\wangzhongxiang\AppData\Local\Programs\Python\Python36\Scripts\;C:\Users\wangzhongxiang\AppData\Local\Programs\Python\Python36\;C:\Users\wangzhongxiang\AppData\Local\Microsoft\WindowsApps;;.]
2018-07-08 16:31:07.119  INFO 728 --- [ost-startStop-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2018-07-08 16:31:07.120  INFO 728 --- [ost-startStop-1] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 2063 ms
2018-07-08 16:31:07.298  INFO 728 --- [ost-startStop-1] o.s.b.w.servlet.ServletRegistrationBean  : Servlet dispatcherServlet mapped to [/]
2018-07-08 16:31:07.304  INFO 728 --- [ost-startStop-1] o.s.b.w.servlet.FilterRegistrationBean   : Mapping filter: 'characterEncodingFilter' to: [/*]
2018-07-08 16:31:07.304  INFO 728 --- [ost-startStop-1] o.s.b.w.servlet.FilterRegistrationBean   : Mapping filter: 'hiddenHttpMethodFilter' to: [/*]
2018-07-08 16:31:07.304  INFO 728 --- [ost-startStop-1] o.s.b.w.servlet.FilterRegistrationBean   : Mapping filter: 'httpPutFormContentFilter' to: [/*]
2018-07-08 16:31:07.304  INFO 728 --- [ost-startStop-1] o.s.b.w.servlet.FilterRegistrationBean   : Mapping filter: 'requestContextFilter' to: [/*]
2018-07-08 16:31:07.599  INFO 728 --- [           main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Mapped URL path [/**/favicon.ico] onto handler of type [class org.springframework.web.servlet.resource.ResourceHttpRequestHandler]
2018-07-08 16:31:07.914  INFO 728 --- [           main] s.w.s.m.m.a.RequestMappingHandlerAdapter : Looking for @ControllerAdvice: org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext@8f2ef19: startup date [Sun Jul 08 16:31:05 CST 2018]; root of context hierarchy
2018-07-08 16:31:07.974  INFO 728 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/login/login],methods=[GET]}" onto public java.lang.String com.java4all.controller.LoginController.login()
2018-07-08 16:31:07.978  INFO 728 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/error]}" onto public org.springframework.http.ResponseEntity<java.util.Map<java.lang.String, java.lang.Object>> org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController.error(javax.servlet.http.HttpServletRequest)
2018-07-08 16:31:07.979  INFO 728 --- [           main] s.w.s.m.m.a.RequestMappingHandlerMapping : Mapped "{[/error],produces=[text/html]}" onto public org.springframework.web.servlet.ModelAndView org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController.errorHtml(javax.servlet.http.HttpServletRequest,javax.servlet.http.HttpServletResponse)
2018-07-08 16:31:08.004  INFO 728 --- [           main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Mapped URL path [/webjars/**] onto handler of type [class org.springframework.web.servlet.resource.ResourceHttpRequestHandler]
2018-07-08 16:31:08.004  INFO 728 --- [           main] o.s.w.s.handler.SimpleUrlHandlerMapping  : Mapped URL path [/**] onto handler of type [class org.springframework.web.servlet.resource.ResourceHttpRequestHandler]
2018-07-08 16:31:08.187  INFO 728 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Registering beans for JMX exposure on startup
2018-07-08 16:31:08.236  INFO 728 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2018-07-08 16:31:08.240  INFO 728 --- [           main] com.java4all.Application                 : Started Application in 4.231 seconds (JVM running for 5.384)

```

结合日志和run方法，我们可以看到项目的启动过程中：

- 1：分配一个线程id
- 2：选择一个配置文件，如果没有就按照默认的配置启动
- 3：启动tomcat，默认端口8080
- 4：启动servlet引擎tomcat
- 5：初始化Sring 内置的WebApplicationContext
- 6：默认配置dispatcherServlet
- 7：默认配置characterEncodingFilter
- 8：默认配置hiddenHttpMethodFilter
- 9：默认配置httpPutFormContentFilter
- 10：默认配置requestContextFilter
- ...
- ...

可以看到这些在spring项目中需要我们自己手动配置的东西现在都是默认配置好了，这再一次验证了Spring Boot的理念：约定大于配置。

### 3.常用的注解解析

在Spring Boot中有很多强大的注解，当然，大部分注解并不是Spring Boot原创的，在Spring时代已经开始使用了，下面列出在Spring Boot中常用的一些注解：

@SpringBootApplication:
这是Sprint Boot的标识，它包含@Configuration、@EnableAutoConfiguration、@ComponentScan
的作用，通常用在程序的主类上，是程序的入口。

@RestController:
一般用在控制层，比如controller，包含@Controller和@ResponseBody,有此注解后，就不用在方法上标注@ResponseBody了，接口会自动返回json格式的数据。

@Service:
用于标注业务层组件。 

@Repository:
用于标注数据访问组件，即DAO组件。

@ResponseBody：
表示该方法的返回结果直接写入HTTP response body中
一般在异步获取数据时使用，在使用@RequestMapping后，返回值通常解析为跳转路径，加上@responsebody后返回结果不会被解析为跳转路径，而是直接写入HTTP response body中。比如异步获取json数据，加上@responsebody后，会直接返回json数据。

@Component：
泛指组件，当组件不好归类的时候，我们可以使用这个注解进行标注。

@ComponentScan：
组件扫描。相当于<context:component-scan>，如果扫描到有@Component @Controller @Service等这些注解的类，则把这些类注册为bean。

@Configuration：
指出该类是 Bean 配置的信息源，相当于XML中的<beans></beans>，一般加在主类上。

@Bean:
相当于XML中的<bean></bean>,放在方法的上面，而不是类，意思是产生一个bean,并交给spring管理。

@EnableAutoConfiguration：
让 Spring Boot 根据应用所声明的依赖来对 Spring 框架进行自动配置，一般加在主类上。

@AutoWired:
把配置好的Bean拿来用，完成属性、方法的组装，它可以对类成员变量、方法及构造函数进行标注，完成自动装配的工作。
当加上（required=false）时，就算找不到bean也不报错。

@Qualifier：
当有多个同一类型的Bean时，可以用@Qualifier("name")来指定。与@Autowired配合使用

@Resource(name="name",type="type")：
没有括号内内容的话，默认byName。与@Autowired干类似的事。

@RequestMapping：
RequestMapping是一个用来处理请求地址映射的注解，可用于类或方法上。用于类上，表示类中的所有响应请求的方法都是以该地址作为父路径。
该注解有六个属性：

- params:指定request中必须包含某些参数值时，才让该方法处理。
- headers:指定request中必须包含某些指定的header值，才能让该方法处理请求。
- value:指定请求的实际地址，指定的地址可以是URI Template 模式
- method:指定请求的method类型， GET、POST、PUT、DELETE等
- consumes:指定处理请求的提交内容类型（Content-Type），如application/json,text/html;
- produces:指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回

@RequestParam：
用在方法的参数前面。
@RequestParam String a =request.getParameter("a")。

@PathVariable:
路径变量。参数与大括号里的名字一样要相同。
@RequestMapping("user/get/mac/{macAddress}")
public String getByMacAddress(@PathVariable String macAddress){
　　//do something;
}

@Profiles
Spring Profiles提供了一种隔离应用程序配置的方式，并让这些配置只能在特定的环境下生效。
任何@Component或@Configuration都能被@Profile标记，从而限制加载它的时机。

@Configuration
@Profile("prod")
public class ProductionConfiguration {
    // ...
}

@ExceptionHandler（Exception.class）：
用在方法上面表示遇到这个异常就执行以下方法。