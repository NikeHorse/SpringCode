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
        userService.test();

    }
}
