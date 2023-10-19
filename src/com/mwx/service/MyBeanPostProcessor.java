package com.mwx.service;

import com.mwx.spring.BeanPostprocessor;
import com.mwx.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class MyBeanPostProcessor implements BeanPostprocessor {
    @Override
    public Object postProcessBeforeInitialization(String beanName, Object bean) {
        System.out.println("-------------------------------postProcessBeforeInitialization-------------------------------");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(String beanName, Object bean) {
        System.out.println("-------------------------------postProcessAfterInitialization-------------------------------");
        if (beanName.equals("userService")) {
            Object object = Proxy.newProxyInstance(MyBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("切面逻辑");
                    return method.invoke(bean, args);
                }
            });
            return object;
        }
        return bean;
    }
}
