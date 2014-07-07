package com.innoxyz.InnoXYZAndroid.ui.fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-11
 * Time: 上午10:13
 * To change this template use File | Settings | File Templates.
 */
public class FragmentSwitcher {
    private Activity activity;
    private BaseFragment fromFragment,toFragment;
    private int layout;
    Bundle bundle;

//    public FragmentSwitcher(Activity activity, BaseFragment toFragment, int layout, Bundle bundle) {
//        this.activity = activity;
//        this.fromFragment = null;
//        this.toFragment = toFragment;
//        this.layout = layout;
//        this.bundle = bundle;
//    }

    public FragmentSwitcher(Activity activity, BaseFragment fromFragment, BaseFragment toFragment, int layout, Bundle bundle) {
        this.activity = activity;
        this.fromFragment = fromFragment;
        this.toFragment = toFragment;
        this.layout = layout;
        this.bundle = bundle;

    }

    public void add() {
        toFragment.setArguments(bundle);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        ft.remove(fromFragment);
        ft.add(layout, toFragment);
        ft.commit();
    }

    public void replace() {
        //Log.e("","replace() called");
        toFragment.setArguments(bundle);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        ft.replace(layout, toFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void removeAttach() {
        //Log.e("","removeAttach() called");
        toFragment.setArguments(bundle);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        ft.remove(fromFragment);
        ft.attach(toFragment);
        ft.addToBackStack(null);
        ft.commit();
    }


    public void detachAdd() {
        //Log.e("","detachAdd() called");
        //Log.e("","detach: " + fromFragment.toString());
        //Log.e("","add: " + toFragment.toString());
        toFragment.setArguments(bundle);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        ft.detach(fromFragment);
        ft.add(layout, toFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void detachAttach() {
        //Log.e("","detachAttach() called");
        //Log.e("","detach: " + fromFragment.toString());
        //Log.e("","attach: " + toFragment.toString());
        toFragment.setArguments(bundle);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        ft.detach(fromFragment);
        ft.attach(toFragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}
