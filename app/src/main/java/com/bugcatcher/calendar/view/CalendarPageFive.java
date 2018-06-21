package com.bugcatcher.calendar.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bugcatcher.calendar.R;
import com.bugcatcher.calendar.model.REntity;
import com.bugcatcher.calendar.model.RentalMonitor;
import com.bugcatcher.calendar.view.util.CalendarDay;
import com.bugcatcher.calendar.view.util.DateRange;

import java.util.ArrayDeque;
import java.util.List;

/**
 * Created by 95 on 2017/3/9.
 * 用于生成ViewHolder 和存储修改输出数据源
 */

public class CalendarPageFive extends CalendarPageAdapter.AbsCalendarPage {
    public static final int MODE_SELECTION = 0;//未选
    public static final int MODE_SELECTED = 1;//选中
    private ArrayDeque<ItemViewHolder> mHolders;
    private Context mContext;
    private REntity mData;
    private DateRange mDateRange;//日历范围
    private REntity.RSchedule mSchedule;
    private int mUserDots = 10;
    private int mMode;


    public CalendarPageFive(Context context, CalendarDay minDay, CalendarDay maxDay) {
        this.mContext = context;
        this.mDateRange = new DateRange(minDay, maxDay);
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
    public void bindData(REntity data) {
        mData = data;
        Log.i("bindData", data.toString());
        for (ItemViewHolder h : mHolders) {
            h.adapter.notifyDataSetChanged();
        }
    }

    /**
     * 重置
     */
    public void cleanSelected() {
        mMode = MODE_SELECTION;//更改切换模式标记符  已选择 => 待选择
        mSchedule.currentReturnDate = 0;
        mSchedule = null;
        //更新正在显示中的视图
        for (ItemViewHolder h : mHolders) {
            h.adapter.notifyDataSetChanged();
        }
    }

    public int[] getSelectedRange() {
        return mSchedule == null ? new int[]{0, 0} : new int[]{mSchedule.getDeliveryDateFMT(), mSchedule.currentReturnDate};
    }

    /**
     * 用户选择了起租日期
     */
    public void doSelected(CalendarDay fromDay) {
        final int integerDay = fromDay.toInteger();
        REntity.RSchedule schedule = mData.isDeliveryDay(integerDay);
        int resultDay = schedule.isAvailableReturnDate(mData, schedule.getDefaultReturnFMT(), mUserDots);
        Log.i("doselected", resultDay + "");
        if (resultDay != -1) {
            schedule.currentReturnDate = resultDay;
            mSchedule = schedule;
            mMode = MODE_SELECTED;
            for (ItemViewHolder h : mHolders) {
                h.adapter.notifyDataSetChanged();
            }
        } else {
            //TODO 报错信息
            Toast.makeText(mContext, "您的积点不足", Toast.LENGTH_SHORT).show();
        }
    }

    public void changeReturnDay(CalendarDay returnDay) {
        final int integerDay = returnDay.toInteger();
        if (integerDay == mSchedule.currentReturnDate) {
            return;
        }
        int resultDay = mSchedule.isAvailableReturnDate(mData, integerDay, mUserDots);
        if (resultDay == integerDay) {
            mSchedule.currentReturnDate = resultDay;
            for (ItemViewHolder h : mHolders) {
                h.adapter.notifyDataSetChanged();
            }
        } else {
            //TODO 报错信息
            Toast.makeText(mContext, "您的积点不足", Toast.LENGTH_SHORT).show();
        }
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
        private List<CalendarDay> mDays;
        private int[] mMonthRange;
        //0.判断是否是当月
        //1.优先判断是否是休息日 需要休息日范围[] 遍历
        //2.判断起租日 需要起租日 int 遍历
        //3.判断计划日 需要计划日 需要计划日范围[] 遍历
        //4.判断是否位于积点周期内 需要积点日 int 遍历

        static final int TYPE_PAUSE = 10;//休息图标 B
        static final int TYPE_FROM = 11;//高亮 A
        static final int TYPE_PLAN = 12;//购物图标 B
        static final int TYPE_OUT = 13;//抢完  (A)
        static final int TYPE_DISABLE = 0;//不在周期范围内 浅灰 不在当月 不显示 C

        static final int TYPE_CAR = 14;//车辆图标 B
        static final int TYPE_MIN = 15;//红色实心圆 A
        static final int TYPE_RETURN = 16;//红色圆边框或不显示圆边框 A
        static final int TYPE_SELECTED = 17;//A


        //选择起租日
        //0.判断当前积点 需要起租日积点
        //1.判断默认归还日前的积点,从小到大遍历当前所选归还日，发现不够立刻终止，返回当前日期
        //需要所选对象的 所选归还日和起租日 从dots内遍历


        //0.判断是否是当月 否 DISABLE
        //1.判断运输日 TYPE_CAR 需要所选对象的 sendDates int遍历
        //2.判断起租日到最小租用日期 TYPE_MIN 需要所选对象的 min_use_dates int 遍历
        //3.判断默认归还日，可选归还日 小于默认归还日圈，大于默认归还日高亮 TYPE_RETURN 需要所选对象的 return_dates_遍历
        //4.判断休息日 TYPE_PAUSE 需要休息日范围[] 遍历
        //5.判断其它起租日 DISABLE  需要起租日 int 遍历
        //6.判断计划日 TYPE_OTHER 需要计划日范围[] 遍历
        //7.判断是否位于积点周期内 在周期内OUT 不在DISABLE 需要积点日 int 遍历

        public CalendarPagerItemAdapter(List<CalendarDay> days, int[] monthRange) {
            mDays = days;
            mMonthRange = monthRange;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh = null;
            switch (viewType) {
                case TYPE_DISABLE:
                    vh = new PlaceHolder(LayoutInflater.from(mContext).inflate(R.layout.item_calendar, parent, false));
                    break;
                case TYPE_PAUSE:
                case TYPE_CAR:
                case TYPE_PLAN:
                    vh = new IconHolder(LayoutInflater.from(mContext).inflate(R.layout.item_calendar_icon, parent, false));
                    break;
                case TYPE_FROM:
                case TYPE_RETURN:
                case TYPE_SELECTED:
                case TYPE_MIN:
                case TYPE_OUT:
                    vh = new MainHolder(LayoutInflater.from(mContext).inflate(R.layout.item_calendar_selected, parent, false));
                    break;
            }
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((BaseHolder) holder).insertData(position);
        }

        @Override
        public int getItemViewType(int position) {
            CalendarDay day = mDays.get(position);
            final int integerDay = day.toInteger();
            if (mData == null || integerDay < mMonthRange[0] || integerDay > mMonthRange[1])
                return TYPE_DISABLE;
            switch (mMode) {
                case MODE_SELECTION:
                    if (mData.isPauseDay(integerDay) != null) return TYPE_PAUSE;
                    if (mData.isDeliveryDay(integerDay) != null) return TYPE_FROM;
                    if (mData.isPlanDay(integerDay) != null) return TYPE_PLAN;
                    if (mData.isDotsDay(integerDay) != null) return TYPE_OUT;
                    return TYPE_DISABLE;
                case MODE_SELECTED:
                    if (mSchedule.isSendDate(integerDay)) return TYPE_CAR;
                    if (mSchedule.getDeliveryDateFMT() == integerDay) return TYPE_SELECTED;
                    if (mSchedule.isMinUseDate(integerDay)) return TYPE_MIN;
                    if (mSchedule.isCanReturnDate(integerDay)) return TYPE_RETURN;
                    if (mData.isPauseDay(integerDay) != null) return TYPE_PAUSE;
                    if (mData.isDeliveryDay(integerDay) != null) return TYPE_DISABLE;
                    if (mData.isPlanDay(integerDay) != null) return TYPE_PLAN;
                    if (mData.isDotsDay(integerDay) != null) return TYPE_OUT;
                    return TYPE_DISABLE;
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return mDays == null ? 0 : mDays.size();
        }


        abstract class BaseHolder extends RecyclerView.ViewHolder {

            public BaseHolder(View itemView) {
                super(itemView);
            }

            abstract void insertData(int position);
        }


        class IconHolder extends BaseHolder {
            private ImageView iv;

            public IconHolder(View itemView) {
                super(itemView);
                iv = (ImageView) itemView.findViewById(R.id.iv_icon);
            }

            @Override
            void insertData(final int position) {
                final int integerDay = mDays.get(position).toInteger();
                switch (getItemViewType()) {
                    case TYPE_PAUSE: //休息
                        iv.setImageResource(R.drawable.rest);

                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (mMode == MODE_SELECTION) {
                                    if (mData.isDeliveryDay(integerDay) != null) {
                                        doSelected(mDays.get(position));
                                        return;
                                    }
                                }
                                REntity.RPauseDate pause = mData.isPauseDay(integerDay);
                                Toast.makeText(mContext, pause.click_message, Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case TYPE_CAR: //配送
                        iv.setImageResource(R.drawable.logistics_iv);
                        break;
                    case TYPE_PLAN: //已有计划
                        iv.setImageResource(R.drawable.bag_iv);
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                REntity.RPlan plan = mData.isPlanDay(integerDay);
                                Toast.makeText(mContext, plan.click_message, Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
            }

        }

        class PlaceHolder extends BaseHolder {
            private TextView tv;

            public PlaceHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.tv_day);
                tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrey));
            }

            @Override
            void insertData(int position) {
                final int integerDay = mDays.get(position).toInteger();
                if (integerDay < mMonthRange[0] || integerDay > mMonthRange[1]) {
                    tv.setText("");
                } else {
                    tv.setText(String.valueOf(mDays.get(position).getDay()));
                }
            }
        }


        class MainHolder extends BaseHolder {
            TextView tvDate, tvDot;
            ImageView ivIcon;

            public MainHolder(View itemView) {
                super(itemView);
                tvDate = (TextView) itemView.findViewById(R.id.tv_day);
                tvDot = (TextView) itemView.findViewById(R.id.tv_desc);
                ivIcon = (ImageView) itemView.findViewById(R.id.iv_stroke);
            }

            @Override
            void insertData(int position) {
                final CalendarDay day = mDays.get(position);
                final int integerDay = day.toInteger();

                switch (getItemViewType()) {
                    case TYPE_FROM:
                        tvDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.text_size_calendar_date));
                        tvDate.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
                        tvDot.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
                        ivIcon.setVisibility(View.GONE);
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                doSelected(day);
                            }
                        });
                        break;
                    case TYPE_RETURN:
                        tvDot.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeReturnDay(day);
                            }
                        });
                        break;
                    case TYPE_SELECTED:
                        tvDate.setText("起租");
                        tvDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.text_size_calendar_date_small));
                        tvDate.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                        tvDot.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
                        ivIcon.setImageResource(R.drawable.shape_oval_red);
                        break;
                    case TYPE_MIN:
                        tvDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.text_size_calendar_date));
                        tvDate.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                        tvDot.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
                        ivIcon.setImageResource(R.drawable.shape_oval_red);
                        break;
                    case TYPE_OUT:
                        tvDate.setText("");
                        tvDot.setTextColor(ContextCompat.getColor(mContext, R.color.colorGrey));
                        tvDot.setText("抢完");
                        ivIcon.setImageResource(R.drawable.finished);
                        break;
                }

                switch (getItemViewType()) {
                    case TYPE_FROM:
                        tvDot.setText(mData.isDotsDay(integerDay).dots_text);
                        tvDate.setText(String.valueOf(day.getDay()));
                        break;
                    case TYPE_MIN:
                        tvDate.setText(String.valueOf(day.getDay()));
                    case TYPE_SELECTED:
                        tvDot.setText(mData.isDotsDay(integerDay).dots_text);
                        break;
                    case TYPE_RETURN:
                        tvDot.setText(mData.isDotsDay(integerDay).dots_text);
                        if (integerDay == mSchedule.currentReturnDate) {
                            tvDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.text_size_calendar_date_small));
                            tvDate.setText("归还");
                            tvDate.setTextColor(ContextCompat.getColor(mContext, R.color.colorWhite));
                            ivIcon.setImageResource(R.drawable.shape_oval_red);
                        } else if (integerDay < mSchedule.currentReturnDate) {
                            tvDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.text_size_calendar_date));
                            tvDate.setText(String.valueOf(day.getDay()));
                            tvDate.setTextColor(ContextCompat.getColor(mContext, R.color.colorRed));
                            ivIcon.setImageResource(R.drawable.shape_stroke_red);
                        } else {
                            tvDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.text_size_calendar_date));
                            tvDate.setText(String.valueOf(day.getDay()));
                            tvDate.setTextColor(ContextCompat.getColor(mContext, R.color.colorBlack));
                            ivIcon.setImageResource(0);
                        }
                        break;
                }
            }
        }

    }

}
