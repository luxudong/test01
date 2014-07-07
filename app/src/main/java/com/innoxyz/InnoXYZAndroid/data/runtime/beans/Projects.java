package com.innoxyz.InnoXYZAndroid.data.runtime.beans;

import com.innoxyz.InnoXYZAndroid.data.json.parser.JsonMap;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.common.Pager;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-8-8
 * Time: 下午12:35
 * To change this template use File | Settings | File Templates.
 */
public class Projects extends Pager<Projects.Project> {

    @JsonMap(name = "data")
    public Project[] projects;

    @Override
    protected Project[] getItems() {
        return projects;
    }

    @Override
    protected void setItems(Project[] items) {
        projects = items;
    }

    public static class Project {
        @JsonMap
        public int id;
        @JsonMap
        public String name;
        @JsonMap(required = false)
        public boolean liked = false;
        @JsonMap
        public String createTime;
        @JsonMap
        public String baseDirId;

    }
}
