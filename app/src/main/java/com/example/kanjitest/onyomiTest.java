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


public class onyomiTest extends Activity {


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
    private QuestionDAO questionDAO;

    //test var
    private int score = 0;
    private int questionCount;
    //onyomi test
    private int currentOnyomiQuestionIndex = 0;
    private int onyomiWrongAnswersCount = 0;

    //database var
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String testType = getIntent().getStringExtra("test_type");
        questionCount = getIntent().getIntExtra("question_count", 10);//default to 10 if not provided

        super.onCreate(savedInstanceState);
        setContentView(R.layout.onyomi_and_kunyomi_testview);

        questionTextView = findViewById(R.id.onyomiTypeQuestionView);
        submitButton = findViewById(R.id.onyomiAnswerButton);
        exitButton = findViewById(R.id.onyomiTestExit);
        nextQuestionButton = findViewById(R.id.onyomiNext);
        answerEditText = findViewById(R.id.onyomiTypeInput);
        questionCounterTextView = findViewById(R.id.onyomiTestQuestionCount);
        notificationTextView = findViewById(R.id.onyomiNotification);

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

                if ("onyomiType".equals(testType)) {
                    checkOnyomiTypeAnswer(userAnswer);
                } else {//This is for other onyomi tests nothing is here yet
                    finish();
                }
            }
        });

        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String testType = getIntent().getStringExtra("test_type");

                if ("onyomiType".equals(testType)) {
                    currentOnyomiQuestionIndex++;
                    if (currentOnyomiQuestionIndex < onyomiQuestions.size()) {
                        displayOnyomiTypeQuestion();
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
        if ("onyomiType".equals(testType)) {
            //start onyomi typed test
            startOnyomiTypeTest();

        } else {
            //for other modes nothing here yet
            finish();
        }


    }//end of on open

    public void startOnyomiTypeTest() {
        float[] selectedRanks = getIntent().getFloatArrayExtra("selected_ranks"); // Get the selected level passed from the previous activity
        onyomiQuestions = questionDAO.getAllOnyomiQuestions(selectedRanks);
        Log.d("onyomiTestSelection", "Selected Rank: " + selectedRanks);
        Log.d("TestActivity", "Number of Onyomi Questions " + onyomiQuestions.size());

        Collections.shuffle(onyomiQuestions);//randomize question list

        if (onyomiQuestions.size() > questionCount) {//What does this do?
            onyomiQuestions = onyomiQuestions.subList(0, questionCount);

        }

        currentOnyomiQuestionIndex = 0;
        score = 0;
        displayOnyomiTypeQuestion();

    }


    public void displayOnyomiTypeQuestion() {
        onyomiQuestion currentQuestion = onyomiQuestions.get(currentOnyomiQuestionIndex);
        Log.d("TestActivity", "Number of Onyomi Questions" + onyomiQuestions.size());
        questionTextView.setText(currentQuestion.getKanji());
        updateQuestionCounter();
    }
//conver user hiragana answer to katakana to match onyomi data that is in katakana

    private String convertHiraganaToKatakana(String hiragana) {
        StringBuilder katakana = new StringBuilder();
        for (char ch : hiragana.toCharArray()) {
            if (ch >= 'ぁ' && ch <= 'ん') {
                katakana.append((char) (ch - 'ぁ' + 'ァ'));
            } else {
                katakana.append(ch);
            }
        }
        return katakana.toString();
    }

    public void checkOnyomiTypeAnswer(String userAnswer) {
        String convertedUserAnswer = convertHiraganaToKatakana(userAnswer);
        String correctAnswer = onyomiQuestions.get(currentOnyomiQuestionIndex).getOnyomi();
        String[] onyomiArray = correctAnswer.split("、");

        boolean isCorrect = false;
        for (String onyomi : onyomiArray) {
            if (convertedUserAnswer.trim().equals(onyomi.trim())) {
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

            onyomiWrongAnswersCount = 0;
            currentOnyomiQuestionIndex++;
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
            onyomiWrongAnswersCount++;
            //when wrong answer count rises to 3 display answer
            if (onyomiWrongAnswersCount >= 3) {
                //set the notification text view to show the correct answer and after some time make the text dissapear
                notificationTextView.setText("正解は「" + correctAnswer + "」です。");
                notificationTextView.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> {
                            notificationTextView.setVisibility(View.GONE);
                        }, 5000
                );
                onyomiWrongAnswersCount = 0;

            }

        }
    }

    //This updates the counter each time the next question appears
    private void updateQuestionCounter() {
        String testType = getIntent().getStringExtra("test_type");
        if ("onyomiType".equals(testType)) {
            questionCounterTextView.setText("質問 " + (currentOnyomiQuestionIndex + 1) + "/" + questionCount);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}//end of class