package com.bugcatcher.calendar.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bugcatcher.calendar.R;
import com.bugcatcher.calendar.model.RentalMonitor;
import com.bugcatcher.calendar.view.util.CalendarDay;
import com.bugcatcher.calendar.view.util.DateRange;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 95 on 2017/3/9.
 * 用于生成ViewHolder 和存储修改输出数据源
 */

public class CalendarPageOne extends CalendarPageAdapter.AbsCalendarPage {
    public static final int MODE_SELECTION = 0;//未选
    public static final int MODE_SELECTED = 1;//选中
    private ArrayDeque<ItemViewHolder> mHolders;
    private Context mContext;
    private RentalMonitor mData;
    private DateRange mDateRange;//日历范围
    private int[] mSelectedRange = new int[4];//用户勾选的日期范围 (0表示开始日，1表示结束日,2表示最早结束日，3表示当前选中日)
    private List<int[]> mSelectionRangeList;//用户可点击的日期范围，不在该范围内的日期为灰色，存放多个范围

    private int mMode;


    public CalendarPageOne(Context context, CalendarDay minDay, CalendarDay maxDay) {
        this.mContext = context;
        this.mDateRange = new DateRange(minDay, maxDay);
        this.mSelectionRangeList = new ArrayList<>();
        this.mHolders = new ArrayDeque<>();
    }

    /**
     * @return {@link #mDateRange}时间范围内包含完整月份的数量
     */
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
        ItemViewHolder holder = new ItemViewHolder(position,
                View.inflate(mContext, R.layout.item_calendar_pager, null));
        mHolders.add(holder);
        return holder.itemView;
    }

    @Override
    public void destoryView(int position) {
        mHolders.remove(position);
    }

    /**
     * @param data 绑定租借信息
     */
    public void bindData(RentalMonitor data) {
        mData = data;
        mSelectionRangeList = mData.getSelectionRangeList();
        for (ItemViewHolder h : mHolders) {
            h.adapter.notifyDataSetChanged();
        }
    }

    /**
     * 重置
     */
    public void cleanSelected() {
        mMode = MODE_SELECTION;//更改切换模式标记符  已选择 => 待选择
        mSelectedRange[0] = mSelectedRange[1] = mSelectedRange[2] = mSelectedRange[3] = 0;//清空用户选择日期
        //更新正在显示中的视图
        for (ItemViewHolder h : mHolders) {
            h.adapter.notifyDataSetChanged();
        }
    }

    /**
     * 选择
     */
    public void doSelected(CalendarDay fromDay) {
        boolean result = mData.getSelectableRange(fromDay, mSelectedRange);
        if (result) {
            mMode = MODE_SELECTED;
            for (ItemViewHolder h : mHolders) {
                h.adapter.notifyDataSetChanged();
            }
        }
    }

    public int[] getSelectedRange() {
        return mSelectedRange;
    }

    private boolean isInSelectionRange(int day) {
        for (int[] range : mSelectionRangeList) {
            if (day >= range[0] && day <= range[1])
                return true;
        }
        return false;
    }


    private RentalMonitor.Dots getMatchedDots(int day) {
        for (RentalMonitor.Dots dots : mData.getDate_dots()) {
            if (dots.getIntegerDate() == day) return dots;
        }
        return null;
    }


    class ItemViewHolder {
        public View itemView;
        public int position;
        public RecyclerView rcv;
        private CalendarPagerItemAdapter adapter;

        public ItemViewHolder(int position, View itemView) {
            this.position = position;
            this.itemView = itemView;
            rcv = (RecyclerView) itemView.findViewById(R.id.rcv);
            rcv.setLayoutManager(new GridLayoutManager(mContext, 7));
            adapter = new CalendarPagerItemAdapter(mDateRange.getItem(position), mDateRange.getMonthRange(position));
            rcv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
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
            switch (mMode) {
                case MODE_SELECTED:
                    final int integerDay = day.toInteger();
                    return integerDay <= mSelectedRange[1] && integerDay >= mSelectedRange[0]
                            && integerDay >= mMonthRange[0] && integerDay <= mMonthRange[1] ?
                            TYPE_SELECTED : TYPE_COMMON;
                case MODE_SELECTION:
                    return TYPE_COMMON;
            }

            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return mDays == null ? 0 : mDays.size();
        }

        class CalendarHolder extends RecyclerView.ViewHolder {
            private TextView tvDate, tvDot;
            private View mask;

            public CalendarHolder(View itemView) {
                super(itemView);
                tvDate = (TextView) itemView.findViewById(R.id.tv_day);
                tvDot = (TextView) itemView.findViewById(R.id.tv_dot);
                mask = itemView.findViewById(R.id.mask);
            }

            public void insertData(int position) {
                CalendarDay day = mDays.get(position);
                final int integerDay = day.toInteger();
                RentalMonitor.Dots dots = getMatchedDots(integerDay);
                if (dots != null) {
                    tvDot.setText(String.valueOf(dots.getDots()));
                } else {
                    tvDot.setText("");
                }
                tvDate.setText(String.valueOf(day.getDay()));
                //超出月份不显示
                if (mMonthRange[0] > integerDay || mMonthRange[1] < integerDay) {
                    mask.setVisibility(View.GONE);
                    tvDot.setVisibility(View.GONE);
                    tvDate.setVisibility(View.GONE);
                    itemView.setOnClickListener(null);
                } else if (mMode == MODE_SELECTED) {
                    mask.setVisibility(View.VISIBLE);
                    tvDot.setVisibility(View.VISIBLE);
                    tvDate.setVisibility(View.VISIBLE);
                    itemView.setOnClickListener(null);// itemView.setClickable(false);
                } else {
                    //如果是待选模式，在点击范围外的变灰
                    tvDot.setVisibility(View.VISIBLE);
                    tvDate.setVisibility(View.VISIBLE);
                    if (isInSelectionRange(day.toInteger())) {
                        mask.setVisibility(View.GONE);
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CalendarDay day = mDays.get(getLayoutPosition());
                                doSelected(day);
                            }
                        });
                    } else {
                        mask.setVisibility(View.VISIBLE);
                        itemView.setOnClickListener(null);
                    }
                }
            }
        }


        class CalendarSelectedHolder extends RecyclerView.ViewHolder {
            TextView tvDate, tvDot;
            View mask;
            ImageView ivStroke;

            public CalendarSelectedHolder(View itemView) {
                super(itemView);
                tvDate = (TextView) itemView.findViewById(R.id.tv_day);
                tvDot = (TextView) itemView.findViewById(R.id.tv_desc);
                tvDot.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
                ivStroke = (ImageView) itemView.findViewById(R.id.iv_stroke);
                mask = itemView.findViewById(R.id.mask);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CalendarDay day = mDays.get(getLayoutPosition());
                        int integetDay = day.toInteger();
                        if (integetDay != mSelectedRange[3] && integetDay >= mSelectedRange[2]
                                && integetDay <= mSelectedRange[1]) {
                            mSelectedRange[3] = integetDay;
                            for (ItemViewHolder holder : mHolders) {
                                holder.adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
            }

            public void insertData(int position) {
                CalendarDay day = mDays.get(position);

                final int integerDay = day.toInteger();
                RentalMonitor.Dots dots = getMatchedDots(integerDay);
                if (dots != null) {
                    tvDot.setText(String.valueOf(dots.getDots()));
                } else {
                    tvDot.setText("");
                }
                //按照优先级顺序高到低判断
                if (integerDay == mSelectedRange[0]) {//起租日期
                    tvDate.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    tvDate.setText("起租");
                    tvDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.text_size_calendar_date_small)); ///////////////字体适当变小
                    ivStroke.setImageResource(R.drawable.shape_oval_red);
                } else if (integerDay == mSelectedRange[3]) {//选择的日期
                    tvDate.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    tvDate.setText("归还");
                    tvDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.text_size_calendar_date_small));///////////////字体适当变小
                    ivStroke.setImageResource(R.drawable.shape_oval_red);
                } else if (integerDay < mSelectedRange[2]) {//最小起租日前面的日期
                    tvDate.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                    tvDate.setText(String.valueOf(day.getDay()));
                    tvDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.text_size_calendar_date));
                    ivStroke.setImageResource(R.drawable.shape_oval_red);
                } else if (integerDay < mSelectedRange[3]) {//选择日期前面的日期
                    tvDate.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
                    tvDate.setText(String.valueOf(day.getDay()));
                    tvDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.text_size_calendar_date));
                    ivStroke.setImageResource(R.drawable.shape_stroke_red);
                } else {//剩下的可选日期，该viewtype的日期必然是在用户可选日期范围内
                    tvDate.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
                    tvDate.setText(String.valueOf(day.getDay()));
                    tvDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.text_size_calendar_date));
                    ivStroke.setImageResource(0);
                }
            }

        }

    }

}
