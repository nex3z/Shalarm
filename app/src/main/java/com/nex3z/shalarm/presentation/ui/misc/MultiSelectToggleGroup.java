package com.nex3z.shalarm.presentation.ui.misc;

import android.content.Context;
import android.util.AttributeSet;

import java.util.Set;

public class MultiSelectToggleGroup extends ToggleButtonGroup {
    private static final String LOG_TAG = MultiSelectToggleGroup.class.getSimpleName();

    public MultiSelectToggleGroup(Context context) {
        this(context, null);
    }

    public MultiSelectToggleGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onToggleButtonClicked(int position) {
        ToggleButton button = mButtons.get(position);
        boolean isChecked = button.isChecked();
        isChecked = !isChecked;
        button.setChecked(isChecked, isAnimationEnabled());
        if (mListener != null) {
            mListener.onCheckedStateChange(position, isChecked);
        }
    }

    public void setCheckedPositions(Set<Integer> positions) {
        uncheckAll();
        for (int position : positions) {
            mButtons.get(position).setChecked(true);
        }
    }

}
