package com.mwx.service;

import com.mwx.spring.Autowired;
import com.mwx.spring.Component;
import com.mwx.spring.Scope;

@Component
@Scope("prototype")
public class UserService {

    /**
     * 依赖注入
     */
    @Autowired
    private ProductService productService;

    public void test() {
        System.out.println(productService);
    }
}
