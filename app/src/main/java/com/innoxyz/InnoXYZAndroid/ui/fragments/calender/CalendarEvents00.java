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
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.calender.CalendarId;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.calender.CalendarIds;
import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;
import com.innoxyz.InnoXYZAndroid.global.InnoXYZApp;
import com.innoxyz.InnoXYZAndroid.ui.activities.DetailActivity;
import com.innoxyz.InnoXYZAndroid.ui.commands.ActivityCommand;
import com.innoxyz.InnoXYZAndroid.ui.decorates.AutoloadListView;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.CommonListViewAdapter;


/**
 * Created by dingwei on 14-5-20.
 */
public class CalendarEvents00 extends BaseFragment {

	SimpleObservedData<CalendarIds> calendarIds, loadeCalendarIds;
	public String r;
	CalendarId[] calendarIdss;

	@Override
	public void onResume(){
		super.onResume();
		getActivity().getActionBar().setTitle(R.string.title_calendar_Events);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
		calendarIds = new SimpleObservedData<CalendarIds>(new CalendarIds());
		loadeCalendarIds = new SimpleObservedData<CalendarIds>(new CalendarIds());
		ListView ret = new ListView(getActivity());
		ret.setDivider(null);
		final AutoloadListView autolv = new AutoloadListView(ret);
		loadeCalendarIds.registerObserver(new IDataObserver() {
			@Override
			public void update(Object o) {
				calendarIds.getData().append(loadeCalendarIds.getData());
				calendarIds.notifyObservers();
				// autolv.endLoad(calendarIds.getData().hasMore());
			}
		});
		autolv.setOnDataLoading(new AutoloadListView.OnDataLoading() {
			@Override
			public void LoadData(int page) {
				autolv.beginLoad();
				new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.Calendar_EventId)
						.addParameter("thingId", "" + InnoXYZApp.getApplication().getCurrentUserId())
						.addParameter("thingType", "USER")
						.setOnResponseListener(new JsonResponseHandler(loadeCalendarIds, CalendarIds.class, null))
						.request();
			}
		});
        ret.setAdapter(new CalendarListViewAdapter(getActivity()));
		if (CalendarEvents00.this.calendarIds.getData().CalendarIds!=null){
		calendarIdss = CalendarEvents00.this.calendarIds.getData().CalendarIds;
		Log.i("calendarIdss", calendarIdss.toString());
		}
		autolv.TriggerLoading();
		return ret;
	}

	private class CalendarListViewAdapter extends CommonListViewAdapter implements IDataObserver {
		private CalendarListViewAdapter(Context context) {
			super(context);
			calendarIds.registerObserver(this);
		}

        @Override
		protected void updateData() {
	        views = new View[1];
            views[0] = mInflator.inflate(R.layout.list_calendarids, null);
	        ((TextView) views[0].findViewById(R.id.calendarIdText)).setText("数据正在加载，请稍后。。。。。");

	        if(CalendarEvents00.this.calendarIds.getData().CalendarIds!=null){
	        calendarIdss = CalendarEvents00.this.calendarIds.getData().CalendarIds;
             String calendarId = "";
	        for(int i = 0; i<calendarIdss.length; i++){
		        if(calendarIdss[i].id!=null){
			        calendarId += "\""+calendarIdss[i].id+"\"";
			        calendarId += ",";
			      }
	        }
	        if(calendarId.length()!=0 && calendarId.charAt(calendarId.length()-1) == ','){
		        calendarId = calendarId.substring(0, calendarId.length()-1);
	        }
	        calendarId = "[" + calendarId;
	        calendarId += "]";
	        Log.i("calendarId", calendarId);
	        Bundle bundle = new Bundle();
	        bundle.putString("idList",calendarId);
	       new ActivityCommand(DetailActivity.class, CalendarEvents01.class, CalendarEvents00.this.getActivity(), bundle, null).Execute();
	        }else {

	        }
        }
	}
}
