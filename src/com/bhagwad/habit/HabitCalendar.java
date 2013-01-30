package com.bhagwad.habit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bhagwad.habit.HabitDefinitions.HabitColumns;

public class HabitCalendar extends Activity {

	Calendar mCalendar;
	GridView mHabitGrid;
	GridView mGridViewWeekdays;
	TextView monthName;
	String habitName;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.habit_calendar);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		if (savedInstanceState != null)
			mCalendar = (Calendar) savedInstanceState.getSerializable("Calendar");
		else
			mCalendar = Calendar.getInstance();
		
		habitName = getIntent().getExtras().getString(HabitColumns.HABIT_NAME);
		mHabitGrid = (GridView) findViewById(R.id.gridview_habit_calendar);

		monthName = (TextView) findViewById(R.id.textView_monthname);
		
		getActionBar().setTitle(habitName);

		setUpMonthName();
		setUpWeekdays();
		setUpDates();
		setUpNavigation();

	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("Calendar", mCalendar);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void setUpNavigation() {
		ImageButton previousMonth = (ImageButton) findViewById(R.id.imageButton_month_previous);
		ImageButton nextMonth = (ImageButton) findViewById(R.id.imageButton_month_next);

		previousMonth.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				changeCalendar(HabitDefinitions.PREVIOUS_MONTH);
	
			}
		});

		nextMonth.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				changeCalendar(HabitDefinitions.NEXT_MONTH);
				
				
			}
		});

	}
	
	protected void changeCalendar(int navigation) {
		
		if (navigation == HabitDefinitions.PREVIOUS_MONTH) {
			
			mCalendar.add(Calendar.MONTH, -1);
			setUpDates();
			setUpMonthName();
			setAnimation(HabitDefinitions.PREVIOUS_MONTH);
			
		} else {
			
			mCalendar.add(Calendar.MONTH, 1);
			setUpDates();
			setUpMonthName();
			
			setAnimation(HabitDefinitions.NEXT_MONTH);
			
		}
		
	}

	private void setAnimation(int navigation) {
		
		AnimationSet mAnimationSet = new AnimationSet(true);
		mAnimationSet.setDuration(400);
		TranslateAnimation mTranslateAnimation = null;
		
		switch (navigation) {
		case HabitDefinitions.NEXT_MONTH:
			mTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
			break;
		case HabitDefinitions.PREVIOUS_MONTH:
			mTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		}
		
		
		mTranslateAnimation.setDuration(1500);
		mAnimationSet.addAnimation(mTranslateAnimation);
		mHabitGrid.startAnimation(mAnimationSet);
		mHabitGrid.setX(0);
		
	}

	private void setUpDates() {
		mHabitGrid.setAdapter(new HabitGrid(this, mCalendar));
		mHabitGrid.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				
				if ( isNotBlank(v)) {
					
					/*If it's a date greater than today's date, ignore*/
					
					if (greaterThanToday(v))
						return;
					
					
					toggleStar(v);
					updateDatabaseOccurrence(v);
					
					/*Update the widget*/
					
					SharedPreferences prefs = getSharedPreferences(HabitWidgetConfiguration.PREFS, 0);
					int mAppWidgetId = Integer.valueOf(prefs.getString(HabitWidgetConfiguration.PREFS_PREFIX_KEY+habitName, null));
					Utilities.updateWidget(mAppWidgetId, habitName, HabitCalendar.this);
					
				}
				
			}

			private boolean greaterThanToday(View v) {
				TextView txtDate = (TextView) v.findViewById(R.id.textView_date);
				TextView txtMonth = (TextView) v.findViewById(R.id.textView_month);
				TextView txtYear = (TextView) v.findViewById(R.id.textView_year);
				
				String dateString = txtDate.getText() + "/" + txtMonth.getText() + "/" + txtYear.getText();
				SimpleDateFormat dateFormat = new SimpleDateFormat(HabitDefinitions.DATE_FORMAT);
				Date date = null;
				try {
					
					date = dateFormat.parse(dateString);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Date todaysDate = new Date();
				if (date.after(todaysDate))
					return true;
				else
					return false;
				
			}

			private boolean isNotBlank(View v) {
				TextView t = (TextView) v.findViewById(R.id.textView_date);
				if (t.getVisibility() == View.VISIBLE)
					return true;
				else
					return false;
			}
		});

	}

	protected void updateDatabaseOccurrence(View v) {
		ImageView star = (ImageView) v.findViewById(R.id.imageView_star);
		TextView textViewDate = (TextView) v.findViewById(R.id.textView_date);
		TextView textViewMonth = (TextView) v.findViewById(R.id.textView_month);
		TextView textViewYear = (TextView) v.findViewById(R.id.textView_year);
		
		/*Create the date string. We add one to the month because January returns 0 insted of 1*/
		
		
		String dateText = textViewDate.getText().toString()+"/"+textViewMonth.getText().toString() + "/" + textViewYear.getText().toString(); 
		
		/*If the star is visible, we enter a date. If not, we delete it*/
		
		if (star.getVisibility() == View.VISIBLE) {
			ContentValues cv = new ContentValues();
			cv.put(HabitColumns.HABIT_NAME, habitName);
			cv.put(HabitColumns.HABIT_OCCURRENCE, dateText);
			getContentResolver().insert(HabitColumns.CONTENT_URI_RECORDS, cv);
		} else {
			getContentResolver().delete(HabitColumns.CONTENT_URI_RECORDS, HabitColumns.HABIT_NAME + "=? AND " + HabitColumns.HABIT_OCCURRENCE + "=?", new String[] {habitName, dateText});
		}
	}

	protected void toggleStar(View v) {
		ImageView star = (ImageView) v.findViewById(R.id.imageView_star);
		if (star.getVisibility() == View.INVISIBLE) {
			star.setVisibility(View.VISIBLE);
			doAnimations(star);
		}
		else {
			star.clearAnimation();
			star.setVisibility(View.INVISIBLE);	
		}
	}
	
	private void doAnimations(View mViewToScale) {

		ScaleAnimation mScaleAnimation = new ScaleAnimation(1, 3, 1, 3,
				mViewToScale.getWidth() / 2,
				mViewToScale.getHeight() / 2);
		mScaleAnimation
				.setInterpolator(new AccelerateInterpolator(2.0f));
		mScaleAnimation.setFillAfter(true);
		mScaleAnimation.setDuration(350);

		mScaleAnimation.setRepeatCount(1);
		mScaleAnimation.setRepeatMode(Animation.REVERSE);

		mViewToScale.setAnimation(mScaleAnimation);

	}

	private void setUpMonthName() {

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
		Calendar mDisplayedMonth;
		HashMap <String, Boolean> occurencesInMonths;
		
		public HabitGrid(Context ctxt, Calendar c) {
			context = ctxt;
			mDisplayedMonth = c;
			mOffset = getOffset();
			occurencesInMonths = new HashMap <String, Boolean> ();
			populateWithStars();

		}

		private void populateWithStars() {
			
			/*Querying the database for each date would be inefficient. Instead, I get all the data
			for the month in one go, create a hashmap with dates in the month that are in it
			and consult it whenever we need to choose whether to display a star or not*/
			
			String dateMatching = "/"+(mDisplayedMonth.get(Calendar.MONTH)+1)+"/"+mCalendar.get(Calendar.YEAR);
			
			
			Cursor c = getContentResolver().query(HabitColumns.CONTENT_URI_RECORDS, new String[] {HabitColumns.HABIT_OCCURRENCE}, HabitColumns.HABIT_NAME + "=? AND " + HabitColumns.HABIT_OCCURRENCE + " LIKE ?", new String[] {habitName, "%"+dateMatching}, null);
			if (c.moveToFirst()) {
				
				while (!c.isAfterLast()) {
					
					String entry = c.getString(c.getColumnIndexOrThrow(HabitColumns.HABIT_OCCURRENCE));
					occurencesInMonths.put(entry, true);
					c.moveToNext();
				}

			}
					
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

				v = (RelativeLayout) convertView;

			v.setMinimumHeight(parent.getHeight() / 6);
			
			renderDate(v, position);
			return v;
		}

		private void renderDate(View v, int position) {
			
			/*Store the month and year values as well so we can retrieve them when the user
			clicks on something. Better encapsulation this way.*/
			
			String month = String.valueOf(mDisplayedMonth.get(Calendar.MONTH)+1);
			String year = String.valueOf(mDisplayedMonth.get(Calendar.YEAR));
			
			TextView textViewDate = (TextView) v.findViewById(R.id.textView_date);
			TextView textViewMonth = (TextView) v.findViewById(R.id.textView_month);
			TextView textViewYear = (TextView) v.findViewById(R.id.textView_year);
			ImageView star = (ImageView)v.findViewById(R.id.imageView_star);
			
			textViewMonth.setText(month);
			textViewYear.setText(year);
			
			String date = getDateFromPosition(position); 
			textViewDate.setText(date);
			
			if (date.equals(""))
				return;
			
			if (!date.equals(""))
				textViewDate.setVisibility(View.VISIBLE);
			
			String fullDateString = date+ "/" + month + "/" + year;
			
			if (occurencesInMonths.get(fullDateString) != null)
				star.setVisibility(View.VISIBLE);
			
			/*If it's today's date, color it a different color*/
			
			SimpleDateFormat dateFormat = new SimpleDateFormat(HabitDefinitions.DATE_FORMAT);
			
			Date itemDate = null;
			try {
				itemDate = dateFormat.parse(fullDateString);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (isToday(itemDate)) {
				v.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
			}
		}

		private boolean isToday(Date date) {
			
			Calendar today = Calendar.getInstance();
			today.setTime(new Date());
			Calendar otherday = Calendar.getInstance();
			otherday.setTime(date);
			return otherday.get(Calendar.YEAR) == today.get(Calendar.YEAR)
			&& otherday.get(Calendar.MONTH) == today.get(Calendar.MONTH)
			&& otherday.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
		}

		private String getDateFromPosition(int position) {

			/* Return nothing if the first days are blank */

			if (position < mOffset) {
				return "";
			}

			/*
			 * Subtract the offset from the position. Add 1 because the first
			 * day of the month is 1 and not 0
			 */

			int date = position - mOffset + 1;

			/* Return nothing if it goes beyond the maximum days in the month */

			if (date > mDisplayedMonth.getActualMaximum(Calendar.DATE))
				return "";

			return String.valueOf(date);
		}

		private int getOffset() {

			/*
			 * Create a copy of the Calendar, set it's date to the first and see
			 * which day it falls on. Return the offset by subtracting one
			 * converting it into a zero based index to match the gridview
			 * positions
			 */

			Calendar tempCal = Calendar.getInstance();
			tempCal.set(Calendar.MONTH, mDisplayedMonth.get(Calendar.MONTH));
			tempCal.set(Calendar.YEAR, mDisplayedMonth.get(Calendar.YEAR));
			tempCal.set(Calendar.DATE, 1);

			return tempCal.get(Calendar.DAY_OF_WEEK) - 1;
		}

	}
}