package org.yellowflash.kartaPracy.zad3;

import java.util.function.Predicate;

public class PredicateDemo {

    public static void main(String[] args) {
        // Trzy małe predykaty
        Predicate<String> minLen3      = s -> s != null && s.length() >= 3;
        Predicate<String> onlyAsciiAlnum = s -> s.matches("[A-Za-z0-9_]+");
        Predicate<String> startsWithLetter = s -> !s.isEmpty() && Character.isLetter(s.charAt(0));

        // Łączymy: AND - wszystkie warunki muszą być spełnione
        Predicate<String> isValidLogin = minLen3.and(onlyAsciiAlnum).and(startsWithLetter);

        String[] loginy = {"adam", "Ala123", "x", "User_01", "ADMIN", "gość", "1234",null};

        int valid = 0;
        System.out.println("Poprawne loginy:");
        for (String login : loginy) {
            if (isValidLogin.test(login)) {
                valid++;
                System.out.println("  - " + login);
            }
        }
        System.out.println("\nPoprawnych: " + valid + " z " + loginy.length);

        // Użycie negate
        Predicate<String> isInvalid = isValidLogin.negate();
        System.out.println("\nNiepoprawne (przez negate):");
        for (String login : loginy) {
            if (isInvalid.test(login)) {
                System.out.println("  - " + login);
            }
        }
        Predicate<String> p = s -> s.length() > 3;

        Predicate<String> negP = p.negate();    // ← tworzymy zaprzeczenie

        System.out.println("p.test('hello')    = " + p.test("hello"));      // długość 5 > 3, więc?
        System.out.println("p.test('ab')       = " + p.test("ab"));         // długość 2 > 3, więc?
        System.out.println("negP.test('hello') = " + negP.test("hello"));
        System.out.println("negP.test('ab')    = " + negP.test("ab"));
        System.out.println("p == negP          = " + (p == negP));          // ten sam obiekt?
    }
    /*
    Jaka jest sygnatura metody Predicate#test?
     Jedna metoda, jeden argument, zwraca boolean.
Co zwróci p.and(q).test(x), jeśli p.test(x) to false? (Pytanie o krótkie spięcie.)
Zwroci false bo waunek && musi byc spelniony
Czy p.negate() modyfikuje p?
nie modyfikuje, tylko tworzy nowy obiekt który sprawdza odwrotność pierwszego
     */
}
