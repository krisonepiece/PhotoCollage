package com.fcu.photocollage.cloudalbum;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fcu.photocollage.member.AppController;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by krisonepiece on 2015/8/25.
 */
public class CreateCloudAlbum implements Runnable{
    private Handler handler;
    private int uid;
    private String URL;
    private CloudAlbumItem newAlbum;
    private Message msg;
    private Bundle data;


    public CreateCloudAlbum(Handler handler, int uid, String url, CloudAlbumItem newAlbum) {
        this.handler = handler;
        this.uid = uid;
        this.URL = url;
        this.newAlbum = newAlbum;
    }

    @Override
    public void run() {
        createCloudAlbum(handler, URL, uid, newAlbum.getPathName());
    }
    /**
     * 建立相簿
     */
    private void createCloudAlbum(final Handler handler,final String URL, final int uid, final String aName) {
        // Tag used to cancel the request
        String createAlbum_req = "createAlbum";
        msg = new Message();
        data = new Bundle();
        StringRequest strReq = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("createAlbum", "Response: " + response.toString());
                JSONObject jObj;
                try {
                    jObj = new JSONObject(response);
                    if (response != null) {
                        //recive result
                        String result = jObj.getString("result");

                        if(response.toString().contains("Succeed")){
                            int Aid = jObj.getInt("Aid");
                            newAlbum.setAlbumId(Aid);
                            //傳送相簿資訊
                            data.putString("result", "相簿建立完成");
                        }
                        else{
                            data.putString("result", "相簿建立失敗");
                        }
                        msg.setData(data);
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("createAlbum", "Error: " + error.getMessage());
                data.putBoolean("result", false);
                msg.setData(data);
                handler.sendMessage(msg);
        }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", Integer.toString(uid));
                params.put("albumName", aName);
                return params;
            }
        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, createAlbum_req);
    }
}
