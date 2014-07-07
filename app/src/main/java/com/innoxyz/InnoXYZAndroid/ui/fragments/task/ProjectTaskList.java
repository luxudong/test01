package com.innoxyz.InnoXYZAndroid.ui.fragments.task;

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
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.User;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.task.Task;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.task.Tasks;
import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;
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
 * Time: 下午1:47
 * To change this template use File | Settings | File Templates.
 */
public class ProjectTaskList extends BaseFragment {

    SimpleObservedData<Tasks> tasks, loadedTasks;
    private int projectId;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        projectId = getArguments().getInt("projectId");
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.title_task_list);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.new_task_actions, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action__newtask) {
            Bundle bundle = new Bundle();
            bundle.putAll(getArguments());
            bundle.putInt("projectId", getArguments().getInt("projectId"));
            new ActivityCommand(NewActivity.class,TaskNew.class,ProjectTaskList.this.getActivity(),bundle,null).Execute();
            //new FragmentCommand(ProjectTaskList.class, TaskNew.class, ProjectTaskList.this.getActivity(), bundle, null).Execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setHasOptionsMenu(true);
        tasks = new SimpleObservedData<Tasks>(new Tasks());
        loadedTasks = new SimpleObservedData(new Tasks());
        ListView ret = new ListView(getActivity());
        ret.setDivider(null);
        final AutoloadListView autolv = new AutoloadListView(ret);
        loadedTasks.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {
                tasks.getData().append(loadedTasks.getData());
                tasks.notifyObservers();
                autolv.endLoad(tasks.getData().hasMore());
            }
        });
        autolv.setOnDataLoading(new AutoloadListView.OnDataLoading() {

            @Override
            public void LoadData(int page) {
                autolv.beginLoad();
                new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.LIST_TASK_IN_LPROJECT)
                        .addParameter("hostId", "" + getArguments().getInt("projectId"))
                        .addParameter("page", "" + page)
                        .setOnResponseListener(new JsonResponseHandler(loadedTasks, Tasks.class, Arrays.asList("pager")))
                        .request();
            }
        });
        ret.setAdapter(new TaskListViewAdapter(getActivity()));
        autolv.TriggerLoading();
        return ret;
    }

    private class TaskListViewAdapter extends CommonListViewAdapter implements IDataObserver {
        private TaskListViewAdapter(Context context) {
            super(context);
            tasks.registerObserver(this);
        }

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Task task = (Task) view.getTag(R.id.item_object);
                Bundle bundle = new Bundle();
                bundle.putAll(getArguments());
                bundle.putString("taskId", task.id);
                bundle.putString("taskName", task.name);
                bundle.putInt("creatorId", task.creatorId);
                bundle.putString("creatorName", task.creatorName);
                String assignees = "";
                for(User u : task.assignees) {
                    assignees += u.realName + " ";
                }
                bundle.putString("assignees", assignees);
                bundle.putString("priority", task.priority.toString());
                bundle.putString("description", task.description);
                bundle.putString("deadline", task.deadline);
                bundle.putString("state", task.state.toString());
                new FragmentCommand(ProjectTaskList.class, TaskHome.class, ProjectTaskList.this.getActivity(), bundle, null).Execute();

            }
        };

        @Override
        protected void updateData() {
            Task[] tasks = ProjectTaskList.this.tasks.getData().tasks;
            views = new View[tasks.length];
            for(int i=0; i<views.length; i++) {
                views[i] = mInflator.inflate(R.layout.listitem_task, null);
                views[i].setTag(R.id.item_object, tasks[i]);
                ((TextView)views[i].findViewById(R.id.task_item_name)).setText(tasks[i].name);
                String assignees = "";
                for(User u : tasks[i].assignees) {
                    assignees += u.realName + " ";
                }
                ((TextView)views[i].findViewById(R.id.task_item_assignee)).setText( getResources().getString(R.string.template_taskitem_assignees).replace("%assignees%", assignees) );
                ((TextView)views[i].findViewById(R.id.task_item_duedate)).setText( getResources().getString(R.string.template_taskitem_duedate).replace("%duedate%", DateFunctions.RewriteDate(tasks[i].deadline, "yyyy-M-d", "unknown")) );
                ((TextView)views[i].findViewById(R.id.task_item_status)).setText(tasks[i].state.toString());
                views[i].setOnClickListener(onClickListener);
            }
        }
    }
}
