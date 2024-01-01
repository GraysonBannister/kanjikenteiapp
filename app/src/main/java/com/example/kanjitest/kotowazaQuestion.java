package com.example.kanjitest;

public class kotowazaQuestion {

    private String Kotowaza;
    private String Question;
    private String Sentence;
    private String Meaning;
    private String Alternative;
    private String SimilarKotowaza;

    public kotowazaQuestion(String Kotowaza, String Sentence,String Question, String Meaning,String Alternative ,String SimilarKotowaza ){
        this.Kotowaza = Kotowaza;
        this.Sentence = Sentence;
        this.Question = Question;
        this.Meaning = Meaning;
        this.Alternative = Alternative;
        this.SimilarKotowaza = SimilarKotowaza;

    }

    public String getAnswer(){return Kotowaza;}
    public String getQuestion(){
    return Question;
    }
    public String getSentence(){return Sentence;}
    public String getMeaning(){return Meaning;}
    public String getAlternative(){return Alternative;}
    public String getSimilarKotowaza(){return SimilarKotowaza;}



}
