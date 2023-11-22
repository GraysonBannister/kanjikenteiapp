package com.example.kanjitest;



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

public class onyomiTestSelection extends AppCompatActivity {
    private Button onyomiType;
    private Button onyomiDraw;
    private Button onyomiBack;
    private SeekBar onyomiQuestionSlider;
    private TextView onyomiQuestionNumberTitle;
    private TextView onyomiQuestionCount;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onyomi_testselection);


        onyomiBack = findViewById(R.id.onyomiBack);
        onyomiQuestionSlider = findViewById(R.id.onyomiQuestionSlider);
        onyomiQuestionNumberTitle = findViewById(R.id.onyomiQuestionNumberTitle);
        onyomiQuestionCount = findViewById(R.id.onyomiQuestionCount);
        onyomiType = findViewById(R.id.onyomiType);




        //Initiate the DatabaseHelper
        DatabaseHelper kanjiDatabaseHelper = new DatabaseHelper(this);

        try {
            kanjiDatabaseHelper.createDatabase();
        } catch (IOException ioe) {
            Toast.makeText(this, "データベース構築中に問題が発生しました。", Toast.LENGTH_SHORT).show();
        }

        onyomiQuestionSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onyomiQuestionCount.setText(progress + "問");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //Goes back to title screen
        onyomiBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //Starts typed onyomi test
        onyomiType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTest("onyomiType", onyomiQuestionSlider.getProgress());

            }
        });

        //Add onyomi drawn test? or something else?

    }

    private void startTest(String testType, int questionCount){
        Intent intent = new Intent(onyomiTestSelection.this, onyomiTest.class);
        intent.putExtra("test_type", testType);
        intent.putExtra("question_count", questionCount);
        if(questionCount > 0){
            startActivity(intent);
        }else{
            onyomiQuestionCount.setText("問題数を選んだでください。");
        }
    }






}
