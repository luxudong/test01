package com.innoxyz.InnoXYZAndroid.ui.fragments.message;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.data.remote.AddressURIs;
import com.innoxyz.InnoXYZAndroid.data.remote.RequestInfo;
import com.innoxyz.InnoXYZAndroid.data.remote.StringRequestBuilder;
import com.innoxyz.InnoXYZAndroid.data.remote.response.JsonResponseHandler;
import com.innoxyz.InnoXYZAndroid.data.runtime.SimpleObservedData;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.message.MessageSent;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.message.MessagesOutbox;
import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;
import com.innoxyz.InnoXYZAndroid.ui.activities.DetailActivity;
import com.innoxyz.InnoXYZAndroid.ui.activities.NewActivity;
import com.innoxyz.InnoXYZAndroid.ui.commands.ActivityCommand;
import com.innoxyz.InnoXYZAndroid.ui.commands.FragmentCommand;
import com.innoxyz.InnoXYZAndroid.ui.customviews.PullToRefreshListView;
import com.innoxyz.InnoXYZAndroid.ui.decorates.AutoloadListView;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.CommonListViewAdapter;
import com.innoxyz.InnoXYZAndroid.ui.utils.DateFunctions;

import java.util.Arrays;

/**
 * Created by dell on 2014/5/23.
 */
public class MessageOutboxList extends BaseFragment {
    SimpleObservedData<MessagesOutbox> messages, loadedMessages;

    PullToRefreshListView pullToRefreshListView;
    AutoloadListView autoloadListView;
    boolean fetchData;


    /**
     * 用来标示收件箱和发件箱，true代表收件箱，false代表发件箱。默认是true
     */
    boolean flag = true;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        fetchData =  true;

        messages = new SimpleObservedData<MessagesOutbox>(new MessagesOutbox());
        loadedMessages = new SimpleObservedData<MessagesOutbox>(new MessagesOutbox());

        pullToRefreshListView = new PullToRefreshListView(getActivity());
        pullToRefreshListView.setDivider(null);
        autoloadListView = new AutoloadListView(pullToRefreshListView);
        loadedMessages.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {
                messages.getData().append(loadedMessages.getData());
                messages.notifyObservers();
                autoloadListView.endLoad(messages.getData().hasMore());
            }
        });
        autoloadListView.setOnDataLoading(new AutoloadListView.OnDataLoading() {

            @Override
            public void LoadData(int page) {
                autoloadListView.beginLoad();
                RequestInfo toSet  = AddressURIs.LIST_SENT_MESSAGE;

                new StringRequestBuilder(getActivity()).setRequestInfo(toSet)
                        .addParameter("page", "" + page)
                        .setOnResponseListener(new JsonResponseHandler(loadedMessages, MessagesOutbox.class, Arrays.asList("pager")))
                        .request();


            }
        });
        messages.registerObserver(new IDataObserver() {
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
                messages.setData(new MessagesOutbox(), false);
                autoloadListView.reLoading();
            }
        });

        pullToRefreshListView.setAdapter(new ListViewAdapter(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.

        setHasOptionsMenu(true);

        if(fetchData){
            autoloadListView.TriggerLoading();
            fetchData = false;
        }
        return pullToRefreshListView;

    }
    //增加的个人设置部分//【注意这里加上的话就会报错，私信打开不了】
    public void onResume(){
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.title_message_outbox);
//        if(flag == false) {
//            getActivity().getActionBar().setTitle(R.string.title_message_outbox);
//        }else {
//            getActivity().getActionBar().setTitle(R.string.title_message_inbox);
//        }

    }




    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){

        inflater.inflate(R.menu.message_outbox_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_add){
            new ActivityCommand(NewActivity.class, MessageNew.class, MessageOutboxList.this.getActivity(), null, null).Execute();
        }else if(id == R.id.action_to_inbox) {
            flag = !flag;

            new FragmentCommand(MessageOutboxList.class,MessageList.class,MessageOutboxList.this.getActivity(),null,null).Execute();
//            messages.setData(new MessagesOutbox(),false);
//            autoloadListView.reLoading();

//            if(flag) {
//                getActivity().getActionBar().setTitle(R.string.title_message_inbox);
//                item.setIcon(R.drawable.action_message_outbox);
//            }
//            else{
//                getActivity().getActionBar().setTitle(R.string.title_message_outbox);
//                item.setIcon(R.drawable.action_message_inbox);
//            }
        }




        //consume it
        return true;
    }

    private class ListViewAdapter extends CommonListViewAdapter {

        public ListViewAdapter(Context context) {
            super(context);
            messages.registerObserver(this);
        }

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String id = (String) view.getTag(R.id.item_id);
                Bundle bundle = new Bundle();
                MessageSent msg = (MessageSent) view.getTag(R.id.item_object);


                //bundle.putAll(getArguments());

                bundle.putString("subject", msg.subject);
                bundle.putInt("senderId", msg.senderId);
                bundle.putString("senderName", msg.senderName);
                bundle.putString("sendTime", msg.sendTime);
                bundle.putString("content", msg.content);

                //new FragmentCommand(MessageList.class, MessageHome.class, MessageList.this.getActivity(), bundle, null).Execute();
                new ActivityCommand(DetailActivity.class, MessageHome.class, MessageOutboxList.this.getActivity(), bundle, null).Execute();

            }
        };

        @Override
        protected void updateData() {
            MessageSent[] messages = MessageOutboxList.this.messages.getData().msgOutboxArr;
            views = new View[messages.length];
            for(int i=0; i<views.length; i++) {
                views[i] = mInflator.inflate(R.layout.listitem_message, null);
                views[i].setTag(R.id.item_object, messages[i]);
                ((TextView)views[i].findViewById(R.id.message_item_usernameTitle)).setText("收件人：");
                ((TextView)views[i].findViewById(R.id.message_item_subject)).setText(messages[i].subject);
                String recivers = "";
                for(int j = 0;j < messages[i].recivers.length; j ++) {
                    recivers += messages[i].recivers[j].realName + "  ";
                }
                ((TextView)views[i].findViewById(R.id.message_item_username)).setText(recivers);
                ((TextView)views[i].findViewById(R.id.message_item_date)).setText(DateFunctions.RewriteDate(messages[i].sendTime, "yyyy-M-d", "unknown"));
                views[i].setOnClickListener(onClickListener);
            }

        }
    }
}
