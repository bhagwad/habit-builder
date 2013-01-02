package com.bhagwad.habit;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;

import com.bhagwad.habit.HabitDefinitions.HabitColumns;

public class Utilities {

	public static int[] getArrayStatistics(String habitName, Context ctxt) {

		/* Create the hashmap table first with HISTORY_LENGTH entries to be sure */

		HashMap<String, Boolean> hashOccurences = Utilities.generateHashMap(habitName, ctxt);

		SimpleDateFormat dateFormat = new SimpleDateFormat(HabitDefinitions.DATE_FORMAT);
		Calendar todaysDateCal = Calendar.getInstance();
		
		/*Get the longest streak so far*/
		
		Cursor c = ctxt.getContentResolver().query(HabitColumns.CONTENT_URI_HABITS, new String[] {HabitColumns.HABIT_LONGEST}, HabitColumns.HABIT_NAME + "=?", new String[] {habitName}, null);
		c.moveToFirst();

		/* Iterate through previous dates one by one going back HISTORY_LENGTH */

		int mostRecentStreak = 0;
		boolean onMostRecentStreak = true;
		int longestStreak = c.getInt(c.getColumnIndexOrThrow(HabitColumns.HABIT_LONGEST));
		int currentStreak = 0;
		boolean isDateChecked;

		/*
		 * Create a specific string for yesterday's date to handle the special
		 * cases ofyesterday and today. It's messy but our expectations seem
		 * arbitrary so no logicto put into it
		 */

		String todaysDate = dateFormat.format(todaysDateCal.getTime());
		todaysDateCal.add(Calendar.DATE, -1);
		String yesterdaysDate = dateFormat.format(todaysDateCal.getTime());

		if (hashOccurences.get(todaysDate) == null && hashOccurences.get(yesterdaysDate) != null) {
			mostRecentStreak = 1;
			currentStreak = 1;
			if (longestStreak == 0)
				longestStreak = 1;

		} else if (hashOccurences.get(todaysDate) != null && hashOccurences.get(yesterdaysDate) != null) {

			mostRecentStreak = 2;
			currentStreak = 2;
			if (longestStreak < 2)
				longestStreak = 2;

			

		} else if (hashOccurences.get(todaysDate) != null && hashOccurences.get(yesterdaysDate) == null) {

			mostRecentStreak = 1;
			if (longestStreak == 0)
				longestStreak = 1;
			//longestStreak = 1;
			onMostRecentStreak = false;

		} else if (hashOccurences.get(todaysDate) == null && hashOccurences.get(yesterdaysDate) == null) {

			onMostRecentStreak = false;

		}
		

		todaysDateCal.add(Calendar.DATE, -1);
		String currentDate = dateFormat.format(todaysDateCal.getTime());
		
		while (hashOccurences.get(currentDate) != null) {
			mostRecentStreak++;
			
			if (mostRecentStreak > longestStreak)
				longestStreak = mostRecentStreak;
			
			todaysDateCal.add(Calendar.DATE, -1);
			currentDate = dateFormat.format(todaysDateCal.getTime());
		}

//		for (int i = 1; i <= HabitDefinitions.HISTORY_LENGTH; i++) {
//
//			todaysDateCal.add(Calendar.DATE, -1);
//			String currentDate = dateFormat.format(todaysDateCal.getTime());
//
//			if (hashOccurences.get(currentDate) != null)
//				isDateChecked = true;
//			else
//				isDateChecked = false;
//
//			/* The statistics are calculated here */
//
//			if (isDateChecked == true) {
//
//				if (onMostRecentStreak == true)
//					mostRecentStreak++;
//
//				currentStreak++;
//
//				if (currentStreak > longestStreak)
//					longestStreak = currentStreak;
//
//			} else {
//
//				currentStreak = 0;
//				onMostRecentStreak = false;
//			}
//
//		}

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
}
