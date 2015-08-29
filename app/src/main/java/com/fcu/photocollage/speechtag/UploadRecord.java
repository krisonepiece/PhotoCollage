package com.fcu.photocollage.speechtag;


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
import com.fcu.photocollage.movie.Photo;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2015/8/27.
 */
public class UploadRecord implements Runnable{
    private static final String TAG = "UploadRecord";
    private Handler handler;
    private ArrayList<Photo> pList;
    private String dbURL;
    private String sURL;

    public UploadRecord(Handler handler, ArrayList<Photo> pList, String dbURL, String sURL) {
        this.handler = handler;
        this.pList = pList;
        this.dbURL = dbURL;
        this.sURL = sURL;
    }

    @Override
    public void run() {

        for(int i = 0 ; i < pList.size() ; i++){
            updateHandleMessage("result","上傳語音...("+ i + "/" + pList.size() + ")");

            //以當下時間為語音命名
            pList.get(i).createRname();

            //上傳語音資訊到資料庫
            uploadRecordToDb(pList.get(i));

			/* 上傳語音到Server */
            String relativePath = "../../Data/" + pList.get(i).getUserID() + "/Picture/" + pList.get(i).getAlbumID() + "/" + pList.get(i).getRname();
            String response = CloudFileUpload.executeMultiPartRequest(sURL, new File(pList.get(i).getRecPath()), relativePath);
            Log.d(TAG, response);
        }
        updateHandleMessage("result","語音上傳完成");
    }

    public void uploadRecordToDb(final Photo photo) {
        // Tag used to cancel the request
        String uploadRecord_req = "uploadRecordToDb";
        StringRequest strReq = new StringRequest(Request.Method.POST,dbURL,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("uploadRecordToDb", "Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    Boolean result = jObj.getBoolean("result");
                    if(!result) {
                        Log.d("uploadRecordToDb", "Error");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("uploadRecordToDb", "Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("Pid", Integer.toString(photo.getPid()));
                params.put("RecPath", photo.getServerPath() + photo.getRname());
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, uploadRecord_req);
    }

    protected void updateHandleMessage(String key, String value) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString(key,value);
        msg.setData(data);
        handler.sendMessage(msg);
    }
}
