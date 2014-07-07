package com.innoxyz.InnoXYZAndroid.ui.fragments.project;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.data.remote.AddressURIs;
import com.innoxyz.InnoXYZAndroid.data.remote.StringRequestBuilder;
import com.innoxyz.InnoXYZAndroid.data.remote.response.JsonResponseHandler;
import com.innoxyz.InnoXYZAndroid.data.runtime.SimpleObservedData;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.ParcelableUser;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.project.MembersOfProj;
import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;
import com.innoxyz.InnoXYZAndroid.global.InnoXYZApp;
import com.innoxyz.InnoXYZAndroid.ui.activities.NewActivity;
import com.innoxyz.InnoXYZAndroid.ui.commands.ActivityCommand;
import com.innoxyz.InnoXYZAndroid.ui.commands.FragmentCommand;
import com.innoxyz.InnoXYZAndroid.ui.fragments.announcement.AnnouncementNew;
import com.innoxyz.InnoXYZAndroid.ui.fragments.announcement.ProjectAnnouncementList;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.fragments.document.DocumentList;
import com.innoxyz.InnoXYZAndroid.ui.fragments.task.ProjectTaskList;
import com.innoxyz.InnoXYZAndroid.ui.fragments.topic.ProjectTopicList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-1
 * Time: 上午11:59
 * To change this template use File | Settings | File Templates.
 */
public class ProjectHome extends BaseFragment {
    private int projectId;

    SimpleObservedData<MembersOfProj> membersOfProj;
    List<ParcelableUser> memberList = new ArrayList<ParcelableUser>();

    private int projectCreatorId;

    private class Item {
        int srcId;
        int textId;
        Class<? extends BaseFragment> linkClass;

        public Item(int srcId, int textId, Class<? extends BaseFragment> linkClass) {
            this.srcId = srcId;
            this.textId = textId;
            this.linkClass = linkClass;
        }

        public View createView(LayoutInflater inflater, int resId, ViewGroup container) {
            View ret = inflater.inflate(resId, container, false);

            ((ImageView)ret.findViewById(R.id.projhome_button_imageView)).setImageResource(srcId);
            ((TextView)ret.findViewById(R.id.projhome_button_textView)).setText(textId);
            ret.setTag(R.id.item_id, this);
            container.addView(ret);
            return ret;
        }
    }

//    final private Item[] hItems = new Item[]{
//            new Item(R.drawable.icon_tasklist, R.string.menu_projecthome_newtask, TaskNew.class),
//            new Item(R.drawable.icon_discusslist, R.string.menu_projecthome_newdiscussion, TopicNew.class),
//            new Item(R.drawable.icon_announcementlist, R.string.menu_projecthome_newannouncement, AnnouncementNew.class),
//            new Item(R.drawable.icon_documentlist,R.string.menu_projecthome_newdocument,null)
//    };
    final private Item[] vItems = new Item[]{
            new Item(R.drawable.icon_tasklist, R.string.menu_projecthome_tasklist, ProjectTaskList.class),
            new Item(R.drawable.icon_discusslist, R.string.menu_projecthome_discussionlist, ProjectTopicList.class),
            new Item(R.drawable.icon_documentlist, R.string.menu_projecthome_documentlist, DocumentList.class),
            new Item(R.drawable.icon_announcementlist, R.string.menu_projecthome_announcementlist, ProjectAnnouncementList.class),
            //new Item(R.drawable.icon_date, R.string.menu_projecthome_date, null),
    };
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);    //To change body of overridden methods use File | Settings | File Templates.
        projectId = getArguments().getInt("projectId");

        //getActivity().getActionBar().setTitle(R.string.title_project_home);
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.title_project_home);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        membersOfProj = new SimpleObservedData<MembersOfProj>(new MembersOfProj());

        membersOfProj.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {
                //memberList = Arrays.asList(membersOfProj.getData().members);
                for(int i=0;i<membersOfProj.getData().members.length;i++){
                    ParcelableUser parcelableUser = new ParcelableUser();
                    parcelableUser.setId(membersOfProj.getData().members[i].user.id);
                    parcelableUser.setName(membersOfProj.getData().members[i].user.realname);
                    if(membersOfProj.getData().members[i].role.name.equals("CREATOR")){
                        projectCreatorId = membersOfProj.getData().members[i].user.id;
                    }
                    memberList.add(i,parcelableUser);
                }
//                for(int i=0;i<memberList.size();i++){
//                    Log.e("aaaaaa",memberList.get(i).user.id+" "+memberList.get(i).user.realname);
//                }
            }
        });

        new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.GET_MEMBER_OF_PROJECT)
                .addParameter("thingId", "" + projectId)
                .addParameter("pageSize", "100")
                .setOnResponseListener(new JsonResponseHandler(membersOfProj, MembersOfProj.class, null))
                .request();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        View ret = inflater.inflate(R.layout.project_home, container, false);
        View.OnClickListener vItemsclickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item item = (Item) view.getTag(R.id.item_id);
                if ( item.linkClass != null ) {
                    Bundle bundle = new Bundle();
                    bundle.putAll(getArguments());
                    bundle.putInt("projectId", projectId);
                    bundle.putInt("projectCreatorId",projectCreatorId);
                    if(memberList != null){
                        ArrayList list = new ArrayList();
                        list.add(memberList);
                        bundle.putParcelableArrayList("memberList",list);
                    }
                    new FragmentCommand(ProjectHome.class, item.linkClass, ProjectHome.this.getActivity(), bundle, null).Execute();
                }
            }
        };

         //open new items in new activity
        View.OnClickListener hItemsclickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item item = (Item) view.getTag(R.id.item_id);
                if ( item.linkClass != null ) {
                    Bundle bundle = new Bundle();
                    bundle.putAll(getArguments());
                    bundle.putInt("projectId", projectId);
                    if(memberList != null){
                        ArrayList list = new ArrayList();
                        list.add(memberList);
                        bundle.putParcelableArrayList("memberList",list);
                    }
                    //new FragmentCommand(ProjectHome.class, item.linkClass, ProjectHome.this.getActivity(), bundle, null).Execute();
                    if(item.linkClass == AnnouncementNew.class && projectCreatorId != InnoXYZApp.getApplication().getCurrentUserId()){
                        new AlertDialog.Builder(getActivity())
                                .setMessage(getString(R.string.announcement_new_warning))
                                .setPositiveButton(getString(R.string.OK), null)
                                .show();
                    }
                    else{
                        new ActivityCommand(NewActivity.class, item.linkClass, ProjectHome.this.getActivity(), bundle, null).Execute();
                    }

                }
            }
        };
//        for(Item item : hItems) {
//            item.createView(inflater, R.layout.fragment_imagebutton_projecthome, (ViewGroup)ret.findViewById(R.id.projecthome_horizontal)).setOnClickListener(hItemsclickListener);
//        }
        for(Item item : vItems) {
            item.createView(inflater, R.layout.fragment_listbutton_projecthome, (ViewGroup)ret.findViewById(R.id.projecthome_vertical)).setOnClickListener(vItemsclickListener);
        }
        return ret;
    }
}
