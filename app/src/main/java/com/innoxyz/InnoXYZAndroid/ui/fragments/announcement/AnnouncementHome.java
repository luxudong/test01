package com.innoxyz.InnoXYZAndroid.ui.fragments.announcement;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-11-29
 * Time: 下午9:09
 * To change this template use File | Settings | File Templates.
 */
public class AnnouncementHome extends BaseFragment {

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);    //To change body of overridden methods use File | Settings | File Templates.

        //getActivity().getActionBar().setTitle(R.string.title_announcement_home);
    }

    @Override
    public void onResume(){
        super.onResume();
        getActivity().getActionBar().setTitle(R.string.title_announcement_home);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View ret = inflater.inflate(R.layout.fragment_announcement_home, container, false);

        ((TextView)ret.findViewById(R.id.announcement_home_projectname)).setText( getResources().getString(R.string.template_topichome_projectname).replace("%name%", getArguments().getString("projectName")) );

        Spanned spanned = Html.fromHtml(getArguments().getString("content"));
        ((TextView)ret.findViewById(R.id.announcement_home_content)).setText(spanned);

        return ret;

    }
}
