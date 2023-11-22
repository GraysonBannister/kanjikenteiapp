package com.example.kanjitest;

public class yojijukugoQuestions {

    private String yojijukugo;
    private String reading;


    //Constructor, getters, and setters
    public yojijukugoQuestions(String yojijukugo, String reading) {
        this.yojijukugo = yojijukugo;
        this.reading = reading;

    }

    public String getYojijukugo(){
        return yojijukugo;
    }

    public String getReading(){
        return reading;
    }

}
