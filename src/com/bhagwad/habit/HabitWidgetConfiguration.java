package com.bhagwad.habit;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
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
	public static final String PREFS_PREFIX_KEY = "prefix_";
	public static String PREFS = "com.bhagwad.habit.appwidget";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.habit_widget_configuration);

		// If something goes wrong, this screen should be cancelled
		setResult(RESULT_CANCELED);
		setWidgetId();
		
		/*Create the spinner*/

		String columns[] = { HabitColumns.HABIT_NAME };
		mHabitSpinner = (Spinner) findViewById(R.id.spinner_habit_list);
		Cursor c = getContentResolver().query(HabitColumns.CONTENT_URI_HABITS, null, null, null, null);
		SimpleCursorAdapter mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, c, columns,
				new int[] { android.R.id.text1 }, 0);
		mHabitSpinner.setAdapter(mAdapter);
		
		mSaveWidget = (Button) findViewById(R.id.button_habit_spinner_save);
		mSaveWidget.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				/*The spinner returns a cursor at the given position*/
				Cursor c = (Cursor) mHabitSpinner.getSelectedItem();
				String habitName = c.getString(c.getColumnIndexOrThrow(HabitColumns.HABIT_NAME));
				Utilities.updateWidget(mAppWidgetId, habitName, HabitWidgetConfiguration.this);
				
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
				setResult(RESULT_OK, resultValue);
				finish();
				

			}
		});

		mCancel = (Button) findViewById(R.id.button_habit_spinner_cancel);
		mCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();

			}
		});

	}

	private void setWidgetId() {

		Bundle extras = getIntent().getExtras();

		// Extract the widget Id we'll need

		if (extras != null) {
			mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// If they gave us an intent without the widget id, just bail.

		if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
			finish();
		}

	}
}
