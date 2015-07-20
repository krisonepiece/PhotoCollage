package com.fcu.speechtag;

import java.io.IOException;
import android.media.MediaRecorder;
import android.util.Log;

public class MyRecoder {

    private static final double EMA_FILTER = 0.6;
    private MediaRecorder recorder = null;
    private double mEMA = 0.0;
    private String output;

    // 建立錄音物件，參數為錄音儲存的位置與檔名
    public MyRecoder(String output) {
        this.output = output;
    }

    // 開始錄音
    public void start() {
        if (recorder == null) {
            // 建立錄音用的MediaRecorder物件
            recorder = new MediaRecorder();
            // 設定錄音來源為麥克風，必須在setOutputFormat方法之前呼叫
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 設定輸出格式為3GP壓縮格式，必須在setAudioSource方法之後，
            // 在prepare方法之前呼叫
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // 設定錄音的編碼方式，必須在setOutputFormat方法之後，
            // 在prepare方法之前呼叫
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // 設定輸出的檔案名稱，必須在setOutputFormat方法之後，
            // 在prepare方法之前呼叫
            recorder.setOutputFile("/sdcard/" + output);

            try {
                // 準備執行錄音工作，必須在所有設定之後呼叫
                recorder.prepare();
            }
            catch (IOException e) {
                Log.d("RecordActivity", e.toString());
            }

            // 開始錄音
            recorder.start();
            mEMA = 0.0;
        }
    }

    // 停止錄音
    public void stop() {
        if (recorder != null) {
            // 停止錄音
            recorder.stop();
            recorder.reset();
            // 清除錄音資源
            recorder.release();
            recorder = null;
            Log.i("MyRecord", output);
        }
    }

    public double getAmplitude() {
        if (recorder != null)
            return (recorder.getMaxAmplitude() / 2700.0);
        else
            return 0;
    }

    // 取得麥克風音量
    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }
}