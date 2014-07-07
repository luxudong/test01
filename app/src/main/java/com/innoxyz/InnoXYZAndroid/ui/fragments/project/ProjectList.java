package com.innoxyz.InnoXYZAndroid.ui.fragments.project;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.data.remote.AddressURIs;
import com.innoxyz.InnoXYZAndroid.data.remote.StringRequestBuilder;
import com.innoxyz.InnoXYZAndroid.data.remote.interfaces.OnPostListener;
import com.innoxyz.InnoXYZAndroid.data.remote.response.JsonResponseHandler;
import com.innoxyz.InnoXYZAndroid.data.remote.response.PostResponseHandler;
import com.innoxyz.InnoXYZAndroid.data.runtime.SimpleObservedData;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.Projects;
import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;
import com.innoxyz.InnoXYZAndroid.ui.activities.DetailActivity;
import com.innoxyz.InnoXYZAndroid.ui.commands.ActivityCommand;
import com.innoxyz.InnoXYZAndroid.ui.customviews.PullToRefreshListView;
import com.innoxyz.InnoXYZAndroid.ui.decorates.AutoloadListView;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.CommonListViewAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-1
 * Time: 上午9:51
 * To change this template use File | Settings | File Templates.
 */
public class ProjectList extends BaseFragment {

    SimpleObservedData<Projects> projects, loadProjects;
    PullToRefreshListView pullToRefreshListView;
    AutoloadListView autoloadListView;
    List<Projects.Project> visibleProjectList;
    List<Projects.Project> searchedProjectList;
    List<Projects.Project> allProjectList;
    SearchView searchView;
    boolean fetchData;
    boolean showAll = true;
    String searchQuery = "";
    Menu currentMenu;
    ProjectListViewAdapter projectListViewAdapter;
    View retView;

    static class ViewHolder{
        TextView project_item_name,project_item_date;
        ToggleButton project_item_liked;
    }

    class ProjectListViewAdapter extends CommonListViewAdapter {

        public ProjectListViewAdapter(Context context) {
            super(context);
            projects.registerObserver(this);
        }

        protected View.OnClickListener clickProject = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Projects.Project project = (Projects.Project)view.getTag(R.id.item_object);
                Bundle bundle = new Bundle();

                bundle.putInt("projectId", project.id);
                bundle.putString("projectName", project.name);

                bundle.putString("entryId",project.baseDirId);
                //new FragmentCommand(ProjectList.class, ProjectHome.class, ProjectList.this.getActivity(), bundle, null).Execute();
                new ActivityCommand(DetailActivity.class, ProjectHome.class, ProjectList.this.getActivity(), bundle, null).Execute();

            }
        };

        @Override
        public int getCount() {
            return visibleProjectList.size();
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup){
            ViewHolder holder;

            if(view == null){
                view = mInflator.inflate(R.layout.listitem_project, null);
                holder = new ViewHolder();
                holder.project_item_name = (TextView)view.findViewById(R.id.project_item_name);
                //holder.project_item_date = (TextView)view.findViewById(R.id.project_item_date);
                holder.project_item_liked = (ToggleButton)view.findViewById(R.id.project_item_liked);

                view.setTag(holder);
            }
            else{
                holder = (ViewHolder) view.getTag();
            }

            view.setTag(R.id.item_object, visibleProjectList.get(i));
            view.setOnClickListener(clickProject);

            holder.project_item_name.setText(visibleProjectList.get(i).name);
            //holder.project_item_date.setText(getResources().getString(R.string.template_projectitem_updated).replace("%time%", DateFunctions.RewriteDate(visibleProjectList.get(i).createTime, "yyyy-M-d", "unknown")));

            if(showAll){
                holder.project_item_liked.setChecked(visibleProjectList.get(i).liked);
            }
            else{
                holder.project_item_liked.setChecked(true);
            }

            holder.project_item_liked.setOnClickListener(onToggleClickListener);
            holder.project_item_liked.setTag(R.id.item_object, visibleProjectList.get(i));

            return view;

        }

        @Override
        protected void updateData() {
            Projects projects = ProjectList.this.projects.getData();
            allProjectList.clear();
            searchedProjectList.clear();
            if( projects != null && projects.projects != null){
                for(int i=0; i<projects.projects.length; i++){
                    allProjectList.add(projects.projects[i]);
                    if(searchQuery==null || searchQuery.length()==0 || projects.projects[i].name.contains(searchQuery)){
                        searchedProjectList.add(projects.projects[i]);
                    }
                }
            }
            if(searchQuery==null || searchQuery.length()==0){
                visibleProjectList = allProjectList;
            }
            else{
                visibleProjectList = searchedProjectList;
            }
//            if ( projects==null || projects.data==null ) {
//                views = new View[0];
//            } else {
//                ArrayList<View> searchedViews = new ArrayList<View>();
//                View[] tmpViews = new View[projects.data.length];
//                for(int i=0; i<tmpViews.length; i++) {
////                    tmpViews[i] = mInflator.inflate(R.layout.listitem_project, null);
////                    tmpViews[i].setTag(R.id.item_object, projects.data[i]);
////
////                    ((TextView)tmpViews[i].findViewById(R.id.project_item_name)).setText(projects.data[i].name);
////                    ((TextView)tmpViews[i].findViewById(R.id.project_item_date)).setText(
////                            getResources().getString(R.string.template_projectitem_updated).replace("%time%", DateFunctions.RewriteDate(projects.data[i].createTime, "yyyy-M-d", "unknown"))
////                    );
////                    ToggleButton projectItemLiked = ((ToggleButton)tmpViews[i].findViewById(R.id.project_item_liked));
////                    projectItemLiked.setChecked(projects.data[i].liked);
////                    projectItemLiked.setOnClickListener(onToggleClickListener);
////                    projectItemLiked.setTag(R.id.item_object, projects.data[i]);
////
////                    tmpViews[i].setOnClickListener(clickProject);
//
//                    if(searchQuery==null || searchQuery.length()==0 || projects.data[i].name.contains(searchQuery)){
//                        searchedViews.add(tmpViews[i]);
//                    }
//                }
//                views = (View[])searchedViews.toArray();
//            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        projects = new SimpleObservedData<Projects>(new Projects());
        loadProjects = new SimpleObservedData<Projects>(new Projects());
        visibleProjectList = new ArrayList<Projects.Project>();
        searchedProjectList = new ArrayList<Projects.Project>();
        allProjectList = new ArrayList<Projects.Project>();

        fetchData = true;

        //Log.e("", "ProjectList onCreate called");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setHasOptionsMenu(true);

        if(fetchData){

            retView = inflater.inflate(R.layout.fragment_projectlist_home, container, false);

            pullToRefreshListView = (PullToRefreshListView)retView.findViewById(R.id.projectlist_itemlist);
            autoloadListView = new AutoloadListView(pullToRefreshListView);

            loadProjects.registerObserver(new IDataObserver() {
                @Override
                public void update(Object o) {
                    projects.getData().append(loadProjects.getData());
                    projects.notifyObservers();
                    autoloadListView.endLoad(projects.getData().hasMore());
                }
            });
            autoloadListView.setOnDataLoading(new AutoloadListView.OnDataLoading() {
                @Override
                public void LoadData(int page) {
                    autoloadListView.beginLoad();
                    fetchData(page, showAll);
                }
            });
            projects.registerObserver(new IDataObserver() {
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

            projectListViewAdapter = new ProjectListViewAdapter(getActivity());
            pullToRefreshListView.setAdapter(projectListViewAdapter);
            pullToRefreshListView.setDivider(null);

            pullToRefreshListView.setLockScrollWhileRefreshing(false);
            pullToRefreshListView.setShowLastUpdatedText(true);
            pullToRefreshListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    //clean cache before refresh
                    projects.setData(new Projects(), false);
                    autoloadListView.reLoading();
                }
            });

            searchView = (SearchView)retView.findViewById(R.id.projectlist_search_view);
            searchView.setSubmitButtonEnabled(true);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    Log.e("AAAAAAAAAA", "search string :" + s);
                    searchQuery = s;
                    //update the adapter to refresh the listview
                    projectListViewAdapter.update(null);
                    searchView.clearFocus();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    //clean
                    if(s==null || s.length()==0){
                        searchQuery = s;
                        projectListViewAdapter.update(null);
                        searchView.clearFocus();
                    }
                    return false;
                }
            });


//            fetchData(showAll);
            autoloadListView.TriggerLoading();
            fetchData = false;
        }
        return retView;
    }

    @Override
    public void onResume(){
        super.onResume();
        searchView.clearFocus();
        if(showAll) {
            getActivity().getActionBar().setTitle(R.string.title_project_list_all);
//            if(currentMenu != null){
//                currentMenu.findItem(R.id.action_projectlist_allorstar).setIcon(R.drawable.all);
//            }
        }
        else{
            getActivity().getActionBar().setTitle(R.string.title_project_list_star);
//            if(currentMenu != null){
//                currentMenu.findItem(R.id.action_projectlist_allorstar).setIcon(R.drawable.star);
//            }
        }
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.projectlist_fragment_actions, menu);
        currentMenu = menu;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_projectlist_allorstar){
            showAll = !showAll;
            //clean cache before refresh
            projects.setData(new Projects(), false);
            autoloadListView.reLoading();
            if(showAll) {
                getActivity().getActionBar().setTitle(R.string.title_project_list_all);
                item.setIcon(R.drawable.all);
            }
            else{
                getActivity().getActionBar().setTitle(R.string.title_project_list_star);
                item.setIcon(R.drawable.star);
            }

        }

        //consume it
        return true;
    }


    private void fetchData(int page, boolean getAll){

        if(getAll){
            new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.LIST_JOINED_LPROJECT)
                    .addParameter("pageSize", "10")
                    .addParameter("page", "" + page)
                    .addParameter("archive", "false")
                    .setOnResponseListener(new JsonResponseHandler(loadProjects, Projects.class, Arrays.asList("data")))
                    .request();
        }
        else{
            new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.LIST_LIKED_LPROJECT)
                    .addParameter("pageSize", "10")
                    .addParameter("page", "" + page)
                    .setOnResponseListener(new JsonResponseHandler(loadProjects, Projects.class, Arrays.asList("pager")))
                    .request();
        }

//        new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.LIST_JOINED_LPROJECT)
//                .addParameter("pageSize","1000")
//                .addParameter("archive", "false")
//                .setOnResponseListener(new TextResponseHandler() {
//                    @Override
//                    public boolean OnResponseContent(int responseCode, String content) throws Exception {
//                        JsonParser parser = InnoXYZApp.getApplication().getJsonParsers().getParser(Projects.class);
//                        projects.setData((Projects) parser.Parse(new JSONObject(content).getJSONObject("data")), true);
//                        return true;  //To change body of implemented methods use File | Settings | File Templates.
//                    }
//                }).request();
    }

    private View.OnClickListener onToggleClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String addPara = "true";

            if (((ToggleButton)v).isChecked()) {
            }
            else{
                addPara = "false";
            }

            Projects.Project project = (Projects.Project)v.getTag(R.id.item_object);
            new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.LINK_LIKE)
                    .addParameter("type2", "LPROJECT")
                    .addParameter("thingId2", String.valueOf(project.id))
                    .addParameter("add", addPara)
                    .setOnResponseListener(new PostResponseHandler(new toggleListener()))
                    .request();
        }
    };

    private class toggleListener implements OnPostListener {

        public void onPostSuccess(){
            //Log.e("AAAAAAAAAAAA", "SUCCESSSSSSSSS");
            //Toast.makeText(getActivity(), R.string.message_new_success, Toast.LENGTH_SHORT).show();

        }

        public void onPostFail(){

            //Log.e("AAAAAAAAAAAA", "FAILLLLLLLLLLLLL");
            //Toast.makeText(getActivity(), R.string.message_new_fail, Toast.LENGTH_SHORT).show();
        }
    }
}
