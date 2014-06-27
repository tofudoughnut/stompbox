package com.example.stompbox.background;

import com.example.stompbox.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private boolean isServiceRunning = false;
	private TextView display;
	private Button btn1;
	private Intent svcIntent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.background_activity_index);
		svcIntent = new Intent(MainActivity.this, MyService.class);
		btn1 = (Button)findViewById(R.id.button1);
		btn1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(isServiceRunning){
					stopService(svcIntent);
				}else{
					startService(svcIntent);
				}
				isServiceRunning = !isServiceRunning;
				btn1.setText(isServiceRunning?"�o�b�N�O���E���h������~":"�o�b�N�O���E���h�����N��");
			}
		});
		findViewById(R.id.button2).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				new MyAsyncTask().execute("John", "Paul", "George", "Ringo");
			}
		});
		// �T�[�r�X���N�����Ă��邩�`�F�b�N����B
		isServiceRunning = com.example.stompbox.utils.SysUtil.isServiceRunning(this, MyService.class.getName());
		display = (TextView)findViewById(R.id.textView1);
		btn1.setText(isServiceRunning?"�o�b�N�O���E���h������~":"�o�b�N�O���E���h�����N��");
	}
	private BroadcastReceiver internalReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if(MyService.ACTION_CLOCK.equals(intent.getAction())){
				display.setText(intent.getStringExtra(MyService.PARAM_NOW_TIME));
			}
		}
	};
	@Override
	protected void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(internalReceiver, new IntentFilter(MyService.ACTION_CLOCK));
	}
	@Override
	protected void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(internalReceiver);
	}
	private class MyAsyncTask extends AsyncTask<String, String, Integer>{
		private ProgressDialog mProgressDialog;
		// �v���O���X�_�C�A���O�J�n�B
		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(MainActivity.this);
			mProgressDialog.setMessage("�o�b�N�O���E���h������");
			mProgressDialog.show();
		}
		@Override
		protected Integer doInBackground(String... params) {
			final int LOOP_MAX = 4;
			for(int i = 0; i < LOOP_MAX; ++i){
				publishProgress((i+1) + "/" + LOOP_MAX + ":" + params[i], "extra");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return 777;
		}
		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			mProgressDialog.setMessage("�o�b�N�O���E���h������:" + values[0] + "," + values[1]);
			display.setText("�o�b�N�O���E���h������:" + values[0]);
		}
		// ���������B�Ăь��ɒʒm�B
		protected void onPostExecute(Integer result) {
			mProgressDialog.dismiss();
			display.setText("AsyncTask�I���A���ʃR�[�h:" + result);
		}
	}
}
