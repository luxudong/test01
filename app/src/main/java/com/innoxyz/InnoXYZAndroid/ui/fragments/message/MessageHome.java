package com.innoxyz.InnoXYZAndroid.ui.fragments.message;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.ui.activities.NewActivity;
import com.innoxyz.InnoXYZAndroid.ui.commands.ActivityCommand;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-11-29
 * Time: 下午8:26
 * To change this template use File | Settings | File Templates.
 */
public class MessageHome extends BaseFragment {

    private String senderName,recivers,subject,content;
    private int senderId;
    private boolean inBox;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);    //To change body of overridden methods use File | Settings | File Templates.

        //getActivity().getActionBar().setTitle(R.string.title_message_home);

    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.title_message_home);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        setHasOptionsMenu(true);

        View ret = inflater.inflate(R.layout.fragment_message_home, container, false);

        //inBox = getArguments().getBoolean("inBox");
        senderId = getArguments().getInt("senderId");
        senderName = getArguments().getString("senderName");
        subject = getArguments().getString("subject");
        content = getArguments().getString("content");
        recivers = getArguments().getString("recivers");


       // if(inBox){
            ((TextView)ret.findViewById(R.id.message_home_nameTitle)).setText(R.string.message_sender_title);
            ((TextView)ret.findViewById(R.id.message_home_nameText)).setText(senderName);
       // }
//        else{
//            ((TextView)ret.findViewById(R.id.message_home_nameTitle)).setText(R.string.message_receiver_title);
//            ((TextView)ret.findViewById(R.id.message_home_nameText)).setText(recivers);
//        }

        ((TextView)ret.findViewById(R.id.message_home_subject)).setText(subject);
        Spanned spanned = Html.fromHtml(content);
        ((TextView)ret.findViewById(R.id.message_home_content)).setText(spanned);
        ((TextView)ret.findViewById(R.id.message_home_content)).setMovementMethod(LinkMovementMethod.getInstance());                    //设置可点击
        return ret;

    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        //if(inBox){
            inflater.inflate(R.menu.message_reply_action, menu);

       // }
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        int id = item.getItemId();
        if (id == R.id.action_reply){
            Bundle bundle = new Bundle();
            bundle.putAll(getArguments());
            new ActivityCommand(NewActivity.class, MessageNew.class, MessageHome.this.getActivity(), bundle, null).Execute();
            //consume it
            return true;
        }

        //not consume it
        return false;
    }
}
