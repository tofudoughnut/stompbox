package com.example.stompbox.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {
	// 特定の日時の通算ミリ秒を受け取り、書式を整えて文字列として返す。
	public static String formatDate(long timeMs){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeMs);
		SimpleDateFormat dateTemplate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		return dateTemplate.format(cal.getTime()).toString();
	}
}
