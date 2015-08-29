package com.fcu.photocollage.library;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by user on 2015/8/27.
 */
public class RecordTool {
    /**
     * 取得語音長度
     */
    public static int getRecTime(String recName) {
        MediaPlayer mp = new MediaPlayer();
        FileInputStream stream;
        int duration;
        try {
            stream = new FileInputStream(recName);
            mp.setDataSource(stream.getFD());
            stream.close();
            mp.prepare();
            duration = mp.getDuration();
            Log.i("Rec-Time", recName + ":" + duration);
            mp.release();
            return duration;

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }
}
