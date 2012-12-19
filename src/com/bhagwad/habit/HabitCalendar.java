package com.bhagwad.habit;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HabitCalendar extends Activity {

	Calendar mCalendar;
	GridView mHabitGrid;
	GridView mGridViewWeekdays;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.habit_calendar);

		mCalendar = Calendar.getInstance();
		
		setUpMonthName();
		setUpWeekdays();
		setUpDates();
		setUpNavigation();

	}

	private void setUpNavigation() {
		// TODO Auto-generated method stub
		
	}

	private void setUpDates() {
		mHabitGrid = (GridView) findViewById(R.id.gridview_habit_calendar);
		mHabitGrid.setAdapter(new HabitGrid(this));

	}

	private void setUpMonthName() {

		TextView monthName = (TextView) findViewById(R.id.textView_monthname);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy");
		simpleDateFormat.setCalendar(mCalendar);

		monthName.setText(simpleDateFormat.format(mCalendar.getTime()));
	}

	private void setUpWeekdays() {
		mGridViewWeekdays = (GridView) findViewById(R.id.gridview_habit_calendar_weekdays);
		String[] weekdaysArray = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
		ArrayAdapter<String> weekdaysArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, weekdaysArray);
		mGridViewWeekdays.setAdapter(weekdaysArrayAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.habit_details, menu);
		return true;
	}

	private class HabitGrid extends BaseAdapter {

		Context context;
		int mOffset;

		public HabitGrid(Context ctxt) {
			context = ctxt;
			mOffset = getOffset();
		}

		public int getCount() {
			return 42;
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			View v;
			if (convertView == null) {

				LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = layoutInflater.inflate(R.layout.habit_calendar_item, null);
			} else

				v = (LinearLayout) convertView;

			v.setMinimumHeight(parent.getHeight()/6);
			TextView t = (TextView) v.findViewById(R.id.textView_date);
			t.setText(getDateFromPosition(position) + "");

			return v;
		}

		private String getDateFromPosition(int position) {
			
			/* Return nothing if the first days are blank */
			
			if (position < mOffset) {
				return "";
			}

			/*Subtract the offset from the position. Add 1 because the first day of the month
			 * is 1 and not 0 */
			
			int date = position - mOffset + 1;
			
			/*Return nothing if it goes beyond the maximum days in the month*/
			
			if (date > mCalendar.getActualMaximum(Calendar.DATE))
				return "";

			return String.valueOf(date);
		}

		private int getOffset() {
			
			/*Create a copy of the Calendar, set it's date to the first and see which day
			it falls on. Return the offset by subtracting one converting it into a zero
			based index to match the gridview positions */
			
			Calendar tempCal = Calendar.getInstance();
			tempCal.set(Calendar.MONTH, mCalendar.get(Calendar.MONTH));
			tempCal.set(Calendar.YEAR, mCalendar.get(Calendar.YEAR));
			tempCal.set(Calendar.DATE, 1);

			return tempCal.get(Calendar.DAY_OF_WEEK)-1;
		}

	}
}
