package com.bugcatcher.calendar.view;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bugcatcher.calendar.model.RentalMonitor;
import com.bugcatcher.calendar.view.util.CalendarDay;
import com.bugcatcher.calendar.view.util.DateRange;

import java.util.ArrayDeque;


/**
 * Created by FEN ZHAO on 2017/3/11 46.
 * Description:
 */

public class CalendarPageAdapter extends android.support.v4.view.PagerAdapter {
    private AbsCalendarPage mCalendarPage;

    public CalendarPageAdapter(AbsCalendarPage calendarPage) {
        mCalendarPage = calendarPage;
        mCalendarPage.attachToParentAdapter(this);
    }

    @Override
    public int getCount() {
        return mCalendarPage.getCount();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mCalendarPage.createView(container, position);
        container.addView(view);
        return view;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        mCalendarPage.destoryView(position);
        container.removeView((View) object);
    }

    public static abstract class AbsCalendarPage {

        protected CalendarPageAdapter mParentAdapter;

        public abstract int getCount();

        public abstract DateRange getDateRange();

        public abstract String getPageTitle(int position);

        public void attachToParentAdapter(CalendarPageAdapter adapter) {
            mParentAdapter = adapter;
        }

        public abstract View createView(ViewGroup container, int position);

        public abstract void destoryView(int position);

        public void bindViewPager(ViewPager pager) {
            CalendarPageAdapter adapter = new CalendarPageAdapter(this);
            pager.setAdapter(adapter);
        }
    }
}
