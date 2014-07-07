package com.innoxyz.InnoXYZAndroid.data.runtime.beans.project;

import com.innoxyz.InnoXYZAndroid.data.json.parser.JsonMap;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.User;

/**
 * Created by laborish on 14-3-15.
 */
public class Member {
    @JsonMap
    public Role role;
    @JsonMap(name = "createUser")
    public User user;
    @JsonMap
    public String confirm;
}
