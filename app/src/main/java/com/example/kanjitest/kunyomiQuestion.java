package com.example.kanjitest;

import android.util.Pair;

import java.util.Random;

public class kunyomiQuestion {
    private String[] kunyomi;
    private String[] reading;

    private String kanji;


    public kunyomiQuestion(String kanji, String[] reading, String[] kunyomi) {
        this.kunyomi = kunyomi;
        this.reading = reading;
        this.kanji = kanji;


    }

    public String[] getKunyomi(){

        return kunyomi;
    }

    public String[] getReading(){

        return reading;
    }

    public String getKanji(){
        return kanji;
    }

    public Pair<String, String> getRandomKunyomiAndReading(){
        int index = new Random().nextInt(kunyomi.length);
        return new Pair<>(kunyomi[index], reading[index]);
    }



}