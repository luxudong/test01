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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dingwei on 2014/5/23.
 */
public class CalendarEvents02 extends BaseFragment{

    SimpleObservedData<CalendarIds> calendarIds, loadeCalendarIds;
    public String r;

    @Override
    public void onResume(){
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.title_calendar_SubEvents);
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

            CalendarId[] calendarIds = CalendarEvents02.this.calendarIds.getData().CalendarIds;
	       /* int count=0;
			for(int i=0; i<calendarIds.length; i++) {
				count++;
			}*/
            //views = new View[count];
            views = new View[1];
            //Log.i("calendarIds.length",""+calendarIds.length);
            int index = 0;
            views[0] = mInflator.inflate(R.layout.list_calendarids, null);
            ((TextView) views[index].findViewById(R.id.calendarIdText)).setText("数据正在加载，请稍后。。。。。");


	        //calendarId:
	        String calendarId = "";
	        List<String> list01 = new ArrayList<String>(calendarIds.length);
	        //List<String> assignedName = assignedSpinner.getSelectedStrings();
	        for(int i = 0; i<calendarIds.length; i++){
		        if(calendarIds[i].id!=null){
			       calendarId += "\""+calendarIds[i].id+"\"";
                   calendarId += ",";
			       // calendarId +=  "\""+calendarIds[i].id+"\"";
		        }
	        }
	        if(calendarId.length()!=0 && calendarId.charAt(calendarId.length()-1) == ','){
		        calendarId = calendarId.substring(0, calendarId.length()-1);
	        }
	        calendarId = "[" + calendarId;
	        calendarId += "]";
	        Log.i("calendarId", calendarId);

/**************************************************************************************/
            //ArrayList<String> list0 = new ArrayList<String>(calendarIds.length);
            List<String> list0 = new ArrayList<String>();
            String[] n = new String[calendarIds.length];
            int j;
            for (j = 0; j < calendarIds.length; j++){

                //list0.add(calendarIds[j].id.toString());
                list0.add(calendarIds[j].id);
               // List.add(String.valueOf(calendarIds[j].id+ " "));
		        /*n[j] = new String(calendarIds[j].id);
                List.add(n[j]);*/

            }
            list0.add("11111");
	       /* List<String> myList=new ArrayList<String>();
	        for (String string :myList){
               }*/


          /**********************************************************/
	      /*  StringBuffer stringBuffer = new StringBuffer("");

	        for(String string : list0){
		        stringBuffer.append(string.replace(" ",""));
	        }


	        String ids = stringBuffer.toString();*/
/**********************************************************/
            List idList=changeValue(list0);
           // List idList=changeValue(ids1);
            Log.i("idList0", list0.toString());
           Log.i("idList", idList.toString());
           // Log.i("idList",ids);
/***************************************************************************/
          Bundle bundle = new Bundle();
	     // bundle.putString("idList", idList.toString());
	      bundle.putString("idList", calendarId);
          new ActivityCommand(DetailActivity.class, CalendarEvents01.class, CalendarEvents02.this.getActivity(), bundle, null).Execute();
        }


    }
    public static List changeValue(List list){
        List newList=new ArrayList();
        if(null!=list&&list.size()>0){
            for(int i=0;i<list.size();i++){
               // String value="\""+(String)list.get(i)+"\"";
                String value="\""+list.get(i).toString().replace(" ","")+"\"";
	            //value.replace(" ","");

	            newList.add(value);
            }
        }
        return newList;
    }
}
