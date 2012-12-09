package com.bhagwad.habit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
				//startActivity(new Intent(HabitList.this, HabitDetails.class));
				HabitEntry testDialog = new HabitEntry();
				testDialog.show(getFragmentManager(), "test");
			}
		});

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
    
    private class HabitEntry extends DialogFragment {
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    		builder.setMessage("Test Dialog box");
    		builder.setPositiveButton("Yes", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
    		
    		builder.setNegativeButton("No", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
    		
    		
    		return builder.create();
    	}
    }
}