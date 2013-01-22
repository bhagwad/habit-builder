package com.bhagwad.habit;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.bhagwad.habit.HabitDefinitions.HabitColumns;

public class HabitWidgetConfiguration extends Activity {

	Spinner mHabitSpinner;
	Button mSaveWidget;
	Button mCancel;
	int mAppWidgetId;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.habit_widget_configuration);
		
		// If something goes wrong, this screen should be cancelled
		setResult(RESULT_CANCELED);
		
		String columns[] = {HabitColumns.HABIT_NAME};
		mHabitSpinner = (Spinner) findViewById(R.id.spinner_habit_list);
		Cursor c = getContentResolver().query(HabitColumns.CONTENT_URI_HABITS, null, null, null, null);
		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, c, columns, new int[] {android.R.id.text1}, 0);
		mHabitSpinner.setAdapter(mAdapter);
		
		mSaveWidget = (Button)findViewById(R.id.button_habit_spinner_save);
		mSaveWidget.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
	}
}
