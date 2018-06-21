package com.bugcatcher.calendar.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bugcatcher.calendar.R;
import com.bugcatcher.calendar.model.RentalMonitor;
import com.bugcatcher.calendar.view.util.CalendarDay;

/**
 * Created by FEN ZHAO on 2017/3/10 03.
 * Description:
 */

public class CalendarLayout extends FrameLayout {
    private View mResetV;
    private View mConfirmV;
    private View mPreV;
    private View mNexV;
    private TextView mTvLocation, mTvYearMonth;
    private boolean mIsTop, mIsBottom;
    private ViewPager mPager;
    private CalendarPageOne mCalendarPageOne;

    public CalendarLayout(Context context) {
        this(context, null);
    }

    public CalendarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_calendar_pager_container, this, true);
        mPreV = findViewById(R.id.pre);
        mNexV = findViewById(R.id.nex);
        mTvLocation = (TextView) findViewById(R.id.tv_location);
        mTvYearMonth = (TextView) findViewById(R.id.tv_year_month);
        mConfirmV = findViewById(R.id.btn_confirm);
        mResetV = findViewById(R.id.btn_reset);


        ///////////////////////////////初始化viewpager///////////////////////////////////////////
        mPager = (ViewPager) findViewById(R.id.vp);
        mPager.setOffscreenPageLimit(2);
        //配置日期范围
        CalendarDay today = CalendarDay.today();
        //最小日期为当天减0年
        CalendarDay minDay = CalendarDay.from(today.getYear() - 0, today.getMonth(), today.getDay());
        //最大日期为当天加200年
        CalendarDay maxDay = CalendarDay.from(today.getYear() + 200, today.getMonth(), today.getDay());
        mCalendarPageOne = new CalendarPageOne(getContext(), minDay, maxDay);
        mCalendarPageOne.bindViewPager(mPager);
        /////////////////////////////////////////////////////////////////////////////////////////
        setListeners();
    }

    public void setListeners() {
        /**
         * 上月
         */
        mPreV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mPager.getCurrentItem();
                mPager.setCurrentItem(position - 1, true);
            }
        });

        /**
         * 下月
         */
        mNexV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mPager.getCurrentItem();
                mPager.setCurrentItem(position + 1, true);
            }
        });

        /**
         * 边界判断
         * 标题月份改变
         */
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                determineTopBottom(position);
                mTvYearMonth.setText(mCalendarPageOne.getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        //重置
        mResetV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarPageOne.cleanSelected();
            }
        });

        mConfirmV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] duration = mCalendarPageOne.getSelectedRange();
                Toast.makeText(getContext(), duration[0] + " ~ " + duration[3], Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 边界判断
     */
    private void determineTopBottom(int position) {
        boolean alreadyTop = position == 0;
        if (alreadyTop != mIsTop) {
            mIsTop = alreadyTop;
            mPreV.setEnabled(!mIsTop);
        }
        boolean alreadyBottom = position == mCalendarPageOne.getCount() - 1;
        if (alreadyBottom != mIsBottom) {
            mIsBottom = alreadyBottom;
            mNexV.setEnabled(!mIsBottom);
        }
    }

    /**
     * 地区
     */
    public CalendarLayout setLocation() {
        mTvLocation.setText("上海   >");
        return this;
    }

    /**
     * @param data 租赁信息
     */
    public void setRentalData(RentalMonitor data) {
        mCalendarPageOne.bindData(data);
        determineTopBottom(mPager.getCurrentItem());
    }
}
