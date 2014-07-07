package com.innoxyz.InnoXYZAndroid.ui.commands;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;

/**
 * Created by laborish on 14-1-4.
 */
public class ActivityCommand extends Command {

    public static final String defaultFragment = "com.innoxyz.InnoXYZAndroid.detailFragmentClazz";

    Class<? extends Activity> toActivityClass;
    Class<? extends BaseFragment> toFragmentClass;

    public ActivityCommand(Class<? extends Activity> toActivity, Class<? extends BaseFragment> toFragment, Activity activity, Bundle bundle, Handler handler){
        super(activity, bundle, handler);

        toActivityClass = toActivity;
        toFragmentClass = toFragment;
    }

    @Override
    protected void executeDo() {
        Intent i = new Intent(activity, toActivityClass);

        if(bundle != null){
            i.putExtras(bundle);
        }
        i.putExtra(ActivityCommand.defaultFragment, toFragmentClass);
        //Log.e("aaaaaaaa", "ActivityCommand" + toFragmentClass.toString());

        activity.startActivity(i);
    }

}
