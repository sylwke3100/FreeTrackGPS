package com.sylwke3100.freetrackgps;


import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;



public class TabListener<T extends Fragment> implements ActionBar.TabListener {
    private Fragment mFragment;
    private final Activity mActivity;
    private final String mTag;
    private final Class<T> mClass;
    private Bundle globalValues;

    public TabListener(Activity activity, String tag, Class<T> clz, Bundle bundleValues) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;
        globalValues = bundleValues;
    }

    public TabListener(Activity activity, String tag, Class<T> clz) {
        mActivity = activity;
        mTag = tag;
        mClass = clz;
    }

    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (mFragment == null) {
            mFragment = Fragment.instantiate(mActivity, mClass.getName());
            if (!mFragment.isAdded()) {
                if (globalValues != null)
                    mFragment.setArguments(globalValues);
                ft.add(android.R.id.content, mFragment, mTag);
            }
        } else {
            if (!mFragment.isDetached())
                if (globalValues != null)
                    mFragment.setArguments(globalValues);
            ft.attach(mFragment);
        }
    }

    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (mFragment != null) {
            ft.detach(mFragment);
        }
    }

    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        if (mFragment != null) {
            ft.attach(mFragment);
        }
    }
}
