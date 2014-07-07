package com.innoxyz.InnoXYZAndroid.ui.fragments.topic;

import com.innoxyz.InnoXYZAndroid.R;

import java.util.HashMap;

/**
 * Created by shenw on 14-4-26.
 */
public class FileWithImage {
    HashMap<String,Integer> attachImage;
    public HashMap<String,Integer> init() {
        attachImage = new HashMap<String, Integer>();
        attachImage.put(".doc", R.drawable.doc);
        attachImage.put(".docx",R.drawable.docx);
        attachImage.put(".ppt",R.drawable.ppt);
        attachImage.put(".pptx",R.drawable.pptx);
        attachImage.put(".pdf",R.drawable.pdf);
        attachImage.put(".txt",R.drawable.txt);
        attachImage.put(".jpeg",R.drawable.jpeg);
        attachImage.put(".jpg",R.drawable.jpg);
        attachImage.put(".png",R.drawable.png);
        attachImage.put(".mp3",R.drawable.mp3);
        attachImage.put(".mp4",R.drawable.mp4);
        attachImage.put(".wma",R.drawable.wma);
        attachImage.put(".gif",R.drawable.gif);
        attachImage.put(".xlsx",R.drawable.xlsx);
        attachImage.put(".xls",R.drawable.xls);
        attachImage.put(".zip",R.drawable.zip);
        attachImage.put(".rar",R.drawable.rar);
        return attachImage;
    }
}
