package com.androidanimation.progressanim.sample1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by popfisher on 2017/7/13.
 */

public class MultiCircleProgressView extends SurfaceView implements SurfaceHolder.Callback  {

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
    /** 定义监听事件列表 */
    private List<IProgressStateChangeListener> mProgressStateChangeListeners;
    /** SurfaceView的绘制线程 */
    private DrawThread mDrawThread;
    private SurfaceHolder mSurfaceHolder;
    /** 最大帧数 (1000 / 20) */
    private static final int DRAW_INTERVAL = 20;
    private static final int CLEAR_COLOR = 0xff0583f7;

    // ================ 外环进度数据 ============= //
    /** 顶部作为计数起点 270度, 计算圆上的任意点坐标时顺时针为正，右边是0 */
    private static final float HEAD_CIRCLE_START_ANGLE = 270f;
    /** 进度条每次移动的角度 */
    private static int mOuterProgressStep = 6;
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
    /** 外环背景的半径 */
    private float mOuterBgRadius = 0;
    /** 外环角度旋转总进度*/
    private float mOuterAngleProgressTotal = 0;
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
    private float mInnerArcAngle = 0;

    // ================ 中间百分比数据 ============= //
    private static final int MAX_PROGRESS_DEFAULT = 100;
    private static final int MIN_PROGRESS_DEFAULT = 1;
    private static final int PERCENT_BASE = 100;
    private static final float TOTAL_ANGLE = 360f;
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
    }

    private void init() {
        mProgressStateChangeListeners = new ArrayList<IProgressStateChangeListener>();
        mProgressPaint = new Paint();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mOuterRoundWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics);
        mInnerRoundWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, metrics);
        mOuterRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, metrics);
        mOuterBgRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75, metrics);
        mInnerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, metrics);
        mSpaceTextAndSign = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, metrics);
        mOuterHeadCircleWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2.5f, metrics);
        mPercentTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 45, metrics);
        mPercentSignSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, metrics);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    /** 剩下需要快速完成的角度 */
    private float mNeedQuickCompleteAngle = 0;
    /** 开始快速完成时的角度起始值 */
    private float mQuickStartAngle = 0;
    /** 是否需要快速完成 */
    private boolean isNeedCompleteQuickly = false;
    public void completeQuickly() {
        isNeedCompleteQuickly = true;
        mQuickStartAngle = 0;
    }

    private void start() {
        if (mDrawThread == null) {
            mDrawThread = new DrawThread(mSurfaceHolder, getContext());
        }
        mDrawThread.isRunning = true;
        mDrawThread.start();
    }

    private void stop() {
        mDrawThread.isRunning = false;
        try {
            mDrawThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stop();
    }

    class DrawThread extends Thread {
        SurfaceHolder surfaceHolder;
        Context context;
        boolean isRunning;

        public DrawThread(SurfaceHolder surfaceHolder, Context context) {
            this.surfaceHolder = surfaceHolder;
            this.context = context;
        }

        @Override
        public void run() {
            long deltaTime;
            long timeStartPerDraw;
            long timerStartMillis = System.currentTimeMillis();
            while (isRunning) {
                Canvas canvas = null;
                timeStartPerDraw = System.currentTimeMillis();
                try {
                    synchronized (surfaceHolder) {
                        Surface surface = surfaceHolder.getSurface();
                        if (surface != null && surface.isValid()) {
                            canvas = surfaceHolder.lockCanvas(null);
                        }
                        if (isNeedCompleteQuickly) {
                            if (mQuickStartAngle == 0) {
                                timerStartMillis = System.currentTimeMillis();
                                mQuickStartAngle = getAngle();
                                mNeedQuickCompleteAngle = 360f - mQuickStartAngle;
                            }
                            deltaTime = System.currentTimeMillis() - timerStartMillis;
                            if (deltaTime <= 2000) {    // 快速结束时2秒走完剩下的
                                setAngle(mQuickStartAngle + mNeedQuickCompleteAngle / 2000f * deltaTime);
                            } else {
                                setAngle(360f);
                            }
                        } else {
                            deltaTime = System.currentTimeMillis() - timerStartMillis;
                            if (deltaTime <= 10000) {   // 前10秒每秒走5% 即18度
                                setAngle(180f / 10000f * deltaTime);
                            } else if (deltaTime <= 30000f) {    // 20秒到30秒走175度
                                setAngle(180f + (175f / 20000f * (deltaTime - 10000)));
                            }
                        }
                        if (canvas != null) {
                            doDraw(canvas);
                            if (getAngle() - 360f >= 0) {
                                isRunning = false;
                            }
                        }
                    }
                } catch (Exception e) {
                    isRunning = false;
                    e.printStackTrace();
                } finally {
                    if (surfaceHolder != null && canvas != null) {
                        try {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        } catch (Exception ignore) {}
                    }
                }

                deltaTime = System.currentTimeMillis() - timeStartPerDraw;
                if (deltaTime < DRAW_INTERVAL) {
                    try {
                        // 控制帧数
                        Thread.sleep(DRAW_INTERVAL - deltaTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private Path mBgCirclePath = new Path();
    @Override
    public void draw(Canvas canvas) {
//        clipCanvas(canvas);
        super.draw(canvas);
    }

    private void clipCanvas(Canvas canvas) {
        // 裁剪画布为一个圆，但是有锯齿啊。。。
        // 设置裁剪的圆心，半径
        if (mCenterX != 0) {
            mBgCirclePath.addCircle(mCenterX, mCenterY, mOuterBgRadius, Path.Direction.CCW);
        } else {
            mBgCirclePath.addCircle(getHeight() / 2, getHeight() / 2, getHeight() / 2, Path.Direction.CCW);
        }
        // 裁剪画布，并设置其填充方式
        canvas.clipPath(mBgCirclePath, Region.Op.REPLACE);
    }

    private void doDraw(Canvas canvas) {
        calculatePreValue();
        drawBg(canvas);
        drawOuterGradientProgress(canvas);
        drawInnerProgress(canvas);
        drawPercentText(canvas);
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

    private void drawBg(Canvas canvas) {
        canvas.drawColor(CLEAR_COLOR);
        mProgressPaint.setColor(CLEAR_COLOR);       // 设置进度的颜色
        mProgressPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mCenterX, mCenterY, mOuterBgRadius, mProgressPaint); // 画出圆环
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
        mOuterAngleProgressTotal += mOuterProgressStep;
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
        if (mOuterHeadCircleAngleTotal - TOTAL_ANGLE > 0) {
            mOuterHeadCircleAngleTotal -= TOTAL_ANGLE;
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
     * progress的范围 0~360
     * @param angle
     */
    public void setAngle(float angle) {
        if (angle - TOTAL_ANGLE > 0) {
            angle = TOTAL_ANGLE;
        } else if (angle < 0) {
            angle = 0;
        }
        mInnerArcAngle = angle;
        mCurProgress = (int) (mInnerArcAngle / TOTAL_ANGLE * PERCENT_BASE);
        if (mCurProgress < MIN_PROGRESS_DEFAULT) {
            mCurProgress = MIN_PROGRESS_DEFAULT;
        } else if (mCurProgress > MAX_PROGRESS_DEFAULT) {
            mCurProgress = MAX_PROGRESS_DEFAULT;
        }
        mCurProgressStr = "" + mCurProgress;
        notifyProgressStateChangeListeners();
    }

    public float getAngle() {
        return mInnerArcAngle;
    }

    public interface IProgressStateChangeListener {
        void onFinished();
    }

    private void notifyProgressStateChangeListeners() {
        if (mCurProgress == MAX_PROGRESS_DEFAULT) {
            for (IProgressStateChangeListener listener : mProgressStateChangeListeners) {
                if (listener != null) {
                    listener.onFinished();
                }
            }
        }
    }

    public void addProgressStateListener(IProgressStateChangeListener listener) {
        mProgressStateChangeListeners.add(listener);
    }

    public void onDestroy() {
        if (mProgressStateChangeListeners != null) {
            mProgressStateChangeListeners.clear();
            mProgressStateChangeListeners = null;
        }
    }
}
