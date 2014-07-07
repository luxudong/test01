package com.innoxyz.InnoXYZAndroid.ui.fragments.task;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.data.remote.AddressURIs;
import com.innoxyz.InnoXYZAndroid.data.remote.StringRequestBuilder;
import com.innoxyz.InnoXYZAndroid.data.remote.interfaces.OnPostListener;
import com.innoxyz.InnoXYZAndroid.data.remote.response.PostResponseHandler;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.WithDoneAction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by laborish on 14-3-28.
 */
public class TaskReply extends BaseFragment implements WithDoneAction {
    private static final String[] stateArray={"修改任务状态", "新建","进行中","挂起","完成","其它"};

    private static final Map<String,String> stateMap = new HashMap<String, String>();
    static{
        stateMap.put("新建","NEW");
        stateMap.put("进行中","IN_PROGRESS");
        stateMap.put("挂起","SUSPEND");
        stateMap.put("完成","COMPLETE");
        stateMap.put("其它","OTHER");
    }

    private int projectId;
    private String taskId;
    private String currentState;

    private EditText replyTaskDetail;
    private Spinner stateSpinner;

    private String taskDetailString;
    private String stateString;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);    //To change body of overridden methods use File | Settings | File Templates.

        projectId = getArguments().getInt("projectId");
        taskId = getArguments().getString("taskId");
        currentState = getArguments().getString("state");


        //getActivity().getActionBar().setTitle(R.string.task_reply_task);
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.task_reply_task);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        View ret = inflater.inflate(R.layout.fragment_task_reply, container, false);

        replyTaskDetail = (EditText)ret.findViewById(R.id.reply_task_detail);
        stateSpinner = (Spinner)ret.findViewById(R.id.reply_task_state);

        stateSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, stateArray));
        stateSpinner.setSelection(0);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                stateString = stateMap.get(parent.getItemAtPosition(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //stateString = stateMap.get(currentState);
            }
        });

        return ret;
    }

    public void actionDone(){

        // Reset errors.
        replyTaskDetail.setError(null);

        // Store values at the time of the login attempt.
        taskDetailString = replyTaskDetail.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(taskDetailString)) {
            replyTaskDetail.setError(getString(R.string.error_field_required));
            focusView = replyTaskDetail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            //post task string like this:
            //state:{"taskId":"532d84b673f2fa1479576e6b","hostId":"545","hostType":"LPROJECT","comment":"456","modifications":{"state":{"newValue":"IN_PROGRESS"}}}

            if(stateString == null){
                stateString = stateMap.get(currentState);
            }
            if(stateString == null){
                stateString = "OTHER";
            }

            String state = String.format("{\"taskId\":\"%s\",\"hostId\":\"%d\",\"hostType\":\"LPROJECT\",\"comment\":\"%s\",\"modifications\":{\"state\":{\"newValue\":\"%s\"}}}"
                    , taskId
                    , projectId
                    , taskDetailString
                    , stateString);

            //Log.e("AAAAAAAAAAAAA", "state:" + state);

            try {
                new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.TASK_REPLY)
                        .addParameter("state", state)
                        .addParameter("attachments", "[]")
                        .setOnResponseListener(new PostResponseHandler(new TaskReplyReplyListener()))
                        .request();
            } catch (Exception e) {
            }
        }
    }

    private class TaskReplyReplyListener implements OnPostListener {

        public void onPostSuccess(){
            //Log.e("AAAAAAAAAAAA", "SUCCESSSSSSSSS");
            Toast.makeText(getActivity(), R.string.task_reply_success, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        public void onPostFail(){

            //Log.e("AAAAAAAAAAAA", "FAILLLLLLLLLLLLL");
            Toast.makeText(getActivity(), R.string.task_reply_fail, Toast.LENGTH_SHORT).show();
        }
    }



}
