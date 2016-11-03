package com.alipay.euler.andfix.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @version V1.0
 * @FileName: com.alipay.euler.andfix.annotation.MethodReplace.java
 * @author: ZhaoHao
 * @date: 2016-11-03 09:56
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodReplace {
    String clazz();

    String method();
}
