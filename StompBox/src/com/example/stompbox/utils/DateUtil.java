package com.example.stompbox.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {
	// ����̓����̒ʎZ�~���b���󂯎��A�����𐮂��ĕ�����Ƃ��ĕԂ��B
	public static String formatDate(long timeMs){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeMs);
		SimpleDateFormat dateTemplate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		return dateTemplate.format(cal.getTime()).toString();
	}
}
