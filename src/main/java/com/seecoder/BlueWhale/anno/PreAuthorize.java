package com.seecoder.BlueWhale.anno;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreAuthorize {

				/*
				* 权限表达式
				*/
				public String value() default "";


}
