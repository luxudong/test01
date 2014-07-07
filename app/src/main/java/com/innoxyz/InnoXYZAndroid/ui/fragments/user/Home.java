package com.innoxyz.InnoXYZAndroid.ui.fragments.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.global.InnoXYZApp;
import com.innoxyz.InnoXYZAndroid.ui.activities.DetailActivity;
import com.innoxyz.InnoXYZAndroid.ui.activities.LoginActivity;
import com.innoxyz.InnoXYZAndroid.ui.commands.ActivityCommand;
import com.innoxyz.InnoXYZAndroid.ui.fragments.calender.Calender;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;

/**
 * Created with IntelliJ IDEA.
 * User: InnoXYZ
 * Date: 13-9-28
 * Time: 下午1:59
 * To change this template use File | Settings | File Templates.
 */
public class Home extends BaseFragment {

	@Override
	public void onResume(){
		super.onResume();
		getActivity().getActionBar().setTitle(R.string.title_user_home);
	}



	private class Item {
		int imgId;
		int nameId;
		Class<? extends BaseFragment> clazz;

		private Item(int imgId, int nameId, Class<? extends BaseFragment> clazz) {
			this.imgId = imgId;
			this.nameId = nameId;
			this.clazz = clazz;
		}
	}

	private Item[] items = new Item[]{
			new Item(R.drawable.icon_info, R.string.menu_userhome_profile, Profile.class),
			new Item(R.drawable.icon_documentlist, R.string.menu_userhome_document, null),
			// new Item(R.drawable.icon_documentlist, R.string.menu_userhome_document, DocumentList.class),
			new Item(R.drawable.icon_discusslist, R.string.menu_userhome_invite, Invite.class),
	        //new Item(R.drawable.icon_date, R.string.menu_userhome_calendar, CalendarEvents00.class),
			new Item(R.drawable.icon_date, R.string.menu_userhome_calendar, Calender.class),
			//new Item(R.drawable.icon_date, R.string.menu_userhome_calendar, CalendarEvents01.class),
			//new Item(R.drawable.icon_date, R.string.menu_userhome_calendar, CalendarSubEvents01.class),
			//new Item(R.drawable.icon_setting, R.string.menu_userhome_setting, null),
			// new Item(R.drawable.icon_setting, R.string.menu_userhome_changepass, null),
			new Item(R.drawable.icon_logout, R.string.menu_userhome_logout, null),
	};

	private View.OnClickListener clickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			Item item = (Item) view.getTag(R.id.item_object);
			if ( item.clazz != null ) {
				//new FragmentCommand(Home.class, item.clazz, getActivity(), null, null).Execute();
				new ActivityCommand(DetailActivity.class, item.clazz, Home.this.getActivity(), null, null).Execute();
			}
			if (item.nameId == R.string.menu_userhome_logout ){
				InnoXYZApp.getApplication().removeIcCookie();
				Intent i = new Intent(getActivity(), LoginActivity.class);
				startActivity(i);
				getActivity().finish();
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout ret = new LinearLayout(getActivity());
		ret.setOrientation(LinearLayout.VERTICAL);
		for(Item item : items) {
			View view = inflater.inflate(R.layout.fragment_userhome_button, container, false);
			view.setTag(R.id.item_object, item);
			((ImageView)view.findViewById(R.id.userhome_button_imageview)).setImageResource(item.imgId);
			((TextView)view.findViewById(R.id.userhome_button_textview)).setText(getResources().getString(item.nameId));
			view.setOnClickListener(clickListener);
			ret.addView(view);
		}
		return ret;
		//增加代码
	}

}
