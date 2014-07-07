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
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.message.Message;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.message.MessageSent;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.message.Messages;
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
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-16
 * Time: 上午10:38
 * To change this template use File | Settings | File Templates.
 */
public class MessageList extends BaseFragment {

    SimpleObservedData<Messages> messages, loadedMessages;

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

        fetchData =  true;

        messages = new SimpleObservedData<Messages>(new Messages());
        loadedMessages = new SimpleObservedData<Messages>(new Messages());

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
                RequestInfo toSet  = null;
                if(flag == true) {
                    toSet = AddressURIs.LIST_MESSAGE;
                }else {
                    toSet = AddressURIs.LIST_SENT_MESSAGE;
                }

                new StringRequestBuilder(getActivity()).setRequestInfo(toSet)
                        .addParameter("page", "" + page)
                        .setOnResponseListener(new JsonResponseHandler(loadedMessages, Messages.class, Arrays.asList("pager")))
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
                messages.setData(new Messages(), false);
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
        getActivity().getActionBar().setTitle(R.string.title_message_inbox);

//        if(flag == false) {
//            getActivity().getActionBar().setTitle(R.string.title_message_outbox);
//        }else {
//            getActivity().getActionBar().setTitle(R.string.title_message_inbox);
//        }

    }




    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.message_fragment_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_add){
            new ActivityCommand(NewActivity.class, MessageNew.class, MessageList.this.getActivity(), null, null).Execute();
        }else if(id == R.id.action_sent) {
            flag = !flag;
            //Log.e("outbox","actionsent pressed");
            new FragmentCommand(MessageList.class,MessageOutboxList.class,MessageList.this.getActivity(),null,null).Execute();

//            messages.setData(new Messages(),false);
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
                Message msg = (Message) view.getTag(R.id.item_object);
                MessageSent msgSent = null;

                //bundle.putAll(getArguments());

                    bundle.putString("subject", msg.subject);
                    bundle.putInt("senderId", msg.senderId);
                    bundle.putString("senderName", msg.senderName);
                    bundle.putString("sendTime", msg.sendTime);
                    bundle.putString("content", msg.content);

                //new FragmentCommand(MessageList.class, MessageHome.class, MessageList.this.getActivity(), bundle, null).Execute();
                new ActivityCommand(DetailActivity.class, MessageHome.class, MessageList.this.getActivity(), bundle, null).Execute();

            }
        };

        @Override
        protected void updateData() {

            Message[] messages = MessageList.this.messages.getData().messages;
            views = new View[messages.length];
            for(int i=0; i<views.length; i++) {
                views[i] = mInflator.inflate(R.layout.listitem_message, null);
                views[i].setTag(R.id.item_object, messages[i]);
                ((TextView)views[i].findViewById(R.id.message_item_usernameTitle)).setText("发件人：");
                ((TextView)views[i].findViewById(R.id.message_item_subject)).setText(messages[i].subject);
                ((TextView)views[i].findViewById(R.id.message_item_username)).setText(messages[i].senderName);
                ((TextView)views[i].findViewById(R.id.message_item_date)).setText(DateFunctions.RewriteDate(messages[i].sendTime, "yyyy-M-d", "unknown"));
                views[i].setOnClickListener(onClickListener);
            }

        }
    }
}
