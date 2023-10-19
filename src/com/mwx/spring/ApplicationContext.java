package com.mwx.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义的上下文容器
 *
 * @author MWX
 */
public class ApplicationContext {

    private Class configClass;

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    /**
     * 单例池
     */
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>();

    private ArrayList<BeanPostprocessor> beanPostProcessorList = new ArrayList<>();

    /**
     * 加载上下文时 扫描＋注册单例/多例Bean
     */
    public ApplicationContext(Class configClass) {
        this.configClass = configClass;
        //扫描
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            //获取注解扫描路径
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            String path = componentScanAnnotation.value();
            //获取相对路径
            path = path.replace(".", "/");
            //扫描 {path} 路径下编译后的反编译文件
            ClassLoader classLoader = ApplicationContext.class.getClassLoader();
            //获取相对路径下的所有资源
            URL resource = classLoader.getResource(path);
            File file = new File(resource.getFile());
            if (file.isDirectory()) {
                //如果此路径资源是文件夹
                File[] files = file.listFiles();
                for (File f : files) {
                    //获取文件的绝对路径
                    String absolutePath = f.getAbsolutePath();
                    //判断文件是否以 {.class}结尾
                    if (absolutePath.endsWith(".class")) {
                        //判断这个类是否是bean对象 思路：使用反射判断
                        String fileName = (path + "." + f.getName().replace(".class", "")).replace("/", ".");
                        try {
                            Class<?> aClass = classLoader.loadClass(fileName);
                            if (aClass.isAnnotationPresent(Component.class)) {
                                //类是否实现了BeanPostProcessor
                                if (BeanPostprocessor.class.isAssignableFrom(aClass)) {
                                    BeanPostprocessor beanPostprocessor = (BeanPostprocessor) aClass.newInstance();
                                    beanPostProcessorList.add(beanPostprocessor);
                                }
                                //有Component注解
                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(aClass);
                                if (aClass.isAnnotationPresent(Scope.class)) {
                                    String scope = aClass.getAnnotation(Scope.class).value();
                                    beanDefinition.setScope("".equals(scope) ? "singleton" : scope);
                                } else {
                                    //单例
                                    beanDefinition.setScope("singleton");
                                }
                                beanDefinitionMap.put(aClass.getAnnotation(Component.class).value().equals("") ?
                                        Introspector.decapitalize(aClass.getSimpleName()) : aClass.getAnnotation(Component.class).value(), beanDefinition)
                                ;
                            }
                        } catch (Exception e) {
                            System.out.println("初始化bean失败了！");
                        }
                    }
                }
            }
        }
        //扫描完后 单例bean处理
        beanDefinitionMap.keySet().stream().forEach(key -> {
            BeanDefinition beanDefinition = beanDefinitionMap.get(key);
            if (beanDefinition.getScope().equals("singleton")) {
                Object bean = createBean(key, beanDefinition);
                singletonObjects.put(key, bean);
            } else {

            }
        });
    }

    /**
     * 创建单例Bean对象
     *
     * <p color=red>模仿bean的生命周期</p>
     */
    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getType();
        try {
            Object instance = clazz.getConstructor().newInstance();
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    declaredField.setAccessible(true);
                    declaredField.set(instance, getBean(declaredField.getName()));
                }
            }
            //实现Aware回调
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }
            //初始化前 AOP BeanPostProcessor
            //可以在 bean 初始化之前进行自定义的处理，比如修改 bean 的属性值，或者执行一些初始化前的逻辑。
            for (BeanPostprocessor beanPostprocessor : beanPostProcessorList) {
                beanPostprocessor.postProcessBeforeInitialization(beanName, instance);
            }
            //初始化
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }
            //初始化后 AOP BeanPostProcessor
            //在初始化后对 bean 进行一些额外的配置或者增强。
            for (BeanPostprocessor beanPostprocessor : beanPostProcessorList) {
                beanPostprocessor.postProcessAfterInitialization(beanName, instance);
            }
            return instance;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取Bean
     */
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (Objects.isNull(beanDefinition)) {
            throw new NoSuchElementException();
        } else {
            String scope = beanDefinition.getScope();
            if (scope.equals("singleton")) {
                //单例操作
                Object o = singletonObjects.get(beanName);
                if (o == null) {
                    o = createBean(beanName, beanDefinition);
                    singletonObjects.put(beanName, o);
                }
                return o;
            } else if (scope.equals("prototype")) {
                //多例操作
                return createBean(beanName, beanDefinition);
            } else {
                //暂未定义 按照单例处理
                Object o = singletonObjects.get(beanName);
                if (o == null) {
                    o = createBean(beanName, beanDefinition);
                    singletonObjects.put(beanName, o);
                }
                return o;
            }
        }
    }
}
