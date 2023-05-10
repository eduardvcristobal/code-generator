package com.cict.core.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MetaSecurityModule {

    public String key() default "";

    public String basePath() default "";

    public String description() default "";

    public String groups() default "";

}
