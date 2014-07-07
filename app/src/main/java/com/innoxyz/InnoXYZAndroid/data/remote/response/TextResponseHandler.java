package com.innoxyz.InnoXYZAndroid.data.remote.response;

import com.android.volley.Response;
import com.innoxyz.InnoXYZAndroid.data.remote.interfaces.OnResponseListener;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-7
 * Time: 下午3:22
 * To change this template use File | Settings | File Templates.
 */
public abstract class TextResponseHandler implements OnResponseListener, Response.Listener<String> {
    @Override
    public final boolean OnResponse(HttpResponse response) /*throws Exception*/ {
        try {
            return OnResponseContent(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));  //To change body of implemented methods use File | Settings | File Templates.
        } catch (Exception e) {
            //throw new RuntimeException(e);
            return false;
        }
    }

    abstract public boolean OnResponseContent(int responseCode, String content) throws Exception;

    public final void onResponse(String t){
        try{
            OnResponseContent(200,t);
        }catch (Exception e) {
            //throw new RuntimeException(e);
        }
    }
}
