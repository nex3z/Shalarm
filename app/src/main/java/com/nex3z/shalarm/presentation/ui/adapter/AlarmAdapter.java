package com.nex3z.shalarm.presentation.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.nex3z.shalarm.R;
import com.nex3z.shalarm.app.App;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.utility.AlarmUtility;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {
    private static final String LOG_TAG = AlarmAdapter.class.getSimpleName();

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("kk:mm");
    private final Set<Integer> workdays = new HashSet<>();

    private static OnItemClickListener mListener;
    private List<AlarmModel> mAlarms;

    public interface OnItemClickListener {
        void onItemClick(int position, AlarmAdapter.ViewHolder vh);
        void onCheckedChanged(int position, boolean isEnabled);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_alarm_card, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AlarmModel alarm = mAlarms.get(position);
        String startTime = TIME_FORMAT.format(alarm.getStart());
        holder.mTvAlarmTime.setText(startTime);
        holder.mTvAlarmLabel.setText(alarm.getAlarmLabel());
        holder.mTvRepeatDays.setText(getDaysDescription(alarm.getRepeatDays()));
        holder.mSwEnable.setChecked(alarm.isEnabled());
        holder.mCardView.setCardBackgroundColor(AlarmUtility.getBackgroundColor(
                alarm.getShakePower()));
    }

    @Override
    public int getItemCount() {
        return mAlarms == null ? 0 : mAlarms.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void setAlarmCollection(Collection<AlarmModel> alarmCollection) {
        validateAlarmCollection(alarmCollection);
        mAlarms = (List<AlarmModel>) alarmCollection;
        notifyDataSetChanged();
    }

    public AlarmModel getItemAt(int position) {
        return mAlarms != null ? mAlarms.get(position) : null;
    }

    private void validateAlarmCollection(Collection<AlarmModel> alarmCollection) {
        if (alarmCollection == null) {
            throw new IllegalArgumentException("The list cannot be null");
        }
    }

    private String getDaysDescription(Set<Integer> days) {
        Context context = App.getAppContext();
        TreeSet<Integer> sortedDays = new TreeSet<>(days);

        StringBuilder sb = new StringBuilder();

        if (sortedDays.size() == 0) {
            sb.append(context.getString(R.string.once));
        } else if (sortedDays.size() == 7) {
            sb.append(context.getString(R.string.everyday));
        } else {
            String[] weekdays = context.getResources().getStringArray(R.array.short_weekdays);
            for(int day : sortedDays) {
                sb.append(weekdays[day]);
                sb.append(" ");
            }
        }

        return sb.toString();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_alarm_time) public TextView mTvAlarmTime;
        @BindView(R.id.tv_alarm_label) public TextView mTvAlarmLabel;
        @BindView(R.id.tv_repeat_days) public TextView mTvRepeatDays;
        @BindView(R.id.sw_enable) public Switch mSwEnable;
        @BindView(R.id.card_alarm_item) CardView mCardView;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(getLayoutPosition(), ViewHolder.this);
                    }
                }
            });

            mSwEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (mListener != null) {
                        Log.v(LOG_TAG, "onCheckedChanged(): position = " + getLayoutPosition() + ", checked = " + b);
                        mListener.onCheckedChanged(getLayoutPosition(), b);
                    }
                }
            });
        }
    }
}
