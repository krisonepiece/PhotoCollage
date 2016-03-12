package com.fcu.photocollage.cloudalbum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fcu.photocollage.R;
import com.fcu.photocollage.library.DateTool;
import com.fcu.photocollage.member.AppController;
import com.fcu.photocollage.member.SQLiteHandler;
import com.fcu.photocollage.member.SessionManager;

public class CloudAlbumFragment extends Fragment {
	private static final String TAG = "CloudAlbumFragment";
	private View thisView;
	private GridView gridView;
	private SQLiteHandler db;
	private SessionManager session;
	private ArrayList<CloudAlbumItem> aList;
	private CloudAlbumAdapter adapter;
	private int uid;
	private String name;
	private String email;
	private String pcode = "1";
	private EditText dText;
	private EditText editEmail;
	private GridViewMenuListener gvMenuListener;
	private ProgressDialog progressDialog;
	private CloudAlbumItem newAlbum;
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

			uid = Integer.parseInt(user.get("uid"));
			name = user.get("name");
			email = user.get("email");

			getImagePathsByDB(name, email);
			//顯示進度條
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.show();
			/*一定要寫在 show 後面!!*/
			progressDialog.setContentView(R.layout.material_progressbar);	//自定義Layout
			((TextView)progressDialog.findViewById(R.id.pg_text)).setText("載入中...");
			progressDialog.getWindow().setBackgroundDrawableResource(R.color.alpha);	//背景透明
		}
		return thisView;
	}

	/**
	 * 點擊返回時，回到相冊頁面
	 */
	private void backAction() {
		Intent intent = new Intent(getActivity(),
				com.fcu.photocollage.MainActivity.class);
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
		StringRequest strReq = new StringRequest(Request.Method.POST,getString(R.string.getCloudAlbum),new Response.Listener<String>() {
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
									String Uid = Integer.toString(album.getInt("Uid"));
									String CreateDate = album.getString("CreateDate");
									
									Log.d("getAlbum","Response:"+ Pid + "," + Ppath + "," + Aname+ "," + Uid+ "," + CreateDate);
									aList.add(new CloudAlbumItem(Aid, Aname, Pcode, AlbumSize, Ppath, Uid, CreateDate));
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
										bundle.putInt("pcode", aList.get(position).getPcode());
										bundle.putString("Uid", aList.get(position).getUserId());

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
				}, new Response.ErrorListener() {
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
	 * 刪除相簿
	 */
	private void deleteCloudAlbum(final int uid, final int position) {
		// Tag used to cancel the request
		String deleteAlbum_req = "deleteAlbum";		
		StringRequest strReq = new StringRequest(Request.Method.POST,getString(R.string.deleteCloudAlbum),new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d("deleteAlbum", "Response: " + response.toString());
						if(response.toString().contains("Success")){
							
							aList.remove(position);
					        adapter.notifyDataSetChanged();
							Toast.makeText(getActivity(), "Delete success!", Toast.LENGTH_SHORT).show();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("deleteAlbum", "Error: " + error.getMessage());
					}
				}) {

			@Override
			protected Map<String, String> getParams() {
				// Posting params to register url
				Map<String, String> params = new HashMap<String, String>();
				params.put("uid", Integer.toString(uid));
				params.put("albumID", Integer.toString(aList.get(position).getAlbumId()));
				return params;
			}
		};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, deleteAlbum_req);		
	}


	//region 新增協作者
	private void addTeamWorker(final String email, final String Aid, final String pcode) {
		// Tag used to cancel the request
		String addTeamWorker_req = "addTeamWorker";
		StringRequest strReq = new StringRequest(Request.Method.POST,getString(R.string.addTeamWorker),new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				Log.d("addTeamWorker", "Response: " + response.toString());
				try {
					JSONObject jObj = new JSONObject(response);
					if (response != null) {
						// User successfully stored in MySQL
						// Now store the user in sqlite
						String result = jObj.getString("result");
						Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Log.e("addTeamWorker", "Error: " + error.getMessage());
			}
		}) {

			@Override
			protected Map<String, String> getParams() {
				// Posting params to register url
				Map<String, String> params = new HashMap<String, String>();
				params.put("email", email);
				params.put("Aid", Aid);
				params.put("pcode", pcode);
				return params;
			}
		};
		// Adding request to request queue
		AppController.getInstance().addToRequestQueue(strReq, addTeamWorker_req);
	}
	//endregion
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
				editEmail = new EditText(getActivity());
				editEmail.setHint(getString(R.string.share_email));
				editEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				editEmail.setMaxEms(40);
				pcode = "1";
				String[] shareItem = {"新增相片", "刪除相片", "編輯相片", "下載相片"};
				new AlertDialog.Builder(getActivity()).setTitle("新增協作者")
						.setView(editEmail)
						.setMultiChoiceItems(shareItem, new boolean[] {false, false, false, false}, new DialogInterface.OnMultiChoiceClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which, boolean isChecked) {
								if(isChecked){
									pcode += Integer.toString(which + 2);
								}
								else{
									pcode.replaceAll(Integer.toString(which + 2), "");
								}
							}
						})
						.setNegativeButton("取消", null)
						.setPositiveButton("新增協作者", new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								addTeamWorker(editEmail.getText().toString(), Integer.toString(aList.get(info.position).getAlbumId()), pcode);
							}
						})
						.show();
						//.setContentView(R.layout.dialog_sharealbum);
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
        				deleteCloudAlbum(uid, info.position);
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
			.setIcon(R.mipmap.ic_collections_white_24dp)
			.setView(dText)				
			.setNegativeButton("取消", null)
			.setPositiveButton("確定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Log.d(TAG, "AlbumName: " + dText.getText().toString());
					newAlbum = new CloudAlbumItem(0, dText.getText().toString(), 12345, 0, Integer.toString(R.mipmap.ic_add_white_36dp), Integer.toString(uid), DateTool.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
					CreateCloudAlbum createCloudAlbum = new CreateCloudAlbum(createHandle, uid, getString(R.string.createCloudAlbum), newAlbum);
					Thread createTd = new Thread(createCloudAlbum);
					createTd.start();
					//createCloudAlbum(name, email, dText.getText().toString());
				}
			})
			.show();
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

	/**
	 * 接收建立相簿進度
	 */
	private Handler createHandle = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
			String result = data.getString("result");
			if(result.contains("完成")){
				aList.add(newAlbum);
				adapter.notifyDataSetChanged();
				showToast("建立成功");
			}
			else
				showToast("建立失敗");


		}
	};
	public void showToast(String text){
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}
}
