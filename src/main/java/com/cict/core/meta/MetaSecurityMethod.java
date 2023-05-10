package com.cict.core.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MetaSecurityMethod {

    public String name() default "";

    public String action() default "";

    public String value() default "";

    public String description() default "";

    public String groups() default "";

}
