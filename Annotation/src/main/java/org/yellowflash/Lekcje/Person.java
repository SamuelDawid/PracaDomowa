package org.yellowflash.Lekcje;

public class Person {
    private String name;
    private int age;

    public Person() {
        this.name = "Jan";
        this.age = 30;
    }

    public void sayHello() {
        System.out.println("Cześć, jestem " + name + ", mam " + age + " lat.");
    }
}