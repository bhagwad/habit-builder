package com.bhagwad.habit;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

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
		listViewHabit.setAdapter(mAdapter);

	}

	private void initializeListViewAndSetupCab() {
		
		
		
		listViewHabit = (ListView) findViewById(R.id.listview_habit);
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