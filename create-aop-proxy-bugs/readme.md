# Spring Boot的bug记录


## 代码文件说明
在使用spring boot做开发的时候发现了一个bug，现记录如下：
为了重现bug，避免其他因素的干扰，特意创建了以下几个类。

`FooService.java`
```java
package com.jikezhiji.examples;

/**
 * Created by liusizuo on 2017/6/6.
 */
public interface FooService {
    void test();
}
```


`FooServiceImpl.java`
```java
package com.jikezhiji.examples;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by liusizuo on 2017/6/6.
 */
@Component
@Transactional
public class FooServiceImpl implements FooService{
    public void test() {
        System.out.println("service");
    }
}

```

`BootApplication.java`
```java
package com.jikezhiji.examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.CustomValidatorBean;

import javax.validation.Validator;

/**
 * Created by liusizuo on 2017/6/6.
 */
@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,value = BasicApplication.class))
public class BootApplication {

    public BootApplication() {
        super();
    }

    @Bean
    public Validator myValidator(){
        return new CustomValidatorBean();
    }

    @Autowired
    public void testInject(FooService service){
        System.out.println(service);
    }
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(BootApplication.class);
        FooService service = ctx.getBean(FooService.class);
        System.out.println(service);
    }
}

```


`BasicApplication.java`
```java
package com.jikezhiji.examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.CustomValidatorBean;

import javax.validation.Validator;


/**
 * Created by liusizuo on 2017/6/6.
 */

@EnableTransactionManagement
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,value = BootApplication.class))
@Configuration
public class BasicApplication {
    public BasicApplication() {
        super();
    }

    @Bean
    public Validator myValidator(){
        return new CustomValidatorBean();
    }

    @Autowired
    public void testInject(FooService service){
        System.out.println(service);
    }
    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(BasicApplication.class);
        FooService service = ctx.getBean(FooService.class);
        System.out.println(service);
    }
}
```

`BarService.java`
```java
package com.jikezhiji.examples;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liusizuo on 2017/6/6.
 */
@Service
public class BarService {

    @Autowired
    public BarService(FooService service)
    {
        System.out.println(service);
    }
}

```


`pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.jikezhiji</groupId>
    <artifactId>create-aop-proxy-bugs</artifactId>
    <version>1.0-SNAPSHOT</version>
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.3.RELEASE</version>
    </parent>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
        </dependency>
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-validator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.tomcat.embed</groupId>
            <artifactId>tomcat-embed-el</artifactId>
        </dependency>
    </dependencies>
</project>
```


## 重现步骤和说明

上面提到的类都放在了github上，地址是：https://github.com/lanmolsz/spring-boot-bugs ，相关代码在子目录create-aop-proxy-bugs中。

代码clone下来之后，将create-aop-proxy-bugs目录导入到ide，debug运行`BootApplication`，我们在运行`BootApplication`之前，先在34行和39行打上断点，查看注入的`FooService`对象。因为我在`FooServiceImpl`类上加上了`@Transactional`注解，那么这个对象按道理应该是一个`代理对象`才对，但结果它却不是，而是一个普通的java对象。

但我在注释掉`BootApplication`类上的`myValidator`和`testInject`中的任意一个方法之后，在断点查看`FooService`对象会发现它又正常了，称为我预期的`代理对象`。

为了验证其他的类对`FooService`引用的对象也依然不是`代理对象`，我后面又加了一个`BarService`类，断点查看注入的`FooService`依然只是一个普通的java对象，它的确不只是在创建过程中有问题，创建结束之后，这个对象依然不符合预期。

为了验证这个bug确实是由spring boot所引起的，因此添加了一个`BasicApplication`和`BootApplication`进行对照。发现在`BasicApplication`上，`FooService`被创建出来的对象始终都是`代理对象`，并没有这样的bug。

因此，这确定是spring boot的bug无疑。。


