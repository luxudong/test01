package com.innoxyz.InnoXYZAndroid.data.remote;

import android.app.Activity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.innoxyz.InnoXYZAndroid.global.InnoXYZApp;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by laborish on 13-12-15.
 */
public class StringRequestBuilder {

    //public StringRequest stringRequest =

    private int method;
    private String baseURI = AddressURIs.HOST;
    private String relURI = "";
    private Map<String, String> params = new HashMap<String, String>();
    private Response.Listener<String> listener = null;
    private boolean reqFromWeb = true;
    private Activity currActivity;

    public StringRequestBuilder(Activity a){
        currActivity = a;
    }

    public StringRequestBuilder reqFromWeb(boolean b){
        reqFromWeb = b;
        return this;
    }

    public StringRequestBuilder setRequestInfo(RequestInfo ri) {
        setSubURI(ri.relURI);
        setMethod(ri.method);
        return this;
    }

    public StringRequestBuilder setMethod(String method) {

        if ( method.compareToIgnoreCase("get")==0 ) {
            this.method = Method.GET;
        } else {
            this.method = Method.POST;
        }
        return this;
    }

    protected StringRequestBuilder setBaseURI(String uri) {
        this.baseURI = uri;
        return this;
    }

    public StringRequestBuilder setSubURI(String uri) {
        this.relURI = uri;
        return this;
    }

    public StringRequestBuilder addParameter(String key, String val){
        this.params.put(key,val);
        return this;
    }

    public StringRequestBuilder setOnResponseListener(Response.Listener<String> listener){
        this.listener = listener;
        return this;
    }

    public StringRequestWithCache getRequest(){
        String uri = baseURI+relURI;
        String uriWithParams = uri;

        uriWithParams += "?";
        for(String key : params.keySet()){
            uriWithParams += key + "=" + params.get(key) + "&";
        }
        uriWithParams = uriWithParams.substring(0,uriWithParams.length()-1);

        if(method == Method.GET){
            uri = uriWithParams;
        }
        StringRequestWithCache stringRequest = new StringRequestWithCache(method,uri,listener,null);
        stringRequest.setKeyUrl(uriWithParams);
        Log.i("Request", "stringstringRequest: " + stringRequest.toString());
        return stringRequest;
    }

    public void request(){
        final StringRequestWithCache stringRequest = getRequest();

        if( !reqFromWeb && InnoXYZApp.getApplication().getWebCache().containsKey(stringRequest.getKeyUrl())){
//            Log.i("CACHE","GET: [" + stringRequest.getKeyUrl() + "][" + InnoXYZApp.getApplication().getWebCache().get(stringRequest.getKeyUrl()) + "]");
//
//            Runnable r = new Runnable() {
//                @Override
//                public void run() {
//                    listener.onResponse(InnoXYZApp.getApplication().getWebCache().get(stringRequest.getKeyUrl()));
//                }
//            };
//            currActivity.runOnUiThread(r);
        }
        else{
            //no cache hit
            InnoXYZApp.getApplication().getRequestQueue().add(stringRequest);
        }



    }



    class StringRequestWithCache extends StringRequest{

        public StringRequestWithCache(int method, String url, Response.Listener<String> listener,
                                      Response.ErrorListener errorListener){
            super(method, url, listener, errorListener);

        }


        String keyUrl = "";

        public void setKeyUrl(String s){
            keyUrl = s;
        }

        public String getKeyUrl(){
            return keyUrl;
        }


        @Override
        public Map<String, String> getParams() {
            Log.i("Request", "params: " + params.toString());
            return params;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headers = super.getHeaders();

            if( headers == null || headers.equals(Collections.emptyMap())){
                headers = new HashMap<String, String>();
            }

            InnoXYZApp.getApplication().addIcCookie(headers);

            headers.put("Accept", "application/json");
            headers.put("Referer", baseURI);
            headers.put("X-Requested-With", "XMLHttpRequest");

            if(method == Method.POST){
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            }
            return headers;
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            // since we don't know which of the two underlying network vehicles
            // will Volley use, we have to handle and store session cookies manually
            InnoXYZApp.getApplication().checkIcCookie(response.headers);

            //add response to cache
            String parsed;
            try {
                parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            } catch (UnsupportedEncodingException e) {
                parsed = new String(response.data);
            }
            InnoXYZApp.getApplication().getWebCache().put(keyUrl, parsed);
            Log.i("CACHE", "ADD: [" + keyUrl + "][" + parsed + "]");


            return super.parseNetworkResponse(response);
        }

    }


}
