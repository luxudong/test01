package com.innoxyz.InnoXYZAndroid.ui.commands;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.innoxyz.InnoXYZAndroid.global.InnoXYZApp;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-11
 * Time: 上午10:27
 * To change this template use File | Settings | File Templates.
 */
public abstract class Command {
    protected Activity activity;
    protected Bundle bundle;
    protected Handler handler;

    protected Command(Activity activity, Bundle bundle, Handler handler) {
        this.activity = activity;
        this.bundle = bundle;
        this.handler = handler;
    }

    public void Execute() {
        if ( handler==null ) {
            handler = InnoXYZApp.getApplication().getMainThreadHandler();
        }
        handler.post(new Runnable() {
                @Override
                public void run() {
                    executeDo();
                }
            });
    }

    protected abstract void executeDo();
}
