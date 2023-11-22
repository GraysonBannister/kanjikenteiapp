package com.example.kanjitest;



import java.util.List;
import java.util.Collections;

import android.app.Activity;
import android.database.sqlite.SQLiteException;
//drawing


import android.util.Log;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Handler;





public class Test extends Activity {
    //UI interface features
    private TextView questionTextView;
    private EditText answerEditText;
    private Button submitButton;
    private Button exitButton;
    private Button nextQuestionButton;
    private TextView scoreTextView;
    private TextView questionCounterTextView;
    private TextView notificationTextView;


    private List<onyomiQuestion> onyomiQuestions;
    private List<jukugoQuestion> jukugoQuestions;
    private List<yojijukugoQuestions> yojijukugoQuestions;
    private QuestionDAO questionDAO;


    //test var
    private int score = 0;
    private int currentOnyomiQuestionIndex = 0;
    private int currentJukugoQuestionIndex = 0;
    private int currentyojijukugoQuestionIndex = 0;
    private int questionCount;
    private int onyomiWrongAnswersCount = 0;
    private int jukugoWrongAnswersCount = 0;
    private int yojijukugoWrongAnswersCount = 0;


    //database var
    private DatabaseHelper dbHelper;
    private YojijukugoDatabaseHelper yojijukugoDBHelper;


    //other code here


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String testType = getIntent().getStringExtra("test_type");
        questionCount = getIntent().getIntExtra("question_count", 10); //default to 10 if not provided

        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_question);


        questionTextView = findViewById(R.id.jukugoTypeQuestionView);
        submitButton = findViewById(R.id.jukugoAnswerButton);
        exitButton = findViewById(R.id.jukugoTestExit);
        nextQuestionButton = findViewById(R.id.nextButon);
        answerEditText = findViewById(R.id.jukugoTypeInput);
        questionCounterTextView = findViewById(R.id.jukugoTestQuestionCount);
        notificationTextView = findViewById(R.id.notificationTextView);



        dbHelper = new DatabaseHelper(this);
        yojijukugoDBHelper = new YojijukugoDatabaseHelper(this);
        try {
            dbHelper.openDatabase();
            yojijukugoDBHelper.openDatabase();

        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast.makeText(this, "データベースのアクセスの問題が発生しました。", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //questionDAO = new QuestionDAO(dbHelper.getReadableDatabase());
        if ("onyomi".equals(testType) || "jukugo".equals(testType)) {
            questionDAO = new QuestionDAO(dbHelper.getReadableDatabase());
        } else { // For yojijukugo test
            questionDAO = new QuestionDAO(yojijukugoDBHelper.getReadableDatabase());
        }


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAnswer = answerEditText.getText().toString().trim();
                String testType = getIntent().getStringExtra("test_type");

                if ("onyomi".equals(testType)) {
                    checkOnyomiAnswer(userAnswer);
                } else if ("jukugo".equals(testType)) {
                    checkJukugoAnswer(userAnswer);
                }else {
                    checkyojijukugoAnswer(userAnswer);
                }

            }
        });

        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String testType = getIntent().getStringExtra("test_type");

                if ("onyomi".equals(testType)) {
                    currentOnyomiQuestionIndex++;
                    if (currentOnyomiQuestionIndex < onyomiQuestions.size()) {//checks if the current question number is less the the total questions
                        displayOnyomiQuestion();//if it is less, then display the question
                    } else {
                        //End of test, display score or return to main menu
                    }
                } else if("jukugo".equals(testType)) {
                    currentJukugoQuestionIndex++;
                    if (currentJukugoQuestionIndex < jukugoQuestions.size()) { //checks if the current question number is less the the total questions
                        displayJukugoQuestion(); //if it is less, then display the question
                    } else {
                        //End of test, display score or return to main menu
                    }
                } else {
                    currentyojijukugoQuestionIndex++;
                    if (currentyojijukugoQuestionIndex < yojijukugoQuestions.size()) { //checks if the current question number is less the the total questions
                        displayyojijukugoQuestion(); //if it is less, then display the question
                    } else {
                        //End of test, display score or return to main menu
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
        if ("onyomi".equals(testType)) {
            //start onyomi test with 'questionCount' questions
            startOnyomiTest();
        } else if ("jukugo".equals(testType)) {
            //start jukugo test with 'questionCount' questions
            startJukugoTest();
        }
        else {
            //start jukugo test with 'questionCount' questions
            startYojijukugoTest();
        }


    }


    public void startOnyomiTest() {
        onyomiQuestions = questionDAO.getAllOnyomiQuestions();

        Collections.shuffle(onyomiQuestions); //randomize questions asked

        if (onyomiQuestions.size() > questionCount) {
            onyomiQuestions = onyomiQuestions.subList(0, questionCount);

        }
        currentOnyomiQuestionIndex = 0;
        score = 0;
        displayOnyomiQuestion();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
        if (yojijukugoDBHelper != null) {
            yojijukugoDBHelper.close();
        }
    }

    public void startJukugoTest() {
        jukugoQuestions = questionDAO.getAllJukugoQuestions();

        Collections.shuffle(jukugoQuestions); //randomize questions asked

        if (jukugoQuestions.size() > questionCount) {
            jukugoQuestions = jukugoQuestions.subList(0, questionCount);
        }
        currentJukugoQuestionIndex = 0;
        score = 0;
        displayJukugoQuestion();


    }
    public void startYojijukugoTest() {
        yojijukugoQuestions = questionDAO.getAllYojijukugoEntries();

        Collections.shuffle(yojijukugoQuestions); //randomize questions asked

        if (yojijukugoQuestions.size() > questionCount) {
            yojijukugoQuestions = yojijukugoQuestions.subList(0, questionCount);
        }
        currentyojijukugoQuestionIndex = 0;
        score = 0;
        displayyojijukugoQuestion();


    }

    public void displayOnyomiQuestion() {
        onyomiQuestion currentQuestion = onyomiQuestions.get(currentOnyomiQuestionIndex);
        Log.d("TestActivity", "Number of Onyomi Questions" + onyomiQuestions.size());
        questionTextView.setText(currentQuestion.getKanji());
        updateQuestionCounter();


    }

    public void displayJukugoQuestion() {
        jukugoQuestion currentQuestion = jukugoQuestions.get(currentJukugoQuestionIndex);
        Log.d("TestActivity", "Number of Jukugo Questions" + jukugoQuestions.size());
        questionTextView.setText(currentQuestion.getJukugo());
        updateQuestionCounter();


    }
    public void displayyojijukugoQuestion() {
        yojijukugoQuestions currentQuestion = yojijukugoQuestions.get(currentyojijukugoQuestionIndex);
        Log.d("TestActivity", "Number of yojijukugo Questions" + yojijukugoQuestions.size());
        questionTextView.setText(currentQuestion.getYojijukugo());
        updateQuestionCounter();


    }

    public void checkOnyomiAnswer(String userAnswer) {
        String correctAnswer = onyomiQuestions.get(currentOnyomiQuestionIndex).getOnyomi();
        String[] onyomiArray = correctAnswer.split(",");

        boolean isCorrect = false;
        for (String onyomi : onyomiArray) {
            if (userAnswer.trim().equals(onyomi.trim())) {
                isCorrect = true;
                break;
            }
        }

        if (isCorrect) {
            answerEditText.setText("正解！"); //when the answer is right, update the ui element to show a 'right answer' message
            answerEditText.setFocusable(false);
            new Handler().postDelayed(() -> {
                answerEditText.setText("");
                answerEditText.setFocusableInTouchMode(true);
                answerEditText.setFocusable(true);
            }, 1000);

            onyomiWrongAnswersCount = 0;
            currentOnyomiQuestionIndex++; //update index
            score++; //update score
        } else {
            answerEditText.setText("答えは間違っています。"); //when the answer is wrong, update the ui element to show a 'wrong answer' message
            answerEditText.setFocusable(false);
            new Handler().postDelayed(() -> {
                answerEditText.setText("");
                answerEditText.setFocusableInTouchMode(true);
                answerEditText.setFocusable(true);
            }, 1000);
            //Increment wrong answer count
            onyomiWrongAnswersCount++;
            //when wrong answer count rises to 3 display answer
            if(onyomiWrongAnswersCount >= 3){
                //set the notification text view to show the correct answer and after some time make the text dissapear
                notificationTextView.setText("正解は「" + correctAnswer + "」です。");
                notificationTextView.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() ->{
                    notificationTextView.setVisibility(View.GONE);
                        },5000
                );

                onyomiWrongAnswersCount = 0;
            }
        }

        if (currentOnyomiQuestionIndex < onyomiQuestions.size()) {
            displayOnyomiQuestion();
        } else {
            answerEditText.setText("試験を終了しました。"); //when the test is finshed, show a "finished message" and return to title screen
            answerEditText.setFocusable(false);
            new Handler().postDelayed(() -> {
                answerEditText.setText("");
                answerEditText.setFocusableInTouchMode(true);
                answerEditText.setFocusable(true);
            }, 5000);

        }
    }

    public void checkJukugoAnswer(String userAnswer) {
        String correctAnswer = jukugoQuestions.get(currentJukugoQuestionIndex).getReading();
        String[] jukugoArray = correctAnswer.split(",");

        boolean isCorrect = false;
        for(String reading : jukugoArray){
            if (userAnswer.trim().equals(reading.trim())){
                isCorrect = true;
                break;
            }
        }

        if (isCorrect) {
            answerEditText.setText("正解！"); //when the answer is right, update the ui element to show a 'right answer' message
            answerEditText.setFocusable(false);
            new Handler().postDelayed(() -> {
                answerEditText.setText("");
                answerEditText.setFocusableInTouchMode(true);
                answerEditText.setFocusable(true);
            }, 1000);

            jukugoWrongAnswersCount = 0;
            currentJukugoQuestionIndex++; //update index
            score++; //update score
        } else {
            answerEditText.setText("答えは間違っています。"); //when the answer is wrong, update the ui element to show a 'wrong answer' message
            answerEditText.setFocusable(false);
            new Handler().postDelayed(() -> {
                answerEditText.setText("");
                answerEditText.setFocusableInTouchMode(true);
                answerEditText.setFocusable(true);
            }, 1000);
            jukugoWrongAnswersCount++;

            if(jukugoWrongAnswersCount >= 3){
                //set the notification text view to show the correct answer and after some time make the text dissapear
                notificationTextView.setText("正解は「" + correctAnswer + "」です。");
                notificationTextView.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() ->{
                            notificationTextView.setVisibility(View.GONE);
                        },5000
                );
            }
        }

        if (currentJukugoQuestionIndex < jukugoQuestions.size()) {
            displayJukugoQuestion();
        } else {
            answerEditText.setText("試験を終了しました。"); //when the test is finshed, show a "finished message" and return to title screen
            answerEditText.setFocusable(false);
            new Handler().postDelayed(() -> {
                answerEditText.setText("");
                answerEditText.setFocusableInTouchMode(true);
                answerEditText.setFocusable(true);
            }, 5000);

        }
    }

    public void checkyojijukugoAnswer(String userAnswer) {
        String correctAnswer = yojijukugoQuestions.get(currentyojijukugoQuestionIndex).getReading();
        String[] yojijukugoArray = correctAnswer.split(",");

        boolean isCorrect = false;
        for(String reading : yojijukugoArray){
            if (userAnswer.trim().equals(reading.trim())){
                isCorrect = true;
                break;
            }
        }

        if (isCorrect) {
            answerEditText.setText("正解！"); //when the answer is right, update the ui element to show a 'right answer' message
            answerEditText.setFocusable(false);
            new Handler().postDelayed(() -> {
                answerEditText.setText("");
                answerEditText.setFocusableInTouchMode(true);
                answerEditText.setFocusable(true);
            }, 1000);

            yojijukugoWrongAnswersCount = 0;
            currentyojijukugoQuestionIndex++; //update index
            score++; //update score
        } else {
            answerEditText.setText("答えは間違っています。"); //when the answer is wrong, update the ui element to show a 'wrong answer' message
            answerEditText.setFocusable(false);
            new Handler().postDelayed(() -> {
                answerEditText.setText("");
                answerEditText.setFocusableInTouchMode(true);
                answerEditText.setFocusable(true);
            }, 1000);
            yojijukugoWrongAnswersCount++;

            if(yojijukugoWrongAnswersCount >= 3){
                //set the notification text view to show the correct answer and after some time make the text dissapear
                notificationTextView.setText("正解は「" + correctAnswer + "」です。");
                notificationTextView.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() ->{
                            notificationTextView.setVisibility(View.GONE);
                        },5000
                );
            }
        }

        if (currentyojijukugoQuestionIndex < yojijukugoQuestions.size()) {
            displayyojijukugoQuestion();
        } else {
            answerEditText.setText("試験を終了しました。"); //when the test is finshed, show a "finished message" and return to title screen
            answerEditText.setFocusable(false);
            new Handler().postDelayed(() -> {
                answerEditText.setText("");
                answerEditText.setFocusableInTouchMode(true);
                answerEditText.setFocusable(true);
            }, 5000);

        }
    }

//This updates the counter each time the next question appears
    private void updateQuestionCounter() {
        String testType = getIntent().getStringExtra("test_type");
        if ("onyomi".equals(testType)) {
            questionCounterTextView.setText("質問 " + (currentOnyomiQuestionIndex + 1) + "/" + questionCount);
        } else if ("jukugo".equals(testType)) {
            questionCounterTextView.setText("質問 " + (currentJukugoQuestionIndex + 1) + "/" + questionCount);
        }else {
            questionCounterTextView.setText("質問 " + (currentyojijukugoQuestionIndex + 1) + "/" + questionCount);
        }

    }




    }


