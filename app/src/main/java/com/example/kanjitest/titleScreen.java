package com.example.kanjitest;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import java.io.IOException;

public class titleScreen extends AppCompatActivity {

   private Button onyomiAndKunyomiTestButton;
   private Button jukugoTestButton;
   private Button yojijukugoTestButton;
   private Button kotowazaTestButton;

   @Override
    protected void onCreate(Bundle savedInstanceState){
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);

       onyomiAndKunyomiTestButton = findViewById(R.id.onyomiTestButton);
        jukugoTestButton = findViewById(R.id.jukugoTestButton);
        yojijukugoTestButton = findViewById(R.id.yojijukugoTestButton);
       kotowazaTestButton = findViewById(R.id.kotowazaTestButton);


       //Initiate the DatabaseHelper
       DatabaseHelper kanjiDatabaseHelper = new DatabaseHelper(this);
       YojijukugoDatabaseHelper yojijukugoDatabaseHelper = new YojijukugoDatabaseHelper(this);

       //Try creating or setting up the Database
       try{
           kanjiDatabaseHelper.createDatabase();

       }catch (IOException e){
           e.printStackTrace();
           //Handle this exception, show message or log
           Toast.makeText(this, "データベース構築中に問題が発生しました。", Toast.LENGTH_SHORT).show();
       }




       //onyomi test button
       onyomiAndKunyomiTestButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){

               Intent intent = new Intent(titleScreen.this, onyomiAndKunyomiTestSelection.class);
               startActivity(intent);
           }
       });
       //jukugo test button
       jukugoTestButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               //startTest("jukugo" , questionsNumberSeekBar.getProgress());
               Intent intent = new Intent(titleScreen.this, jukugoTestSelectionActivity.class);
               startActivity(intent);
           }
       });
       //yojijukugo test button
       yojijukugoTestButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               Intent intent = new Intent(titleScreen.this, YojijukugoTestSelectionActivity.class);
               startActivity(intent);
           }
       });
       kotowazaTestButton.setOnClickListener(new View.OnClickListener(){
           @Override
           public void onClick(View v){
               Intent intent = new Intent(titleScreen.this, kotowazaTestSelectionActivity.class);
               startActivity(intent);
           }
       });

       }

}

