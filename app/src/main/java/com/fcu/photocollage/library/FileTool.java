package com.fcu.photocollage.library;

import java.io.File;
import android.util.Log;

public class FileTool {
	/**
	 * 建立新資料夾
	 * 
	 * @param path
	 */
	public static void createNewFolder(String path) {
		// 建立資料夾
		File sdFile = android.os.Environment.getExternalStorageDirectory();
		File dirFile = new File(path);
		if (!dirFile.exists()) {// 如果資料夾不存在
			dirFile.mkdir();// 建立資料夾
			Log.i("Create-File", path + "");
		}
	}

	/**
	 * 清空資料夾
	 * 
	 * @param path
	 */
	public static void deleteFolder(String path) {
		File delFile = new File(path);
		for (File i : delFile.listFiles()) {
			if (i.exists()) {
				i.delete();
				Log.i("DELETE-File", i.getName() + "");
			}
		}
	}
	
}
