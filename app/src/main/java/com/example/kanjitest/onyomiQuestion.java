package com.example.kanjitest;

public class onyomiQuestion {

    private String kanji;
    private String onyomi;

    public onyomiQuestion(String kanji, String onyomi) {

        this.kanji = kanji;
        this.onyomi = onyomi;
        
    }

    public String getKanji(){return  kanji;}
    public String getOnyomi(){return onyomi;}


}
