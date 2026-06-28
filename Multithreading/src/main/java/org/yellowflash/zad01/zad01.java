package org.yellowflash.zad01;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class zad01 extends Thread{
    String name;
    zad01(String name){
        super(name);
        this.name = name;
    }
    public void run() {
        System.out.println(this.getName());
        for (int i = 1; i < 11; i++) {
            System.out.println(i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(this.getName() + " finished his job");
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        System.out.println("How many new Threads??");
        String output = sc.nextLine();
        List<Thread> threadList = new ArrayList<>();
        for (int i = 0; i < Integer.parseInt(output); i++) {
                String name = "Watek-" + (i +1);
                threadList.add(new zad01(name));
                threadList.get(i).start();
        }
        for(Thread t : threadList)
            t.join();
        }
    }


