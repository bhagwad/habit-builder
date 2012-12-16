package com.bhagwad.habit;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class HabitCalendar extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habit_calendar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.habit_details, menu);
        return true;
    }
}
