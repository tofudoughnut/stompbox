package com.example.stompbox.homeicon;

import com.example.stompbox.R;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class MyWidgetProvider extends AppWidgetProvider {
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		for(int appWidgetId : appWidgetIds){
			Intent activityIntent = new Intent(context, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
			RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_main);
			views.setOnClickPendingIntent(R.id.button1, pendingIntent);
			appWidgetManager.updateAppWidget(appWidgetId, views);
		}
	}
}
