package edu.example.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener
{

    private Button btnTrue, btnFalse, btnCheat;
    private ImageButton btnNext, btnPrev;
    private TextView tvQuestion, tvCheatsLeft;
    private Question[] questionBank;
    private int currentIndex, cheatsQuantityLeft;

    public static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String CLICKED_BTN = "clicked";
    private static final String CHEATER = "cheater";
    private static final String TRY = "try";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final int REQUEST_CODE_LEFT = 1;
    private boolean isCheated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLog("onCreate");
        setContentView(R.layout.quiz_main);

        currentIndex = 0;
        cheatsQuantityLeft = 3;

        btnTrue = findViewById(R.id.btnTrue);
        btnFalse = findViewById(R.id.btnFalse);
        btnCheat = findViewById(R.id.btnCheat);
        btnNext = findViewById(R.id.btnNext);
        btnPrev = findViewById(R.id.btnPrev);
        tvQuestion = findViewById(R.id.tvQuestion);
        tvCheatsLeft = findViewById(R.id.tvCheatsLeft);

        if (savedInstanceState != null) {
            currentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            if (savedInstanceState.containsKey(CLICKED_BTN)) {
                btnFalse.setEnabled(savedInstanceState.getBoolean(CLICKED_BTN));
                btnTrue.setEnabled(savedInstanceState.getBoolean(CLICKED_BTN));
            }
            if (savedInstanceState.containsKey(TRY)) {
                cheatsQuantityLeft = savedInstanceState.getInt(TRY);
            }
            if (savedInstanceState.containsKey(CHEATER)) {
                isCheated = true;
            }
        }

        setTvCheatsLeft(cheatsQuantityLeft);

        btnFalse.setOnClickListener(this);
        btnTrue.setOnClickListener(this);
        btnCheat.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPrev.setOnClickListener(this);
        tvQuestion.setOnClickListener(this);

        makeQuestionsArray();
        updateQuestion();
    }

    private void setTvCheatsLeft(int cheatsQuantityLeft) {
        tvCheatsLeft.setText(getResources().getString(R.string.left) + cheatsQuantityLeft);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showLog("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        showLog("onResume");
        if (cheatsQuantityLeft == 0) {
            btnCheat.setEnabled(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        showLog("onPause");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, currentIndex);
        outState.putInt(TRY, cheatsQuantityLeft);
        if (!btnTrue.isEnabled()) {
            outState.putBoolean(CLICKED_BTN, false);
        }
        if (isCheated) {
            outState.putBoolean(CHEATER, true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        showLog("onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        showLog("onDestroy");
    }

    private void makeQuestionsArray() {
        questionBank = new Question[] {
                new Question(R.string.question_australia, true, 0),
                new Question(R.string.question_oceans, true, 0),
                new Question(R.string.question_mideast, false, 0),
                new Question(R.string.question_africa, false, 0),
                new Question(R.string.question_americas, true, 0),
                new Question(R.string.question_asia, true, 0),
        };
    }

    @Override
    public void onClick(View v){
        switch (v.getId())
        {
            case R.id.btnTrue:
                checkAnswer(true);
                break;
            case R.id.btnFalse:
                checkAnswer(false);
                break;
            case R.id.btnCheat:
                Intent intent = CheatActivity.newIntent(QuizActivity.this, questionBank[currentIndex].isAnswerTrue(), cheatsQuantityLeft);
                startActivityForResult(intent,REQUEST_CODE_CHEAT);
                /*if (cheatsQuantityLeft>0) {
                    cheatsQuantityLeft--;
                    setTvCheatsLeft(cheatsQuantityLeft);
                    if (cheatsQuantityLeft==0) {
                        btnCheat.setEnabled(false);
                    }
                }*/
                break;
            case R.id.btnNext:
                nextQuestion(currentIndex);
                break;
            case R.id.btnPrev:
                prevQuestion(currentIndex);
                break;
            case R.id.tvQuestion:
                nextQuestion(currentIndex);
                break;
        }
    }

    private void prevQuestion(int currentIndex) {
        if (currentIndex != 0) {
            this.currentIndex--;
        }
        updateQuestion();
    }

    private void nextQuestion(int currentIndex) {
        if (btnFalse.isEnabled() || btnTrue.isEnabled()) {
            Toast.makeText(this, getString(R.string.answer_the_question), Toast.LENGTH_SHORT).show();
        } else {
            this.currentIndex = (currentIndex + 1) % questionBank.length;
            btnTrue.setEnabled(true);
            btnFalse.setEnabled(true);
            if (currentIndex + 1 == questionBank.length) {
                calculatePercent();
            }
            isCheated = false;
            updateQuestion();
        }
    }

    private void calculatePercent() {
        int rightAnswers = 0;
        for(int i=0;i<questionBank.length;i++) {
            rightAnswers += questionBank[i].getAnswer();
        }
        int percent = (rightAnswers * 100) / questionBank.length;
        Toast.makeText(this, getResources().getString(R.string.result) +
                                          String.valueOf(percent) +
                                          getResources().getString(R.string.percent_symbol), Toast.LENGTH_SHORT).show();
        cheatsQuantityLeft = 3;
        setTvCheatsLeft(cheatsQuantityLeft);
    }

    private void updateQuestion(){
        tvQuestion.setText(questionBank[currentIndex].getTextResId());
    }


    private void checkAnswer(boolean isTrue) {
        if (isCheated) {
            Toast.makeText(this, R.string.judgment_toast,Toast.LENGTH_LONG).show();
        } else {
            if (isTrue == questionBank[currentIndex].isAnswerTrue()) {
                Toast.makeText(this, R.string.trueBtn,Toast.LENGTH_LONG).show();
                questionBank[currentIndex].setAnswer(1);
            } else {
                Toast.makeText(this,R.string.falseBtn,Toast.LENGTH_LONG).show();
            }
        }
        btnTrue.setEnabled(false);
        btnFalse.setEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            isCheated = CheatActivity.wasAnswerShown(data);
            cheatsQuantityLeft = CheatActivity.countCheats(data);
            setTvCheatsLeft(cheatsQuantityLeft);
            showLog("onActivityResult");
        }
    }

    public void showLog(String str) {
        Log.d("tag", "HERE " + str);
    }

}
