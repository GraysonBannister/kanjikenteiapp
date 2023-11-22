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

public class jukugoTest extends Activity {
    //UI interface features
    private TextView questionTextView;
    private EditText answerEditText;
    private Button submitButton;
    private Button exitButton;
    private Button nextQuestionButton;
    private TextView scoreTextView;
    private TextView questionCounterTextView;
    private TextView notificationTextView;
    private List<jukugoQuestion> jukugoQuestions;
    private QuestionDAO questionDAO;

    //test var
    private int score = 0;
    private int currentjukugoQuestionIndex = 0;
    private int questionCount;
    private int jukugoWrongAnswersCount = 0;

    //database var
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        String testType = getIntent().getStringExtra("test_type");
        questionCount = getIntent().getIntExtra("question_count", 10);//default to 10 if not provided

        super.onCreate(savedInstanceState);
        setContentView(R.layout.jukugo_testview);

        questionTextView = findViewById(R.id.jukugoTypeQuestionView);
        submitButton = findViewById(R.id.jukugoAnswerButton);
        exitButton = findViewById(R.id.jukugoTestExit);
        nextQuestionButton = findViewById(R.id.jukugoNext);
        answerEditText = findViewById(R.id.jukugoTypeInput);
        questionCounterTextView = findViewById(R.id.jukugoTestQuestionCount);
        notificationTextView = findViewById(R.id.jukugoNotification);

        dbHelper = new DatabaseHelper(this);

        try {
            dbHelper.openDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast.makeText(this, "データベースのアクセスの問題が発生しました。", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //sets up question database
        questionDAO = new QuestionDAO(dbHelper.getReadableDatabase());

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userAnswer = answerEditText.getText().toString().trim();
                String testType = getIntent().getStringExtra("test_type");

                if ("jukugoType".equals(testType)) {
                    checkjukugoTypeAnswer(userAnswer);
                } else {//This is for other jukugo tests nothing is here yet
                    finish();
                }
            }
        });

        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String testType = getIntent().getStringExtra("test_type");

                if ("jukugoType".equals(testType)) {
                    currentjukugoQuestionIndex++;
                    if (currentjukugoQuestionIndex < jukugoQuestions.size()) {
                        displayjukugoTypeQuestion();
                    } else {
                        //end test if no test is selected
                        finish();
                    }
                } else {
                    //for other modes. Nothing here yet
                }
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Finish the activity and go back to previous screen
                finish();
            }
        });

        //Start test!
        if ("jukugoType".equals(testType)) {
            //start jukugo typed test
            startjukugoTypeTest();

        } else {
            //for other modes nothing here yet
            finish();
        }

    }//end of on open

    public void startjukugoTypeTest() {
        jukugoQuestions = questionDAO.getAllJukugoQuestions();

        Collections.shuffle(jukugoQuestions);//randomize question list

        if (jukugoQuestions.size() > questionCount) {//What does this do?
            jukugoQuestions = jukugoQuestions.subList(0, questionCount);

        }

        currentjukugoQuestionIndex = 0;
        score = 0;
        displayjukugoTypeQuestion();

    }


    public void displayjukugoTypeQuestion() {
        jukugoQuestion currentQuestion = jukugoQuestions.get(currentjukugoQuestionIndex);
        Log.d("TestActivity", "Number of jukugo Questions" + jukugoQuestions.size());
        questionTextView.setText(currentQuestion.getJukugo());
        updateQuestionCounter();
    }

    public void checkjukugoTypeAnswer(String userAnswer) {
        String correctAnswer = jukugoQuestions.get(currentjukugoQuestionIndex).getReading();
        String[] jukugoArray = correctAnswer.split(",");

        boolean isCorrect = false;
        for (String jukugo : jukugoArray) {
            if (userAnswer.trim().equals(jukugo.trim())) {
                isCorrect = true;
                break;
            }
        }


        if (isCorrect) {
            answerEditText.setText("正解！");
            answerEditText.setFocusable(false);
            new Handler().postDelayed(() -> {
                answerEditText.setText("");
                answerEditText.setFocusableInTouchMode(true);
                answerEditText.setFocusable(true);
            }, 1000);

            jukugoWrongAnswersCount = 0;
            currentjukugoQuestionIndex++;
            score++;
        } else {
            answerEditText.setText("答えは間違っています。"); //when the answer is wrong, update the ui element to show a 'wrong answer' message
            answerEditText.setFocusable(false);
            new Handler().postDelayed(() -> {
                answerEditText.setText("");
                answerEditText.setFocusableInTouchMode(true);
                answerEditText.setFocusable(true);
            }, 1000);
            //Increment wrong answer count
            jukugoWrongAnswersCount++;
            //when wrong answer count rises to 3 display answer
            if (jukugoWrongAnswersCount >= 3) {
                //set the notification text view to show the correct answer and after some time make the text dissapear
                notificationTextView.setText("正解は「" + correctAnswer + "」です。");
                notificationTextView.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> {
                            notificationTextView.setVisibility(View.GONE);
                        }, 5000
                );
                jukugoWrongAnswersCount = 0;

            }

        }
    }

    //This updates the counter each time the next question appears
    private void updateQuestionCounter() {
        String testType = getIntent().getStringExtra("test_type");
        if ("jukugoType".equals(testType)) {
            questionCounterTextView.setText("質問 " + (currentjukugoQuestionIndex + 1) + "/" + questionCount);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

}
