package com.bhagwad.habit;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

import com.bhagwad.habit.HabitDefinitions.HabitColumns;
import com.bhagwad.habit.HabitEntry.HabitEntryListener;

public class HabitList extends Activity implements HabitEntryListener, LoaderCallbacks<Cursor> {
	
	public static final int CREATE_HABIT = 0;
	public static final int EDIT_HABIT = 1;
	public static final String CREATE_EDIT = "create_edit";
	
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
					setStatistics((RelativeLayout) view.getParent());
				}
				return false;
			}

			private void setStatistics(RelativeLayout parent) {
				
				TextView txtHabitName = (TextView) parent.findViewById(R.id.textView_habit_list_name);
				String habitName = txtHabitName.getText().toString();
				
				int[] streakArray = Utilities.getArrayStatistics(habitName, HabitList.this);
				
				int longestStreak = streakArray[0];
				int mostRecentStreak = streakArray[1];
				
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(HabitList.this); 
				String habitLength = sharedPref.getString("habit_length_days", "21"); 
				
				TextView txtLatestStreak = (TextView) parent.findViewById(R.id.textView_latest_streak);
				TextView txtLongestStreak = (TextView) parent.findViewById(R.id.textView_longest_streak);
				
				txtLatestStreak.setText("Latest Streak: " + mostRecentStreak);
				txtLongestStreak.setText("Longest Streak: " + longestStreak);
				
				ProgressBar pbLatest = (ProgressBar) parent.findViewById(R.id.progressBar_latest_streak);
				int percentRecentStreak = (mostRecentStreak*100)/Integer.valueOf(habitLength); 
				
				pbLatest.setProgress(percentRecentStreak);
				
				TextView txtLatestPercent = (TextView) parent.findViewById(R.id.textView_latest_percent);
				txtLatestPercent.setText(percentRecentStreak + "%");
				
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
					break;
					
				case R.id.menu_habit_edit:
					
					Bundle args = new Bundle();
					args.putInt(CREATE_EDIT, EDIT_HABIT);
					args.putLong(HabitColumns._ID, longSelectedId);
					
					HabitEntry habitEntryDialog = new HabitEntry();
					habitEntryDialog.setArguments(args);
					habitEntryDialog.show(getFragmentManager(), "habit_entry_dialogue");
					mode.finish();

					break;
					
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
			
			Bundle args = new Bundle();
			args.putInt(CREATE_EDIT, CREATE_HABIT);
			
			HabitEntry habitEntryDialog = new HabitEntry();
			habitEntryDialog.setArguments(args);
			habitEntryDialog.show(getFragmentManager(), "habit_entry_dialogue");
			break;
			
		case R.id.menu_settings:
			startActivityForResult(new Intent(this, SettingsActivity.class), 1);
			break;
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