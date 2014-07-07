package com.innoxyz.InnoXYZAndroid.data.runtime.beans;

import com.innoxyz.InnoXYZAndroid.data.json.parser.JsonMap;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.project.Member;

/**
 * Created by laborish on 2014-4-14.
 */
public class MembersOfThing{

    @JsonMap(name = "data")
    public Member[] members;
}

