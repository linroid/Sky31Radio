package com.linroid.radio.view;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.linroid.radio.R;

import timber.log.Timber;

/**
 * Created by linroid on 1/21/15.
 */
public class TitleSwitcher extends FrameLayout
        implements Animation.AnimationListener {

    TextView currentText;
    TextView switcherText;

    Animation forwardInAnim;
    Animation forwardOutAnim;
    Animation backInAnim;
    Animation backOutAnim;

    public TitleSwitcher(Context context) {
        super(context, null, R.style.RtlOverlay_Widget_AppCompat_ActionBar_TitleItem);
        this.setLayoutParams(new Toolbar.LayoutParams(-2, -2));
        this.setBackgroundColor(0);
        inflate(context, R.layout.merge_title_switcher, this);
        currentText = (TextView) getChildAt(0);
        switcherText = (TextView) getChildAt(1);

        forwardInAnim   = AnimationUtils.loadAnimation(context, R.anim.title_switcher_forward_in);
        forwardOutAnim  = AnimationUtils.loadAnimation(context, R.anim.title_switcher_forward_out);
        backInAnim      = AnimationUtils.loadAnimation(context, R.anim.title_switcher_back_in);
        backOutAnim     = AnimationUtils.loadAnimation(context, R.anim.title_switcher_back_out);
        forwardInAnim.setAnimationListener(this);
        backInAnim.setAnimationListener(this);
    }

    public void setToolbar(Toolbar toolbar) {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT);
        toolbar.addView(this, layoutParams);
        currentText.setText(toolbar.getTitle());
    }

    public void setCurrentTitle(CharSequence currentTitle) {
        currentText.setText(currentTitle);

    }

    public void switchForward(CharSequence title){
        Timber.e(title.toString());
        switcherText.setText(title);
        switcherText.startAnimation(forwardInAnim);
        currentText.startAnimation(forwardOutAnim);
    }
    public void switchBack(CharSequence title){
        switcherText.setText(title);
        switcherText.startAnimation(backInAnim);
        currentText.startAnimation(backOutAnim);
    }
    private void swap(){
        TextView tmpView    = currentText;
        currentText         = switcherText;
        switcherText        = tmpView;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        switcherText.setVisibility(VISIBLE);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        currentText.setVisibility(GONE);
        this.swap();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
