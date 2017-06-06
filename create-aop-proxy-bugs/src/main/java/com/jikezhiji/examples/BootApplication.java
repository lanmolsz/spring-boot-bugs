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
