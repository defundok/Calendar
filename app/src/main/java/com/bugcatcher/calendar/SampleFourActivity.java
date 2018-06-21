package com.bugcatcher.calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bugcatcher.calendar.model.RentalMonitor;
import com.bugcatcher.calendar.view.CalendarPageFour;
import com.bugcatcher.calendar.view.CalendarPageThree;
import com.bugcatcher.calendar.view.util.CalendarDay;
import com.bugcatcher.calendar.view.util.CalendarUtils;
import com.bugcatcher.calendar.view.util.DateRange;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by 95 on 2017/3/29.
 */

public class SampleFourActivity extends AppCompatActivity {
    private View mResetV;
    private View mConfirmV;
    private View mPreV;
    private View mNexV;
    private TextView mTvYearMonth;
    private boolean mIsTop, mIsBottom;
    private ViewPager mPager;
    private CalendarPageFour mCalendarPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_calendar_pager_container);
        mPreV = findViewById(R.id.pre);
        mNexV = findViewById(R.id.nex);
        mTvYearMonth = (TextView) findViewById(R.id.tv_year_month);
        mConfirmV = findViewById(R.id.btn_confirm);
        mResetV = findViewById(R.id.btn_reset);

        ///////////////////////////////初始化viewpager///////////////////////////////////////////
        mPager = (ViewPager) findViewById(R.id.vp);
        mPager.setOffscreenPageLimit(2);
        //配置日期范围
        CalendarDay today = CalendarDay.today();
        //最小日期为当天减0年
        CalendarDay minDay = CalendarDay.from(today.getYear(), today.getMonth(), today.getDay());
        //最大日期为当天加200年
        CalendarDay maxDay = CalendarDay.from(today.getYear() + 200, today.getMonth(), today.getDay());
        mCalendarPage = new CalendarPageFour(SampleFourActivity.this, minDay, maxDay);
        mCalendarPage.bindViewPager(mPager);
        /////////////////////////////////////////////////////////////////////////////////////////
        setListeners();

        //绑定日期
        mCalendarPage.bindData(generateData());
        determineTopBottom(mPager.getCurrentItem());

    }

    /**
     * 模拟数据
     */
    private RentalMonitor generateData() {
        RentalMonitor rm = new RentalMonitor();
        rm.setRental_expiry_date(System.currentTimeMillis() / 1000 + 365 * 24 * 3600);
        //由于数据格式相同，所以依然是个List,只不过长度为1
        ArrayList<RentalMonitor.Schedule> schedules = new ArrayList<>();
        ArrayList<RentalMonitor.Dots> dots = new ArrayList<>();
        RentalMonitor.Schedule schedule = new RentalMonitor.Schedule();
        schedule.setStart_time(System.currentTimeMillis() / 1000 + 20 * 24 * 3600);
        schedule.setEnd_time(System.currentTimeMillis() / 1000 + 40 * 24 * 3600);
        schedules.add(schedule);
        RentalMonitor.Dots dot = new RentalMonitor.Dots();
        dot.setDate(System.currentTimeMillis() / 1000 + (3 + 20) * 24 * 3600);
        dot.setDots(1);
        dots.add(dot);
        rm.setLastRentalStart(System.currentTimeMillis() / 1000 + 15 * 24 * 3600);
        rm.setLastRentalEnd(System.currentTimeMillis() / 1000 + 20 * 24 * 3600);
        rm.setMax_use_days(10);
        rm.setMin_use_days(3);
        rm.setDefault_back_days(6);
        rm.setClean_days(1);
        rm.setSend_express_days(0);
        rm.setReturn_express_days(1);
        rm.setSchedule(schedules);
        rm.setDate_dots(dots);
        return rm;
    }


    public void setListeners() {
        /**
         * 上月
         */
        mPreV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mPager.getCurrentItem();
                mPager.setCurrentItem(position - 1, true);
            }
        });

        /**
         * 下月
         */
        mNexV.setOnClickListener(new View.OnClickListener() {
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
                mTvYearMonth.setText(mCalendarPage.getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //重置
        mResetV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarPage.cleanSelected();
            }
        });

        mConfirmV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] duration = mCalendarPage.getSelectedRange();
                Toast.makeText(SampleFourActivity.this, duration[0] + " ~ " + duration[3], Toast.LENGTH_SHORT).show();
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
        boolean alreadyBottom = position == mCalendarPage.getCount() - 1;
        if (alreadyBottom != mIsBottom) {
            mIsBottom = alreadyBottom;
            mNexV.setEnabled(!mIsBottom);
        }
    }
}

