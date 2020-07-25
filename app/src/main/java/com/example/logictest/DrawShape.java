package com.example.logictest;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class DrawShape {

    public static void regularPolygon(Path shapes, Point [] pointShape, int nSides,
                                      double radius, double cX, double cY, int rot){

        double x, y, radian;

        for (int i = 0; i < nSides; i++) {

            radian = (Math.PI / nSides) * (2 * i + rot);
            x = cX + radius * Math.sin(radian);
            y = cY - radius* Math.cos(radian);
            pointShape[i] = new Point((int) x, (int) y);

            if (i==0){
                shapes.moveTo((float) x, (float) y);
            }

            else {
                shapes.lineTo((float) x, (float) y);
            }
        }
        shapes.close();

        float topMargin = 10000;
        for (int i = 0; i < pointShape.length; i++) {
            if (pointShape[i].y < topMargin) topMargin = pointShape[i].y;
        }

        while (topMargin >= 30){
            shapes.offset(0, -5);
            for (int i = 0; i < pointShape.length; i++) {
                pointShape[i].offset(0, -5);
            }
            topMargin-=5;
        }
    }


    public static void arrangePieces(Path [] pieces, Point [][] pointPieces,
                                     double [] pOut, double [] pIn, double [] ratio, int nSides, double cX, double cY,
                                     double radius, int rot){

        double x, y;
        double [] pShape = new double[nSides];
        Point [] pointShape = new Point[nSides];
        for (int i = 0; i < nSides; i++) {
            pShape[i] = (Math.PI / nSides) * (2 * i + rot);
            x = cX + radius * Math.sin(pShape[i]);
            y = cY - radius * Math.cos(pShape[i]);
            pointShape[i] = new Point((int) x, (int) y);
        }

        Point [] pointOut = new Point[pOut.length];
        double [] rat = new double[pOut.length];
        for (int i = 0; i < pOut.length; i++) { rat[i] = 1; }
        arrangePoints(pOut, pointOut, rat, nSides, radius, rot, cX, cY);


        Point [] pointIn = new Point[pIn.length];
        arrangePoints(pIn, pointIn, ratio, nSides, radius, rot, cX, cY);

        ArrayList<Point> points = new ArrayList<>();
        boolean bool = false;
        int rand = 0;

        int [] countPointIn = new int[pOut.length];
        for (int i = 0; i < pOut.length; i++) {
            countPointIn[i] = 0;
        }

        for (int i = 0; i <= pOut.length; i++) {

            if (bool) i = rand;

            pieces[i] = new Path();
            pieces[i].moveTo((float) pointOut[i].x, (float) pointOut[i].y);
            points.clear();
            points.add(new Point(pointOut[i].x, pointOut[i].y));

            double [] d = new double[pIn.length];
            double min = 2 * radius;
            int numb1 = 0;
            for (int j = 0; j < pIn.length; j++) {
                d[j] = Math.sqrt(Math.pow((pointOut[i].x - pointIn[j].x), 2) +
                        Math.pow((pointOut[i].y - pointIn[j].y), 2));

                if (d[j] < min){ min = d[j]; numb1 = j;}
            }

            int n;
            if (i+1 != pOut.length){
                for (int j = 0; j < nSides; j++) {
                    if (pShape[j] > Math.toRadians(pOut[i] + 180 / nSides * rot) &&
                            pShape[j] < Math.toRadians(pOut[i+1] + 180 / nSides * rot)){

                        pieces[i].lineTo((float) pointShape[j].x, (float) pointShape[j].y);
                        points.add(new Point(pointShape[j].x, pointShape[j].y));
                    }
                }
                n = i + 1;
            }

            else {
                for (int j = 0; j < nSides; j++) {
                    if (pShape[j] > Math.toRadians(pOut[i] + 180 / nSides * rot)){

                        pieces[i].lineTo((float) pointShape[j].x, (float) pointShape[j].y);
                        points.add(new Point(pointShape[j].x, pointShape[j].y));
                    }
                }
                for (int j = 0; j < nSides; j++) {
                    if (pShape[j] < Math.toRadians(pOut[0] + 180 / nSides * rot)){

                        pieces[i].lineTo((float) pointShape[j].x, (float) pointShape[j].y);
                        points.add(new Point(pointShape[j].x, pointShape[j].y));
                    }
                }
                n = 0;
            }

            pieces[i].lineTo((float) pointOut[n].x, (float) pointOut[n].y);
            points.add(new Point(pointOut[n].x, pointOut[n].y));

            min = 2 * radius;
            int numb2 = 0;
            for (int j = 0; j < pIn.length; j++) {
                d[j] = Math.sqrt(Math.pow((pointOut[n].x - pointIn[j].x), 2) +
                        Math.pow((pointOut[n].y - pointIn[j].y), 2));

                if (d[j] < min){ min = d[j]; numb2 = j; }
            }

            if (!bool){
                if (numb2 >= numb1){
                    for (int j = numb2; j >= numb1; j--) {
                        pieces[i].lineTo((float) pointIn[j].x, (float) pointIn[j].y);
                        points.add(new Point(pointIn[j].x, pointIn[j].y));
                        countPointIn[i]++;
                    }
                }

                else {
                    for (int j = numb2; j >= 0; j--) {
                        pieces[i].lineTo((float) pointIn[j].x, (float) pointIn[j].y);
                        points.add(new Point(pointIn[j].x, pointIn[j].y));
                        countPointIn[i]++;
                    }

                    for (int j = pIn.length-1; j >= numb1; j--) {
                        pieces[i].lineTo((float) pointIn[j].x, (float) pointIn[j].y);
                        points.add(new Point(pointIn[j].x, pointIn[j].y));
                        countPointIn[i]++;
                    }
                }
            }

            else { //COMBINE CENTER WITH A RANDOM PIECE

                if (numb2 > numb1){
                    for (int j = numb2; j < pIn.length; j++) {
                        pieces[i].lineTo((float) pointIn[j].x, (float) pointIn[j].y);
                        points.add(new Point(pointIn[j].x, pointIn[j].y));
                    }

                    for (int j = 0; j <= numb1; j++) {
                        pieces[i].lineTo((float) pointIn[j].x, (float) pointIn[j].y);
                        points.add(new Point(pointIn[j].x, pointIn[j].y));
                    }
                }

                else {
                    for (int j = numb2; j <= numb1; j++) {
                        pieces[i].lineTo((float) pointIn[j].x, (float) pointIn[j].y);
                        points.add(new Point(pointIn[j].x, pointIn[j].y));
                    }
                }

                pieces[i].close();

                pointPieces[i] = new Point[points.size()];
                for (int j = 0; j < points.size(); j++) {
                    pointPieces[i][j] = points.get(j);
                }

                break;
            }

            pieces[i].close();

            pointPieces[i] = new Point[points.size()];
            for (int j = 0; j < points.size(); j++) {
                pointPieces[i][j] = points.get(j);
            }

            if (i+1 == pOut.length && pIn.length >= 3){
                while (!bool){
                    rand = new Random().nextInt(pOut.length);
                    if (countPointIn[rand] > 1){
                        bool = true;
                    }
                }
            }

            if (i+1 == pOut.length && pIn.length < 3) break;
        }

        float topMargin = 10000;
        for (int i = 0; i < pointPieces.length; i++) {
            for (int j = 0; j < pointPieces[i].length; j++) {
                if (pointPieces[i][j].y < topMargin) topMargin = pointPieces[i][j].y;
            }
        }

        while (topMargin >= 30){
            for (int i = 0; i < pointPieces.length; i++) {
                pieces[i].offset(0, -5);
                for (int j = 0; j < pointPieces[i].length; j++) {
                    pointPieces[i][j].offset(0, -5);
                }
            }
            topMargin-=5;
        }
    }

    private static void arrangePoints(double [] point, Point [] Point, double [] ratio,
                                      int nSides, double radius, int rot, double cX, double cY){

        double x, y, radian;
        for (int i = 0; i < point.length; i++) {

            double p = point[i] * nSides / 180;
            radian = Math.PI / nSides * (p + rot);
            double radian2 = Math.PI / nSides * (p - ((int) p/2)*2);
            double r = radius * Math.cos(Math.PI/nSides) / Math.cos(Math.PI/nSides - radian2) * ratio[i];
            x = cX + r * Math.sin(radian);
            y = cY - r * Math.cos(radian);

            Point[i] = new Point((int) x, (int) y);
        }
    }



    public static void locatePieces(Canvas canvas, RectF[] rect, RectF [][] rectPieces, Path [] pieces,
                                    Point [][] pointPieces, float topMargin){


        Point [][] outerPoints = new Point[pieces.length][];

        for (int i = 0; i < pieces.length; i++) {
            surroundWithRect(canvas, pointPieces, rect, i);
            drawInnerRects(pointPieces, rect, rectPieces, i);
            drawFullOuterPoints(pointPieces, i, outerPoints, 5);
        }

        float space = 20, sumRow = 0, rowWidth = canvas.getWidth() - 10, maxHeight = 0, sumMaxHeight = 0;
        int row = 0, col = 0;
        ArrayList<Integer> numbRow1 = new ArrayList<>();
        ArrayList<Integer> numbRow2 = new ArrayList<>();
        int [][] rowCol = new int[100][];
        final float dist = 20;

        ArrayList<Integer> random = new ArrayList<>();
        for (int i = 0; i < pieces.length; i++) { random.add(i); }

        for (int i = 0; i < pieces.length; i++) {
            int rand = random.get(new Random().nextInt(random.size()));
            random.remove(random.indexOf(rand));

            float x = (float) (space + sumRow - rect[rand].left);
            float y = (float) (topMargin - rect[rand].top + sumMaxHeight);

            move(pieces, rect, pointPieces, rectPieces, outerPoints, rand, x, y);

            if (col != 0){ //COMPRESS PIECES IN HORIZONTAL DIRECTION

                boolean bool = false;
                do {
                    move(pieces, rect, pointPieces, rectPieces, outerPoints, rand, -dist,0);

                    for (int j = 0; j < col; j++) {
                        bool = piecesAreClose(outerPoints, rand, rectPieces, numbRow1.get(j));
                        if (bool) break;
                    }

                    if (bool || rect[rand].left <= space){
                        move(pieces, rect, pointPieces, rectPieces, outerPoints, rand,(float)(dist * 1.5),0);
                        break;
                    }
                }
                while (!bool);
            }

            sumRow = space + rect[rand].right;

            boolean last = false;
            if (row != 0) numbRow2.add(rand);

            if (sumRow > rowWidth) // PASS NEW ROW
            {
                sumMaxHeight += maxHeight + space;

                move(pieces, rect, pointPieces, rectPieces, outerPoints, rand,
                        (float) (space - rect[rand].left), (float) (space + maxHeight));
                numbRow2.add(rand);

                sumRow = space + rect[rand].right;
                maxHeight = 0;
                col = 0;

                if (i == pieces.length - 1) last = true;

                rowCol[row] = new int[numbRow1.size()];
                for (int j = 0; j < rowCol[row].length; j++) {
                    rowCol[row][j] = numbRow1.get(j);
                }
                numbRow1.clear();
                row++;
            }

            if ((rect[rand].bottom - rect[rand].top) > maxHeight) // DEFINE THE HIGHEST SHAPE IN A ROW
                maxHeight = rect[rand].bottom - rect[rand].top;

            col++;
            numbRow1.add(rand);

            if (i == pieces.length - 1 && !last){
                rowCol[row] = new int[numbRow1.size()];
                for (int j = 0; j < rowCol[row].length; j++) {
                    rowCol[row][j] = numbRow1.get(j);
                }
                row++;
            }
        }

        for (int round = 0; round < 20; round++) { //COMPRESS PIECES IN VERTICAL DIRECTION

            for (int i = 0; i < numbRow2.size(); i++) {

                boolean bool = false;
                do {
                    move(pieces, rect, pointPieces, rectPieces, outerPoints,
                            numbRow2.get(i), 0, -dist);

                    for (int k = 0; k < pieces.length; k++) {
                        if (k == numbRow2.get(i)) continue;

                        bool = piecesAreClose(outerPoints, numbRow2.get(i), rectPieces, k);
                        if (bool) break;
                    }

                    if (bool || rect[numbRow2.get(i)].top <= topMargin){
                        move(pieces, rect, pointPieces, rectPieces, outerPoints,
                                numbRow2.get(i),0,(float)(dist * 1.5));
                        break;
                    }
                }
                while (!bool);

            }
        }

    }

    private static void drawFullOuterPoints(Point [][] pointPieces, int i, Point [][] outerPoints, int space){

        ArrayList<Point> points = new ArrayList<>();

        for (int j = 0; j < pointPieces[i].length; j++) {

            int j2;

            if (j < pointPieces[i].length - 1) j2 = j + 1;
            else j2 = 0;

            double dx = pointPieces[i][j2].x - pointPieces[i][j].x;
            double dy = pointPieces[i][j2].y - pointPieces[i][j].y;
            double dist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
            double sin = dy / dist;
            double cos = dx / dist;

            for (double k = 0; k < dist; k+=space) {
                points.add(new Point((int)(k * cos + pointPieces[i][j].x),
                        (int)(k * sin + pointPieces[i][j].y)));
            }
        }

        outerPoints[i] = new Point[points.size()];
        for (int j = 0; j < outerPoints[i].length; j++) {
            outerPoints[i][j] = points.get(j);
        }
    }

    public static void surroundWithRect(Canvas canvas, Point [][] pointPieces, RectF [] rect, int i){

        double minX = canvas.getWidth(), minY = canvas.getHeight(), maxX = 0, maxY = 0;
        for (int j = 0; j < pointPieces[i].length; j++) {
            if (pointPieces[i][j].x < minX) minX = pointPieces[i][j].x;
            if (pointPieces[i][j].y < minY) minY = pointPieces[i][j].y;
            if (pointPieces[i][j].x > maxX) maxX = pointPieces[i][j].x;
            if (pointPieces[i][j].y > maxY) maxY = pointPieces[i][j].y;
        }

        rect[i] = new RectF((float) minX, (float) minY, (float) maxX, (float) maxY);
    }


    private static void drawOuterPoints(Point [][] pointPieces, int i, ArrayList<Point> points, int space){
        for (int j = 0; j < pointPieces[i].length; j++) {

            if (j < pointPieces[i].length - 1){
                if (pointPieces[i][j].y < pointPieces[i][j+1].y){
                    for (int row = pointPieces[i][j].y; row < pointPieces[i][j+1].y; row+=space) {

                        float dx = (row - pointPieces[i][j].y) * (pointPieces[i][j+1].x - pointPieces[i][j].x) /
                                (pointPieces[i][j+1].y - pointPieces[i][j].y);

                        points.add(new Point((int)(dx + pointPieces[i][j].x), row));
                    }
                }

                else {
                    for (int row = pointPieces[i][j+1].y; row < pointPieces[i][j].y; row+=space) {

                        float dx = (row - pointPieces[i][j].y) * (pointPieces[i][j+1].x - pointPieces[i][j].x) /
                                (pointPieces[i][j+1].y - pointPieces[i][j].y);

                        points.add(new Point((int)(dx + pointPieces[i][j].x), row));
                    }
                }
            }

            else {
                if (pointPieces[i][j].y < pointPieces[i][0].y){
                    for (int row = pointPieces[i][j].y; row < pointPieces[i][0].y; row+=space) {

                        float dx = (row - pointPieces[i][j].y) * (pointPieces[i][0].x - pointPieces[i][j].x) /
                                (pointPieces[i][0].y - pointPieces[i][j].y);

                        points.add(new Point((int)(dx + pointPieces[i][j].x), row));
                    }
                }

                else {
                    for (int row = pointPieces[i][0].y; row < pointPieces[i][j].y; row+=space) {

                        float dx = (row - pointPieces[i][j].y) * (pointPieces[i][0].x - pointPieces[i][j].x) /
                                (pointPieces[i][0].y - pointPieces[i][j].y);

                        points.add(new Point((int)(dx + pointPieces[i][j].x), row));
                    }
                }
            }
        }
    }


    public static void drawInnerRects(Point [][] pointPieces, RectF [] rect, RectF [][] rectPieces, int i){

        ArrayList<Point> outerPoints = new ArrayList<>();
        drawOuterPoints(pointPieces, i, outerPoints, 1);

        int space = 10;
        ArrayList<RectF> rects = new ArrayList<>();

        for (float row = rect[i].top; row < rect[i].bottom; row+=space) {

            ArrayList<Integer> outerPointsX = new ArrayList<>();
            for (int j = 0; j < outerPoints.size(); j++) {
                if (row == outerPoints.get(j).y){
                    outerPointsX.add(outerPoints.get(j).x);
                }
            }
            Collections.sort(outerPointsX);

            if (outerPointsX.size()%2 == 0){
                for (int j = 0; j < outerPointsX.size(); j+=2) {
                    RectF rectF = new RectF(outerPointsX.get(j), row,
                            outerPointsX.get(j+1), row + space);
                    rects.add(rectF);
                }
            }
        }

        rectPieces[i] = new RectF[rects.size()];
        for (int j = 0; j < rectPieces[i].length; j++) {
            rectPieces[i][j] = rects.get(j);
        }
    }


    private static boolean piecesAreClose(Point [][] outerPoints, int i, RectF [][] rectPieces, int k) {
        boolean isClose = false;

        for (int j = 0; j < outerPoints[i].length; j++) {

            for (int l = 0; l < rectPieces[k].length; l++) {

                if (rectPieces[k][l].contains(outerPoints[i][j].x, outerPoints[i][j].y)) {
                    isClose = true;
                    break;
                }
            }

            if (isClose) break;

        }

        if (isClose) return true;

        else return false;
    }

    private static void move(Path [] pieces, RectF [] rect, Point [][] pointPieces,
                             RectF [][] rectPieces, Point [][] outerPoints, int rand, float x, float y){

        for (int j = 0; j < rectPieces[rand].length; j++) {
            rectPieces[rand][j].offset(x, y);
        }

        for (int j = 0; j < pointPieces[rand].length; j++) {
            pointPieces[rand][j].offset((int) x, (int) y);
        }

        for (int j = 0; j < outerPoints[rand].length; j++) {
            outerPoints[rand][j].offset((int) x, (int) y);
        }

        pieces[rand].offset(x, y);
        rect[rand].offset(x, y);
    }
}
