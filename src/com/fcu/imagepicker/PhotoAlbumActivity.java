package com.fcu.imagepicker;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.fcu.R;
import com.fcu.photocollage.MainActivity;
import static com.fcu.imagepicker.Utility.isImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * 分相冊查看SD卡所有圖片。
 */
public class PhotoAlbumActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_album);

        if (!Utility.isSDcardOK()) {
            Utility.showToast(this, "SD卡不可用。");
            return;
        }

        Intent t = getIntent();
        if (!t.hasExtra("latest_count")) {
            return;
        }

        TextView titleTV = (TextView) findViewById(R.id.topbar_title_tv);
        titleTV.setText(R.string.select_album);

        Button cancelBtn = (Button) findViewById(R.id.topbar_right_btn);
        cancelBtn.setText(R.string.main_cancel);
        cancelBtn.setVisibility(View.VISIBLE);

        ListView listView = (ListView) findViewById(R.id.select_img_listView);

//        //第一種方式：使用file
//        File rootFile = new File(Utility.getSDcardRoot());
//        //屏蔽/mnt/sdcard/DCIM/.thumbnails目錄
//        String ignorePath = rootFile + File.separator + "DCIM" + File.separator + ".thumbnails";
//        getImagePathsByFile(rootFile, ignorePath);

        //第二種方式：使用ContentProvider。（效率更高）
        final ArrayList<PhotoAlbumLVItem> list = new ArrayList<PhotoAlbumLVItem>();
        //「最近照片」
        list.add(new PhotoAlbumLVItem(getResources().getString(R.string.latest_image),
                t.getIntExtra("latest_count", -1), t.getStringExtra("latest_first_img")));
        //相冊
        list.addAll(getImagePathsByContentProvider());

        PhotoAlbumLVAdapter adapter = new PhotoAlbumLVAdapter(this, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PhotoAlbumActivity.this, PhotoWallActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                //第一行為「最近照片」
                if (position == 0) {
                    intent.putExtra("code", 200);
                } else {
                    intent.putExtra("code", 100);
                    intent.putExtra("folderPath", list.get(position).getPathName());
                }
                startActivity(intent);
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消，回到主頁面
                backAction();
            }
        });
    }

    /**
     * 點擊返回時，回到相冊頁面
     */
    private void backAction() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    //重寫返回鍵
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backAction();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 獲取目錄中圖片的個數。
     */
    private int getImageCount(File folder) {
        int count = 0;
        if(folder.listFiles() != null){
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
    	if(folder.listFiles() != null){
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
//        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//
//        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
//        String key_DATA = MediaStore.Images.Media.DATA;
//
//        ContentResolver mContentResolver = getContentResolver();
//
//        // 只查詢jpg和png的圖片
//        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA},
//                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
//                new String[]{"image/jpg", "image/jpeg", "image/png"},
//                MediaStore.Images.Media.DATE_MODIFIED);

     // which image properties are we querying
        String[] projection = new String[]{
        		MediaStore.Images.Media.DATA,
        		MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };
        String groupby =  "1=1) group by (" + MediaStore.Images.Media.BUCKET_DISPLAY_NAME; 
        // Get the base URI for the People table in the Contacts content provider.
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        
        // Make the query.
        ContentResolver mContentResolver = getContentResolver();
        Cursor cursor = mContentResolver.query(mImageUri,
                projection, // Which columns to return
                groupby,         // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null          // Ordering
                );
        
        ArrayList<PhotoAlbumLVItem> list = null;
        if (cursor != null) {
            if (cursor.moveToLast()) {
                //路徑緩存，防止多次掃瞄同一目錄
                HashSet<String> cachePath = new HashSet<String>();
                list = new ArrayList<PhotoAlbumLVItem>();

                while (true) {
                    // 獲取圖片的路徑
                    String imagePath = cursor.getString(0);

                    File parentFile = new File(imagePath).getParentFile();
                    String parentPath = null;
                    if(parentFile != null)
                    	parentPath = parentFile.getAbsolutePath();
                    	if(parentPath != null){
	                    	//不掃瞄重複路徑
	                    	if (!cachePath.contains(parentPath)) {
	                            list.add(new PhotoAlbumLVItem(parentPath, getImageCount(parentFile),
	                                    getFirstImagePath(parentFile)));
	                            cachePath.add(parentPath);
	                        }
                    	}
                    else
                        Log.i("ALBUM", "Put in cache failed");                    
                    

                    if (!cursor.moveToPrevious()) {
                        break;
                    }
                }
            }

            cursor.close();
        }

        return list;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //動畫
        overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
    }

//    /**
//     * 使用File讀取SD卡所有圖片。
//     */
//    private void getImagePathsByFile(File file, String ignorePath) {
//        if (file.isFile()) {
//            File parentFile = file.getParentFile();
//            String parentFilePath = parentFile.getAbsolutePath();
//
//            if (cachePath.contains(parentFilePath)) {
//                return;
//            }
//
//            if (isImage(file.getName())) {
//                list.add(new SelectImgGVItem(parentFilePath, getImageCount(parentFile),
//                        getFirstImagePath(parentFile)));
//                cachePath.add(parentFilePath);
//            }
//        } else {
//            String absolutePath = file.getAbsolutePath();
//            //屏蔽文件夾
//            if (absolutePath.equals(ignorePath)) {
//                return;
//            }
//
//            //不讀取縮略圖
//            if (absolutePath.contains("thumb")) {
//                return;
//            }
//
//            //不讀取層級超過5的
//            if (Utility.countMatches(absolutePath, File.separator) > 5) {
//                return;
//            }
//
//            //不讀取路徑包含.的和隱藏文件
//            if (file.getName().contains(".")) {
//                return;
//            }
//
//            File[] childFiles = file.listFiles();
//            if (childFiles != null) {
//                for (File childFile : childFiles) {
//                    getImagePathsByFile(childFile, ignorePath);
//                }
//            }
//        }
//    }
}
