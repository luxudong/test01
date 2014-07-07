package com.innoxyz.InnoXYZAndroid.ui.customviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.innoxyz.InnoXYZAndroid.R;

/**
 * Created by laborish on 14-1-15.
 */
public class TabWithNumber extends RelativeLayout {
    TextView name;
    TextView number;
    View badeg;


    public TabWithNumber(Context context){
        super(context);
    }

    public TabWithNumber(Context context, String s){
        super(context);

        ((LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.customviews_tab_with_number, this, true);

        name = (TextView) findViewById(R.id.tab_text);
        name.setText(s);

        number = (TextView) findViewById(R.id.tab_badge_text);

        badeg = findViewById(R.id.tab_badge);
    }

    public void setName(String s){
        name.setText(s);
    }

    public void setTextColor(int color) {
        name.setTextColor(color);
    }

    public void setNumber(int i){
        if(i <= 0){
            badeg.setVisibility(GONE);
        }
        else {
            badeg.setVisibility(VISIBLE);
            if( i > 99){
                number.setText("99+");
            }
            number.setText(String.valueOf(i));
        }
    }
}
