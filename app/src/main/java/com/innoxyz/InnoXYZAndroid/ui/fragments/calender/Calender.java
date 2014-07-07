package com.innoxyz.InnoXYZAndroid.ui.fragments.calender;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.innoxyz.InnoXYZAndroid.R;
import com.innoxyz.InnoXYZAndroid.ui.activities.DetailActivity;
import com.innoxyz.InnoXYZAndroid.ui.commands.ActivityCommand;
import com.innoxyz.InnoXYZAndroid.ui.fragments.Calendar07.CalendarEvent07;
import com.innoxyz.InnoXYZAndroid.ui.fragments.common.BaseFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;

/**
 * Created by dingwei on 14-3-23.
 */
public class Calender extends BaseFragment {
    // 生成日历，外层容器
    private LinearLayout layContent = null;
    private ArrayList<DateWidgetDayCell> days = new ArrayList<DateWidgetDayCell>();

    // 日期变量
    public static Calendar calStartDate = Calendar.getInstance();
    private Calendar calToday = Calendar.getInstance();
    private Calendar calCalendar = Calendar.getInstance();
    private Calendar calSelected = Calendar.getInstance();

    // 当前操作日期
    private int iMonthViewCurrentMonth = 0;
    private int iMonthViewCurrentYear = 0;
    private int iFirstDayOfWeek = Calendar.MONDAY;

    private int Calendar_Width = 0;
    private int Cell_Width = 0;

    // 页面控件
    TextView Top_Date = null;
    Button btn_pre_month = null;
    Button btn_next_month = null;
    TextView arrange_text = null;
    LinearLayout mainLayout = null;
    LinearLayout arrange_layout = null;

    // 数据源
    ArrayList<String> Calendar_Source = null;
    Hashtable<Integer, Integer> calendar_Hashtable = new Hashtable<Integer, Integer>();
    Boolean[] flag = null;
    Calendar startDate = null;
    Calendar endDate = null;
    int dayvalue = -1;

    public static int Calendar_WeekBgColor = 0;
    public static int Calendar_DayBgColor = 0;
    public static int isHoliday_BgColor = 0;
    public static int unPresentMonth_FontColor = 0;
    public static int isPresentMonth_FontColor = 0;
    public static int isToday_BgColor = 0;
    public static int special_Reminder = 0;
    public static int common_Reminder = 0;
    public static int Calendar_WeekFontColor = 0;

    String UserName = "";
   //SimpleObservedData<UserInfo> info = new SimpleObservedData<UserInfo>(new UserInfo());

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

	@Override
	public void onResume() {
		super.onResume();
		getActivity().getActionBar().setTitle("个人日历");
	}

	//当创建fragment时系统调用此方法。在其中你必须初始化fragment的基础组件们。可参考activity的说明。
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
       /* try {
            Intent i = new Intent();
            ComponentName cn = null;
            if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
                cn = new ComponentName("com.android.calendar",
                        "com.android.calendar.LaunchActivity");

            } else {
                cn = new ComponentName("com.google.android.calendar",
                        "com.android.calendar.LaunchActivity");
            }
            i.setComponent(cn);
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
            Log.e("ActivityNotFoundException", e.toString());
        }*/
       /* FragmentManager fragmentManager=getFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();*/



    }

//要学会使用ationbar上面的控件的使用的。
//现在已经基本上不在下面显示了，都改为上面的显示了。不过popupwindow这个方法处理的效果才是最好的。
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.calender_action,menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String[] items = {"今天","最近一周"};
		if(item.getItemId() == R.id.calender_setting) {
			new AlertDialog.Builder(getActivity()).setTitle("请点击获取相应的日历事件").setItems(items,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						//case 0: new FragmentCommand(Calender.class, CalendarEvent.class, Calender.this.getActivity(), getArguments(), null).Execute();
						//case 0: new FragmentCommand(Calender.class, CalendarEvent.class, Calender.this.getActivity(), null, null).Execute();
						case 0: new ActivityCommand(DetailActivity.class, CalendarEvent.class, Calender.this.getActivity(),null, null).Execute();
							Log.e("dialog", "点击第一个位置"); break;
						case 1:
							//new FragmentCommand(Calender.class, CalendarSubEvents01.class, Calender.this.getActivity(), getArguments(), null).Execute();
							new ActivityCommand(DetailActivity.class, CalendarEvent07.class, Calender.this.getActivity(),null, null).Execute();
							Log.e("dialog", "点击第2个位置"); break;
						default:break;
					}
				}
			}).show();
		}
		return super.onOptionsItemSelected(item);
	}



	//系统在fragment要画自己的界面时调用（在真正显示之前）此方法。这个方法必须返回frament的layout的根控件。如果这个fragment不提供界面，那它应返回null。
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        {
            super.onCreate(savedInstanceState);
       /* try {
            Intent i = new Intent();
            ComponentName cn = null;
            if (Integer.parseInt(Build.VERSION.SDK) >= 8) {
                cn = new ComponentName("com.android.calendar",
                        "com.android.calendar.LaunchActivity");

            } else {
                cn = new ComponentName("com.google.android.calendar",
                        "com.android.calendar.LaunchActivity");
            }
            i.setComponent(cn);
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            // TODO: handle exception
            Log.e("ActivityNotFoundException", e.toString());
        }*/
       /* FragmentManager fragmentManager=getFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();*/


            // 获得屏幕宽和高，并計算出屏幕寬度分七等份的大小
            WindowManager windowManager = getActivity().getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            int screenWidth = display.getWidth();
            Calendar_Width = screenWidth;
            Cell_Width = Calendar_Width / 7 + 1;

            // 制定布局文件，并设置属性
            mainLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(
                    R.layout.fragment_calendar, null);
            // mainLayout.setPadding(2, 0, 2, 0);
            getActivity().setContentView(mainLayout);

            // 声明控件，并绑定事件
            Top_Date = (TextView) getActivity().findViewById(R.id.Top_Date);
            btn_pre_month = (Button) getActivity().findViewById(R.id.btn_pre_month);
            btn_next_month = (Button) getActivity().findViewById(R.id.btn_next_month);
            btn_pre_month.setOnClickListener(new Pre_MonthOnClickListener());
            btn_next_month.setOnClickListener(new Next_MonthOnClickListener());

            // 计算本月日历中的第一天(一般是上月的某天)，并更新日历
            calStartDate = getCalendarStartDate();
            mainLayout.addView(generateCalendarMain());
            DateWidgetDayCell daySelected = updateCalendar();

            if (daySelected != null)
                daySelected.requestFocus();

            LinearLayout.LayoutParams Param1 = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);

            //ScrollView view = new ScrollView(this);
            ScrollView view = new ScrollView(this.getActivity());
            arrange_layout = createLayout(LinearLayout.VERTICAL);
            arrange_layout.setPadding(5, 2, 0, 0);
            //arrange_text = new TextView(this);
            arrange_text = new TextView(this.getActivity());
            mainLayout.setBackgroundColor(Color.WHITE);
            arrange_text.setTextColor(Color.BLACK);
            arrange_text.setTextSize(18);
            arrange_layout.addView(arrange_text);

            startDate = GetStartDate();
            calToday = GetTodayDate();

            endDate = GetEndDate(startDate);
            view.addView(arrange_layout, Param1);
            mainLayout.addView(view);

            // 新建线程
            new Thread() {
                @Override
                public void run() {
                    int day = GetNumFromDate(calToday, startDate);

                    if (calendar_Hashtable != null
                            && calendar_Hashtable.containsKey(day)) {
                        dayvalue = calendar_Hashtable.get(day);
                    }
                }

            }.start();

            Calendar_WeekBgColor = this.getResources().getColor(
                    R.color.Calendar_WeekBgColor);
            Calendar_DayBgColor = this.getResources().getColor(
                    R.color.Calendar_DayBgColor);
            isHoliday_BgColor = this.getResources().getColor(
                    R.color.isHoliday_BgColor);
            unPresentMonth_FontColor = this.getResources().getColor(
                    R.color.unPresentMonth_FontColor);
            isPresentMonth_FontColor = this.getResources().getColor(
                    R.color.isPresentMonth_FontColor);
            isToday_BgColor = this.getResources().getColor(R.color.isToday_BgColor);
            special_Reminder = this.getResources()
                    .getColor(R.color.specialReminder);
            common_Reminder = this.getResources().getColor(R.color.commonReminder);
            Calendar_WeekFontColor = this.getResources().getColor(
                    R.color.Calendar_WeekFontColor);
        }
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    protected String GetDateShortString(Calendar date) {
        String returnString = date.get(Calendar.YEAR) + "/";
        returnString += date.get(Calendar.MONTH) + 1 + "/";
        returnString += date.get(Calendar.DAY_OF_MONTH);

        return returnString;
    }

    // 得到当天在日历中的序号
    private int GetNumFromDate(Calendar now, Calendar returnDate) {
        Calendar cNow = (Calendar) now.clone();
        Calendar cReturnDate = (Calendar) returnDate.clone();
        setTimeToMidnight(cNow);
        setTimeToMidnight(cReturnDate);

        long todayMs = cNow.getTimeInMillis();
        long returnMs = cReturnDate.getTimeInMillis();
        long intervalMs = todayMs - returnMs;
        int index = millisecondsToDays(intervalMs);

        return index;
    }

    private int millisecondsToDays(long intervalMs) {
        return Math.round((intervalMs / (1000 * 86400)));
    }

    private void setTimeToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    // 生成布局
    private LinearLayout createLayout(int iOrientation) {
       // LinearLayout lay = new LinearLayout(this);
        LinearLayout lay = new LinearLayout(this.getActivity());
        lay.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        lay.setOrientation(iOrientation);

        return lay;
    }

    // 生成日历头部
    private View generateCalendarHeader() {
        LinearLayout layRow = createLayout(LinearLayout.HORIZONTAL);
        // layRow.setBackgroundColor(Color.argb(255, 207, 207, 205));

        for (int iDay = 0; iDay < 7; iDay++) {
           // DateWidgetDayHeader day = new DateWidgetDayHeader(this, Cell_Width,
            DateWidgetDayHeader day = new DateWidgetDayHeader(this.getActivity(), Cell_Width,
                    35);

            final int iWeekDay = DayStyle.getWeekDay(iDay, iFirstDayOfWeek);
            day.setData(iWeekDay);
            layRow.addView(day);
        }

        return layRow;
    }

    // 生成日历主体
    private View generateCalendarMain() {
        layContent = createLayout(LinearLayout.VERTICAL);
        // layContent.setPadding(1, 0, 1, 0);
        layContent.setBackgroundColor(Color.argb(255, 105, 105, 103));
        layContent.addView(generateCalendarHeader());
        days.clear();

        for (int iRow = 0; iRow < 6; iRow++) {
            layContent.addView(generateCalendarRow());
        }

        return layContent;
    }

    // 生成日历中的一行，仅画矩形
    private View generateCalendarRow() {
        LinearLayout layRow = createLayout(LinearLayout.HORIZONTAL);

        for (int iDay = 0; iDay < 7; iDay++) {
            //DateWidgetDayCell dayCell = new DateWidgetDayCell(this, Cell_Width,
            DateWidgetDayCell dayCell = new DateWidgetDayCell(this.getActivity(), Cell_Width,
                    Cell_Width);
            dayCell.setItemClick(mOnDayCellClick);
            days.add(dayCell);
            layRow.addView(dayCell);
        }

        return layRow;
    }

    // 设置当天日期和被选中日期
    private Calendar getCalendarStartDate() {
        calToday.setTimeInMillis(System.currentTimeMillis());
        calToday.setFirstDayOfWeek(iFirstDayOfWeek);

        if (calSelected.getTimeInMillis() == 0) {
            calStartDate.setTimeInMillis(System.currentTimeMillis());
            calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
        } else {
            calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
            calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);
        }

        UpdateStartDateForMonth();
        return calStartDate;
    }

    // 由于本日历上的日期都是从周一开始的，此方法可推算出上月在本月日历中显示的天数
    private void UpdateStartDateForMonth() {
        iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
        iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
        calStartDate.set(Calendar.DAY_OF_MONTH, 1);
        calStartDate.set(Calendar.HOUR_OF_DAY, 0);
        calStartDate.set(Calendar.MINUTE, 0);
        calStartDate.set(Calendar.SECOND, 0);
        // update days for week
        UpdateCurrentMonthDisplay();
        int iDay = 0;
        int iStartDay = iFirstDayOfWeek;

        if (iStartDay == Calendar.MONDAY) {
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;
            if (iDay < 0)
                iDay = 6;
        }

        if (iStartDay == Calendar.SUNDAY) {
            iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
            if (iDay < 0)
                iDay = 6;
        }

        calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);
    }

    // 更新日历
    private DateWidgetDayCell updateCalendar() {
        DateWidgetDayCell daySelected = null;
        boolean bSelected = false;
        final boolean bIsSelection = (calSelected.getTimeInMillis() != 0);
        final int iSelectedYear = calSelected.get(Calendar.YEAR);
        final int iSelectedMonth = calSelected.get(Calendar.MONTH);
        final int iSelectedDay = calSelected.get(Calendar.DAY_OF_MONTH);
        calCalendar.setTimeInMillis(calStartDate.getTimeInMillis());

        for (int i = 0; i < days.size(); i++) {
            final int iYear = calCalendar.get(Calendar.YEAR);
            final int iMonth = calCalendar.get(Calendar.MONTH);
            final int iDay = calCalendar.get(Calendar.DAY_OF_MONTH);
            final int iDayOfWeek = calCalendar.get(Calendar.DAY_OF_WEEK);
            DateWidgetDayCell dayCell = days.get(i);

            // 判断是否当天
            boolean bToday = false;

            if (calToday.get(Calendar.YEAR) == iYear) {
                if (calToday.get(Calendar.MONTH) == iMonth) {
                    if (calToday.get(Calendar.DAY_OF_MONTH) == iDay) {
                        bToday = true;
                    }
                }
            }

            // check holiday
            boolean bHoliday = false;
            if ((iDayOfWeek == Calendar.SATURDAY)
                    || (iDayOfWeek == Calendar.SUNDAY))
                bHoliday = true;
            if ((iMonth == Calendar.JANUARY) && (iDay == 1))
                bHoliday = true;

            // 是否被选中
            bSelected = false;

            if (bIsSelection)
                if ((iSelectedDay == iDay) && (iSelectedMonth == iMonth)
                        && (iSelectedYear == iYear)) {
                    bSelected = true;
                }

            dayCell.setSelected(bSelected);

            // 是否有记录
            boolean hasRecord = false;

            if (flag != null && flag[i] == true && calendar_Hashtable != null
                    && calendar_Hashtable.containsKey(i)) {
                // hasRecord = flag[i];
                hasRecord = Calendar_Source.get(calendar_Hashtable.get(i))
                        .contains(UserName);
            }

            if (bSelected)
                daySelected = dayCell;

            dayCell.setData(iYear, iMonth, iDay, bToday, bHoliday,
                    iMonthViewCurrentMonth, hasRecord);

            calCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        layContent.invalidate();

        return daySelected;
    }

    // 更新日历标题上显示的年月
    private void UpdateCurrentMonthDisplay() {
        String date = calStartDate.get(Calendar.YEAR) + "年"
                + (calStartDate.get(Calendar.MONTH) + 1) + "月";
        Top_Date.setText(date);
    }

    // 点击上月按钮，触发事件
    class Pre_MonthOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            arrange_text.setText("");
            calSelected.setTimeInMillis(0);
            iMonthViewCurrentMonth--;

            if (iMonthViewCurrentMonth == -1) {
                iMonthViewCurrentMonth = 11;
                iMonthViewCurrentYear--;
            }

            calStartDate.set(Calendar.DAY_OF_MONTH, 1);
            calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
            calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
            calStartDate.set(Calendar.HOUR_OF_DAY, 0);
            calStartDate.set(Calendar.MINUTE, 0);
            calStartDate.set(Calendar.SECOND, 0);
            calStartDate.set(Calendar.MILLISECOND, 0);
            UpdateStartDateForMonth();

            startDate = (Calendar) calStartDate.clone();
            endDate = GetEndDate(startDate);

            // 新建线程
            new Thread() {
                @Override
                public void run() {

                    int day = GetNumFromDate(calToday, startDate);

                    if (calendar_Hashtable != null
                            && calendar_Hashtable.containsKey(day)) {
                        dayvalue = calendar_Hashtable.get(day);
                    }
                }
            }.start();

            updateCalendar();

	        for(DateWidgetDayCell cell:days){
		        cell.invalidate();
	        }
        }

    }

    // 点击下月按钮，触发事件
    class Next_MonthOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            arrange_text.setText("");
            calSelected.setTimeInMillis(0);
            iMonthViewCurrentMonth++;

            if (iMonthViewCurrentMonth == 12) {
                iMonthViewCurrentMonth = 0;
                iMonthViewCurrentYear++;
            }

            calStartDate.set(Calendar.DAY_OF_MONTH, 1);
            calStartDate.set(Calendar.MONTH, iMonthViewCurrentMonth);
            calStartDate.set(Calendar.YEAR, iMonthViewCurrentYear);
            UpdateStartDateForMonth();

            startDate = (Calendar) calStartDate.clone();
            endDate = GetEndDate(startDate);

            // 新建线程
            new Thread() {
                @Override
                public void run() {
                    int day = 5;

                    if (calendar_Hashtable != null
                            && calendar_Hashtable.containsKey(day)) {
                        dayvalue = calendar_Hashtable.get(day);
                    }
                }
            }.start();
  //这里更新的是每一个单元格的里面的内容，但是并没有刷新每一个单元格。
            updateCalendar();
 //这里就是强制刷新他的每一个单元格。
	        for(DateWidgetDayCell cell:days){
		        cell.invalidate();
	        }
        }
    }

    // 点击日历，触发事件
    private DateWidgetDayCell.OnItemClick mOnDayCellClick = new DateWidgetDayCell.OnItemClick() {
        public void OnClick(DateWidgetDayCell item) {
            calSelected.setTimeInMillis(item.getDate().getTimeInMillis());
            int day = GetNumFromDate(calSelected, startDate);

            if (calendar_Hashtable != null
                    && calendar_Hashtable.containsKey(day)) {
                arrange_text.setText(Calendar_Source.get(calendar_Hashtable
                        .get(day)));
            } else {
                arrange_text.setText("暂无数据记录");
            }

            item.setSelected(true);
            updateCalendar();
        }
    };

    public Calendar GetTodayDate() {
        Calendar cal_Today = Calendar.getInstance();
        cal_Today.set(Calendar.HOUR_OF_DAY, 0);
        cal_Today.set(Calendar.MINUTE, 0);
        cal_Today.set(Calendar.SECOND, 0);
        cal_Today.setFirstDayOfWeek(Calendar.MONDAY);

        return cal_Today;
    }

    // 得到当前日历中的第一天
    public Calendar GetStartDate() {
        int iDay = 0;
        Calendar cal_Now = Calendar.getInstance();
        cal_Now.set(Calendar.DAY_OF_MONTH, 1);
        cal_Now.set(Calendar.HOUR_OF_DAY, 0);
        cal_Now.set(Calendar.MINUTE, 0);
        cal_Now.set(Calendar.SECOND, 0);
        cal_Now.setFirstDayOfWeek(Calendar.MONDAY);

        iDay = cal_Now.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY;

        if (iDay < 0) {
            iDay = 6;
        }

        cal_Now.add(Calendar.DAY_OF_WEEK, -iDay);

        return cal_Now;
    }

    public Calendar GetEndDate(Calendar startDate) {
        // Calendar end = GetStartDate(enddate);
        Calendar endDate = Calendar.getInstance();
        endDate = (Calendar) startDate.clone();
        endDate.add(Calendar.DAY_OF_MONTH, 41);
        return endDate;
    }
}
