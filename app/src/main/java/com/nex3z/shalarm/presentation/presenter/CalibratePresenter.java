package com.nex3z.shalarm.presentation.presenter;

import android.util.Log;

import com.nex3z.shalarm.R;
import com.nex3z.shalarm.presentation.internal.di.PerActivity;
import com.nex3z.shalarm.presentation.ui.CalibrateView;
import com.nex3z.shalarm.presentation.utility.SensorUtility;

import javax.inject.Inject;

@PerActivity
public class CalibratePresenter implements Presenter {
    private static final String LOG_TAG = CalibratePresenter.class.getSimpleName();

    private static final float ACCEPTABLE_MINIMUM_FORCE = 0.5f;

    private CalibrateView mView;
    private float mMaxForce = 0;

    @Inject
    public CalibratePresenter() {}

    public void setView(CalibrateView view) {
        mView = view;
    }

    public void initialize() {
        mMaxForce = 0;
    }

    public void onForceChanged(float force) {
        if (force > mMaxForce) {
            Log.v(LOG_TAG, "onForceChanged(): force = " + force);
            mMaxForce = force;
            mView.renderForceMeter(mMaxForce);
        }
    }

    public void onConfirm() {
        if (mMaxForce < ACCEPTABLE_MINIMUM_FORCE) {
            mView.showMessage(mView.getContext().getString(R.string.invalid_calibrate_message));
        } else {
            SensorUtility.setMaxForce(mView.getContext(), mMaxForce);
            mView.finishView();
        }
    }

    @Override
    public void resume() {}

    @Override
    public void pause() {}

    @Override
    public void destroy() {
        mView = null;
    }
}
