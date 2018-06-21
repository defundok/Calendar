package com.bugcatcher.calendar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by FEN ZHAO on 2017/3/13 27.
 * Description:
 */

public class SampleTwoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_two);
        getSupportFragmentManager().beginTransaction().add(R.id.container, new SampleTwoFragment()).commit();
    }
}
