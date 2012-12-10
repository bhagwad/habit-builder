package com.bhagwad.habit;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class HabitEntry extends DialogFragment implements OnClickListener {
	
	Button mSaveHabit;
	Button mCancel;
	EditText mHabitName;
	EditText mHabitGoal;
	
	
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
		
		mSaveHabit.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		getDialog().setTitle("Create a New Habit");
		
		
		
		return v;
	}

	public void onClick(View v) {
		if (v.getId() == R.id.button_habit_cancel)
			this.dismiss();
		
		// We can make this cast because HabitList implements HabitEntryListener
		HabitEntryListener mActivity = (HabitEntryListener) getActivity();
		
		mActivity.onHabitEntry(mHabitName.getText().toString(), mHabitGoal.getText().toString());
		this.dismiss();
	}
	
}