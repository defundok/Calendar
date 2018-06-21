package com.bugcatcher.calendar.model;

import android.util.Log;

import com.bugcatcher.calendar.view.util.CalendarDay;
import com.bugcatcher.calendar.view.util.CalendarUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by FEN ZENG on 2017/3/10 23.
 * Description:
 */

public class RentalMonitor {
    private long rental_expiry_date;
    private List<Schedule> schedule;
    private int send_express_days;
    private int return_express_days;
    private int max_use_days;
    private int clean_days;
    private int min_use_days;
    private int default_back_days;
    private ArrayList<int[]> mSelectionRangeList;//用户可点击的范围
    private List<Dots> date_dots;
    private long lastRentalStart;
    private long lastRentalEnd;

    public long getRental_expiry_date() {
        return rental_expiry_date;
    }

    public void setRental_expiry_date(long rental_expiry_date) {
        this.rental_expiry_date = rental_expiry_date;
    }

    public List<Schedule> getSchedule() {
        return schedule;
    }

    public void setSchedule(List<Schedule> schedule) {
        this.schedule = schedule;
    }

    public int getSend_express_days() {
        return send_express_days;
    }

    public void setSend_express_days(int send_express_days) {
        this.send_express_days = send_express_days;
    }

    public int getReturn_express_days() {
        return return_express_days;
    }

    public void setReturn_express_days(int return_express_days) {
        this.return_express_days = return_express_days;
    }

    public int getMax_use_days() {
        return max_use_days;
    }

    public void setMax_use_days(int max_use_days) {
        this.max_use_days = max_use_days;
    }

    public int getClean_days() {
        return clean_days;
    }

    public void setClean_days(int clean_days) {
        this.clean_days = clean_days;
    }

    public int getMin_use_days() {
        return min_use_days;
    }

    public void setMin_use_days(int min_use_days) {
        this.min_use_days = min_use_days;
    }

    public long getLastRentalStart() {
        return lastRentalStart;
    }

    public void setLastRentalStart(long lastRentalStart) {
        this.lastRentalStart = lastRentalStart;
    }

    public long getLastRentalEnd() {
        return lastRentalEnd;
    }

    public void setLastRentalEnd(long lastRentalEnd) {
        this.lastRentalEnd = lastRentalEnd;
    }

    public int getDefault_back_days() {
        return default_back_days;
    }

    public void setDefault_back_days(int default_back_days) {
        this.default_back_days = default_back_days;
    }

    public List<Dots> getDate_dots() {
        return date_dots;
    }

    public void setDate_dots(List<Dots> date_dots) {
        this.date_dots = date_dots;
    }

    /**
     * 获取指定周期内的用户可选取日期范围
     * TODO 需要根据实际情况重写
     *
     * @return 0下标最早可选出租日期，1下标最晚可选出租日期 3下标 该周期的最晚返还日期
     */
    public int[] getSelectionRange(Schedule schedule) {
        int range[] = new int[3];
        Calendar calendar = Calendar.getInstance();
        schedule.getStartDay().copyTo(calendar);
        //周期开始时间加上送到用户那边物流时间作为用户可选择的最早租借时间
        calendar.add(Calendar.DAY_OF_MONTH, send_express_days );
        range[0] = CalendarUtils.convert2Integer(calendar);
        schedule.getEndDay().copyTo(calendar);
        //获取周期的最晚返还日期，为周期结束日期减去返回物流时间，清洗时间
        calendar.add(Calendar.DAY_OF_MONTH, -(return_express_days + clean_days));
        range[2] = CalendarUtils.convert2Integer(calendar);
        //获取最晚租借时间 !!!!不知道是不是最晚返还日期减去最大使用日期
        calendar.add(Calendar.DAY_OF_MONTH, -(min_use_days - 1));
        range[1] = CalendarUtils.convert2Integer(calendar);
        //获取会员到期日
        calendar = Calendar.getInstance();
        calendar.setTime(new Date(rental_expiry_date * 1000));
        //获取会员到期前的最晚归还日期
        calendar.add(Calendar.DAY_OF_MONTH, -(return_express_days + clean_days));

        //周期的实际最晚返还日期为两者最小值
        range[2] = Math.min(range[2], CalendarUtils.convert2Integer(calendar));
        //会员到期日减去最小使用时间、送回物流时间和清洗时间作为会员到期前的最晚租借日期
        calendar.add(Calendar.DAY_OF_MONTH, -( min_use_days - 1));
        int expiryDate = CalendarUtils.convert2Integer(calendar);

        if (range[0] > expiryDate) {
            //租借日期超出会员到期前的最晚租借时间，返回空
            return null;
        } else {
            range[1] = Math.min(expiryDate, range[1]);
            if (range[0] >= range[1]) return null;
        }
        return range;
    }

    /**
     * 获取自动选择的日期范围
     * TODO 需要根据实际情况重写
     *
     * @param fromDay       用户点击租借的日期
     * @param selectedRange 用于存储自动选择的日期范围
     * @return 是否有匹配的自动选择日期
     */
    public boolean getSelectableRange(CalendarDay fromDay, int[] selectedRange) {
        //获取用户选择的租借日期
        int startDay = fromDay.toInteger();
        //获取会员到期日
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(rental_expiry_date * 1000));
        //TODO unchecked
        //会员到期日减去送回物流时间和清洗时间作为
        //会员到期前的最晚返还日期
        calendar.add(Calendar.DAY_OF_MONTH, -(clean_days + return_express_days));

        //TODO unchecked
        //获取最早返还日期，为用户选择的起租日期加上最小使用天数减一
        fromDay.copyTo(calendar);
        calendar.add(Calendar.DAY_OF_MONTH, min_use_days - 1);
        int minEndDay = CalendarUtils.convert2Integer(calendar);

        //TODO unchecked
        //获取默认返还日期
        calendar.add(Calendar.DAY_OF_MONTH, default_back_days - min_use_days);
        int defEndDay = CalendarUtils.convert2Integer(calendar);

        //TODO unchecked
        //获取最晚返还日期
        calendar.add(Calendar.DAY_OF_MONTH, max_use_days - default_back_days);
        int maxEndDay = CalendarUtils.convert2Integer(calendar);
        getSelectionRangeList();

        int lastDate = CalendarUtils.convert2Integer(calendar);
        Log.i("startDay", "sd " + startDay + " ned " + minEndDay + " xed " + maxEndDay + " ld " + lastDate);
        for (int[] range : mSelectionRangeList) {
            Log.i("range", range[0] + "~" + range[1] + "~" + range[2]);
            if (range[0] <= startDay && range[1] >= startDay) {
                if (minEndDay > range[2]) {//如果使用最少天数的归还日期还是大于 周期最晚归还日期的话返回false
                    return false;
                }
                selectedRange[0] = startDay;//开始租借日
                selectedRange[1] = Math.min(range[2], maxEndDay);//最晚归还日
                selectedRange[2] = Math.min(range[2], minEndDay);//最早归还日期
                selectedRange[3] = Math.min(range[2], defEndDay);//默认归还日期
                Log.i("selectedRange", selectedRange[0] + " " + selectedRange[1] + " " + selectedRange[2] + " " + selectedRange[3]);
                return true;
            }
        }
        return false;
    }

    /**
     * @return 用户可选取日期范围集合
     */
    public List<int[]> getSelectionRangeList() {
        if (mSelectionRangeList == null) {
            mSelectionRangeList = new ArrayList<>();
            for (RentalMonitor.Schedule s : schedule) {
                int[] range = getSelectionRange(s);
                if (range != null) {
                    mSelectionRangeList.add(range);
                }
            }
        }
        return mSelectionRangeList;
    }


    public static class Schedule {
        private long start_time;
        private long end_time;
        private int days;
        private CalendarDay startDay;//startTime转CalendarDay对象
        private CalendarDay endDay;//endTime转CalendarDay对象

        public long getStart_time() {
            return start_time;
        }

        public void setStart_time(long start_time) {
            this.start_time = start_time;
            this.startDay = null;
        }

        public long getEnd_time() {
            return end_time;
        }

        public void setEnd_time(long end_time) {
            this.end_time = end_time;
            this.endDay = null;
        }

        public int getDays() {
            return days;
        }

        public void setDays(int days) {
            this.days = days;
        }

        public CalendarDay getStartDay() {
            return startDay == null ? CalendarDay.from(new Date(start_time * 1000)) : startDay;
        }

        public CalendarDay getEndDay() {
            return endDay == null ? CalendarDay.from(new Date(end_time * 1000)) : endDay;
        }
    }

    public static class Dots {
        private long date;
        private int dots;
        private int integerDate;

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public int getDots() {
            return dots;
        }

        public void setDots(int dots) {
            this.dots = dots;
        }

        public int getIntegerDate() {
            if (integerDate == 0) {
                integerDate = CalendarDay.from(new Date(date * 1000)).toInteger();
            }
            return integerDate;
        }
    }
}
