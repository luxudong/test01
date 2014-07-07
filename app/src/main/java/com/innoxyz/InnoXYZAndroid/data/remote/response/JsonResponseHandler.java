package com.innoxyz.InnoXYZAndroid.data.remote.response;

import android.util.Log;

import com.innoxyz.InnoXYZAndroid.data.json.parser.JsonParser;
import com.innoxyz.InnoXYZAndroid.data.remote.exceptions.ActionFailedException;
import com.innoxyz.InnoXYZAndroid.data.runtime.SimpleObservedData;
import com.innoxyz.InnoXYZAndroid.global.InnoXYZApp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-22
 * Time: 下午3:50
 * To change this template use File | Settings | File Templates.
 */
public class JsonResponseHandler extends TextResponseHandler {

    final static String RESULT = "action_returns";
    final static String ERROR = "action_errors";
    final static String SUCCESS = "success";

    protected final SimpleObservedData related;
    protected final Class<?> clazz;
    protected final List<String> subObjects;

    public JsonResponseHandler(SimpleObservedData sod, Class<?> clazz, List<String> subObjects) {
        this.related = sod;
        this.clazz = clazz;
        this.subObjects = subObjects;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final boolean OnResponseContent(int responseCode, String content) throws Exception {
        Log.e("Request", content);
        JSONObject json = new JSONObject(content);
        if ( json.getString(RESULT).compareTo(SUCCESS)!=0 ) {
            String err_msg = "";
            if ( json.has(ERROR) ) {
                JSONArray err_arr = json.getJSONArray(ERROR);
                for (int i=0; i< err_arr.length(); i++) {
                    err_msg += err_arr.getString(i) + "\n";
                }
            }
            throw new ActionFailedException(err_msg);
        }
        JsonParser parser = InnoXYZApp.getApplication().getJsonParsers().getParser(clazz);
        if ( subObjects != null ) {
            for(String name : subObjects) {
                json = json.getJSONObject(name);
            }
        }
        related.setData(parser.Parse(json), true);
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
