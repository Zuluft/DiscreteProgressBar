package com.zuluft.lib;


import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

public class DiscreteProgressBar
        extends
        View {

    private static final int DEFAULT_ANIMATION_DURATION = 300;

    private static final int DEFAULT_COLOR_MODE = PorterDuff.Mode.SRC_IN.ordinal();

    private static final int EMPTY_PROGRESS_INDICATOR_COLOR = -1;


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
    private int mActiveProgressIndicatorColor;
    private int mActiveProgressIndicatorColorMode;


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


    public void setActiveIndicatorColor(@ColorInt final int color,
                                        @NonNull final PorterDuff.Mode mode) {
        mActiveProgressIndicatorColorMode = mode.ordinal();
        mActiveProgressIndicatorColor = color;
        mActiveProgressIndicator.setColorFilter(color, mode);
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
                    getDrawableByResId(R.drawable.ic_active_progress_indicator);
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

        mActiveProgressIndicatorColor = typedArray.getColor(
                R.styleable.DiscreteProgressBar_activeProgressIndicatorColor,
                EMPTY_PROGRESS_INDICATOR_COLOR
        );

        if (mActiveProgressIndicatorColor != EMPTY_PROGRESS_INDICATOR_COLOR) {
            mActiveProgressIndicatorColorMode = typedArray.getInt(R.styleable
                            .DiscreteProgressBar_activeProgressIndicatorColorMode,
                    DEFAULT_COLOR_MODE);
            mActiveProgressIndicator.setColorFilter(mActiveProgressIndicatorColor,
                    PorterDuff.Mode.values()[mActiveProgressIndicatorColorMode]);
        }
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


    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mActiveProgressIndicatorColor = ss.mActiveIndicatorColor;
        if (mActiveProgressIndicatorColor != EMPTY_PROGRESS_INDICATOR_COLOR) {
            setActiveIndicatorColor(mActiveProgressIndicatorColor,
                    PorterDuff.Mode.values()[ss.mActiveProgressIndicatorColorMode]);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {

        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.mActiveIndicatorColor = mActiveProgressIndicatorColor;
        ss.mActiveProgressIndicatorColorMode = mActiveProgressIndicatorColorMode;
        return ss;
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


    private static class SavedState extends BaseSavedState {

        private int mActiveIndicatorColor;
        private int mActiveProgressIndicatorColorMode;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mActiveIndicatorColor = in.readInt();
            mActiveProgressIndicatorColorMode = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mActiveIndicatorColor);
            out.writeInt(mActiveProgressIndicatorColorMode);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
