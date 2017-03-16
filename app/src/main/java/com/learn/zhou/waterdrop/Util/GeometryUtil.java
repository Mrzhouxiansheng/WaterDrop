package com.learn.zhou.waterdrop.Util;

import android.graphics.PointF;



public class GeometryUtil {

    /**
     * As meaning of method name. 获得两点之间的距离
     *
     * @param p0
     * @param p1
     * @return
     */
    public static float getDistanceBetween2Points(PointF p0, PointF p1) {
        float distance = (float) Math.sqrt(Math.pow(p0.y - p1.y, 2) + Math.pow(p0.x - p1.x, 2));
        return distance;
    }

    /**
     * Get middle point between p1 and p2. 获得两点连线的中点
     *
     * @param p1
     * @param p2
     * @return
     */
    public static PointF getMiddlePoint(PointF p1, PointF p2) {
        return new PointF((p1.x + p2.x) / 2.0f, (p1.y + p2.y) / 2.0f);
    }

    /**
     * Get point between p1 and p2 by percent. 根据百分比获取两点之间的某个点坐标
     *
     * @param p1
     * @param p2
     * @param percent
     * @return
     */
    public static PointF getPointByPercent(PointF p1, PointF p2, float percent) {
        return new PointF(evaluateValue(percent, p1.x, p2.x), evaluateValue(percent, p1.y, p2.y));
    }

    /**
     * 根据分度值，计算从start到end中，fraction位置的值。fraction范围为0 -> 1
     *
     * @param fraction
     * @param start
     * @param end
     * @return
     */
    public static float evaluateValue(float fraction, Number start, Number end) {
        return start.floatValue() + (end.floatValue() - start.floatValue()) * fraction;
    }

    /**
     * Get the point of intersection between circle and line. 获取
     * 通过指定圆心，斜率为lineK的直线与圆的交点。
     *
     * @param pMiddle
     *            The circle center point.
     * @param radius
     *            The circle radius.
     * @param lineK
     *            The slope of line which cross the pMiddle.
     * @return
     */
    public static PointF[] getIntersectionPoints(PointF pMiddle, float radius, Double lineK) {
        PointF[] points = new PointF[2];

        float radian, xOffset = 0, yOffset = 0;
        if (lineK != null) {
            radian = (float) Math.atan(lineK);
            xOffset = (float) (Math.sin(radian) * radius);
            yOffset = (float) (Math.cos(radian) * radius);
        } else {
            xOffset = radius;
            yOffset = 0;
        }
        points[0] = new PointF(pMiddle.x + xOffset, pMiddle.y - yOffset);
        points[1] = new PointF(pMiddle.x - xOffset, pMiddle.y + yOffset);

        return points;
    }

    /**
     * 获得WaterDrop连接处与圆的两个交点
     */
    public static PointF[] getConnectPoint(PointF middleCenter,float angle, float middleRadius,
                                           PointF moveCenter){
        PointF[] points = new PointF[2];
        float distance = getDistanceBetween2Points(middleCenter,moveCenter);
        //两点之间的角度
        float angleTemp = (float) Math.asin((moveCenter.y-middleCenter.y)/distance);
        if (moveCenter.x-middleCenter.x<0 && moveCenter.y-middleCenter.y>=0){
            angleTemp = (float) (Math.PI - angleTemp);
        }else if (moveCenter.x-middleCenter.x<0 && moveCenter.y-middleCenter.y<0){
            angleTemp = (float) (-Math.PI - angleTemp);
        }
        float y1 = (float) (middleRadius * Math.sin(angle+angleTemp));
        float x1 = (float) (middleRadius * Math.cos(angle+angleTemp));

        float y2 = (float) (middleRadius * Math.sin(angleTemp - angle));
        float x2 = (float) (middleRadius * Math.cos(angleTemp - angle));

        points[0] = new PointF(x1+middleCenter.x, y1+middleCenter.y);
        points[1] = new PointF(x2+middleCenter.x, y2+middleCenter.y);
        return points;
    }
}
