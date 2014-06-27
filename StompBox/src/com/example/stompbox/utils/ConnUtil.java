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
	// HTTP通信開始（postBodyがnullならGET通信）。
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
			// レスポンス受信。
			in=conn.getInputStream();
			resultOut=new ByteArrayOutputStream();
			while((size=in.read(byteBuffer)) != -1){
				resultOut.write(byteBuffer, 0, size);
			}
			Log.v(TAG, "JSON:" + resultOut.toString());		// 今回はJSON形式なので必ずテキスト。TODO: 比較的重いので最後に消す事。
			return resultOut.toByteArray();
		}catch(Exception e){
			Log.v(TAG, "通信エラー:" + e.toString());
		}finally{
			try{if(out!=null)out.close();}catch(Exception ignore){}
			try{if(in!=null)in.close();}catch(Exception ignore){}
			try{if(resultOut!=null)resultOut.close();}catch(Exception ignore){}
			try{if(conn!=null)conn.disconnect();}catch(Exception ignore){}
		}
		return null;
	}
	// 機内モード判定。
	public static boolean isAirplaneModeOn(Activity mActivity){
    	return (Settings.System.getInt(mActivity.getApplicationContext().getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0)!=0)?true:false;
	}
    // ネットワーク使用可否チェック。
    public static boolean isNetworkAvailable(Activity mActivity){
    	boolean isAirplaneMode = isAirplaneModeOn(mActivity);	// 恐らく以下でも判定される。一応先頭でチェック。
    	if(isAirplaneMode){		// 機内モードならネットワーク使用不可（false）を返す。
    		Log.v(TAG, "<<airplane mode>>");
    		return false;
    	}
    	// 機内モードでなければ、ConnectivityManagerで接続状態を取得する。
    	ConnectivityManager cm = (ConnectivityManager)mActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
    	if(cm != null){			// サービス自体取れてなければネットワーク利用不可。
        	NetworkInfo[] niList = cm.getAllNetworkInfo();
        	if(niList != null){
            	for(int i = 0; i < niList.length; i++){
            		if(niList[i].isConnectedOrConnecting()){	// niList[i].getState() == NetworkInfo.State.CONNECTED
            			Log.v(TAG, "found connection type:" + niList[i].getTypeName());
            			return true;	// 抜け口を減らすため敢えてネスト。
            		}
            	}
        	}
    	}
    	return false;
    }
}
