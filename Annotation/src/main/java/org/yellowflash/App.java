package org.yellowflash;

import org.yellowflash.zad01.PasswordValidator;
import org.yellowflash.zad01.User;
import org.yellowflash.zad02.Pojazd;
import org.yellowflash.zad02.Samochod;
import org.yellowflash.zad02.oliczenie;
import org.yellowflash.zad02Lombok.DigitalProduct;
import org.yellowflash.zad02Lombok.Order;
import org.yellowflash.zad02Lombok.PhysicalProduct;
import org.yellowflash.zad02Lombok.Product;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception  {
        //startregion zad1i2
//        User sam = new User("samuel123");
//        User sam1 = new User("samuelsamuel123");
//        User sam2 = new User("samuel123!samuel");
//        User sam3 = new User("sam123");
//
//        PasswordValidator validator = new PasswordValidator();
//
//        System.out.println(validator.validate(sam));
//        System.out.println(validator.validate(sam1));
//        System.out.println(validator.validate(sam2));
//        System.out.println(validator.validate(sam3));

//        Class<?> pojazd = Class.forName("org.yellowflash.zad02.Pojazd");
//        Class<?> samochod = Class.forName("org.yellowflash.zad02.Samochod");
//        Constructor<?> constructorPojazd = pojazd.getConstructor();
//        Constructor<?> constructorSamochod = samochod.getConstructor();
//        Object pojazdInst = constructorPojazd.newInstance();
//        Object samochInst = constructorSamochod.newInstance();
//
//
//        for (Method method : pojazd.getDeclaredMethods()) {
//            if(method.invoke(pojazdInst) != null)
//            System.out.println(method.invoke(pojazdInst));
//        }
//        for (Method method : samochod.getDeclaredMethods()) {
//            if(method.invoke(samochInst) != null)
//            System.out.println(method.invoke(samochInst));
//        }
//
//        oliczenie mnozenie = (val1,val2) -> val1 * val2;
//        oliczenie dzielenie = (val1,val2) -> val1 / val2;
//
//        List<Integer> list = List.of(1,2,3,4,5,12,312,31,412,4);
//        mnozenie.wykonaj(list.get(0),list.get(2));
//region
        Product cd = DigitalProduct.builder()
                .stock(2)
                .build();
        Product cd1 = DigitalProduct.builder()
                .stock(2)
                .build();
        Product cd2 = PhysicalProduct.builder()
                .stock(2)
                .build();
        Product cd3 = PhysicalProduct.builder()
                .stock(2)
                .build();

        cd.getName();
        cd.getPrice();
        cd.setPrice(2.3);
        System.out.println(cd);
        System.out.println(cd.equals(cd1));
        System.out.println(cd2.equals(cd3));
    }
}
