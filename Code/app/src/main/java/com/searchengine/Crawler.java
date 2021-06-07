package com.searchengine;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Crawler implements Runnable {
    DBHandler DB;
    private static int index = 0;
    private static ArrayList<String> pending = new ArrayList<>();


    public Crawler(String[] start_url, DBHandler db) {
        DB = db;
        for (String s : start_url) {
            DB.insertIntoToBeCrawled(s);
        }
        pending = fillPending();
    }

    @Override
    public void run() {
        this.start();
    }

    public void start() {
        crawl();
    }

    private ArrayList<String> fillPending() {
        ResultSet rs = null;
        int i = 10;
        // try 10 times before leaving
        while (rs == null && i != 0) {
            rs = DB.getPendingURLs();
            i--;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Thread " + Thread.currentThread().getName() + " Exited");
                return null;
            }
        }

        ArrayList<String> newPending = new ArrayList<>();
        while (rs != null)
            try {
                if (!rs.next()) break;
                newPending.add(rs.getString(1));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        return (newPending.size() != 0) ? newPending : null;
    }

    private synchronized String getPendingURL() {

        // We need to fetch new data
        if (pending == null || index == pending.size()) {
            index = 0;
            pending = fillPending();
            if (pending == null)
                return null;
        }
        return pending.get(index++);
    }

    private void crawl() {
        while (DB.getNumberOfCrawledPages() < 5000) {
            String url = getPendingURL();
            if (url == null)
                break;
            else {

                Connection con = Jsoup.connect(url).userAgent(Common.userAgent).referrer(Common.referrer).maxBodySize(0);
                Document doc = null;
                try {
                    doc = con.get();
                } catch (IOException e) {
                    // Cannot reach to this url
                    DB.deleteIntoToBeCrawled(url);
                    continue;
                }

                // the url is Crawled before
                if (DB.insertIntoCrawled(url, doc.title()) == 0)
                    continue;

                robot(url);

                Elements elements = doc.select("a");
                for (Element e : elements) {
                    String href = e.attr("abs:href");

                    if (href != null && !href.contains("#") && con.response().statusCode() == 200)
                        DB.insertIntoToBeCrawled(href);
                }

                System.out.println("Thread -" + Thread.currentThread().getName() + " Crawled " + url + ", Size  = " + DB.getNumberOfCrawledPages());

            }
        }
        System.out.println("Finished, size = " + DB.getNumberOfCrawledPages());
    }

    private String processLink(String link, String base) {

        try {
            URL u = new URL(base);
            if (link.startsWith("/")) {
                link = u.getProtocol() + "://" + u.getAuthority() + stripFilename(u.getPath()) + link;
            } else if (link.startsWith("./")) {
                link = link.substring(2);
                link = u.getProtocol() + "://" + u.getAuthority() + stripFilename(u.getPath()) + link;
            } else if (link.startsWith("javascript:") || link.contains("#"))
                link = null;
            else if (link.startsWith("../") || (!link.startsWith("http://") && !link.startsWith("https://")))
                link = u.getProtocol() + "://" + u.getAuthority() + stripFilename(u.getPath()) + link;

            if (link != null && link.endsWith("/"))
                link = link.substring(0, link.length() - 1);

            return link;
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }

    }

    private String stripFilename(String path) {
        int pos = path.lastIndexOf("/");
        return pos <= -1 ? path : path.substring(0, pos);
    }

    private void robot(String url) {
        try {
            URL u = new URL(url);
            url = u.getProtocol() + "://" + u.getAuthority();
            String new_url = url + "/robots.txt";


            // if zero rows was effected when trying to insert the url
            // i.e. it was there before
            if (DB.insertIntoDoneRobot(url) == 0)
                return;

            Connection con = Jsoup.connect(new_url);
            Document doc = con.get();

            String[] content = doc.text().split(" ");
            for (int i = 0; i < content.length; i++) {
                if (content[i].equals("User-agent:") && content[i + 1].equals("*")) {
                    int j = i + 2;
                    while (j < content.length && !content[j].equals("User-agent:")) {
                        if (content[j].equals("Disallow:"))
                            DB.insertIntoBlockedURLS(processLink(content[++j], url));
                        j++;
                    }

                }
            }

        } catch (Exception e) {
//            e.printStackTrace();
        }
    }
}