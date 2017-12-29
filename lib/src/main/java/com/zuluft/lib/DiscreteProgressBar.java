package com.zuluft.lib;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscreteProgressBar
        extends
        View {

    public static final int DEFAULT_ANIMATION_DURATION = 300;

    private Drawable mInactiveProgressIndicator;
    private Drawable mActiveProgressIndicator;
    private Drawable mSeparator;
    private int mProgressIndicatorSize;
    private int mSeparatorHeight;
    private int mSeparatorWidth;
    private int mSeparatorPadding;
    private int mAnimationDuration;

    private int mMaxProgress;
    private int mCurrentProgress;

    private int mActiveIndicatorColor;
    final int DEFAULT_ACTIVE_INDICATOR_COLOR = Color.GREEN;



    public DiscreteProgressBar(Context context) {
        super(context);
        init(null);
    }

    public DiscreteProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DiscreteProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    public void setActiveIndicatorColor(int color){
         this.mActiveIndicatorColor = color;
         mActiveProgressIndicator.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
         invalidate();
    }

    public final void setMaxProgress(final int maxProgress) {
        mMaxProgress = maxProgress;
    }

    public final void setCurrentProgress(int progress) {
        if (progress >= mMaxProgress) {
            progress = mMaxProgress - 1;
        } else if (progress < 0) {
            progress = 0;
        }
        final int currentProgress = progress;
        post(new Runnable() {
            @Override
            public void run() {
                if (calculateProgressBarWidth() > getWidth()) {
                    int singleItemWidth = calculateSingleItemWidth();
                    int visibleItems = getWidth() / singleItemWidth;
                    int targetTranslateX;
                    if (currentProgress <= visibleItems / 2) {
                        targetTranslateX = 0;
                    } else if (currentProgress + visibleItems >= mMaxProgress) {
                        targetTranslateX = calculateProgressBarWidth() - getWidth();
                    } else {
                        targetTranslateX = (currentProgress - visibleItems / 2) * singleItemWidth;
                    }
                    animate().translationX(-targetTranslateX)
                            .setDuration(mAnimationDuration).start();
                }
                mCurrentProgress = currentProgress;
                invalidate();
            }
        });
    }

    private int calculateSingleItemWidth() {
        return mProgressIndicatorSize + 2 * mSeparatorPadding + mSeparatorWidth;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), mProgressIndicatorSize);
    }

    private void init(@Nullable final AttributeSet attrs) {
        final Resources resources = getResources();
        final TypedArray typedArray = getContext()
                .obtainStyledAttributes(attrs, R.styleable.DiscreteProgressBar);
        mInactiveProgressIndicator = typedArray
                .getDrawable(R.styleable.DiscreteProgressBar_inactiveProgressIndicator);
        if (mInactiveProgressIndicator == null) {
            mInactiveProgressIndicator =
                    getDrawableByResId(R.drawable.ic_inactive_progress_indicator);
        }
        mActiveProgressIndicator = typedArray
                .getDrawable(R.styleable.DiscreteProgressBar_activeProgressIndicator);
        if (mActiveProgressIndicator == null) {
            mActiveProgressIndicator =
                    getDrawableByResId(R.drawable.ic_active_vector);


            String attrColor = typedArray.getString(R.styleable.DiscreteProgressBar_activeIndicatorColor);

            mActiveIndicatorColor = validateColor(attrColor);
            mActiveProgressIndicator.setColorFilter(mActiveIndicatorColor, PorterDuff.Mode.SRC_ATOP);
        }
        mSeparator = typedArray
                .getDrawable(R.styleable.DiscreteProgressBar_separator);
        if (mSeparator == null) {
            mSeparator =
                    getDrawableByResId(R.drawable.ic_separator_line);
        }
        mProgressIndicatorSize = typedArray
                .getDimensionPixelSize(R.styleable.DiscreteProgressBar_indicatorSize,
                        resources
                                .getDimensionPixelSize(R.dimen.progress_indicator_default_size));
        mSeparatorWidth = typedArray
                .getDimensionPixelSize(R.styleable.DiscreteProgressBar_separatorWidth,
                        resources
                                .getDimensionPixelSize(R.dimen.progress_separator_default_width));
        mSeparatorHeight = typedArray
                .getDimensionPixelSize(R.styleable.DiscreteProgressBar_separatorHeight,
                        resources
                                .getDimensionPixelSize(R.dimen.progress_separator_default_height));
        mSeparatorPadding = typedArray
                .getDimensionPixelSize(R.styleable.DiscreteProgressBar_separatorPadding,
                        resources
                                .getDimensionPixelSize(R.dimen.progress_separator_default_padding));
        mAnimationDuration = typedArray
                .getInteger(R.styleable.DiscreteProgressBar_animationDuration,
                        DEFAULT_ANIMATION_DURATION);
        typedArray.recycle();
    }

    private Drawable getDrawableByResId(@DrawableRes final int drawableRes) {
        return ContextCompat.getDrawable(getContext(), drawableRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int progressBarWidth = calculateProgressBarWidth();
        int indicatorStartX = calculateInitialX(getWidth(), progressBarWidth);
        int separatorStartY = (mProgressIndicatorSize - mSeparatorHeight) / 2;
        for (int i = 0; i < mMaxProgress; i++) {
            Drawable progressIndicatorDrawable = i <= mCurrentProgress ?
                    mActiveProgressIndicator : mInactiveProgressIndicator;
            progressIndicatorDrawable.setBounds(
                    indicatorStartX,
                    0,
                    indicatorStartX = indicatorStartX + mProgressIndicatorSize,
                    mProgressIndicatorSize);
            progressIndicatorDrawable.draw(canvas);
            if (i != mMaxProgress - 1) {
                indicatorStartX += mSeparatorPadding;
                mSeparator.setBounds(indicatorStartX,
                        separatorStartY,
                        indicatorStartX += mSeparatorWidth,
                        separatorStartY + mSeparatorHeight);
                mSeparator.draw(canvas);
                indicatorStartX += mSeparatorPadding;
            }
        }
    }

    private int validateColor(String color){

        if(color == null)
            return DEFAULT_ACTIVE_INDICATOR_COLOR;


        String pattern =  "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3}|[A-Fa-f0-9]{8})$";
        Pattern colorPattern = Pattern.compile(pattern);
        Matcher m = colorPattern.matcher(color);

        return m.matches() ? Color.parseColor(color) : DEFAULT_ACTIVE_INDICATOR_COLOR;
    }

    private int calculateProgressBarWidth() {
        final int paddingForOneSeparator = 2 * mSeparatorPadding;
        final int separatorWidthWithPadding = mSeparatorWidth + paddingForOneSeparator;
        return mMaxProgress *
                (mProgressIndicatorSize + separatorWidthWithPadding) - separatorWidthWithPadding;
    }

    private int calculateInitialX(final int width, final int progressBarWidth) {
        int result = (width - progressBarWidth) / 2;
        return result < 0 ? 0 : result;
    }
}
