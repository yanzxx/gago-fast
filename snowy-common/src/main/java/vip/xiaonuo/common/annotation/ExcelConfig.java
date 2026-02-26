package vip.xiaonuo.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义导出Excel数据注解
 *
 * @author ruoyi
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelConfig {



    /**
     * 导出到Excel中的名字.
     */
    String name() default "";



}