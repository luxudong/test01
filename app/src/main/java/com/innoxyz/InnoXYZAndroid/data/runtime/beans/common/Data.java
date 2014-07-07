package com.innoxyz.InnoXYZAndroid.data.runtime.beans.common;

import com.innoxyz.InnoXYZAndroid.ui.utils.ArrayUtils;

/**
 * Created by dingwei on 14-5-19.
 */
  abstract public class Data<T> {
	protected abstract T[] getItems();
	protected abstract void setItems(T[] items);

	public void append(Data<T> data) {
		if ( getItems() == null || getItems().length == 0 ) {
			setItems(data.getItems());
		} else {
			setItems(ArrayUtils.merge(getItems(), data.getItems()));
		}
}
}