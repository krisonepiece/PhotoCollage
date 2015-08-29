package com.fcu.photocollage.movie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.fcu.photocollage.R;
import com.fcu.photocollage.cloudalbum.CloudAlbumItem;
import com.fcu.photocollage.cloudalbum.CloudPhotoItem;
import com.fcu.photocollage.cloudalbum.CreateCloudAlbum;
import com.fcu.photocollage.cloudalbum.UploadPhoto;
import com.fcu.photocollage.imagepicker.Utility;
import com.fcu.photocollage.library.AnimationTool;
import com.fcu.photocollage.library.DateTool;
import com.fcu.photocollage.library.FileTool;
import com.fcu.photocollage.library.RecordTool;
import com.fcu.photocollage.member.SQLiteHandler;
import com.fcu.photocollage.member.SessionManager;
import com.fcu.photocollage.speechtag.MyRecoder;
import com.fcu.photocollage.speechtag.UploadRecord;

public class PhotoMovieMain extends Fragment {
	private static final String TAG = "PhotoMovieMain";
	private SQLiteHandler db;
	private SessionManager session;
	private String name;
	private int uid;
	private CloudAlbumItem newAlbum;
	private EditText dText;
	private ArrayList<String> paths; // 儲存圖片路徑
	private ArrayList<CloudPhotoItem> cPaths; // 儲存雲端圖片路徑
	private ArrayList<Photo> pList; // 照片清單
	private ArrayList<Photo> rList;	//有語音的照片清單
	private String musicPath; // 儲存音樂路徑
	private Button btnGenerate; // 產生按鈕
	private Button btnAddPic; // 增加圖片按鈕
	private Button btnAddMus; // 增加音樂按鈕
	private Button btnAddSpe; // 增加語音按鈕
	private Button btnAddEff; // 增加特效按鈕
	private ImageButton btnPlay; // 播放語音
	private ImageButton btnTime; // 設定時間
	private ImageButton btnTurnLeft; // 往左翻轉
	private ImageButton btnTurnRight; // 往右翻轉
	private ImageButton btnMoveLeft; // 往左移動
	private ImageButton btnMoveRight; // 往右移動
	private ImageButton btnDelete; // 刪除照片
	private LinearLayout linelay; // 圖片縮圖線性布局
	private int currentPhoto; // 當前選擇的照片編號
	private final static int MUSIC = 11;
	private final static int PHOTO = 22;
	private boolean hasMusic = false;
	private int photoCount = 0;
	private View lastView;
	private String tempPath = "/sdcard/PCtemp"; // 暫存資料夾路徑
	private ProgressDialog progressDialog;
	// 增加語音
	private AlertDialog dialog;
	private ImageButton record_button;
	private Button record_ok;
	private Button record_cancel;
	private boolean isRecording = false;
	private ProgressBar record_volumn;
	private MyRecoder myRecoder;
	private View login_view;
	private MediaPlayer mediaPlayer;
	private LayoutInflater inflater;
	View thisView;

	public PhotoMovieMain() {
        // Empty constructor required for fragment subclasses
    }

	//region 元件初始化
	private void initializeVariables() {
		btnGenerate = (Button) thisView.findViewById(R.id.btn_generate);
		btnAddPic = (Button) thisView.findViewById(R.id.btn_addPic);
		btnAddMus = (Button) thisView.findViewById(R.id.btn_addMus);
		btnAddSpe = (Button) thisView.findViewById(R.id.btn_addSpe);
		btnAddEff = (Button) thisView.findViewById(R.id.btn_addEff);
		btnPlay = (ImageButton) thisView.findViewById(R.id.btn_play);
		btnTime = (ImageButton) thisView.findViewById(R.id.btn_setSec);
		btnTurnLeft = (ImageButton) thisView.findViewById(R.id.btn_turnLeft);
		btnTurnRight = (ImageButton) thisView.findViewById(R.id.btn_turnRight);
		btnMoveLeft = (ImageButton) thisView.findViewById(R.id.btn_moveLeft);
		btnMoveRight = (ImageButton) thisView.findViewById(R.id.btn_moveRight);
		btnDelete = (ImageButton) thisView.findViewById(R.id.btn_delete);
		musicPath = null;
		linelay = (LinearLayout) thisView.findViewById(R.id.anogallery);
		inflater = LayoutInflater.from(thisView.getContext());
		login_view = inflater.inflate(R.layout.activity_record, null);
		record_button = (ImageButton) login_view.findViewById(R.id.record_button);
		record_volumn = (ProgressBar) login_view.findViewById(R.id.record_volumn);
		record_ok = (Button) login_view.findViewById(R.id.record_ok);
		record_cancel = (Button) login_view.findViewById(R.id.record_cancel);		
	}
	//endregion

	//region 按鈕狀態初始化
	private void initializeBtn() {
		btnPlay.setEnabled(false);
		btnPlay.setBackgroundColor(getResources().getColor(R.color.green_300));
		btnTime.setEnabled(false);
		btnTime.setBackgroundColor(getResources().getColor(R.color.green_300));
		btnTurnLeft.setEnabled(false);
		btnTurnLeft.setBackgroundColor(getResources().getColor(R.color.green_300));
		btnTurnRight.setEnabled(false);
		btnTurnRight.setBackgroundColor(getResources().getColor(R.color.green_300));
		btnMoveLeft.setEnabled(false);
		btnMoveLeft.setBackgroundColor(getResources().getColor(R.color.green_300));
		btnMoveRight.setEnabled(false);
		btnMoveRight.setBackgroundColor(getResources().getColor(R.color.green_300));
		btnDelete.setEnabled(false);
		btnDelete.setBackgroundColor(getResources().getColor(R.color.green_300));
		btnAddSpe.setEnabled(false);
		btnAddSpe.setBackgroundColor(getResources().getColor(R.color.grey));
		btnAddEff.setEnabled(false);
		btnAddEff.setBackgroundColor(getResources().getColor(R.color.grey));
		btnGenerate.setEnabled(false);
		btnGenerate.setBackgroundColor(getResources().getColor(R.color.green_300));
	}
	//endregion

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		thisView = inflater.inflate(R.layout.fragment_photomovie, container, false);
	    // 變數初始化
	    initializeVariables();
	    // 按鈕狀態初始化
	    initializeBtn();	    
		// 建立暫存資料夾
		FileTool.createNewFolder(tempPath);

		pList = new ArrayList<Photo>();

		// SqLite database handler
		db = new SQLiteHandler(getActivity().getApplicationContext());

		// session manager
		session = new SessionManager(getActivity().getApplicationContext());

		if (session.isLoggedIn()) {
			// Fetching user details from sqlite
			HashMap<String, String> user = db.getUserDetails();

			name = user.get("name");
			uid = Integer.parseInt(user.get("uid"));
		}

		//region 加入圖片
		btnAddPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(),
						com.fcu.photocollage.imagepicker.ImagePickerActivity.class);
				startActivityForResult(intent,PHOTO);

			}
		});
		//endregion

		//region 加入音樂
		btnAddMus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hasMusic = !hasMusic;
				if (hasMusic) {
					//mfChoose = new MagicFileChooser(getActivity());
					//mfChoose.showFileChooser("audio/*", "選擇音樂", false, true);
					Intent intent = new Intent();
					intent.setType("audio/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intent, MUSIC);
				} else {
					musicPath = null;
					btnAddMus.setText("加入音樂");
				}

			}
		});
		//endregion

		//region 加入語音
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("語音標籤");
		builder.setView(login_view);
		dialog = builder.create();
		btnAddSpe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btnAddSpe.getText().equals("加入語音")) {
					dialog.show();
				} else {
					Log.i("DEL", pList.get(currentPhoto).getRname());
					File gpFile = new File("/sdcard/PCtemp/" + pList.get(currentPhoto).getRname());
					if (gpFile.delete()) {
						pList.get(currentPhoto).setRecPath(null); // 移除語音路徑
						pList.get(currentPhoto).setRname(null);	// 移除語音檔名
						Toast.makeText(getActivity(), "移除成功",
								Toast.LENGTH_SHORT).show();
						btnPlay.setEnabled(false);
						btnPlay.setAlpha(0.8f);
						btnAddSpe.setText("加入語音");
					}
				}
			}
		});
		//endregion

		//region 產生電影
		btnGenerate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				ViewGroup viewGroup = linelay;
				for (int i = 0; i < viewGroup.getChildCount(); i++) {
					ImageView imageView = (ImageView) viewGroup.getChildAt(i);
					// 先把Drawable轉成Bitmap
					Bitmap bmp = ((BitmapDrawable) imageView.getDrawable())
							.getBitmap();
//					Bitmap bmp = convertViewToBitmap(imageView);
					// 壓縮並建立圖片
					compressAndCreatePhoto(tempPath + "/" + i + ".jpg", bmp,
							Bitmap.CompressFormat.JPEG, 70);
					pList.get(i).setpPath(tempPath + "/" + i + ".jpg");
				}
				//建立相簿
				dText = new EditText(getActivity());
				new AlertDialog.Builder(getActivity()).setTitle("幫相簿取個名字吧")
				.setIcon(R.mipmap.ic_collections_white_24dp)
						.setView(dText)
						.setNegativeButton("取消", null)
						.setPositiveButton("確定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							Log.d(TAG, "AlbumName: " + dText.getText().toString());

							//進度條
							progressDialog = new ProgressDialog(getActivity());
							progressDialog.setCanceledOnTouchOutside(false);
							progressDialog.show();
							/*一定要寫在 show 後面!!*/
							progressDialog.setContentView(R.layout.material_progressbar);    //自定義Layout
							((TextView) progressDialog.findViewById(R.id.pg_text)).setText("請稍後...");
							progressDialog.getWindow().setBackgroundDrawableResource(R.color.alpha);    //背景透明
							newAlbum = new CloudAlbumItem(0, dText.getText().toString(), 0, Integer.toString(R.mipmap.ic_add_white_36dp), name, DateTool.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
							CreateCloudAlbum createCloudAlbum = new CreateCloudAlbum(handler, uid, getString(R.string.createCloudAlbum), newAlbum);
							Thread createTd = new Thread(createCloudAlbum);
							createTd.start();
							}
						})
						.show();
			}
		});
		//endregion

		//region 小按鈕監聽
		btnAddEff.setOnClickListener(addEffect);
		btnPlay.setOnClickListener(onPlay);
		btnTime.setOnClickListener(setSecond);
		btnTurnLeft.setOnClickListener(turn);
		btnTurnRight.setOnClickListener(turn);
		btnMoveLeft.setOnClickListener(move);
		btnMoveRight.setOnClickListener(move);
		btnDelete.setOnClickListener(delete);
		record_ok.setOnClickListener(recordSubmit);
		record_cancel.setOnClickListener(recordSubmit);
		record_button.setOnClickListener(clickRecord);
		//endregion

	    return thisView;
	 }


	//region 接收上傳檔案進度
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
	        String result = data.getString("result");
			((TextView) progressDialog.findViewById(R.id.pg_text)).setText(result);
			if (result.equals("相簿建立完成")) {
				for(int i = 0 ; i < pList.size() ; i++){
					pList.get(i).setAlbumID(newAlbum.getAlbumId());
				}
				shell("photo");
			}
			else if(result.equals("照片上傳完成")){
				rList = new ArrayList<Photo>();
				for(int i = 0 ; i < pList.size() ; i++){
					if( pList.get(i).getRecPath() != null )
						rList.add(pList.get(i));
				}
				if(rList.size() != 0)
					shell("record");
				else if(hasMusic)
					shell("music");
				else
					shell("ffmpeg");
			}
			else if(result.equals("語音上傳完成")){
				//上傳音樂
				if(hasMusic)
					shell("music");
				else
					shell("ffmpeg");
			}
			else if(result.equals("音樂上傳完成")){
				shell("ffmpeg");
			}
			else if(result.equals("完成")){
				progressDialog.dismiss();
				// 清空暫存檔案
				FileTool.deleteFolder(tempPath);
				// 切換預覽頁面
				Intent it = new Intent();
				it.setClass(getActivity(), MovieView.class);
				it.putExtra("uid", uid);
				startActivity(it);
			}
			else if(result.equals("Error")){
				progressDialog.dismiss();
				Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT);
			}
		}
	};
	//endregion

	//region 執行對Server的命令
	private void shell(String file) {
		Thread uploadThread;
 		switch(file){
			case "photo":
				//上傳圖片
				UploadPhoto uploadPhoto = new UploadPhoto(handler, pList, getString(R.string.uploadPhoto), getString(R.string.uploadFile));
				uploadThread = new Thread(uploadPhoto);
				uploadThread.start();
				break;
			case "record":
				//上傳語音
				UploadRecord uploadRecord = new UploadRecord(handler, rList, getString(R.string.uploadRecord), getString(R.string.uploadFile));
				uploadThread = new Thread(uploadRecord);
				uploadThread.start();
				break;
			case "music":
				//上傳音樂
				UploadMusic uploadMusic = new UploadMusic(handler, musicPath, uid,  getString(R.string.uploadFile));
				uploadThread = new Thread(uploadMusic);
				uploadThread.start();
				break;
			case "ffmpeg":
				CallFFmpeg callFFmpeg = new CallFFmpeg(handler, createFFmpegCommend(), uid, getString(R.string.callFFmpeg));
				uploadThread = new Thread(callFFmpeg);
				uploadThread.start();
				break;
		}
	}
	//endregion

	//region 產生FFmpeg命令
	/*
	 * [張數] [Pid][秒數][翻轉][特效][語音] . . [Pid][秒數][翻轉][特效][語音] [音樂]
	 */
	private String createFFmpegCommend() {
		String commend = "";
		int pCount = pList.size();
		commend += pCount + " ";

		for(int i = 0 ; i < pCount ; i++){
			commend += pList.get(i).getPid() + " ";
			commend += (pList.get(i).getRecSec() > pList.get(i).getSec() ? pList.get(i).getRecSec() : pList.get(i).getSec() ) + " ";	// 命令[秒數], 當語音秒數比照片秒數長，則使用語音秒數
			commend += pList.get(i).getTurn() + " ";
			commend += pList.get(i).getEffect() + " ";
			commend += (pList.get(i).getRecPath() != null ? "1" : "0" ) + " ";
		}
		commend += hasMusic ? "1" : "0";
		return commend;
	}
	//endregion

	//region 取得縮圖
	private ImageView getImageView(int i, int pCount) {
		ImageView img = new ImageView(getActivity());
		img.setBackgroundColor(Color.BLACK); // 設定 ImageView 背景顏色
		img.setPadding(5, 3, 5, 3); // 設定 ImageView 內縮
		img.setAdjustViewBounds(true); // 打開才可設定最大寬度和高度
		img.setMaxHeight(500); // 設定 ImageView 最大高度
		img.setMaxWidth(500); // 設定 ImageView 最大寬度
		// 將 ImageView 置中
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;
		img.setLayoutParams(lp);

		File imgFile = new File(paths.get(i));
		// 圖片壓縮
		Bitmap myBitmap = ScalePicEx(imgFile.getAbsolutePath(), 600, 800);

		img.setImageBitmap(myBitmap);
		img.setId(pCount);
		img.setOnClickListener(imgOnClickListener);
		img.setOnDragListener(new View.OnDragListener() {
			//region DragListener listener
			@Override
			public boolean onDrag(final View view, DragEvent event) {
				ViewGroup viewGroup = (ViewGroup) view.getParent();
				DragState dragState = (DragState) event.getLocalState();
				setupDragDelete(btnDelete, viewGroup);
				switch (event.getAction()) {
					// 開始拖動事件
					case DragEvent.ACTION_DRAG_STARTED:
						if (view == dragState.view) {
							view.setVisibility(View.INVISIBLE);
							Log.i("Drag-start", view.getId() + "");
							// listener.onDragStarted();
							btnDelete.setBackgroundColor(getResources().getColor(
									R.color.light_green_500));
							AnimationTool.shakeAnimation(btnDelete);
						}
						return true;
					// 拖動中改變位置事件
					case DragEvent.ACTION_DRAG_LOCATION: {
						if (view == dragState.view) {
							break;
						}
						int index = viewGroup.indexOfChild(view);
						if ((index > dragState.index && event.getX() > view
								.getWidth() / 2)
								|| (index < dragState.index && event.getX() < view
								.getWidth() / 2)) {
							// 更新CurrentPhoto
							if (currentPhoto == view.getId())
								currentPhoto = dragState.view.getId();
							else if (currentPhoto == dragState.view.getId())
								currentPhoto = view.getId();
							Log.i("CurrentPhoto", currentPhoto + "");
							swapViews(viewGroup, view, index, dragState);
							Collections.swap(pList, view.getId(),
									dragState.view.getId()); // 交換 pList
							// 交換 view id
							int tmp = view.getId();
							view.setId(dragState.view.getId());
							dragState.view.setId(tmp);
							Log.i("Drag-swapViews", view.getId() + " " + index
									+ " " + dragState.view.getId());
						} else {
							swapViewsBetweenIfNeeded(viewGroup, index, dragState);
							Log.i("Drag-swapBetween", view.getId() + " " + index
									+ " " + dragState.view.getId());
						}
						break;
					}
					// 拖動完成事件
					case DragEvent.ACTION_DRAG_ENDED:
						if (view == dragState.view) {
							view.setVisibility(View.VISIBLE);
							// listener.onDragEnded();
							Log.i("Drag-end", dragState.view.getId() + "");
							if (btnDelete.isEnabled())
								btnDelete.setBackgroundColor(getResources()
										.getColor(R.color.green_500));
							btnDelete.clearAnimation();
						}
						break;
				}
				return true;
			}
			//endregion
		});

		img.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				imgOnClickListener.onClick(view);
				view.startDrag(null, new View.DragShadowBuilder(view),
						new DragState(view), 0);
				return true;
			}
		});
		return img;
	}
	//endregion

	//region 縮圖點擊事件
	private View.OnClickListener imgOnClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			// 啟用按鈕
			setBtnEnable();
			// 將上個選中的特效還原初始狀態
			if (lastView != null) {
				lastView.setBackgroundColor(Color.BLACK);
				lastView.setAlpha(1f);
				lastView.setPadding(5, 3, 5, 3);
			}

			// 點選圖片時的特效
			v.setBackgroundColor(getResources().getColor(R.color.green_700));
			v.setAlpha(0.8f);
			v.setPadding(6, 6, 6, 6);
			lastView = v;
			currentPhoto = v.getId(); // 設定當前選擇的照片編號

			Log.i("onClickView", Integer.toString(currentPhoto));

			record_ok.setEnabled(false);// 隱藏確定按鈕
			// 判斷加入語音
			if (pList.get(currentPhoto).getRecPath() == null) {
				// 隱藏播放圖示
				btnPlay.setEnabled(false);
				btnPlay.setBackgroundColor(getResources().getColor(R.color.green_300));
				record_button.setImageResource(R.mipmap.ic_mic_none_white_24dp);
				Log.i("TEST", "File not exist");
				// 加入語音
				btnAddSpe.setText("加入語音");
			} else {
				btnPlay.setEnabled(true);
				btnPlay.setBackgroundColor(getResources().getColor(R.color.green_500));
				Log.i("TEST", "File exist");
				// 移除語音
				btnAddSpe.setText("移除語音");
			}
			Toast.makeText(v.getContext(), "您選擇了" + (currentPhoto + 1),
					Toast.LENGTH_SHORT).show();
		}
	};
	//endregion

	//region 圖片壓縮
	/**
	 * 不失真壓縮圖片
	 */
	public Bitmap ScalePicEx(String path, int height, int width) {

		BitmapFactory.Options opts = null;
		opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);
		// 計算圖片縮放比例
		final int minSideLength = Math.min(width, height);
		opts.inSampleSize = computeSampleSize(opts, minSideLength, width
				* height);
		opts.inJustDecodeBounds = false;
		opts.inInputShareable = true;
		opts.inPurgeable = true;
		return BitmapFactory.decodeFile(path, opts);
	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 壓縮並產生圖片
	 */
	protected boolean compressAndCreatePhoto(String path, Bitmap bmp, CompressFormat format, int quality) {
		// 壓縮圖片
		FileOutputStream fop;
		try {
			// 實例化FileOutputStream，參數是生成路徑
			fop = new FileOutputStream(path);
			// 壓縮bitmap寫進outputStream參數：輸出格式輸出質量目標OutputStream
			// 格式可以為jpg,png,jpg不能存儲透明
			bmp.compress(format, quality, fop);
			// 關閉流
			fop.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	//endregion

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("TAG","onActivityResult RequestCode:" + requestCode);

		// 藉由requestCode判斷是否為新增音樂而呼叫的，且data不為null
		if (requestCode == MUSIC && data != null) {
			// 取得音樂路徑uri
			Uri uri = data.getData();
			musicPath = MagicFileChooser.getAbsolutePathFromUri(getActivity(), uri);
			btnAddMus.setText("移除音樂");
			Log.i("Music - Path", musicPath);
		}		
	}
	@Override  
	public void onResume() {  
	    super.onResume();  	    
	    Log.i(TAG,"onResume");
    	int code = getActivity().getIntent().getIntExtra("code", -1);
		if (code != 100 && code != 200) {
			return;
		}
		else if(code == 100){
			Bundle bundle = getActivity().getIntent().getExtras();
			paths = bundle.getStringArrayList("paths");	//取得手機相片路徑

			for (int i = 0; i < paths.size(); i++) {
				// 取得拍攝日期
				String takeDate = Utility.getTakeDate(paths.get(i), "yyyy/MM/dd HH:mm:ss");
				//建立相片
				Photo tmpP = new Photo(paths.get(i), takeDate, 3000, 0, 1, uid);
				// 將相片加入相片群
				pList.add(tmpP);
				linelay.addView(getImageView(i, photoCount));
				Log.i("ADD-PHOTO", Integer.toString(photoCount));
				photoCount++;

				// 啟用產生電影按鈕
				btnGenerate.setEnabled(true);
				btnGenerate.setBackgroundColor(getResources().getColor(R.color.green_500));
			}
		}
		else if(code == 200){
			Bundle bundle = getActivity().getIntent().getExtras();
			cPaths = (ArrayList<CloudPhotoItem>) bundle.getSerializable("cPaths");
			Log.i("CLOUD - URI", cPaths.get(0).getpPath());
			for (int i = 0; i < cPaths.size(); i++) {
				Photo tmpP = new Photo(cPaths.get(i).getId(), cPaths.get(i).getpPath(), cPaths.get(i).getRecPath(), 0, 3000, 0, 1);
				
				// 取得拍攝日期
				String takeDate = cPaths.get(i).getTakeDate();
				if(takeDate != null){
					tmpP.setTakeDate(takeDate);
				}
				else{
					tmpP.setTakeDate("0000-00-00 00:00:00");
				}
	
				pList.add(tmpP); // 將相片加入相片群
				linelay.addView(getImageView(i, photoCount));
				Log.i("ADD-Cloud", Integer.toString(photoCount));
				photoCount++;

				// 啟用產生電影按鈕
				btnGenerate.setEnabled(true);
				btnGenerate.setBackgroundColor(getResources().getColor(
						R.color.green_500));
			}
		}
		getActivity().getIntent().removeExtra("code");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	//region 錄音事件
	/**
	 * 錄音按鈕事件
	 * 
	 * @param view
	 */
	public View.OnClickListener clickRecord = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			// 切換
			isRecording = !isRecording;

			// 開始錄音
			if (isRecording) {
				pList.get(currentPhoto).createRname();
				// 錄音中，關閉確定和取消按鈕
				record_ok.setEnabled(false);
				record_cancel.setEnabled(false);
				// 設定按鈕圖示為錄音中
				record_button.setImageResource(R.mipmap.ic_stop_white_24dp);
				// 建立錄音物件
				myRecoder = new MyRecoder("/PCtemp/" + pList.get(currentPhoto).getRname());
				// 開始錄音
				myRecoder.start();
				// 建立並執行顯示麥克風音量的AsyncTask物件
				new MicLevelTask().execute();

				// 開始辨識，將此code移到某個按鈕的onClick()裡
				// sr.startListening(recognizerIntent);

			}
			// 停止錄音
			else {
				// 設定按鈕圖示為停止錄音
				record_button.setImageResource(R.mipmap.ic_keyboard_voice_white_24dp);
				// 麥克風音量歸零
				record_volumn.setProgress(0);
				// 停止錄音
				myRecoder.stop();
				// 錄音完成，開啟確定和取消按鈕
				record_cancel.setEnabled(true);
				record_ok.setEnabled(true);

				// 停止辨識，將此code移到某個按鈕的onClick()裡
				// 我們平常不需要做這個處理，語音辨識完畢它自己會停
				// 但如果我們想中斷它，或者有異常發生時，就可以呼叫底下的程式碼
				// sr.stopListening();
				// sr.cancel();
			}
		}
	};
	/**
	 * 點擊確定與取消按鈕都會呼叫這個方法
	 *
	 * @param view
	 */
	public View.OnClickListener recordSubmit = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// 確定按鈕
			if (v.getId() == R.id.record_ok) {
				pList.get(currentPhoto).setRecSec(RecordTool.getRecTime("/sdcard/PCtemp/" + pList.get(currentPhoto).getRname()));
				pList.get(currentPhoto).setRecPath("/sdcard/PCtemp/" + pList.get(currentPhoto).getRname()); // 儲存語音路徑
				btnPlay.setEnabled(true);
				btnPlay.setBackgroundColor(getResources().getColor(R.color.green_500));
				btnAddSpe.setText("移除語音");
				record_ok.setEnabled(false);// 隱藏確定按鈕
				// 取得回傳資料用的Intent物件
				Intent result = getActivity().getIntent();
				// 設定回應結果為確定
				getActivity().setResult(Activity.RESULT_OK, result);
			} else if (v.getId() == R.id.record_cancel) {
				record_ok.setEnabled(false);// 隱藏確定按鈕
			}
			// 結束
			dialog.dismiss();
		}
	};
	// 在錄音過程中顯示麥克風音量
    private class MicLevelTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... args) {
            while (isRecording) {
                publishProgress();
 
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    Log.d("RecordActivity", e.toString());
                }
            }
 
            return null;
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            record_volumn.setProgress((int) myRecoder.getAmplitudeEMA());
        } 
    }
	//endregion

	//region 小按鈕事件
	// 播放語音事件
	public View.OnClickListener onPlay = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			Uri uri = Uri.parse("/sdcard/PCtemp/"
					+ pList.get(currentPhoto).getPid() + ".3gp");
			mediaPlayer = MediaPlayer.create(getActivity(), uri);
			mediaPlayer.start();			
		}
	};
	// 設定秒數事件
	public View.OnClickListener setSecond = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("設定照片秒數");
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setSingleChoiceItems(new String[] { "1","2","3", "4", "5", "6", "7",
					"8", "9", "10" }, pList.get(currentPhoto).getSec() / 1000 - 1,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							pList.get(currentPhoto).setSec((which + 1) * 1000);
							Log.i("SET-SECOND", which + "");
							dialog.dismiss();
						}
					});
			builder.setNegativeButton("取消", null).show();
		}		
	};
	// 翻轉事件
	public View.OnClickListener turn = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ViewGroup viewGroup = linelay;
			ImageView currentView = (ImageView) viewGroup.getChildAt(currentPhoto);
			Bitmap bitmap = ((BitmapDrawable) currentView.getDrawable())
					.getBitmap();
			Matrix matrix = new Matrix();
			if (v.getId() == R.id.btn_turnLeft)
				matrix.setRotate(-90);
			else if (v.getId() == R.id.btn_turnRight)
				matrix.setRotate(90);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
			currentView.setImageBitmap(bitmap);			
		}		
	};
	// 移動事件
	public View.OnClickListener move = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			int flag;
			if (v.getId() == R.id.btn_moveLeft && currentPhoto != 0)
				flag = -1;
			else if (v.getId() == R.id.btn_moveRight
					&& currentPhoto != pList.size() - 1)
				flag = 1;
			else
				return;
			ViewGroup viewGroup = linelay;
			View currentView = viewGroup.getChildAt(currentPhoto);
			View moveView = viewGroup.getChildAt(currentPhoto + flag);
			AppUtils.swapViewGroupChildren(viewGroup, moveView, currentView);
			Collections.swap(pList, moveView.getId(), currentView.getId()); // 交換
																			// pList
			// 交換 view id
			int tmp = moveView.getId();
			moveView.setId(currentView.getId());
			currentView.setId(tmp);
			currentPhoto = currentPhoto + flag;			
		}
		
	};
	// 刪除事件
	public View.OnClickListener delete = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			ViewGroup viewGroup = linelay;
			View currentView = viewGroup.getChildAt(currentPhoto);
			for (int i = currentPhoto + 1; i < viewGroup.getChildCount(); i++) {
				viewGroup.getChildAt(i).setId(i - 1);
			}
			pList.remove(currentPhoto);
			viewGroup.removeView(currentView);
			photoCount--;
			// 判斷選擇的照片
			if (viewGroup.getChildAt(currentPhoto) != null) {
				imgOnClickListener.onClick(viewGroup.getChildAt(currentPhoto));
			} else if (viewGroup.getChildAt(currentPhoto - 1) != null) {
				imgOnClickListener.onClick(viewGroup.getChildAt(currentPhoto - 1));
			} else {
				initializeBtn();
			}			
		}		
	};
	// 加入特效事件
	public View.OnClickListener addEffect = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("加入特效");
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setSingleChoiceItems(new String[] { "無", "淡入淡出" },
					pList.get(currentPhoto).getEffect(),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							pList.get(currentPhoto).setEffect(which);
							Log.i("SET-EFFECT", which + "");
							dialog.dismiss();
						}
					});
			builder.setNegativeButton("取消", null).show();
		}
	};
	//endregion

	//region 拖動刪除事件
	public void setupDragDelete(View view, final ViewGroup viewGroup) {
		view.setOnDragListener(new View.OnDragListener() {
			@Override
			public boolean onDrag(View view, DragEvent event) {
				switch (event.getAction()) {
				case DragEvent.ACTION_DRAG_ENTERED:
					view.setActivated(true);
					break;
				case DragEvent.ACTION_DRAG_EXITED:
					view.setActivated(false);
					break;
				case DragEvent.ACTION_DROP:
					DragState dragState = (DragState) event.getLocalState();
					for (int i = dragState.view.getId() + 1; i < viewGroup
							.getChildCount(); i++) {
						viewGroup.getChildAt(i).setId(i - 1);
					}
					pList.remove(dragState.view.getId());
					photoCount--;
					removeView(viewGroup, dragState);
					// 判斷選擇的照片
					if (viewGroup.getChildAt(currentPhoto) != null) {
						imgOnClickListener.onClick(viewGroup
								.getChildAt(currentPhoto));
					} else if (viewGroup.getChildAt(currentPhoto - 1) != null) {
						imgOnClickListener.onClick(viewGroup
								.getChildAt(currentPhoto - 1));
					} else {
						initializeBtn();
					}
					break;
				case DragEvent.ACTION_DRAG_ENDED:
					// NOTE: Needed because ACTION_DRAG_EXITED may not be sent
					// when the drag
					// ends within the view.
					view.setActivated(false);
					break;
				}
				return true;
			}
		});
	}
	private static void removeView(final ViewGroup viewGroup,
								   DragState dragState) {

		final int oldViewGroupLayoutParamsHeight = viewGroup.getLayoutParams().height;
		final int oldViewGroupHeight = viewGroup.getHeight();
		viewGroup.removeView(dragState.view);

		int childCount = viewGroup.getChildCount();
		for (int i = dragState.index; i < childCount; ++i) {
			final View view = viewGroup.getChildAt(i);
			final float viewY = view.getY();
			AppUtils.postOnPreDraw(view, new Runnable() {
				@Override
				public void run() {
					ObjectAnimator.ofFloat(view, View.Y, viewY, view.getTop())
							.setDuration(getDuration(view)).start();
				}
			});
		}
	}
	//endregion

	//region 拖動事件 Method
	private static int getDuration(View view) {
		return view.getResources().getInteger(
				android.R.integer.config_shortAnimTime);
	}

	public static interface DragListener {
		public void onDragStarted();

		public void onDragEnded();
	}

	public static interface OnDragDeletedListener {
		public void onDragDeleted();
	}

	private static class DragState {

		public View view;
		public int index;

		private DragState(View view) {
			this.view = view;
			index = ((ViewGroup) view.getParent()).indexOfChild(view);
		}
	}
	//endregion

	//region 交換圖片軸內的View
	private static void swapViewsBetweenIfNeeded(ViewGroup viewGroup,
			int index, DragState dragState) {
		if (index - dragState.index > 1) {
			int indexAbove = index - 1;
			swapViews(viewGroup, viewGroup.getChildAt(indexAbove), indexAbove,
					dragState);
		} else if (dragState.index - index > 1) {
			int indexBelow = index + 1;
			swapViews(viewGroup, viewGroup.getChildAt(indexBelow), indexBelow,
					dragState);
		}
	}
	private static void swapViews(ViewGroup viewGroup, final View view,
			int index, DragState dragState) {
		swapViewsBetweenIfNeeded(viewGroup, index, dragState);
		final float viewX = view.getX();
		AppUtils.swapViewGroupChildren(viewGroup, view, dragState.view);
		dragState.index = index;
		AppUtils.postOnPreDraw(view, new Runnable() {
			@Override
			public void run() {
				ObjectAnimator.ofFloat(view, View.X, viewX, view.getLeft())
						.setDuration(getDuration(view)).start();
			}
		});
	}
	//endregion

	//region 啟用按鈕
	private void setBtnEnable() {
		btnTime.setEnabled(true);
		btnTime.setBackgroundColor(getResources().getColor(R.color.green_500));
		btnTurnLeft.setEnabled(true);
		btnTurnLeft.setBackgroundColor(getResources().getColor(R.color.green_500));
		btnTurnRight.setEnabled(true);
		btnTurnRight.setBackgroundColor(getResources().getColor(R.color.green_500));
		btnMoveLeft.setEnabled(true);
		btnMoveLeft.setBackgroundColor(getResources().getColor(R.color.green_500));
		btnMoveRight.setEnabled(true);
		btnMoveRight.setBackgroundColor(getResources().getColor(R.color.green_500));
		btnDelete.setEnabled(true);
		btnDelete.setBackgroundColor(getResources().getColor(R.color.green_500));
		btnAddSpe.setEnabled(true);
		btnAddSpe.setBackgroundColor(getResources().getColor(R.color.white));
		btnAddEff.setEnabled(true);
		btnAddEff.setBackgroundColor(getResources().getColor(R.color.white));
	}
	//endregion

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
