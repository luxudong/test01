package com.innoxyz.InnoXYZAndroid.ui.activities;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Toast;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.data.remote.AddressURIs;
import com.innoxyz.InnoXYZAndroid.data.remote.StringRequestBuilder;
import com.innoxyz.InnoXYZAndroid.data.remote.response.JsonResponseHandler;
import com.innoxyz.InnoXYZAndroid.data.runtime.SimpleObservedData;
import com.innoxyz.InnoXYZAndroid.data.runtime.beans.user.UserInfo;
import com.innoxyz.InnoXYZAndroid.data.runtime.interfaces.IDataObserver;
import com.innoxyz.InnoXYZAndroid.global.InnoXYZApp;
import com.innoxyz.InnoXYZAndroid.ui.customviews.TabWithNumber;
import com.innoxyz.InnoXYZAndroid.ui.fragments.TopMenubar;
import com.innoxyz.InnoXYZAndroid.ui.fragments.message.MessageList;
import com.innoxyz.InnoXYZAndroid.ui.fragments.notify.NotifyList;
import com.innoxyz.InnoXYZAndroid.ui.fragments.project.ProjectList;
import com.innoxyz.InnoXYZAndroid.ui.fragments.user.Home;
import com.innoxyz.InnoXYZAndroid.ui.services.GetUnreadNumService;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-7-30
 * Time: 下午3:56
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends Activity {

    ActionBar actionBar;
    private long exitTime = 0;

    static TabWithNumber t =null;

    TopMenubar topMenubar = null;
    Tab notifies, projects, mails, user;

    boolean isResumed = false;
    boolean isNeedUpdateNtf = false;
    boolean isNeedUpdateMsg = false;

    int unReadNotifies = 0;
    int unReadMails = 0;

    private BroadcastReceiver unReadCountReceiver;
    private Intent unReadNumIntent;

    public void setTopMenubar(TopMenubar topMenubar){
        this.topMenubar = topMenubar;
    }
    public TopMenubar getTopMenubar(){
        return this.topMenubar;
    }

    SimpleObservedData<UserInfo> info;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get current userId
        info = new SimpleObservedData<UserInfo>(new UserInfo());
        info.registerObserver(new IDataObserver() {
            @Override
            public void update(Object o) {
                if (info.getData().detail != null) {
                    InnoXYZApp.getApplication().setCurrentUserId(info.getData().detail.id);
                }

            }
        });
        new StringRequestBuilder(this).setRequestInfo(AddressURIs.USER_PROFILE)
                .setOnResponseListener(new JsonResponseHandler(info, UserInfo.class, Arrays.asList("data")))
                .request();

        actionBar = getActionBar();

        //actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.background_black));
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        notifies = actionBar.newTab()/*.setText("待办").setIcon(R.drawable.icon_event)*/;
        projects = actionBar.newTab()/*.setText("项目").setIcon(R.drawable.icon_project)*/;
        mails = actionBar.newTab()/*.setText("私信").setIcon(R.drawable.icon_mail)*/;
        user = actionBar.newTab()/*.setText("用户").setIcon(R.drawable.icon_user)*/;
        t = new TabWithNumber(this, "首页");
        Log.e("original", t.toString());

        notifies.setCustomView(t);
        projects.setCustomView(new TabWithNumber(this, "项目"));
        mails.setCustomView(new TabWithNumber(this, "私信"));
        user.setCustomView(new TabWithNumber(this, "用户"));

        notifies.setTabListener(new MyTabListener<NotifyList>(this, "NotifyList", NotifyList.class));
        projects.setTabListener(new MyTabListener<ProjectList>(this, "ProjectList", ProjectList.class));
        mails.setTabListener(new MyTabListener<MessageList>(this, "MessageList", MessageList.class));
        user.setTabListener(new MyTabListener<Home>(this, "Home", Home.class));

        actionBar.addTab(notifies);
        actionBar.addTab(projects);
        actionBar.addTab(mails);
        actionBar.addTab(user);

        try {
            InnoXYZApp app = InnoXYZApp.getApplication();
            app.getRuntimeDataManager().Add(this, false);
            //app.getTheClient().Login("swordzz9154@gmail.com", "222222");
            //app.getTheClient().Login("laborish@gmail.com", "123456");

            setContentView(R.layout.activity_main);

            //new FragmentSwitcher(this, null, new TopMenubar(), R.id.upper_tab_frame, null).add();
            //new FragmentCommand(NetworkImageTest.class, this, null, null).Execute();
            //new FragmentCommand(null, NotifyList.class, this, null, null).Execute();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //NameCardBox box = new NameCardBox(this);
        //((FrameLayout) findViewById(R.id.frame_content)).addView(box);

//        //start the service
        unReadNumIntent = new Intent(this, GetUnreadNumService.class);
//        startService(unReadNumIntent);


        //reg the broadcast receiver
        IntentFilter intentFilter = new IntentFilter(GetUnreadNumService.UNREAD_COUNT);
        unReadCountReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //extract our message from intent
                int unNtf = intent.getIntExtra(GetUnreadNumService.UNREAD_NOTIFIES, -1);
                if (unNtf != -1) {
                    unReadNotifies = unNtf;
                    //Log.e("","get unReadNotifies = " + unReadNotifies);
                    if (isResumed) {
                        ((TabWithNumber) notifies.getCustomView()).setNumber(unReadNotifies);
                    } else {
                        isNeedUpdateNtf = true;
                    }
                }

                int unMsg = intent.getIntExtra(GetUnreadNumService.UNREAD_MAILS, -1);
                if (unMsg != -1) {
                    unReadMails = unMsg;
                    //Log.e("","get unReadMails = " + unReadMails);
                    if (isResumed) {
                        ((TabWithNumber) mails.getCustomView()).setNumber(unReadMails);
                    } else {
                        isNeedUpdateMsg = true;
                    }
                }
            }
        };
        //registering our receiver
        this.registerReceiver(unReadCountReceiver, intentFilter);


    }

        @Override
    public void onResume (){
        super.onResume();
            //getActionBar().setDisplayShowHomeEnabled(false);
            Log.e("MainActivity", "onResume");
        //start the service
        startService(unReadNumIntent);

        if(isNeedUpdateNtf){
            isNeedUpdateNtf = false;
            ((TabWithNumber)notifies.getCustomView()).setNumber(unReadNotifies);
        }
        if(isNeedUpdateMsg){
            isNeedUpdateMsg = false;
            ((TabWithNumber)mails.getCustomView()).setNumber(unReadMails);
        }

        isResumed = true;
    }

    @Override
    public void onPause() {
        Log.e("MainActivity", "onPause");
        super.onPause();

        stopService(unReadNumIntent);

        isResumed = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
        Log.e("MainActivity", "onDestroy");

        unregisterReceiver(unReadCountReceiver);

        InnoXYZApp.getApplication().getRuntimeDataManager().Release(this);


    }


    private static Boolean isExit = false;
    private static Boolean hasTask = false;
    Timer tExit = new Timer();
    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            isExit = false;
            hasTask = true;
        }
    };
        public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(isExit == false){
                isExit = true;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                if(!hasTask){
                    tExit.schedule(task, 2000);
                }
            }else{
                finish();
                System.exit(0);
            }
        }
        return false;
    }

    class MyTabListener<T extends Fragment> implements TabListener
    {
        // 接收每个Tab对应的Fragment，操作
        private Fragment fragment;
        private final Activity activity;
        private final String tag;
        private final Class<T> clazz;


        public MyTabListener(Activity activity, String tag, Class<T> clz)
        {
            this.activity = activity;
            this.tag = tag;
            this.clazz = clz;
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft)
        {
            //Log.e("",tab.getText() + "onTabReselected called");
            //ft.replace(R.id.frame_content, fragment, null);

//            //remove the tabfragment and replace the subfragment
//            ft.remove(fragment);
//            //fragment = Fragment.instantiate(activity, clazz.getName());
//            ft.replace(R.id.frame_content, fragment, tag);

            ft.detach(fragment);
            ft.attach(fragment);
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft)
        {
            //Log.e("",tab.getText() + "onTabSelected called");
            //ft.replace(R.id.frame_content, fragment, null);

            // Check if the fragment is already initialized
            if(fragment == null){
                // If not, instantiate and add it to the activity
                fragment = Fragment.instantiate(activity, clazz.getName());
                ft.add(R.id.frame_content, fragment, tag);
            }
            else{
                // If it exists, simply attach it in order to show it
                ft.attach(fragment);
            }
            //View view = getLayoutInflater().inflate(R.layout.customviews_tab_with_number,null);
           // TabWithNumber view = (TabWithNumber)tab.getCustomView();
            //Log.e("after",view.toString());
            //t.setTextColor(0x34a0c8);
            //t.setTextColor(0x000000);
            //view.setTextColor(0x34a0c8);

            //LayoutInflater inflater = LayoutInflater.from(getBaseContext());
           // View view = (TextView)inflater.inflate(R.layout.customviews_tab_with_number,null);
            //TextView textView = (TextView)view.findViewById(R.id.tab_badge_text);

            //textView.setTextColor(0x34a0c8);
            MainActivity.this.getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft)
        {
            //Log.e("",tab.getText() + "onTabUnselected called");
            ft.detach(fragment);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);    //To change body of overridden methods use File | Settings | File Templates.
        //getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return true;
    }

    private List<Runnable> runnables = new LinkedList<Runnable>();
    public void addOnFocusChangeOperation(Runnable runnable) {
        synchronized (runnables) {
            runnables.add(runnable);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);    //To change body of overridden methods use File | Settings | File Templates.
        // This is meant to be OnLoad
        List<Runnable> local_runnables = runnables;
        Log.i(this.getClass().getName(), "Focus changed! " + local_runnables.size() + " runnable(s) are about to run.");
        synchronized (runnables) {
            runnables = new LinkedList<Runnable>();
        }
        for(Runnable run : local_runnables) {
            run.run();
        }
    }



//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK
//                && event.getAction() == KeyEvent.ACTION_DOWN){
//            if((System.currentTimeMillis()-exitTime) > 2000){
//                Toast.makeText(getApplicationContext(), "再按一次 退出", Toast.LENGTH_SHORT).show();
//                exitTime = System.currentTimeMillis();
//            } else {
//                finish();
//                System.exit(0);
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}