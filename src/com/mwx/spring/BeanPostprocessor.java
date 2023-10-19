package com.mwx.spring;

public interface BeanPostprocessor {

    /**
     * 可以在 bean 初始化之前进行自定义的处理，比如修改 bean 的属性值，或者执行一些初始化前的逻辑。
     * */
    public Object postProcessBeforeInitialization(String beanName, Object bean);

    /**
     * 在初始化后对 bean 进行一些额外的配置或者增强。
     * */
    public Object postProcessAfterInitialization(String beanName, Object bean);
}
