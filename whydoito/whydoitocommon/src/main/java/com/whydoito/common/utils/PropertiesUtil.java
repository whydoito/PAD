package com.whydoito.common.utils;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.util.StringValueResolver;


public class PropertiesUtil implements EmbeddedValueResolverAware {

    private static StringValueResolver resolver;

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        resolver = stringValueResolver;
    }

    public static String getProperty(String name) {
        return resolver.resolveStringValue("${" + name + "}");
    }

    public static String getProperty(String name, String defaultValue) {
        String s = getProperty(name);
        return (s == null || (s = s.trim()).isEmpty()) ? defaultValue : s;
    }

}
