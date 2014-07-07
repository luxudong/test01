package com.innoxyz.InnoXYZAndroid.data.remote.response;

import com.innoxyz.InnoXYZAndroid.data.remote.exceptions.ActionFailedException;
import com.innoxyz.InnoXYZAndroid.data.remote.interfaces.OnPostListener;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by laborish on 14-3-16.
 */
public class PostResponseHandler extends TextResponseHandler {

    final static String RESULT = "action_returns";
    final static String ERROR = "action_errors";
    final static String SUCCESS = "success";

    protected final OnPostListener onPostListener;

    public PostResponseHandler(OnPostListener onPostListener){
        this.onPostListener = onPostListener;
    }

    public final boolean OnResponseContent(int responseCode, String content) throws Exception {

        //Log.e("Request", content);
        JSONObject json = new JSONObject(content);
        if ( json.getString(RESULT).compareTo(SUCCESS)!=0 ) {

            onPostListener.onPostFail();

            String err_msg = "";
            if ( json.has(ERROR) ) {
                JSONArray err_arr = json.getJSONArray(ERROR);
                for (int i=0; i< err_arr.length(); i++) {
                    err_msg += err_arr.getString(i) + "\n";
                }
            }
            throw new ActionFailedException(err_msg);
        }

        onPostListener.onPostSuccess();
        return true;
    }
}
