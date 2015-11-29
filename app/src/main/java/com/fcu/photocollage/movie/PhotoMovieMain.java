package com.fcu.photocollage.movie;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GestureDetectorCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.fcu.photocollage.R;
import com.fcu.photocollage.cloudalbum.CloudAlbumItem;
import com.fcu.photocollage.cloudalbum.CloudPhotoItem;
import com.fcu.photocollage.cloudalbum.CreateCloudAlbum;
import com.fcu.photocollage.cloudalbum.UploadPhoto;
import com.fcu.photocollage.imagepicker.Utility;
import com.fcu.photocollage.library.DateTool;
import com.fcu.photocollage.library.FileTool;
import com.fcu.photocollage.library.ImageTool;
import com.fcu.photocollage.library.RecordTool;
import com.fcu.photocollage.member.SQLiteHandler;
import com.fcu.photocollage.member.SessionManager;
import com.fcu.photocollage.speechtag.MyRecoder;
import com.fcu.photocollage.speechtag.UploadRecord;

public class PhotoMovieMain extends Fragment {

	//region 宣告
	private final int PHOTO_MAX = 10;			// 最大相片張數
	private static final String TAG = "PhotoMovieMain";
	private SQLiteHandler db;
	private SessionManager session;
	private String name;						// 使用者姓名
	private int uid;							// 使用者編號
	private CloudAlbumItem newAlbum;			// 新相簿資訊
	private EditText dText;						// 建立相簿輸入框
	private ArrayList<String> paths;			// 儲存圖片路徑
	private ArrayList<CloudPhotoItem> cPaths; 	// 儲存雲端圖片路徑
	private ArrayList<Photo> pList; 			// 照片清單
	private ArrayList<Photo> rList;				// 有語音的照片清單
	private String musicPath; 					// 儲存音樂路徑
	private FloatingActionButton btnGenerate; 	// 產生按鈕
	private ImageButton btnAddPic; 				// 增加圖片按鈕
	private ImageButton btnAddMus; 				// 增加音樂按鈕
	private ImageButton btnAdjSize; 			// 調整尺寸
	private ScrollView scrollPhoto;				// 圖片捲軸
	private ArrayList<ImageButton> recBtnList;	// 語音按鈕集合
	private LinearLayout linelay; 				// 圖片縮圖線性布局
	private final static int MUSIC = 11;
	private final static int PHOTO = 22;
	private boolean hasMusic = false;			// 是否加入音樂旗標
	private int photoCount = 0;					// 相片總數
	private String tempPath = "/sdcard/PCtemp"; // 暫存資料夾路徑
	private ProgressDialog progressDialog;		// 進度條
	private AlertDialog dialog;					// 錄音彈出視窗
	private ImageButton record_button;			// 錄音按鈕
	private Button record_ok;					// 錄音確定按鈕
	private Button record_cancel;				// 錄音取消按鈕
	private boolean isRecording = false;		// 是否加入語音旗標
	private ProgressBar record_timeBar;			// 錄音時間條
	private Chronometer record_timeText;		// 錄音時間顯示
	private MyRecoder myRecoder;
	private View record_view;					// 錄音介面
	private int currentIndex;					// 當前圖片索引
	private MediaPlayer mediaPlayer;
	private LayoutInflater inflater;
	private AsyncTask recordTask;
	private boolean scrollDown;					// 向下捲動開關
	private boolean scrollUp;					// 向上捲動開關
	private float lastY;						// 上個座標
	View thisView;
	//endregion

	public PhotoMovieMain() {
        // Empty constructor required for fragment subclasses
    }

	//region 元件初始化
	private void initializeVariables() {
		btnGenerate = (FloatingActionButton) thisView.findViewById(R.id.btn_generate);
		btnAddPic = (ImageButton) thisView.findViewById(R.id.btn_addPic);
		btnAddMus = (ImageButton) thisView.findViewById(R.id.btn_addMus);
		btnAdjSize = (ImageButton) thisView.findViewById(R.id.btn_adjSize);
		musicPath = null;
		linelay = (LinearLayout) thisView.findViewById(R.id.anogallery);
		inflater = LayoutInflater.from(thisView.getContext());
		record_view = inflater.inflate(R.layout.activity_record, null);
		record_button = (ImageButton) record_view.findViewById(R.id.record_button);
		record_timeBar = (ProgressBar) record_view.findViewById(R.id.record_time);
		record_timeText = (Chronometer) record_view.findViewById(R.id.record_chronometer);
		record_ok = (Button) record_view.findViewById(R.id.record_ok);
		record_cancel = (Button) record_view.findViewById(R.id.record_cancel);
		scrollPhoto = (ScrollView) thisView.findViewById(R.id.honScview);
		scrollDown = false;
		scrollUp = false;
		lastY = 0;
	}
	//endregion


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		thisView = inflater.inflate(R.layout.fragment_photomovie, container, false);

	    // 變數初始化
	    initializeVariables();

		// 建立暫存資料夾
		FileTool.createNewFolder(tempPath);

		pList = new ArrayList<Photo>();
		recBtnList = new ArrayList<ImageButton>();

		// SqLite database handler
		db = new SQLiteHandler(getActivity().getApplicationContext());

		// session manager
		session = new SessionManager(getActivity().getApplicationContext());

		if (session.isLoggedIn()) {
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
				}
			}
		});
		//endregion

		//region 產生電影
		btnGenerate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//region 測試播放介面
				// 切換預覽頁面
//				Fragment movieViewFg = new MovieView();
//				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//				Bundle bundle = new Bundle();
//				bundle.putInt("uid", 1);
//				movieViewFg.setArguments(bundle);
//				fragmentManager
//						.beginTransaction()
//						.replace(R.id.content_frame,  movieViewFg)
//						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//						.addToBackStack(null).commit();
//				Intent it = new Intent();
//				it.setClass(getActivity(), MovieView.class);
//				it.putExtra("uid", 1);
//				startActivity(it);
				//endregion

				if (pList.size() > 0 && pList.size() <= PHOTO_MAX) {
					ViewGroup viewGroup = linelay;
					for (int i = 0; i < viewGroup.getChildCount(); i++) {
						ImageView imageView = (ImageView) ((ViewGroup) viewGroup.getChildAt(i)).getChildAt(0);
						Bitmap bmp = ImageTool.getMagicDrawingCache(imageView);

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
									((TextView) progressDialog.findViewById(R.id.pg_text)).setTextColor(Color.WHITE);
									progressDialog.getWindow().setBackgroundDrawableResource(R.color.material_drawer_dark_background);    //背景透明
									newAlbum = new CloudAlbumItem(0, dText.getText().toString(), 12345, 0, Integer.toString(R.mipmap.ic_add_white_36dp), name, DateTool.getCurrentTime("yyyy-MM-dd HH:mm:ss"));
									CreateCloudAlbum createCloudAlbum = new CreateCloudAlbum(handler, uid, getString(R.string.createCloudAlbum), newAlbum);
									Thread createTd = new Thread(createCloudAlbum);
									createTd.start();
								}
							})
							.show();
				} else if (pList.size() > PHOTO_MAX) {
					Toast.makeText(getActivity(), "最多只能10張哦！", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), "記得加入圖片哦！", Toast.LENGTH_SHORT).show();
				}
			}

		});
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
				Fragment movieViewFg = new MovieView();
				FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
				Bundle bundle = new Bundle();
				bundle.putInt("uid", uid);
				movieViewFg.setArguments(bundle);
				fragmentManager
						.beginTransaction()
						.replace(R.id.content_frame,  movieViewFg)
						.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
						.addToBackStack(null).commit();
			}
			else if(result.equals("Error")){
				progressDialog.dismiss();
				Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
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
	private View getImageView(int i, int pCount) {
		LayoutInflater inflater = LayoutInflater.from(thisView.getContext());
		View image = inflater.inflate(R.layout.photomovie_item, null);				// 包含圖片、按鈕的整體物件
		ImageView img = (ImageView)image.findViewById(R.id.image_item);				// 圖片
		ImageButton btnClean = (ImageButton)image.findViewById(R.id.btn_delete);		// 刪除按鈕
		ImageButton btnAddSpe = (ImageButton) image.findViewById(R.id.btn_addSpe);	// 增加語音按鈕
		ImageButton btnAddEff = (ImageButton) image.findViewById(R.id.btn_addEff);	// 增加特效按鈕
		ImageButton btnTime = (ImageButton) image.findViewById(R.id.btn_setSec);		// 調整時間按鈕

		btnAddSpe.setTag(R.id.noRecord);			// 設定語音初始狀態 : 沒有語音檔
		recBtnList.add(btnAddSpe);				// 儲存語音按鈕
		img.setBackgroundColor(Color.BLACK); 	// 設定 ImageView 背景顏色
		img.setPadding(5, 3, 5, 3); 			// 設定 ImageView 內縮
//		img.setAdjustViewBounds(true); 			// 打開才可設定最大寬度和高度
//		img.setMinimumWidth(1000);
//		img.setMinimumHeight(800);
//		img.setMaxHeight(800); 					// 設定 ImageView 最大高度
//		img.setMaxWidth(1000);					// 設定 ImageView 最大寬度
		img.setScaleType(ImageView.ScaleType.CENTER_CROP);	// 設定圖片縮放格式
		// 將 ImageView 置中
//		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		lp.gravity = Gravity.CENTER;
//		img.setLayoutParams(lp);

		File imgFile = new File(paths.get(i));
		// 圖片壓縮
		//Bitmap myBitmap = ScalePicEx(imgFile.getAbsolutePath(), 600, 600);
		//Bitmap myBitmap = revitionImageSize(imgFile.getAbsolutePath(),10);
		//img.setImageBitmap(myBitmap);
		Glide.with(getActivity())
				.load(imgFile)
				.centerCrop()
				.placeholder(R.mipmap.empty_photo)
				.error(R.mipmap.empty_photo)
				.into(img);

		//region 拖移事件
		image.setOnDragListener(new View.OnDragListener() {

			@Override
			public boolean onDrag(final View view, DragEvent event) {
				ViewGroup viewGroup = (ViewGroup) view.getParent();
				DragState dragState = (DragState) event.getLocalState();
				switch (event.getAction()) {
					// 開始拖動事件
					case DragEvent.ACTION_DRAG_STARTED:
						if (view == dragState.view) {
							lastY = 0;
							view.setVisibility(View.INVISIBLE);
						}
						return true;
					// 拖動中改變位置事件
					case DragEvent.ACTION_DRAG_LOCATION: {
						lastY = (lastY == 0) ? event.getY() + view.getY() : lastY;

						Log.d("scroll", "even.getY: " + (event.getY() + view.getY()) + " " + lastY + " " + dragState.view.getScrollY());

						if( event.getY() + view.getY() - lastY > 10 || scrollDown){
							scrollDown = true;
							scrollUp = false;
							scrollPhoto.smoothScrollBy(0,10);
						}
						if( event.getY() + view.getY() - lastY < -10 || scrollUp){
							scrollUp = true;
							scrollDown = false;
							scrollPhoto.smoothScrollBy(0,-10);
						}
						if(event.getY() + view.getY() - lastY > 10 || event.getY() + view.getY() - lastY < -10){
							lastY = event.getY() + view.getY();
						}


						if (view == dragState.view) {
							break;
						}
						int index = viewGroup.indexOfChild(view);
						int dragIndex = viewGroup.indexOfChild(dragState.view);
						if ((index > dragState.index && event.getY() > view.getHeight() / 2)
								|| (index < dragState.index && event.getY() < view.getHeight() / 2)) {

							Log.d(TAG, "index=" + index + " dragState.index=" + dragIndex);
							swapViews(viewGroup, view, index, dragState);
							Collections.swap(pList, index, dragIndex); // 交換 pList
							Collections.swap(recBtnList, index, dragIndex); // 交換 recBtnList

						} else {
							swapViewsBetweenIfNeeded(viewGroup, index, dragState);
						}
						break;
					}
					// 拖動完成事件
					case DragEvent.ACTION_DRAG_ENDED:
						if (view == dragState.view) {
							scrollDown = false;
							scrollUp = false;
							view.setVisibility(View.VISIBLE);
						}
						break;
				}
				return true;
			}

		});
		//endregion

		//region 長按事件
		image.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
				//imgOnClickListener.onClick(view);
				view.startDrag(null, new View.DragShadowBuilder(view),
						new DragState(view), 0);
				return true;
			}
		});
		//endregion

		//region 刪除事件
		btnClean.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewGroup viewGroup = linelay;
				int index = viewGroup.indexOfChild((View)v.getParent());
				Log.d(TAG, "image index: " + index);

				pList.remove(index);
				recBtnList.remove(index);
				viewGroup.removeViewAt(index);
				photoCount--;
			}
		});
		//endregion

		//region 加入語音
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("語音標籤");
		builder.setView(record_view);
		dialog = builder.create();
		btnAddSpe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				record_timeText.setFormat("%s");		// 設定時間顯示格式
				record_timeText.setBase(SystemClock.elapsedRealtime());	// 重置時間
				ViewGroup viewGroup = linelay;
				int index = viewGroup.indexOfChild((View)v.getParent().getParent());
				currentIndex = index;	//紀錄點選照片的索引

				if(v.getTag().equals(R.id.haveRecord)){	// 有語音
					record_button.setImageResource(R.mipmap.ic_play_arrow_white_24dp);
					record_cancel.setEnabled(true);
				}
				else{									// 沒語音
					record_button.setImageResource(R.mipmap.ic_mic_none_white_24dp);
					record_cancel.setEnabled(false);
				}
				dialog.show();
			}
		});
		btnTime.setOnClickListener(setSecond);
		btnAddEff.setOnClickListener(addEffect);
		record_button.setOnClickListener(clickRecord);
		record_ok.setOnClickListener(recordSubmit);
		record_cancel.setOnClickListener(recordSubmit);
		//endregion
		return image;
	}
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

	//region 錄音事件
	/**
	 * 錄音按鈕事件
	 */
	public View.OnClickListener clickRecord = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(pList.get(currentIndex).getRecPath() != null){				// 有語音則撥放
				Uri uri = Uri.parse(pList.get(currentIndex).getRecPath());
				mediaPlayer = MediaPlayer.create(getActivity(), uri);
				mediaPlayer.start();
			}
			else {															// 沒語音則按一下開始錄音
				// 切換
				isRecording = !isRecording;
				// 開始錄音
				if (isRecording) {
					pList.get(currentIndex).createRname();
					// 錄音中，關閉確定和取消按鈕
					record_ok.setEnabled(false);
					record_cancel.setEnabled(false);
					// 設定按鈕圖示為錄音中
					record_button.setImageResource(R.mipmap.ic_stop_white_24dp);
					// 建立錄音物件
					myRecoder = new MyRecoder("/PCtemp/" + pList.get(currentIndex).getRname());
					// 開始錄音
					myRecoder.start();
					// 開始計時
					recordTask = new recordBarTask().execute();	// 時間進度條
					record_timeText.start();					// 文字時間
//				// 建立並執行顯示麥克風音量的AsyncTask物件
//				new MicLevelTask().execute();
				}
				// 停止錄音
				else {
					// 設定按鈕圖示為停止錄音
					record_button.setImageResource(R.mipmap.ic_play_arrow_white_24dp);
//				// 麥克風音量歸零
//				record_volumn.setProgress(0);
					// 停止錄音
					myRecoder.stop();
					//停止計時
					record_timeText.stop();
					recordTask.cancel(true);
					// 錄音完成，開啟確定和取消按鈕
					record_cancel.setEnabled(true);
					record_ok.setEnabled(true);

					pList.get(currentIndex).setRecSec(RecordTool.getRecTime("/sdcard/PCtemp/" + pList.get(currentIndex).getRname()));
					pList.get(currentIndex).setRecPath("/sdcard/PCtemp/" + pList.get(currentIndex).getRname()); // 儲存語音路徑

					recBtnList.get(currentIndex).setTag(R.id.haveRecord);
					recBtnList.get(currentIndex).setImageTintList(getResources().getColorStateList(R.color.md_green_500));
				}
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
				// 取得回傳資料用的Intent物件
				Intent result = getActivity().getIntent();
				// 設定回應結果為確定
				getActivity().setResult(Activity.RESULT_OK, result);
			} else if (v.getId() == R.id.record_cancel) {
				Log.i("DEL", pList.get(currentIndex).getRname());
				File gpFile = new File("/sdcard/PCtemp/" + pList.get(currentIndex).getRname());
				if (gpFile.delete()) {
					pList.get(currentIndex).setRecPath(null); // 移除語音路徑
					pList.get(currentIndex).setRname(null);	// 移除語音檔名
					Toast.makeText(getActivity(), "移除成功", Toast.LENGTH_SHORT).show();
					recBtnList.get(currentIndex).setTag(R.id.noRecord);
					recBtnList.get(currentIndex).setImageTintList(null);
					record_button.setImageResource(R.mipmap.ic_mic_none_white_24dp);
				}
			}
			// 結束
			dialog.dismiss();
		}
	};

	/**
	 *  時間進度條
	 */
	private class recordBarTask extends AsyncTask<Void, Integer, Void> {
		@Override
		protected void onPreExecute() {
			record_timeBar.setProgress(0);
			record_timeText.setBase(SystemClock.elapsedRealtime());
		}

		@Override
		protected Void doInBackground(Void... args) {
			int time = 0;
			while(time <= 100 && !isCancelled()){
				try {
					Thread.sleep(70);
					time++;
					publishProgress(time);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		@Override
		protected void onProgressUpdate(Integer... values) {
			record_timeBar.setProgress(values[0]);
		}
		@Override
		protected void onPostExecute(Void aVoid) {
			record_timeBar.setProgress(0);
			record_timeText.setBase(SystemClock.elapsedRealtime());
			clickRecord.onClick(record_button);
		}

		@Override
		protected void onCancelled() {
			record_timeBar.setProgress(0);
			record_timeText.setBase(SystemClock.elapsedRealtime());
		}
	}
	// 在錄音過程中顯示麥克風音量
//    private class MicLevelTask extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... args) {
//            while (isRecording) {
//                publishProgress();
//
//                try {
//                    Thread.sleep(100);
//                }
//                catch (InterruptedException e) {
//                    Log.d("RecordActivity", e.toString());
//                }
//            }
//            return null;
//        }
//        @Override
//        protected void onProgressUpdate(Void... values) {
//            record_volumn.setProgress((int) myRecoder.getAmplitudeEMA());
//        }
//    }
	//endregion

	//region 設定秒數事件
	public View.OnClickListener setSecond = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ViewGroup viewGroup = linelay;
			//紀錄點選照片的索引
			int index = viewGroup.indexOfChild((View)v.getParent().getParent());
			currentIndex = index;
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("設定照片秒數");
			builder.setIcon(R.mipmap.ic_access_time_white_24dp);
			builder.setSingleChoiceItems(new String[] { "1","2","3", "4", "5", "6", "7",
							"8", "9", "10" }, pList.get(currentIndex).getSec() / 1000 - 1,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							pList.get(currentIndex).setSec((which + 1) * 1000);
							Log.i("SET-SECOND", which + "");
							dialog.dismiss();
						}
					});
			builder.setNegativeButton("取消", null).show();
		}
	};
	//endregion

	//region 加入特效事件
	public View.OnClickListener addEffect = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			ViewGroup viewGroup = linelay;
			//紀錄點選照片的索引
			int index = viewGroup.indexOfChild((View)v.getParent().getParent());
			currentIndex = index;
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("加入特效");
			builder.setIcon(R.mipmap.camera78);
			builder.setSingleChoiceItems(new String[] { "無", "淡入淡出" },
					pList.get(currentIndex).getEffect(),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							pList.get(currentIndex).setEffect(which);
							Log.i("SET-EFFECT", which + "");
							dialog.dismiss();
						}
					});
			builder.setNegativeButton("取消", null).show();
		}
	};
	//endregion

	//region 拖動事件 Method
	private static int getDuration(View view) {
		return view.getResources().getInteger(
				android.R.integer.config_shortAnimTime);
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
		final float viewY = view.getY();
		AppUtils.swapViewGroupChildren(viewGroup, view, dragState.view);
		dragState.index = index;
		AppUtils.postOnPreDraw(view, new Runnable() {
			@Override
			public void run() {
				ObjectAnimator.ofFloat(view, View.Y, viewY, view.getTop())
						.setDuration(getDuration(view)).start();
			}
		});
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
			Log.i("Music - Path", musicPath);
		}		
	}

	@Override  
	public void onResume() {  
	    super.onResume();  	    
	    Log.i(TAG,"onResume");
    	int code = getActivity().getIntent().getIntExtra("code", -1);
		if (code != 100) {
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
			}
		}
		getActivity().getIntent().removeExtra("code");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}



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
