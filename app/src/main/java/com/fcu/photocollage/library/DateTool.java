package com.fcu.photocollage.library;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTool {
	public static String getCurrentTime(String format) {
		// 先行定義時間格式("yyyy/MM/dd HH:mm:ss")
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		// 取得現在時間
		Date dt = new Date(System.currentTimeMillis());

		return sdf.format(dt);
	}
	
	public static String dateFormat(String dateString, String format) {
		// 先行定義時間格式("yyyy/MM/dd HH:mm:ss")
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = (Date) sdf.parse(dateString);
			sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "0000-00-00 00:00:00";	
	}
}
