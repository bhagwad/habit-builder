package com.bhagwad.habit;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.bhagwad.habit.HabitDefinitions.HabitColumns;

public class HabitWidgetConfiguration extends Activity {

	Spinner mHabitSpinner;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.habit_widget_configuration);
		
		String columns[] = {HabitColumns.HABIT_NAME};
		mHabitSpinner = (Spinner) findViewById(R.id.spinner_habit_list);
		Cursor c = getContentResolver().query(HabitColumns.CONTENT_URI_HABITS, null, null, null, null);
		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, c, columns, new int[] {android.R.id.text1}, 0);
		mHabitSpinner.setAdapter(mAdapter);
		
		
	}
}
