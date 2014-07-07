package com.innoxyz.InnoXYZAndroid.data.runtime.beans.calender;

import com.innoxyz.InnoXYZAndroid.data.json.parser.JsonMap;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.common.Data;

/**
 * Created by dingwei on 14-5-20.
 */
public class CalendarIds extends Data<CalendarId> {
	@JsonMap(name = "data")
	public CalendarId[] CalendarIds;



	@Override
	protected CalendarId[] getItems() {
		return CalendarIds;
	}

	@Override
	protected void setItems(CalendarId[] items) {
		CalendarIds=items;

	}
}
