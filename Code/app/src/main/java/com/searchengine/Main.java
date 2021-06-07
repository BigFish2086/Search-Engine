package com.searchengine;


import java.util.Scanner;

public class Main {

    static DBHandler DB;

    public static void main(String[] args) {
        DB = new DBHandler();
        startCrawling(DB);
        InvertedIndex.processDirectory(DB);
        DB.finish();

    }

    public static void startCrawling(DBHandler db) {
        int n = getNumThreads();
        String[] start_urls = {
                "https://codeforces.com/",
                "https://www.programiz.com/",
                "https://www.wchools.com/",
                "https://alphacoders.com/",
                "https://www.mathway.com/",
                "https://shahid.mbc.net/",
                "https://crackwatch.com/"
        };

        Crawler bot = new Crawler(start_urls, db);

        Thread[] threads = new Thread[n];

        for (int i = 0; i < n; i++) {
            threads[i] = new Thread(bot);
            threads[i].setName(Integer.toString(i));
        }


        long before = System.currentTimeMillis();
        for (int i = 0; i < n; i++)
            threads[i].start();
        long after = System.currentTimeMillis();

//        System.out.println("Time  = " + (after - before) / 60 + " Sec = " + (after - before) / 60000 + " min");
    }

    public static int getNumThreads() {
        System.out.println("Enter the Number of Threads you Want");

        int n = -1;
        while (n <= 0)
            try {
                n = new Scanner(System.in).nextInt();
                if (n <= 0)
                    System.out.println("Please Enter a Valid Number");
            } catch (Exception e) {
                System.out.println("Please Enter a Number");
                n = -1;
            }
        return n;
    }
}


