package com.learn.zhou.waterdrop;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import com.learn.zhou.waterdrop.Util.AutoUtil;


//不动圆

public class StaticCircle {
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    float radius = AutoUtil.px2x(206f);//半径
    PointF center = new PointF(AutoUtil.px2x(540f), AutoUtil.px2yStatus(705f));
    //贝塞尔曲线开始的两个点
    PointF[] intersectPointFs = new PointF[] { new PointF(250f, 250f), new PointF(250f, 350f) };

    StaticCircle() {
        paint.setColor(Color.RED);
    }
}
