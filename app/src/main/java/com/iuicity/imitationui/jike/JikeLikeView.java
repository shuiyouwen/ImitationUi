package com.iuicity.imitationui.jike;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.iuicity.imitationui.R;

/**
 * 即刻点赞view
 * Created by Shui on 2018/1/23.
 */

public class JikeLikeView extends View {
    private float mPadding = 5;
    private float mShiningPadding = -10;
    private float mRingWidth = 5f;
    private int mLikeNum;
    private float mTextDrawablePadding = 5f;
    private long mAnimationTime = 300;
    private float mOffsetCoefficient = 1.3f;//偏转系数
    private boolean mClickble = true;//是否可点击

    private Bitmap mUnlikeBitmap;
    private Paint mPaint;
    private Bitmap mLikeBitmap;
    private Bitmap mShiningBitmap;
    private Matrix mMatrix = new Matrix();

    private float mScaleArg = 1f;
    private float mAlphaArg = 1f;
    private float mRadiusArg;
    private float mRadius;
    private Paint mCirclePaint;

    private PointF mCircleCenterPoint;
    private PointF mShiningPoint;
    private PointF mLikePoint;
    private PointF mTextPoint;

    private boolean mIsLike = false;
    private Paint mTextPaint;
    private Rect mTextRect;
    private int mChangeNum = 0;//改变到的目标值
    private String mInvariantNumStr = "";//不变的数值部分
    private String mVariantNumStr = "";//变化的数值部分
    private String mTargetVariantNumStr = "";//变化的目标数值部分
    private float mNumberOffset = 0f;//数值偏移量

    public float getNumberOffset() {
        return mNumberOffset;
    }

    public void setNumberOffset(float numberOffset) {
        mNumberOffset = numberOffset;
        invalidate();
    }

    public float getRadiusArg() {
        return mRadiusArg;
    }

    public void setRadiusArg(float radiusArg) {
        mRadiusArg = radiusArg;
        if (radiusArg == 1.1f) {
            invalidate();
        }
    }

    public float getAlphaArg() {
        return mAlphaArg;
    }

    public void setAlphaArg(float alphaArg) {
        mAlphaArg = alphaArg;
    }


    public float getScaleArg() {
        return mScaleArg;
    }

    public void setScaleArg(float scaleArg) {
        mScaleArg = scaleArg;
    }

    public boolean isLike() {
        return mIsLike;
    }

    public void setLike(boolean like) {
        mIsLike = like;
    }

    public JikeLikeView(Context context) {
        this(context, null);
    }

    public JikeLikeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JikeLikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mClickble) {
                    return true;
                }

                mClickble = false;
                updateNumber();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setLike(!isLike());
                        if (isLike()) {
                            ObjectAnimator.ofFloat(JikeLikeView.this, "radiusArg", 0f, 1.1f).setDuration(mAnimationTime / 2).start();
                        }
                    }
                }, mAnimationTime / 2);
                ObjectAnimator.ofFloat(this, "scaleArg", 1f, 0.8f, 1f).setDuration(mAnimationTime).start();
                ObjectAnimator.ofFloat(this, "alphaArg", 1f, 0.6f, 1f).setDuration(mAnimationTime).start();
                break;
        }
        return true;
    }

    private void setLikeNum(int likeNum) {
        mLikeNum = likeNum;
        mChangeNum = likeNum;
    }

    /**
     * 修改数值
     */
    private void updateNumber() {
        mChangeNum = !isLike() ? mLikeNum + 1 : mLikeNum - 1;
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this, "numberOffset", 0f, 1f).setDuration(mAnimationTime);
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mClickble = true;
                setLikeNum(mChangeNum);
                getDisplayStr();
                invalidate();
            }
        });
        objectAnimator.start();
        getDisplayStr();
    }

    private void getDisplayStr() {
        String likeNumStr = String.valueOf(mLikeNum);
        String changeNumStr = String.valueOf(mChangeNum);
        StringBuilder invariantNum = new StringBuilder();//不变的数值
        StringBuilder variantNum = new StringBuilder();//变化的数值
        StringBuilder targetVarianNum = new StringBuilder();//目标变化数值
        char[] changeNumChars = changeNumStr.toCharArray();
        char[] likeNumChars = likeNumStr.toCharArray();
        for (int i = 0; i < likeNumChars.length; i++) {
            if (likeNumChars[i] == changeNumChars[i]) {
                invariantNum.append(likeNumChars[i]);
            } else {
                variantNum.append(likeNumChars[i]);
                targetVarianNum.append(changeNumChars[i]);
            }
        }
        mInvariantNumStr = invariantNum.toString();
        mVariantNumStr = variantNum.toString();
        mTargetVariantNumStr = targetVarianNum.toString();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(Color.parseColor("#6CFF4081"));
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mRingWidth);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.parseColor("#9e9e9e"));
        mTextPaint.setTextSize(30f);

        mUnlikeBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_comment_like);
        mLikeBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_messages_like_selected);
        mShiningBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_messages_like_selected_shining);

        int shiningBitmapWidth = mShiningBitmap.getWidth();
        int shiningBitmapHeight = mShiningBitmap.getHeight();

        int likeHeight = mLikeBitmap.getHeight();
        int likeWidth = mLikeBitmap.getWidth();

        float totalLikeHeight = likeHeight + mShiningPadding + shiningBitmapHeight;
        float maxLength = Math.max(totalLikeHeight, likeWidth);
        mRadius = maxLength / 2;
        mCircleCenterPoint = new PointF(mRadius + mPadding - likeWidth / 2, mRadius + mPadding);
        mShiningPoint = new PointF((maxLength - shiningBitmapWidth) / 2 + mPadding, (maxLength - totalLikeHeight) / 2 + mPadding);
        mLikePoint = new PointF((maxLength - likeWidth) / 2 + mPadding, mShiningPoint.y + shiningBitmapHeight + mShiningPadding);

        mTextRect = new Rect();
        String numStr = String.valueOf(mLikeNum);
        mTextPaint.getTextBounds(String.valueOf(mLikeNum), 0, numStr.length(), mTextRect);
        mTextPoint = new PointF(mLikePoint.x + likeWidth + mTextDrawablePadding, mShiningPoint.y + totalLikeHeight / 2 + mTextRect.height() / 2 + likeHeight / 2 + mShiningPadding);

        // TODO: 2018/1/24 测试
        setLikeNum(239);
        getDisplayStr();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLikeIcn(canvas);
        drawNumberText(canvas);
    }

    /**
     * 绘制文字部分
     *
     * @param canvas
     */
    private void drawNumberText(Canvas canvas) {
        float offset = mNumberOffset * mTextRect.height() * mOffsetCoefficient;
        offset = mChangeNum > mLikeNum ? -offset : offset;

        //绘制原来数值
        canvas.drawText(mInvariantNumStr, mTextPoint.x, mTextPoint.y, mTextPaint);//不变的数值
        float textWidth = mTextPaint.measureText(mInvariantNumStr);
        canvas.drawText(mVariantNumStr, mTextPoint.x + textWidth, mTextPoint.y + offset, mTextPaint);//变化部分数值

        //绘制变化目标数值
        if (mChangeNum > mLikeNum) {
            //点赞数变大
            canvas.drawText(mTargetVariantNumStr, mTextPoint.x + textWidth, mTextPoint.y + mTextRect.height() * mOffsetCoefficient + offset, mTextPaint);
        } else if (mChangeNum < mLikeNum) {
            //点赞数变小
            canvas.drawText(mTargetVariantNumStr, mTextPoint.x + textWidth, mTextPoint.y - mTextRect.height() * mOffsetCoefficient + offset, mTextPaint);
        }
    }

    /**
     * 绘制点赞图标
     *
     * @param canvas
     */
    private void drawLikeIcn(Canvas canvas) {
        mPaint.setAlpha((int) (mAlphaArg * 255));
        canvas.save();
        canvas.translate(mShiningPoint.x, mShiningPoint.y);
        if (mIsLike) {
            canvas.drawBitmap(mShiningBitmap, 0, 0, mPaint);
            if (mRadiusArg < 1.1f) {
                canvas.drawCircle(mCircleCenterPoint.x, mCircleCenterPoint.y, mRadius * mRadiusArg, mCirclePaint);
            }
        }
        canvas.restore();

        canvas.save();
        canvas.translate(mLikePoint.x, mLikePoint.y);
        mMatrix.reset();
        mMatrix.postScale(mScaleArg, mScaleArg);
        canvas.drawBitmap(mIsLike ? mLikeBitmap : mUnlikeBitmap, mMatrix, mPaint);
        canvas.restore();
    }
}
