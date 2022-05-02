package com.pisim.nsp;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 用于获取Spring bean对象以及上下文
 * 特别提醒、特别提醒、特别提醒：该类一定要、一定要放在与使用该类的文件的同级目录或者子目录下
 *
 */
@Component
public class SpringBootUtil implements ApplicationContextAware {

    private static ApplicationContext mApplicationContext = null;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringBootUtil.mApplicationContext == null){
            SpringBootUtil.mApplicationContext  = applicationContext;
        }

        System.out.println("setApplicationContext  mApplicationContext:   "+mApplicationContext);
    }

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        System.out.println("getApplicationContext  mApplicationContext:   "+mApplicationContext);
        return mApplicationContext;
    }

    //通过name获取 Bean.
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);

    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }

}

