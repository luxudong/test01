package com.innoxyz.InnoXYZAndroid.ui.fragments.calender;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.ui.commands.FragmentCommand;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;

/**
 * Created by dingwei on 14-4-29.
 */
public class CalendarEvent extends BaseFragment {
private Button b1,b2;
    @Override
    public void onResume() {
        super.onResume();
      getActivity().getActionBar().setTitle(R.string.title_calendar_Event);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ret=inflater.inflate(R.layout.listitem_calendarevent,container,false);
        b1=(Button)ret.findViewById(R.id.calendarEvent_default);
        b2= (Button) ret.findViewById(R.id.calendarEvent_Subscribe);
        b1.setOnClickListener(new OnGetDefaultCalendarEventsClickListener());
        b2.setOnClickListener(new OnGetSubscribleCalendarEventsClickListener());
        return ret;
       // return super.onCreateView(inflater, container, savedInstanceState);

    }

    private class OnGetDefaultCalendarEventsClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
           // new FragmentCommand(CalendarEvent.class, CalendarEvents.class, CalendarEvent.this.getActivity(), getArguments(), null).Execute();
            //new FragmentCommand(CalendarEvent.class, CalendarEvents01.class, CalendarEvent.this.getActivity(), getArguments(), null).Execute();
            new FragmentCommand(CalendarEvent.class, CalendarEvents02.class, CalendarEvent.this.getActivity(), getArguments(), null).Execute();
        }
    }


    private class OnGetSubscribleCalendarEventsClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new FragmentCommand(CalendarEvent.class, CalendarSubEvents01.class, CalendarEvent.this.getActivity(), getArguments(), null).Execute();
        }
    }

}
