package com.nex3z.shalarm.presentation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nex3z.shalarm.R;
import com.nex3z.shalarm.presentation.model.AlarmModel;
import com.nex3z.shalarm.presentation.ui.adapter.AlarmAdapter;
import com.nex3z.shalarm.presentation.ui.fragment.AlarmListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlarmListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AlarmListFragment.Callbacks {
    private static final String LOG_TAG = AlarmListActivity.class.getSimpleName();

    private String mFilter = AlarmListFragment.FILTER_ALL_ALARMS;

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_list);

        ButterKnife.bind(this);

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
    protected void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "onPause()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "onDestroy()");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_alarm_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
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
        startActivity(intent);
    }

    private void initialize() {
        setupDrawer();
        setupFloatingActionButton();
    }

    private void setupDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);
    }

    private void setupFloatingActionButton() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmListActivity.this, AddAlarmActivity.class);
                startActivity(intent);
            }
        });
    }

    private void replaceAlarmListWithFilter(String filter) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        AlarmListFragment fragment = AlarmListFragment.newInstanceByFilter(filter);
        ft.replace(R.id.fl_alarm_list_container, fragment).commit();

        mFab.show();
    }
}
