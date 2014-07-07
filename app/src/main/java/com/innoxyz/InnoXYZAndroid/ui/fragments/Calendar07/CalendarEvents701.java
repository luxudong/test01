package com.innoxyz.InnoXYZAndroid.ui.fragments.Calendar07;

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
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.calender.CalendarIds;
import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;
import com.innoxyz.InnoXYZAndroid.global.InnoXYZApp;
import com.innoxyz.InnoXYZAndroid.ui.decorates.AutoloadListView;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.CommonListViewAdapter;
import com.innoxyz.InnoXYZAndroid.ui.utils.DateFunctions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by dingwei on 14-5-15.
 */
public class CalendarEvents701 extends BaseFragment{

	SimpleObservedData<CalendarGetEvents> calendarEvents, loadedCalendarEvents;
	SimpleObservedData<CalendarIds> calendarIds;
	public String value;

	@Override
	public void onResume(){
		super.onResume();
		getActivity().getActionBar().setTitle(R.string.title_calendar_Events);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
		calendarEvents = new SimpleObservedData<CalendarGetEvents>(new CalendarGetEvents());
		loadedCalendarEvents = new SimpleObservedData<CalendarGetEvents>(new CalendarGetEvents());


        //SimpleDateFormat   sDateFormat   =   new SimpleDateFormat("yyyy-MM-dd   hh:mm:ss");
        SimpleDateFormat sDateFormat   =   new SimpleDateFormat("yyyy-MM-dd");
        String date   =   sDateFormat.format(new Date());
		//后面的是时分秒，毫秒
		String date1=date+"T15:59:59.999Z";
		Log.i("date1", date1);
		//date1输出为：2014-05-23T15:59:59.999Z
		//单引号的意思是直接读取就可以，不用做处理的。
		/****************************************************************/
		Date data =new Date() ;
		//SimpleDateFormat   sDateFormat1   =   new SimpleDateFormat("yyyy-MM-dd'T'hh+12:mm:ss.SSS'Z'");
		SimpleDateFormat sDateFormat1   =   new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
		final String s=sDateFormat1.format(data);
		Log.i("s", s);

		long dataTime=data.getTime();
		Log.i("dataTime", dataTime + "");
		Long data07= Long.valueOf(dataTime)+7*24*3600*1000;
		SimpleDateFormat sDateFormat2   =   new SimpleDateFormat("yyyy-MM-dd'T00:00:00.000Z'");
		final String s1=sDateFormat2.format(data07);
		Log.i("s1", s1);
     /****************************************************************/
	   long dataTime1=data.getTime();
		Log.i("dataTime", dataTime1 + "");


		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		Calendar calendar3 = Calendar.getInstance();
		int dayOfWeek=calendar1.get(Calendar.DAY_OF_WEEK)-1;
		int dayOfWeek2=calendar3.get(Calendar.DAY_OF_WEEK)+1;
		int offset1=1-dayOfWeek;
		int offset2=7-dayOfWeek;
		int offset3=1+dayOfWeek2;
		calendar1.add(Calendar.DATE, offset1-7);
		calendar2.add(Calendar.DATE, offset2-7);
		calendar3.add(Calendar.DATE,offset3+7);

		Log.i("calendar1", calendar1.getTime().toString());
		Log.i("calendar2", calendar2.getTime().toString());
		Log.i("calendar3", calendar3.getTime().toString());
		//System.out.println(calendar1.getTime());//last Monday
		//System.out.println(calendar2.getTime());//last Sunday
		
        DateFunctions.RewriteDate(date);
       Calendar.getInstance();
        Log.i("currentTime", DateFunctions.RewriteDate(date));
        //currentTime﹕ unknown
        Log.i("currentTime01", date);
       // currentTime01﹕ 2014-05-23
        Log.i("currentTime02", Calendar.getInstance().toString());



        Bundle bundle =new Bundle(getArguments());
		value=bundle.getString("idList");
       if (bundle.getString("idList")!=null){
		Log.i("idList09", value);
		}



		ListView ret = new ListView(getActivity());
		ret.setDivider(null);
		final AutoloadListView autolv = new AutoloadListView(ret);
		loadedCalendarEvents.registerObserver(new IDataObserver() {
			@Override
			public void update(Object o) {
				calendarEvents.getData().append(loadedCalendarEvents.getData());
				calendarEvents.notifyObservers();
				//autolv.endLoad(calendarEvents.getData().hasMore());
			}
		});
		Log.i("thingId", String.valueOf(InnoXYZApp.getApplication().getCurrentUserId()));
		autolv.setOnDataLoading(new AutoloadListView.OnDataLoading() {
	@Override
			public void LoadData(int page) {
				autolv.beginLoad();

			new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.Calendar_Events)
						//.addParameter("idList","["52fc78d9f81e7e1ea399b619","53508f91e674a5da017b643b","53508ff2e674a5da017b6445","535a0611e674a5da017b95c1"]")
						//.addParameter("idList", "[\"52fc78d9f81e7e1ea399b619\",\"53508f91e674a5da017b643b\",\"53508ff2e674a5da017b6445\",\"535a0611e674a5da017b95c1\"]")
						.addParameter("idList", value)
						.addParameter("from", s)
						.addParameter("to", s1 )
						.setOnResponseListener(new JsonResponseHandler(loadedCalendarEvents, CalendarGetEvents.class, null))
						.request();
			}
		});
		ret.setAdapter(new CalendarListViewAdapter(getActivity()));
		//这一行代码不是显示正在加载的，去掉什么数据也显示不了。
		autolv.TriggerLoading();
		return ret;
	}
    private class GetCalendarIds implements IDataObserver{
	    @Override
	    public void update(Object o) {

	    }
    }
	private class CalendarListViewAdapter extends CommonListViewAdapter implements IDataObserver {
		private CalendarListViewAdapter(Context context) {
			super(context);
			calendarEvents.registerObserver(this);
		}


		
		@Override
		protected void updateData() {
			//  Task[] calendarEvents = CalendarSubEvents01.this.calendarEvents.getData().calendarEvents;

			/*CalendarId[] calendarIds = CalendarEvents01.this.calendarIds.getData().CalendarIds;
			Log.i("calendarEvents.length", "" + calendarIds.length);

			String[] s ;
			for(int j=0; j<calendarIds.length; j++) {
				//Array[] id=new Array[];
				s = new String[calendarIds.length] ;

					s[j] =calendarIds[j].id.toString();
                 Log.i("CalendarId",s[j]);

				}*/


			CalendarGetEvent[] calendarEvents = CalendarEvents701.this.calendarEvents.getData().CalendarGetEvents;
			Log.i("calendarEvents701", String.valueOf(calendarEvents.length));
			if(calendarEvents.length!=0){

			int count=0;
			for(int i=0; i<calendarEvents.length; i++) {
				for(int j=0;j<calendarEvents[i].events.length;j++){
					count++;
				}
			}
				views = new View[count];
          /*if (count!=0){
	          views = new View[count];
          }else{
	          views=new View[1];
          }*/




			int index=0;
			for(int i=0; i<calendarEvents.length; i++) {
				for(int j=0;j<calendarEvents[i].events.length;j++){
					//二位数组，要2次循环才可以得到。

					views[index] = mInflator.inflate(R.layout.listitem_subcalendar, null);
					views[index].setTag(R.id.item_object, calendarEvents[i]);
					//Log.i("ceshi 09",calendarEvents[0].events[0].summary);
					Log.i("ceshi 09", "28");
					if (calendarEvents[i].events[j].summary!=null){
					((TextView)views[index].findViewById(R.id.calendarEvent_SummaryTitle)).setText(calendarEvents[i].events[j].summary);
					Log.e("events", calendarEvents[i].events.toString());
					//((TextView) views[index].findViewById(R.id.calendarEvent_startTime)).setText(calendarEvents[i].events[j].startTime);
					((TextView) views[index].findViewById(R.id.calendarEvent_startTime)).setText(DateFunctions.RewriteDate(calendarEvents[i].events[j].startTime));
					((TextView)views[index].findViewById(R.id.calendarEvent_endTime)).setText(DateFunctions.RewriteDate(calendarEvents[i].events[j].endTime));
					((TextView)views[index].findViewById(R.id.calendarEvent_location)).setText(calendarEvents[i].events[j].location);
					((TextView)views[index].findViewById(R.id.calendarEvent_description)).setText(calendarEvents[i].events[j].description);
					}else{
						views[index] = mInflator.inflate(R.layout.listitem_subcalendar, null);
						//views[index].setTag(R.id.item_object, calendarEvents[i]);
						((TextView)views[index].findViewById(R.id.calendarEvent_SummaryTitle)).setText("暂无您所需要的 数据");
						Log.e("events", calendarEvents[i].events.toString());
						//((TextView) views[index].findViewById(R.id.calendarEvent_startTime)).setText(calendarEvents[i].events[j].startTime);
						((TextView) views[index].findViewById(R.id.calendarEvent_startTime)).setText("暂无您所需要的 数据");
						((TextView)views[index].findViewById(R.id.calendarEvent_endTime)).setText("暂无您所需要的 数据");
						((TextView)views[index].findViewById(R.id.calendarEvent_location)).setText("暂无您所需要的 数据");
						((TextView)views[index].findViewById(R.id.calendarEvent_description)).setText("暂无您所需要的 数据");
						Log.i("测试无数据情况01", "001");
					}

					index++;
					Log.i("index", String.valueOf(index));
				}
			}
		}
			else{
			/*int index1=0;
				views =new View[1];
				views[0]=mInflator.inflate(R.layout.list_calendarids,null);
				((TextView) views[index1].findViewById(R.id.calendarIdText)).setText("不好意思，暂无您所需要的日历事件。");
	        Log.i("测试无数据情况","007");*/
				int index=0;
				views = new View[1];
				views[index] = mInflator.inflate(R.layout.listitem_subcalendar, null);
				//views[index].setTag(R.id.item_object, calendarEvents[i]);
				((TextView)views[index].findViewById(R.id.calendarEvent_SummaryTitle)).setText("暂无您所需要的数据");
				//Log.e("events", calendarEvents[0].events.toString());//这里的log日志不可以乱打的，因为这里的是calendarEvents[0].events是null,所以以后要注意，要是这种情况需要先判断一下。
				((TextView) views[index].findViewById(R.id.calendarEvent_startTime)).setText("暂无您所需要的数据");
				((TextView)views[index].findViewById(R.id.calendarEvent_endTime)).setText("暂无您所需要的数据");
				((TextView)views[index].findViewById(R.id.calendarEvent_location)).setText("暂无您所需要的数据");
				((TextView)views[index].findViewById(R.id.calendarEvent_description)).setText("暂无您所需要的数据");
				Log.i("测试无数据情况02", "002");
			}



		}
		protected void updataCalendarData(){
			 CalendarGetEvent[] calendarEvents = CalendarEvents701.this.calendarEvents.getData().CalendarGetEvents;

			 int count=0;
			 for(int i=0; i<calendarEvents.length; i++) {
				 for(int j=0;j<calendarEvents[i].events.length;j++){
					 count++;
				 }
			 }


			 views = new View[count];

			 Log.i("calendarEvents.length", "" + calendarEvents.length);
			 Log.i("views.length", "" + views.length);
			 Log.i("calendarEvents[0].events.length", "" + calendarEvents[0].events.length);
			 Log.i("calendarEvents[1].events.length", "" + calendarEvents[1].events.length);


			 int index=0;
			 for(int i=0; i<calendarEvents.length; i++) {
				 for(int j=0;j<calendarEvents[i].events.length;j++){
					 //二位数组，要2次循环才可以得到。
					 views[index] = mInflator.inflate(R.layout.listitem_subcalendar, null);
					 views[index].setTag(R.id.item_object, calendarEvents[i]);
					 ((TextView)views[index].findViewById(R.id.calendarEvent_SummaryTitle)).setText(calendarEvents[i].events[j].summary);
					 Log.e("events", calendarEvents[i].events.toString());
					 //((TextView) views[index].findViewById(R.id.calendarEvent_startTime)).setText(calendarEvents[i].events[j].startTime);
					 ((TextView) views[index].findViewById(R.id.calendarEvent_startTime)).setText(DateFunctions.RewriteDate(calendarEvents[i].events[j].startTime));
					 ((TextView)views[index].findViewById(R.id.calendarEvent_endTime)).setText(DateFunctions.RewriteDate(calendarEvents[i].events[j].endTime));
					 ((TextView)views[index].findViewById(R.id.calendarEvent_location)).setText(calendarEvents[i].events[j].location);
					 ((TextView)views[index].findViewById(R.id.calendarEvent_description)).setText(calendarEvents[i].events[j].description);

					 index++;
				 }
			 }
		 }

	}
}
