package com.innoxyz.InnoXYZAndroid.data.runtime.beans.task;

import com.innoxyz.InnoXYZAndroid.data.json.parser.JsonMap;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.common.Attachment;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-15
 * Time: 下午1:03
 * To change this template use File | Settings | File Templates.
 */
public class Pushstate {
    @JsonMap
    public String creatorName;
    @JsonMap
    public int creatorId;
    @JsonMap
    public String createTime;
    @JsonMap
    public String comment;
    @JsonMap(required = false)
    public Attachment[] attachments = null;
    @JsonMap(required = false)
    public Modification modifications = null;

    public static class Modification {
        @JsonMap(name = "state", required = false)
        public Change stateChange = null;
        @JsonMap(name = "name", required = false)
        public Change nameChange = null;
    }

    public static class Change {
        @JsonMap
        public String oldValue;
        @JsonMap
        public String newValue;
    }
}

