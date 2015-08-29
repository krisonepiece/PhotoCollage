package com.fcu.photocollage.movie;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fcu.photocollage.cloudalbum.CloudFileUpload;
import com.fcu.photocollage.library.FileTool;
import com.fcu.photocollage.member.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2015/8/27.
 */
public class UploadMusic implements Runnable{
    private static final String TAG = "UploadMusic";
    private Handler handler;
    private String musicPath;
    private int userId;
    private String sURL;

    public UploadMusic(Handler handler, String musicPath, int userId, String sURL) {
        this.handler = handler;
        this.musicPath = musicPath;
        this.userId = userId;
        this.sURL = sURL;
    }

    @Override
    public void run() {

        /* 上傳語音到Server */
        updateHandleMessage("result","上傳音樂...");
        String relativePath = "../../Data/" + userId + "/temp/" + "music.mp3";
        String response = CloudFileUpload.executeMultiPartRequest(sURL, new File(musicPath), relativePath);
        Log.d(TAG, response);

        updateHandleMessage("result","音樂上傳完成");
    }

    protected void updateHandleMessage(String key, String value) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString(key,value);
        msg.setData(data);
        handler.sendMessage(msg);
    }
}
