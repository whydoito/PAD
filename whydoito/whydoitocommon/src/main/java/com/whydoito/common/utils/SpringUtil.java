package com.whydoito.common.utils;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.AbstractMessageSource;


public class SpringUtil implements ApplicationContextAware {

    private static final Logger       log = LoggerFactory.getLogger(SpringUtil.class);

    private static ApplicationContext appCtx;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appCtx = applicationContext;
        log.info("ApplicationContext setted to [" + appCtx + "]");
    }

    /**
     * return the associated spring ApplicationContext. available only after
     * this class been declared in applicationContext.xml and after the
     * applicaitonContext.xml been parsed already
     * 
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return appCtx;
    }

    /**
     * return the bean with the given name
     * 
     * @param <T>
     * @param beanName
     * @return null if not exist
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        try {
            return (T) appCtx.getBean(beanName);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    /**
     * return the bean with the given bean class
     * 
     * @param beanClz
     * @return
     */
    public static <T> T getBean(Class<T> beanClz) {
        try {
            return (T) appCtx.getBean(beanClz);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    /**
     * the resources defined in the file declared for bean messageSource
     * 
     * @param key
     * @return null if not exist
     */
    public static String getResource(String key) {
        return getResource(key, null, null);
    }

    /**
     * the resources defined in the file declared for bean messageSource
     * 
     * @param key
     * @return null if not exist
     */
    public static String getResource(String key, Object[] args, Locale locale) {
        try {
            return appCtx.getMessage(key, args, locale);
        } catch (NoSuchMessageException e) {
            return null;
        } catch (IllegalStateException e) {
            AbstractMessageSource ms = (AbstractMessageSource) appCtx.getBean("messageSource");
            try {
                return ms.getMessage(key, args, locale);
            } catch (NoSuchMessageException ex) {
                return null;
            }
        }
    }
}
