package com.innoxyz.InnoXYZAndroid.ui.customviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.innoxyz.InnoXYZAndroid.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-16
 * Time: 上午11:58
 * To change this template use File | Settings | File Templates.
 */
public class SimpleTable extends TableLayout {
    LayoutInflater mInflator;
    public SimpleTable(Context context, List<String> keys, HashMap<String, String> map) {
        super(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,4,0,2);
        this.setLayoutParams(lp);
        mInflator = LayoutInflater.from(context);
        for(String key : keys) {
            addView(createRow(key, map.get(key)));
        }
    }

    private TableRow createRow(String key, String value) {
        TableRow row = new TableRow(getContext());
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(4,2,4,0);
        row.setLayoutParams(lp);
        row.setBackgroundResource(R.color.background_lightgray);
        TextView header = (TextView) mInflator.inflate(R.layout.customviews_textheader, null, false);
        header.setText(key);
        TextView text = new TextView(getContext());
        text.setText(value);
        text.setPadding(12, 4, 4, 4);
        text.setTextColor(R.color.text_dark);
        row.addView(header);
        row.addView(text);
        return row;
    }
}
