package com.bugcatcher.calendar.view.util;

import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Description:
 */

public class DateRange {

    private final CalendarDay min;
    private final int count;

    /**
     * 按页数存储该页的日期
     */
    private SparseArray<List<CalendarDay>> dayCache = new SparseArray<>();


    public DateRange(@NonNull CalendarDay min, @NonNull CalendarDay max) {
        this.min = CalendarDay.from(min.getYear(), min.getMonth(), 1);
        max = CalendarDay.from(max.getYear(), max.getMonth(), 1);
        this.count = indexOf(max) + 1;
    }

    public int getCount() {
        return count;
    }

    /**
     * 获取该天所在页数
     */
    public int indexOf(CalendarDay day) {
        int yDiff = day.getYear() - min.getYear();
        int mDiff = day.getMonth() - min.getMonth();
        return (yDiff * 12) + mDiff;
    }

    /**
     * @return 该下标位所对应的月份的第一天
     */
    public CalendarDay getFirstDayOfMonthByPosition(int position) {
        int numY = position / 12;
        int numM = position % 12;

        int year = min.getYear() + numY;
        int month = min.getMonth() + numM;
        if (month >= 12) {
            year += 1;
            month -= 12;
        }

        return CalendarDay.from(year, month, 1);
    }
    public int[] getMonthRange(int position) {
        int[] monthRange = new int[2];
        int numY = position / 12;
        int numM = position % 12;

        int year = min.getYear() + numY;
        int month = min.getMonth() + numM;
        if (month >= 12) {
            year += 1;
            month -= 12;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        monthRange[0] = CalendarDay.from(calendar).toInteger();
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        monthRange[1] = CalendarDay.from(calendar).toInteger();
        return monthRange;
    }

    /**
     * @param position 页数
     */
    public List<CalendarDay> getItem(int position) {
        List<CalendarDay> re = dayCache.get(position);
        if (re != null) {
            return re;
        }

        int numY = position / 12;
        int numM = position % 12;

        int year = min.getYear() + numY;
        int month = min.getMonth() + numM;
        if (month >= 12) {
            year += 1;
            month -= 12;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        re = new ArrayList<>();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        for (int i = 0; i < dayOfWeek - 1; i++) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            re.add(0, CalendarDay.from(calendar));
        }
        calendar.set(year, month, 1);
        final int count = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < count; i++) {
            final CalendarDay day = CalendarDay.from(calendar);
            re.add(day);
            calendar.add(calendar.DAY_OF_MONTH, 1);
        }
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek != Calendar.SUNDAY) {
            for (int i = 0; i < 7 - dayOfWeek + 1; i++) {
                re.add(CalendarDay.from(calendar));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        dayCache.put(position, re);
        return re;
    }
}
