package com.example.kanjitest;

public class kotowazaQuestion {

    private String kotowaza;
    private String answer;


    public kotowazaQuestion(String kotowaza, String answer){
        this.kotowaza = kotowaza;
        this.answer = answer;

    }

    public String getKotowaza(){

        return kotowaza;
    }
    public String getAnswer(){
    return answer;
    }
}
