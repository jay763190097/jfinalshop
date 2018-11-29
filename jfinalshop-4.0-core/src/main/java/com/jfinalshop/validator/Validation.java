package com.jfinalshop.validator;
import java.lang.annotation.*;

/**
 * 定义Validator规则的注解
 * @author lianghao
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Validation {
	String rules ();
}