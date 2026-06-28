package org.yellowflash.zad01;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class PasswordValidator {

    public boolean validate(Object object) throws IllegalAccessException {
        Field[] allFields = object.getClass().getDeclaredFields();
        boolean result = true;
        for (Field f : allFields)
            if(f.isAnnotationPresent(PasswordValidation.class)){
                f.setAccessible(true);
                String password = (String) f.get(object);
                PasswordValidation annotation = f.getAnnotation(PasswordValidation.class);
                Predicate<String> minLen = s -> s != null && s.length() >= annotation.minLength();
                Predicate<String> requireDigit = s -> s.chars().anyMatch(Character::isDigit);
                Predicate<String> requireSpecial = s -> s.chars().anyMatch(c -> !Character.isLetterOrDigit(c));

                Predicate<String> validator = minLen;
                if(annotation.requireDigit()) validator = validator.and(requireDigit);
                if(annotation.requireSpecialChar()) validator = validator.and(requireSpecial);

                result = result && validator.test(password);
            }

                return result;
    }
}
