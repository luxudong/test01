package com.innoxyz.InnoXYZAndroid.data.runtime.beans.message;

import com.innoxyz.InnoXYZAndroid.data.json.parser.JsonMap;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.common.Pager;

/**
 * Created by dell on 2014/5/18.
 */
public class MessagesOutbox extends Pager<MessageSent>{
    @JsonMap(name = "data")
    public MessageSent[] msgOutboxArr;

    @Override
    protected MessageSent[] getItems() {
        return msgOutboxArr;
    }

    @Override
    protected void setItems(MessageSent[] items) {
        msgOutboxArr = items;
    }
}
