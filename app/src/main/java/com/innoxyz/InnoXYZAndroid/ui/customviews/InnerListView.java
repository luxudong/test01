package com.innoxyz.InnoXYZAndroid.ui.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 再listview中嵌套listview，子listview不能正确获取总高度，需重写onMeasure方法
 * Created by dell on 2014/5/3.
 */
public class InnerListView extends ListView {
    public  InnerListView  (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public  InnerListView  (Context context) {
        super(context);
    }

    public  InnerListView  (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
