package com.example.stompbox.alarm;

import com.example.stompbox.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	public static final String ACTION_ALARM = "stompbox.intent.action.ALARM_NOTICE";
	public static final String PARAM_ALARM_MSG = "com.example.stompbox.alarm_msg";
	private static final int ALARM_WAIT_SEC = 10;
	private Intent noticeIntent = null;
	private PendingIntent alarmIntent = null;
	private TextView display;
	private Button clockButton;
	private AlarmManager am = null;
	private boolean isAlarmSet = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_activity_main);
		display = (TextView)findViewById(R.id.textView1);
		// AlarmManagerから自身に向けて投げてもらうIntent。
		noticeIntent = new Intent(this, AlarmReceiver.class);
		noticeIntent.setAction(ACTION_ALARM);
		noticeIntent.putExtra(PARAM_ALARM_MSG, "時間です");
		// アラームが登録済みか確認する。登録が無ければnullが返る。
		// http://stackoverflow.com/questions/4556670/how-to-check-if-alarmmamager-already-has-an-alarm-set
		isAlarmSet = (PendingIntent.getBroadcast(this, 0, noticeIntent, PendingIntent.FLAG_NO_CREATE) != null);
		// AlarmManagerにセットするPendingIntent。
		alarmIntent = PendingIntent.getBroadcast(this, 0, noticeIntent, 0);
		// AlarmManager。
		am=(AlarmManager)getSystemService(ALARM_SERVICE);
		// 時間表示開始ボタン。
		clockButton=(Button)findViewById(R.id.button1);
		clockButton.setText(isAlarmSet?"アラーム解除":"アラーム設定(" + ALARM_WAIT_SEC + "秒後)");
		clockButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(isAlarmSet){	// セットされていたらキャンセルする。
					am.cancel(alarmIntent);
				}else{			// セットされていなかったら登録する。
					am.set(AlarmManager.RTC, System.currentTimeMillis() + ALARM_WAIT_SEC * 1000, alarmIntent);
				}
				isAlarmSet = !isAlarmSet;
				clockButton.setText(isAlarmSet?"アラーム解除":"アラーム設定(" + ALARM_WAIT_SEC + "秒後)");
			}
		});
		Intent intent = getIntent();
		if(intent != null){
			display.setText(intent.getStringExtra(PARAM_ALARM_MSG));
		}
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(ACTION_ALARM.equals(intent.getAction())){
			display.setText(intent.getStringExtra(PARAM_ALARM_MSG));
		}
	}
}
