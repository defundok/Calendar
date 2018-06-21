package com.bugcatcher.calendar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.bugcatcher.calendar.R;

/**
 * Created by 95 on 2017/3/9.
 */

public class StickyWidthLayout extends FrameLayout {
    private float mHeightDivideWidthRatio;

    public StickyWidthLayout(Context context) {
        this(context, null);
    }

    public StickyWidthLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickyWidthLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.StickyWidthLayout, 0, 0);
        mHeightDivideWidthRatio = a.getFloat(R.styleable.StickyWidthLayout_heightDivideWidthRatio, 1);//默认正方形
        a.recycle();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY
                && mHeightDivideWidthRatio > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (
                    mHeightDivideWidthRatio * MeasureSpec.getSize(widthMeasureSpec)), MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
