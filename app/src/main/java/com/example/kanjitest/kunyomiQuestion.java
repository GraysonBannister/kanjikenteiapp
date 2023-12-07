package com.example.kanjitest;

public class kunyomiQuestion {
    private String kunyomi;
    private String reading;
    private int rank;
    private String kanji;


    public kunyomiQuestion(int rank, String kanji, String reading, String kunyomi) {
        this.kunyomi = kunyomi;
        this.reading = reading;
        this.rank = rank;
        this.kanji = kanji;


    }

    public String getKunyomi(){

        return kunyomi;
    }

    public String getReading(){

        return reading;
    }




}
