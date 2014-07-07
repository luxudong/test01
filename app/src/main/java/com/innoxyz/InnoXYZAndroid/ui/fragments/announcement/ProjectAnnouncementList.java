package com.innoxyz.InnoXYZAndroid.ui.fragments.announcement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.data.remote.AddressURIs;
import com.innoxyz.InnoXYZAndroid.data.remote.StringRequestBuilder;
import com.innoxyz.InnoXYZAndroid.data.remote.response.JsonResponseHandler;
import com.innoxyz.InnoXYZAndroid.data.runtime.SimpleObservedData;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.announcement.Announcement;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.announcement.Announcements;
import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;
import com.innoxyz.InnoXYZAndroid.global.InnoXYZApp;
import com.innoxyz.InnoXYZAndroid.ui.activities.NewActivity;
import com.innoxyz.InnoXYZAndroid.ui.commands.ActivityCommand;
import com.innoxyz.InnoXYZAndroid.ui.commands.FragmentCommand;
import com.innoxyz.InnoXYZAndroid.ui.decorates.AutoloadListView;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.CommonListViewAdapter;
import com.innoxyz.InnoXYZAndroid.ui.utils.DateFunctions;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-22
 * Time: 下午3:33
 * To change this template use File | Settings | File Templates.
 */
public class ProjectAnnouncementList extends BaseFragment {

    SimpleObservedData<Announcements> announcements, loadedannouncements;
    private int projectId;
    private int projectCreatorId;

    @Override
    public void onResume(){
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.title_task_list);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        projectId = getArguments().getInt("projectId");
        projectCreatorId = getArguments().getInt("projectCreatorId");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.new_announcement_actions,menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("announce_add_select", "新建通知");
        if(item.getItemId() == R.id.action__newannouncement) {
            Bundle bundle = new Bundle();
            bundle.putAll(getArguments());
            bundle.putInt("projectId",projectId);
            if(projectCreatorId != InnoXYZApp.getApplication().getCurrentUserId()) {
                new AlertDialog.Builder(getActivity())
                        .setMessage(getString(R.string.announcement_new_warning))
                        .setPositiveButton(getString(R.string.OK), null)
                        .show();
            }else {
                new ActivityCommand(NewActivity.class,AnnouncementNew.class,ProjectAnnouncementList.this.getActivity(),bundle,null).Execute();
            }
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setHasOptionsMenu(true);
        announcements = new SimpleObservedData<Announcements>(new Announcements());
        loadedannouncements = new SimpleObservedData<Announcements>(new Announcements());
        ListView ret = new ListView(getActivity());
        ret.setDivider(null);
        final AutoloadListView autolv = new AutoloadListView(ret);
        loadedannouncements.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {
                announcements.getData().append(loadedannouncements.getData());
                announcements.notifyObservers();
                autolv.endLoad(announcements.getData().hasMore());
            }
        });
        autolv.setOnDataLoading(new AutoloadListView.OnDataLoading() {

            @Override
            public void LoadData(int page) {
                autolv.beginLoad();
                new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.LIST_ANNOUNCEMENT_IN_PROJECT)
                        .addParameter("thingId", "" + getArguments().getInt("projectId"))
                        .addParameter("page", "" + page)
                        .setOnResponseListener(new JsonResponseHandler(loadedannouncements, Announcements.class, Arrays.asList("pager")))
                        .request();
            }
        });
        ret.setAdapter(new ListViewAdapter(getActivity()));
        autolv.TriggerLoading();
        return ret;

    }

    private class ListViewAdapter extends CommonListViewAdapter {

        public ListViewAdapter(Context context) {
            super(context);
            announcements.registerObserver(this);
        }

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String id = (String) view.getTag(R.id.item_id);
                Announcement announcement = (Announcement) view.getTag(R.id.item_object);
                Bundle bundle = new Bundle();
                bundle.putAll(getArguments());
                bundle.putString("content", announcement.content);
                new FragmentCommand(ProjectAnnouncementList.class, AnnouncementHome.class, ProjectAnnouncementList.this.getActivity(), bundle, null).Execute();

            }
        };

        @Override
        protected void updateData() {
            Announcement[] announcements = ProjectAnnouncementList.this.announcements.getData().announcements;
            views = new View[announcements.length];
            for(int i=0; i<views.length; i++) {
                views[i] = mInflator.inflate(R.layout.listitem_announcement, null);
                views[i].setTag(R.id.item_object, announcements[i]);
                ((TextView)views[i].findViewById(R.id.announcement_item_name)).setText(announcements[i].title);
                ((TextView)views[i].findViewById(R.id.announcement_item_lastupdate)).setText(DateFunctions.RewriteDate(announcements[i].createTime, "yyyy-M-d", "unknown"));
                views[i].setOnClickListener(onClickListener);
            }
        }
    }
}
