package com.innoxyz.InnoXYZAndroid.ui.fragments.task;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.data.remote.AddressURIs;
import com.innoxyz.InnoXYZAndroid.data.remote.StringRequestBuilder;
import com.innoxyz.InnoXYZAndroid.data.remote.interfaces.OnPostListener;
import com.innoxyz.InnoXYZAndroid.data.remote.response.PostResponseHandler;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.ParcelableUser;
import com.innoxyz.InnoXYZAndroid.ui.customviews.MultiSelectSpinner;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.WithDoneAction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by laborish on 14-3-22.
 */
public class TaskNew extends BaseFragment implements WithDoneAction {

    private static final String[] priorityArray={"高","中","低"};
    //private static final String[] priorityArrayEn={"HIGH","NORMAL","LOW"};
    private static final Map<String,String> priorityMap = new HashMap<String, String>();
    static{
        priorityMap.put("高","HIGH");
        priorityMap.put("中","NORMAL");
        priorityMap.put("低","LOW");
    }

    private int projectId;
    private String priority = "NORMAL";
    private List<ParcelableUser> memberList;
    private Map<String,Integer> memberMap = new HashMap<String, Integer>();

    private MultiSelectSpinner assignedSpinner,connectedSpinner;
    private EditText newTaskName, newTaskDetail;
    private Spinner prioritySpinner;
    private String taskNameString, taskDetailString;
    private View selectDueDate;
    private EditText dueDate;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);    //To change body of overridden methods use File | Settings | File Templates.
        projectId = getArguments().getInt("projectId");
        memberList = (List<ParcelableUser>)getArguments().getParcelableArrayList("memberList").get(0);
        for(int i=0;i<memberList.size();i++){
            //Log.e("aaaaaa", memberList.get(i).getId() + " " + memberList.get(i).getName());
            memberMap.put(memberList.get(i).getName(), memberList.get(i).getId());
        }

        //getActivity().getActionBar().setTitle(R.string.task_new_task);
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.task_new_task);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        View ret = inflater.inflate(R.layout.fragment_new_task, container, false);

        assignedSpinner = (MultiSelectSpinner)ret.findViewById(R.id.new_task_assigned);
        assignedSpinner.setHint(R.string.task_new_task_assigned_members_hint);
        assignedSpinner.setItems(new ArrayList<String>(memberMap.keySet()));

        connectedSpinner = (MultiSelectSpinner)ret.findViewById(R.id.new_task_connected);
        connectedSpinner.setHint(R.string.task_new_task_connected_members_hint);
        connectedSpinner.setItems(new ArrayList<String>(memberMap.keySet()));

        newTaskName = (EditText)ret.findViewById(R.id.new_task_name);
        newTaskDetail = (EditText)ret.findViewById(R.id.new_task_detail);

        newTaskName.setHint(R.string.task_new_task_name_hint);
        newTaskDetail.setHint(R.string.task_new_task_detail_hint);

        prioritySpinner = (Spinner)ret.findViewById(R.id.new_task_priority);
        prioritySpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, priorityArray));
        prioritySpinner.setSelection(1);
        prioritySpinner.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                // An item was selected. You can retrieve the selected item using
                // parent.getItemAtPosition(pos)
                priority = priorityMap.get(parent.getItemAtPosition(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                priority = "NORMAL";
            }
        });

        selectDueDate = ret.findViewById(R.id.new_task_select_due_date);
        selectDueDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogFragment newFragment = new datePickerDialogFragment();
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
        dueDate = (EditText)ret.findViewById(R.id.new_task_due_date);
        dueDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DialogFragment newFragment = new datePickerDialogFragment();
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
        return ret;
    }

    public class datePickerDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Calendar calendar= Calendar.getInstance();

            return new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener(){
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    dueDate.setText(String.format("%d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));

                }
            },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));

        }
    }


    public void actionDone(){

        // Reset errors.
        newTaskName.setError(null);
        newTaskDetail.setError(null);

        // Store values at the time of the login attempt.
        taskNameString = newTaskName.getText().toString();
        taskDetailString = newTaskDetail.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(taskDetailString)) {
            newTaskDetail.setError(getString(R.string.error_field_required));
            focusView = newTaskDetail;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(taskNameString)) {
            newTaskName.setError(getString(R.string.error_field_required));
            focusView = newTaskName;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
//            showProgress(true);


            //connectedUserIds:
            String connectedUserIds = "";
            List<String> connectedName = connectedSpinner.getSelectedStrings();
            for(int i = 0; i<connectedName.size(); i++){
                if(memberMap.containsKey(connectedName.get(i))){
                    //connectedUserIds += "\"";
                    connectedUserIds += Integer.toString(memberMap.get(connectedName.get(i)));
                    //connectedUserIds += "\"";
                    connectedUserIds += ",";
                }
            }
            if(connectedUserIds.length()!=0 && connectedUserIds.charAt(connectedUserIds.length()-1) == ','){
                connectedUserIds = connectedUserIds.substring(0, connectedUserIds.length()-1);
            }
            connectedUserIds = "[" + connectedUserIds;
            connectedUserIds += "]";

            //assignedUserIds:
            String assignedUserIds = "";
            List<String> assignedName = assignedSpinner.getSelectedStrings();
            for(int i = 0; i<assignedName.size(); i++){
                if(memberMap.containsKey(assignedName.get(i))){
                    assignedUserIds += "{\"id\":";
                    assignedUserIds += Integer.toString(memberMap.get(assignedName.get(i)));
                    assignedUserIds += "}";
                    assignedUserIds += ",";
                }
            }
            if(assignedUserIds.length()!=0 && assignedUserIds.charAt(assignedUserIds.length()-1) == ','){
                assignedUserIds = assignedUserIds.substring(0, assignedUserIds.length()-1);
            }
            assignedUserIds = "[" + assignedUserIds;
            assignedUserIds += "]";

            //post task string like this:
            //task:{"hostId":"302","name":"aaa","priority":"LOW","state":"NEW","deadline":"2014-04-01T00:00:00.000Z","description":"bbb","assignees":[{"id":883},{"id":8}]}
            //task:{"hostId":"302","name":"aaa","priority":"LOW","state":"NEW","description":"bbb","assignees":[{"id":883},{"id":8}]}

            //Log.e("AAAAAAAAAAAAA", "["+dueDate.getText()+"]");

            String task = String.format("{\"hostId\":\"%s\",\"name\":\"%s\",\"priority\":\"%s\",\"state\":\"NEW\",\"description\":\"%s\",\"assignees\":%s"
                    , projectId
                    , taskNameString
                    , priority
                    , taskDetailString
                    , assignedUserIds);

            if(dueDate.getText().length() == 0 || dueDate.getText().equals(getResources().getString(R.string.task_new_default_due_date))){

            }
            else{
                String dueDateString = String.format("\"deadline\":\"%sT00:00:00.000Z\"", dueDate.getText());
                task += ",";
                task += dueDateString;
            }

            task += "}";

            //Log.e("AAAAAAAAAAAAA", "task:" + task);





            try {
                new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.TASK_NEW)
                        .addParameter("task", task)
                        .addParameter("userIds", connectedUserIds)
                        .addParameter("attachments", "[]")
                        .setOnResponseListener(new PostResponseHandler(new TaskNewReplyListener()))
                        .request();
            } catch (Exception e) {
            }
        }
    }

    private class TaskNewReplyListener implements OnPostListener {

        public void onPostSuccess(){
            //Log.e("AAAAAAAAAAAA", "SUCCESSSSSSSSS");
            Toast.makeText(getActivity(), R.string.task_new_success, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        public void onPostFail(){

            //Log.e("AAAAAAAAAAAA", "FAILLLLLLLLLLLLL");
            Toast.makeText(getActivity(), R.string.task_new_fail, Toast.LENGTH_SHORT).show();
        }
    }


}
