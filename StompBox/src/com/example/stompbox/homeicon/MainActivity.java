package com.example.stompbox.homeicon;

import com.example.stompbox.R;
import com.example.stompbox.utils.DateUtil;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RemoteViews;

public class MainActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homeicon_activity_main);
		findViewById(R.id.button1).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(MainActivity.this);
				RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_main);
				ComponentName widget = new ComponentName(MainActivity.this, MyWidgetProvider.class);
				views.setTextViewText(R.id.textView1, "pressed:" + DateUtil.formatDate(System.currentTimeMillis()));
				appWidgetManager.updateAppWidget(widget, views);
			}
		});
	}
}
