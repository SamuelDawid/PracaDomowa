import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectionExample {
    public static void main(String[] args) throws Exception {
        // 1. Pobieramy obiekt Class na podstawie nazwy klasy
        Class<?> clazz = Class.forName("Person");

        // 2. Tworzymy obiekt przez refleksję (używamy konstruktora bezargumentowego)
        Constructor<?> constructor = clazz.getConstructor();
        Object person = constructor.newInstance();

        // 3. Wyświetlamy wszystkie metody
        System.out.println("Metody klasy Person:");
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println(" - " + method.getName());
        }

        // 4. Wywołujemy metodę sayHello przez refleksję
        Method sayHello = clazz.getMethod("sayHello");
        sayHello.invoke(person);

        // 5. Zmieniamy prywatne pole name
        Field nameField = clazz.getDeclaredField("name");
        nameField.setAccessible(true); // omijamy prywatność
        nameField.set(person, "Kacper");

        // 6. Wywołujemy ponownie metodę
        sayHello.invoke(person);
    }
}