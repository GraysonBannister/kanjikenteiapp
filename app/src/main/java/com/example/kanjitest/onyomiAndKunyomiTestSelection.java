package com.example.kanjitest;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

public class onyomiAndKunyomiTestSelection extends AppCompatActivity {
    private Button onyomiType;
    private Button onyomiDraw;

    private Button kunyomiType;

    private Button onyomiAndKunyomiBack;
    private SeekBar onyomiAndKunyomiQuestionSlider;
    private TextView onyomiAndKunyomiQuestionNumberTitle;
    private TextView onyomiAndKunyomiQuestionCount;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onyomi_and_kunyomi_testselection);


        onyomiAndKunyomiBack = findViewById(R.id.onyomiBack);
        onyomiAndKunyomiQuestionSlider = findViewById(R.id.onyomiQuestionSlider);
        onyomiAndKunyomiQuestionNumberTitle = findViewById(R.id.onyomiQuestionNumberTitle);
        onyomiAndKunyomiQuestionCount = findViewById(R.id.onyomiQuestionCount);
        onyomiType = findViewById(R.id.onyomiType);

        kunyomiType =findViewById(R.id.kunyomiType);





        //Initiate the DatabaseHelper
        DatabaseHelper kanjiDatabaseHelper = new DatabaseHelper(this);
        kunyomiDatabaseHelper kunyomiDatabaseHelper = new kunyomiDatabaseHelper(this);

        try {
            kanjiDatabaseHelper.createDatabase();
            kunyomiDatabaseHelper.createDatabase();

        } catch (IOException ioe) {
            Toast.makeText(this, "データベース構築中に問題が発生しました。", Toast.LENGTH_SHORT).show();
        }

        onyomiAndKunyomiQuestionSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onyomiAndKunyomiQuestionCount.setText(progress + "問");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //Goes back to title screen
        onyomiAndKunyomiBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //Starts typed onyomi test
        onyomiType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTest("onyomiType", onyomiAndKunyomiQuestionSlider.getProgress());

            }



        });

        kunyomiType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int questionCountValue = onyomiAndKunyomiQuestionSlider.getProgress();
                if(questionCountValue > 0){
                    Intent intent = new Intent(onyomiAndKunyomiTestSelection.this, kunyomiTest.class);
                    intent.putExtra("test_type", "kunyomiType");
                    intent.putExtra("question_count", questionCountValue);
                    startActivity(intent);
                } else {
                    Toast.makeText(onyomiAndKunyomiTestSelection.this, "問題数を選んでください。", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Add onyomi drawn test? or something else?

    }

    private void startTest(String testType, int questionCount){
        Intent intent = new Intent(onyomiAndKunyomiTestSelection.this, onyomiTest.class);
        intent.putExtra("test_type", testType);
        intent.putExtra("question_count", questionCount);
        if(questionCount > 0){
            startActivity(intent);
        }else{
            onyomiAndKunyomiQuestionCount.setText("問題数を選んだでください。");
        }
    }






}
