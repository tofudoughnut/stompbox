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
		// ���ʕ\���p��TextView���擾����B
		display = (TextView)findViewById(R.id.textView1);
		// �J�����p��SurfaceView�����C�A�E�g����擾���A�T�C�Y����ʂ̔����ɒ�������B
		camView = (CamPreView)findViewById(R.id.camPreView1);
		LayoutParams viewParams = camView.getLayoutParams();
		Point dispSize = new Point();
		getWindowManager().getDefaultDisplay().getSize(dispSize);
		viewParams.height = dispSize.y / 2;
		viewParams.width = dispSize.x / 2;
		camView.setLayoutParams(viewParams);

		// �{�^���̏����B
		findViewById(R.id.button1).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				display.setText("");	// �\������U�N���A�B
				camView.startCapture(ScanActivity.this);	// �o�[�R�[�h�ǂݎ��J�n�B
			}
		});
    }
    // �o�[�R�[�h�X�L�����������I���ƌĂ΂��B
	@Override
	public void onCodeScanned(String scannedCode) {
		display.setText(scannedCode);	// ���ʂ���ʂɕ\������B
		Toast.makeText(this, "�ǂݍ��ݏ������������܂����B", Toast.LENGTH_SHORT).show();
	}
}
