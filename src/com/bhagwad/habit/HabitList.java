package com.bhagwad.habit;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import com.bhagwad.habit.HabitDefinitions.HabitColumns;
import com.bhagwad.habit.HabitEntry.HabitEntryListener;

public class HabitList extends Activity implements HabitEntryListener, LoaderCallbacks<Cursor> {
	
	SimpleCursorAdapter mAdapter;
	ListView listViewHabit;
	long longSelectedId;
	boolean inCab = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.habit_list);
		
		setUpListViewAndAdapter();

		getLoaderManager().initLoader(0, null, this);

	}
	
	private void setUpListViewAndAdapter() {
		
		initializeListViewAndSetupCab();
		String columns[] = {HabitColumns.HABIT_NAME, HabitColumns.HABIT_GOAL};
		mAdapter = new SimpleCursorAdapter(this, R.layout.habit_list_item, null, columns, new int[] {R.id.textView_habit_list_name, R.id.textView_habit_list_goal}, 0);
		
		/*We need to perform some calculations for each line. Ideally I would do all this in "getView"
		with a custom adapter, but cursorloader works too well and I don't know how to use a
		regular Loader with a custom adapter (if it's even possible). So I'm intercepting control
		when the view is bound, making sure it runs only once for each item and returning false so that
		regular binding can occur.*/
		
		mAdapter.setViewBinder(new ViewBinder() {
			
			public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
				
				if (view.getId() == R.id.textView_habit_list_goal) {
					setStatistics((LinearLayout) view.getParent());
				}
				return false;
			}

			private void setStatistics(LinearLayout parent) {
				
				TextView txtHabitName = (TextView) parent.findViewById(R.id.textView_habit_list_name);
				TextView txtLatestStreak = (TextView) parent.findViewById(R.id.textView_latest_streak);
				TextView txtLongestStreak = (TextView) parent.findViewById(R.id.textView_longest_streak);
				
				String habitName = txtHabitName.getText().toString();
				
				/*Create the hashmap table first with HISTORY_LENGTH entries to be sure*/
				HashMap<String, Boolean> hashOccurences = generateHashMap(habitName);
				
				SimpleDateFormat dateFormat = new SimpleDateFormat(HabitDefinitions.DATE_FORMAT);
				Calendar todaysDateCal = Calendar.getInstance();
				
				/*Iterate through previous dates one by one going back HISTORY_LENGTH*/
				
				int mostRecentStreak = 0;
				boolean onMostRecentStreak = true;
				int longestStreak = 0;
				int currentStreak = 0;
				boolean isDateChecked;
				
				/*Create a specific string for yesterday's date to handle the special cases of
				 *yesterday and today. It's messy but our expectations seem arbitrary so no logic
				 *to put into it */
				
				String todaysDate = dateFormat.format(todaysDateCal.getTime());
				todaysDateCal.add(Calendar.DATE, -1);
				String yesterdaysDate = dateFormat.format(todaysDateCal.getTime());
				
				if (hashOccurences.get(todaysDate) == null && hashOccurences.get(yesterdaysDate) != null)
				{
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
				
				for (int i = 1; i<=HabitDefinitions.HISTORY_LENGTH; i++) {
					
					todaysDateCal.add(Calendar.DATE, -1);
					String currentDate = dateFormat.format(todaysDateCal.getTime());
					
					if (hashOccurences.get(currentDate) != null)
						isDateChecked = true;
					else
						isDateChecked = false;
					
					/*The statistics are calculated here*/
					
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
				
				//Log.d("Debug", habitName + " Latest Streak: " + latestStreak + " Longest streak: " + longestStreak);
				txtLatestStreak.setText("Latest Streak: " + mostRecentStreak);
				txtLongestStreak.setText("Longest Streak: " + longestStreak);
				
				ProgressBar pbLatest = (ProgressBar) parent.findViewById(R.id.progressBar_latest_streak);
				int percentRecentStreak = (mostRecentStreak*100)/HabitDefinitions.HABIT_LIMIT; 
				
				pbLatest.setProgress(percentRecentStreak);
				
			}

			private HashMap<String, Boolean> generateHashMap(String habitName) {
				
				HashMap<String, Boolean> hashOccurences = new HashMap<String, Boolean>();
				
				Cursor c = getContentResolver().query(HabitColumns.CONTENT_URI_RECORDS, new String[] {HabitColumns.HABIT_OCCURRENCE}, HabitColumns.HABIT_NAME + "=?", new String[] {habitName}, HabitColumns.HABIT_OCCURRENCE + " LIMIT " + HabitDefinitions.HISTORY_LENGTH);
				
				if (c.moveToFirst()) {
					
					while (!c.isAfterLast()) {
						
						String entry = c.getString(c.getColumnIndexOrThrow(HabitColumns.HABIT_OCCURRENCE));
						hashOccurences.put(entry, true);
						c.moveToNext();
					}

				}
				
				return hashOccurences;
			}
		});
		listViewHabit.setAdapter(mAdapter);

	}

	private void initializeListViewAndSetupCab() {
		
		
		
		listViewHabit = (ListView) findViewById(R.id.listview_habit);
		listViewHabit.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				/*Get the Habit name and pass it along. Ideally we should send the id instead,
				 * but this eliminates an additional step for our small application*/
				
				String habitName = "";
				Cursor c = getContentResolver().query(HabitColumns.CONTENT_URI_HABITS, new String[] {HabitColumns.HABIT_NAME}, HabitColumns._ID + "=?", new String[] {String.valueOf(id)}, null);
				if (c.moveToFirst()) {
					habitName = c.getString(c.getColumnIndexOrThrow(HabitColumns.HABIT_NAME));
				}
					
				
				Intent i = new Intent(HabitList.this, HabitCalendar.class);
				i.putExtra(HabitColumns.HABIT_NAME, habitName);
				startActivityForResult(i, 0);
			}
		});
		listViewHabit.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		
		listViewHabit.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// Allow the user to only select one item
				return false;
			}
			
			public void onDestroyActionMode(ActionMode mode) {
				/*
				 * We're exiting CAB mode so change the flag back to what it was
				 * */
				inCab = false;
			}
			
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.habit_list_cab, menu);
				return true;
			}
			
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				
				switch (item.getItemId()) {
				case R.id.menu_habit_delete:
					
					/*
					 * We delete longSelectedId which we set when we first came in
					 */ 
					getContentResolver().delete(HabitColumns.CONTENT_URI_HABITS, HabitColumns._ID + "=?", new String[] {String.valueOf(longSelectedId)});	
					
				}
			
				return true;
			}
			
			public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
				/* We have to keep track of the very first item that's selected.
				 * So inCab will be false the first time we come in and we assign the id
				 * to longSelected which we will be deleting if the user chooses.
				 * If the user selects more stuff after the long click, longSelected won't change
				 * because we've set it to true.
				 * */
				if (!inCab) {
					longSelectedId = id;
					inCab = true;
				}
				
			}
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getLoaderManager().restartLoader(0, null, this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.habit_list_action_bar, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menu_add:
			HabitEntry habitEntryDialog = new HabitEntry();
			habitEntryDialog.show(getFragmentManager(), "habit_entry_dialogue");

		}

		return true;
	}

	public void onHabitEntry(String mHabitName, String mHabitGoal) {
		
		ContentValues cv = new ContentValues();
		cv.put(HabitColumns.HABIT_NAME, mHabitName);
		cv.put(HabitColumns.HABIT_GOAL, mHabitGoal);
		getContentResolver().insert(HabitColumns.CONTENT_URI_HABITS, cv);
		
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(this, HabitColumns.CONTENT_URI_HABITS, null, null, null, null);

	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
		
	}

	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.swapCursor(null);
		
	}


}