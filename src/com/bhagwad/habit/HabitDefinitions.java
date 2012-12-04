package com.bhagwad.habit;

import android.net.Uri;
import android.provider.BaseColumns;

public final class HabitDefinitions {
	
	public static final String AUTHORITY = "com.bhagwad.habit.provider";
	
	public static final String TABLE_HABITS = "habits";
	public  static final String TABLE_HABITS_RECORD = "habits_record";
	
	private HabitDefinitions() {};
	
	public static final class HabitColumns implements BaseColumns {
		
		private HabitColumns() {};
		
		public static final String HABIT_NAME = "habit_name";
		public static final String HABIT_GOAL = "habit_goal";
		public static final String HABIT_OCCURRENCE = "habit_occurrence";
		
		public static final Uri CONTENT_URI_HABITS = Uri.parse("content://" + AUTHORITY + "/" + TABLE_HABITS);
		public static final Uri CONTENT_URI_RECORDS = Uri.parse("content://" + AUTHORITY + "/" + TABLE_HABITS_RECORD);
		
		public static final String CONTENT_TYPE_HABITS = "vnd.android.cursor.dir/vnd.com.bhagwad.provider."+ TABLE_HABITS;
		
	}

}
