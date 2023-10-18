package com.mwx.spring;

/**
 * Bean的定义
 */
public class BeanDefinition {

    private Class type;

    private String scope;

    public BeanDefinition(Class type, String scope) {
        this.type = type;
        this.scope = scope;
    }

    public BeanDefinition() {
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
