package com.example.negative_screen;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.WindowManager;

import androidx.annotation.ColorInt;


public class MarqueeView extends TextureView {

    private Context mContext;
    /**
     * 字体大小
     */
    private float mTextSize = 50;
    /**
     * 字体颜色
     */
    private int mTextColor = Color.WHITE;

    private int mTextAlpha = 255;

    /**
     * 是否重复滚动
     */
    private boolean mIsRepeat;
    /**
     * 开始滚动的位置
     */
    private int mStartPoint;
    /**
     * 滚动方向 0向左 1向右
     */
    private int mDirection;
    /**
     * 睡眠时长，可根据此属性调整速度
     */
    private int mSpeed;
    private TextPaint mTextPaint;
    private MarqueeViewThread mThread;
    private String margueeString = "";
    private int textWidth = 0, textHeight = 0;
    private int currentX = 0; // 当前 x 位置
    /**
     * 每步滚动的距离 此属性可调整滚动速度
     */
    private int sepX = 10;

    public MarqueeView(Context context) {
        this(context, null);
    }

    public MarqueeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MarqueeView, defStyleAttr, 0);
        mTextColor = a.getColor(R.styleable.MarqueeView_textColor, Color.WHITE);
        mTextSize = a.getDimension(R.styleable.MarqueeView_textSize, 48);
        mIsRepeat = a.getBoolean(R.styleable.MarqueeView_isRepeat, false);
        mStartPoint = a.getInt(R.styleable.MarqueeView_startPoint, 0);
        mDirection = a.getInt(R.styleable.MarqueeView_direction, 0);
        mSpeed = a.getInt(R.styleable.MarqueeView_speed, 20);
        a.recycle();

        setOpaque(false);
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
    }

    public void setText(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            measurementsText(msg);
        }
    }

    public void setTextColor(@ColorInt int color) {
        mTextColor = color;
        measurementsText(margueeString);
    }

    public void setTextAlpha(float alpha) {
        mTextAlpha = (int) (alpha * 255);
        measurementsText(margueeString);
    }

    public void setTextSize(float textSize) {
        mTextSize = textSize;
        measurementsText(margueeString);
    }

    public void setScrollSpeed(int speed) {
        mSpeed = speed;
    }

    public void setScrollDirection(int direction) {
        mDirection = direction;
    }

    protected void measurementsText(String msg) {
        margueeString = msg;
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setFakeBoldText(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setAlpha(mTextAlpha);
        mTextPaint.setStrokeWidth(0.5f);
        mTextPaint.setFakeBoldText(true);
        textWidth = (int) mTextPaint.measureText(margueeString);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        textHeight = (int) fontMetrics.bottom;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        currentX = (mStartPoint == 0) ? 0 : width - getPaddingLeft() - getPaddingRight();
    }

    /**
     * 开始滚动
     */
    public void startScroll() {
        if (mThread != null && mThread.isRun) return;
        if (margueeString.isEmpty()) return;
        mThread = new MarqueeViewThread();
        mThread.start();
    }

    /**
     * 停止滚动
     */
    public void stopScroll() {
        if (mThread != null) {
            mThread.isRun = false;
            mThread.interrupt();
        }
        mThread = null;
    }

    class MarqueeViewThread extends Thread {
        public boolean isRun; // 是否运行

        public MarqueeViewThread() {
            isRun = true;
        }

        public void onDraw() {
            try {
                Canvas canvas = lockCanvas();
                if (canvas == null) return;
                int paddingLeft = getPaddingLeft();
                int paddingRight = getPaddingRight();
                int contentWidth = getWidth() - paddingLeft - paddingRight;
                int centerYPos = (int) ((getHeight() / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));

                if (mDirection == 0) { // 向左滚动
                    if (currentX <= -textWidth) {
                        if (!mIsRepeat) { // 不重复滚动
                            mHandler.sendEmptyMessage(ROLL_OVER);
                        }
                        currentX = contentWidth;
                    } else {
                        currentX -= sepX;
                    }
                } else { // 向右滚动
                    if (currentX >= contentWidth) {
                        if (!mIsRepeat) { // 不重复滚动
                            mHandler.sendEmptyMessage(ROLL_OVER);
                        }
                        currentX = -textWidth;
                    } else {
                        currentX += sepX;
                    }
                }

                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                canvas.drawText(margueeString, currentX, centerYPos, mTextPaint);
                unlockCanvasAndPost(canvas);
                Thread.sleep(mSpeed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (isRun) {
                onDraw();
            }
        }
    }

    public static final int ROLL_OVER = 100;
    Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == ROLL_OVER) {
                stopScroll();
                if (mOnMargueeListener != null) {
                    mOnMargueeListener.onRollOver();
                }
            }
        }
    };

    public interface OnMargueeListener {
        void onRollOver(); // 滚动完毕
    }

    OnMargueeListener mOnMargueeListener;

    public void setOnMargueeListener(OnMargueeListener listener) {
        this.mOnMargueeListener = listener;
    }
}
