package edu.example.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    public static final String EXTRA_ANSWER_IS_TRUE = "edu.example.geoquiz.answer_is_true";
    public static final String EXTRA_ANSWER_SHOWN = "edu.example.geoquiz.answer_show";
    public static final String SHOWED_ANSWER = "showed";

    private boolean answerIsTrue;
    private TextView tvAnswer;
    private Button btnShowAnswer;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        tvAnswer = findViewById(R.id.tvAnswer);
        btnShowAnswer = findViewById(R.id.btnShowAnswer);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(SHOWED_ANSWER)) {
                setAnswerShowResult(true);
                tvAnswer.setText(savedInstanceState.getString(SHOWED_ANSWER));
            }
        } else {
            answerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        }

        btnShowAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (answerIsTrue) {
                    tvAnswer.setText(R.string.trueBtn);
                } else {
                    tvAnswer.setText(R.string.falseBtn);
                }
                setAnswerShowResult(true);

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
    }

    private void setAnswerShowResult(boolean isAnswerShow) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShow);
        setResult(RESULT_OK, data);
    }
}
