package com.derbysoft.dhp.fileserver.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author neo.fei {neocxf@gmail.com}
 * @version 2/24/17
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@Import(PdfConverterConfig.class)
public @interface EnablePdfConverter {
}
