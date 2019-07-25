package com.msd.bdp.csvDbIntegrity;

import java.util.StringJoiner;
import java.util.function.Function;

class FunctionProjectList {


    static Function<String[], String> get(String project) {
        switch (project.toLowerCase()) {
        		case "firefly":
        			return FunctionProjectList::getFirefly;
        		case "artwork":
        			return FunctionProjectList::getArtwork;
        		case "artwork2":
        			return FunctionProjectList::getArtwork2;	
        		default:
        			return FunctionProjectList::getDefault;
        }
    }

    private static String getDefault(String[] rec) {
        StringJoiner s = new StringJoiner("<:>");
        for (String e : rec) {
            e = e.replaceAll("\"", "").trim();
            s.add(e.isEmpty() ? "null" : e);
        }
        return String.format(">>%s<<", s.toString());
    }

    private static String getFirefly(String[] rec) {
        StringJoiner s = new StringJoiner("<:>");
        for (String e : rec) {
            e = e.replaceAll("\"", "").trim();
            s.add(e.isEmpty() ? "" : e);
        }
        return String.format(">>%s<<", s.toString());
    }

    private static String getArtwork(String[] rec) {
        StringJoiner s = new StringJoiner("<:>");
        for (String e : rec) {
            e = e.trim();
            s.add(e.isEmpty() ? "null" : e);
        }
        return String.format(">>%s<<", s.toString());
    }
    
    private static String getArtwork2(String[] rec) {
        StringJoiner s = new StringJoiner("<:>");
        for (String e : rec) {
            e = e.trim();
            s.add(e.isEmpty() ? "" : e);
        }
        return String.format(">>%s<<", s.toString());
    }
    
    private FunctionProjectList() {
        throw new IllegalStateException("FunctionProjectList class");
    }
}
