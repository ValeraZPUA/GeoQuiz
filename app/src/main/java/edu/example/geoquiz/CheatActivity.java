package edu.example.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    public static final String EXTRA_ANSWER_IS_TRUE = "edu.example.geoquiz.answer_is_true";
    public static final String EXTRA_ANSWER_SHOWN = "edu.example.geoquiz.answer_show";
    public static final String EXTRA_ANSWER_LEFT_CHEATS = "edu.example.geoquiz.left_cheats";
    public static final String EXTRA_ANSWER_LEFT= "edu.example.geoquiz.left";

    public static final String LEFT = "left";
    public static final String SHOWED_ANSWER = "showed";

    private boolean answerIsTrue;
    private TextView tvAnswer, tvAPIVersion;
    private Button btnShowAnswer;
    private int cheatsQuantityLeft;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue, int leftCheats) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(EXTRA_ANSWER_LEFT_CHEATS, leftCheats);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    public static int countCheats(Intent result) {
        return result.getIntExtra(EXTRA_ANSWER_LEFT,3);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        tvAnswer = findViewById(R.id.tvAnswer);
        tvAPIVersion = findViewById(R.id.tvAPIVersion);
        btnShowAnswer = findViewById(R.id.btnShowAnswer);

        tvAPIVersion.setText(getResources().getString(R.string.api_lvl) +  String.valueOf(Build.VERSION.SDK_INT));

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SHOWED_ANSWER)) {
                tvAnswer.setText(savedInstanceState.getString(SHOWED_ANSWER));
            } else {
                answerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
            }

            if (savedInstanceState.containsKey(LEFT)) {
                cheatsQuantityLeft = savedInstanceState.getInt(LEFT);
                Log.d("tag", "CheatActivity savedInstanceState " + cheatsQuantityLeft);
            } else {
                cheatsQuantityLeft = getIntent().getIntExtra(EXTRA_ANSWER_LEFT_CHEATS,3);
            }
            setAnswerShowResult(true, cheatsQuantityLeft);
            //if(tvAnswer.getText().equals(getResources().getString(R.string.falseBtn)) || tvAnswer.getText().equals(getResources().getString(R.string.trueBtn))) {
                btnShowAnswer.setVisibility(View.INVISIBLE);
            //}
        } else {
            answerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
            cheatsQuantityLeft = getIntent().getIntExtra(EXTRA_ANSWER_LEFT_CHEATS,1);
        }

        btnShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cheatsQuantityLeft--;
                if (answerIsTrue) {
                    tvAnswer.setText(R.string.trueBtn);
                } else {
                    tvAnswer.setText(R.string.falseBtn);
                }
                setAnswerShowResult(true, cheatsQuantityLeft);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = btnShowAnswer.getWidth() / 2;
                    int cy = btnShowAnswer.getHeight() / 2;
                    float radius = btnShowAnswer.getWidth();
                    Animator anim = ViewAnimationUtils
                            .createCircularReveal(btnShowAnswer, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            btnShowAnswer.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                } else {
                    btnShowAnswer.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SHOWED_ANSWER, tvAnswer.getText().toString());
        Log.d("tag", "CheatActivity onSaveInstanceState " + cheatsQuantityLeft);
        outState.putInt(LEFT, cheatsQuantityLeft);
    }

    private void setAnswerShowResult(boolean isAnswerShow, int cheatsQuantityLeft) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShow);
        data.putExtra(EXTRA_ANSWER_LEFT, cheatsQuantityLeft);
        setResult(RESULT_OK, data);
    }
}
