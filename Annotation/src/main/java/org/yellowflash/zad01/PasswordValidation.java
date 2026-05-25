package org.yellowflash.zad01;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordValidation {

    int minLength() default  8;
    boolean requireDigit() default true;
    boolean requireSpecialChar() default false;

}
