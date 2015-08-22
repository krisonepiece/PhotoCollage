package com.fcu.cloudalbum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.fcu.R;
import com.fcu.imagepicker.Utility;
import com.fcu.library.DateTool;
import com.fcu.member.AppController;
import com.fcu.member.LoginFragment;
import com.fcu.member.SQLiteHandler;
import com.fcu.member.SessionManager;

public class CloudAlbumFragment extends Fragment {
	private static final String TAG = "CloudAlbumFragment";
	private View thisView;
	private GridView gridView;
	private SQLiteHandler db;
	private SessionManager session;
	private ArrayList<CloudAlbumItem> aList;
	private CloudAlbumAdapter adapter;
	private String name;
	private String email;
	private EditText dText;
	private GridViewMenuListener gvMenuListener;
	private ProgressDialog progressDialog;
	private static final int MENU_ADD_TEAMWORKER = 0;
    private static final int MENU_RENAME = 1;
    private static final int MENU_DELETE = 2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);
		thisView = inflater.inflate(R.layout.fragment_cloud_album, container, false);
		gridView = (GridView) thisView.findViewById(R.id.cloud_album_grid);
		gvMenuListener = new GridViewMenuListener();
		gridView.setOnCreateContextMenuListener(gvMenuListener);

		((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.select_album);

		// SqLite database handler
		db = new SQLiteHandler(getActivity().getApplicationContext());

		// session manager
		session = new SessionManager(getActivity().getApplicationContext());

		if (session.isLoggedIn()) {
			// Fetching user details from sqlite
			HashMap<String, String> user = db.getUserDetails();

			name = user.get("name");
			email = user.get("email");

			getImagePathsByDB(name, email);
			//顯示進度條
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setTitle("載入相簿");
			progressDialog.setMessage("請稍後...");
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.show();
		}
		return thisView;
	}

	/**
	 * 點擊返回時，回到相冊頁面
	 */
	private void backAction() {
		Intent intent = new Intent(getActivity(),
				com.fcu.menu.MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	/**
	 * 讀取資料庫取得相簿第一張圖片。
	 */
	private ArrayList<CloudAlbumItem> getImagePathsByDB(final String name,final String email) {		
		// Tag used to cancel the request
		String getAlbum_req = "getAlbum";
		aList = new ArrayList<CloudAlbumItem>();
		StringRequest strReq = new StringRequest(Method.POST,getString(R.string.getCloudAlbum),new Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d("getAlbum", "Response: " + response.toString());
						try {
							JSONObject jObj = new JSONObject(response);
							if (response != null) {
								// User successfully stored in MySQL
								// Now store the user in sqlite
								int size = jObj.getInt("Size");
								for(int i = 0 ; i < size ; i++){
									JSONObject album = jObj.getJSONObject("a"+i);
									int Pid = album.getInt("Pid");
									int Aid = album.getInt("Aid");
									int AlbumSize = album.getInt("AlbumSize");									
									int Pcode = album.getInt("Pcode");
									String Ppath = album.getString("Ppath");
									String Aname = album.getString("Aname");
									String Uname = album.getString("Uname");
									String CreateDate = album.getString("CreateDate");
									
									Log.d("getAlbum","Response:"+ Pid + "," + Ppath + "," + Aname+ "," + Uname+ "," + CreateDate);
									aList.add(new CloudAlbumItem(Aid, Aname, AlbumSize, Ppath, Uname, CreateDate));
								}
								
								adapter = new CloudAlbumAdapter(getActivity(), aList);
								gridView.setAdapter(adapter);
																
								gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
									@Override
									public void onItemClick(AdapterView<?> parent, View view,
											int position, long id) {

										Fragment cloudPhotoFg = new CloudPhotoFragment();
										FragmentManager fragmentManager = getActivity()
												.getSupportFragmentManager();

										Bundle bundle = new Bundle();

										bundle.putInt("AlbumID", aList.get(position).getAlbumId());
										bundle.putString("AlbumName", aList.get(position).getPathName());

										cloudPhotoFg.setArguments(bundle);
										fragmentManager
												.beginTransaction()
												.replace(R.id.content_frame, cloudPhotoFg)
												.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
												.addToBackStack(null).commit();
									}
								});
								progressDialog.dismiss();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("getAlbum", "Error: " + error.getMessage());
					}
				}) {

			@Override
			protected Map<String, String> getParams() {
				// Posting params to register url
				Map<String, String> params = new HashMap<String, String>();
				params.put("name", name);
				params.put("email", email);
				return params;
			}
		};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, getAlbum_req);
		return aList;
	}
	/**
	 * 建立相簿
	 */
	private void createCloudAlbum(final String name, final String email, final String aName) {
		// Tag used to cancel the request
		String createAlbum_req = "createAlbum";		
		StringRequest strReq = new StringRequest(Method.POST,getString(R.string.createCloudAlbum),new Listener<String>() {
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
									aList.add(new CloudAlbumItem(Aid, aName, 0, Integer.toString(R.drawable.ic_add_white_36dp), name, DateTool.getCurrentTime("yyyy-MM-dd HH:mm:ss")));
									adapter.notifyDataSetChanged();
								}
								Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("createAlbum", "Error: " + error.getMessage());
					}
				}) {

			@Override
			protected Map<String, String> getParams() {
				// Posting params to register url
				Map<String, String> params = new HashMap<String, String>();
				params.put("name", name);
				params.put("email", email);
				params.put("albumName", aName);
				return params;
			}
		};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, createAlbum_req);
	}
	/**
	 * 刪除相簿
	 */
	private void deleteCloudAlbum(final String name, final String email, final int position) {
		// Tag used to cancel the request
		String deleteAlbum_req = "deleteAlbum";		
		StringRequest strReq = new StringRequest(Method.POST,getString(R.string.deleteCloudAlbum),new Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d("deleteAlbum", "Response: " + response.toString());
						if(response.toString().contains("Success")){
							
							aList.remove(position);
					        adapter.notifyDataSetChanged();
							Toast.makeText(getActivity(), "Delete success!", Toast.LENGTH_SHORT).show();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("deleteAlbum", "Error: " + error.getMessage());
					}
				}) {

			@Override
			protected Map<String, String> getParams() {
				// Posting params to register url
				Map<String, String> params = new HashMap<String, String>();
				params.put("name", name);
				params.put("email", email);
				params.put("albumID", Integer.toString(aList.get(position).getAlbumId()));
				return params;
			}
		};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, deleteAlbum_req);		
	}
	
	//自建長按選單
	private class GridViewMenuListener implements OnCreateContextMenuListener{
		@Override
	    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	        menu.add(Menu.NONE, MENU_ADD_TEAMWORKER, Menu.NONE, "新增協作者");
	        menu.add(Menu.NONE, MENU_RENAME, Menu.NONE, "重新命名");
	        menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "刪除");
	    }
	}
	
	@Override
    public boolean onContextItemSelected(MenuItem item) {
    	final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	
        switch (item.getItemId()) {
            case MENU_ADD_TEAMWORKER:
                Log.i("ContextMenu", "MENU_ADD_TEAMWORKER was chosen" + info.position);
                return true;
            case MENU_RENAME:
                Log.i("ContextMenu", "MENU_RENAME was chosen" + info.position);
                return true;
            case MENU_DELETE:
                Log.i("ContextMenu", "MENU_DELETE was chosen" + info.position);
                //確認刪除視窗
                new AlertDialog.Builder(getActivity()).setTitle("確定刪除相簿？")
        		.setIcon(android.R.drawable.ic_dialog_info)        					
        		.setNegativeButton("取消", null)
        		.setPositiveButton("確定", new OnClickListener() {				
        			@Override
        			public void onClick(DialogInterface dialog, int which) {
        				deleteCloudAlbum(name, email, info.position);					
        			}
        		})
        		.show();                                
                return true;
        }
        return super.onContextItemSelected(item);
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d(TAG,"onCreateOptionsMenu");
		inflater.inflate(R.menu.cloud_album_menu, menu);
//		MenuItem mi = menu.findItem(R.id.action_check);
//		mi.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.action_add_album:
			dText = new EditText(getActivity());
			new AlertDialog.Builder(getActivity()).setTitle("幫相簿取個名字吧")
			.setIcon(R.drawable.ic_collections_white_24dp)
			.setView(dText)				
			.setNegativeButton("取消", null)
			.setPositiveButton("確定", new OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.d(TAG,"AlbumName: " + dText.getText().toString());
					createCloudAlbum(name, email, dText.getText().toString());						
				}
			})
			.show();
			return true;
		case R.id.action_share_album:
			
			
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
