package com.example.stompbox.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class SysUtil {
	// �T�[�r�X���N�������m�F����B���s���̃T�[�r�X�ꗗ���擾���āA���̒�����T���B
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
