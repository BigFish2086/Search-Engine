package com.searchengine;

import java.sql.Timestamp;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Common {
	public static int urlsSize = 0;
	public static final int maxThreadsCount = 3;
	public static final int maxNumPages = 5000;
	public static final String referrer = "https://www.google.com/";
	public static final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.2 (KHTML, like Gecko) Chrome/15.0.874.120 Safari/535.2";
	
	public static Timestamp getTimestamp() {
		java.util.Date date = new java.util.Date();
		return new Timestamp(date.getTime());
	}

	public static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

	public static String getName(String _url) {
		String pattern = "^(https|http)://(.*?)/$";
		Pattern pat = Pattern.compile(pattern);
		Matcher mat = pat.matcher(_url);
		if(mat.find()) {
			return mat.group(2) + ".txt";
		}else {
			return "temp" + Integer.toString(new Random().nextInt()) + ".txt";
		}
	}

}
