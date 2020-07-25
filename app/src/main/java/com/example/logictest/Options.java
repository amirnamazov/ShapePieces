package com.example.logictest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class Options extends AppCompatActivity {

    public static int diffLevel = 1;
    public static long timeLimit = 0;
    private static int minutes = 0;
    private long [] defaultTimeLimit;
    private TextView tvDifficultyLevel, tvBest, tvTimeLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        tvDifficultyLevel = findViewById(R.id.tvDifficultyLevel);
        tvBest = findViewById(R.id.tvBest);
        tvTimeLimit = findViewById(R.id.tvTimeLimit);

        tvDifficultyLevel.setText("LEVEL " + diffLevel);

        getBest();

        defaultTimeLimit = new long[20];
        for (int i = 0; i < defaultTimeLimit.length; i++) {
            defaultTimeLimit[i] = (60 + i * 15) * 1000;
        }

        checkMinutes();
    }

    public void btnLeft1(View view){
        if (diffLevel > 1){
            diffLevel--;
            tvDifficultyLevel.setText("LEVEL " + diffLevel);
            getBest();
            checkMinutes();
        }
    }

    public void btnRight1(View view){
        if (diffLevel < 20){
            diffLevel++;
            tvDifficultyLevel.setText("LEVEL " + diffLevel);
            getBest();
            checkMinutes();
        }
    }

    public void btnLeft2(View view){
        if (minutes > 0){
            minutes--;
            checkMinutes();
        }
    }

    public void btnRight2(View view){
        if (minutes < 31){
            minutes++;
            checkMinutes();
        }
    }

    private void checkMinutes(){
        switch (minutes){
            case 0:
                tvTimeLimit.setText("INFINITE");
                timeLimit = 0;
                break;
            case 1:
                tvTimeLimit.setText("DEFAULT");
                timeLimit = defaultTimeLimit[diffLevel - 1] + 1000;
                break;
            default:
                tvTimeLimit.setText(minutes - 1 + " MINUTE");
                timeLimit = (minutes - 1) * 60000 + 1000;
                break;
        }
    }

    private void getBest(){
        try {
            BestResultDB db = new BestResultDB(this);
            db.open();
            String s = db.getData(diffLevel);
            if (!s.isEmpty()) tvBest.setText("BEST: " + getTime(Long.parseLong(s)));
            else tvBest.setText("");
            db.close();
        }
        catch (SQLException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static String getTime(long millis){

        return String.format("%01d:%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)),
                TimeUnit.MILLISECONDS.toMillis(millis) -
                        TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(millis)));
    }
}
