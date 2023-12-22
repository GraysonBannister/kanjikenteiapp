package com.example.kanjitest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class jukugoTestSelectionActivity extends AppCompatActivity {

    private Button jukugoType;
    private Button jukugoDraw;
    private Button jukugoBack;
    private SeekBar jukugoQuestionSlider;
    private TextView jukugoQuestionNumberTitle;
    private TextView jukugoQuestionCount;
    private ChipGroup levelChipGroup;
    private List<Float> selectedRanks = new ArrayList<>(); // Store multiple ranks



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jukugo_testselection);

        jukugoType = findViewById(R.id.jukugoType);
        jukugoBack = findViewById(R.id.jukugoBack);
        jukugoQuestionSlider = findViewById(R.id.jukugoQuestionSlider);
        jukugoQuestionNumberTitle = findViewById(R.id.jukugoQuestionNumberTitle);
        jukugoQuestionCount = findViewById(R.id.jukugoQuestionCount);

        levelChipGroup = findViewById(R.id.jukugoLevelChipGroup);

        //Initiate the DatabaseHelper
        jukugoDatabaseHelper kanjiDatabaseHelper = new jukugoDatabaseHelper(this);

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
                int questionCountValue = jukugoQuestionSlider.getProgress();
                if(questionCountValue > 0){
                    Intent intent = new Intent(jukugoTestSelectionActivity.this, jukugoTest.class);
                    intent.putExtra("test_type", "jukugoType");
                    intent.putExtra("question_count", questionCountValue);
                    // Convert List<Float> to float[]
                    float[] ranksArray = new float[selectedRanks.size()];
                    for (int i = 0; i < selectedRanks.size(); i++) {
                        ranksArray[i] = selectedRanks.get(i);
                    }
                    intent.putExtra("selected_ranks", ranksArray);
                    startActivity(intent);
                } else {
                    Toast.makeText(jukugoTestSelectionActivity.this, "問題数を選んでください。", Toast.LENGTH_SHORT).show();
                }

            }
        });
        levelChipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                selectedRanks.clear(); // Clear the list for new selections
                for (int id : checkedIds) {
                    if (!checkedIds.isEmpty()) {
                        // Get the ID of the last selected chip
                        Chip selectedChip = findViewById(id);
                        if (selectedChip != null) {
                            try {
                                float rank = Float.parseFloat(selectedChip.getTag().toString());
                                selectedRanks.add(rank); // Add rank to the list
                                Log.d("ChipGroup", "Selected rank: " + rank);
                            } catch (NumberFormatException e) {
                                Log.e("ChipGroup", "Error parsing chip tag to float", e);
                            }
                        }
                    }
                }
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
