package com.innoxyz.InnoXYZAndroid.data.remote.response;

/**
 * Created by shenw on 14-5-20.
 */
public class DocumentDownloadResponseHandler extends TextResponseHandler{

    @Override
    public boolean OnResponseContent(int responseCode, String content) throws Exception {
        return false;
    }
}
