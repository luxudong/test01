package com.innoxyz.InnoXYZAndroid.data.runtime.beans.calender;

import com.innoxyz.InnoXYZAndroid.data.json.parser.JsonMap;

/**
 * Created by dingwei on 14-5-16.
 */
public class Events {
    @JsonMap
    public String id;
    @JsonMap
    public String summary;
    @JsonMap
    public String startTime;
    @JsonMap
    public String endTime;
    @JsonMap
    public String location;
    @JsonMap
    public String description;
}
