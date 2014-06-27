package com.example.stompbox.barcode;

import com.example.stompbox.R;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class ScanActivity extends Activity implements CamPreView.OnCodeScannedListener {
	private static final String TAG = "ScanActivity";
	private CamPreView camView;
	private TextView display;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_activity_scan);
		// 結果表示用のTextViewを取得する。
		display = (TextView)findViewById(R.id.textView1);
		// カメラ用のSurfaceViewをレイアウトから取得し、サイズを画面の半分に調整する。
		camView = (CamPreView)findViewById(R.id.camPreView1);
		LayoutParams viewParams = camView.getLayoutParams();
		Point dispSize = new Point();
		getWindowManager().getDefaultDisplay().getSize(dispSize);
		viewParams.height = dispSize.y / 2;
		viewParams.width = dispSize.x / 2;
		camView.setLayoutParams(viewParams);

		// ボタンの処理。
		findViewById(R.id.button1).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				display.setText("");	// 表示を一旦クリア。
				camView.startCapture(ScanActivity.this);	// バーコード読み取り開始。
			}
		});
    }
    // バーコードスキャン処理が終わると呼ばれる。
	@Override
	public void onCodeScanned(String scannedCode) {
		display.setText(scannedCode);	// 結果を画面に表示する。
		Toast.makeText(this, "読み込み処理が完了しました。", Toast.LENGTH_SHORT).show();
	}
}
