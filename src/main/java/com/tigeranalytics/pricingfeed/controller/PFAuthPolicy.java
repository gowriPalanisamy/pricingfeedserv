package com.tigeranalytics.pricingfeed.controller;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PFAuthPolicy {
    String[] roles() default "";
}
