package com.twtchnz.superscannr.superscanner.adapters;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import com.twtchnz.superscannr.superscanner.utils.Utils;

public class CustomViewPager extends ViewPager {

    private boolean enabled;
    private SectionPagerAdapter sectionPagerAdapter;

    public CustomViewPager(Context context) {
        super(context);

        this.enabled = true;
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.enabled = true;
    }

    public void setAdapter(SectionPagerAdapter adapter) {
        super.setAdapter(adapter);

        this.sectionPagerAdapter = adapter;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled)
            return super.onTouchEvent(event);

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(this.enabled)
            return super.onInterceptTouchEvent(event);

        return false;
    }

    public void setPagerEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Fragment getFragmentAt(int position) { return sectionPagerAdapter.getItem(position); }
}
