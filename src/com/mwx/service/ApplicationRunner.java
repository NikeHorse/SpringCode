package com.mwx.service;

import com.mwx.spring.ApplicationContext;

/**
 * 测试启动类
 *
 * @author MWX
 */
public class ApplicationRunner {
    public static void main(String[] args) {
        //定义上下文（容器）
        ApplicationContext context = new ApplicationContext(AppConfig.class);
        //调用getBean()方法
        UserService userService = (UserService) context.getBean("userService");
        UserService userService1 = (UserService) context.getBean("userService");
        UserService userService2 = (UserService) context.getBean("userService");
        UserService userService3 = (UserService) context.getBean("userService");
        UserService userService4 = (UserService) context.getBean("userService");
        System.out.println(userService);
        System.out.println(userService1);
        System.out.println(userService2);
        System.out.println(userService3);
        System.out.println(userService4);


    }
}
