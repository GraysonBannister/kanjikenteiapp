package com.example.kanjitest;


import android.content.Intent;
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
    public List<onyomiQuestion> getAllOnyomiQuestions(){
        List<onyomiQuestion> questions = new ArrayList<>();
        String query  = "Select kanji, onyomi FROM KanjiData";

        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String kanji = cursor.getString(0);
                String onyomi = cursor.getString(1);

                //add each non null value to the list
                if (kanji != null && onyomi != null){
                    questions.add(new onyomiQuestion(kanji,onyomi));
                }
            } while (cursor.moveToNext());
        }
        Log.d("QuestionDAO", "Fetched" + questions.size() + " onyomi questions."); //log the amount of questions fetched
        cursor.close();
        return questions;
    }
    public List<jukugoQuestion> getAllJukugoQuestions(){
        List<jukugoQuestion> questions = new ArrayList<>();
        String query  = "Select usedwords1, usedreadings1, usedwords2, usedreadings2, usedwords3, usedreadings3 FROM KanjiData";

        Cursor cursor = database.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String jukugo1 = cursor.getString(0);
                String reading1 = cursor.getString(1);
                String jukugo2 = cursor.getString(2);
                String reading2 = cursor.getString(3);
                String jukugo3 = cursor.getString(4);
                String reading3 = cursor.getString(5);

                // Add each non-null question to the list
                if (jukugo1 != null && reading1 != null) {
                    questions.add(new jukugoQuestion(jukugo1, reading1));
                }
                if (jukugo2 != null && reading2 != null) {
                    questions.add(new jukugoQuestion(jukugo2, reading2));
                }
                if (jukugo3 != null && reading3 != null) {
                    questions.add(new jukugoQuestion(jukugo3, reading3));
                }
            } while (cursor.moveToNext());
        }
        Log.d("QuestionDAO" , "Fetched " + questions.size() + " jukugo questions."); //log the amount of questions fetched
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

    public List<kunyomiQuestion> getAllKunyomiEntries(){
        List<kunyomiQuestion> entries = new ArrayList<>();

        String query = "SELECT rank, kanji, reading, kunyomi FROM kunyomiData";

        Cursor cursor = database.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                int rank = cursor.getInt(0);
                String kanji = cursor.getString(1);
                String reading = cursor.getString(2);
                String kunyomi = cursor.getString(3);

                if(kanji != null && reading != null && kunyomi != null){
                    // Split the reading and kunyomi strings into arrays
                    String[] readingsArray = reading.split(",");
                    String[] kunyomiArray = kunyomi.split(",");

                    // Check if the arrays lengths match to ensure data integrity
                    if (readingsArray.length == kunyomiArray.length) {
                        entries.add(new kunyomiQuestion(rank, kanji, readingsArray, kunyomiArray));
                    } else {
                        Log.e("KunyomiDAO", "Mismatch in lengths of readings and kunyomi for kanji: " + kanji);
                    }

                    entries.add(new kunyomiQuestion(rank, kanji, readingsArray, kunyomiArray));
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

