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
