package com.example.kanjitest;

import android.app.Activity;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

public class kunyomiTest extends Activity {

    private TextView questionTextView;
    private EditText answerEditText;
    private Button submitButton;
    private Button exitButton;
    private Button nextQuestionButton;
    private TextView scoreTextView;
    private TextView questionCounterTextView;
    private TextView notificationTextView;

    private List<kunyomiQuestion> KunyomiQuestions;
    private QuestionDAO questionDAO;

    //test var
    private int score = 0;
    private int questionCount;
    //Kunyomi test
    private int currentKunyomiQuestionIndex = 0;

    // Field to keep track of the current correct answer
    private String currentCorrectAnswer;
    private int KunyomiWrongAnswersCount = 0;

    //database var
    private kunyomiDatabaseHelper dbHelper;

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

        dbHelper = new kunyomiDatabaseHelper(this);

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

                if ("kunyomiType".equals(testType)) {
                    checkKunyomiTypeAnswer(userAnswer);
                } else {//This is for other Kunyomi tests nothing is here yet
                    finish();
                }
            }
        });

        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String testType = getIntent().getStringExtra("test_type");

                if ("kunyomiType".equals(testType)) {
                    currentKunyomiQuestionIndex++;
                    if (currentKunyomiQuestionIndex < KunyomiQuestions.size()) {
                        displayKunyomiTypeQuestion();
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
        if ("kunyomiType".equals(testType)) {
            //start Kunyomi typed test
            startKunyomiTypeTest();

        } else {
            //for other modes nothing here yet
            finish();
        }


    }//end of on open

    public void startKunyomiTypeTest() {
        KunyomiQuestions = questionDAO.getAllKunyomiEntries();

        Collections.shuffle(KunyomiQuestions);//randomize question list

        if (KunyomiQuestions.size() > questionCount) {//What does this do?
            KunyomiQuestions = KunyomiQuestions.subList(0, questionCount);

        }

        currentKunyomiQuestionIndex = 0;
        score = 0;
        displayKunyomiTypeQuestion();

    }

    public void displayKunyomiTypeQuestion() {
        kunyomiQuestion currentQuestion = KunyomiQuestions.get(currentKunyomiQuestionIndex);
        Pair<String, String> kunyomiAndReading = currentQuestion.getRandomKunyomiAndReading();


        Log.d("TestActivity", "Number of Kunyomi Questions" + KunyomiQuestions.size());
        // Set the current correct answer
        currentCorrectAnswer = kunyomiAndReading.second;

        // Display the kunyomi to the user
        questionTextView.setText(kunyomiAndReading.first);
        updateQuestionCounter();
    }

    public void checkKunyomiTypeAnswer(String userAnswer) {
        //String correctAnswer = KunyomiQuestions.get(currentKunyomiQuestionIndex).getReading();
        //String[] KunyomiArray = correctAnswer.split(",");

        boolean isCorrect = userAnswer.trim().equals(currentCorrectAnswer.trim());

        if (userAnswer.trim().equals(currentCorrectAnswer.trim())) {
            isCorrect = true;
        }



        if (isCorrect) {
            answerEditText.setText("正解！");
            answerEditText.setFocusable(false);
            new Handler().postDelayed(() -> {
                answerEditText.setText("");
                answerEditText.setFocusableInTouchMode(true);
                answerEditText.setFocusable(true);
            }, 1000);

            KunyomiWrongAnswersCount = 0;
            currentKunyomiQuestionIndex++;
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
            KunyomiWrongAnswersCount++;
            //when wrong answer count rises to 3 display answer
            if (KunyomiWrongAnswersCount >= 3) {
                //set the notification text view to show the correct answer and after some time make the text dissapear
                notificationTextView.setText("正解は「" + currentCorrectAnswer + "」です。");
                notificationTextView.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> {
                            notificationTextView.setVisibility(View.GONE);
                        }, 5000
                );
                KunyomiWrongAnswersCount = 0;

            }

        }
    }

    //This updates the counter each time the next question appears
    private void updateQuestionCounter() {
        String testType = getIntent().getStringExtra("test_type");
        if ("kunyomiType".equals(testType)) {
            questionCounterTextView.setText("質問 " + (currentKunyomiQuestionIndex + 1) + "/" + questionCount);
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