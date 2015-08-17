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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.fcu.R;

import static com.fcu.imagepicker.Utility.isImage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * 分相冊查看SD卡所有圖片。
 */
public class PhotoAlbumFragment extends Fragment {
	private static final String TAG = "PhotoAlbumFragment";
	private View thisView;
//	private TextView titleTV;
//	private Button cancelBtn;
	private ListView listView;
	
	public void init() { 
//		titleTV = (TextView) thisView.findViewById(R.id.topbar_title_tv);
//		cancelBtn = (Button) thisView.findViewById(R.id.topbar_right_btn);
		listView = (ListView) thisView.findViewById(R.id.select_img_listView);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		thisView = inflater.inflate(R.layout.fragment_photo_album, container, false);
		
		init();	
		
		((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
		if (!Utility.isSDcardOK()) {
			Utility.showToast(getActivity(), "SD卡不可用。");
			backAction();
		}

		//Intent t = getActivity().getIntent();
		if ( getArguments() == null) {
			backAction();
		}
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.select_album);
//		titleTV.setText(R.string.select_album);
//		cancelBtn.setText(R.string.main_cancel);
//		cancelBtn.setVisibility(View.VISIBLE);
		
		
		// //第一種方式：使用file
		// File rootFile = new File(Utility.getSDcardRoot());
		// //屏蔽/mnt/sdcard/DCIM/.thumbnails目錄
		// String ignorePath = rootFile + File.separator + "DCIM" +
		// File.separator + ".thumbnails";
		// getImagePathsByFile(rootFile, ignorePath);

		// 第二種方式：使用ContentProvider。（效率更高）
		final ArrayList<PhotoAlbumLVItem> list = new ArrayList<PhotoAlbumLVItem>();
		// 「最近照片」
		list.add(new PhotoAlbumLVItem(getResources().getString(
				R.string.latest_image), getArguments().getInt("latest_count", -1),
										getArguments().getString("latest_first_img")));
		// 相冊
		list.addAll(getImagePathsByContentProvider());

		PhotoAlbumLVAdapter adapter = new PhotoAlbumLVAdapter(getActivity(), list);
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				Intent intent = new Intent(PhotoAlbumActivity.this,
//						PhotoWallActivity.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				Fragment phonePhotoFg = new PhotoWallFragment();
				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
	            
				// 第一行為「最近照片」
	            Bundle bundle = new Bundle();
	            
				if (position == 0) {
					bundle.putInt("code", 200);
				} else {
					bundle.putInt("code", 100);
					bundle.putString("folderPath", list.get(position).getPathName());
				}
				phonePhotoFg.setArguments(bundle);
				fragmentManager.beginTransaction()
								.replace(R.id.phone_frame, phonePhotoFg)
								.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
								.addToBackStack(null)
								.commit();
			}
		});

//		cancelBtn.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// 取消，回到主頁面
//				backAction();
//			}
//		});
				
		return thisView;
	}

	/**
	 * 點擊返回時，回到相冊頁面
	 */
	private void backAction() {
		Intent intent = new Intent(getActivity(), com.fcu.menu.MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

//	// 重寫返回鍵
//	@Override
//	public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			backAction();
//			return true;
//		} else {
//			return super.onKeyDown(keyCode, event);
//		}
//	}

	/**
	 * 獲取目錄中圖片的個數。
	 */
	private int getImageCount(File folder) {
		int count = 0;
		if (folder.listFiles() != null) {
			File[] files = folder.listFiles();
			for (File file : files) {
				if (isImage(file.getName())) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * 獲取目錄中最新的一張圖片的絕對路徑。
	 */
	private String getFirstImagePath(File folder) {
		if (folder.listFiles() != null) {
			File[] files = folder.listFiles();
			for (int i = files.length - 1; i >= 0; i--) {
				File file = files[i];
				if (isImage(file.getName())) {
					return file.getAbsolutePath();
				}
			}
		}
		return null;
	}

	/**
	 * 使用ContentProvider讀取SD卡所有圖片。
	 */
	private ArrayList<PhotoAlbumLVItem> getImagePathsByContentProvider() {
		// Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		//
		// String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
		// String key_DATA = MediaStore.Images.Media.DATA;
		//
		// ContentResolver mContentResolver = getContentResolver();
		//
		// // 只查詢jpg和png的圖片
		// Cursor cursor = mContentResolver.query(mImageUri, new
		// String[]{key_DATA},
		// key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE +
		// "=?",
		// new String[]{"image/jpg", "image/jpeg", "image/png"},
		// MediaStore.Images.Media.DATE_MODIFIED);

		// which image properties are we querying
		String[] projection = new String[] { MediaStore.Images.Media.DATA,
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
		String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
		String groupby = "1=1 or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE
				+ "=? or " + key_MIME_TYPE + "=? ) group by ("
				+ MediaStore.Images.Media.BUCKET_DISPLAY_NAME;
		// Get the base URI for the People table in the Contacts content
		// provider.
		Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

		// Make the query.
		ContentResolver mContentResolver = getActivity().getContentResolver();
		Cursor cursor = mContentResolver.query(mImageUri, projection, // Which
																		// columns
																		// to
																		// return
				groupby, // Which rows to return (all rows)
				new String[] { "image/jpg", "image/jpeg", "image/png" }, // Selection
																			// arguments
																			// (none)
				null // Ordering
				);

		ArrayList<PhotoAlbumLVItem> list = null;
		if (cursor != null) {
			if (cursor.moveToLast()) {
				// 路徑緩存，防止多次掃瞄同一目錄
				HashSet<String> cachePath = new HashSet<String>();
				list = new ArrayList<PhotoAlbumLVItem>();

				while (true) {
					// 獲取圖片的路徑
					String imagePath = cursor.getString(0);

					File parentFile = new File(imagePath).getParentFile();
					String parentPath = null;
					try {
						if (parentFile != null) {
							parentPath = parentFile.getAbsolutePath();
							// 不掃描路徑含.且層級大於5的資料夾
							if (parentPath != null
									&& !parentPath.contains(".")
									&& Utility.countMatches(parentPath,
											File.separator) <= 5) {
								// 不掃瞄重複路徑
								if (!cachePath.contains(parentPath)) {
									list.add(new PhotoAlbumLVItem(parentPath,
											getImageCount(parentFile),
											getFirstImagePath(parentFile)));
									cachePath.add(parentPath);
								}
							}
						}
					} catch (Exception e) {
						Log.i("ALBUM", "Put in cache failed");
					}

					if (!cursor.moveToPrevious()) {
						break;
					}
				}
			}

			cursor.close();
		}

		return list;
	}

//	@Override
//	public void onResume() {		
//		super.onResume();
//		// 動畫
//		getActivity().overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
//	}

	// /**
	// * 使用File讀取SD卡所有圖片。
	// */
	// private void getImagePathsByFile(File file, String ignorePath) {
	// if (file.isFile()) {
	// File parentFile = file.getParentFile();
	// String parentFilePath = parentFile.getAbsolutePath();
	//
	// if (cachePath.contains(parentFilePath)) {
	// return;
	// }
	//
	// if (isImage(file.getName())) {
	// list.add(new SelectImgGVItem(parentFilePath, getImageCount(parentFile),
	// getFirstImagePath(parentFile)));
	// cachePath.add(parentFilePath);
	// }
	// } else {
	// String absolutePath = file.getAbsolutePath();
	// //屏蔽文件夾
	// if (absolutePath.equals(ignorePath)) {
	// return;
	// }
	//
	// //不讀取縮略圖
	// if (absolutePath.contains("thumb")) {
	// return;
	// }
	//
	// //不讀取層級超過5的
	// if (Utility.countMatches(absolutePath, File.separator) > 5) {
	// return;
	// }
	//
	// //不讀取路徑包含.的和隱藏文件
	// if (file.getName().contains(".")) {
	// return;
	// }
	//
	// File[] childFiles = file.listFiles();
	// if (childFiles != null) {
	// for (File childFile : childFiles) {
	// getImagePathsByFile(childFile, ignorePath);
	// }
	// }
	// }
	// }
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d(TAG,"onCreateOptionsMenu");
//		MenuItem mi = menu.findItem(R.id.action_check);
//		mi.setVisible(false);
		super.onCreateOptionsMenu(menu, inflater);	
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//super.onOptionsItemSelected(item);
		switch(item.getItemId()){
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
