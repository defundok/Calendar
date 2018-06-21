package com.bugcatcher.calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bugcatcher.calendar.view.CalendarPageThree;
import com.bugcatcher.calendar.view.util.CalendarDay;
import com.bugcatcher.calendar.view.util.CalendarUtils;
import com.bugcatcher.calendar.view.util.DateRange;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by FEN ZENG on 2017/3/13 27.
 * Description:
 */

public class SampleThreeActivity extends AppCompatActivity {
    private View mPreV;
    private View mNexV;
    private TextView mTvYearMonth;
    private boolean mIsTop, mIsBottom;
    private ViewPager mPager;
    private CalendarPageThree mCalendarPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_three);
        mPreV = findViewById(R.id.pre);
        mNexV = findViewById(R.id.nex);
        mTvYearMonth = (TextView) findViewById(R.id.tv_year_month);

        mPager = (ViewPager) findViewById(R.id.vp);
        mPager.setOffscreenPageLimit(2);
        //配置日期范围
        CalendarDay today = CalendarDay.today();
        //最小日期为当天减0年
        CalendarDay minDay = CalendarDay.from(today.getYear() - 200, today.getMonth(), today.getDay());
        //最大日期为当天加200年
        CalendarDay maxDay = CalendarDay.from(today.getYear() + 0, today.getMonth(), today.getDay());
        mCalendarPage = new CalendarPageThree(SampleThreeActivity.this, new DateRange(minDay, maxDay));
        mCalendarPage.bindViewPager(mPager);
        mPager.setCurrentItem(mCalendarPage.getCount() - 1, false);
        determineTopBottom(mPager.getCurrentItem());


        //绑定返回的签到日期
        mCalendarPage.bindData(generateSignedData());

        setListeners();
    }

    public List<Integer> generateSignedData() {
        ArrayList<Integer> list = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        CalendarDay.today().copyTo(calendar);
        for (int i = 0; i < 30; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            Log.i("dates", CalendarUtils.convert2Integer(calendar) + "");
            list.add(CalendarUtils.convert2Integer(calendar));
        }
        return list;
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
