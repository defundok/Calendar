package com.bugcatcher.calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bugcatcher.calendar.view.CalendarPageTwo;
import com.bugcatcher.calendar.view.util.CalendarDay;
import com.bugcatcher.calendar.view.util.DateRange;

/**
 * Created by FEN ZENG on 2017/3/13 54.
 * Description:
 */

public class SampleTwoFragment extends Fragment {
    private View mResetV;
    private View mConfirmV;
    private View mPreV;
    private View mNexV;
    private TextView mTvLocation, mTvYearMonth;
    private boolean mIsTop, mIsBottom;
    private ViewPager mPager;
    private CalendarPageTwo mCalendarPage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_calendar_pager_container, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPreV = view.findViewById(R.id.pre);
        mNexV = view.findViewById(R.id.nex);
        mTvLocation = (TextView) view.findViewById(R.id.tv_location);
        mTvYearMonth = (TextView) view.findViewById(R.id.tv_year_month);
        mConfirmV = view.findViewById(R.id.btn_confirm);
        mResetV = view.findViewById(R.id.btn_reset);

        ////////////////////////////////////////////////////////////////////////////////////////////

        mPager = (ViewPager) view.findViewById(R.id.vp);
        mPager.setOffscreenPageLimit(2);
        //配置日期范围
        CalendarDay today = CalendarDay.today();
        //最小日期为当天减0年
        CalendarDay minDay = CalendarDay.from(today.getYear() - 0, today.getMonth(), today.getDay());
        //最大日期为当天加200年
        CalendarDay maxDay = CalendarDay.from(today.getYear() + 200, today.getMonth(), today.getDay());
        mCalendarPage = new CalendarPageTwo(getContext(), new DateRange(minDay, maxDay));
        mCalendarPage.bindViewPager(mPager);
        determineTopBottom(mPager.getCurrentItem());
        ////////////////////////////////////////////////////////////////////////////////////////////

        setListeners();
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

        /**
         * 确定
         */
        mConfirmV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), mCalendarPage.getSelectedDate() + " ", Toast.LENGTH_SHORT).show();
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
