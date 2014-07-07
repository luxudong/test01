package com.innoxyz.InnoXYZAndroid.data.runtime.beans.project;

import com.innoxyz.InnoXYZAndroid.data.json.parser.JsonMap;

/**
 * Created by laborish on 14-3-15.
 */
public class MembersOfProj {

    @JsonMap(name = "links")
    public Member[] members;
}
