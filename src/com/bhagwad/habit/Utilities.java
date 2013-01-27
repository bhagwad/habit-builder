package com.bhagwad.habit;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.View.OnClickListener;
import android.widget.RemoteViews;

import com.bhagwad.habit.HabitDefinitions.HabitColumns;

public class Utilities {

	public static void updateWidget(int mAppWidgetId, String habitName, Context ctxt) {
		
		int[] streakArray = Utilities.getArrayStatistics(habitName, ctxt);
		int longestStreak = streakArray[0];
		int mostRecentStreak = streakArray[1];
		
		RemoteViews views = new RemoteViews(ctxt.getPackageName(), R.layout.habit_widget_layout);
		
		Intent i = new Intent(ctxt, HabitList.class);
		PendingIntent pi = PendingIntent.getActivity(ctxt, 0, i, 0);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctxt); 
		String habitLength = sharedPref.getString("habit_length_days", "21");
		
		/*Input the habit name into the shared preferences linked with the appwidgetid*/
		
		saveTheName(ctxt, mAppWidgetId, habitName);
		
		int percentRecentStreak = (mostRecentStreak*100)/Integer.valueOf(habitLength);
		
		views.setTextViewText(R.id.textView_widget_habit_name, habitName);
		views.setProgressBar(R.id.progressBar_widget_latest_streak, 100, percentRecentStreak, false);
		views.setTextViewText(R.id.textView_widget_percentage, String.valueOf(percentRecentStreak)+"%");
		
		AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(ctxt);
		views.setOnClickPendingIntent(R.id.widget_container, pi);
		mAppWidgetManager.updateAppWidget(mAppWidgetId, views);
	}
	
	public static int[] getArrayStatistics(String habitName, Context ctxt) {

		/* Create the hashmap table first with HISTORY_LENGTH entries to be sure */

		HashMap<String, Boolean> hashOccurences = Utilities.generateHashMap(habitName, ctxt);

		SimpleDateFormat dateFormat = new SimpleDateFormat(HabitDefinitions.DATE_FORMAT);
		Calendar todaysDateCal = Calendar.getInstance();

		/* Iterate through previous dates one by one going back HISTORY_LENGTH */

		int mostRecentStreak = 0;
		boolean onMostRecentStreak = true;
		int longestStreak = 0;
		int currentStreak = 0;
		boolean isDateChecked;

		/*
		 * Create a specific string for yesterday's date to handle the special
		 * cases of yesterday and today. It's messy but our expectations seem
		 * arbitrary so no logicto put into it
		 */

		String todaysDate = dateFormat.format(todaysDateCal.getTime());
		todaysDateCal.add(Calendar.DATE, -1);
		String yesterdaysDate = dateFormat.format(todaysDateCal.getTime());

		if (hashOccurences.get(todaysDate) == null && hashOccurences.get(yesterdaysDate) != null) {
			mostRecentStreak = 1;
			longestStreak = 1;
			currentStreak = 1;

		} else if (hashOccurences.get(todaysDate) != null && hashOccurences.get(yesterdaysDate) != null) {

			mostRecentStreak = 2;
			longestStreak = 2;
			currentStreak = 2;

		} else if (hashOccurences.get(todaysDate) != null && hashOccurences.get(yesterdaysDate) == null) {

			mostRecentStreak = 1;
			longestStreak = 1;
			onMostRecentStreak = false;

		} else if (hashOccurences.get(todaysDate) == null && hashOccurences.get(yesterdaysDate) == null) {

			onMostRecentStreak = false;

		}

		for (int i = 1; i <= HabitDefinitions.HISTORY_LENGTH; i++) {

			todaysDateCal.add(Calendar.DATE, -1);
			String currentDate = dateFormat.format(todaysDateCal.getTime());

			if (hashOccurences.get(currentDate) != null)
				isDateChecked = true;
			else
				isDateChecked = false;

			/* The statistics are calculated here */

			if (isDateChecked == true) {

				if (onMostRecentStreak == true)
					mostRecentStreak++;

				currentStreak++;

				if (currentStreak > longestStreak)
					longestStreak = currentStreak;

			} else {

				currentStreak = 0;
				onMostRecentStreak = false;
			}

		}

		return new int[] { longestStreak, mostRecentStreak };

	}

	public static HashMap<String, Boolean> generateHashMap(String habitName, Context ctxt) {

		HashMap<String, Boolean> hashOccurences = new HashMap<String, Boolean>();

		Cursor c = ctxt.getContentResolver().query(HabitColumns.CONTENT_URI_RECORDS, new String[] { HabitColumns.HABIT_OCCURRENCE },
				HabitColumns.HABIT_NAME + "=?", new String[] { habitName },
				HabitColumns.HABIT_OCCURRENCE + " LIMIT " + HabitDefinitions.HISTORY_LENGTH);

		if (c.moveToFirst()) {

			while (!c.isAfterLast()) {

				String entry = c.getString(c.getColumnIndexOrThrow(HabitColumns.HABIT_OCCURRENCE));
				hashOccurences.put(entry, true);
				c.moveToNext();
			}

		}

		return hashOccurences;
	}
	
	private static void saveTheName(Context ctxt, int mAppWidgetId, String text) {
		
		SharedPreferences.Editor prefs = ctxt.getSharedPreferences(HabitWidgetConfiguration.PREFS, 0).edit();
		prefs.putString(HabitWidgetConfiguration.PREFS_PREFIX_KEY+mAppWidgetId, text);
		prefs.commit();
		
	}

}
