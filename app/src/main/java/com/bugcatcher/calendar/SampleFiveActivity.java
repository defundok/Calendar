package com.bugcatcher.calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bugcatcher.calendar.model.REntity;
import com.bugcatcher.calendar.model.RentalMonitor;
import com.bugcatcher.calendar.model.ResponseData;
import com.bugcatcher.calendar.view.CalendarPageFive;
import com.bugcatcher.calendar.view.CalendarPageFour;
import com.bugcatcher.calendar.view.util.CalendarDay;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by 95 on 2017/3/29.
 */

public class SampleFiveActivity extends AppCompatActivity {
    private View mResetV;
    private View mConfirmV;
    private View mPreV;
    private View mNexV;
    private TextView mTvYearMonth;
    private boolean mIsTop, mIsBottom;
    private ViewPager mPager;
    private CalendarPageFive mCalendarPage;

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
        mCalendarPage = new CalendarPageFive(SampleFiveActivity.this, minDay, maxDay);
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
    private REntity generateData() {
        return new Gson().fromJson("{\"status\":\"ok\",\"data\":{\"send_express_days\":1,\"max_use_days\":15,\"min_use_days\":\"5\",\"rental_expiry_date\":1525363199,\"default_back_days\":5,\"schedule\":[{\"start_time\":1495900800,\"end_time\":1496505600,\"days\":8,\"send_dates\":[1495900800],\"send_dates_show_empty\":true,\"delivery_date\":1495987200,\"min_use_dates\":[1495987200,1496073600,1496160000,1496246400],\"can_return_dates\":[1496332800],\"default_return\":1496332800},{\"start_time\":1495987200,\"end_time\":1496592000,\"days\":8,\"send_dates\":[1495987200],\"send_dates_show_empty\":false,\"delivery_date\":1496073600,\"min_use_dates\":[1496073600,1496160000,1496246400,1496332800],\"can_return_dates\":[1496419200],\"default_return\":1496419200},{\"start_time\":1496073600,\"end_time\":1496678400,\"days\":8,\"send_dates\":[1496073600],\"send_dates_show_empty\":false,\"delivery_date\":1496160000,\"min_use_dates\":[1496160000,1496246400,1496332800,1496419200],\"can_return_dates\":[1496505600],\"default_return\":1496505600},{\"start_time\":1496160000,\"end_time\":1496764800,\"days\":8,\"send_dates\":[1496160000],\"send_dates_show_empty\":false,\"delivery_date\":1496246400,\"min_use_dates\":[1496246400,1496332800,1496419200,1496505600],\"can_return_dates\":[1496592000],\"default_return\":1496592000},{\"start_time\":1496246400,\"end_time\":1496851200,\"days\":8,\"send_dates\":[1496246400],\"send_dates_show_empty\":false,\"delivery_date\":1496332800,\"min_use_dates\":[1496332800,1496419200,1496505600,1496592000],\"can_return_dates\":[1496678400],\"default_return\":1496678400}],\"date_dots\":[{\"date\":1493827200,\"dots\":6,\"dots_text\":6},{\"date\":1493913600,\"dots\":6,\"dots_text\":6},{\"date\":1494000000,\"dots\":6,\"dots_text\":6},{\"date\":1494086400,\"dots\":6,\"dots_text\":6},{\"date\":1494172800,\"dots\":6,\"dots_text\":6},{\"date\":1494259200,\"dots\":6,\"dots_text\":6},{\"date\":1494345600,\"dots\":6,\"dots_text\":6},{\"date\":1494432000,\"dots\":6,\"dots_text\":6},{\"date\":1494518400,\"dots\":6,\"dots_text\":6},{\"date\":1494604800,\"dots\":6,\"dots_text\":6},{\"date\":1494691200,\"dots\":6,\"dots_text\":6},{\"date\":1494777600,\"dots\":6,\"dots_text\":6},{\"date\":1494864000,\"dots\":6,\"dots_text\":6},{\"date\":1494950400,\"dots\":6,\"dots_text\":6},{\"date\":1495036800,\"dots\":6,\"dots_text\":6},{\"date\":1495123200,\"dots\":6,\"dots_text\":6},{\"date\":1495209600,\"dots\":6,\"dots_text\":6},{\"date\":1495296000,\"dots\":6,\"dots_text\":6},{\"date\":1495382400,\"dots\":6,\"dots_text\":6},{\"date\":1495468800,\"dots\":6,\"dots_text\":6},{\"date\":1495555200,\"dots\":6,\"dots_text\":6},{\"date\":1495641600,\"dots\":6,\"dots_text\":6},{\"date\":1495728000,\"dots\":6,\"dots_text\":6},{\"date\":1495814400,\"dots\":6,\"dots_text\":6},{\"date\":1495900800,\"dots\":6,\"dots_text\":6},{\"date\":1495987200,\"dots\":6,\"dots_text\":6},{\"date\":1496073600,\"dots\":6,\"dots_text\":6},{\"date\":1496160000,\"dots\":6,\"dots_text\":6},{\"date\":1496246400,\"dots\":6,\"dots_text\":6},{\"date\":1496332800,\"dots\":6,\"dots_text\":6},{\"date\":1496419200,\"dots\":6,\"dots_text\":6},{\"date\":1496505600,\"dots\":6,\"dots_text\":6},{\"date\":1496592000,\"dots\":6,\"dots_text\":6},{\"date\":1496678400,\"dots\":6,\"dots_text\":6},{\"date\":1496764800,\"dots\":6,\"dots_text\":6},{\"date\":1496851200,\"dots\":6,\"dots_text\":6},{\"date\":1496937600,\"dots\":6,\"dots_text\":6},{\"date\":1497024000,\"dots\":6,\"dots_text\":6},{\"date\":1497110400,\"dots\":6,\"dots_text\":6},{\"date\":1497196800,\"dots\":6,\"dots_text\":6},{\"date\":1497283200,\"dots\":6,\"dots_text\":6},{\"date\":1497369600,\"dots\":6,\"dots_text\":6},{\"date\":1497456000,\"dots\":6,\"dots_text\":6},{\"date\":1497542400,\"dots\":6,\"dots_text\":6},{\"date\":1497628800,\"dots\":6,\"dots_text\":6},{\"date\":1497715200,\"dots\":6,\"dots_text\":6}],\"dots_not_enough_message\":\"\\u989d\\u5ea6\\u4e0d\\u8db3\\uff0c\\u65e0\\u6cd5\\u79df\\u8d41\\u8be5\\u7f8e\\u8863\",\"show_dots_not_enough_message\":true,\"plans\":[{\"dates\":{\"start_time\":1494864000,\"end_time\":1495209600},\"click_message\":\"\\u60a8\\u6709\\u4e00\\u4e2a05.16\\u81f305.20\\u7684\\u8863\\u888b\\uff0c\\u662f\\u5426\\u67e5\\u770b\\u8be5\\u8863\\u888b\\uff1f\",\"click_show_message\":true}],\"pause_dates\":[{\"dates\":{\"start_time\":1495641600,\"end_time\":1495814400},\"click_show_message\":true,\"click_message\":\"\\u4e1a\\u52a1\\u4f11\\u4e1a\\u671f\\u95f4\\uff0c\\u65e0\\u6cd5\\u79df\\u8d41\\u8be5\\u7f8e\\u8863\"}],\"new_return_need_days\":2}}",
                ResponseData.class).data;
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
                Toast.makeText(SampleFiveActivity.this, duration[0] + " ~ " + duration[1], Toast.LENGTH_SHORT).show();
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

