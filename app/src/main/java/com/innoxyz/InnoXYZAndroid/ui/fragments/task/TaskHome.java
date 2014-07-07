package com.innoxyz.InnoXYZAndroid.ui.fragments.task;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.data.remote.AddressURIs;
import com.innoxyz.InnoXYZAndroid.data.remote.StringRequestBuilder;
import com.innoxyz.InnoXYZAndroid.data.remote.response.JsonResponseHandler;
import com.innoxyz.InnoXYZAndroid.data.runtime.SimpleObservedData;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.common.Attachment;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.common.StateMap;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.task.Pushstate;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.task.Pushstates;
import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;
import com.innoxyz.InnoXYZAndroid.global.InnoXYZApp;
import com.innoxyz.InnoXYZAndroid.ui.activities.NewActivity;
import com.innoxyz.InnoXYZAndroid.ui.commands.ActivityCommand;
import com.innoxyz.InnoXYZAndroid.ui.commands.FragmentCommand;
import com.innoxyz.InnoXYZAndroid.ui.decorates.AutoloadListView;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.CommonListViewAdapter;
import com.innoxyz.InnoXYZAndroid.ui.fragments.project.ProjectHome;
import com.innoxyz.InnoXYZAndroid.ui.utils.DateFunctions;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-15
 * Time: 下午1:29
 * To change this template use File | Settings | File Templates.
 */
public class TaskHome extends BaseFragment {

    SimpleObservedData<Pushstates> pushstates, loadedPushstate;
    AutoloadListView autolv;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);    //To change body of overridden methods use File | Settings | File Templates.
        pushstates = new SimpleObservedData<Pushstates>(new Pushstates());
        loadedPushstate = new SimpleObservedData<Pushstates>(new Pushstates());

        //getActivity().getActionBar().setTitle(R.string.title_task_home);
    }

    private void setupHomeInfo(View view, Bundle args) {
        ((TextView)view.findViewById(R.id.task_home_creatorName)).setText(args.getString("creatorName"));
        ((TextView)view.findViewById(R.id.task_home_assignees)).setText(args.getString("assignees"));
        ((TextView)view.findViewById(R.id.task_home_desc)).setText(args.getString("description"));
        ((TextView)view.findViewById(R.id.task_home_deadline)).setText(DateFunctions.RewriteDate(args.getString("deadline"), "yyyy-M-dd", "unknown"));
        ((TextView)view.findViewById(R.id.task_home_priority)).setText(args.getString("priority"));
        ((TextView)view.findViewById(R.id.task_home_state)).setText(args.getString("state"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View ret = inflater.inflate(R.layout.fragment_task_home, container, false);
        setupHomeInfo(ret, getArguments());
        ((TextView)ret.findViewById(R.id.taskhome_projectname)).setText( getResources().getString(R.string.template_topichome_projectname).replace("%name%", getArguments().getString("projectName")) );


        ret.findViewById(R.id.task_home_reply).setOnClickListener(onReplyClicked);
        ret.findViewById(R.id.task_home_goto_project).setOnClickListener(onGoToProjectClicked);


        ListView listView = (ListView) ret.findViewById(R.id.taskhome_itemlist);
        autolv = new AutoloadListView(listView);
        loadedPushstate.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {
                pushstates.getData().append(loadedPushstate.getData());
                pushstates.notifyObservers();
                autolv.endLoad(pushstates.getData().hasMore());
            }

        });
        autolv.setOnDataLoading(new AutoloadListView.OnDataLoading() {
            @Override
            public void LoadData(int page) {
                autolv.beginLoad();
                new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.LIST_PUSHSTATE)
                        .addParameter("taskId", getArguments().getString("taskId"))
                        .addParameter("page", "" + page)
                        .setOnResponseListener(new JsonResponseHandler(loadedPushstate, Pushstates.class, Arrays.asList("pager")))
                        .request();
            }
        });
        listView.setAdapter(new ListViewAdapter(getActivity()));
        //autolv.TriggerLoading();
        //autolv.reLoading();
        return ret;
    }

    @Override
    public void onResume (){
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.title_task_home);
        //clean cache before refresh
        pushstates.setData(new Pushstates(), false);
        autolv.reLoading();
    }

    private View.OnClickListener onReplyClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new ActivityCommand(NewActivity.class, TaskReply.class, TaskHome.this.getActivity(), getArguments(), null).Execute();
        }
    };

    protected View.OnClickListener onGoToProjectClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new FragmentCommand(TaskHome.class, ProjectHome.class, TaskHome.this.getActivity(), getArguments(), null).Execute();
        }
    };

    static class ViewHolder{
        TextView taskhome_item_username,taskhome_item_date,taskhome_item_content,taskhome_item_attach,taskhome_item_state,taskhome_item_stateTitle;
        NetworkImageView taskhome_item_avatar;
    }

    private class ListViewAdapter extends CommonListViewAdapter {

        public ListViewAdapter(Context context) {
            super(context);
            pushstates.registerObserver(this);
        }

        @Override
        public int getCount() {
            if(pushstates == null || pushstates.getData() == null || pushstates.getData().pushstates == null )
                return 0;
            else
                return pushstates.getData().pushstates.length;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            ViewHolder holder;
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_taskhome_pushstate, null);
                holder = new ViewHolder();
                holder.taskhome_item_username = (TextView)view.findViewById(R.id.taskhome_item_username);
                holder.taskhome_item_date = (TextView)view.findViewById(R.id.taskhome_item_date);
                holder.taskhome_item_content = (TextView)view.findViewById(R.id.taskhome_item_content);
                holder.taskhome_item_attach = (TextView)view.findViewById(R.id.taskhome_item_attach);
                holder.taskhome_item_state = (TextView)view.findViewById(R.id.taskhome_item_state);
                holder.taskhome_item_stateTitle = (TextView)view.findViewById(R.id.taskhome_item_stateTitle);
                holder.taskhome_item_avatar = (NetworkImageView)view.findViewById(R.id.taskhome_item_avatar);

                view.setTag(holder);
            }
            else{
                holder = (ViewHolder) view.getTag();
            }

            String name, content, updateDate, attachment = "";
            int creatorId;
            Pushstate ps = pushstates.getData().pushstates[i];
            creatorId = ps.creatorId;
            name = ps.creatorName;
            content = ps.comment;
            updateDate = ps.createTime;
            if ( ps.attachments != null ) {
                for(Attachment attach : ps.attachments) {
                    attachment += attach.name + " ";
                }
            }
            updateDate = DateFunctions.RewriteDate(updateDate, "yyyy-M-d", "unknown");


            holder.taskhome_item_username.setText(getResources().getString(R.string.template_topichome_username).replace("%name%", name));
            holder.taskhome_item_content.setText(content);
            holder.taskhome_item_attach.setText(getResources().getString(R.string.template_topichome_attachments).replace("%attach%", attachment));
            holder.taskhome_item_date.setText(updateDate);

            if ( ps.modifications != null && ps.modifications.stateChange != null ) {
                holder.taskhome_item_state.setText(StateMap.getText(ps.modifications.stateChange.newValue));
                holder.taskhome_item_state.setVisibility(View.VISIBLE);
                holder.taskhome_item_stateTitle.setVisibility(View.VISIBLE);
            } else {
                holder.taskhome_item_state.setVisibility(View.GONE);
                holder.taskhome_item_stateTitle.setVisibility(View.GONE);
            }
            holder.taskhome_item_avatar.setImageUrl(AddressURIs.getUserAvatarURL(creatorId), InnoXYZApp.getApplication().getImageLoader());

            return view;
        }



        @Override
        protected void updateData() {
//            views = new View[ pushstates.getData().pushstates.length ];
//            for(int i=0; i<views.length; i++) {
//                views[i] = mInflator.inflate(R.layout.listitem_taskhome_pushstate, null);
//                String name, content, updateDate, attachment = "";
//                int creatorId;
//                Pushstate ps = pushstates.getData().pushstates[i];
//                creatorId = ps.creatorId;
//                name = ps.creatorName;
//                content = ps.comment;
//                updateDate = ps.createTime;
//                if ( ps.attachments != null ) {
//                    for(Attachment attach : ps.attachments) {
//                        attachment += attach.name + " ";
//                    }
//                }
//                updateDate = DateFunctions.RewriteDate(updateDate, "yyyy-M-d", "unknown");
//                ((TextView)views[i].findViewById(R.id.taskhome_item_username)).setText(getResources().getString(R.string.template_topichome_username).replace("%name%", name));
//                ((TextView)views[i].findViewById(R.id.taskhome_item_content)).setText(content);
//                ((TextView)views[i].findViewById(R.id.taskhome_item_attach)).setText(getResources().getString(R.string.template_topichome_attachments).replace("%attach%", attachment));
//                ((TextView)views[i].findViewById(R.id.taskhome_item_date)).setText(updateDate);
//                if ( ps.modifications != null && ps.modifications.stateChange != null ) {
//                    ((TextView)views[i].findViewById(R.id.taskhome_item_state)).setText(StateMap.getText(ps.modifications.stateChange.newValue));
//                } else {
//                    views[i].findViewById(R.id.taskhome_item_state).setVisibility(View.GONE);
//                    views[i].findViewById(R.id.taskhome_item_stateTitle).setVisibility(View.GONE);
//                }
//                //new ProfileImage((ImageView) views[i].findViewById(R.id.taskhome_item_avatar),creatorId).showProfileImage();
//                ((NetworkImageView) views[i].findViewById(R.id.taskhome_item_avatar)).setImageUrl(AddressURIs.getUserAvatarURL(creatorId), InnoXYZApp.getApplication().getImageLoader());
//
//            }
        }
    }
}
