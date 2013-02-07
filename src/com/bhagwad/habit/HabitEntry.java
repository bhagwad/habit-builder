package com.bhagwad.habit;

import android.app.DialogFragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bhagwad.habit.HabitDefinitions.HabitColumns;

public class HabitEntry extends DialogFragment implements OnClickListener {

	Button mSaveHabit;
	Button mCancel;
	EditText mHabitName;
	EditText mHabitGoal;
	TextView textErrorMessage;

	boolean editHabit = false;
	long mId;
	String mOldHabitName = "";
	String mOldGoalName = "";

	// We'll use this interface in HabitList to pass stuff back
	public interface HabitEntryListener {
		void onHabitEntry(String mHabitName, String mHabitGoal);
	}

	public HabitEntry() {

		// Empty constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.habit_entry, container);
		mSaveHabit = (Button) v.findViewById(R.id.button_habit_save);
		mCancel = (Button) v.findViewById(R.id.button_habit_cancel);

		mHabitName = (EditText) v.findViewById(R.id.editText_habit_name);
		mHabitGoal = (EditText) v.findViewById(R.id.edittext_habit_goal);

		textErrorMessage = (TextView) v.findViewById(R.id.textView_habit_error);

		if (getArguments().getInt(HabitList.CREATE_EDIT, 5) == HabitList.EDIT_HABIT) {
			editHabit = true;
			populateHabitEntry();
		} else
			getDialog().setTitle("Create a New Habit");

		mSaveHabit.setOnClickListener(this);
		mCancel.setOnClickListener(this);

		return v;
	}

	private void populateHabitEntry() {

		mId = getArguments().getLong(HabitColumns._ID);

		Cursor c = getActivity().getContentResolver().query(HabitColumns.CONTENT_URI_HABITS,
				new String[] { HabitColumns.HABIT_NAME, HabitColumns.HABIT_GOAL }, HabitColumns._ID + "=?",
				new String[] { String.valueOf(mId) }, null);
		if (c.moveToFirst()) {
			mOldHabitName = c.getString(c.getColumnIndexOrThrow(HabitColumns.HABIT_NAME));
			mOldGoalName = c.getString(c.getColumnIndexOrThrow(HabitColumns.HABIT_GOAL));
		}

		mHabitName.setText(mOldHabitName);
		mHabitGoal.setText(mOldGoalName);
		getDialog().setTitle("Edit Habit");
	}

	public void onClick(View v) {
		if (v.getId() == R.id.button_habit_cancel)
			this.dismiss();

		String habitNameText = mHabitName.getText().toString();

		if (habitNameText.equals("")) {

			textErrorMessage.setText("You must enter a habit");
			textErrorMessage.setVisibility(View.VISIBLE);
			return;
		}

		if (duplicateHabit(habitNameText)) {
			textErrorMessage.setText("That habit already exists!");
			textErrorMessage.setVisibility(View.VISIBLE);
			return;
		}
		
		if (editHabit == true) {
			changeHabit(habitNameText, mHabitGoal.getText().toString());
			this.dismiss();
		}

		// We can make this cast because HabitList implements HabitEntryListener
		HabitEntryListener mActivity = (HabitEntryListener) getActivity();

		mActivity.onHabitEntry(habitNameText, mHabitGoal.getText().toString());
		this.dismiss();

	}



	private void changeHabit(String habitNameText, String habitGoalText) {
		
		ContentValues cv = new ContentValues();
		cv.put(HabitColumns.HABIT_NAME, habitNameText);
		cv.put(HabitColumns.HABIT_GOAL, habitGoalText);
		
		getActivity().getContentResolver().update(HabitColumns.CONTENT_URI_HABITS, cv, HabitColumns._ID + " =?", new String[] {String.valueOf(mId)});
		this.dismiss();
	}

	private boolean duplicateHabit(String mHabitNameText) {

		Cursor c;
		
		if (editHabit == false)
			c = getActivity().getContentResolver().query(HabitColumns.CONTENT_URI_HABITS, new String[] { HabitColumns.HABIT_NAME },
				HabitColumns.HABIT_NAME + " =?", new String[] { mHabitNameText }, null);
		
		else
			
			c = getActivity().getContentResolver().query(HabitColumns.CONTENT_URI_HABITS, new String[] { HabitColumns.HABIT_NAME },
					HabitColumns.HABIT_NAME + " =? AND " + HabitColumns._ID + " <>?", new String[] { mHabitNameText, String.valueOf(mId) }, null);
		
		if (c.getCount() == 0)
			return false;
		else
			return true;
	}

}