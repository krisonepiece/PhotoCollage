package com.fcu.photocollage.cloudalbum;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fcu.photocollage.library.FileTool;
import com.fcu.photocollage.library.ImageTool;
import com.fcu.photocollage.member.AppController;
import com.fcu.photocollage.movie.Photo;


public class UploadPhoto implements Runnable{
	private static final String TAG = "UploadPhoto";
	private ArrayList<Photo> pList;
	private Handler handler;
	private String dbURL;
	private String sURL;
	private int pid;
	
	public UploadPhoto(Handler handler, ArrayList<Photo> pList, String dbURL, String sURL) {
		this.handler = handler;
		this.pList = pList;
		this.dbURL = dbURL;
		this.sURL = sURL;
	}

	@Override
	public void run() {
		
		//建立暫存資料夾
		FileTool.createNewFolder("/sdcard/PCtemp");
		
		for(int i = 0 ; i < pList.size() ; i++){
			updateHandleMessage("value","上傳照片...("+ i + "/" + pList.size() + ")");
			
			//以當下時間為照片命名
			pList.get(i).createPname();
			
			//上傳相片資訊到資料庫
			uploadPhotoToDb(pList.get(i));
			
			/* 上傳相片到Server */			
			//壓縮圖片
			File picFile = new File(pList.get(i).getpPath());
			Bitmap bitmap = BitmapFactory.decodeFile(picFile.getAbsolutePath(), new BitmapFactory.Options());			
			bitmap = ImageTool.ScalePicEx(picFile.getAbsolutePath(), 600, 800);
			String tmpPath = "/sdcard/PCtemp/" + pList.get(i).getPname();
			ImageTool.compressAndCreatePhoto(tmpPath, bitmap, Bitmap.CompressFormat.JPEG, 40);
			//上傳檔案
			String relativePath = "../../Data/" + pList.get(i).getUserID() + "/Picture/" + pList.get(i).getAlbumID() + "/" + pList.get(i).getPname();
			String response = CloudFileUpload.executeMultiPartRequest(sURL, new File(tmpPath), relativePath);
			Log.d(TAG,response);
		}
		updateHandleMessage("value","完成");
	}
	
	public void uploadPhotoToDb(final Photo photo) {
		// Tag used to cancel the request
		String uploadPhoto_req = "uploadPhotoToDb";
		StringRequest strReq = new StringRequest(Request.Method.POST,dbURL,new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.d("uploadPhotoToDb", "Response: " + response.toString());
				try {							
					if (response != null) {	
						JSONObject jObj = new JSONObject(response);
						String result = jObj.getString("result");
						if(result.contains("success"))
							pid = jObj.getInt("Pid");
						Log.d("uploadPhotoToDb", result);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("uploadPhotoToDb", "Error: " + error.getMessage());
			}
		}) {

			@Override
			protected Map<String, String> getParams() {
				// Posting params to register url
				Map<String, String> params = new HashMap<String, String>();
				params.put("Pname", photo.getPname());
				params.put("TakeDate", photo.getTakeDate());
				params.put("Ppath", photo.getpServerPath() + photo.getPname());
				params.put("AlbumID", Integer.toString(photo.getAlbumID()));
				params.put("UserID", Integer.toString(photo.getUserID()));
				return params;
			}
		};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, uploadPhoto_req);
	}
	protected void updateHandleMessage(String key, String value) {
		Message msg = new Message();;
        Bundle data = new Bundle();
		data.putString(key,value);        
        msg.setData(data);
        handler.sendMessage(msg);
	}
}
