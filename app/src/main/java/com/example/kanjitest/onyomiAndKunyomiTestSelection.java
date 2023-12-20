package com.example.kanjitest;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.IOException;
import java.util.List;

public class onyomiAndKunyomiTestSelection extends AppCompatActivity {
    private Button onyomiType;
    private Button onyomiDraw;

    private Button kunyomiType;

    private Button onyomiAndKunyomiBack;
    private SeekBar onyomiAndKunyomiQuestionSlider;
    private TextView onyomiAndKunyomiQuestionNumberTitle;
    private TextView onyomiAndKunyomiQuestionCount;
    private ChipGroup levelChipGroup;
    private float currentSelectedRank = 5.0f; // Default rank
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
        levelChipGroup = findViewById(R.id.levelChipGroup);
        levelChipGroup.check(R.id.levelTen); // Assuming levelTen is the ID of a chip






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

        levelChipGroup.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                if (!checkedIds.isEmpty()) {
                    // Get the ID of the last selected chip
                    int selectedChipId = checkedIds.get(checkedIds.size() - 1);
                    Chip selectedChip = findViewById(selectedChipId);
                    if (selectedChip != null) {
                        try {
                            currentSelectedRank = Float.parseFloat(selectedChip.getTag().toString());
                            Log.d("ChipGroup", "Selected rank: " + currentSelectedRank);
                        } catch (NumberFormatException e) {
                            Log.e("ChipGroup", "Error parsing chip tag to float", e);
                        }
                    }
                }
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
                    intent.putExtra("rank", currentSelectedRank);
                    startActivity(intent);
                } else {
                    Toast.makeText(onyomiAndKunyomiTestSelection.this, "問題数を選んでください。", Toast.LENGTH_SHORT).show();
                }
            }

        });

        //Add onyomi drawn test? or something else?

    }
    private float getRanks(ChipGroup levelChipGroup) {
        int selectedChipId = levelChipGroup.getCheckedChipId();
        float defaultRank = 5.0f;
        if (selectedChipId != -1) {
            Chip selectedChip = levelChipGroup.findViewById(selectedChipId);
            Log.d("selectedChip", "Selected chip: " + selectedChip);
            defaultRank = 5.0f;
            if (selectedChip != null) {
                try {
                    return Float.parseFloat(selectedChip.getTag().toString());
                } catch (NumberFormatException e) {
                    Log.e("ChipGroup", "Error parsing chip tag to float", e);
                }
            }// Default or maximum level if no chip is selected
        }
        return defaultRank;
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
