package com.twtchnz.superscannr.superscanner.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;
import com.twtchnz.superscannr.superscanner.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SectionPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    private int mainFragmentPosition;

    private int count;

    public SectionPagerAdapter(FragmentManager fragmentManager, ArrayList<Fragment> sideScrollFragments, int mainFragmentPosition) {
        super(fragmentManager);

        this.mainFragmentPosition = mainFragmentPosition;
        this.count = sideScrollFragments.size();

        Iterator<Fragment> it = sideScrollFragments.iterator();

        while(it.hasNext())
            fragments.add(it.next());
    }

    public int getFragmentsSize() { return fragments.size(); }

    public int getFragmentPosition(Fragment fragment) {
        return fragments.indexOf(fragment);
    }

    public int getMainFragmentPosition() { return mainFragmentPosition; }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return count;
    }

    public void setCount(int count) { this.count = count; notifyDataSetChanged(); }

}
