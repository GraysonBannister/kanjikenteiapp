package com.example.kanjitest;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Handler;

import com.google.gson.Gson;

//DataHolder.getInstance().setUserAnswers(userAnswers);


class kotowazaQA {
    String question;
    byte[] userDrawing;
    String answer;
    String meaning;
    String alternative;
    String similar;

    public kotowazaQA(String question, byte[] userDrawing, String answer, String meaning, String alternative, String similar) {
        this.question = question;
        this.userDrawing = userDrawing;
        this.answer = answer;
        this.meaning = meaning;
        this.alternative = alternative;
        this.similar = similar;


    }

    // Add getters here if needed
}
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

    private List<kotowazaQA> userAnswers = new ArrayList<>();



    //drawing test
    private List<List<List<Float>>> userDrawnKanjiStrokes = new ArrayList<>();


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




        try {

            kotowazaDBHelper.openDatabase();

        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast.makeText(this, "データベースのアクセスの問題が発生しました。", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //questionDAO = new QuestionDAO(dbHelper.getReadableDatabase());

            questionDAO = new QuestionDAO(kotowazaDBHelper.getReadableDatabase());

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
                saveAnswer();

                String correctAnswer = kotowazaQuestions.get(currentkotowazaQuestionIndex).getAnswer();
                notificationTextView.setText(correctAnswer);
                notificationTextView.setVisibility(View.VISIBLE);


            }


        });


        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String testType = getIntent().getStringExtra("test_type");
                notificationTextView.setText("");
                kotowazaDrawCanvas.clearDrawing();
                //Fix this for kotowazaType
                if ("kotowazaTest".equals(testType)) {
                    currentkotowazaQuestionIndex++;
                    if (currentkotowazaQuestionIndex < kotowazaQuestions.size()) {
                        //checks if the current question number is less the the total questions
                        displayKotowazaQuestion();//if it is less, then display the question
                    } else if (currentkotowazaQuestionIndex >= kotowazaQuestions.size()) {
                        //change button to display "finish"
                        goToReviewActivity();
                        //End of test, display score or return to main menu
                    }else{
                        finish();
                    }
                    //Fix this for kotowazaDraw
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
        if (testType.equals("kotowazaTest") ) {
            //start kotowazaType test with 'questionCount' questions
           // kotowazaQuestions = 0,100;
            startKotowazaTest();

        } else {
            finish();
        }

    }



    private void goToReviewActivity() {
        kotowazaDataHolder.getInstance().setUserAnswers(userAnswers);

        Intent intent = new Intent(kotowazaTest.this, kotowazaTestReview.class);
        startActivity(intent);
    }
        public void displayKotowazaQuestion () {
            if (!kotowazaQuestions.isEmpty() && currentkotowazaQuestionIndex < kotowazaQuestions.size()) {

                kotowazaQuestion currentQuestion = kotowazaQuestions.get(currentkotowazaQuestionIndex);
                Log.d("TestActivity", "Number of kotowaza Questions" + kotowazaQuestions.size());
                questionTextView.setText(currentQuestion.getQuestion());
                updateQuestionCounter();
            } else {
                Log.e("TestActivity", "kotowazaQuesitons list is empty or index of bounds");
            }

        }
        public void startKotowazaTest () {

            float[] selectedRanks = getIntent().getFloatArrayExtra("selected_ranks"); // Get the selected level passed from the previous activity
            notificationTextView.setText("");
            kotowazaQuestions = questionDAO.getAllKotowazaEntries(selectedRanks); // Use the selected level to filter questions
            Log.d("kotowazaTestSelection", "Selected Rank(s): " + selectedRanks);
            Log.d("TestActivity", "Number of kotowaza Questions " + kotowazaQuestions.size());
            Collections.shuffle(kotowazaQuestions); //randomize questions asked

            if (kotowazaQuestions.size() > questionCount) {
                kotowazaQuestions = kotowazaQuestions.subList(0, questionCount);
            }
            currentkotowazaQuestionIndex = 0;
            score = 0;

            // Initialize userAnswers with placeholders
            userAnswers.clear(); // Clear any previous data
            for (kotowazaQuestion question : kotowazaQuestions) {
                userAnswers.add(new kotowazaQA(question.getQuestion(), null, question.getAnswer(), question.getMeaning(), question.getAlternative(), question.getSimilarKotowaza())); // No answer yet
            }

            displayKotowazaQuestion();//Start kotowaza Draw Test

        }
        //for saving each question and answer
        private void saveAnswer() {
            if (currentkotowazaQuestionIndex >= kotowazaQuestions.size()) {
                return; // Prevent out-of-bounds access
            }

            Bitmap drawing = kotowazaDrawCanvas.getDrawing();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            drawing.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] drawingBytes = baos.toByteArray();

            // Update the answer in userAnswers
            userAnswers.set(currentkotowazaQuestionIndex, new kotowazaQA(
                    kotowazaQuestions.get(currentkotowazaQuestionIndex).getQuestion(),
                    drawingBytes, kotowazaQuestions.get(currentkotowazaQuestionIndex).getAnswer(),kotowazaQuestions.get(currentkotowazaQuestionIndex).getMeaning(),
                    kotowazaQuestions.get(currentkotowazaQuestionIndex).getAlternative(),kotowazaQuestions.get(currentkotowazaQuestionIndex).getSimilarKotowaza()
            ));
        }

        private void updateQuestionCounter () {
            String testType = getIntent().getStringExtra("test_type");
            if ("kotowazaTest".equals(testType)) {
                //Update question counter view for kotowazaType
                questionCounterTextView.setText("質問 " + (currentkotowazaQuestionIndex + 1) + "/" + questionCount);
            } else {
                finish();
            }

        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (kotowazaDBHelper != null) {
            kotowazaDBHelper.close();
        }
    }


    }







