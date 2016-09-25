package com.nex3z.shalarm.presentation.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nex3z.shalarm.R;
import com.nex3z.shalarm.presentation.internal.di.component.AlarmComponent;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.presenter.AlarmListPresenter;
import com.nex3z.shalarm.presentation.ui.AlarmListView;
import com.nex3z.shalarm.presentation.ui.adapter.AlarmAdapter;

import java.util.Collection;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AlarmListFragment extends BaseFragment implements AlarmListView {
    private static final String LOG_TAG = AlarmListFragment.class.getSimpleName();

    private static final String ALARM_FILTER = "alarm_filter";

    public static final String FILTER_ENABLED_ALARMS = "filter_enabled_alarms";
    public static final String FILTER_DISABLED_ALARMS = "filter_disabled_alarms";
    public static final String FILTER_ALL_ALARMS = "filter_all_alarms";

    @BindView(R.id.rv_alarm_list) RecyclerView mRvAlarmList;
    @BindView(R.id.linear_no_alarm) LinearLayout mLinearNoAlarm;

    @Inject AlarmAdapter mAlarmAdapter;
    @Inject AlarmListPresenter mPresenter;

    private Unbinder mUnbinder;
    private String mFilter = FILTER_ALL_ALARMS;
    private Callbacks mCallbacks = sDummyCallbacks;

    private static Callbacks sDummyCallbacks = (alarm, vh) -> { };

    public interface Callbacks {
        void onItemSelected(AlarmModel alarmModel, AlarmAdapter.ViewHolder vh);
    }

    public AlarmListFragment() {}

    public static AlarmListFragment newInstanceByFilter(String filter) {
        AlarmListFragment fragment = new AlarmListFragment();

        Bundle args = new Bundle();
        args.putString(ALARM_FILTER, filter);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }
        mCallbacks = (Callbacks) context;
    }

    @Override
    protected boolean onInjectView() throws IllegalStateException {
        getComponent(AlarmComponent.class).inject(this);
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alarm_list, container, false);

        mUnbinder = ButterKnife.bind(this, rootView);

        Bundle arguments = getArguments();
        if (arguments != null) {
            if (arguments.containsKey(ALARM_FILTER)) {
                mFilter = arguments.getString(ALARM_FILTER);
                Log.v(LOG_TAG, "onCreateView(): mFilter = " + mFilter);
            }
        }

        return rootView;
    }

    @Override
    protected void onViewInjected(Bundle savedInstanceState) {
        super.onViewInjected(savedInstanceState);
        setupRecyclerView();
        mPresenter.setView(this);
        loadAlarms();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRvAlarmList.setAdapter(null);
        mUnbinder.unbind();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
    }

    @Override
    public void renderAlarmList(Collection<AlarmModel> alarmModelCollection) {
        if (alarmModelCollection.isEmpty()) {
            mLinearNoAlarm.setVisibility(View.VISIBLE);
        } else {
            mLinearNoAlarm.setVisibility(View.GONE);
        }

        mAlarmAdapter.setAlarmCollection(alarmModelCollection);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void setupRecyclerView() {
        mAlarmAdapter.setOnItemClickListener(new AlarmAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, AlarmAdapter.ViewHolder vh) {
                mCallbacks.onItemSelected(mAlarmAdapter.getItemAt(position), vh);
            }

            @Override
            public void onCheckedChanged(int position, boolean isEnabled) {
                mPresenter.setAlarmEnabled(position, isEnabled);
            }
        });

        mRvAlarmList.setAdapter(mAlarmAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvAlarmList.setLayoutManager(layoutManager);

        mRvAlarmList.setHasFixedSize(true);
    }

    private void loadAlarms() {
        mPresenter.initialize();
        switch (mFilter) {
            case FILTER_ENABLED_ALARMS:
                mPresenter.loadEnableAlarms();
                break;
            case FILTER_DISABLED_ALARMS:
                mPresenter.loadDisabledAlarms();
                break;
            case FILTER_ALL_ALARMS:
            default:
                mPresenter.loadAllAlarms();
                break;
        }
    }

}
