package com.bugcatcher.calendar.model;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.bugcatcher.calendar.view.util.CalendarUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by 95 on 2017/5/4.
 */

public class REntity {
    public static final int UNSET = 0;
    public int send_express_days;
    public int max_use_days;
    public int min_use_days;
    public long rental_expiry_date;
    public int default_back_days;
    public List<RSchedule> schedule;
    public List<RDots> date_dots;
    public List<RPauseDate> pause_dates;
    public List<RPlan> plans;
    public int new_return_need_days;
    public String dots_not_enough_message;
    public boolean show_dots_not_enough_message;
    private ArrayMap<Integer, RDots> dots;

    public RDots isDotsDay(int dateFMT) {
        if (dots == null) {
            dots = new ArrayMap<>();
            for (RDots dot : date_dots) {
                dots.put(dot.getDateFMT(), dot);
            }
        }
        RDots dot = dots.get(dateFMT);
        if (dot == null) {
            Log.i("date = ", dateFMT + "");
            for (int i = 0; i < dots.size(); i++) {
                Log.i("dots.key", dots.keyAt(i) + "");
            }
        }
        return dots.get(dateFMT);
    }

    public RPauseDate isPauseDay(int dateFMT) {
        for (RPauseDate date : pause_dates) {
            if (date.dates.getStartTimeFMT() <= dateFMT && date.dates.getEndTimeFMT() >= dateFMT) {
                return date;
            }
        }
        return null;
    }

    public RSchedule isDeliveryDay(int dateFMT) {
        for (RSchedule s : schedule) {
            if (dateFMT == s.getDeliveryDateFMT())
                return s;
        }
        return null;
    }

    public RPlan isPlanDay(int dateFMT) {
        for (RPlan p : plans) {
            if (p.dates.getStartTimeFMT() <= dateFMT && p.dates.getEndTimeFMT() >= dateFMT) {
                return p;
            }
        }
        return null;
    }


    public static class RSchedule {
        public long start_time;
        public long end_time;
        public int days;
        public List<Long> send_dates;
        public boolean send_dates_show_empty;
        public long delivery_date;
        public List<Long> min_use_dates;
        public List<Long> can_return_dates;
        public long default_return;
        public int currentReturnDate;// 当前返还时间 （额外添加）
        private int startTime;
        private int endTime;
        private int deliveryDate;
        private int defaultReturn;
        private List<Integer> sendDates;
        private List<Integer> minUseDates;
        private List<Integer> canReturnDates;

        @Override
        public String toString() {
            return "RSchedule{" +
                    "start_time=" + start_time +
                    ", end_time=" + end_time +
                    ", days=" + days +
                    ", send_dates=" + send_dates +
                    ", send_dates_show_empty=" + send_dates_show_empty +
                    ", delivery_date=" + delivery_date +
                    ", min_use_dates=" + min_use_dates +
                    ", can_return_dates=" + can_return_dates +
                    ", default_return=" + default_return +
                    ", currentReturnDate=" + currentReturnDate +
                    ", startTime=" + startTime +
                    ", endTime=" + endTime +
                    ", deliveryDate=" + deliveryDate +
                    ", defaultReturn=" + defaultReturn +
                    ", sendDates=" + sendDates +
                    ", minUseDates=" + minUseDates +
                    ", canReturnDates=" + canReturnDates +
                    '}';
        }

        public int getStartTimeFMT() {
            if (startTime == UNSET) {
                startTime = CalendarUtils.convert2Integer(start_time);
            }
            return startTime;
        }

        public int getEndTimeFMT() {
            if (endTime == UNSET) {
                endTime = CalendarUtils.convert2Integer(end_time);
            }
            return endTime;
        }

        public int getDefaultReturnFMT() {
            if (defaultReturn == UNSET) {
                defaultReturn = CalendarUtils.convert2Integer(default_return);
            }
            return defaultReturn;
        }

        public int getDeliveryDateFMT() {
            if (deliveryDate == UNSET) {
                deliveryDate = CalendarUtils.convert2Integer(delivery_date);
            }
            return deliveryDate;
        }

        public List<Integer> getSendDatesFMT() {
            if (sendDates == null) {
                sendDates = getFMTDates(send_dates);
            }
            return sendDates;
        }

        public boolean isSendDate(int dateFMT) {
            getSendDatesFMT();
            for (int d : sendDates) {
                if (d == dateFMT)
                    return true;
            }
            return false;
        }

        public boolean isCanReturnDate(int dateFMT) {
            getCanReturnDatesFMT();
            for (int d : canReturnDates) {
                if (d == dateFMT)
                    return true;
            }
            return false;
        }

        public boolean isMinUseDate(int dateFMT) {
            getMinUseDatesFMT();
            for (int d : minUseDates) {
                if (d == dateFMT)
                    return true;
            }
            return false;
        }

        /*
        * 判断积点范围内的日期 范围是起租日期到dateFMT;
        * */
        public int isAvailableReturnDate(REntity entity, int dateFMT, int userDots) {
            getDeliveryDateFMT();
            getMinUseDatesFMT();
            getCanReturnDatesFMT();
            if (dateFMT < canReturnDates.get(0)) return -1;

            RDots dots = entity.isDotsDay(deliveryDate);
            if (dots != null) {
                if (dots.dots > userDots) return -1;
            }
            for (Integer d : minUseDates) {
                dots = entity.isDotsDay(d);
                if (dots != null) {
                    if (dots.dots > userDots) return -1;
                }
            }

            int i = 0;
            for (Integer d : canReturnDates) {
                dots = entity.isDotsDay(d);
                if (dots != null) {
                    if (dots.dots > userDots) return i == 0 ? -1 : canReturnDates.get(i - 1);
                    if (d >= dateFMT) return dateFMT;
                }
                i++;
            }
            return -1;
        }

        public List<Integer> getMinUseDatesFMT() {
            if (minUseDates == null) {
                minUseDates = getFMTDates(min_use_dates);
            }
            return minUseDates;
        }

        public List<Integer> getCanReturnDatesFMT() {
            if (canReturnDates == null) {
                canReturnDates = getFMTDates(can_return_dates);
            }
            return canReturnDates;
        }

    }

    public static class RDots {
        public long date;
        public int dots;
        public String dots_text;
        private int dateFMT;

        public int getDateFMT() {
            if (dateFMT == UNSET) {
                dateFMT = CalendarUtils.convert2Integer(date);
            }
            return dateFMT;
        }
    }

    public static class RPauseDate {
        public boolean click_show_message;
        public String click_message;
        public RSchedule dates;
    }

    public static class RPlan {
        public boolean click_show_message;
        public String click_message;
        public RSchedule dates;
    }

    private static ArrayList<Integer> getFMTDates(List<Long> source) {
        ArrayList<Integer> result = new ArrayList<>();
        for (long second : source) {
            result.add(CalendarUtils.convert2Integer(second));
        }
        return result;
    }
}
