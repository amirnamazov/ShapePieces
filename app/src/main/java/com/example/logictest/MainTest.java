package com.example.logictest;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

public class MainTest extends Activity {

    ProgressBar progressView;
    View mainView;

    public static RelativeLayout LLProblem;
    TextView tvDiffLevel;
    View shapePieces;

    public static ImageButton imbtnMenu;
    public static TextView tvTime, tvWellDone;
    private static Button btnResume, btnChangeNext, btnMainMenu;

    public static Chronometer chronometer;
    private long pauseOffset;
    private boolean running = true;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);

        progressView = findViewById(R.id.progressBar);
        mainView = findViewById(R.id.main_view);

        Drawable progressDrawable = progressView.getIndeterminateDrawable().mutate();
        progressDrawable.setColorFilter(getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        progressView.setProgressDrawable(progressDrawable);

        LLProblem = findViewById(R.id.LLProblem);
        chronometer = findViewById(R.id.chronometer);
        tvDiffLevel = findViewById(R.id.tvDifflevel);
        tvWellDone = findViewById(R.id.tvWellDone);
        tvTime = findViewById(R.id.tvTime);

        tvDiffLevel.setText("LEVEL " + Options.diffLevel);

        btnMainMenu = findViewById(R.id.btnMainMenu);
        btnResume = findViewById(R.id.btnResumeChall);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Options.timeLimit != 0) {
            chronometer.setCountDown(true);
        }

        btnChangeNext = findViewById(R.id.btnChangeNext);
        btnChangeNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValueAnimator animator = ValueAnimator.ofInt(0, progressView.getMax());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation){
                        showProgress(true);

                        LLProblem.removeAllViews();
                        shapePieces = new ShapePieces(MainTest.this);
                        LLProblem.addView(shapePieces);
                        LLProblem.addView(btnResume);
                        LLProblem.addView(btnChangeNext);
                        LLProblem.addView(btnMainMenu);
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        showProgress(false);
                    }
                });
                animator.start();

                running = true;
                visible = false;
                pauseEnd(visible, "CHANGE");
            }
        });
        btnChangeNext.callOnClick();


        imbtnMenu = findViewById(R.id.imbtnMenu);
        imbtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!visible) {
                    visible = true;
                    pauseChronometer();
                }
                else {
                    visible = false;
                    startChronometer();
                }

                LLProblem.removeView(shapePieces);
                LLProblem.addView(shapePieces);
                pauseEnd(visible, "CHANGE");
            }
        });

        btnResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visible = false;
                startChronometer();
                LLProblem.removeView(shapePieces);
                LLProblem.addView(shapePieces);
                pauseEnd(visible, "CHANGE");
            }
        });

        btnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static boolean visible = false;

    public static void pauseEnd(boolean show, String text){
        if (text == "CHANGE") btnResume.setVisibility(show ? View.VISIBLE : View.GONE);

        btnChangeNext.setText(text);
        btnChangeNext.setVisibility(show ? View.VISIBLE : View.GONE);
        btnMainMenu.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            mainView.setVisibility(show ? View.GONE : View.VISIBLE);
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        else {
            mainView.setVisibility(show ? View.GONE : View.VISIBLE);
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void startChronometer(){
        if (!running){
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            running = true;
        }
    }

    private void pauseChronometer(){
        if (running){
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            running = false;
        }
    }
}
