package com.example.kanjitest;

public class jukugoQuestion {

    private String definition;
    private String jukugo;
    private String reading;


    //Constructor, getters, and setters
    public jukugoQuestion(String definition, String jukugo, String reading) {
        this.definition = definition;
        this.jukugo = jukugo;
        this.reading = reading;

    }

   public String getJukugo(){
        return jukugo;
   }
   public String getReading(){
        return reading;
   }
   public String getDefinition(){return definition; }

}
