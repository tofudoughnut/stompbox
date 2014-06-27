package com.example.stompbox.network;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.stompbox.R;
import com.example.stompbox.utils.ConnUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Button btn;
	private TextView display;
	private Thread httpThread = null;
	private Handler handler = null;
	StringBuilder buff = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.network_activity_main);
		handler = new Handler();
		display = (TextView)findViewById(R.id.textView1);
		btn = (Button)findViewById(R.id.button1);
		btn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				httpThread = new Thread(){
					public void run(){
						final byte[] respBodyByte = ConnUtil.startHttp("http://dione.techfirm.co.jp/cgi-bin/lab/sendJson.cgi", null, null);
						String jsonStr = new String(respBodyByte);
						buff = new StringBuilder();
						try {
							JSONArray jsonRoot = new JSONArray(jsonStr);
							int rootSize = jsonRoot.length();
							for(int i = 0; i < rootSize; ++i){
								JSONObject jsonObj = jsonRoot.getJSONObject(i);
								Iterator<String> keyIterator = jsonObj.keys();
								while(keyIterator.hasNext()){
									String key = keyIterator.next();
									buff.append(key).append(":").append(jsonObj.getString(key)).append("\n");
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						handler.post(new Runnable(){
							public void run(){
								display.setText(buff.toString());
							}
						});
					}
				};
				httpThread.start();
			}
		});
	}
}
