package com.fcu.imagepicker;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

public class Utility {

    /**
     * 判斷SD卡是否可用
     */
    public static boolean isSDcardOK() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 獲取SD卡跟路徑。SD卡不可用時，返回null
     */
    public static String getSDcardRoot() {
        if (isSDcardOK()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }

        return null;
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int msgId) {
        Toast.makeText(context, msgId, Toast.LENGTH_SHORT).show();
    }

    /**獲取字符串中某個字符串出現的次數。*/
    public static int countMatches(String res, String findString) {
        if (res == null) {
            return 0;
        }

        if (findString == null || findString.length() == 0) {
            throw new IllegalArgumentException("The param findString cannot be null or 0 length.");
        }

        return (res.length() - res.replace(findString, "").length()) / findString.length();
    }

    /**判斷該文件是否是一個圖片。*/
    public static boolean isImage(String fileName) {
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")
        		|| fileName.endsWith(".JPG") || fileName.endsWith(".JPEG") || fileName.endsWith(".PNG");
    }

}
