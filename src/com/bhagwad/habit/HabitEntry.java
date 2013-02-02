package com.bhagwad.habit;

import android.app.DialogFragment;
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

	// We'll use this interface in HabitList to pass stuff back
	public interface HabitEntryListener {
		void onHabitEntry(String mHabitName, String mHabitGoal);
	}

	public HabitEntry() {

		// Empty constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		//Log.d("Debug", getArguments().getInt(HabitList.CREATE_EDIT, 5) + "");
		
		View v = inflater.inflate(R.layout.habit_entry, container);
		mSaveHabit = (Button) v.findViewById(R.id.button_habit_save);
		mCancel = (Button) v.findViewById(R.id.button_habit_cancel);

		mHabitName = (EditText) v.findViewById(R.id.editText_habit_name);
		mHabitGoal = (EditText) v.findViewById(R.id.edittext_habit_goal);

		textErrorMessage = (TextView) v.findViewById(R.id.textView_habit_error);

		mSaveHabit.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		getDialog().setTitle("Create a New Habit");

		return v;
	}

	public void onClick(View v) {
		if (v.getId() == R.id.button_habit_cancel)
			this.dismiss();

		String mHabitNameText = mHabitName.getText().toString();

		if (mHabitNameText.equals("")) {

			textErrorMessage.setText("You must enter a habit");
			textErrorMessage.setVisibility(View.VISIBLE);
			return;
		}

		if (duplicateHabit(mHabitNameText)) {
			textErrorMessage.setText("That habit already exists!");
			textErrorMessage.setVisibility(View.VISIBLE);
			return;
		}

		// We can make this cast because HabitList implements HabitEntryListener
		HabitEntryListener mActivity = (HabitEntryListener) getActivity();

		mActivity.onHabitEntry(mHabitNameText, mHabitGoal.getText().toString());
		this.dismiss();
	}

	private boolean duplicateHabit(String mHabitNameText) {

		Cursor c = getActivity().getContentResolver().query(HabitColumns.CONTENT_URI_HABITS, new String[] { HabitColumns.HABIT_NAME },
				HabitColumns.HABIT_NAME + " =?", new String[] { mHabitNameText }, null);
		if (c.getCount() == 0)
			return false;
		else
			return true;
	}

}