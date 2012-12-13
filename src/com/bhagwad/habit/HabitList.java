package com.bhagwad.habit;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.bhagwad.habit.HabitDefinitions.HabitColumns;
import com.bhagwad.habit.HabitEntry.HabitEntryListener;

public class HabitList extends Activity implements HabitEntryListener, LoaderCallbacks<Cursor> {
	
	SimpleCursorAdapter mAdapter;
	ListView listViewHabit;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.habit_list);
		
		setUpListViewAndAdapter();

		getLoaderManager().initLoader(0, null, this);

	}

	private void setUpListViewAndAdapter() {
		
		initializeListViewAndSetupCab();
		
		String columns[] = {HabitColumns.HABIT_NAME};
		mAdapter = new SimpleCursorAdapter(this, R.layout.habit_list_item, null, columns, new int[] {R.id.textView_habit_list_name}, 0);
		
		// Set the first row
		LayoutInflater li = this.getLayoutInflater();
		listViewHabit.addHeaderView(li.inflate(R.layout.list_add_new_habit, null));
		listViewHabit.setAdapter(mAdapter);

		listViewHabit.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				if (view.getId() == R.id.linearlayout_new_row) {
					Log.d("Debug", "First item clicked");
					HabitEntry habitEntryDialog = new HabitEntry();
					habitEntryDialog.show(getFragmentManager(), "habit_entry_dialogue");
				}
				
			}
		});
		
	}

	private void initializeListViewAndSetupCab() {
		listViewHabit = (ListView) findViewById(R.id.listview_habit);
		listViewHabit.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listViewHabit.setLongClickable(true);
		
		listViewHabit.setMultiChoiceModeListener(new MultiChoiceModeListener() {
			
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				return false;
			}
			
			public void onDestroyActionMode(ActionMode mode) {
				// TODO Auto-generated method stub
				
			}
			
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.habit_list_cab, menu);
				return true;
			}
			
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				// TODO Auto-generated method stub
				return false;
			}
			
			public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.menu_new_habit:
			startActivity(new Intent(this, HabitEntry.class));

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