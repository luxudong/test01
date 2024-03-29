package com.innoxyz.InnoXYZAndroid.ui.fragments.common;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-22
 * Time: 下午2:09
 * To change this template use File | Settings | File Templates.
 */
public abstract class CommonListViewAdapter extends BaseAdapter implements IDataObserver {

    protected final LayoutInflater mInflator;
    protected View[] views;
    protected Handler handler = new Handler();

    public CommonListViewAdapter(Context context) {
        mInflator = LayoutInflater.from(context);
        views = new View[0];
    }

    @Override
    public int getCount() {
        return views.length;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getItem(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getItemId(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return views[i];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void update(Object o) {
        updateData();
        updateViews();
    }

    abstract protected void updateData();

    protected final void updateViews() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
}
