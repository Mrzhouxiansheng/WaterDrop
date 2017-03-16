package com.learn.zhou.waterdrop;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;


import com.fanyu.litelibrary.lite;
import com.learn.zhou.waterdrop.Util.AutoUtil;
import com.learn.zhou.waterdrop.Util.GeometryUtil;

import java.util.ArrayList;


public class MoveCircle {
    String tag = "";
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintForQuad = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintForText = new Paint(Paint.ANTI_ALIAS_FLAG);
    float radius = AutoUtil.px2x(94f);//直径
    PointF center = new PointF(750f, 540f);
    PointF centerEnd = new PointF(750f, 540f);
    //贝塞尔曲线开始的两个点
    PointF[] intersectPointFs = new PointF[]{new PointF(50f, 250f), new PointF(50f, 350f)};
    //控制点: 用于绘制贝塞尔曲线
    PointF ctrPointF = new PointF(150f, 300f);

    boolean isConnected = true;
    float space = AutoUtil.px2x(20f);
    float breakSpace = AutoUtil.px2x(300f);
    double currentAngle;

    MoveCircle(StaticCircle sCircle, double angle) {
        paint.setColor(Color.BLUE);
        paintForText.setColor(Color.WHITE);
        paintForText.setTextSize(lite.appContext().getResources().getDimension(R.dimen.text_circle_move));
        paintForText.setFakeBoldText(false);
        paintForText.setStyle(Paint.Style.FILL);
        paintForText.setTypeface(Typeface.DEFAULT);
        paintForText.setTextAlign(Paint.Align.CENTER);
        setCenter(sCircle, angle);
    }

    void setCenterByTouch(ArrayList<MoveCircle> mCircles, StaticCircle sCircle, float downX, float downY) {
        double angle = Math.asin((downY - sCircle.center.y) / GeometryUtil.getDistanceBetween2Points(
                new PointF(downX - sCircle.center.x, downY - sCircle.center.y), new PointF(0f, 0f)));
        if (downX - sCircle.center.x < 0 && downY - sCircle.center.y >= 0) {
            angle = (float) (Math.PI - angle);
        } else if (downX - sCircle.center.x < 0 && downY - sCircle.center.y < 0) {
            angle = (float) (-Math.PI - angle);
        }
        currentAngle = angle;
        //设置圆心位置
        float centerDistance = GeometryUtil.getDistanceBetween2Points(sCircle.center, new PointF(downX, downY));
        if (centerDistance > sCircle.radius + radius + breakSpace) {
            isConnected = false;
        }
        if (centerDistance < (sCircle.radius + radius)) {
            isConnected = true;
        }
        center.set(downX, downY);
        //判断动圆之间是否交叉
        double compareAngle = currentAngle;
        for (MoveCircle moveCircle : mCircles) {
            if (moveCircle.equals(this)) continue;
            double diff = $(moveCircle.currentAngle - compareAngle);
            if (diff > 0 && diff < Math.PI / 3) {
                compareAngle = $(compareAngle + Math.PI / 3);
                moveCircle.setCenter(sCircle, compareAngle);
                for (MoveCircle moveCircle1 : mCircles){
                    if (moveCircle1.equals(this)) continue;
                    if (moveCircle1.equals(moveCircle))continue;
                    double diff1 = $(moveCircle1.currentAngle - compareAngle);
                    if (diff1>0 && diff1 < Math.PI /3){
                        compareAngle = $(compareAngle + Math.PI / 3);
                        moveCircle1.setCenter(sCircle, compareAngle);
                        for (MoveCircle moveCircle2 : mCircles){
                            if (moveCircle2.equals(this)) continue;
                            if (moveCircle2.equals(moveCircle))continue;
                            if (moveCircle2.equals(moveCircle1))continue;
                            double diff2 = $(moveCircle2.currentAngle - compareAngle);
                            if (diff2>0 && diff2 < Math.PI /3){
                                compareAngle = $(compareAngle + Math.PI / 3);
                                moveCircle2.setCenter(sCircle, compareAngle);
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            } else if (diff < 0 && diff > -Math.PI / 3) {
                compareAngle = $(compareAngle - Math.PI / 3);
                moveCircle.setCenter(sCircle, compareAngle);
                for (MoveCircle moveCircle1 : mCircles){
                    if (moveCircle1.equals(this)) continue;
                    if (moveCircle1.equals(moveCircle))continue;
                    double diff1 = $(moveCircle1.currentAngle - compareAngle);
                    if (diff1<0 && diff1> -Math.PI / 3){
                        compareAngle = $(compareAngle - Math.PI / 3);
                        moveCircle1.setCenter(sCircle, compareAngle);
                        for (MoveCircle moveCircle2 : mCircles){
                            if (moveCircle2.equals(this)) continue;
                            if (moveCircle2.equals(moveCircle))continue;
                            if (moveCircle2.equals(moveCircle1))continue;
                            double diff2 = $(moveCircle2.currentAngle - compareAngle);
                            if (diff2<0 && diff2 > -Math.PI /3){
                                compareAngle = $(compareAngle - Math.PI / 3);
                                moveCircle2.setCenter(sCircle, compareAngle);
                                break;
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
    }
//动画终点
    void setEndPoint(StaticCircle sCircle, float downX, float downY) {
        double angle = Math.asin((downY - sCircle.center.y) / GeometryUtil.getDistanceBetween2Points(
                new PointF(downX - sCircle.center.x, downY - sCircle.center.y), new PointF(0f, 0f)));
        if (downX - sCircle.center.x < 0 && downY - sCircle.center.y >= 0) {
            angle = (float) (Math.PI - angle);
        } else if (downX - sCircle.center.x < 0 && downY - sCircle.center.y < 0) {
            angle = (float) (-Math.PI - angle);
        }
        currentAngle = $(angle);
        //设置动画的最终点
        float x = (float) ((radius + sCircle.radius + space) * Math.cos(angle) + sCircle.center.x);
        float y = (float) ((radius + sCircle.radius + space) * Math.sin(angle) + sCircle.center.y);
        centerEnd.set(x, y);
    }

    private void setCenter(StaticCircle sCircle, double angle) {
        angle = $(angle);
        float x = (float) ((radius + sCircle.radius + space) * Math.cos(angle) + sCircle.center.x);
        float y = (float) ((radius + sCircle.radius + space) * Math.sin(angle) + sCircle.center.y);
        center.set(x, y);
        currentAngle = angle;
    }

    private double $(double angle) {
        double result;
        if (angle > Math.PI) {
            result = angle - Math.PI * 2;
        } else if (angle < -Math.PI) {
            result = angle + Math.PI * 2;
        } else {
            return angle;
        }
        return $(result);
    }

}
