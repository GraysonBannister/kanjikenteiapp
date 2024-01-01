package com.example.kanjitest;

import java.util.List;

public class kotowazaDataHolder {
    private static final kotowazaDataHolder instance = new kotowazaDataHolder();
    private List<kotowazaQA> userAnswers;

    private kotowazaDataHolder() {}

    public static kotowazaDataHolder getInstance() {
        return instance;
    }

    public void setUserAnswers(List<kotowazaQA> userAnswers) {
        this.userAnswers = userAnswers;
    }

    public List<kotowazaQA> getUserAnswers() {
        return userAnswers;
    }



}
