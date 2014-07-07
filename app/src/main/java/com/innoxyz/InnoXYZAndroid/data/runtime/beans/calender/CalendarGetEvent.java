package com.innoxyz.InnoXYZAndroid.data.runtime.beans.calender;

import com.innoxyz.InnoXYZAndroid.data.json.parser.JsonMap;

/**
 * Created by dingwei on 14-4-29.
 */
public class CalendarGetEvent  {
    @JsonMap
    public String id;
   @JsonMap
    public String creatorName;
    @JsonMap
    public String name;

    //public Events[] events = new Events[0];
    @JsonMap(name = "events")
    public Events[] events;




}
