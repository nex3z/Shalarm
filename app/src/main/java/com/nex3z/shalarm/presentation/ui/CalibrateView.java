package com.nex3z.shalarm.presentation.ui;

import android.content.Context;

public interface CalibrateView {
    void showMessage(String message);

    Context getContext();

    void renderForceMeter(float force);

    void finishView();
}
