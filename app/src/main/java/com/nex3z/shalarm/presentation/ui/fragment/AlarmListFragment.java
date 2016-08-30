package com.nex3z.shalarm.presentation.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nex3z.shalarm.R;
import com.nex3z.shalarm.data.entity.mapper.AlarmEntityDataMapper;
import com.nex3z.shalarm.data.executor.JobExecutor;
import com.nex3z.shalarm.data.repository.AlarmDataRepository;
import com.nex3z.shalarm.data.repository.datasource.alarm.AlarmDataStoreFactory;
import com.nex3z.shalarm.domain.interactor.UseCase;
import com.nex3z.shalarm.domain.interactor.alarm.query.GetAlarmList;
import com.nex3z.shalarm.domain.interactor.alarm.query.GetAlarmListArg;
import com.nex3z.shalarm.domain.interactor.alarm.update.UpdateAlarm;
import com.nex3z.shalarm.domain.mapper.AlarmDataMapper;
import com.nex3z.shalarm.domain.repository.AlarmRepository;
import com.nex3z.shalarm.presentation.UIThread;
import com.nex3z.shalarm.presentation.mapper.AlarmModelDataMapper;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.presenter.AlarmListPresenter;
import com.nex3z.shalarm.presentation.ui.AlarmListView;
import com.nex3z.shalarm.presentation.ui.adapter.AlarmAdapter;

import java.util.Collection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmListFragment extends Fragment implements AlarmListView {
    private static final String LOG_TAG = AlarmListFragment.class.getSimpleName();

    private static final String ALARM_FILTER = "alarm_filter";

    public static final String FILTER_ENABLED_ALARMS = "filter_enabled_alarms";
    public static final String FILTER_DISABLED_ALARMS = "filter_disabled_alarms";
    public static final String FILTER_ALL_ALARMS = "filter_all_alarms";

    @BindView(R.id.rv_alarm_list) RecyclerView mRvAlarmList;

    private String mFilter = FILTER_ALL_ALARMS;
    private AlarmAdapter mAlarmAdapter;
    private AlarmListPresenter mPresenter;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alarm_list, container, false);
        ButterKnife.bind(this, rootView);

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
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
    public void onDetach() {
        super.onDetach();
        mCallbacks = sDummyCallbacks;
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
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
    }

    @Override
    public void renderAlarmList(Collection<AlarmModel> alarmModelCollection) {
        mAlarmAdapter.setAlarmCollection(alarmModelCollection);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void init() {
        initPresenter();
        initRecyclerView();
        loadAlarms();
    }

    private void initPresenter() {
        AlarmRepository repository = new AlarmDataRepository(
                new AlarmDataStoreFactory(), new AlarmEntityDataMapper(), new AlarmDataMapper());

        GetAlarmListArg arg;
        switch (mFilter) {
            case FILTER_ENABLED_ALARMS:
                arg = new GetAlarmListArg(GetAlarmListArg.SORT_BY_START_DESC,
                        GetAlarmListArg.FILTER_ENABLED_ALARMS);
                break;
            case FILTER_DISABLED_ALARMS:
                arg = new GetAlarmListArg(GetAlarmListArg.SORT_BY_START_DESC,
                        GetAlarmListArg.FILTER_DISABLED_ALARMS);
                break;
            case FILTER_ALL_ALARMS:
            default:
                arg = new GetAlarmListArg(GetAlarmListArg.SORT_BY_START_DESC);
                break;
        }
        UseCase getAlarmList = new GetAlarmList(arg, repository, new JobExecutor(), new UIThread());

        UseCase updateAlarm = new UpdateAlarm(repository, new JobExecutor(), new UIThread());
        mPresenter = new AlarmListPresenter(getAlarmList, updateAlarm,new AlarmModelDataMapper());

        mPresenter.setView(this);
    }

    private void initRecyclerView() {
        mAlarmAdapter = new AlarmAdapter();
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
    }


}
