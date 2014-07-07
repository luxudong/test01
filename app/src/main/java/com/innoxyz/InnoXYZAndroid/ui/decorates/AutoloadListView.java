package com.innoxyz.InnoXYZAndroid.ui.decorates;

import android.widget.AbsListView;
import android.widget.ListView;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.global.InnoXYZApp;
import com.innoxyz.InnoXYZAndroid.ui.customviews.FooterHintView;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-24
 * Time: 下午6:38
 * To change this template use File | Settings | File Templates.
 */
public class AutoloadListView {
    final protected ListView listView;
    protected boolean ended = false;
    protected FooterHintView fhv;
    protected int page = -1;

    public AutoloadListView(ListView listView) {
        this.listView = listView;
        listView.setOnScrollListener(scrollListener);
        fhv = new FooterHintView(getListView().getContext());
        listView.addFooterView(fhv);
    }

    public ListView getListView() {
        return listView;
    }

    protected OnDataLoading onDataLoading = null;

    public void setOnDataLoading(OnDataLoading onDataLoading) {
        this.onDataLoading = onDataLoading;
    }

    protected volatile boolean processing = false;

    public void beginLoad() {
        processing = true;
    }

    public void TriggerLoading() {
//        if ( processing || ended ) { Log.e("AAAAAAAA", "in TriggerLoading processing = " + processing);return; }
        page++;
        onDataLoading.LoadData(page);
    }

    public void reLoading(){
//        if ( processing ) { Log.e("AAAAAAAA", "in reLoading processing = " + processing); return; }
        ended = false;
        page = 0;
        onDataLoading.LoadData(page);
    }

    public void endLoad(boolean hasMore) {
        processing = false;
//        Log.e("AAAAAAAA", "in endLoad processing = " + processing);
        if ( !hasMore ) {
            ended = true;
            InnoXYZApp.getApplication().getMainThreadHandler().post(new Runnable() {
                @Override
                public void run() {
                    fhv.setText(getListView().getResources().getString(R.string.hint_list_nomore));
                }
            });
        }
    }
    private ListView.OnScrollListener scrollListener = new ListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
            if ( absListView.getLastVisiblePosition() >= absListView.getCount() - 1 ) {
                TriggerLoading();
            }
        }
        @Override
        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
        }
    };

    public static interface OnDataLoading {
        public void LoadData(int page);
    }
}
