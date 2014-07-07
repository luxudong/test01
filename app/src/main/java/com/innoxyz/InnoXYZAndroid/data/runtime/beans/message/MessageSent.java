package com.innoxyz.InnoXYZAndroid.data.runtime.beans.message;

import com.innoxyz.InnoXYZAndroid.data.json.parser.JsonMap;

/**
 * Created by dell on 2014/5/18.
 */
public class MessageSent {
    @JsonMap
    public String id;

    @JsonMap
    public String content;

    @JsonMap
    public Reciver[] recivers;

    @JsonMap
    public String subject;

    @JsonMap
    public String senderName;

    @JsonMap
    public int senderId;

    @JsonMap
    public Boolean deleted;

    @JsonMap
    public Boolean read;

    @JsonMap
    public String sendTime;

    @JsonMap
    String ownerId;

}
