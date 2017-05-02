package com.whydoito.common.utils;

import java.util.List;

import com.whydoito.common.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;


public class JsonBeanUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonBeanUtil.class);

    public static <T> T jsonString2Bean(String jsonString, Class<T> clazz) throws ServiceException
    {
        try {
            T t = JSON.parseObject(jsonString, clazz);
            return t;
        } catch (Exception e) {
            log.error("transfer json string [" + jsonString + "] to bean failed ", e);
            throw new ServiceException("jsonString2Bean fail", "解析报文出错", e);
        }
    }
    
    public static <T> List<T> jsonString2BeanList(String jsonString, Class<T> clazz) throws ServiceException {
        try {
            List<T> t = JSON.parseArray(jsonString, clazz);
            return t;
        } catch (Exception e) {
            log.error("transfer json string [" + jsonString + "] to bean list failed ", e);
            throw new ServiceException("jsonString2BeanList fail", "解析报文出错", e);
        }
    }
    
    public static String bean2JsonString(Object obj) throws ServiceException {
        try {
            if (null == obj) {
                return "";
            }
            String jsonString = JSON.toJSONString(obj);
            return jsonString;
        } catch (Exception e) {
            log.error("bean2JsonString failed ", e);
            throw new ServiceException("bean2JsonString fail", "解析报文出错", e);
        }
    }
    
}
