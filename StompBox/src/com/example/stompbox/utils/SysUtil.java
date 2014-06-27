package com.example.stompbox.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class SysUtil {
	// サービスが起動中か確認する。実行中のサービス一覧を取得して、その中から探す。
    public static boolean isServiceRunning(Context context, String serviceClassName){
    	ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
    	List<RunningServiceInfo> rs = am.getRunningServices(Integer.MAX_VALUE);
    	for(RunningServiceInfo thisSvcInfo : rs){
    		if(thisSvcInfo.service.getClassName().equals(serviceClassName)){
    			return true;
    		}
    	}
    	return false;
    }
}
