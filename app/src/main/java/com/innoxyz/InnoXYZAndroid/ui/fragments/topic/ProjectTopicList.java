package com.innoxyz.InnoXYZAndroid.ui.fragments.topic;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.topic.Topic;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.topic.Topics;
import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;
import com.innoxyz.InnoXYZAndroid.ui.activities.NewActivity;
import com.innoxyz.InnoXYZAndroid.ui.commands.ActivityCommand;
import com.innoxyz.InnoXYZAndroid.ui.commands.FragmentCommand;
import com.innoxyz.InnoXYZAndroid.ui.decorates.AutoloadListView;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.CommonListViewAdapter;
import com.innoxyz.InnoXYZAndroid.ui.utils.DateFunctions;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-22
 * Time: 下午3:33
 * To change this template use File | Settings | File Templates.
 */
public class ProjectTopicList extends BaseFragment {

    SimpleObservedData<Topics> topics, loadedTopics;
    private int projectId;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        projectId = getArguments().getInt("projectId");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.new_topic_actions,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action__newtopic) {
            Bundle bundle = new Bundle();

            bundle.putAll(getArguments());
            bundle.putInt("projectId",projectId);
            new ActivityCommand(NewActivity.class,TopicNew.class,ProjectTopicList.this.getActivity(),bundle,null).Execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.title_task_list);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setHasOptionsMenu(true);
        topics = new SimpleObservedData<Topics>(new Topics());
        loadedTopics = new SimpleObservedData<Topics>(new Topics());
        ListView ret = new ListView(getActivity());
        ret.setDivider(null);
        final AutoloadListView autolv = new AutoloadListView(ret);
        loadedTopics.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {
                loadedTopics.getData().Normalize();
                topics.getData().topicList.append(loadedTopics.getData().topicList);
                topics.notifyObservers();
                autolv.endLoad(topics.getData().topicList.hasMore());
            }
        });
        autolv.setOnDataLoading(new AutoloadListView.OnDataLoading() {

            @Override
            public void LoadData(int page) {
                autolv.beginLoad();
                new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.LIST_TOPIC_IN_LPROJECT)
                        .addParameter("lprojectId", "" + getArguments().getInt("projectId"))
                        .addParameter("page", "" + page)
                        .setOnResponseListener(new JsonResponseHandler(loadedTopics, Topics.class, null))
                        .request();
            }
        });
        ret.setAdapter(new TopicListViewAdapter(getActivity()));
        autolv.TriggerLoading();
        return ret;

    }

    private class TopicListViewAdapter extends CommonListViewAdapter {

        public TopicListViewAdapter(Context context) {
            super(context);
            topics.registerObserver(this);
        }

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = (String) view.getTag(R.id.item_id);
                Bundle bundle = new Bundle();
                bundle.putAll(getArguments());
                bundle.putString("topicId", id);
                new FragmentCommand(ProjectTopicList.class, TopicHome.class, ProjectTopicList.this.getActivity(), bundle, null).Execute();

            }
        };

        @Override
        protected void updateData() {
            Topic[] topics = ProjectTopicList.this.topics.getData().topicList.topics;
            views = new View[topics.length];
            for(int i=0; i<views.length; i++) {
                views[i] = mInflator.inflate(R.layout.listitem_topic, null);
                views[i].setTag(R.id.item_id, topics[i].id);
                ((TextView)views[i].findViewById(R.id.topic_item_name)).setText(topics[i].subject);
                String lastReply = "";
                if ( topics[i].lastPostId == null ) {
                    lastReply = getResources().getString(R.string.hint_topicitem_noreply);
                } else {
                    lastReply = getResources().getString(R.string.template_topicitem_updated)
                            .replace("%user%", topics[i].lastPost.creatorName)
                            .replace("%date%",DateFunctions.RewriteDate(topics[i].lastPost.createTime, "yyyy-M-d", "unknown"));
                }
                ((TextView)views[i].findViewById(R.id.topic_item_lastupdate)).setText(lastReply);
                views[i].setOnClickListener(onClickListener);
            }
        }
    }
}
