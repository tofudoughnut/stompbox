package com.example.stompbox.barcode;

import java.util.EnumMap;
import java.util.Map;

import com.example.stompbox.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	private EditText input;
	private ImageView dispBarcode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.barcode_activity_main);
		// QRコードに変換する文字列を入力するフィールド。
		input = (EditText)findViewById(R.id.editText1);
		// できあがったQRコード（画像）を表示するView。
		dispBarcode = (ImageView)findViewById(R.id.imageView1);
		dispBarcode.setVisibility(View.GONE);
		// ボタンの処理。フィールドの文字列をQRコードに変換して画面に表示。
		findViewById(R.id.button1).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Bitmap qrCodeBmp = strToQrCode(input.getText().toString());
				if(qrCodeBmp!=null)dispBarcode.setImageBitmap(qrCodeBmp);
				dispBarcode.setVisibility(View.VISIBLE);
			}
		});
	}
	public static Bitmap strToQrCode(String str){
		MultiFormatWriter barcodeWriter = new MultiFormatWriter();
		int width = 400;
		int height = 400;
		try {
			final boolean setEncoding = true;
			BitMatrix result;
			if(setEncoding){
				Map<EncodeHintType, Object>hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
				hints.put(EncodeHintType.CHARACTER_SET, "SJIS");
//				hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);	// エラー訂正レベルも指定可能。
				result = barcodeWriter.encode(str, BarcodeFormat.QR_CODE, width, height, hints);
			}else{
				result = barcodeWriter.encode(str, BarcodeFormat.QR_CODE, width, height);
			}
			width = result.getWidth();
			height = result.getHeight();
			Log.v(TAG, "size of result, w:" + width + ", h:" + height);
			int[] pixels = new int[width * height];
			for(int y = 0; y < height; ++y){
				int offset = y * width;
				for(int x = 0; x < width; ++x){
					pixels[offset + x] = result.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
				}
			}
			// ピクセル配列からビットマップを生成する。
			Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			bmp.setPixels(pixels, 0, width, 0, 0, width, height);
			return bmp;
		} catch (WriterException e) {
			e.printStackTrace();
		}
    	return null;
    }
}
