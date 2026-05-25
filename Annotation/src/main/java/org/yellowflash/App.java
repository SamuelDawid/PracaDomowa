package org.yellowflash;

import org.yellowflash.zad01.PasswordValidator;
import org.yellowflash.zad01.User;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IllegalAccessException {
        User sam = new User("samuel123");
        User sam1 = new User("samuelsamuel123");
        User sam2 = new User("samuel123!samuel");
        User sam3 = new User("sam123");

        PasswordValidator validator = new PasswordValidator();

        System.out.println(validator.validate(sam));
        System.out.println(validator.validate(sam1));
        System.out.println(validator.validate(sam2));
        System.out.println(validator.validate(sam3));
    }
}
