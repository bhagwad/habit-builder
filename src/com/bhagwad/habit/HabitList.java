package com.bhagwad.habit;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bhagwad.habit.HabitDefinitions.HabitColumns;

public class HabitList extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habit_list);
        
        ListView listViewHabit = (ListView) findViewById(R.id.listview_habit);
        
        String[] items = new String[] {"Item 1", "Item 2", "Item 3"};
        ArrayAdapter<String> adapter =
          new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        
        listViewHabit.setAdapter(adapter);
        
        listViewHabit.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivity(new Intent(HabitList.this, HabitDetails.class));
				
			}
		});
        
        getContentResolver().query(HabitColumns.CONTENT_URI_HABITS, new String[] {HabitColumns.HABIT_NAME}, null, null, null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
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
}