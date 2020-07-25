package com.example.logictest;

import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Collections;

public class Pointer extends Path {

    public ArrayList<RectF> innerRects = new ArrayList<>();

    public Pointer(){}

    public Pointer(Point [] pointPieces, float length){

        ArrayList<Point> points = new ArrayList<>();

        final float dirFingerLength = length * 2 / 3;
        float fingerLength = dirFingerLength * 35 / 100;
        final float thumbLength = dirFingerLength * 25 / 100;

        float radius = dirFingerLength / 10;

        Point upPoint = lowestPointOfPiece(pointPieces);

        float cX = upPoint.x - radius;
        float cY = upPoint.y + radius + dirFingerLength;

        points.add(new Point((int) cX, (int) cY));
        points.add(new Point((int) cX, (int) (cY - dirFingerLength)));

        incompleteCircle(points, cX, cY, cX, cY - dirFingerLength, radius, 180); // DIRECTING FINGER

        points.add(new Point((int) (cX + 2*radius), (int) cY));

        float startX = 0, startY = 0, endX = 0, endY = 0;
        for (int i = 0; i < 3; i++) {//OTHER FINGERS

            startX = cX + (i+1)*2*radius;
            startY = cY;
            endX = cX + (i+1)*2*radius;
            endY = cY - fingerLength + i * radius * 8 / 10;

            points.add(new Point((int) endX, (int) endY));

            incompleteCircle(points, startX, startY, endX, endY, radius, 180);

            points.add(new Point((int) (cX + (i+2)*2*radius), (int) cY));

            if (i==2){
                startX = cX + (i+2)*2*radius;
                startY = cY - fingerLength + i * radius * 8 / 10;
                endX = cX + (i+2)*2*radius;
                endY = cY;
            }
        }

        incompleteCircle(points, startX, startY, endX, endY, radius * 5, 180); //PALM

        points.add(new Point((int) (cX - 2*radius), (int) (cY - thumbLength)));

        incompleteCircle(points, cX - 2*radius, cY, cX - 2*radius,
                cY - thumbLength, (radius - radius * 3 / 10), 180); // THUMB

        this.moveTo(points.get(0).x, points.get(0).y);
        for (int i = 1; i < points.size(); i++) {
            this.lineTo(points.get(i).x, points.get(i).y);
        }
        this.close();

        drawInnerRects(points, innerRects, 10);
    }

    private void incompleteCircle(ArrayList<Point> points, float startX, float startY, float endX,
                                         float endY, float radius, float angle){

        float x1 = startX - endX;
        float y1 = startY - endY;
        float x2 = (float) Math.sqrt(Math.pow(radius, 2) / (1 + Math.pow(x1/y1, 2)));
        float y2 = (float) Math.sqrt(Math.pow(radius, 2) / (1 + Math.pow(y1/x1, 2)));

        float startAngle = 180 + (float) Math.toDegrees(Math.atan2(y1, x1));

        if (x1 < 0 && y1 < 0){ x2 = -x2; }
        else if (x1 > 0 && y1 > 0){ y2 = -y2;}
        else if (x1 > 0 && y1 < 0){ x2 = -x2; y2 = -y2;}

        else if (x1 == 0){
            if (y1 > 0){ x2 = radius;}
            else{ x2 = -radius;}
            y2 = 0;
        }

        else if (y1 == 0){
            if (x1 > 0){ y2 = -radius;}
            else y2 = radius;
            x2 = 0;
        }

        float cX = endX + x2;
        float cY = endY + y2;

        for (float i = startAngle; i < angle + startAngle; i++) {
            float radian = (float) (Math.toRadians(i));
            float x = (float) (cX + radius * Math.sin(radian));
            float y = (float) (cY - radius * Math.cos(radian));

            points.add(new Point((int) x, (int) y));
        }
    }

    private Point lowestPointOfPiece(Point [] pointPieces){

        Point point = new Point();
        float maxY = 0;

        for (int j = 0; j < pointPieces.length; j++) {
            if (pointPieces[j].y > maxY){
                maxY = pointPieces[j].y;
                point = pointPieces[j];
            }
        }

        return point;
    }


    private RectF surroundWithRect(ArrayList<Point> points){

        double minX = 100000, minY = 100000, maxX = 0, maxY = 0;
        for (int j = 0; j < points.size(); j++) {
            if (points.get(j).x < minX) minX = points.get(j).x;
            if (points.get(j).y < minY) minY = points.get(j).y;
            if (points.get(j).x > maxX) maxX = points.get(j).x;
            if (points.get(j).y > maxY) maxY = points.get(j).y;
        }

        return new RectF((float) minX, (float) minY, (float) maxX, (float) maxY);
    }

    private void drawOuterPoints(ArrayList<Point> points, ArrayList<Point> outerPoints, int space){

        for (int j = 0; j < points.size(); j++) {

            int j2;

            if (j < points.size() - 1) j2 = j + 1;
            else j2 = 0;

            if (points.get(j).y < points.get(j2).y){
                for (int row = points.get(j).y; row < points.get(j2).y; row+=space) {

                    float dx = (row - points.get(j).y) * (points.get(j2).x - points.get(j).x) /
                            (points.get(j2).y - points.get(j).y);

                    outerPoints.add(new Point((int)(dx + points.get(j).x), row));
                }
            }

            else {
                for (int row = points.get(j2).y; row < points.get(j).y; row+=space) {

                    float dx = (row - points.get(j).y) * (points.get(j2).x - points.get(j).x) /
                            (points.get(j2).y - points.get(j).y);

                    outerPoints.add(new Point((int)(dx + points.get(j).x), row));
                }
            }
        }

    }

    private void drawInnerRects(ArrayList<Point> points, ArrayList<RectF> innerRects, int space){

        RectF rect = surroundWithRect(points);

        ArrayList<Point> outerPoints = new ArrayList<>();
        drawOuterPoints(points, outerPoints, 1);

        for (float row = rect.top; row < rect.bottom; row+=space) {

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
                    innerRects.add(rectF);
                }
            }
        }
    }

}
