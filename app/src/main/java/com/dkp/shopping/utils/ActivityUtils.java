package com.dkp.shopping.utils;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;


import com.dkp.shopping.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dkp on 2018/8/23.
 */

public class ActivityUtils {
    private static Map<String, Fragment> sFragmentStacks = new HashMap<>();

    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }

    public static void addFragmentUseStack(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int frameId, String tag) {
        if (!fragment.isAdded()) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(frameId, fragment);
            transaction.addToBackStack(tag);
            transaction.commit();
        }
    }

    public static void addFragmentUseStack(@NonNull FragmentManager fragmentManager, @NonNull Fragment current, @NonNull Fragment target, int frameId, String tag) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.animator.slide_left_in, R.animator.slide_right_out);
        if (!target.isAdded()) {
            transaction.add(frameId, target);
            transaction.addToBackStack(tag);
        }
        transaction.hide(current).show(target).commit();
    }

    public static void replaceFragmentToActivity(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment, int frameId){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(frameId, fragment);
        transaction.commitAllowingStateLoss();
    }

    public static Map<String, Fragment> getFragmentStacks() {
        return sFragmentStacks;
    }
}
