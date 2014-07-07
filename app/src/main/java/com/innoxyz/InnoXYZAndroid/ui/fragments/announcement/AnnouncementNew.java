package com.innoxyz.InnoXYZAndroid.ui.fragments.announcement;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.data.remote.AddressURIs;
import com.innoxyz.InnoXYZAndroid.data.remote.StringRequestBuilder;
import com.innoxyz.InnoXYZAndroid.data.remote.interfaces.OnPostListener;
import com.innoxyz.InnoXYZAndroid.data.remote.response.PostResponseHandler;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.WithDoneAction;


/**
 * Created by laborish on 14-3-29.
 */
public class AnnouncementNew extends BaseFragment implements WithDoneAction {

    private int projectId;

    private EditText newAnnouncementTitle, newAnnouncementContent;
    private String announcementTitleString, announcementContentString;
    private com.innoxyz.InnoXYZAndroid.ui.customviews.MultiSelectSpinner newAnnouncementSelectUser;
    private CheckBox newAnnouncementCheckBox;
    Boolean checkBoxData;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);    //To change body of overridden methods use File | Settings | File Templates.
        projectId = getArguments().getInt("projectId");

        //getActivity().getActionBar().setTitle(R.string.announcement_new_announcement);
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.announcement_new_announcement);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        View ret = inflater.inflate(R.layout.fragment_new_announcement, container, false);

        newAnnouncementTitle = (EditText)ret.findViewById(R.id.new_announcement_title);
        newAnnouncementContent = (EditText)ret.findViewById(R.id.new_announcement_content);

        newAnnouncementCheckBox = (CheckBox)ret.findViewById(R.id.announcement_new_send_to_regEmail_checkBox);

        newAnnouncementTitle.setHint(R.string.announcement_new_announcement_title_hint);
        newAnnouncementContent.setHint(R.string.announcement_new_announcement_content_hint);

        return ret;
    }

    public void actionDone(){

        //Log.e("AAAAAAAAAAAA","actionDone pressed");

        // Reset errors.
        newAnnouncementTitle.setError(null);
        newAnnouncementContent.setError(null);

        // Store values at the time of the login attempt.
        announcementTitleString = newAnnouncementTitle.getText().toString();
        announcementContentString = newAnnouncementContent.getText().toString();
        checkBoxData = newAnnouncementCheckBox.isChecked();


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(announcementContentString)) {
            newAnnouncementContent.setError(getString(R.string.error_field_required));
            focusView = newAnnouncementContent;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(announcementTitleString)) {
            newAnnouncementTitle.setError(getString(R.string.error_field_required));
            focusView = newAnnouncementTitle;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            try {
                new StringRequestBuilder(getActivity()).setRequestInfo(AddressURIs.ANNOUNCEMENT_NEW)
                        .addParameter("thingId", "" + projectId)
                        .addParameter("title", announcementTitleString)
                        .addParameter("content", announcementContentString)
                        .addParameter("emailNotify", ""+checkBoxData)
                        .addParameter("attachments", "[]")
                        .setOnResponseListener(new PostResponseHandler(new AnnouncementNewReplyListener()))
                        .request();
            } catch (Exception e) {
                Log.e("AnnouncementNew", "通知提交失败！");
            }
        }
    }

    private class AnnouncementNewReplyListener implements OnPostListener {

        public void onPostSuccess(){
            //Log.e("AAAAAAAAAAAA", "SUCCESSSSSSSSS");
            Toast.makeText(getActivity(), R.string.announcement_new_success, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        public void onPostFail(){

            //Log.e("AAAAAAAAAAAA", "FAILLLLLLLLLLLLL");
            Toast.makeText(getActivity(), R.string.announcement_new_fail, Toast.LENGTH_SHORT).show();
        }
    }

}
