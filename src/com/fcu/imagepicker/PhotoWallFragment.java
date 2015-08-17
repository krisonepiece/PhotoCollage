package com.fcu.imagepicker;

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

import com.fcu.R;

import static com.fcu.imagepicker.Utility.isImage;

/**
 * 選擇照片頁面
 * Created by hanj on 14-10-15.
 */
public class PhotoWallFragment extends Fragment {
	private static final String TAG = "PhotoWallFragment";
//    private TextView titleTV;
//    private Button backBtn;
//    private Button confirmBtn;
    private ArrayList<String> list;
    private GridView mPhotoWall;
    private PhotoWallAdapter adapter;
    private View thisView;

    /**
     * 當前文件夾路徑
     */
    private String currentFolder = null;
    /**
     * 當前展示的是否為最近照片
     */
    private boolean isLatest = true;   
    
    public void init() {    	
//    	titleTV = (TextView) thisView.findViewById(R.id.topbar_title_tv);
//        backBtn = (Button) thisView.findViewById(R.id.topbar_left_btn);
//        confirmBtn = (Button) findViewById(R.id.action_check);
        mPhotoWall = (GridView) thisView.findViewById(R.id.photo_wall_grid);
	}

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
    	super.onCreateView(inflater, container, savedInstanceState);
    	thisView = inflater.inflate(R.layout.fragment_photo_wall, container, false);
    	
    	init();
    	((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
    	((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.latest_image);
//    	titleTV.setText(R.string.latest_image);
//    	backBtn.setText(R.string.photo_album);
//        backBtn.setVisibility(View.VISIBLE);
        //confirmBtn.setText(R.string.main_confirm);
        //confirmBtn.setVisibility(View.VISIBLE);
        list = getLatestImagePaths(100);
        adapter = new PhotoWallAdapter(getActivity(), list);
        mPhotoWall.setAdapter(adapter);
        
//        //選擇照片完成
//        confirmBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //選擇圖片完成,回到起始頁面
//                ArrayList<String> paths = getSelectImagePaths();
//
//                Intent intent = new Intent(getActivity(), com.fcu.menu.MainActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.putExtra("code", paths != null ? 100 : 101);
//                Bundle bundle = new Bundle();
//                bundle.putStringArrayList("paths", paths);
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        });

//        //點擊返回，回到選擇相冊頁面
//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //backAction();
//            }
//        });
    	
		return thisView;
	}

    /**
     * 第一次跳轉至相冊頁面時，傳遞最新照片信息
     */
    private boolean firstIn = true;

    /**
     * 點擊返回時，跳轉至相冊頁面
     */
    private void backAction() {
//        Intent intent = new Intent(this, PhotoAlbumActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        Fragment phoneAlbumFg = new PhotoAlbumFragment();
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        //傳遞「最近照片」分類信息
		Bundle bundle = new Bundle();
        if (firstIn) {
            if (list != null && list.size() > 0) {
            	bundle.putInt("latest_count", list.size());
            	bundle.putString("latest_first_img", list.get(0));
            	phoneAlbumFg.setArguments(bundle);
//                intent.putExtra("latest_count", list.size());
//                intent.putExtra("latest_first_img", list.get(0));
            }
            firstIn = false;
        }
        fragmentManager.beginTransaction()
				        .replace(R.id.phone_frame, phoneAlbumFg)
				        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
						.addToBackStack(null)
						.commit();
        //startActivity(intent);
        //動畫
        //getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
    }

    /**
     * 根據圖片所屬文件夾路徑，刷新頁面
     */
    private void updateView(int code, String folderPath) {
        list.clear();
        adapter.clearSelectionMap();
        adapter.notifyDataSetChanged();

        if (code == 100) {   //某個相冊
            int lastSeparator = folderPath.lastIndexOf(File.separator);
            String folderName = folderPath.substring(lastSeparator + 1);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(folderName);
            list.addAll(getAllImagePathsByFolder(folderPath));
        } else if (code == 200) {  //最近照片
        	((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.latest_image);
            list.addAll(getLatestImagePaths(100));
        }

        adapter.notifyDataSetChanged();
        if (list.size() > 0) {
            //滾動至頂部
            mPhotoWall.smoothScrollToPosition(0);
        }
    }


    /**
     * 獲取指定路徑下的所有圖片文件。
     */
    private ArrayList<String> getAllImagePathsByFolder(String folderPath) {
        File folder = new File(folderPath);
        String[] allFileNames = folder.list();
        if (allFileNames == null || allFileNames.length == 0) {
            return null;
        }

        ArrayList<String> imageFilePaths = new ArrayList<String>();
        for (int i = allFileNames.length - 1; i >= 0; i--) {
            if (isImage(allFileNames[i])) {
                imageFilePaths.add(folderPath + File.separator + allFileNames[i]);
            }
        }

        return imageFilePaths;
    }

    /**
     * 使用ContentProvider讀取SD卡最近圖片。
     */
    private ArrayList<String> getLatestImagePaths(int maxCount) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = getActivity().getContentResolver();

        // 只查詢jpg和png的圖片,按最新修改排序
        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);

        ArrayList<String> latestImagePaths = null;
        if (cursor != null) {
            //從最新的圖片開始讀取.
            //當cursor中沒有數據時，cursor.moveToLast()將返回false
            if (cursor.moveToLast()) {
                latestImagePaths = new ArrayList<String>();

                while (true) {
                    // 獲取圖片的路徑
                    String path = cursor.getString(0);
                    latestImagePaths.add(path);

                    if (latestImagePaths.size() >= maxCount || !cursor.moveToPrevious()) {
                        break;
                    }
                }
            }
            cursor.close();
        }

        return latestImagePaths;
    }

    //獲取已選擇的圖片路徑
    private ArrayList<String> getSelectImagePaths() {
        SparseBooleanArray map = adapter.getSelectionMap();
        if (map.size() == 0) {
            return null;
        }

        ArrayList<String> selectedImageList = new ArrayList<String>();

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
		Log.i("onResume","phone");
		//動畫
        //getActivity().overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);
		
		if(getArguments() != null){
	        int code = getArguments().getInt("code", -1);
	        if (code == 100) {
	            //某個相冊
	            String folderPath = getArguments().getString("folderPath");
	            if (isLatest || (folderPath != null && !folderPath.equals(currentFolder))) {
	                currentFolder = folderPath;
	                updateView(100, currentFolder);
	                isLatest = false;
	            }
	        } else if (code == 200) {
	            //「最近照片」
	            if (!isLatest) {
	                updateView(200, null);
	                isLatest = true;
	            }
	        }
	        getArguments().remove("code");
        }        
	}	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d(TAG,"onCreateOptionsMenu");
//		MenuItem mi = menu.findItem(R.id.action_check);
//		mi.setVisible(true);
		super.onCreateOptionsMenu(menu, inflater);	
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//super.onOptionsItemSelected(item);
		switch(item.getItemId()){
			case R.id.action_check:
				//選擇圖片完成,回到起始頁面
				ArrayList<String> paths = getSelectImagePaths();
				Intent intent = new Intent(getActivity(), com.fcu.menu.MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("code", paths != null ? 100 : 101);
				Bundle bundle = new Bundle();
				bundle.putStringArrayList("paths", paths);
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
