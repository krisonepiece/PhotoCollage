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
import com.fcu.photocollage.member.AppController;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2015/8/27.
 */
public class CallFFmpeg  implements Runnable{
    private static final String TAG = "CallFFmpeg";
    private Handler handler;
    private String commend;
    private int uid;
    private String URL;

    public CallFFmpeg(Handler handler, String commend, int uid,  String URL) {
        this.handler = handler;
        this.commend = commend;
        this.uid = uid;
        this.URL = URL;
    }

    @Override
    public void run() {
        updateHandleMessage("result", "建立電影...");
        shellCommend(commend, uid, URL);
    }

    public void shellCommend(String commend, int uid,  String URL) {
        try {
            // 連線到 url網址
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost method = new HttpPost(URL);
            // 傳值給PHP
            List<NameValuePair> vars = new ArrayList<NameValuePair>();
            vars.add(new BasicNameValuePair("commend", commend));
            vars.add(new BasicNameValuePair("uid", Integer.toString(uid)));
            method.setEntity(new UrlEncodedFormEntity(vars, HTTP.UTF_8));
            HttpResponse response = httpclient.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String msg;
                msg = EntityUtils.toString(entity, "utf-8");// 如果成功將網頁內容存入key
                JSONObject jObj = new JSONObject(msg);
                String cmd = jObj.getString("commend");
                String tokenCmd = jObj.getString("tokenCmd");
                Boolean result = jObj.getBoolean("result");
                Log.d(TAG, cmd);
                Log.d(TAG, tokenCmd);
                if(!result) {
                    Log.d("shellCommend", "Error");
                    updateHandleMessage("result","Error");
                }
                updateHandleMessage("result","完成");
            }
            else{
                Log.d("shellCommend", "Error");
                updateHandleMessage("result","Error");
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        // Tag used to cancel the request
//        String shellCommend_req = "shellCommend";
//        StringRequest strReq = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d("shellCommend", "Response: " + response.toString());
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    String cmd = jObj.getString("commend");
//                    String tokenCmd = jObj.getString("tokenCmd");
//                    Boolean result = jObj.getBoolean("result");
//                    Log.d(TAG, cmd);
//                    Log.d(TAG, tokenCmd);
//                    if(!result) {
//                        Log.d("shellCommend", "Error");
//                        updateHandleMessage("result","Error");
//                    }
//                    updateHandleMessage("result","完成");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("shellCommend", "Error: " + error.getMessage());
//            }
//        }) {
//
//            @Override
//            protected Map<String, String> getParams() {
//                // Posting params to register url
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("commend", commend);
//                params.put("uid", Integer.toString(uid));
//                return params;
//            }
//        };
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(strReq, shellCommend_req);
    }

    protected void updateHandleMessage(String key, String value) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString(key,value);
        msg.setData(data);
        handler.sendMessage(msg);
    }
}
