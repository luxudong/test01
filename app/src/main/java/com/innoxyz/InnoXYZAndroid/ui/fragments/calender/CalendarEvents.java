package com.innoxyz.InnoXYZAndroid.ui.fragments.calender;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.data.runtime.SimpleObservedData;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.calender.CalendarGetEvents;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;


/**
 * Created by dingwei on 14-4-29.
 */

public class CalendarEvents extends BaseFragment {
SimpleObservedData<CalendarGetEvents> calendarGetEvents;
    @Override
    public void onResume() {
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.title_calendar_Event);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        calendarGetEvents = new SimpleObservedData<CalendarGetEvents>(new CalendarGetEvents());

    }
    private void setupHomeInfo(View view, Bundle args) {
        ((TextView)view.findViewById(R.id.calendarEvent_SummaryTitle)).setText(args.getString("summary"));
        ((TextView)view.findViewById(R.id.calendarEvent_startTime)).setText(args.getString("startTime"));
        ((TextView)view.findViewById(R.id.calendarEvent_endTime)).setText(args.getString("endTime"));
        //((TextView)view.findViewById(R.id.task_home_deadline)).setText(DateFunctions.RewriteDate(args.getString("deadline"), "yyyy-M-dd", "unknown"));
        ((TextView)view.findViewById(R.id.calendarEvent_location)).setText(args.getString("location"));
        ((TextView)view.findViewById(R.id.calendarEvent_description)).setText(args.getString("description"));
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.listitem_calendarevents, container, false);
       /* new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.LIST_TASK_IN_LPROJECT)
                .addParameter("hostId", "" + getArguments().getInt("projectId"))
                .addParameter("page", "" + page)
                .setOnResponseListener(new JsonResponseHandler(loadedTasks, Tasks.class, Arrays.asList("pager")))
                .request();*/

       /* new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.Calendar_SubEvents)
                //.addParameter("from","")
                //.addParameter("to","")
                // .setOnResponseListener(new JsonResponseHandler(loadedPushstate, Pushstates.class, Arrays.asList("pager")))
                .setOnResponseListener(new JsonResponseHandler(calendarGetEvents,CalendarGetEvents.class,Arrays.asList("page")))
                .request();*/
      /*if(calendarGetEvents.getData().CalendarGetEvents!=null){
            Log.i("calendar00", "calendarGetEvents: " + calendarGetEvents.getData().CalendarGetEvents.length);
        }*/

        //getArguments().getString()
       setupHomeInfo(ret, getArguments());
        //((TextView)ret.findViewById(R.id.taskhome_projectname)).setText( getResources().getString(R.string.template_topichome_projectname).replace("%name%", getArguments().getString("projectName")) );
       return ret;
       // return super.onCreateView(inflater, container, savedInstanceState);
    }


}
