package com.bugcatcher.calendar.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bugcatcher.calendar.R;
import com.bugcatcher.calendar.view.util.CalendarDay;
import com.bugcatcher.calendar.view.util.DateRange;

import java.util.ArrayDeque;
import java.util.List;

/**
 * Created by 95 on 2017/3/9.
 * 用于生成ViewHolder 和存储修改输出数据源
 * <p>
 * 共有时间层：最大和最小日期
 * <p>
 * SELECTION
 * 时间层二：当月起始和结束日期(超出部分不显示)
 * <p>
 * SELECTED
 * 时间层一：选取的时间范围
 */

public class CalendarPageThree extends CalendarPageAdapter.AbsCalendarPage {
    private Context mContext;
    private DateRange mDateRange;//日历范围
    private CalendarDay mToday;
    private ArrayDeque<ItemViewHolder> mHolders;
    private List<Integer> mSignDates;//签到日期数组

    public CalendarPageThree(Context context, DateRange dateRange) {
        this.mContext = context;
        this.mDateRange = dateRange;
        this.mToday = CalendarDay.today();
        this.mHolders = new ArrayDeque<>();
    }

    public void bindData(List<Integer> signPos) {
        this.mSignDates = signPos;
    }

    /**
     * 判断是否签到（包括当天）
     */
    public boolean isSignedDay(CalendarDay day) {
        if (day.equals(mToday)) {
            return true;
        }
        if (day.isBefore(mToday)) {
            for (Integer signed : mSignDates) {
                if (day.toInteger()==(signed)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * @return {@link #mDateRange}时间范围内包含完整月份的数量
     */
    @Override
    public int getCount() {
        return mDateRange.getCount();
    }

    @Override
    public DateRange getDateRange() {
        return mDateRange;
    }

    @Override
    public String getPageTitle(int position) {
        CalendarDay day = mDateRange.getFirstDayOfMonthByPosition(position);
        return (day.getYear() + "年" + (day.getMonth() + 1) + "月");
    }

    @Override
    public View createView(ViewGroup container, int position) {
        ItemViewHolder holder = new ItemViewHolder(position, View.inflate(mContext, R.layout.item_calendar_pager, null));
        mHolders.add(holder);
        return holder.itemView;
    }

    @Override
    public void destoryView(int position) {
        mHolders.remove(position);
    }


    class ItemViewHolder {
        public RecyclerView rcv;
        public int position;
        public View itemView;
        private CalendarPagerItemAdapter adapter;

        public ItemViewHolder(int position, View itemView) {
            this.itemView = itemView;
            this.position = position;
            rcv = (RecyclerView) itemView.findViewById(R.id.rcv);
            rcv.setLayoutManager(new GridLayoutManager(mContext, 7));
            adapter = new CalendarPagerItemAdapter(mDateRange.getItem(position), mDateRange.getMonthRange(position));
            rcv.setAdapter(adapter);
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * {@link ItemViewHolder} 内部RecyclerView的adapter
     */

    class CalendarPagerItemAdapter extends RecyclerView.Adapter {
        static final int TYPE_COMMON = 0;
        static final int TYPE_SELECTED = 1;
        private List<CalendarDay> mDays;
        private int[] mMonthRange;

        public CalendarPagerItemAdapter(List<CalendarDay> days, int[] monthRange) {
            mDays = days;
            mMonthRange = monthRange;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh = null;
            switch (viewType) {
                case TYPE_COMMON:
                    vh = new CalendarHolder(LayoutInflater.from(mContext).inflate(
                            R.layout.item_calendar, parent, false));
                    break;
                case TYPE_SELECTED:
                    vh = new CalendarSelectedHolder(LayoutInflater.from(mContext).inflate(
                            R.layout.item_calendar_selected, parent, false));
                    break;
            }
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case TYPE_COMMON:
                    CalendarHolder h0 = (CalendarHolder) holder;
                    h0.insertData(position);
                    break;
                case TYPE_SELECTED:
                    CalendarSelectedHolder h1 = (CalendarSelectedHolder) holder;
                    h1.insertData(position);
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            CalendarDay day = mDays.get(position);
            final int integerDay = day.toInteger();
            return isSignedDay(day) && integerDay >= mMonthRange[0] && integerDay <= mMonthRange[1] ? TYPE_SELECTED : TYPE_COMMON;
        }

        @Override
        public int getItemCount() {
            return mDays == null ? 0 : mDays.size();
        }

        class CalendarHolder extends RecyclerView.ViewHolder {
            private TextView tvDate;
            private View mask;

            public CalendarHolder(View itemView) {
                super(itemView);
                tvDate = (TextView) itemView.findViewById(R.id.tv_day);
                mask = itemView.findViewById(R.id.mask);
            }

            public void insertData(int position) {
                CalendarDay day = mDays.get(position);
                final int integerDay = day.toInteger();
                //超出本月不显示
                if (integerDay < mMonthRange[0] || integerDay > mMonthRange[1]) {
                    tvDate.setVisibility(View.GONE);
                    mask.setVisibility(View.GONE);
                    itemView.setOnClickListener(null);
                } else {
                    tvDate.setVisibility(View.VISIBLE);
                    tvDate.setText(String.valueOf(day.getDay()));
                    if (integerDay < mToday.toInteger()) {
                        //小于当天变灰
                        mask.setVisibility(View.VISIBLE);
                    } else {
                        //当天之后正常显示
                        mask.setVisibility(View.GONE);
                    }
                }
            }
        }


        class CalendarSelectedHolder extends RecyclerView.ViewHolder {
            TextView tvDate;
            ImageView ivStroke;

            public CalendarSelectedHolder(View itemView) {
                super(itemView);
                tvDate = (TextView) itemView.findViewById(R.id.tv_day);
                ivStroke = (ImageView) itemView.findViewById(R.id.iv_stroke);
            }

            public void insertData(int position) {
                CalendarDay day = mDays.get(position);
                tvDate.setText(String.valueOf(day.getDay()));
                if (day.equals(mToday)) {
                    tvDate.setTextColor(ContextCompat.getColor(mContext, R.color.colorPurple));
                    ivStroke.setImageResource(R.drawable.choose_purple);
                } else {
                    tvDate.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
                    ivStroke.setImageResource(R.drawable.choose_black);
                }
            }
        }

    }

}
