package org.example;

import java.util.*;

public class Main {

    public static int lenght = 100;
    public static String letters = "RLRFR";
    public static int numberOfThreads = 10;
    public static String R = "R";
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) {
        while (numberOfThreads > 0) {
            new Thread(() -> {
                String str = generateRoute(letters, lenght);
                int key = countKey(str, R); //ключ-количество раз буквы R
                synchronized (sizeToFreq) {

                    if (sizeToFreq.containsKey(key)) {
                        sizeToFreq.put(key, sizeToFreq.get(key) + 1);
                    } else {
                        sizeToFreq.put(key, 1);
                    }
                    sizeToFreq.notify();
                }

            }).start();
            numberOfThreads--;
        }


        MyThread myThread = new MyThread();
        myThread.start();
        myThread.interrupt();


//
//        System.out.println("Самое частое количество повторений " + maxKey(sizeToFreq) +
//                " (встретилось " + maxKeyValue(sizeToFreq) + " раз)");

        System.out.println("Другие размеры:");
        sizeToFreq.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(x -> System.out.println("- " + x.getKey() + " ( " + x.getValue() + " раз)"));
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }

    public static int countKey(String str, String target) {      //метод для подсчета частоты буквы R
        return str.length() - str.replace(target, "").length();
    }

    public static int maxKey(Map<Integer, Integer> map) {
        return Collections.max(map.entrySet(), Comparator.comparingInt(Map.Entry::getKey)).getKey();
    }

    public static int maxKeyValue(Map<Integer, Integer> map) {
        return Collections.max(map.entrySet(), Comparator.comparingInt(Map.Entry::getKey)).getValue();
    }

    public static class MyThread extends Thread {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println("Самое частое количество повторений " + maxKey(sizeToFreq) +
                        " (встретилось " + maxKeyValue(sizeToFreq) + " раз)");
            }
        }
    }
}