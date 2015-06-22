package com.fcu.imagepicker;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import com.fcu.R;
import com.fcu.photocollage.MainActivity;



import static com.fcu.imagepicker.Utility.isImage;

/**
 * 選擇照片頁面
 */
public class PhotoWallActivity extends Activity {

	private TextView titleTV;
    private ArrayList<String> list;
    private GridView mPhotoWall;
    private PhotoWallAdapter adapter;

    /**
     * 當前資料夾路徑
     */
    private String currentFolder = null;
    /**
     * 當前顯示的是否為最近相片
     */
    private boolean isLatest = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_wall);

        titleTV = (TextView) findViewById(R.id.text_bar);
        Button backBtn = (Button) findViewById(R.id.btn_back);
        Button confirmBtn = (Button) findViewById(R.id.btn_select);
        backBtn.setVisibility(View.VISIBLE);
        confirmBtn.setVisibility(View.VISIBLE);

        mPhotoWall = (GridView) findViewById(R.id.grid_photowall);
        list = getLatestImagePaths(100);
        adapter = new PhotoWallAdapter(this, list);
        mPhotoWall.setAdapter(adapter);

        //选择照片完成
        confirmBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //选择图片完成,回到起始页面
                ArrayList<String> paths = getSelectImagePaths();

                Intent intent = new Intent(PhotoWallActivity.this, com.fcu.photocollage.MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("code", paths != null ? 100 : 101);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("paths1", paths);
                intent.putExtras(bundle);
                //intent.putStringArrayListExtra("paths", paths);
                startActivity(intent);
            }
        });

        //点击返回，回到选择相册页面
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	Intent intent = new Intent(PhotoWallActivity.this, com.fcu.photocollage.MainActivity.class);
            	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            	startActivity(intent);
            	//backAction();
            }
        });
    }

    /**
     * 第一次跳转至相册页面时，传递最新照片信息
     */
    private boolean firstIn = true;

    /**
     * 点击返回时，跳转至相册页面
     */
    private void backAction() {
        Intent intent = new Intent(this, MainActivity.class);
        
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        //传递“最近照片”分类信息
        if (firstIn) {
            if (list != null && list.size() > 0) {
                intent.putExtra("latest_count", list.size());
                intent.putExtra("latest_first_img", list.get(0));
            }
            firstIn = false;
        }

        startActivity(intent);
        //动画
        overridePendingTransition(R.anim.in_from_left, R.anim.out_from_right);
    }
/*
    //重写返回键
    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backAction();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
*/
    
     /* 根据图片所属文件夹路径，刷新页面
     */
    private void updateView(int code, String folderPath) {
        list.clear();
        adapter.clearSelectionMap();
        adapter.notifyDataSetChanged();

        if (code == 100) {   //某个相册
            int lastSeparator = folderPath.lastIndexOf(File.separator);
            String folderName = folderPath.substring(lastSeparator + 1);
            titleTV.setText(folderName);
            list.addAll(getAllImagePathsByFolder(folderPath));
        } else if (code == 200) {  //最近照片
            //titleTV.setText(R.string.latest_image);
            list.addAll(getLatestImagePaths(100));
        }

        adapter.notifyDataSetChanged();
        if (list.size() > 0) {
            //滚动至顶部
            mPhotoWall.smoothScrollToPosition(0);
        }
    }


    /**
     * 获取指定路径下的所有图片文件。
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
     * 使用ContentProvider读取SD卡最近图片。
     */
    private ArrayList<String> getLatestImagePaths(int maxCount) {
        Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String key_MIME_TYPE = MediaStore.Images.Media.MIME_TYPE;
        String key_DATA = MediaStore.Images.Media.DATA;

        ContentResolver mContentResolver = getContentResolver();

        // 只查询jpg和png的图片,按最新修改排序
        Cursor cursor = mContentResolver.query(mImageUri, new String[]{key_DATA},
                key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=? or " + key_MIME_TYPE + "=?",
                new String[]{"image/jpg", "image/jpeg", "image/png"},
                MediaStore.Images.Media.DATE_MODIFIED);

        ArrayList<String> latestImagePaths = null;
        if (cursor != null) {
            //从最新的图片开始读取.
            //当cursor中没有数据时，cursor.moveToLast()将返回false
            if (cursor.moveToLast()) {
                latestImagePaths = new ArrayList<String>();

                while (true) {
                    // 获取图片的路径
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

    //获取已选择的图片路径
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
/*
    //从相册页面跳转至此页
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //动画
        overridePendingTransition(R.anim.in_from_right, R.anim.out_from_left);

        int code = intent.getIntExtra("code", -1);
        if (code == 100) {
            //某个相册
            String folderPath = intent.getStringExtra("folderPath");
            if (isLatest || (folderPath != null && !folderPath.equals(currentFolder))) {
                currentFolder = folderPath;
                updateView(100, currentFolder);
                isLatest = false;
            }
        } else if (code == 200) {
            //“最近照片”
            if (!isLatest) {
                updateView(200, null);
                isLatest = true;
            }
        }
    }*/
}
