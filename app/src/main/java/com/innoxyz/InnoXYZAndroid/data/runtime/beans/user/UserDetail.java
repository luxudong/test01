package com.innoxyz.InnoXYZAndroid.data.runtime.beans.user;

import com.innoxyz.InnoXYZAndroid.data.json.parser.JsonMap;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-10-16
 * Time: 下午2:10
 * To change this template use File | Settings | File Templates.
 */
public class UserDetail {
    @JsonMap
    public int id;
    @JsonMap
    public String city;
    @JsonMap
    public String department;
    @JsonMap
    public String institution;
    @JsonMap
    public String title;

}
