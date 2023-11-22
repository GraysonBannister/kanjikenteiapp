package com.example.kanjitest;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteException;
//drawing
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;


import android.util.AttributeSet;
import android.util.Log;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Handler;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

public class yojiTest extends Activity {

    //UI interface features
    private TextView questionTextView;
    private EditText answerEditText;
    private Button submitButton;
    private Button exitButton;
    private Button nextQuestionButton;
    private TextView scoreTextView;
    private TextView questionCounterTextView;
    private TextView notificationTextView;
    private drawingView yojiDrawCanvas;
    private ImageButton clearDrawingButton;


    private List<yojijukugoQuestions> yojijukugoQuestions;
    private QuestionDAO questionDAO;


    //test var
    private int score = 0;
    private int currentyojijukugoQuestionIndex = 0;
    private int questionCount;
    private int yojijukugoWrongAnswersCount = 0;


    // Convert dp to pixels
    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    //database var
    private YojijukugoDatabaseHelper yojijukugoDBHelper;

    private PythonInterpreter pythonInterpreter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String testType = getIntent().getStringExtra("test_type");
        questionCount = getIntent().getIntExtra("question_count", 10); //default to 10 if not provided

        super.onCreate(savedInstanceState);
        setContentView(R.layout.yoji_testview);


        questionTextView = findViewById(R.id.jukugoTypeQuestionView);
        submitButton = findViewById(R.id.jukugoAnswerButton);
        exitButton = findViewById(R.id.jukugoTestExit);
        nextQuestionButton = findViewById(R.id.jukugoNext);
        answerEditText = findViewById(R.id.jukugoTypeInput);
        questionCounterTextView = findViewById(R.id.jukugoTestQuestionCount);
        notificationTextView = findViewById(R.id.jukugoNotification);
        yojiDrawCanvas = findViewById(R.id.yojiDrawCanvas);
        clearDrawingButton = findViewById(R.id.clearDrawingButton);

//database
        yojijukugoDBHelper = new YojijukugoDatabaseHelper(this);


        //start python
        if (!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }
//python interpreter
        pythonInterpreter = new PythonInterpreter();

        try {

            yojijukugoDBHelper.openDatabase();

        } catch (SQLiteException e) {
            e.printStackTrace();
            Toast.makeText(this, "データベースのアクセスの問題が発生しました。", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //questionDAO = new QuestionDAO(dbHelper.getReadableDatabase());
        if ("yojiType".equals(testType)) {
            questionDAO = new QuestionDAO(yojijukugoDBHelper.getReadableDatabase());
        } else { // For yojiDraw test
            questionDAO = new QuestionDAO(yojijukugoDBHelper.getReadableDatabase());

        }




        clearDrawingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yojiDrawCanvas.clearDrawing();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String testType = getIntent().getStringExtra("test_type");

                if ("yojiType".equals(testType)) {
                    String userAnswer = answerEditText.getText().toString().trim();
                    checkYojiTypeAnswer(userAnswer);
                } else { //fix this for yojiDraw
                    String correctAnswer = yojijukugoQuestions.get(currentyojijukugoQuestionIndex).getYojijukugo();
                    notificationTextView.setText(correctAnswer);
                    notificationTextView.setVisibility(View.VISIBLE);
                    currentyojijukugoQuestionIndex++;
                    yojiDrawCheckAnswer();

                }

            }
        });

        nextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String testType = getIntent().getStringExtra("test_type");

                //Fix this for yojiType
                if ("yojiType".equals(testType)) {
                    currentyojijukugoQuestionIndex++;
                    if (currentyojijukugoQuestionIndex <yojijukugoQuestions.size()) {
                        //checks if the current question number is less the the total questions
                        displayyojijukugoQuestion();//if it is less, then display the question
                    } else{
                        finish(); //End of test, display score or return to main menu
                    }
                //Fix this for yojiDraw
                } else if ("yojiDraw".equals(testType)) {
                    currentyojijukugoQuestionIndex++;
                    if (currentyojijukugoQuestionIndex < yojijukugoQuestions.size()) { //checks if the current question number is less the the total questions

                        displayYojiDrawQuesiton();
                        //if it is less, then display the question
                    } else {
                        finish();//End of test, display score or return to main menu
                    }

                }

            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Finish the activity to go back to the previous screen
                finish();
            }
        });

//Starts Tests
        if ("yojiType".equals(testType)) {
            //start yojiType test with 'questionCount' questions
            yojiDrawCanvas.setVisibility(View.GONE);
            answerEditText.setVisibility(View.VISIBLE);
            clearDrawingButton.setVisibility(View.GONE);
            startYojiTypeTest();

        } else if("yojiDraw".equals(testType)) {
            //start yojiDraw test with 'questionCount' questions
            answerEditText.setVisibility(View.GONE);
            yojiDrawCanvas.setVisibility(View.VISIBLE);
            startYojiDrawTest();
        }else{
            finish();
        }


    }
    public void startYojiTypeTest() {
        yojijukugoQuestions = questionDAO.getAllYojijukugoEntries();

        Collections.shuffle(yojijukugoQuestions); //randomize questions asked

        if (yojijukugoQuestions.size() > questionCount) {
            yojijukugoQuestions = yojijukugoQuestions.subList(0, questionCount);
        }
        currentyojijukugoQuestionIndex = 0;
        score = 0;
        displayyojijukugoQuestion();


    }

    public void startYojiDrawTest() {
        yojijukugoQuestions = questionDAO.getAllYojijukugoEntries();

        Collections.shuffle(yojijukugoQuestions); //randomize questions asked

        if (yojijukugoQuestions.size() > questionCount) {
            yojijukugoQuestions = yojijukugoQuestions.subList(0, questionCount);
        }
        currentyojijukugoQuestionIndex = 0;
        score = 0;
        displayYojiDrawQuesiton();//Start Yoji Draw Test


    }
//This function is for the Typing test. Shows yojijukugo, not reading
    public void displayyojijukugoQuestion() {

        yojijukugoQuestions currentQuestion = yojijukugoQuestions.get(currentyojijukugoQuestionIndex);
        Log.d("TestActivity", "Number of yojijukugo Questions" + yojijukugoQuestions.size());
        questionTextView.setText(currentQuestion.getYojijukugo());
        updateQuestionCounter();


    }


    public void displayYojiDrawQuesiton(){
        // Re-enable drawing for the next question

        notificationTextView.setText("");
        yojijukugoQuestions currentQuestion = yojijukugoQuestions.get(currentyojijukugoQuestionIndex);
        Log.d("TestActivity", "Number of yojijukugo Questions" + yojijukugoQuestions.size());
        questionTextView.setText(currentQuestion.getReading());
        updateQuestionCounter();

    }
//Check answer (for yojiType only)
    public void checkYojiTypeAnswer(String userAnswer) {
        String correctAnswer = yojijukugoQuestions.get(currentyojijukugoQuestionIndex).getReading();
        String[] yojijukugoArray = correctAnswer.split(",");

        boolean isCorrect = false;
        for(String reading : yojijukugoArray){
            if (userAnswer.trim().equals(reading.trim())){
                isCorrect = true;
                break;
            }
        }

        if (isCorrect) {
            answerEditText.setText("正解！"); //when the answer is right, update the ui element to show a 'right answer' message
            answerEditText.setFocusable(false);
            new Handler().postDelayed(() -> {
                answerEditText.setText("");
                answerEditText.setFocusableInTouchMode(true);
                answerEditText.setFocusable(true);
            }, 1000);

            yojijukugoWrongAnswersCount = 0;
            currentyojijukugoQuestionIndex++; //update index
            score++; //update score
        } else {
            answerEditText.setText("答えは間違っています。"); //when the answer is wrong, update the ui element to show a 'wrong answer' message
            answerEditText.setFocusable(false);
            new Handler().postDelayed(() -> {
                answerEditText.setText("");
                answerEditText.setFocusableInTouchMode(true);
                answerEditText.setFocusable(true);
            }, 1000);
            yojijukugoWrongAnswersCount++;

            if(yojijukugoWrongAnswersCount >= 3){
                //set the notification text view to show the correct answer and after some time make the text dissapear
                notificationTextView.setText("正解は「" + correctAnswer + "」です。");
                notificationTextView.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() ->{
                            notificationTextView.setVisibility(View.GONE);
                        },5000
                );
            }
        }

        if (currentyojijukugoQuestionIndex < yojijukugoQuestions.size()) {
            displayyojijukugoQuestion();
        } else {
            answerEditText.setText("試験を終了しました。"); //when the test is finshed, show a "finished message" and return to title screen
            answerEditText.setFocusable(false);
            new Handler().postDelayed(() -> {
                answerEditText.setText("");
                answerEditText.setFocusableInTouchMode(true);
                answerEditText.setFocusable(true);
            }, 5000);

        }
    }
    // Implement getUserDrawingStrokes to convert user's drawing from yojiDrawCanvas to List<List<Float>>
    private List<List<Float>> getUserDrawingStrokes() {
        // This depends on how you're storing the user's drawing data
        List<List<Float>> processedStrokes = new ArrayList<>();
        List<List<Float>> rawStrokes = yojiDrawCanvas.getStrokes();

        float canvasWidthPx = dpToPx(300);// Assuming 300dp is the width
        float canvasHeightPx = dpToPx(380);// Assuming 380dp is the height

        for (List<Float> stroke : rawStrokes) {
            //Simply stroke to two poitns (start and end)
            //This is basic example; may need a more sophisticated approach
            if (stroke.size() >= 4) { //ensure at least 2 points
                float x1 = stroke.get(0);
                float y1 = stroke.get(1);
                float x2 = stroke.get(stroke.size() - 2);
                float y2 = stroke.get(stroke.size() - 1);
                processedStrokes.add(Arrays.asList(x1, y1, x2, y2));
                // Normalize to 0-255 range
                float normalizedX1 = (x1 / canvasWidthPx) * 255;
                float normalizedY1 = (y1 / canvasHeightPx) * 255;
                float normalizedX2 = (x2 / canvasWidthPx) * 255;
                float normalizedY2 = (y2 / canvasHeightPx) * 255;
                processedStrokes.add(Arrays.asList(normalizedX1, normalizedY1, normalizedX2, normalizedY2));
            }

        }
        return processedStrokes;
    }

    public void yojiDrawCheckAnswer() {
        List<List<Float>> userStrokes = getUserDrawingStrokes(); // Implement to retrieve user's drawing
        String result = pythonInterpreter.analyzeDrawing(userStrokes);

        runOnUiThread(() -> {
            if (result != null && !result.isEmpty()) {
                //Show the result in notificationTextView or handle as needed
                notificationTextView.setText(result);
            } else {
                notificationTextView.setText("No matches found or error in analysis");
            }
        });
    }


    //This updates the counter each time the next question appears
    private void updateQuestionCounter() {
        String testType = getIntent().getStringExtra("test_type");
        if ("yojiType".equals(testType)) {
            //Update question counter view for yojiType

            questionCounterTextView.setText("質問 " + (currentyojijukugoQuestionIndex + 1) + "/" + questionCount);
        }else if("yojiDraw".equals(testType)) {
           //update question counter view for yojiDraw

            questionCounterTextView.setText("質問 " + (currentyojijukugoQuestionIndex + 1) + "/" + questionCount);
        }else{
            finish();
        }

    }


    public static class drawingView extends View{



        private List<List<Float>> strokes = new ArrayList<>(); //Stores all strokes
        private List<Float> currentStroke = new ArrayList<>(); //Stores points of the current stroke


        private Path drawPath;
        private Paint drawPaint, canvasPaint;
        private Canvas drawCanvas;
        private Bitmap canvasBitmap;
        private boolean isDrawingEnabled = true;

        public drawingView(Context context, AttributeSet attrs) {

            super(context, attrs);
            setupDrawing();
            strokes = new ArrayList<List<Float>>();


        }
        private void setupDrawing() {
            drawPath = new Path();
            drawPaint = new Paint();
            drawPaint.setColor(Color.BLACK);
            drawPaint.setAntiAlias(true);
            drawPaint.setStrokeWidth(10);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setStrokeJoin(Paint.Join.ROUND);
            drawPaint.setStrokeCap(Paint.Cap.ROUND);

            setOnTouchListener((view, motionEvent) -> {
                if (!isDrawingEnabled) return false;

                float touchX = motionEvent.getX();
                float touchY = motionEvent.getY();

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        drawPath.moveTo(touchX, touchY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        drawPath.lineTo(touchX, touchY);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (drawCanvas != null) {
                            drawCanvas.drawPath(drawPath, drawPaint);
                        }
                        drawPath.reset();
                        performClick();
                        break;
                    default:
                        return false;
                }

                invalidate();
                return true;
            });
        }


        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            drawCanvas = new Canvas(canvasBitmap);
        }




        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(canvasBitmap, 0, 0, null);
            canvas.drawPath(drawPath, drawPaint);
        }



        @Override
        public boolean onTouchEvent(MotionEvent event) {

            float touchX = event.getX();
            float touchY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    startNewStroke(touchX, touchY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    // ... handle action move ...
                    continueStroke(touchX, touchY);
                    break;
                case MotionEvent.ACTION_UP:
                    finishStroke();
                    break;
                default:
                    return false;
                    }
                    //drawPath.reset();
                   // performClick();  // Call this method here
                  //  break;
              //  default:
                  //  return falsee

            invalidate();
            return true;
        }

        private void startNewStroke(float x, float y){
            currentStroke = new ArrayList<>();
            currentStroke.add(x);
            currentStroke.add(y);
        }

        private void continueStroke(float x, float y){
            currentStroke.add(x);
            currentStroke.add(y);
        }

        private void finishStroke(){
            if (!currentStroke.isEmpty()){
                strokes.add(new ArrayList<>(currentStroke));
            }
            currentStroke.clear();
        }

        //Capture stroke input

        public List<List<Float>> getStrokes() {
            // Implement logic to extract strokes from the drawing
            // and convert them into a list of points (x, y).
            // Example: [[x1, y1, x2, y2], [x3, y3, x4, y4], ...]
            return strokes;
        }

        public void clearDrawing() {

            strokes.clear();
            currentStroke.clear();


            drawPath.reset();
            if (drawCanvas != null) {
                drawCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            }
            invalidate();
        }



        @Override
        public boolean performClick() {
            // Calls the super implementation, which generates an AccessibilityEvent
            // and calls the OnClickListener (if any)
            return super.performClick();
        }

        public void freezeDrawing() {
            isDrawingEnabled = false;
        }

        public void enableDrawing() {
            isDrawingEnabled = true;
        }





    }

//Closes database
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (yojijukugoDBHelper != null) {
            yojijukugoDBHelper.close();
        }
    }
}