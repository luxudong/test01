package com.innoxyz.InnoXYZAndroid.data.runtime.beans.notify;

import com.innoxyz.InnoXYZAndroid.data.json.parser.JsonMap;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.User;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.common.Attachment;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.common.NamedText;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.topic.Post;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-11-28
 * Time: 下午10:15
 * To change this template use File | Settings | File Templates.
 */
public class NotifyThing {
    //TASK
    @JsonMap(required = false)
    public String id;
    @JsonMap(required = false)
    public String name;
    @JsonMap(required = false)
    public User[] assignees = new User[0];
    @JsonMap(required = false)
    public String deadline;
    @JsonMap(required = false)
    public NamedText state;
    @JsonMap(required = false)
    public NamedText priority;
    @JsonMap(required = false)
    public String description;
    @JsonMap(required = false)
    public int creatorId;
    @JsonMap(required = false)
    public String creatorName;

    //TOPIC
    //@JsonMap
    //public String id;
    @JsonMap(required = false)
    public String subject;
    @JsonMap(required = false)
    public String content;
    @JsonMap(required = false)
    public String createTime;
    @JsonMap(required = false)
    public String lastPostId = null;
    @JsonMap(required = false)
    public Attachment[] attachments;
    //@JsonMap
    //public String creatorName;
    //@JsonMap
    //public int creatorId;

    public Post lastPost;

    //FILE
    //@JsonMap
    //public String id;
    @JsonMap(required = false)
    public int length;
    //@JsonMap(required = false)
    //public int creatorId;
    //@JsonMap(required = false)
    //public String creatorName;
    //@JsonMap(required = false)
    //public String createTime;
    @JsonMap(required = false)
    public String type;
    @JsonMap(required = false)
    public String contentType;
    @JsonMap(required = false)
    public String dispName;

    //ANNOUNCEMENT
    //@JsonMap
    //public String id;
    //@JsonMap
    //public String content;
    //@JsonMap
    //public String createTime;

    @JsonMap(required = false)
    public String title;


    public String getName() {
        if(subject!=null){
            return subject;
        }
        else if(title!=null){
            return title;
        }
        else if(dispName!=null){
            return dispName;
        }
        else if(name!=null){
            return name;
        }
        return "";
    }

}
