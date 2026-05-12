package org.yellowflash.zadanie;

import java.util.function.Function;
import java.util.function.Predicate;

public class App {
    public static void main(String[] args) {
        //region zadanie1
        Runnable runnable1 = () -> System.out.println("Startuje Program...");
        Runnable runnable2 = () -> System.out.println("Kończę Program...");

        runnable1.run();
        runnable2.run();
        new Thread(runnable1).start();
        //endregion
        //region  zadanie2
        Function<String,String> toLower = String::toLowerCase;
        Function<String,String> replaceSpace = s ->s.replaceAll(" ","");
        String[] loginy = {"  Adam  ", "ANIA K  ", "  k o w a l ","   lol   "};
        String[] noweLoginy = new String[loginy.length];
        for (int i = 0;i < loginy.length;i++)
            noweLoginy[i] =replaceSpace.andThen(toLower).apply(loginy[i]);
        for (String s : noweLoginy)
            System.out.println(s);
        //endregion

        //region zadanie3

        //endregion
    }
}
