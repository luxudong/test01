package com.innoxyz.InnoXYZAndroid.global;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.innoxyz.InnoXYZAndroid.data.json.parser.JsonParsers;
import com.innoxyz.InnoXYZAndroid.data.remote.TheClient;
import com.innoxyz.InnoXYZAndroid.data.runtime.RuntimeDataManager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-7
 * Time: 下午5:15
 * To change this template use File | Settings | File Templates.
 */
public class InnoXYZApp extends Application {

    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    private static final String IC_COOKIE = "ic";

    private static InnoXYZApp instance = null;
    public static InnoXYZApp getApplication() {
        return instance;
    }


    Map<String,String> webCache = new HashMap<String, String>();

    public Map<String,String> getWebCache() {return webCache;}

    private SharedPreferences sharedPreferences;


    private RuntimeDataManager runtimeDataManager;
    private JsonParsers jsonParsers;
    private TheClient theClient;
    private Handler handler;

    private ImageLruCache imageLruCache;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    public Semaphore getGetUnreadNumNow() {
        return getUnreadNumNow;
    }

    private Semaphore getUnreadNumNow = new Semaphore(1);

    private int currentUserId;

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }

    public RuntimeDataManager getRuntimeDataManager() {
        return runtimeDataManager;
    }

    public JsonParsers getJsonParsers() {
        return jsonParsers;
    }

    public TheClient getTheClient() {
        return theClient;
    }

    public Handler getMainThreadHandler() {
         return handler;
    }

    public ImageLruCache getImageLruCache() { return imageLruCache; }

    public RequestQueue getRequestQueue() { return requestQueue; }

    public ImageLoader getImageLoader() {return imageLoader; }

    @Override
    public void onCreate() {
        super.onCreate();    //To change body of overridden methods use File | Settings | File Templates.
        instance = this;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            runtimeDataManager = new RuntimeDataManager();
            jsonParsers = new JsonParsers();
            //theClient = new TheClient();

            imageLruCache = new ImageLruCache(ImageLruCache.getDefaultLruCacheSize());
            requestQueue = Volley.newRequestQueue(this);
            imageLoader = new ImageLoader(requestQueue, imageLruCache);
        } catch (Exception e) {
            Log.e("Application creating", "Failed on initializing application data. exiting..." + Log.getStackTraceString(e));
        }
        handler = new Handler();
    }

    public final void checkIcCookie(Map<String, String> headers) {
        if (headers.containsKey(SET_COOKIE_KEY)
                && headers.get(SET_COOKIE_KEY).contains(IC_COOKIE)) {
            String cookie = headers.get(SET_COOKIE_KEY);

            if (cookie.length() > 0) {
                String[] splitCookie = cookie.split(";");
                for(String s : splitCookie){
                    if(s.contains(IC_COOKIE)){
                        cookie = s;
                    }
                }
                String[] splitIc = cookie.split("=");
                cookie = splitIc[1];
                SharedPreferences.Editor prefEditor = sharedPreferences.edit();
                prefEditor.putString(IC_COOKIE, cookie);
                prefEditor.commit();
            }
        }
    }

    public final void addIcCookie(Map<String, String> headers) {
        String ic = sharedPreferences.getString(IC_COOKIE, "");
        if (ic.length() > 0) {
            StringBuilder builder = new StringBuilder();
            builder.append(IC_COOKIE);
            builder.append("=");
            builder.append(ic);
            if (headers.containsKey(COOKIE_KEY)) {
                builder.append("; ");
                builder.append(headers.get(COOKIE_KEY));
            }
            headers.put(COOKIE_KEY, builder.toString());
        }
    }

    public final boolean hasIcCookie(){
        String ic = sharedPreferences.getString(IC_COOKIE, "");
        if (ic.length() > 0)
            return true;
        else
            return false;
    }

    public final void removeIcCookie(){
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        prefEditor.remove(IC_COOKIE);
        prefEditor.commit();
    }
}
