package com.example.kanjitest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class kotowazaTestSelectionActivity extends AppCompatActivity {




    private Button kotowazaTest;
    private Button kotowazaBack;
    private SeekBar kotowazaQuestionSlider;
    private TextView kotowazaQuestionNumberTitle;
    private TextView kotowazaQuestionCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kotowaza_test_selection);

        kotowazaBack = findViewById(R.id.kotowazaBack);
        kotowazaQuestionSlider = findViewById(R.id.kotowazaQuestionSlider);
        kotowazaQuestionNumberTitle = findViewById(R.id.kotowazaQuestionNumberTitle);
        kotowazaQuestionCount = findViewById(R.id.kotowazaQuestionCount);
        kotowazaTest = findViewById(R.id.kotowazaTest);


        //Initiate the DatabaseHelper
        DatabaseHelper kotowazaDatabaseHelper = new DatabaseHelper(this);


        try {
            kotowazaDatabaseHelper.createDatabase();

        } catch (IOException ioe) {
            Toast.makeText(this, "データベース構築中に問題が発生しました。", Toast.LENGTH_SHORT).show();
        }

        kotowazaQuestionSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                kotowazaQuestionCount.setText(progress + "問");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //Goes back to title screen
        kotowazaBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        kotowazaTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTest("kotowazaTest", kotowazaQuestionSlider.getProgress());

            }


        });
    }
        private void startTest(String testType, int questionCount){
            Intent intent = new Intent(kotowazaTestSelectionActivity.this, kotowazaTest.class);
            intent.putExtra("test_type", testType);
            intent.putExtra("question_count", questionCount);
            if(questionCount > 0){
                startActivity(intent);
            }else{
                kotowazaQuestionCount.setText("問題数を選んだでください。");
            }
        }

    }
