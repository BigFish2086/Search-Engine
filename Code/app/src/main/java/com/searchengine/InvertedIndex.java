package com.searchengine;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import opennlp.tools.stemmer.PorterStemmer;

public class InvertedIndex {

    private static DBHandler DB;
    private static final StopWords stopWords = new StopWords("./src/en_stopwords.txt");

    public static void processDocument(String url) {

        PorterStemmer ps = new PorterStemmer();
        Connection con = Jsoup.connect(url).userAgent(Common.userAgent).referrer(Common.referrer).maxBodySize(0);
        Document doc = null;
        String Text;

        try {
            doc = con.get();
            Text = doc.text();
        } catch (IOException e) {
            return;
        }

        Text = Text.toLowerCase().replaceAll("[^a-zA-Z]", " ");
        Text = stopWords.removeStopWords(Text);

        HashMap<String, Integer> wordFreq = new HashMap<>();
        HashMap<String, String> wordContent = new HashMap<>();

        String[] words = Text.split("\\s+|\r");
        String[] words2 = Text.split("\\s+|\r");

        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].toLowerCase();
            words[i] = ps.stem(words[i]);
            if (wordFreq.containsKey(words[i]))
                wordFreq.put(words[i], wordFreq.get(words[i]) + 1);
            else {
                wordFreq.put(words[i], 1);
                StringBuilder content = new StringBuilder();
                if (i < 10) {
                    int j = 0;
                    for (; j < i; j++)
                        content.append(words2[j]).append(" ");
                    while (j < 20 && j < words.length)
                        content.append(words2[j++]).append(" ");
                } else {
                    int k = 0;
                    int j = i - 10;
                    while (k++ < 20 && j < words.length)
                        content.append(words2[j++]).append(" ");
                }
                wordContent.put(words[i], content.toString());
            }
        }

        for (Map.Entry<String, Integer> entry : wordFreq.entrySet())
            DB.insertIntoIndexed(entry.getKey(), url, entry.getValue() / (float) words.length, wordContent.get(entry.getKey()));


        System.out.println("INDEXED " + url);
    }

    public static void processDirectory(DBHandler db) {
        DB = db;

        while (true) {
            ResultSet rs = DB.getCrawledURL();

            while (true) {
                try {
                    if (rs == null || !rs.next()) break;
                    processDocument(rs.getString("URL"));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
//        System.out.println("Finished indexing");
    }

}
