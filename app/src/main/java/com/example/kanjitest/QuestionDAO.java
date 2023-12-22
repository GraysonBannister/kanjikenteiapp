package com.example.kanjitest;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class QuestionDAO {

    private SQLiteDatabase database;

    public QuestionDAO(SQLiteDatabase database){
        this.database = database;
    }

    public List<jukugoQuestion> getAllJukugoQuestions(float[] ranks){
        List<jukugoQuestion> questions = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT 意味, 言葉, 読み方 FROM allJukugo WHERE rank IN (");

        for (int i = 0; i < ranks.length; i++){
            queryBuilder.append("?");
            if(i < ranks.length - 1){
                queryBuilder.append(", ");
            }
        }

        queryBuilder.append(")");
        String[] rankStrings = new String[ranks.length];
        for (int i = 0; i < ranks.length; i++){
            rankStrings[i] = String.valueOf(ranks[i]);
        }

        Cursor cursor = database.rawQuery(queryBuilder.toString(), rankStrings);
        if (cursor.moveToFirst()) {
            do {
                String 意味 = cursor.getString(0);
                String 言葉 = cursor.getString(1);
                String 読み方 = cursor.getString(2);


                // Add each non-null question to the list
                if (意味 != null && 言葉 != null && 読み方 != null) {
                    questions.add(new jukugoQuestion(意味, 言葉, 読み方));
                }

            } while (cursor.moveToNext());
        }
        Log.d("jukugoDAO" , "Fetched " + questions.size() + " jukugo questions."); //log the amount of questions fetched
        cursor.close();
        return questions;
    }
    public List<yojijukugoQuestions> getAllYojijukugoEntries() {
        List<yojijukugoQuestions> entries = new ArrayList<>();
        String query = "SELECT yojijukugo, reading FROM ikkyuuYojijukugoWithReadings";

        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String yojijukugo = cursor.getString(0);
                String reading = cursor.getString(1);

                // Add each non-null entry to the list
                if (yojijukugo != null && reading != null) {
                    entries.add(new yojijukugoQuestions(yojijukugo, reading));
                }
            } while (cursor.moveToNext());
        }
        Log.d("YojijukugoDAO", "Fetched " + entries.size() + " yojijukugo entries.");
        cursor.close();
        return entries;
    }
    public List<onyomiQuestion> getAllOnyomiQuestions(float[] ranks){
        List<onyomiQuestion> questions = new ArrayList<>();
        StringBuilder queryBuilder = new StringBuilder("SELECT number, kanji, onyomi FROM kanjiOnyomi WHERE rank IN (");

        for (int i = 0; i < ranks.length; i++){
            queryBuilder.append("?");
            if(i < ranks.length - 1){
                queryBuilder.append(", ");
            }
        }

        queryBuilder.append(")");
        String[] rankStrings = new String[ranks.length];
        for (int i = 0; i < ranks.length; i++){
            rankStrings[i] = String.valueOf(ranks[i]);
        }

        Cursor cursor = database.rawQuery(queryBuilder.toString(), rankStrings);

        if (cursor.moveToFirst()) {
            do {
                int number = cursor.getInt(0);
                String kanji = cursor.getString(1);
                String onyomi = cursor.getString(2);

                //add each non null value to the list
                if (kanji != null && onyomi != null){
                    questions.add(new onyomiQuestion(kanji,onyomi));
                }
                else {
                    Log.e("onyomiDAO", "Mismatch in lengths of readings and onyomi for kanji: " + kanji);
                }
            } while (cursor.moveToNext());
        }


        Log.d("QuestionDAO", "Fetched" + questions.size() + " onyomi questions."); //log the amount of questions fetched
        cursor.close();
        return questions;
    }
    public List<kunyomiQuestion> getAllKunyomiEntries(float[] ranks){
        List<kunyomiQuestion> entries = new ArrayList<>();

        StringBuilder queryBuilder = new StringBuilder("SELECT kanji, reading, kunyomi FROM kunyomiData WHERE rank IN (");
        for (int i = 0; i < ranks.length; i++){
            queryBuilder.append("?");
            if(i < ranks.length - 1){
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(")");
        String[] rankStrings = new String[ranks.length];
        for (int i = 0; i < ranks.length; i++){
            rankStrings[i] = String.valueOf(ranks[i]);
        }

        Cursor cursor = database.rawQuery(queryBuilder.toString(), rankStrings);
        if(cursor.moveToFirst()){
            do{
                String kanji = cursor.getString(0);
                String reading = cursor.getString(1);
                String kunyomi = cursor.getString(2);

                if(kanji != null && reading != null && kunyomi != null){
                    // Split the reading and kunyomi strings into arrays
                    String[] readingsArray = reading.split(",");
                    String[] kunyomiArray = kunyomi.split(",");

                    // Check if the arrays lengths match to ensure data integrity
                    if (readingsArray.length == kunyomiArray.length) {
                        entries.add(new kunyomiQuestion(kanji, readingsArray, kunyomiArray));
                    } else {
                        Log.e("KunyomiDAO", "Mismatch in lengths of readings and kunyomi for kanji: " + kanji);
                    }

                    entries.add(new kunyomiQuestion(kanji, readingsArray, kunyomiArray));
                }


            }while(cursor.moveToNext());

        }
        Log.d("KunyomiDAO", "Fetched " + entries.size() + " kunyomi entries.");
        cursor.close();
        return entries;



    }

    public List<kotowazaQuestion> getAllKotowazaEntries(){
        List<kotowazaQuestion> entries =  new ArrayList<>();

        String query = "SELECT kotowaza, answer FROM kotowaza";

        Cursor cursor = database.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                String kotowaza = cursor.getString(0);
                String answer = cursor.getString(1);

                if (kotowaza != null && answer != null){
                    entries.add(new kotowazaQuestion(kotowaza, answer));
                }
            }while(cursor.moveToNext());
        }
        Log.d("KotowazaDOA", "Fetched" + entries.size() + "kotowza entries.");
        cursor.close();
        return entries;
    }

}

