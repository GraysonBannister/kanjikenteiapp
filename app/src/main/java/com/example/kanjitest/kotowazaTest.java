package com.example.kanjitest;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteException;
//drawing
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;


import android.util.AttributeSet;
import android.util.Log;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Handler;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;


public class kotowazaTest extends Activity {

    //UI interface features
    private TextView questionTextView;
    private EditText answerEditText;
    private Button submitButton;
    private Button exitButton;
    private Button nextQuestionButton;
    private TextView scoreTextView;
    private TextView questionCounterTextView;
    private TextView notificationTextView;
    private yojiTest.drawingView kotowazaDrawCanvas;
    private ImageButton clearDrawingButton;
    private Button submitDrawnKanji;

    //drawing test
    private List<List<List<Float>>> userDrawnKanjiStrokes = new ArrayList<>(4);


    private List<kotowazaQuestion> kotowazaQuestions;
    private QuestionDAO questionDAO;


    //test var
    private int score = 0;
    private int currentkotowazaQuestionIndex = 0;
    private int questionCount;
    private int kotowazaWrongAnswersCount = 0;


    //database var
    private kotowazaDatabaseHelper kotowazaDBHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String testType = getIntent().getStringExtra("test_type");
        questionCount = getIntent().getIntExtra("question_count", 10); //default to 10 if not provided

        super.onCreate(savedInstanceState);
        setContentView(R.layout.kotowaza_testview);


        questionTextView = findViewById(R.id.kotowazaQuestionView);
        submitButton = findViewById(R.id.kotowazaAnswerButton);
        exitButton = findViewById(R.id.kotowazaTestExit);
        nextQuestionButton = findViewById(R.id.kotowazaNext);
        questionCounterTextView = findViewById(R.id.kotowazaTestQuestionCount);
        notificationTextView = findViewById(R.id.kotowazaNotifcation);
        kotowazaDrawCanvas = findViewById(R.id.kotowazaDrawCanvas);
        clearDrawingButton = findViewById(R.id.kotowazaClearDrawingButton);

//database
        kotowazaDBHelper = new kotowazaDatabaseHelper(this);


        //start python
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }

        try {

            kotowazaDBHelper.openDatabase();

        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast.makeText(this, "データベースのアクセスの問題が発生しました。", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //questionDAO = new QuestionDAO(dbHelper.getReadableDatabase());
        if ("kotowazaType".equals(testType)) {
            questionDAO = new QuestionDAO(kotowazaDBHelper.getReadableDatabase());
        } else { // For kotowazaDraw test
            questionDAO = new QuestionDAO(kotowazaDBHelper.getReadableDatabase());

        }
//drawing test drawing list
        for (int i = 0; i < 4; i++) {
            userDrawnKanjiStrokes.add(new ArrayList<>());
        }


        clearDrawingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kotowazaDrawCanvas.clearDrawing();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String testType = getIntent().getStringExtra("test_type");


                String correctAnswer = kotowazaQuestions.get(currentkotowazaQuestionIndex).getAnswer();
                notificationTextView.setText(correctAnswer);
                notificationTextView.setVisibility(View.VISIBLE);
                currentkotowazaQuestionIndex++;


            }


        });


        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String testType = getIntent().getStringExtra("test_type");

                //Fix this for kotowazaType
                if ("kotowazaType".equals(testType)) {
                    currentkotowazaQuestionIndex++;
                    if (currentkotowazaQuestionIndex < kotowazaQuestions.size()) {
                        //checks if the current question number is less the the total questions
                        displayKotowazaQuestion();//if it is less, then display the question
                    } else {
                        finish(); //End of test, display score or return to main menu
                    }
                    //Fix this for kotowazaDraw
                } else if ("kotowazaDraw".equals(testType)) {
                    currentkotowazaQuestionIndex++;
                    if (currentkotowazaQuestionIndex < kotowazaQuestions.size()) { //checks if the current question number is less the the total questions

                        displayKotowazaQuestion();
                        //if it is less, then display the question
                    } else {
                        finish();//End of test, display score or return to main menu
                    }

                }

            }
        });


        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Finish the activity to go back to the previous screen
                finish();
            }
        });

        //Starts Tests
        if ("kotowazaTest".equals(testType)) {
            //start kotowazaType test with 'questionCount' questions
            startKotowazaTest();

        } else {
            finish();
        }

    }




        public void displayKotowazaQuestion () {
            if (!kotowazaQuestions.isEmpty() && currentkotowazaQuestionIndex < kotowazaQuestions.size()) {

                kotowazaQuestion currentQuestion = kotowazaQuestions.get(currentkotowazaQuestionIndex);
                Log.d("TestActivity", "Number of kotowaza Questions" + kotowazaQuestions.size());
                questionTextView.setText(currentQuestion.getKotowaza());
                updateQuestionCounter();
            } else {
                Log.e("TestActivity", "kotowazaQuesitons list is empty or index of bounds");
            }

        }
        public void startKotowazaTest () {


            notificationTextView.setText("");
            kotowazaQuestions = questionDAO.getAllKotowazaEntries();

            Collections.shuffle(kotowazaQuestions); //randomize questions asked

            if (kotowazaQuestions.size() > questionCount) {
                kotowazaQuestions = kotowazaQuestions.subList(0, questionCount);
            }
            currentkotowazaQuestionIndex = 0;
            score = 0;
            displayKotowazaQuestion();//Start kotowaza Draw Test

        }

        private void updateQuestionCounter () {
            String testType = getIntent().getStringExtra("test_type");
            if ("kotowazaType".equals(testType)) {
                //Update question counter view for kotowazaType

                questionCounterTextView.setText("質問 " + (currentkotowazaQuestionIndex + 1) + "/" + questionCount);
            } else if ("kotowazaDraw".equals(testType)) {
                //update question counter view for kotowazaDraw

                questionCounterTextView.setText("質問 " + (currentkotowazaQuestionIndex + 1) + "/" + questionCount);
            } else {
                finish();
            }

        }
    }







