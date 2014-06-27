package com.example.stompbox.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

public class ConnUtil {
	private static final String TAG = "ConnUtil";
	// HTTP�ʐM�J�n�ipostBody��null�Ȃ�GET�ʐM�j�B
	public static byte[] startHttp(String url, byte[] postBody, String ContentType){
		int size;
		byte[] byteBuffer = new byte[1024];
		HttpURLConnection conn=null;
		InputStream in=null;
		OutputStream out=null;
		ByteArrayOutputStream resultOut=null;
		try{
			URL urlObj=new URL(url);
			conn=(HttpURLConnection)urlObj.openConnection();
			if(ContentType!=null) conn.addRequestProperty("Content-Type", ContentType);
			conn.setReadTimeout(60 * 1000);
			if(postBody == null){
				conn.setRequestMethod("GET");
				conn.connect();
			}else{
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				out=conn.getOutputStream();
				out.write(postBody);
				out.flush();
			}
			int respCode = conn.getResponseCode();
			Log.v(TAG, "http, respCode:" + respCode);
			// ���X�|���X��M�B
			in=conn.getInputStream();
			resultOut=new ByteArrayOutputStream();
			while((size=in.read(byteBuffer)) != -1){
				resultOut.write(byteBuffer, 0, size);
			}
			Log.v(TAG, "JSON:" + resultOut.toString());		// �����JSON�`���Ȃ̂ŕK���e�L�X�g�BTODO: ��r�I�d���̂ōŌ�ɏ������B
			return resultOut.toByteArray();
		}catch(Exception e){
			Log.v(TAG, "�ʐM�G���[:" + e.toString());
		}finally{
			try{if(out!=null)out.close();}catch(Exception ignore){}
			try{if(in!=null)in.close();}catch(Exception ignore){}
			try{if(resultOut!=null)resultOut.close();}catch(Exception ignore){}
			try{if(conn!=null)conn.disconnect();}catch(Exception ignore){}
		}
		return null;
	}
	// �@�����[�h����B
	public static boolean isAirplaneModeOn(Activity mActivity){
    	return (Settings.System.getInt(mActivity.getApplicationContext().getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0)!=0)?true:false;
	}
    // �l�b�g���[�N�g�p�ۃ`�F�b�N�B
    public static boolean isNetworkAvailable(Activity mActivity){
    	boolean isAirplaneMode = isAirplaneModeOn(mActivity);	// ���炭�ȉ��ł����肳���B�ꉞ�擪�Ń`�F�b�N�B
    	if(isAirplaneMode){		// �@�����[�h�Ȃ�l�b�g���[�N�g�p�s�ifalse�j��Ԃ��B
    		Log.v(TAG, "<<airplane mode>>");
    		return false;
    	}
    	// �@�����[�h�łȂ���΁AConnectivityManager�Őڑ���Ԃ��擾����B
    	ConnectivityManager cm = (ConnectivityManager)mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
    	if(cm != null){			// �T�[�r�X���̎��ĂȂ���΃l�b�g���[�N���p�s�B
        	NetworkInfo[] niList = cm.getAllNetworkInfo();
        	if(niList != null){
            	for(int i = 0; i < niList.length; i++){
            		if(niList[i].isConnectedOrConnecting()){	// niList[i].getState() == NetworkInfo.State.CONNECTED
            			Log.v(TAG, "found connection type:" + niList[i].getTypeName());
            			return true;	// �����������炷���ߊ����ăl�X�g�B
            		}
            	}
        	}
    	}
    	return false;
    }
}
