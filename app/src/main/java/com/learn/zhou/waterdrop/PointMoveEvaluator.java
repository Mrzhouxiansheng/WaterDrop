package com.learn.zhou.waterdrop;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * 点的属性动画
 */
public class PointMoveEvaluator implements TypeEvaluator{

    @Override
    public Object evaluate(float fraction, Object startValue, Object endValue) {
        PointF startPointF = (PointF) startValue;
        PointF endPointF = (PointF) endValue;
        float x = startPointF.x + fraction * (endPointF.x - startPointF.x);
        float y = startPointF.y + fraction * (endPointF.y - startPointF.y);
        return new PointF(x, y);
    }
}
