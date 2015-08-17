package com.fcu.imagepicker;

import static com.fcu.imagepicker.Utility.isImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

import android.app.ActionBar;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.android.volley.Request.Priority;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.fcu.R;
import com.fcu.member.AppController;
import com.fcu.member.SQLiteHandler;
import com.fcu.member.SessionManager;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class CloudAlbumFragment extends Fragment {
	private static final String TAG = "CloudAlbumFragment";
	private View thisView;
	private GridView gridView;
	private SQLiteHandler db;
	private SessionManager session;
	private ArrayList<CloudAlbumItem> list;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);
		thisView = inflater.inflate(R.layout.fragment_cloud_album, container,
				false);
		gridView = (GridView) thisView.findViewById(R.id.cloud_album_grid);

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

			String name = user.get("name");
			String email = user.get("email");

			list = new ArrayList<CloudAlbumItem>();
			getImagePathsByDB(name, email);
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
		final ArrayList<CloudAlbumItem> aList = new ArrayList<CloudAlbumItem>();
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
									String Ppath = album.getString("Ppath");
									String Aname = album.getString("Aname");
									int Aid = album.getInt("Aid");
									int AlbumSize = album.getInt("AlbumSize");
									Log.d("getAlbum","Response:"+ Pid + "," + Ppath + "," + Aname);
									aList.add(new CloudAlbumItem(Aid, Aname, AlbumSize, Ppath));
								}
								
								CloudAlbumAdapter adapter = new CloudAlbumAdapter(getActivity(), aList);
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
												.replace(R.id.cloud_frame, cloudPhotoFg)
												.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
												.addToBackStack(null).commit();
									}
								});
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d(TAG,"onCreateOptionsMenu");
		MenuItem mi = menu.findItem(R.id.action_check);
		mi.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// super.onOptionsItemSelected(item);
		switch (item.getItemId()) {

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
