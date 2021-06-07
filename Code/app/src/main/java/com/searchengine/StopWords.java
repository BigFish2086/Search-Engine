package com.searchengine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class StopWords {

	private String[] defaultStopWords = { "i", "a", "about", "an", "are", "as", "at", "be", "by", "com", "for", "from",
			"how", "in", "is", "it", "of", "on", "or", "that", "the", "this", "to", "was", "what", "when", "where",
			"who", "will", "with" };

	private static HashSet<String> stopWords = new HashSet<>();

	public StopWords() {
		stopWords.addAll(Arrays.asList(defaultStopWords));
	}

	public StopWords(String fileName) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line = null;
			while ((line = br.readLine()) != null) {
				stopWords.add(line);
			}
			br.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void addStopWord(String word) {
		stopWords.add(word);
	}

	public String[] removeStopWords(String[] words) {
		ArrayList<String> tokens = new ArrayList<String>(Arrays.asList(words));
		for (int i = 0; i < tokens.size(); i++) {
			if (stopWords.contains(tokens.get(i))) {
				tokens.remove(i);
			}
		}
		return (String[]) tokens.toArray(new String[tokens.size()]);
	}

	public String removeStopWords(String words) {
		String[] arr = words.split("\\s+|\r");
		StringBuilder sb = new StringBuilder();
		for (String s : arr) {
			if (!stopWords.contains(s) && !s.isEmpty()) {
				sb.append(s.trim()).append(" ");
			}
		}
		return sb.toString();
	}

	public void displayStopWords() {
		for (String stopWord : stopWords) {
			System.out.print("[" + stopWord + "]\n");
		}
	}
}