package com.example.kanjitest;

public class jukugoQuestion {

    private String jukugo;
    private String reading;


    //Constructor, getters, and setters
    public jukugoQuestion(String jukugo, String reading) {
        this.jukugo = jukugo;
        this.reading = reading;

    }

   public String getJukugo(){
        return jukugo;
   }

   public String getReading(){
        return reading;
   }

}
