package com.nex3z.shalarm.presentation.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nex3z.shalarm.R;
import com.nex3z.shalarm.presentation.alert.AlertManager;
import com.nex3z.shalarm.presentation.internal.di.HasComponent;
import com.nex3z.shalarm.presentation.internal.di.component.AlarmComponent;
import com.nex3z.shalarm.presentation.internal.di.component.DaggerAlarmComponent;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.ui.adapter.AlarmAdapter;
import com.nex3z.shalarm.presentation.ui.fragment.AlarmListFragment;
import com.nex3z.shalarm.presentation.utility.AlarmUtility;

import java.text.DateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmListActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, AlarmListFragment.Callbacks,
        HasComponent<AlarmComponent> {
    private static final String LOG_TAG = AlarmListActivity.class.getSimpleName();

    private static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance(DateFormat.SHORT);

    private AlarmComponent mAlarmComponent;
    private String mFilter = AlarmListFragment.FILTER_ALL_ALARMS;
    private BroadcastReceiver mReceiver = new NextAlarmBroadcastReceiver();

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        ButterKnife.bind(this);
        initInjector();

        setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            AlarmListFragment alarmListFragment = new AlarmListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fl_alarm_list_container, alarmListFragment)
                    .commit();
        }

        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFab.show();
        IntentFilter intentFilter = new IntentFilter(AlertManager.ACTION_NEXT_ALARM_UPDATE);
        registerReceiver(mReceiver, intentFilter);
        AlarmModel nextAlarm = AlertManager.getInstance().getNextAlarm();
        renderNextAlarmTime(nextAlarm);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_alarm_all) {
            mFilter = AlarmListFragment.FILTER_ALL_ALARMS;
            replaceAlarmListWithFilter(mFilter);
        } else if (id == R.id.nav_alarm_enabled) {
            mFilter = AlarmListFragment.FILTER_ENABLED_ALARMS;
            replaceAlarmListWithFilter(mFilter);
        } else if (id == R.id.nav_alarm_disabled) {
            mFilter = AlarmListFragment.FILTER_DISABLED_ALARMS;
            replaceAlarmListWithFilter(mFilter);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(AlarmModel alarmModel, AlarmAdapter.ViewHolder vh) {
        Log.v(LOG_TAG, "onItemSelected(): alarmModel = " + alarmModel);
        Intent intent = new Intent(this, EditAlarmActivity.class)
                .putExtra(EditAlarmActivity.ALARM_INFO, alarmModel);
        ActivityOptionsCompat activityOptions =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this);
        ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
    }

    @Override
    public AlarmComponent getComponent() {
        Log.v(LOG_TAG, "getComponent(): mAlarmComponent = " + mAlarmComponent);
        return mAlarmComponent;
    }

    private void initialize() {
        setupDrawer();
        setupFloatingActionButton();
    }

    private void initInjector() {
        mAlarmComponent = DaggerAlarmComponent.builder()
                .appComponent(getAppComponent())
                .activityModule(getActivityModule())
                .build();
    }

    private void setupDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mNavigationView.getMenu().getItem(0).setChecked(true);
    }

    private void setupFloatingActionButton() {
        mFab.setOnClickListener(view -> {
            Intent intent = new Intent(AlarmListActivity.this, AddAlarmActivity.class);
            ActivityOptionsCompat activityOptions =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
        });
    }

    private void replaceAlarmListWithFilter(String filter) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        AlarmListFragment fragment = AlarmListFragment.newInstanceByFilter(filter);
        ft.replace(R.id.fl_alarm_list_container, fragment).commit();

        mFab.show();
    }

    private void renderNextAlarmTime(AlarmModel alarmModel) {
        View header =  mNavigationView.getHeaderView(0);

        TextView nextAlarm = (TextView) header.findViewById(R.id.tv_next_alarm_time);
        LinearLayout container = (LinearLayout) header.findViewById(R.id.linear_nav_header);
        ProgressBar progressBar = (ProgressBar) header.findViewById(R.id.pb_loading_next_alarm);
        progressBar.setVisibility(View.GONE);
        nextAlarm.setVisibility(View.VISIBLE);

        if (alarmModel != null) {
            nextAlarm.setText(TIME_FORMAT.format(alarmModel.getStart()));
            container.setBackgroundColor(AlarmUtility.getBackgroundColor(
                    alarmModel.getShakePower()));
        } else {
            nextAlarm.setText(getString(R.string.nav_header_no_enabled_alarm));
            container.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.color_nav_header_alarm_disabled));
        }
    }

    private class NextAlarmBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(AlertManager.ACTION_NEXT_ALARM_UPDATE)) {
                AlarmModel alarmModel = intent.getParcelableExtra(AlertManager.EXTRA_NEXT_ALARM);
                Log.v(LOG_TAG, "onReceive(): alarmModel = " + alarmModel);
                renderNextAlarmTime(alarmModel);
            }
        }
    }
}
