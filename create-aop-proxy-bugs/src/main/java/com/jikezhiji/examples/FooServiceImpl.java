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
