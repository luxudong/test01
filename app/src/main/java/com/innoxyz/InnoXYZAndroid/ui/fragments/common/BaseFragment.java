package com.innoxyz.InnoXYZAndroid.ui.fragments.common;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-26
 * Time: 下午8:58
 * To change this template use File | Settings | File Templates.
 */
public class BaseFragment extends Fragment {

    @Override
    public void onAttach (Activity activity){
        //Log.e("",this.getClass().toString() + " onAttach called");
        super.onAttach(activity);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //Log.e("",this.getClass().toString() + " onCreateView called");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume (){
        super.onResume();
        //Log.e("",this.getClass().toString());
//        TopMenubar topMenubar = ((MainActivity)getActivity()).getTopMenubar();
//        if(topMenubar!=null){
//            topMenubar.onFragmentChanged(this.getClass());
//        }
    }

    @Override
    public void onDestroyView (){
        //Log.e("",this.getClass().toString() + " onDestroyView called");
        super.onDestroyView();
    }

    @Override
    public void onDestroy (){
        //Log.e("",this.getClass().toString() + " onDestroy called");
        super.onDestroy();
    }

    @Override
    public void onDetach (){
        //Log.e("",this.getClass().toString() + " onDetach called");
        super.onDetach();
    }
}
