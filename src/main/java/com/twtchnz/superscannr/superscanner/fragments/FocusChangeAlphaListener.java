package com.twtchnz.superscannr.superscanner.fragments;

import android.view.View;

import java.util.ArrayList;

public class FocusChangeAlphaListener implements View.OnFocusChangeListener {

    ArrayList<View> views;

    public FocusChangeAlphaListener(View... args) {
        views = new ArrayList<View>();
        for (View v : args) {
            views.add(v);
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        for (View v : views) {
            float alpha = v.getAlpha();

            if (alpha == 1f) {
                v.setAlpha(0.5f);
            } else {
                v.setAlpha(1f);
            }
        }
    }
}
