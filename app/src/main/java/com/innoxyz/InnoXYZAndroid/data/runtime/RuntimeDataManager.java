package com.innoxyz.InnoXYZAndroid.data.runtime;

import android.content.Context;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-2
 * Time: 下午3:19
 * To change this template use File | Settings | File Templates.
 */
public class RuntimeDataManager {
    private HashMap<Context, HashMap<String, Object>> dataCenter = new HashMap<Context, HashMap<String, Object>>();

    public RuntimeDataManager() {}

    public HashMap<String, Object> Add(Context context, boolean alwaysCreateNew) {
        if ( alwaysCreateNew || !dataCenter.containsKey(context) ) {
            return dataCenter.put(context, new HashMap<String, Object>());
        } else {
            return dataCenter.get(context);
        }
    }

    public void Release(Context context) {
        dataCenter.remove(context);
    }

    public HashMap<String, Object> get(Context context) {
        return dataCenter.get(context);
    }
}
