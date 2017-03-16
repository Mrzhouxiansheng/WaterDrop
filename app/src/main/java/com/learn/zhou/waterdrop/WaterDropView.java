package com.learn.zhou.waterdrop;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.learn.zhou.waterdrop.Util.AutoUtil;
import com.learn.zhou.waterdrop.Util.GeometryUtil;

import java.util.ArrayList;

/**
 * 粘性水滴控件
 */
public class WaterDropView extends View {

    //中间不动的圆
    StaticCircle sCircle;
    //周围移动的圆
    ArrayList<MoveCircle> mCircles = new ArrayList<>();
    MoveCircle mCircleCtr;
    boolean isUnderAnimation = false;

    public WaterDropView(Context context) {
        super(context);
    }

    public WaterDropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
//加载水滴
    private void init() {
        if (isInEditMode())return;
        sCircle = new StaticCircle();
        for (int i=0; i<4; i++){
            MoveCircle mCircle = new MoveCircle(sCircle, Math.PI/2*(i+1)+Math.PI/4);
            mCircle.tag = String.valueOf(i)+"0";
            mCircles.add(mCircle);
        }
    }

    public WaterDropView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode())return;
        // 画不动的圆
        canvas.drawCircle(sCircle.center.x, sCircle.center.y, sCircle.radius, sCircle.paint);

        // 画动圆、画连接部分
        for (MoveCircle mCircle: mCircles){
            canvas.drawCircle(mCircle.center.x, mCircle.center.y, mCircle.radius, mCircle.paint);
            drawTextOfMoveCircle(canvas, mCircle);
            drawConnect(canvas, mCircle);
        }
    }
//水滴text的paint
    private void drawTextOfMoveCircle(Canvas canvas, MoveCircle mCircle) {
        int padding = (int) AutoUtil.px2x(30);
        Rect rect = new Rect((int) mCircle.center.x-padding, (int) (mCircle.center.y-padding),
                (int)mCircle.center.x+padding,(int) (mCircle.center.y+padding));//画一个矩形
        Paint rectPaint = new Paint();
        rectPaint.setColor(Color.BLUE);
        rectPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rect, rectPaint);

        Paint.FontMetrics fontMetrics = mCircle.paintForText.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom

        int baseLineY = (int) (rect.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式

        canvas.drawText(mCircle.tag,rect.centerX(),baseLineY,mCircle.paintForText);

//        canvas.drawText(mCircle.tag,mCircle.center.x,mCircle.center.y,mCircle.paintForText);
    }
//画连接部分
    private void drawConnect(Canvas canvas, MoveCircle mCircle) {
        if (GeometryUtil.getDistanceBetween2Points(sCircle.center, mCircle.center) <
                (sCircle.radius + mCircle.radius)) {
            //如果两圆相交则退出方法
            return;
        }

        if (!mCircle.isConnected)return;

        // 求得连接处的交点
        sCircle.intersectPointFs = GeometryUtil.getConnectPoint(sCircle.center, (float) (Math.PI / 6),
                sCircle.radius, mCircle.center);
        mCircle.intersectPointFs = GeometryUtil.getConnectPoint(mCircle.center, (float) (Math.PI / 6),
                mCircle.radius, sCircle.center);
        // 求得连接处的控制点
        PointF pointF1 = GeometryUtil.getMiddlePoint(sCircle.intersectPointFs[0], sCircle.intersectPointFs[1]);
        PointF pointF2 = GeometryUtil.getMiddlePoint(mCircle.intersectPointFs[0], mCircle.intersectPointFs[1]);
        mCircle.ctrPointF = GeometryUtil.getMiddlePoint(pointF1, pointF2);

        Path path = new Path();
        path.moveTo(sCircle.intersectPointFs[0].x, sCircle.intersectPointFs[0].y);
        path.quadTo(mCircle.ctrPointF.x, mCircle.ctrPointF.y, mCircle.intersectPointFs[1].x,
                mCircle.intersectPointFs[1].y);
        path.lineTo(mCircle.intersectPointFs[0].x, mCircle.intersectPointFs[0].y);
        path.quadTo(mCircle.ctrPointF.x, mCircle.ctrPointF.y,
                sCircle.intersectPointFs[1].x, sCircle.intersectPointFs[1].y);
        path.close();// 关闭后，会回到最开始的地方，形成封闭的图形
        //设置渐变色
        Shader shader = new LinearGradient(pointF1.x, pointF1.y, pointF2.x, pointF2.y,
                new int[]{Color.RED, Color.BLUE}, null, Shader.TileMode.CLAMP);
        mCircle.paintForQuad.setShader(shader);
        //绘制连接段
        canvas.drawPath(path, mCircle.paintForQuad);
    }
//触摸事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isUnderAnimation)return true;
        float downX = 0.0f;
        float downY = 0.0f;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 得到按下的坐标
                downX = event.getX();
                downY = event.getY();
                setControlCircle(downX,downY);
                if (mCircleCtr == null)break;
                // 更新移动的坐标
                mCircleCtr.setCenterByTouch(mCircles,sCircle,downX,downY);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCircleCtr == null)break;
                // 得到按下的坐标
                downX = event.getX();
                downY = event.getY();
                // 更新移动的坐标
                mCircleCtr.setCenterByTouch(mCircles, sCircle,downX,downY);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (mCircleCtr == null)break;
                // 得到抬起的坐标
                downX = event.getX();
                downY = event.getY();
                mCircleCtr.setEndPoint(sCircle, downX, downY);
                startMoveCircleAnimation(mCircleCtr);
                mCircleCtr = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;

        }
        return true;
    }

    private void setControlCircle(float downX, float downY) {
        for (MoveCircle mCircle : mCircles){
            float distance = GeometryUtil.getDistanceBetween2Points(mCircle.center,new PointF(downX,downY));
            if (distance <= mCircle.radius){
                mCircleCtr = mCircle;
                return;
            }
        }
    }
//拉动水滴动画
    private void startMoveCircleAnimation(MoveCircle mCircleCtr){
        if (mCircleCtr==null)return;
        final Integer pos = getPositionOfCtrCircle(mCircleCtr);
        if (pos==null)return;
        isUnderAnimation = true;
        final PointF startPoint = mCircles.get(pos).center;
        final PointF endPoint = mCircles.get(pos).centerEnd;
        ValueAnimator anim = ValueAnimator.ofObject(new PointMoveEvaluator(), startPoint, endPoint);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCircles.get(pos).center = (PointF) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setBounceMoveAnimation(pos, endPoint, startPoint,200,0.2f,"1.1");
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.setDuration(500);
        anim.start();
    }
//弹性动画
    private void setBounceMoveAnimation(final int pos, PointF start, PointF end, final long duration,
                                        float reduce, final String tag) {
        final PointF startPoint = start;
        float x = (end.x-start.x) * reduce +start.x;
        float y = (end.y-start.y) * reduce +start.y;
        final PointF endPoint = new PointF(x,y);

        ValueAnimator anim = ValueAnimator.ofObject(new PointMoveEvaluator(), startPoint, endPoint);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCircles.get(pos).center = (PointF) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                String tagNew = "";
                float reduce = 0.4f;
                if (tag.equals("1.1")){
                    tagNew = "1.2";
                    reduce = 1f;
                }else if (tag.equals("1.2")){
                    tagNew = "2.1";
                    reduce = 0.4f;
                }else if (tag.equals("2.1")){
                    tagNew = "2.2";
                    reduce = 1f;
                }else if (tag.equals("2.2")){
                    tagNew = "3.1";
                    reduce = 0.4f;
                }else if (tag.equals("3.1")){
                    tagNew = "3.2";
                    reduce = 1f;
                }else {
                    isUnderAnimation = false;
                    return;
                }
                setBounceMoveAnimation(pos, endPoint, startPoint, (long) (duration*reduce),reduce,tagNew);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.setDuration(duration);
        anim.start();
    }

    private Integer getPositionOfCtrCircle(MoveCircle mCircleCtr) {
        for (int i=0; i<mCircles.size(); i++){
            if (mCircles.get(i).equals(mCircleCtr)){
                return i;
            }
        }
        return null;
    }

}

