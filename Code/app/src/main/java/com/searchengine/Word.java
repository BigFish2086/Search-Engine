package com.searchengine;

import java.util.*;

public class Word {
	private class Position {
	    private int sentence;
	    private int position;

	    public Position(int sentence, int position) {
	        this.sentence = sentence;
	        this.position = position;
	    }
	    
	    public int getSent() {
	    	return this.sentence;
	    }
	    
	    public int getPos() {
	    	return this.position;
	    }
	}
	
	private String word;
	private HashMap<String, ArrayList<Position>> map;
    
    public Word(String word) {
    	this.word = word;
    	this.map = new HashMap<>();
    }

    public void addToMap(String url, int sent, int pos) {
    	ArrayList<Position> updated;
    	if(map.containsKey(url)) {
    		updated = map.remove(url);
    	} else {
    		updated = new ArrayList<>();
    	}
    	updated.add(new Position(sent, pos));
    	map.put(url, updated);
    }
    
    public String getWord() {
        return word;
    }
    
    public void printUrls() {
    	Iterator it = map.entrySet().iterator();
    	while(it.hasNext()) {
    		Map.Entry<String, ArrayList<Position>> pair = (Map.Entry<String, ArrayList<Position>>) it.next();
    		Common.print("[+] %s", pair.getKey());
    	}
    	
    }
    
    public void printWordInfo() {
    	Common.print("Word is %s", this.word);
    	Iterator it = map.entrySet().iterator();
    	while(it.hasNext()) {
    		Map.Entry<String, ArrayList<Position>> pair = (Map.Entry<String, ArrayList<Position>>) it.next();
    		Common.print("[+] Found @ %s", pair.getKey());
    		for(Position p: pair.getValue()) {
    			Common.print("\tat line# %d at position# %d", p.getSent(), p.getPos());
    		}
    	}
    }
}










