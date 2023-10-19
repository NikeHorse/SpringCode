package com.mwx.spring;

public interface InitializingBean {

    /**
     * 可以在 afterPropertiesSet 方法中进行一些需要在 bean 属性设置后才能进行的逻辑，比如建立连接、初始化资源等。
     * */
    void afterPropertiesSet();
}
