package com.mwx.service;

import com.mwx.spring.*;

@Component
@Scope("prototype")
public class UserService implements BeanNameAware, InitializingBean, UserInterface {

    /**
     * 依赖注入
     */
    @Autowired
    private ProductService productService;

    private String beanName;

    private String xxx;

    public void test() {
        System.out.println(productService);
        System.out.println(beanName);
    }

    public void xxx() {

    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("------------------做一些初始化方法------------------");
    }
}
