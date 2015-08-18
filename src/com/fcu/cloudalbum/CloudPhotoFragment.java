package com.fcu.cloudalbum;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
import com.fcu.member.AppController;

import static com.fcu.imagepicker.Utility.isImage;

/**
 * 選擇照片頁面
 */
public class CloudPhotoFragment extends Fragment {
	private static final String TAG = "CloudPhotoFragment";
    private ArrayList<CloudPhotoItem> list;
    private GridView mPhotoWall;
    private CloudPhotoAdapter adapter;
    private View thisView;
    private int albumId;
    private String albumName;

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
    	list = new ArrayList<CloudPhotoItem>();
        getImagePathsFromAlbumId();        

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
    private void updateView(int albumId, String albumName) {
        list.clear();
        adapter.clearSelectionMap();
        adapter.notifyDataSetChanged();
        
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(albumName);
        getImagePathsFromAlbumId();
        
        adapter.notifyDataSetChanged();
        if (list.size() > 0) {
            //滾動至頂部
            mPhotoWall.smoothScrollToPosition(0);
        }
    }

    /**
     * 讀取指定相簿的圖片。
     */
    private ArrayList<CloudPhotoItem> getImagePathsFromAlbumId() {
    	final ArrayList<CloudPhotoItem> pList = new ArrayList<CloudPhotoItem>();
    	// Tag used to cancel the request
		String getPhoto_req = "getCloudPhoto";
		StringRequest strReq = new StringRequest(Method.POST,getString(R.string.getCloudPhoto),new Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.d("getCloudPhoto", "Response: " + response.toString());
				try {
					JSONObject jObj = new JSONObject(response);
					// boolean error = jObj.getBoolean("error");
					if (response != null) {
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
							Log.d("getCloudPhoto","Response:"+ pid + "," + pName + "," + pPath);
							pList.add(new CloudPhotoItem(pid, pName, takeDate, uploadDate, pPath, recPath));							
						}
						list = pList;
						CloudPhotoAdapter adapter = new CloudPhotoAdapter(getActivity(), pList);
						mPhotoWall.setAdapter(adapter);
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
        return pList;
    }

    //獲取已選擇的圖片路徑
    private ArrayList<CloudPhotoItem> getSelectImage() {
        SparseBooleanArray map = adapter.getSelectionMap();
        if (map.size() == 0) {
            return null;
        }

        ArrayList<CloudPhotoItem> selectedImageList = new ArrayList<CloudPhotoItem>();

        for (int i = 0; i < list.size(); i++) {
            if (map.get(i)) {
                selectedImageList.add(list.get(i));
            }
        }
        return selectedImageList;
    }

    //從相冊頁面跳轉至此頁
	@Override
	public void onResume() {
		super.onResume();		
		
		if(getArguments() != null){
	        albumId = getArguments().getInt("AlbumID", -1);
	        albumName = getArguments().getString("AlbumName", null);
	        if (albumId != -1 && albumName != null) {
	            //某個相冊
	        	//updateView(albumId, albumName);
	        }
	        getArguments().remove("AlbumID");
	        getArguments().remove("AlbumName");
        }
		Log.i("onResume","phone," + albumId);
	}	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d(TAG,"onCreateOptionsMenu");
		inflater.inflate(R.menu.album_menu, menu);
		MenuItem mi = menu.findItem(R.id.action_check);
		mi.setVisible(true);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//super.onOptionsItemSelected(item);
		switch(item.getItemId()){
			case R.id.action_check:
				//選擇圖片完成,回到起始頁面
				ArrayList<CloudPhotoItem> cPaths = getSelectImage();
				Intent intent = new Intent(getActivity(), com.fcu.menu.MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("code", cPaths != null ? 200 : 201);
				Bundle bundle = new Bundle();
				bundle.putSerializable("cPaths", cPaths);
				intent.putExtras(bundle);
				startActivity(intent);
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
