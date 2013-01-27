package com.bhagwad.habit;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class HabitWidgetProvider extends AppWidgetProvider {
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		// Start the service for each widget that has to be updated
		
		for (int i = 0; i < appWidgetIds.length; i++) {
			
			int id = appWidgetIds[i];
			
			// Get the text stored for each widget
			SharedPreferences prefs = context.getSharedPreferences(HabitWidgetConfiguration.PREFS, 0);
			String habitName = prefs.getString(HabitWidgetConfiguration.PREFS_PREFIX_KEY+id, null);
			
			/*Odd, but sometimes this is called even when there are no widgets. So just
			break if that happens*/
			
			if (habitName==null)
				break;
			
			Utilities.updateWidget(id, habitName, context);
			
		}
	}


}
