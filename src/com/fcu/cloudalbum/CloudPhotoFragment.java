package com.fcu.cloudalbum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.GatheringByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.StringRequest;
import com.fcu.R;
import com.fcu.imagepicker.Utility;
import com.fcu.library.FileTool;
import com.fcu.member.AppController;
import com.fcu.member.SQLiteHandler;
import com.fcu.member.SessionManager;
import com.fcu.photocollage.FileUpload;
import com.fcu.photocollage.HttpPhotoUpload;
import com.fcu.photocollage.MovieView;
import com.fcu.photocollage.Photo;
import com.fcu.library.*;

import static com.fcu.imagepicker.Utility.isImage;

/**
 * 選擇照片頁面
 */
public class CloudPhotoFragment extends Fragment {
	private static final String TAG = "CloudPhotoFragment";
	private final static int PHOTO = 22;
    private ArrayList<String> paths;
    private ArrayList<Photo> pList;
    private GridView mPhotoWall;
	private SQLiteHandler db;
	private int userID;
    private CloudPhotoAdapter adapter;
    private View thisView;
    private int albumId;
    private String albumName;
    private ProgressDialog progressDialog;

    public void init() {    	
        mPhotoWall = (GridView) thisView.findViewById(R.id.cloud_photo_grid);
	}

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
    	//super.onCreateView(inflater, container, savedInstanceState);
    	setHasOptionsMenu(true);
    	thisView = inflater.inflate(R.layout.fragment_cloud_photo, container, false);
    	
    	init();
    	((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
    	((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(albumName);
    	//顯示進度條
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setTitle("載入相片");
		progressDialog.setMessage("請稍後...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.show();
        getImagePathsFromAlbumId();
        
		// SqLite database handler
		db = new SQLiteHandler(getActivity().getApplicationContext());

		// Fetching user details from sqlite
		HashMap<String, String> user = db.getUserDetails();
		userID = Integer.parseInt(user.get("uid"));


		return thisView;
	}

    /**
     * 點擊返回時，跳轉至相冊頁面
     */
    private void backAction() {
        Fragment cloudAlbumFg = new CloudAlbumFragment();
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        fragmentManager.beginTransaction()
				        .replace(R.id.content_frame, cloudAlbumFg)
				        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
						.addToBackStack(null)
						.commit();
    }

    /**
     * 根據圖片所屬文件夾路徑，刷新頁面
     */
    private void updateView() {
        pList.clear();
        adapter.notifyDataSetChanged();
        
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(albumName);
        getImagePathsFromAlbumId();
        
        adapter.notifyDataSetChanged();
        if (pList.size() > 0) {
            //滾動至頂部
            mPhotoWall.smoothScrollToPosition(0);
        }
    }

    /**
     * 讀取指定相簿的圖片。
     */
    private void getImagePathsFromAlbumId() {
    	// Tag used to cancel the request
		String getPhoto_req = "getCloudPhoto";
		pList = new ArrayList<Photo>();
		StringRequest strReq = new StringRequest(Method.POST,getString(R.string.getCloudPhoto),new Listener<String>() {
			@Override
			public void onResponse(String response) {
				//Log.d("getCloudPhoto", "Response: " + response.toString());
				try {
					JSONObject jObj = new JSONObject(response);
					// boolean error = jObj.getBoolean("error");
					if (jObj.getBoolean("result")) {
						// User successfully stored in MySQL
						// Now store the user in sqlite
						int size = jObj.getInt("Size");
						for(int i = 0 ; i < size ; i++){
							JSONObject photo = jObj.getJSONObject("p"+i);
							int pid = photo.getInt("Pid");;
							String pName = photo.getString("Pname");
							String takeDate = photo.getString("TakeDate");
							String uploadDate = photo.getString("UploadDate");
							String pPath = photo.getString("Ppath");
							String recPath = photo.getString("RecPath");								
							//Log.d("getCloudPhoto","Response:"+ pid + "," + pName + "," + pPath);
							pList.add(new Photo(pid, pName, takeDate, uploadDate, pPath, recPath));							
						}
						adapter = new CloudPhotoAdapter(getActivity(), pList);
						mPhotoWall.setAdapter(adapter);
						progressDialog.dismiss();
					}
					else{
						updateView();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("getCloudPhoto", "Error: " + error.getMessage());
			}
		}) {

			@Override
			protected Map<String, String> getParams() {
				// Posting params to register url
				Map<String, String> params = new HashMap<String, String>();
				params.put("AlbumID", Integer.toString(albumId));
				Log.d("putAlbumID", "AlbumID: " + Integer.toString(albumId));
				return params;
			}
		};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, getPhoto_req);
    }

//    //獲取已選擇的圖片路徑
//    private ArrayList<Photo> getSelectImage() {
//        SparseBooleanArray map = adapter.getSelectionMap();
//        if (map.size() == 0) {
//            return null;
//        }
//
//        ArrayList<Photo> selectedImageList = new ArrayList<Photo>();
//
//        for (int i = 0; i < pList.size(); i++) {
//            if (map.get(i)) {
//                selectedImageList.add(pList.get(i));
//            }
//        }
//        return selectedImageList;
//    }

    
	@Override
	public void onResume() {
		super.onResume();	
		Log.i(TAG, "onResume");
		//從相冊頁面跳轉至此頁
		//if(getArguments() != null){	        
        if (getArguments().getInt("AlbumID", -1) != -1 && getArguments().getString("AlbumName", null) != null) {
        	albumId = getArguments().getInt("AlbumID", -1);
	        albumName = getArguments().getString("AlbumName", null);
        	getArguments().remove("AlbumID");
	        getArguments().remove("AlbumName");
        //}
        }
        else if (getActivity().getIntent().getIntExtra("code", -1) != -1){
        	int code = getActivity().getIntent().getIntExtra("code", -1);
    		if(code == 100){
    			Bundle bundle = getActivity().getIntent().getExtras();    			
    			paths = bundle.getStringArrayList("paths");
    			
    			ArrayList<Photo> uploadList = new ArrayList<Photo>();    			
				
    			for (int i = 0; i < paths.size(); i++) {    				
    				// 取得拍攝日期
    				String takeDate = Utility.getTakeDate(paths.get(i),"yyyy/MM/dd HH:mm:ss");
    				
    				//建立Photo資訊
    				Photo tmpP = new Photo(paths.get(i), takeDate, albumId, userID);
    				uploadList.add(tmpP);
    			}
    			//上傳圖片
    			UploadPhoto uploadPhoto = new UploadPhoto(handler, uploadList, getString(R.string.uploadPhoto), getString(R.string.uploadFile));
    			Thread uploadThread = new Thread(uploadPhoto);
   				uploadThread.start();
   				
   				//顯示進度條
    			progressDialog = new ProgressDialog(getActivity());
				progressDialog.setTitle("上傳照片");
				progressDialog.setMessage("請稍後...");
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.show();
    		}    		
    		getActivity().getIntent().removeExtra("code");
        }
	}
	/**
	 * 接收上傳檔案進度
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
	        String value = data.getString("value");
			progressDialog.setMessage(value);
			
			if (value.equals("完成")) {
				progressDialog.dismiss();
				//刪除暫存
   				FileTool.deleteFolder("/sdcard/PCtemp");
				// 重新整理頁面
   				updateView();
			}
		}
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d(TAG,"onCreateOptionsMenu");
		inflater.inflate(R.menu.cloud_photo_menu, menu);		
//		MenuItem mi = menu.findItem(R.id.action_check);
//		mi.setVisible(true);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//super.onOptionsItemSelected(item);
		switch(item.getItemId()){
			case R.id.action_upload_photo:
				//上傳照片
				Intent intent = new Intent(getActivity(),
						com.fcu.imagepicker.ImagePickerActivity.class);
				startActivityForResult(intent,PHOTO);
				return true;
			
			case android.R.id.home:
				backAction();
				return true;
		}
		return false;
	}
	@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG,"setUserVisibleHint");
        // 每次切換Fragment調用的方法

    }
}
