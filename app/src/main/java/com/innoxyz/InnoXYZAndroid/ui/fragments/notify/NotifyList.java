package com.innoxyz.InnoXYZAndroid.ui.fragments.notify;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.data.remote.AddressURIs;
import com.innoxyz.InnoXYZAndroid.data.remote.StringRequestBuilder;
import com.innoxyz.InnoXYZAndroid.data.remote.interfaces.OnPostListener;
import com.innoxyz.InnoXYZAndroid.data.remote.response.JsonResponseHandler;
import com.innoxyz.InnoXYZAndroid.data.remote.response.PostResponseHandler;
import com.innoxyz.InnoXYZAndroid.data.runtime.SimpleObservedData;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.User;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.notify.Notifications;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.notify.Notify;
import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;
import com.innoxyz.InnoXYZAndroid.global.InnoXYZApp;
import com.innoxyz.InnoXYZAndroid.ui.activities.DetailActivity;
import com.innoxyz.InnoXYZAndroid.ui.commands.ActivityCommand;
import com.innoxyz.InnoXYZAndroid.ui.customviews.PullToRefreshListView;
import com.innoxyz.InnoXYZAndroid.ui.decorates.AutoloadListView;
import com.innoxyz.InnoXYZAndroid.ui.fragments.announcement.AnnouncementHome;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.CommonListViewAdapter;
import com.innoxyz.InnoXYZAndroid.ui.fragments.task.TaskHome;
import com.innoxyz.InnoXYZAndroid.ui.fragments.topic.TopicHome;
import com.innoxyz.InnoXYZAndroid.ui.utils.DateFunctions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

//import com.innoxyz.InnoXYZAndroid.ui.customviews.PullToRefreshListView.OnRefreshListener;
//import com.markupartist.android.widget.PullToRefreshListView;

/**
 * Created with IntelliJ IDEA
 * User: InnoXYZ
 * Date: 13-11-26
 * Time: 下午8:19
 */
public class NotifyList extends BaseFragment {

    private class NotifyType{
        public int imageId;
        public int stringId;

        public NotifyType(int stringId, int imageId){
            this.stringId = stringId;
            this.imageId = imageId;
        }
    }
    public final static HashMap<String,NotifyType> visibleNotifyType = new HashMap<String,NotifyType>();
    public final static HashMap<String,Integer> subNotifyType = new HashMap<String, Integer>();

    SimpleObservedData<Notifications> notifications, loadedNotifications;
    PullToRefreshListView pullToRefreshListView;
    AutoloadListView autoloadListView;
    List<Notify> visibleNotifyList;
    NotifyListViewAdapter notifyListViewAdapter;
    boolean fetchData;
    boolean doneFlag = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Log.e("", "NotifyList onCreate called");

        fetchData = true;

        visibleNotifyType.put("TOPIC", new NotifyType(R.string.notify_list_topic, R.drawable.icon_discusslist));
        visibleNotifyType.put("TASK", new NotifyType(R.string.notify_list_task, R.drawable.icon_tasklist));
        visibleNotifyType.put("ANNOUNCEMENT", new NotifyType(R.string.notify_list_announcement, R.drawable.icon_announcementlist));
        visibleNotifyType.put("FILE", new NotifyType(R.string.notify_list_file, R.drawable.icon_documentlist));

        subNotifyType.put("TOPIC_NEW", R.string.notify_list_topic_topic_new);
        subNotifyType.put("REPLY_NEW", R.string.notify_list_topic_reply_new);
        subNotifyType.put("INVOLVED_NEW", R.string.notify_list_topic_involved_new);
        subNotifyType.put("TASK_NEW", R.string.notify_list_task_task_new);
        subNotifyType.put("TASK_PUSHSTATE", R.string.notify_list_task_task_pushstate);
        subNotifyType.put("FILE_NEW", R.string.notify_list_file_file_new);
        subNotifyType.put("ANNOUNCE_NEW", R.string.notify_list_announcement_announce_new);

        visibleNotifyList = new ArrayList<Notify>();

        notifications = new SimpleObservedData<Notifications>(new Notifications());
        loadedNotifications = new SimpleObservedData<Notifications>(new Notifications());
        //ListView ret = new ListView(getActivity());
        pullToRefreshListView = new PullToRefreshListView(getActivity());
        pullToRefreshListView.setCacheColorHint(0x00000000);
        pullToRefreshListView.setDivider(null);
        autoloadListView = new AutoloadListView(pullToRefreshListView);
        loadedNotifications.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {
                notifications.getData().append(loadedNotifications.getData());
                notifications.notifyObservers();
                autoloadListView.endLoad(notifications.getData().hasMore());
            }
        });
        autoloadListView.setOnDataLoading(new AutoloadListView.OnDataLoading() {
            @Override
            public void LoadData(int page) {
                autoloadListView.beginLoad();
                new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.LIST_NOTIFY)
                        .addParameter("pageSize", "10")
                        .addParameter("page", "" + page)
                        .addParameter("read", doneFlag?"true":"false")
                        .setOnResponseListener(new JsonResponseHandler(loadedNotifications, Notifications.class, Arrays.asList("pager")))
                        .request();

            }
        });
        notifications.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pullToRefreshListView.onRefreshComplete();
                    }
                });
            }
        });
        pullToRefreshListView.setLockScrollWhileRefreshing(false);
        pullToRefreshListView.setShowLastUpdatedText(true);
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //clean cache before refresh
                notifications.setData(new Notifications(), false);
                autoloadListView.reLoading();
                //release, let service update unread number now
                InnoXYZApp.getApplication().getGetUnreadNumNow().release();
            }
        });
        notifyListViewAdapter = new NotifyListViewAdapter(getActivity());
        pullToRefreshListView.setAdapter(notifyListViewAdapter);


//        SwipeDismissListViewTouchListener touchListener =
//                new SwipeDismissListViewTouchListener(
//                        pullToRefreshListView,
//                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
//                            @Override
//                            public boolean canDismiss(int position){
//                                return true;
//                            }
//
//
//                            @Override
//                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
//                                for (int position : reverseSortedPositions) {
//                                    //notifyListViewAdapter.remove(notifyListViewAdapter.getItem(position));
//                                    Log.e("AAAAAAAAAAAAAAA","position del:" + position);
//                                }
//                                notifyListViewAdapter.notifyDataSetChanged();
//                            }
//                        });
//        pullToRefreshListView.setOnTouchListener(touchListener);
//        pullToRefreshListView.setOnScrollListener(touchListener.makeScrollListener());



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        //Log.e("","NotifyList onCreateView called");

        if(fetchData){
            autoloadListView.TriggerLoading();
            fetchData = false;
        }

        return pullToRefreshListView;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(doneFlag) {
            getActivity().getActionBar().setTitle(R.string.title_nofity_done);
        }
        else{
            getActivity().getActionBar().setTitle(R.string.title_nofity_undone);
        }
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.notify_fragment_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_switch){
            doneFlag = !doneFlag;
            //clean cache before refresh
            notifications.setData(new Notifications(), false);
            autoloadListView.reLoading();
            if(doneFlag) {
                getActivity().getActionBar().setTitle(R.string.title_nofity_done);
            }
            else{
                getActivity().getActionBar().setTitle(R.string.title_nofity_undone);
            }

        }

        //consume it
        return true;
    }

    static class ViewHolder{
        TextView listnotify_button_textView,listnotify_item_name,listnotify_projname,listnotify_last_update_time,listnotify_last_update_username,listnotify_last_update_content;
        ImageView listnotify_button_imageView, listnotify_acctpt;
    }

    private class NotifyListViewAdapter extends CommonListViewAdapter implements IDataObserver {


        private NotifyListViewAdapter(Context context){
            super(context);

            notifications.registerObserver(this);
        }

        private View.OnClickListener onClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Notify notify = (Notify) view.getTag(R.id.item_object);
                Bundle bundle = new Bundle();
                bundle.putInt("projectId", notify.hostId);
                bundle.putString("projectName", notify.hostName);

                //TASK
                if(notify.thingType.equals("TASK")){
                    bundle.putString("taskId", notify.thing.id);
                    bundle.putString("taskName", notify.thing.name);
                    bundle.putInt("creatorId", notify.thing.creatorId);
                    bundle.putString("creatorName", notify.thing.creatorName);
                    String assignees = "";
                    for(User u : notify.thing.assignees) {
                        assignees += u.realName + " ";
                    }
                    bundle.putString("assignees", assignees);
                    bundle.putString("priority", notify.thing.priority.toString());
                    bundle.putString("description", notify.thing.description);
                    bundle.putString("deadline", notify.thing.deadline);
                    bundle.putString("state", notify.thing.state.toString());
                    //new FragmentCommand(NotifyList.class, TaskHome.class, NotifyList.this.getActivity(), bundle, null).Execute();
                    new ActivityCommand(DetailActivity.class, TaskHome.class, NotifyList.this.getActivity(), bundle, null).Execute();
                }

                //TOPIC
                else if(notify.thingType.equals("TOPIC")){
                    bundle.putString("topicId", notify.thing.id);
                    //new FragmentCommand(NotifyList.class, TopicHome.class, NotifyList.this.getActivity(), bundle, null).Execute();
                    new ActivityCommand(DetailActivity.class, TopicHome.class, NotifyList.this.getActivity(), bundle, null).Execute();
                }

                //ANNOUNCEMENT
                else if(notify.thingType.equals("ANNOUNCEMENT")){
                    bundle.putString("projectName", notify.hostName);
                    bundle.putString("content", notify.thing.content);
                    //new FragmentCommand(NotifyList.class, AnnouncementHome.class, NotifyList.this.getActivity(), bundle, null).Execute();
                    new ActivityCommand(DetailActivity.class, AnnouncementHome.class, NotifyList.this.getActivity(), bundle, null).Execute();
                }
            }
        };

        private View.OnClickListener acceptClicked = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Notify notify = (Notify) view.getTag(R.id.item_object);


                new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.SET_NOTIFY_READ)
                        .addParameter("thingId", notify.thing.id)
                        .addParameter("thingType", notify.thingType)
                        .addParameter("value", doneFlag?"true":"false")
                        .setOnResponseListener(new PostResponseHandler(new NoitfySetReadListener()))
                        .request();
            }
        };

        private class NoitfySetReadListener implements OnPostListener {

            public void onPostSuccess(){
                //Log.e("AAAAAAAAAAAA", "SUCCESSSSSSSSS");
                if(doneFlag) {
                    Toast.makeText(getActivity(), R.string.notify_set_unread_success, Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getActivity(), R.string.notify_set_read_success, Toast.LENGTH_SHORT).show();
                }


                //clean cache before refresh
                notifications.setData(new Notifications(), false);
                autoloadListView.reLoading();
                //release, let service update unread number now
                InnoXYZApp.getApplication().getGetUnreadNumNow().release();

            }

            public void onPostFail(){

                //Log.e("AAAAAAAAAAAA", "FAILLLLLLLLLLLLL");
                Toast.makeText(getActivity(), R.string.notify_set_read_fail, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public int getCount() {
            return visibleNotifyList.size();
        }

        /**
         *
         * @param timeStr  包含时间的字符串
         * @return 返回经过处理过的时间，人性化显示
         */
        private String getStandardDate(String timeStr) {
            StringBuffer sb = new StringBuffer();
            String re_time = "";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");   //注意，这里T和Z一定要加单引号,服务器传回来的时间格式
            Date d =null;
            try {
                d = sdf.parse(timeStr);
                long l = d.getTime();
                String str = String.valueOf(l);
                re_time = str;
                // re_time就是时间戳
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long t = Long.parseLong(re_time);
            long time = System.currentTimeMillis() - t - 8 * 60 * 60 * 1000;                            //服务器是美国时间，差了8个小时
            //Log.e("time",String.valueOf(time));
            long mill = (long) Math.ceil(time / 1000);//秒前
            long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前
            long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时
            long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前

            if (day - 1 > 0) {
                return DateFunctions.RewriteDate(timeStr,"yyyy-M-d","unknown");
            } else if (hour - 1 > 0) {
                if (hour >= 24) {
                    sb.append("1天");
                } else {
                    //Log.e("",String.valueOf(hour));
                    sb.append(hour + "小时");
                }
            } else if (minute - 1 > 0) {
                if (minute == 60) {
                    sb.append("1小时");
                } else {
                    sb.append(minute + "分钟");
                }
            } else if (mill - 1 > 0) {
                if (mill == 60) {
                    sb.append("1分钟");
                } else {
                    sb.append(mill + "秒");
                }
            } else {
                sb.append("刚刚");
            }
            if (!sb.toString().equals("刚刚")) {
                sb.append("前");
            }
            return sb.toString();
        }

        /**
         *
         * @param i
         * @param view
         * @param viewGroup
         * @return
         */
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;

            if(view == null){
                view = mInflator.inflate(R.layout.listitem_notify, null);
                holder = new ViewHolder();
                holder.listnotify_button_imageView = (ImageView) view.findViewById(R.id.listnotify_button_imageView);
                holder.listnotify_button_textView = (TextView) view.findViewById(R.id.listnotify_button_textView);
                holder.listnotify_item_name = (TextView) view.findViewById(R.id.listnotify_item_name);
                holder.listnotify_projname = (TextView) view.findViewById(R.id.listnotify_projname);
                holder.listnotify_last_update_time = (TextView) view.findViewById(R.id.listnotify_last_update_time);
                holder.listnotify_last_update_username = (TextView) view.findViewById(R.id.listnotify_last_update_username);
                holder.listnotify_last_update_content = (TextView) view.findViewById(R.id.listnotify_last_update_content);
                //holder.listnotify_acctpt = (ImageView) view.findViewById(R.id.listnotify_acctpt);

                view.setTag(holder);
            }
            else{
                holder = (ViewHolder) view.getTag();
            }

            View textPart = view.findViewById(R.id.listnotify_text);
            textPart.setTag(R.id.item_object, visibleNotifyList.get(i));
            textPart.setOnClickListener(onClickListener);

            View acceptButton = view.findViewById(R.id.listnotify_button);
            acceptButton.setTag(R.id.item_object, visibleNotifyList.get(i));
            acceptButton.setOnClickListener(acceptClicked);


            //view.setOnClickListener(onClickListener);

            holder.listnotify_button_textView.setText(visibleNotifyType.get(visibleNotifyList.get(i).thingType).stringId);
            holder.listnotify_button_imageView.setImageResource(visibleNotifyType.get(visibleNotifyList.get(i).thingType).imageId);
            holder.listnotify_item_name.setText(visibleNotifyList.get(i).thing.getName());
            holder.listnotify_projname.setText(visibleNotifyList.get(i).hostName);
            holder.listnotify_last_update_time.setText(getStandardDate(visibleNotifyList.get(i).last));
            //holder.listnotify_last_update_time.setText(DateFunctions.RewriteDate(visibleNotifyList.get(i).last, "yyyy-M-d", "unknown"));



            if(visibleNotifyList.get(i).subNotifies.length > 0){
                holder.listnotify_last_update_username.setText(visibleNotifyList.get(i).subNotifies[0].creatorName);
                holder.listnotify_last_update_content.setText(subNotifyType.get(visibleNotifyList.get(i).subNotifies[0].subNotifyType));
            }

            return view;
        }

        @Override
        protected void updateData(){
            Notify[] notifies = NotifyList.this.notifications.getData().notifies;
            visibleNotifyList.clear();
            for(int i=0;i<notifies.length;i++){
                if(notifies[i].thingType != null && visibleNotifyType.containsKey(notifies[i].thingType)){
                    visibleNotifyList.add(notifies[i]);
                }
            }

//            visibleNotifyList = Arrays.asList(notifies);
//            Log.e("AAAAAAAAAAAAAAA",visibleNotifyList.toString());
//            Log.e("AAAAAAAAAAAAAAA","sizeof visibleNotifyList: " + visibleNotifyList.size());
//            for (int i=0; i<visibleNotifyList.size(); i++){
//                if( visibleNotifyList.get(i).thingType == null || !visibleNotifyType.containsKey(visibleNotifyList.get(i).thingType) ){
//                    visibleNotifyList.remove(i);
//                }
//            }
        }

    }
}
