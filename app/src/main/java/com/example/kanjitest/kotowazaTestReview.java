package com.example.kanjitest;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

//List<KotowazaQA> userAnswers = DataHolder.getInstance().getUserAnswers();

public class kotowazaTestReview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kotowaza_test_review);
        String jsonUserAnswers = getIntent().getStringExtra("userAnswers");
        Type type = new TypeToken<List<kotowazaQA>>(){}.getType();

        List<kotowazaQA> userAnswers = kotowazaDataHolder.getInstance().getUserAnswers();


        LinearLayout linearLayoutContainer = findViewById(R.id.kotowaza_review_linear_layout_container);
        Button finishButton = findViewById(R.id.kotowazaReviewFinish);


        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Explicitly start the HomeActivity (replace HomeActivity.class with your actual home activity class)
                Intent intent = new Intent(kotowazaTestReview.this, kotowazaTestSelectionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear the back stack
                startActivity(intent);
                finish(); // Close the current activity
            }
        });


        // Display the drawings and questions
        for (kotowazaQA answer : userAnswers) {
            LinearLayout questionAnswerLayout = new LinearLayout(this);
            questionAnswerLayout.setOrientation(LinearLayout.HORIZONTAL);
            questionAnswerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            questionAnswerLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.answer_review_border));
            questionAnswerLayout.setClickable(true);
            questionAnswerLayout.setPadding(25, 50, 25, 50);

            // Create TextView for the question
            TextView questionTextView = new TextView(this);
            questionTextView.setText(answer.question);
            questionTextView.setTextColor(Color.BLACK); // Ensure text is visible
            LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, .5f); // 1f weight
            questionTextView.setLayoutParams(textLayoutParams);
            questionAnswerLayout.addView(questionTextView);

            // Check if there is an answer
            if (answer.userDrawing != null && answer.userDrawing.length > 0) {
                // Convert byte array back to Bitmap and create ImageView for the drawing
                Bitmap drawing = BitmapFactory.decodeByteArray(answer.userDrawing, 0, answer.userDrawing.length);
                ImageView drawingImageView = new ImageView(this);
                drawingImageView.setImageBitmap(drawing);
                int imageSize = 150; // Adjust this value as needed
                LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                        imageSize, imageSize); // Fixed size for image
                drawingImageView.setLayoutParams(imageLayoutParams);
                questionAnswerLayout.addView(drawingImageView);
            } else {
                // Display a message indicating no answer was provided
                TextView noAnswerText = new TextView(this);
                noAnswerText.setText("No answer provided");
                noAnswerText.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                questionAnswerLayout.addView(noAnswerText);
            }
            // Set an OnClickListener for the entire layout
            questionAnswerLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(answer);
                }
            });


            linearLayoutContainer.addView(questionAnswerLayout);
        }



    }

    private void showPopup(kotowazaQA answer) {
        // Create a dialog or use an AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Create a TextView for the custom title
        TextView title = new TextView(this);
        title.setText("Review Answer");
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20); // Set text size as needed
        title.setTextColor(Color.BLACK); // Set text color as needed
        // Set other title styles as needed

        // Create a custom layout for the dialog
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setPadding(20, 20, 20, 20);
        dialogLayout.setGravity(Gravity.CENTER_HORIZONTAL); // Set gravity to center

        // Add question and answer details to the layout
        TextView questionText = new TextView(this);
        String questionAndAnswer = "Question: " + answer.question + "\nAnswer: " + (answer.answer != null ? answer.answer : "No answer provided");
        questionText.setText(questionAndAnswer);
        questionText.setGravity(Gravity.CENTER); // Center the text
        questionText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Width
                LinearLayout.LayoutParams.WRAP_CONTENT // Height
        ));
        dialogLayout.addView(questionText);

// Add the drawing image to the layout if it exists
        if (answer.userDrawing != null && answer.userDrawing.length > 0) {
            Bitmap drawing = BitmapFactory.decodeByteArray(answer.userDrawing, 0, answer.userDrawing.length);
            ImageView drawingImageView = new ImageView(this);
            drawingImageView.setImageBitmap(drawing);
            int popupImageSize = 300; // Adjust this value as needed for popup size
            drawingImageView.setLayoutParams(new LinearLayout.LayoutParams(
                    popupImageSize, popupImageSize));
            drawingImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            dialogLayout.addView(drawingImageView);
        }

        // You can add more details here as needed

// Set the custom title and view to the AlertDialog builder
        builder.setCustomTitle(title);
        builder.setView(dialogLayout);
        // Add dialog buttons (e.g., Close)
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Show the dialog
        builder.create().show();
    }
}