package com.example.stompbox.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {
	// “Á’è‚Ì“ú‚Ì’ÊZƒ~ƒŠ•b‚ğó‚¯æ‚èA‘®‚ğ®‚¦‚Ä•¶š—ñ‚Æ‚µ‚Ä•Ô‚·B
	public static String formatDate(long timeMs){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeMs);
		SimpleDateFormat dateTemplate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		return dateTemplate.format(cal.getTime()).toString();
	}
}
