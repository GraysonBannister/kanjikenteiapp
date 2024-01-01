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
        StringBuilder queryBuilder = new StringBuilder("SELECT definition, reading, word FROM allJukugo WHERE rank IN (");

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
                String definition = cursor.getString(0);

                String reading = cursor.getString(1);
                String word = cursor.getString(2);

                // Add each non-null question to the list
                if (definition != null && word != null && reading != null) {
                    questions.add(new jukugoQuestion(definition, word, reading));
                }else {
                    Log.e("jukugoDAO", "Mismatch in lengths of readings and onyomi for kanji: " + word);
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

    public List<kotowazaQuestion> getAllKotowazaEntries(float[] ranks){
        List<kotowazaQuestion> entries = new ArrayList<>();

        StringBuilder queryBuilder = new StringBuilder("SELECT kotowaza,sentence, question, readingFound, meaning, alternative, similarKotowaza FROM kotowazaData WHERE rank IN (");
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
                String Kotowaza = cursor.getString(0);  // 1st column
                String Sentence = cursor.getString(1);  // 2nd column
                String Question = cursor.getString(2);  // 3rd column
                int ReadingFound = cursor.getInt(3);    // 4th column
                String Meaning = cursor.getString(4);   // 5th column
                String Alternative = cursor.getString(5); // 6th column
                String SimilarKotowaza = cursor.getString(6); // 7th column


                if(Kotowaza != null && Sentence != null && Question != null && ReadingFound == 1 ){
                    // Split the reading and kunyomi strings into arrays

                    // Check if the arrays lengths match to ensure data integrity

                    entries.add(new kotowazaQuestion(Kotowaza, Sentence, Question, Meaning, Alternative, SimilarKotowaza));
                }


            }while(cursor.moveToNext());

        }
        Log.d("kotowazaDAO", "Fetched " + entries.size() + " kotowaza entries.");
        cursor.close();
        return entries;
    }

}

