package com.innoxyz.InnoXYZAndroid.data.runtime.beans.notify;

import com.innoxyz.InnoXYZAndroid.data.json.parser.JsonMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-15
 * Time: 下午12:39
 * To change this template use File | Settings | File Templates.
 */
public class Notify {
    @JsonMap(name = "_id")
    public String id;
    @JsonMap(required = false)
    public int hostId;
    @JsonMap(required = false)
    public String hostName;
    @JsonMap
    public String last;
    @JsonMap(required = false)
    public String thingType = null;
    @JsonMap(name = "notifications")
    public SubNotify[] subNotifies;
    @JsonMap(required = false)
    public NotifyThing thing;
}
