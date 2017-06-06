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
