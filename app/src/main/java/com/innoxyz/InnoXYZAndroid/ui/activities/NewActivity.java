package com.innoxyz.InnoXYZAndroid.ui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.ui.commands.ActivityCommand;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.WithDoneAction;


public class NewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        //actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.background_black));
        //actionBar.setDisplayHomeAsUpEnabled(false);

        Bundle bundle = getIntent().getExtras();
        Class<? extends BaseFragment> toFragmentClass = (Class<? extends BaseFragment>)bundle.getSerializable(ActivityCommand.defaultFragment);

        //Log.e("aaaaaaaa", "NewActivity" + toFragmentClass.toString());

        if(toFragmentClass != null){
            try{
                BaseFragment baseFragment = toFragmentClass.newInstance();
                baseFragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .add(R.id.frame_content, baseFragment, "CURRENT_FRAGMENT")
                        .commit();
            }
            catch (Exception e){

            }

        }



//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_done) {
            WithDoneAction withDoneAction = (WithDoneAction)getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT");
            if(withDoneAction != null){
                withDoneAction.actionDone();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
