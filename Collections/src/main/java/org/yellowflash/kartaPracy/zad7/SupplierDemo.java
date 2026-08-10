package org.yellowflash.kartaPracy.zad7;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

public class SupplierDemo {

    public static void main(String[] args) {
        final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        final Random rng = new Random(42);

        Supplier<String> codeSupplier = () -> {
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                sb.append(alphabet.charAt(rng.nextInt(alphabet.length())));
            }
            return sb.toString();
        };
        Supplier<java.util.UUID> codeSupplierUUID = UUID::randomUUID;
        // Wypełnienie tablicy 10 kodów - klasyczna pętla, BEZ Stream.generate
        String[] codes = new String[10];
        for (int i = 0; i < codes.length; i++) {
            codes[i] = codeSupplier.get();
        }
        UUID[] uuids = new UUID[5];
        for (int i = 0; i < uuids.length;i++){
            uuids[i] = codeSupplierUUID.get();
        }
        System.out.println("Wygenerowane kody:");
        for (String c : codes) {
            System.out.println("  " + c);
        }
        for (UUID u : uuids)
            System.out.println(u);
    }
    /*
    Jaki jest typ wyniku codeSupplier.get()?
    Generyczny T,
Po co Supplier, skoro można po prostu wywołać metodę?
Supplier służy do redukowania kodu i generowania różnego rodzaju wyników
Czy Supplier#get rzuca jakieś sprawdzane wyjątki? (Spójrz w dokumentację.)
nie
     */
}
