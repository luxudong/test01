package com.innoxyz.InnoXYZAndroid.data.runtime.beans.calender;

import com.innoxyz.InnoXYZAndroid.data.json.parser.JsonMap;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.common.Data;

/**
 * Created by dingwei on 14-4-29.
 */
//public class CalendarGetEvents extends Pager<CalendarGetEvent> {
public class CalendarGetEvents extends Data<CalendarGetEvent> {
//【注意】这里的Pager<CalendarGetEvent>，是导致JsonParser.java报错的原因.因为这里有page，pageSize,total.
//如果不用Pager<CalendarGetEvent>，JsonParser.java中的114-135行都不需要的，这个本来是对这里没有page，pageSize,total.这3个异常的处理的。
//如果不用Pager<CalendarGetEvent>，JsonParser.java中的114-135行都不需要的，这个本来是对这里没有page，pageSize,total.这3个异常的处理的。
//还有CalendarEvents01.java和CalendarSubEvents01.java中的autolv.endLoad(calendarEvents.getData().hasMore());要注释掉。


     @JsonMap(name = "data")
     public CalendarGetEvent[] CalendarGetEvents;
    @Override
    protected CalendarGetEvent[] getItems() {
        return CalendarGetEvents;
    }

    @Override
    protected void setItems(CalendarGetEvent[] items) {
        CalendarGetEvents=items;

    }
}
