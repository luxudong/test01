package com.innoxyz.InnoXYZAndroid.data.runtime.beans.user;

import com.innoxyz.InnoXYZAndroid.data.json.parser.JsonMap;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.User;

/**
 * Created by laborish on 14-3-23.
 */
public class AutoCompleteUserList {

    @JsonMap(name = "data")
    public User[] users;
}
