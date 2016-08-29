package com.nex3z.shalarm.presentation.ui.misc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.nex3z.shalarm.R;

public class ToggleButton {
    private static final String LOG_TAG = ToggleButton.class.getSimpleName();

    private View mView;
    private ImageView mIvBackground;
    private TextView mTvText;
    private boolean mIsChecked = false;
    private boolean mIsAnimationEnabled = true;
    private int mAnimationDuration = 150;
    private Animation mExpand;
    private Animation mShrink;

    public ToggleButton(Context context) {
        this(LayoutInflater.from(context).inflate(R.layout.item_toggle_button, null));
    }

    public ToggleButton(View view) {
        mView = view;
        mIvBackground = (ImageView)view.findViewById(R.id.iv_background);
        mTvText = (TextView)view.findViewById(R.id.tv_text);

        mExpand = new ScaleAnimation(0, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mExpand.setDuration(mAnimationDuration);

        mShrink = new ScaleAnimation(1, 0, 1, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mShrink.setDuration(mAnimationDuration);
        mShrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                mIvBackground.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    public View getView() {
        return mView;
    }

    public TextView getText() {
        return mTvText;
    }

    public ImageView getBackground() {
        return mIvBackground;
    }

    public void setChecked(boolean checked) {
        if (checked) {
            mIvBackground.setVisibility(View.VISIBLE);
        } else {
            mIvBackground.setVisibility(View.GONE);
        }

        mIsChecked = checked;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public int getAnimationDuration() {
        return mAnimationDuration;
    }

    public void setAnimationDuration(int animationDuration) {
        mAnimationDuration = animationDuration;
        mExpand.setDuration(animationDuration);
        mShrink.setDuration(animationDuration);
    }

    public boolean isAnimationEnabled() {
        return mIsAnimationEnabled;
    }

    public void setAnimationEnabled(boolean animationEnabled) {
        mIsAnimationEnabled = animationEnabled;
    }

    public boolean changeCheckedState() {
        mIsChecked = !mIsChecked;

        if (mIsAnimationEnabled) {
            if (mIsChecked) {
                mIvBackground.setVisibility(View.VISIBLE);
                mIvBackground.startAnimation(mExpand);
            } else {
                mIvBackground.setVisibility(View.VISIBLE);
                mIvBackground.startAnimation(mShrink);
            }
        }

        return mIsChecked;
    }

}
