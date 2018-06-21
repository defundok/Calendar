package com.bugcatcher.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bugcatcher.calendar.model.RentalMonitor;
import com.bugcatcher.calendar.view.CalendarLayout;

import java.util.ArrayList;

public class SampleOneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_one);
        final CalendarLayout cl = (CalendarLayout) findViewById(R.id.cl);
        cl.setRentalData(generateData());
    }

    /**
     * 模拟数据
     */
    private RentalMonitor generateData() {
        RentalMonitor rm = new RentalMonitor();
        rm.setRental_expiry_date(System.currentTimeMillis() / 1000 + 365 * 24 * 3600);
        ArrayList<RentalMonitor.Schedule> schedules = new ArrayList<>();
        ArrayList<RentalMonitor.Dots> dots = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            RentalMonitor.Schedule schedule = new RentalMonitor.Schedule();
            schedule.setStart_time(System.currentTimeMillis() / 1000 + (1 + i * 35) * 24 * 3600);
            schedule.setEnd_time(System.currentTimeMillis() / 1000 + (30+ i * 35) * 24 * 3600);
            schedules.add(schedule);
            RentalMonitor.Dots dot = new RentalMonitor.Dots();
            dot.setDate(System.currentTimeMillis() / 1000 + (3 + i * 20) * 24 * 3600);
            dot.setDots(1);
            dots.add(dot);
        }
        rm.setMax_use_days(10);
        rm.setMin_use_days(3);
        rm.setDefault_back_days(6);
        rm.setClean_days(1);
        rm.setSend_express_days(3);
        rm.setReturn_express_days(1);
        rm.setSchedule(schedules);
        rm.setDate_dots(dots);
        return rm;
    }
}
