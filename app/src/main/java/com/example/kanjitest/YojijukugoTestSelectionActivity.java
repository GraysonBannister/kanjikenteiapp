package com.example.kanjitest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.IOException;


public class YojijukugoTestSelectionActivity extends AppCompatActivity {
    private Button yojiType;
    private Button yojiDraw;
    private Button yojiBack;
    private SeekBar yojiQuestionSlider;
    private TextView yojiQuestionNumberTitle;
    private TextView yojiQuestionCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.yojijukugo_testselection);

        yojiType = findViewById(R.id.yojiType);
        yojiDraw = findViewById(R.id.yojiDraw);
        yojiBack = findViewById(R.id.yojiBack);
        yojiQuestionCount = findViewById(R.id.yojiQuestionCount);
        yojiQuestionSlider = findViewById(R.id.yojiQuestionSlider);
        yojiQuestionNumberTitle = findViewById(R.id.yojiQuestionNumberTitle);



        YojijukugoDatabaseHelper yojijukugoDatabaseHelper = new YojijukugoDatabaseHelper(this);

        try {
            yojijukugoDatabaseHelper.createDatabase();
        } catch (IOException ioe) {
            Toast.makeText(this, "データベース構築中に問題が発生しました。", Toast.LENGTH_SHORT).show();
        }

        yojiQuestionSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                yojiQuestionCount.setText(progress + "問");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Goes back to main title screen
        yojiBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
        //Starts typed yojijukugo test
        yojiType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTest("yojiType", yojiQuestionSlider.getProgress());
            }
        });

        //Starts drawn yojijukugo test
        yojiDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTest("yojiDraw", yojiQuestionSlider.getProgress());
            }
        });


    }

    private void startTest(String testType, int questionCount){
        Intent intent = new Intent(YojijukugoTestSelectionActivity.this, yojiTest.class);
        intent.putExtra("test_type", testType);
        intent.putExtra("question_count", questionCount);
        if (questionCount > 0){
            startActivity(intent);
        } else{
            yojiQuestionCount.setText("問題数を選んだでください。");
        }

    }
}