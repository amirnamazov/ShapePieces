package com.example.logictest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

public class ShapePieces extends View {

    int nSides, rot;

    int total = Options.diffLevel + 10;
    int numbDiffPieces = new Random().nextInt((int) (total / 3.) + 1);
    int numberOfPieces = total - numbDiffPieces;

    int [] diffPieces;

    double [] pOut;
    double [] pIn;
    double [] ratio;

    double [] pOut2;
    double [] pIn2;
    double [] ratio2;

    Path shape;
    Point [] pointShape;
    float bottomMargin;

    Path [] pieces1;
    Point [][] pointPieces1;

    Path [] pieces2;
    Point [][] pointPieces2;

    Path [] pieces;
    RectF [] rect;
    Point [][] pointPieces;
    RectF [][] rectPieces;

    Paint paintFrame, mTextPaint;
    Paint [] paints, palePaints;
    int mPadding=5;

    Context context;
    View view;

    public ShapePieces(Context context) {
        super(context);

        this.context = context;
        this.view = this;

        do {
            nSides = new Random().nextInt(48)+3;
        }
        while (nSides > 12 && nSides < 50);

        rot = new Random().nextInt(2);

        pOut = new double[numberOfPieces];
        pIn = new double[new Random().nextInt(15) + 3];
        ratio = new double[pIn.length];

        pOut2 = new double[numberOfPieces];
        pIn2 = new double[pIn.length];
        ratio2 = new double[pIn.length];

        initializeValues(pOut, pIn, ratio);
        initializeValues(pOut2, pIn2, ratio2);

        ArrayList<Integer> random = new ArrayList<>();
        for (int i = 0; i < pOut2.length; i++) { random.add(i); }

        diffPieces = new int[numbDiffPieces];
        for (int i = 0; i < diffPieces.length; i++) {
            diffPieces[i] = random.get(new Random().nextInt(random.size()));
            random.remove(random.indexOf(diffPieces[i]));
        }

        int [] red = new int[total];
        int [] green = new int[total];
        int [] blue = new int[total];
        paints = new Paint[total];
        palePaints = new Paint[total];

        for (int i = 0; i < paints.length; i++) {
            red[i] = new Random().nextInt(170);
            green[i] = new Random().nextInt(170);
            blue[i] = new Random().nextInt(170);

            paints[i] = new Paint();
            paints[i].setAntiAlias(true);
            paints[i].setStyle(Paint.Style.FILL);
            paints[i].setColor(Color.rgb(red[i], green[i], blue[i]));

            palePaints[i] = new Paint();
            palePaints[i].setAntiAlias(true);
            palePaints[i].setStyle(Paint.Style.FILL);
            palePaints[i].setColor(Color.rgb(red[i] + 86, green[i] + 86, blue[i] + 86));
        }

        paintFrame = new Paint();
        paintFrame.setAntiAlias(true);
        paintFrame.setStyle(Paint.Style.STROKE);
        paintFrame.setColor(Color.BLACK);
        paintFrame.setStrokeWidth(5);

        /*mTextPaint = new Paint(Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.RED);
        mTextPaint.setTextSize(40);*/
    }


    @SuppressLint({"WrongViewCast", "DrawAllocation"})
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        RectF frame = new RectF(mPadding, mPadding, getWidth() - mPadding, getHeight() - mPadding);
        paintFrame.setColor(Color.BLACK);
        canvas.drawRect(frame, paintFrame);

        final double cX = getWidth() / 2, cY = getHeight() / 4;
        double percent = 90;

        ///////////////////////////////////////////

        if (answered){

            paintFrame.setColor(Color.LTGRAY);

            for (int i = 0; i < pieces.length; i++) {
                canvas.drawPath(pieces[i], palePaints[i]);
            }

            MainTest.LLProblem.removeView(MainTest.tvWellDone);
            MainTest.tvWellDone.setText("WELL DONE!");
            MainTest.LLProblem.addView(MainTest.tvWellDone);
            MainTest.imbtnMenu.setClickable(false);

        }

        else if (MainTest.visible || timeOver){

            paintFrame.setColor(Color.LTGRAY);

            for (int i = 0; i < touchedNumb.size(); i++){
                canvas.drawPath(pieces[touchedNumb.get(i)], palePaints[touchedNumb.get(i)]);
            }
        }

        else {

            MainTest.imbtnMenu.setClickable(true);

            if (!touched) {

                MainTest.pauseEnd(false, "NEXT");

                boolean exceeds;
                do {

                    exceeds = false;

                    double radius = cY * percent / 100;

                    shape = new Path();
                    pointShape = new Point[nSides];
                    DrawShape.regularPolygon(shape, pointShape, nSides, radius, cX, cY, rot);

                    pieces1 = new Path[pOut.length];
                    pointPieces1 = new Point[pOut.length][];
                    DrawShape.arrangePieces(pieces1, pointPieces1, pOut, pIn, ratio, nSides, cX, cY, radius, rot);

                    pieces2 = new Path[pOut2.length];
                    pointPieces2 = new Point[pOut2.length][];
                    DrawShape.arrangePieces(pieces2, pointPieces2, pOut2, pIn2, ratio2, nSides, cX, cY, radius, rot);

                    pieces = new Path[total];
                    pointPieces = new Point[total][];
                    {
                        for (int i = 0; i < pieces1.length; i++) {
                            pieces[i] = pieces1[i];
                            pointPieces[i] = new Point[pointPieces1[i].length];
                            for (int j = 0; j < pointPieces1[i].length; j++) {
                                pointPieces[i][j] = new Point(pointPieces1[i][j]);
                            }
                        }

                        for (int i = pieces2.length; i < pieces2.length + diffPieces.length; i++) {
                            pieces[i] = pieces2[diffPieces[i - pieces2.length]];
                            pointPieces[i] = pointPieces2[diffPieces[i - pieces2.length]];
                        }
                    }

                    bottomMargin = 0;
                    for (int i = 0; i < pointShape.length; i++) {
                        if (pointShape[i].y > bottomMargin) bottomMargin = pointShape[i].y;
                    }

                    rect = new RectF[pieces.length];
                    rectPieces = new RectF[pieces.length][];
                    DrawShape.locatePieces(canvas, rect, rectPieces, pieces, pointPieces, bottomMargin + 20);

                    for (int i = 0; i < rect.length; i++) {
                        if (rect[i].bottom >= canvas.getHeight() - 20) {

                            initializeValues(pOut, pIn, ratio);
                            initializeValues(pOut2, pIn2, ratio2);

                            exceeds = true;
                            percent-=10;
                            if (percent < 50) percent = 90;
                            break;
                        }
                    }
                }
                while (exceeds);

                for (int i = 0; i < pieces.length; i++) {
                    canvas.drawPath(pieces[i], paints[i]);
                    touchedNumb.add(i);
                }

                dx = new float[pointPieces.length][];
                dy = new float[pointPieces.length][];

                for (int i = 0; i < pointPieces.length; i++) {
                    dx[i] = new float[pointPieces[i].length];
                    dy[i] = new float[pointPieces[i].length];

                    for (int j = 0; j < pointPieces[i].length - 1; j++) {

                        dx[i][j] = pointPieces[i][j+1].x - pointPieces[i][j].x;
                        dy[i][j] = pointPieces[i][j+1].y - pointPieces[i][j].y;
                    }
                }

                MainTest.chronometer.setBase(SystemClock.elapsedRealtime() + Options.timeLimit);
                MainTest.chronometer.start();
                if (Options.timeLimit != 0){
                    MainTest.chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                        @Override
                        public void onChronometerTick(Chronometer chronometer) {
                            if (Math.abs(SystemClock.elapsedRealtime() + Options.timeLimit
                                    - chronometer.getBase()) >= Options.timeLimit){
                                chronometer.stop();
                                timeOver = true;

                                MainTest.LLProblem.removeView(view);
                                MainTest.LLProblem.addView(view);
                                MainTest.pauseEnd(true, "NEXT");
                                MainTest.LLProblem.removeView(MainTest.tvWellDone);
                                MainTest.tvWellDone.setText("TIME OVER!");
                                MainTest.LLProblem.addView(MainTest.tvWellDone);
                                MainTest.imbtnMenu.setClickable(false);
                            }
                        }
                    });
                }

                touched = true;
            }

            else {

                boolean found = false;

                if (pointerActive){
                    for (int j = 0; j < pointerArea.size(); j++) {
                        if (pointerArea.get(j).contains(initialX, initialY)){
                            found = true;
                            break;
                        }
                    }
                    pointerActive = false;
                }

                if (!found){
                    for (int i = touchedNumb.size() - 1; i >= 0; i--) {
                        for (int j = 0; j < rectPieces[touchedNumb.get(i)].length; j++) {

                            if (rectPieces[touchedNumb.get(i)][j].contains(initialX, initialY)) {
                                touchedNumber = touchedNumb.get(i);
                                found = true;
                                break;
                            }
                        }
                        if (found) break;
                    }
                }

                if (found){

                    int i = touchedNumber;

                    if (ACTION_DOWN) {

                        measuredDX = pointPieces[i][0].x - initialX;
                        measuredDY = pointPieces[i][0].y - initialY;
                    }

                    else if (ACTION_MOVE) {

                        pieces[i] = new Path();
                        pointPieces[i][0].x = (int) (X + measuredDX);
                        pointPieces[i][0].y = (int) (Y + measuredDY);
                        pieces[i].moveTo(pointPieces[i][0].x, pointPieces[i][0].y);

                        for (int k = 1; k < pointPieces[i].length; k++) {
                            pointPieces[i][k].x = (int) (pointPieces[i][k-1].x + dx[i][k-1]);
                            pointPieces[i][k].y = (int) (pointPieces[i][k-1].y + dy[i][k-1]);

                            pieces[i].lineTo(pointPieces[i][k].x, pointPieces[i][k].y);
                        }
                        pieces[i].close();
                    }

                    else if (ACTION_UP) {
                        DrawShape.surroundWithRect(canvas, pointPieces, rect, i);
                        DrawShape.drawInnerRects(pointPieces, rect, rectPieces, i);

                        pointerArea = new Pointer(pointPieces[i], 250).innerRects;
                    }

                    Pointer pointer = new Pointer(pointPieces[i], 250);
                    canvas.drawPath(pointer, paintFrame);
                    canvas.clipPath(pointer, Region.Op.DIFFERENCE);
                    pointerActive = true;
                }

                touchedNumb.remove(touchedNumb.indexOf(touchedNumber));
                touchedNumb.add(touchedNumber);
                for (int i = 0; i < touchedNumb.size(); i++){
                    canvas.drawPath(pieces[touchedNumb.get(i)], paints[touchedNumb.get(i)]);
                }

                boolean contains = true;
                final float minDist = 40;

                for (int i = 0; i < pointPieces1.length; i++) {

                    if (Math.abs(pointPieces[i][0].x - pointPieces1[i][0].x) > minDist
                            || Math.abs(pointPieces[i][0].y - pointPieces1[i][0].y) > minDist){
                        contains = false; break;
                    }
                }

                if (contains){

                    answered = true;
                    MainTest.pauseEnd(true, "NEXT");

                    for (int i = 0; i < pointPieces1.length; i++) {

                        pieces[i].offset((pointPieces1[i][0].x - pointPieces[i][0].x),
                                (pointPieces1[i][0].y - pointPieces[i][0].y));

                        for (int j = 0; j < pointPieces1[i].length; j++) {
                            pointPieces[i][j] = new Point(pointPieces1[i][j]);
                        }
                    }

                    MainTest.chronometer.stop();

                    MainTest.LLProblem.removeView(MainTest.tvTime);

                    long millis = (SystemClock.elapsedRealtime() + Options.timeLimit - MainTest.chronometer.getBase());

                    try {
                        BestResultDB db = new BestResultDB(context);
                        db.open();
                        String s = db.getData(Options.diffLevel);
                        if (!s.isEmpty()){
                            if (millis < Long.parseLong(s))
                                db.updateEntry(String.valueOf(Options.diffLevel), String.valueOf(millis));
                        }
                        else db.updateEntry(String.valueOf(Options.diffLevel), String.valueOf(millis));
                        db.close();
                    }
                    catch (SQLException e){
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    MainTest.tvTime.setText("TIME: " + Options.getTime(millis));
                    MainTest.LLProblem.addView(MainTest.tvTime);
                }
            }
        }

        canvas.drawPath(shape, paintFrame);
    }

    float X, Y;
    boolean answered = false, timeOver = false;
    boolean touched = false, ACTION_DOWN = false, ACTION_MOVE = false, ACTION_UP = false;

    float initialX;
    float initialY;

    float [][] dx, dy;
    float measuredDX, measuredDY;

    ArrayList<RectF> pointerArea = new ArrayList<>();
    boolean pointerActive = false;
    int touchedNumber;
    ArrayList<Integer> touchedNumb = new ArrayList<>();

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                initialX = event.getX();
                initialY = event.getY();

                touched = true;
                ACTION_DOWN = true;
                ACTION_MOVE = false;
                ACTION_UP = false;
                break;

            case MotionEvent.ACTION_MOVE:

                X = event.getX();
                Y = event.getY();

                touched = true;
                ACTION_DOWN = false;
                ACTION_MOVE = true;
                ACTION_UP = false;
                break;

            case MotionEvent.ACTION_UP:
                touched = true;
                ACTION_DOWN = false;
                ACTION_MOVE = false;
                ACTION_UP = true;
                break;
        }
        invalidate();
        return true;
    }

    private void initializeValues(double [] pOut, double [] pIn, double [] ratio){

        double angle = Math.random() * 60 + 1;
        double bound1 = 0, bound2 = angle + (361 - angle)/pOut.length;

        for (int j = 0; j < pOut.length; j++) {
            pOut[j] = new Random().nextInt((int) bound2) + bound1;

            if (j == 0) bound2 = angle;
            else bound2 -= (pOut[j] - bound1);

            bound1 = pOut[j] + (361 - angle)/pOut.length;

            if (pOut[j] >= 360) pOut[j] -= 360;
        }

        for (int j = 0; j < pOut.length; j++) {
            double pOut1;
            for (int k = j+1; k < pOut.length; k++) {
                if (pOut[j] > pOut[k]){
                    pOut1 = pOut[j];
                    pOut[j] = pOut[k];
                    pOut[k] = pOut1;
                }
            }
        }

        angle = Math.random() * 60 + 1;
        bound1 = 0; bound2 = angle + (361 - angle)/pIn.length;

        for (int j = 0; j < pIn.length; j++) {
            pIn[j] = new Random().nextInt((int) bound2) + bound1;

            if (j == 0) bound2 = angle;
            else bound2 -= (pIn[j] - bound1);

            bound1 = pIn[j] + (361 - angle)/pIn.length;

            if (pIn[j] >= 360) pIn[j] -= 360;

            ratio[j] = (double) (new Random().nextInt(61)+15)/100;
        }

        for (int j = 0; j < pIn.length; j++) {
            double pIn1;
            for (int k = j+1; k < pIn.length; k++) {
                if (pIn[j] > pIn[k]){
                    pIn1 = pIn[j];
                    pIn[j] = pIn[k];
                    pIn[k] = pIn1;
                }
            }
        }
    }
}