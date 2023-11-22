package com.example.kanjitest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
public class jukugoTestSelectionActivity extends AppCompatActivity {

    private Button jukugoType;
    private Button jukugoDraw;
    private Button jukugoBack;
    private SeekBar jukugoQuestionSlider;
    private TextView jukugoQuestionNumberTitle;
    private TextView jukugoQuestionCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jukugo_testselection);

        jukugoType = findViewById(R.id.jukugoType);
        jukugoBack = findViewById(R.id.jukugoBack);
        jukugoQuestionSlider = findViewById(R.id.jukugoQuestionSlider);
        jukugoQuestionNumberTitle = findViewById(R.id.jukugoQuestionNumberTitle);
        jukugoQuestionCount = findViewById(R.id.jukugoQuestionCount);

        //Initiate the DatabaseHelper
        DatabaseHelper kanjiDatabaseHelper = new DatabaseHelper(this);

        try {
            kanjiDatabaseHelper.createDatabase();
        } catch (IOException ioe) {
            Toast.makeText(this, "データベース構築中に問題が発生しました。", Toast.LENGTH_SHORT).show();
        }

        jukugoQuestionSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                jukugoQuestionCount.setText(progress + "問");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Goes back to title screen
        jukugoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //Starts typed onyomi test
        jukugoType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTest("jukugoType", jukugoQuestionSlider.getProgress());

            }
        });
    }

        private void startTest(String testType, int questionCount){
            Intent intent = new Intent(jukugoTestSelectionActivity.this, jukugoTest.class);
            intent.putExtra("test_type", testType);
            intent.putExtra("question_count", questionCount);
            if(questionCount > 0){
                startActivity(intent);
            }else{
                jukugoQuestionCount.setText("問題数を選んだでください。");
            }
        }



    }//end of class
