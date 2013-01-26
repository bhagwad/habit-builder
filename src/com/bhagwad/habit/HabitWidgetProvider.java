package com.bhagwad.habit;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class HabitWidgetProvider extends AppWidgetProvider {
	
	private static String WIDGET_ID = "widget_id";
	private static String WIDGET_TEXT = "widget_text";
	
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		
		Log.d("Debug", "In here");

		// Start the service for each widget that has to be updated
		
		for (int i = 0; i < appWidgetIds.length; i++) {
			
			int id = appWidgetIds[i];
			
			// Get the text stored for each widget
			SharedPreferences prefs = context.getSharedPreferences(HabitWidgetConfiguration.PREFS, 0);
			String habitName = prefs.getString(HabitWidgetConfiguration.PREFS_PREFIX_KEY+id, null);
			
			Utilities.updateWidget(id, habitName, context);
			
		}
	}


}
