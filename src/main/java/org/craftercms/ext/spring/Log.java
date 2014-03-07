package org.craftercms.ext.spring;

import java.lang.annotation.*;

/** Custom @Logger annotation **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Log { }