package com.innoxyz.InnoXYZAndroid.ui.commands;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.ui.fragments.FragmentSwitcher;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.fragments.message.MessageList;
import com.innoxyz.InnoXYZAndroid.ui.fragments.notify.NotifyList;
import com.innoxyz.InnoXYZAndroid.ui.fragments.project.ProjectList;
import com.innoxyz.InnoXYZAndroid.ui.fragments.user.Home;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-22
 * Time: 下午1:12
 * To change this template use File | Settings | File Templates.
 */
public class FragmentCommand extends Command {

    static Map<Class<? extends BaseFragment>, BaseFragment> fragmentMap ;
    static{
        fragmentMap = new HashMap<Class<? extends BaseFragment>, BaseFragment>();
        fragmentMap.put(NotifyList.class,null);
        fragmentMap.put(ProjectList.class,null);
        fragmentMap.put(MessageList.class,null);
        fragmentMap.put(Home.class,null);
    }

    Class<? extends BaseFragment> fromFragmentClass;
    Class<? extends BaseFragment> toFragmentClass;

    public FragmentCommand(Class<? extends BaseFragment> fromFragment,Class<? extends BaseFragment> toFragment, Activity activity, Bundle bundle, Handler handler){
        super(activity, bundle, handler);

        fromFragmentClass = fromFragment;
        toFragmentClass = toFragment;
    }

    @Override
    protected void executeDo() {
        BaseFragment fromInstance,toInstance;
        try {
            //new FragmentSwitcher(activity, null, toFragmentClass.newInstance(), R.id.detail_container, bundle).replace();
            new FragmentSwitcher(activity, null, toFragmentClass.newInstance(), R.id.frame_content, bundle).replace();

//
//            if(fragmentMap.containsKey(fromFragmentClass)){
//
//                //detach
//                if(fragmentMap.containsKey(toFragmentClass)){
//                    if((toInstance = fragmentMap.get(toFragmentClass)) != null){
//                        //detach attach
//                        new FragmentSwitcher(activity, fragmentMap.get(fromFragmentClass), toInstance, R.id.frame_content, bundle).detachAttach();
//                    }
//                    else{
//                        //detach add
//                        toInstance = toFragmentClass.newInstance();
//                        fragmentMap.put(toFragmentClass,toInstance);
//                        new FragmentSwitcher(activity, fragmentMap.get(fromFragmentClass), toInstance, R.id.frame_content, bundle).detachAdd();
//                    }
//                }
//                else{
//                    //detach add
//                    new FragmentSwitcher(activity, fragmentMap.get(fromFragmentClass), toFragmentClass.newInstance(), R.id.frame_content, bundle).detachAdd();
//                }
//            }
//            else{
//                fromInstance = (BaseFragment)activity.getFragmentManager().findFragmentById(R.id.frame_content);
//                //remove
//                if(fragmentMap.containsKey(toFragmentClass)){
//                    if((toInstance = fragmentMap.get(toFragmentClass)) != null){
//                        //remove attach
//                        new FragmentSwitcher(activity, fromInstance, toInstance, R.id.frame_content, bundle).removeAttach();
//                    }
//                    else{
//                        //remove add
//                        toInstance = toFragmentClass.newInstance();
//                        fragmentMap.put(toFragmentClass,toInstance);
//                        new FragmentSwitcher(activity, fromInstance, toInstance, R.id.frame_content, bundle).replace();
//                    }
//                }
//                else{
//                    //remove add
//                    new FragmentSwitcher(activity, fromInstance, toFragmentClass.newInstance(), R.id.frame_content, bundle).replace();
//                }
//
//            }
        } catch (Exception e) {  }
    }
}
