package com.androidanimation.progressanim.sample1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by popfisher on 2017/7/13.
 */

public class MultiCircleProgressView extends View {

    // ================ 公共数据 ============= //
    /** 顶部作为计数起点, 右边是0，左边是-180或者180，底部是90 */
    private static final int START_POINT_TOP = -90;
    /** 圆环进度的颜色 */
    private int mRoundProgressColor = 0xff04d3ff;
    /** 圆心的x坐标 */
    int mCenterX = 0;
    /** 圆心的y坐标 */
    int mCenterY = 0;
    /** 定义画笔 */
    private Paint mProgressPaint;

    // ================ 外环进度数据 ============= //
    /** 顶部作为计数起点 270度, 计算圆上的任意点坐标时顺时针为正，右边是0 */
    private static final float HEAD_CIRCLE_START_ANGLE = 270f;
    /** 进度条每次移动的角度 */
    private static final int OUTER_PROGRESS_STEP = 3;
    /** 修改这个颜色数组就会出现不一样的渐变圆弧 */
    private int[] mColors = {
            0x0004d3ff, 0x0004d3ff, 0x4004d3ff, 0x8004d3ff, 0xff04d3ff
    };
    /** 外环渐变处理器 */
    private SweepGradient mOuterSweepGradient;
    /** 外环用于旋转的矩阵 Matrix */
    private Matrix mOuterMatrix = new Matrix();
    /** 外圆环的宽度 */
    private float mOuterRoundWidth;
    /** 外圆环头部的圆圈半径 */
    private float mOuterHeadCircleWidth;
    /** 外环的半径 */
    private float mOuterRadius = 0;
    /** 外环角度旋转总进度*/
    private int mOuterAngleProgressTotal = 0;
    /** 外环头部圆选择角度 */
    private float mOuterHeadCircleAngleTotal = 0;
    private double mOuterHeadCircleAngleTotalMath = 0;

    // ================ 内环进度数据 ============= //
    /** 内环的半径 */
    private float mInnerRadius = 0;
    /** 内圆环的宽度 */
    private float mInnerRoundWidth;
    /** 用于定义的圆弧的形状和大小的界限 */
    private RectF mInnerArcLimitRect = new RectF();
    /** 内环总弧长 */
    private int mInnerArcAngle = 0;

    // ================ 中间百分比数据 ============= //
    private static final int MAX_PROGRESS_DEFAULT = 100;
    private static final int PERCENT_BASE = 100;
    private static final int TOTAL_ANGLE = 360;
    /** 当前进度 */
    private int mCurProgress = 0;
    private String mCurProgressStr = "";
    private String mPercentSignStr = "%";
    /** 中间进度百分比的字符串的颜色 */
    private int mPercentTextColor = 0xffffffff;
    /** 中间进度百分比的字符串的字体大小 */
    private float mPercentTextSize;
    /** 中间进度百分号字体大小 */
    private float mPercentSignSize;
    /** 百分比文本与%符号之间的间距 */
    private float mSpaceTextAndSign;
    /** 百分比数字文本的边界 */
    private Rect mPercentTextBounds = new Rect();
    /** %符号的边界 */
    private Rect mPercentSignBounds = new Rect();
    /** 字体绘制的基线 */
    private float mFontBaseline;

    public MultiCircleProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
//        setProgress(90);
    }

    private void init() {
        mProgressPaint = new Paint();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mOuterRoundWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics);
        mInnerRoundWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, metrics);
        mOuterRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, metrics);
        mInnerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, metrics);
        mSpaceTextAndSign = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, metrics);
        mOuterHeadCircleWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2.5f, metrics);
        mPercentTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 45, metrics);
        mPercentSignSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, metrics);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        calculatePreValue();
        drawOuterGradientProgress(canvas);
        drawInnerProgress(canvas);
        drawPercentText(canvas);
        postInvalidate();
    }

    private void calculatePreValue() {
        if (mCenterX == 0) {
            mCenterX = getWidth() / 2;                         // 获取圆心的x坐标
        }
        if (mCenterY == 0) {
            mCenterY = getHeight() / 2;
        }
        if (mInnerArcLimitRect.isEmpty()) {
            mInnerArcLimitRect.set(mCenterX - mInnerRadius, mCenterX - mInnerRadius, mCenterX + mInnerRadius, mCenterX + mInnerRadius);
        }
    }

    private void drawOuterGradientProgress(final Canvas canvas) {
        mProgressPaint.setStrokeWidth(mOuterRoundWidth);         // 设置圆环的宽度
        mProgressPaint.setColor(mRoundProgressColor);       // 设置进度的颜色
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        // 定义一个梯度渲染，由于梯度渲染是从三点钟方向开始，所以再让他逆时针旋转90°，从0点开始
        if (mOuterSweepGradient == null) {
            mOuterSweepGradient = new SweepGradient(mCenterX, mCenterY, mColors, null);
        }
        mOuterMatrix.setRotate((START_POINT_TOP + mOuterAngleProgressTotal), mCenterX, mCenterY);
        mOuterSweepGradient.setLocalMatrix(mOuterMatrix);
        mProgressPaint.setShader(mOuterSweepGradient);
        canvas.drawCircle(mCenterX, mCenterY, mOuterRadius, mProgressPaint); // 画出圆环
        drawOuterArcHeadCircle(canvas);
        mOuterAngleProgressTotal += OUTER_PROGRESS_STEP;
        if (mOuterAngleProgressTotal > TOTAL_ANGLE) {
            mOuterAngleProgressTotal -= TOTAL_ANGLE;
        }
    }

    private void drawOuterArcHeadCircle(final Canvas canvas) {
        mProgressPaint.setShader(null);
        mProgressPaint.setStrokeWidth(0);
        mProgressPaint.setStyle(Paint.Style.FILL);
        // 一开始从顶部开始旋转
        mOuterHeadCircleAngleTotal = (HEAD_CIRCLE_START_ANGLE + mOuterAngleProgressTotal);
        if (mOuterHeadCircleAngleTotal - 360f > 0) {
            mOuterHeadCircleAngleTotal -= 360f;
        }
        // 根据旋转角度计算圆上当前位置点坐标，再以当前位置左边点位圆心画一个圆
        mOuterHeadCircleAngleTotalMath = mOuterHeadCircleAngleTotal * Math.PI / 180f;
        canvas.drawCircle((float) (mCenterX + mOuterRadius * Math.cos(mOuterHeadCircleAngleTotalMath)),
                (float) (mCenterY + mOuterRadius * Math.sin(mOuterHeadCircleAngleTotalMath)),
                mOuterHeadCircleWidth, mProgressPaint);
    }

    private void drawInnerProgress(final Canvas canvas) {
        mProgressPaint.setStrokeWidth(mInnerRoundWidth);         // 设置圆环的宽度
        mProgressPaint.setColor(mRoundProgressColor);       // 设置进度的颜色
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setShader(null);
        canvas.drawArc(mInnerArcLimitRect, START_POINT_TOP, mInnerArcAngle, false, mProgressPaint);
        setProgress(mCurProgress += 1);
    }

    private void drawPercentText(final Canvas canvas) {
        mProgressPaint.setStrokeWidth(0);
        mProgressPaint.setStyle(Paint.Style.FILL);
        mProgressPaint.setColor(mPercentTextColor);
        mProgressPaint.setTextSize(mPercentTextSize);
        mProgressPaint.setTextAlign(Paint.Align.CENTER);
        mProgressPaint.setTypeface(Typeface.DEFAULT_BOLD);                        // 设置字体

        mProgressPaint.getTextBounds(mCurProgressStr, 0, mCurProgressStr.length(), mPercentTextBounds);
        Paint.FontMetrics fontMetrics = mProgressPaint.getFontMetrics();
        mFontBaseline = mCenterY - (fontMetrics.top + fontMetrics.bottom) / 2;

        mProgressPaint.setTextSize(mPercentSignSize);
        mProgressPaint.getTextBounds(mPercentSignStr, 0, mPercentSignStr.length(), mPercentSignBounds);

        if (mCurProgress != 0) {
            mProgressPaint.setTextSize(mPercentTextSize);
            canvas.drawText(mCurProgressStr, mCenterX - (mSpaceTextAndSign + mPercentSignBounds.width()) / 2 ,
                    mFontBaseline, mProgressPaint); // 画出进度百分比

            mProgressPaint.setTextSize(mPercentSignSize);
            canvas.drawText(mPercentSignStr, mCenterX + (mSpaceTextAndSign + mPercentTextBounds.width()) / 2,
                    mFontBaseline, mProgressPaint); // 画出进度百分f符号
        }
    }

    /**
     * progress的范围 0~100
     * @param progress
     */
    public void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > MAX_PROGRESS_DEFAULT) {
//            progress = MAX_PROGRESS_DEFAULT;
            progress = 0;
        } else if (progress < 0) {
            progress = 0;
        }
        mCurProgress = progress;
        mInnerArcAngle = TOTAL_ANGLE * progress / PERCENT_BASE;
        mCurProgressStr = "" + mCurProgress;
    }
}
