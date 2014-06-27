package com.example.stompbox.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {
	private static final String TAG = "MyService";
	public static final String PARAM_NOW_TIME = "com.example.stompbox.NowTime";
	public static final String ACTION_CLOCK = "stompbox.intent.action.CLOCK_PUSH";
	private Context context = null;
	private boolean isValid = true;
	private Thread thread = null;
	private Intent noticeIntent = null;
	@Override
	public void onCreate() {
		super.onCreate();
		Log.v(TAG, "onCreate");
		context = this;
		// http://stackoverflow.com/questions/16285137/is-it-safe-to-reuse-an-intent
		noticeIntent = new Intent(ACTION_CLOCK);
	}
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v(TAG, "onStartCommand");
		Toast.makeText(this, "サービス開始", Toast.LENGTH_SHORT).show();
		thread = new Thread(new Runnable(){
			@Override
			public void run() {
				while(isValid){
					try {
						Thread.sleep(1 * 1000);
					} catch (InterruptedException e) {
						Log.v(TAG, "sleep interrupted");
						break;
					}
					String nowTime = com.example.stompbox.utils.DateUtil.formatDate(System.currentTimeMillis());
					Log.v(TAG, "send clock:" + nowTime);
					noticeIntent.putExtra(PARAM_NOW_TIME, nowTime);
					LocalBroadcastManager.getInstance(context).sendBroadcast(noticeIntent);
				}
				Log.v(TAG, "exit loop");
			}
		});
		thread.start();
		Log.v(TAG, "thread started");
		return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v(TAG, "onDestroy");
		isValid = false;
		thread.interrupt();
		Toast.makeText(this, "サービス終了", Toast.LENGTH_SHORT).show();
	}
}
