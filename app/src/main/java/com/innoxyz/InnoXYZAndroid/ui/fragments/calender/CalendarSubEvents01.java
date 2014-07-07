package com.innoxyz.InnoXYZAndroid.ui.fragments.calender;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.data.remote.AddressURIs;
import com.innoxyz.InnoXYZAndroid.data.remote.StringRequestBuilder;
import com.innoxyz.InnoXYZAndroid.data.remote.response.JsonResponseHandler;
import com.innoxyz.InnoXYZAndroid.data.runtime.SimpleObservedData;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.calender.CalendarGetEvent;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.calender.CalendarGetEvents;
import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;
import com.innoxyz.InnoXYZAndroid.ui.decorates.AutoloadListView;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.CommonListViewAdapter;
import com.innoxyz.InnoXYZAndroid.ui.utils.DateFunctions;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by dingwei on 14-5-15.
 */
public class CalendarSubEvents01 extends BaseFragment {
    //SimpleObservedData<Tasks> calendarSubEvents, loadedCalendarSubEvents;

    SimpleObservedData<CalendarGetEvents> calendarSubEvents, loadedCalendarSubEvents;

	@Override
	public void onResume(){
		super.onResume();
		getActivity().getActionBar().setTitle(R.string.title_calendar_SubEvents);
	}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        calendarSubEvents = new SimpleObservedData<CalendarGetEvents>(new CalendarGetEvents());
        loadedCalendarSubEvents = new SimpleObservedData<CalendarGetEvents>(new CalendarGetEvents());

	    Date data =new Date() ;
	    // SimpleDateFormat   sDateFormat1   =   new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
	    SimpleDateFormat sDateFormat1   =   new SimpleDateFormat("yyyy-MM-dd'T00:00:00.000Z'");
	    final String s3=sDateFormat1.format(data);
	    Log.i("s3", s3);
        ListView ret = new ListView(getActivity());
        ret.setDivider(null);
        final AutoloadListView autolv = new AutoloadListView(ret);
        loadedCalendarSubEvents.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {
                calendarSubEvents.getData().append(loadedCalendarSubEvents.getData());
                calendarSubEvents.notifyObservers();
               // autolv.endLoad(calendarSubEvents.getData().hasMore());
            }
        });
        autolv.setOnDataLoading(new AutoloadListView.OnDataLoading() {
          //【思考】这下面的LoadData(int page)里面的int page 参数是否可以去掉，因为这里面没有page这个参数。
	        //要去掉要去把AutoloadListView.java 中的OnDataLoading方法的LoadData(int page)中的int page参数去掉才可以，但是这个java文件已被很多地方
	        //使用，必须要重新写一个方法去重写他才可以的。
            @Override
            public void LoadData(int page) {
                autolv.beginLoad();
	            //已经测试过这样写的日期是可以的。
                new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.Calendar_SubEvents)
		                //.addParameter("from", "2014-04-27T15:59:59.999Z")
		               // .addParameter("from", "2014-04-27T15:59:59.999Z")
		                .addParameter("from", s3)
		                .addParameter("to", s3)
                        .setOnResponseListener(new JsonResponseHandler(loadedCalendarSubEvents, CalendarGetEvents.class, null))
                        .request();
            }
        });
        ret.setAdapter(new CalendarListViewAdapter(getActivity()));
        autolv.TriggerLoading();
        return ret;
    }

    private class CalendarListViewAdapter extends CommonListViewAdapter implements IDataObserver {
        private CalendarListViewAdapter(Context context) {
            super(context);
            calendarSubEvents.registerObserver(this);
        }




        @Override
        protected void updateData() {

         CalendarGetEvent[] calendarSubEvents = CalendarSubEvents01.this.calendarSubEvents.getData().CalendarGetEvents;
	        if(calendarSubEvents.length!=0){

	        int count=0;
	        for(int i=0; i<calendarSubEvents.length; i++) {
		        for(int j=0;j<calendarSubEvents[i].events.length;j++){
			        count++;
		        }
		    }


            views = new View[count];

	        Log.i("calendarSubEvents.length", "" + calendarSubEvents.length);
	        Log.i("views.length", "" + views.length);
	        Log.i("calendarSubEvents[0].events.length", "" + calendarSubEvents[0].events.length);
	        Log.i("calendarSubEvents[1].events.length", "" + calendarSubEvents[1].events.length);


	        int index=0;
            for(int i=0; i<calendarSubEvents.length; i++) {
                   for(int j=0;j<calendarSubEvents[i].events.length;j++){
                    //二位数组，要2次循环才可以得到。
			        views[index] = mInflator.inflate(R.layout.listitem_subcalendar, null);
			        views[index].setTag(R.id.item_object, calendarSubEvents[i]);
			        ((TextView)views[index].findViewById(R.id.calendarEvent_SummaryTitle)).setText(calendarSubEvents[i].events[j].summary);
			        Log.e("events", calendarSubEvents[i].events.toString());
			        ((TextView)views[index].findViewById(R.id.calendarEvent_startTime)).setText(DateFunctions.RewriteDate(calendarSubEvents[i].events[j].startTime));
			        ((TextView)views[index].findViewById(R.id.calendarEvent_endTime)).setText(DateFunctions.RewriteDate(calendarSubEvents[i].events[j].endTime));
			        ((TextView)views[index].findViewById(R.id.calendarEvent_location)).setText(calendarSubEvents[i].events[j].location);
			        ((TextView)views[index].findViewById(R.id.calendarEvent_description)).setText(calendarSubEvents[i].events[j].description);
			          index++;
		        }
	        }
        }else{
		        int index=0;
		        views = new View[1];
		        views[index] = mInflator.inflate(R.layout.listitem_subcalendar, null);
		        ((TextView)views[index].findViewById(R.id.calendarEvent_SummaryTitle)).setText("暂无您所需要的数据");
		        ((TextView) views[index].findViewById(R.id.calendarEvent_startTime)).setText("暂无您所需要的数据");
		        ((TextView)views[index].findViewById(R.id.calendarEvent_endTime)).setText("暂无您所需要的数据");
		        ((TextView)views[index].findViewById(R.id.calendarEvent_location)).setText("暂无您所需要的数据");
		        ((TextView)views[index].findViewById(R.id.calendarEvent_description)).setText("暂无您所需要的数据");
		        Log.i("测试无数据情况03", "003");
	        }
        }
    }
}
