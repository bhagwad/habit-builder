package com.bhagwad.habit;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class HabitEntry extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habit_entry);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.habit_entry, menu);
        return true;
    }
}
