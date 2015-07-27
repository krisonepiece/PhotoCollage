package com.fcu.photocollage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.fcu.speechtag.MyRecoder;
import com.fcu.R;
import com.fcu.imagepicker.*;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher.ViewFactory;
import android.widget.Toast;

public class MainActivity extends Activity implements ViewFactory {
	private ArrayList<String> paths; // 儲存圖片路徑
	private ArrayList<Photo> pList; // 照片清單
	private String musicPath; // 儲存音樂路徑
	private String user = "Kris"; // 使用者名稱
	// private DisplayMetrics mPhone; // 手機解析度
	// private TextView textSec; // 秒數文字
	private Button btnGenerate; // 產生按鈕
	private Button btnAddPic; 			// 增加圖片按鈕
	private Button btnAddMus; 			// 增加音樂按鈕
	private Button btnAddSpe; 			// 增加語音按鈕
	private Button btnAddEff; 			// 增加特效按鈕
	private ImageButton btnPlay;		// 播放語音	
	private ImageButton btnTime;		// 設定時間
	private ImageButton btnTurnLeft;	// 往左翻轉
	private ImageButton btnTurnRight;	// 往右翻轉
	private ImageButton btnMoveLeft;	// 往左移動
	private ImageButton btnMoveRight;	// 往右移動
	private ImageButton btnDelete;		// 刪除照片
	// private SeekBar seekBarSec; // 秒數滑動桿
	private LinearLayout linelay; // 圖片縮圖線性布局
	// private ImageSwitcher imgSwi2; // 圖片選擇器
	private int currentPhoto; // 當前選擇的照片編號
	private int pid = 0;
	private final static int MUSIC = 11;
	private boolean hasMusic = false;
	private int photoCount = 0;
	private View lastView;
	Cursor cursor;
	// 上傳圖片
	// private HttpPhotoUpload photoUpload = new
	// HttpPhotoUpload("http://192.168.0.100/php/UploadPhoto.php", user);
	private HttpPhotoUpload photoUpload = new HttpPhotoUpload(
			"http://140.134.26.13/PhotoCollage/php/UploadPhoto.php", user);
	private String tempPath = "/sdcard/PCtemp"; // 暫存資料夾路徑
	// 增加語音
	private AlertDialog dialog;
	private ImageButton record_button;
	private Button record_ok;
	private Button record_cancel;
	private boolean isRecording = false;
	private ProgressBar record_volumn;
	private MyRecoder myRecoder;
	private String fileName = null;
	private View login_view;
	private MediaPlayer mediaPlayer;	
	// private Intent recognizerIntent;
	// private SpeechRecognizer sr;
	private LayoutInflater inflater;

	private void initializeVariables() {
		btnGenerate = (Button) findViewById(R.id.btn_generate);
		btnAddPic = (Button) findViewById(R.id.btn_addPic);
		btnAddMus = (Button) findViewById(R.id.btn_addMus);
		btnAddSpe = (Button) findViewById(R.id.btn_addSpe);
		btnAddEff = (Button) findViewById(R.id.btn_addEff);
		btnPlay = (ImageButton) findViewById(R.id.btn_play);
		btnTime = (ImageButton) findViewById(R.id.btn_setSec);
		btnTurnLeft = (ImageButton) findViewById(R.id.btn_turnLeft);
		btnTurnRight = (ImageButton) findViewById(R.id.btn_turnRight);
		btnMoveLeft = (ImageButton) findViewById(R.id.btn_moveLeft);
		btnMoveRight = (ImageButton) findViewById(R.id.btn_moveRight);
		btnDelete = (ImageButton) findViewById(R.id.btn_delete);
		musicPath = null;

		inflater = LayoutInflater.from(MainActivity.this);
		login_view = inflater.inflate(R.layout.activity_record, null);
		record_button = (ImageButton) login_view
				.findViewById(R.id.record_button);
		record_volumn = (ProgressBar) login_view
				.findViewById(R.id.record_volumn);
		record_ok = (Button) login_view.findViewById(R.id.record_ok);
		record_cancel = (Button) login_view.findViewById(R.id.record_cancel);		
	}
	private void initializeBtn() {
		// 按鈕狀態初始化
				btnPlay.setEnabled(false);
				btnPlay.setBackgroundColor(getResources().getColor(R.color.green_5));
				btnTime.setEnabled(false);
				btnTime.setBackgroundColor(getResources().getColor(R.color.green_5));
				btnTurnLeft.setEnabled(false);
				btnTurnLeft.setBackgroundColor(getResources().getColor(R.color.green_5));
				btnTurnRight.setEnabled(false);
				btnTurnRight.setBackgroundColor(getResources().getColor(R.color.green_5));
				btnMoveLeft.setEnabled(false);
				btnMoveLeft.setBackgroundColor(getResources().getColor(R.color.green_5));
				btnMoveRight.setEnabled(false);
				btnMoveRight.setBackgroundColor(getResources().getColor(R.color.green_5));
				btnDelete.setEnabled(false);
				btnDelete.setBackgroundColor(getResources().getColor(R.color.green_5));
				btnAddSpe.setEnabled(false);
				btnAddSpe.setBackgroundColor(getResources().getColor(R.color.grey));
				btnAddEff.setEnabled(false);
				btnAddEff.setBackgroundColor(getResources().getColor(R.color.grey));
				btnGenerate.setEnabled(false);
				btnGenerate.setBackgroundColor(getResources().getColor(R.color.green_5));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ScreenUtils.initScreen(this);
		//變數初始化
		initializeVariables();
		//按鈕狀態初始化
		initializeBtn();

		// 建立暫存資料夾
		createNewFolder(tempPath);

		// 圖片畫廊
		linelay = (LinearLayout) findViewById(R.id.anogallery);
		/*
		 * imgSwi2 = (ImageSwitcher)findViewById(R.id.imgSw2);
		 * imgSwi2.setFactory(this);
		 * imgSwi2.setInAnimation(AnimationUtils.loadAnimation(this,
		 * android.R.anim.fade_in));
		 * imgSwi2.setOutAnimation(AnimationUtils.loadAnimation(this,
		 * android.R.anim.fade_out));
		 */
		pList = new ArrayList<Photo>();
		/*
		 * for(int i=0; i < thumbImgArr.length; i++) {
		 * linelay.addView(getImageView(i)); }
		 */
		// 加入圖片
		btnAddPic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						com.fcu.imagepicker.PhotoWallActivity.class);
				startActivity(intent);
			}
		});

		// 加入音樂
		btnAddMus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hasMusic = !hasMusic;
				if(hasMusic){
					Intent intent = new Intent();
					intent.setType("audio/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(intent, MUSIC);
				}
				else{					
					musicPath = null;
					btnAddMus.setText("加入音樂");
				}
				

			}
		});
		// 加入語音
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("語音標籤");
		builder.setView(login_view);
		dialog = builder.create();
		btnAddSpe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (btnAddSpe.getText().equals("加入語音")) {
					fileName = pList.get(currentPhoto).getPid() + ".3gp";
					dialog.show();
					// pList.get( currentPhoto ).setRecPath("/sdcard/" +
					// fileName); //儲存語音路徑
					// btnPlay.setVisibility(View.VISIBLE);
					// btnAddSpe.setText("移除語音");
				} else {
					fileName = pList.get(currentPhoto).getPid() + ".3gp";
					Log.i("DEL", fileName);
					File gpFile = new File("/sdcard/PCtemp/" + fileName);
					if (gpFile.delete()) {
						pList.get(currentPhoto).setRecPath(null); // 移除語音路徑
						Toast.makeText(MainActivity.this, "移除成功",
								Toast.LENGTH_SHORT).show();
						btnPlay.setEnabled(false);
						btnPlay.setAlpha(0.8f);
						btnAddSpe.setText("加入語音");
					}
				}
			}
		});
		// 播放語音
		// 建立指定資源的MediaPlayer物件
		/*
		 * btnPlay.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Uri uri =
		 * Uri.parse("/sdcard/" + fileName); mediaPlayer =
		 * MediaPlayer.create(this, uri); mediaPlayer.start(); } });
		 */

		// 產生電影
		btnGenerate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				ViewGroup viewGroup = linelay;
				for (int i = 0; i < viewGroup.getChildCount(); i++) {
					ImageView imageView = (ImageView) viewGroup.getChildAt(i);
					// 先把Drawable轉成Bitmap
					Bitmap bmp = ((BitmapDrawable) imageView.getDrawable())
							.getBitmap();
					// 壓縮並建立圖片
					compressAndCreatePhoto(tempPath + "/" + i + ".jpg", bmp,
							Bitmap.CompressFormat.JPEG, 70);
					pList.get(i).setpPath(tempPath + "/" + i + ".jpg");
				}
				// 上傳
				photoUpload.upload(pList, musicPath);

				// 清空暫存檔案
				deleteFolder(tempPath);

				// 切換預覽頁面
				Intent it = new Intent();
				it.setClass(MainActivity.this, MovieView.class);
				startActivity(it);
			}
		});
		/*
		 * // SeekBar秒數限制 seekBarSec.setOnSeekBarChangeListener(new
		 * OnSeekBarChangeListener() {
		 * 
		 * @Override public void onStopTrackingTouch(SeekBar seekBar) {
		 * 
		 * }
		 * 
		 * @Override public void onStartTrackingTouch(SeekBar seekBar) {
		 * 
		 * }
		 * 
		 * @Override public void onProgressChanged(SeekBar seekBar, int
		 * progress, boolean fromUser) { textSec.setText("秒數：" + (progress +
		 * 1)); pList.get(currentPhoto).setSec(progress + 1);
		 * 
		 * } });
		 */
	}

	/**
	 * 點選縮圖事件
	 */
	private ImageView getImageView(int i, int pCount) {
		// 讀取手機解析度
		// mPhone = new DisplayMetrics();
		// getWindowManager().getDefaultDisplay().getMetrics(mPhone);

		ImageView img = new ImageView(this);
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
		// Bitmap myBitmap =
		// BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		// Bitmap sBitmap = ScalePic(myBitmap, 1.3f, 1.4f);

		img.setImageBitmap(myBitmap);
		img.setId(pCount);
		img.setOnClickListener(imgOnClickListener);
		img.setOnDragListener(new View.OnDragListener() {
			// DragListener listener;
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
						btnDelete.setBackgroundColor(getResources().getColor(R.color.green_4));
						RotateAnimation rotateAnimation = new RotateAnimation(-3.0f, 3.0f, 50.0f, 50.0f);  
						rotateAnimation.setDuration(100);  
						rotateAnimation.setRepeatCount(Animation.INFINITE);  
						rotateAnimation.setRepeatMode(Animation.REVERSE);  
						btnDelete.setAnimation(rotateAnimation);  
						rotateAnimation.start();
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
						if(btnDelete.isEnabled())
							btnDelete.setBackgroundColor(getResources().getColor(R.color.green_1));
						btnDelete.clearAnimation();
					}
					break;
				}
				return true;
			}
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
	/**
	 * ImageView 點擊事件
	 */
	private View.OnClickListener imgOnClickListener = new View.OnClickListener() {
		public void onClick(View v) {
			//啟用按鈕
			setBtnEnable();
			// 將上個選中的特效還原初始狀態
			if (lastView != null) {
				lastView.setBackgroundColor(Color.BLACK);
				lastView.setAlpha(1f);
				lastView.setPadding(5, 3, 5, 3);
			}

			// 點選圖片時的特效
			v.setBackgroundColor(getResources().getColor(R.color.green_3));
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
				btnPlay.setAlpha(0.8f);
				Log.i("TEST", "File not exist");
				// 加入語音
				btnAddSpe.setText("加入語音");
			} else {
				btnPlay.setEnabled(true);
				btnPlay.setAlpha(1f);
				Log.i("TEST", "File exist");
				// 移除語音
				btnAddSpe.setText("移除語音");
			}
			Toast.makeText(v.getContext(), "您選擇了" + (currentPhoto + 1),
					Toast.LENGTH_SHORT).show();
		}
	};
	
	public View makeView() {
		ImageView v1 = new ImageView(this);
		v1.setBackgroundColor(0xFF000000); // 設定背景顏色
		v1.setScaleType(ImageView.ScaleType.FIT_CENTER); // 設定填充方式
		v1.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return v1;
	}

	/**
	 * 壓縮圖片
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public Bitmap ScalePic(Bitmap bitmap, float w, float h) {
		// 轉換為圖片指定大小
		// 獲得圖片的寬高
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// 設置想要的大小
		int newWidth = 800;
		int newHeight = 600;
		// 計算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix參數
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bmResult = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, false);
		return bmResult;
	}

	/**
	 * 不失真壓縮圖片
	 * 
	 * @param path
	 * @param h
	 * @param w
	 * @return
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

		/*
		 * BitmapFactory.Options options =new BitmapFactory.Options();
		 * options.inJustDecodeBounds = true; Bitmap bitmap =
		 * BitmapFactory.decodeFile(path, options); options.inJustDecodeBounds =
		 * false; //計算縮放比 int be = (int)(options.outHeight / h); if (be <= 0) be
		 * = 1; options.inSampleSize = be;
		 * //重新讀入圖片，注意這次要把options.inJustDecodeBounds 設為 false哦 bitmap =
		 * BitmapFactory.decodeFile(path, options); int wg = bitmap.getWidth();
		 * int hg = bitmap.getHeight(); Log.i("PIC",wg + "," + hg); return
		 * bitmap;
		 */
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
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

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
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
	protected boolean compressAndCreatePhoto(String path, Bitmap bmp,
			CompressFormat format, int quality) {
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

	/**
	 * 建立新資料夾
	 * 
	 * @param path
	 */
	protected void createNewFolder(String path) {
		// 建立資料夾
		File sdFile = android.os.Environment.getExternalStorageDirectory();
		File dirFile = new File(path);
		if (!dirFile.exists()) {// 如果資料夾不存在
			dirFile.mkdir();// 建立資料夾
			Log.i("Create-File", path + "");
		}
	}

	/**
	 * 清空資料夾
	 * 
	 * @param path
	 */
	protected void deleteFolder(String path) {
		File delFile = new File(path);
		for (File i : delFile.listFiles()) {
			if (i.exists()) {
				i.delete();
				Log.i("DELETE-File", i.getName() + "");
			}
		}
	}

	/**
	 * 取得路徑
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		int code = intent.getIntExtra("code", -1);
		if (code != 100) {
			return;
		}
		Bundle bundle = intent.getExtras();
		paths = bundle.getStringArrayList("paths");
		Log.i("PHOTO - URI", paths.get(0));
		for (int i = 0; i < paths.size(); i++) {
			Photo tmpP = new Photo(pid, paths.get(i), null, 2, 3000, 0, 1);
			// 取得拍攝日期
			try {
				ExifInterface exifInterface = new ExifInterface(tmpP.getpPath());
				String FDateTime = exifInterface
						.getAttribute(ExifInterface.TAG_DATETIME);
				if (FDateTime != null) {
					// 先行定義時間格式
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy:MM:dd HH:mm:ss");

					// 轉換時間格式
					Date date = (Date) sdf.parse(FDateTime);
					Log.i("GET1", date.toString());
					sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					String date2 = sdf.format(date);
					Log.i("GET2", date2);
					// 加入拍攝時間
					tmpP.setTakeDate(date2);
				} else {
					tmpP.setTakeDate("0000-00-00 00:00:00");
				}
			} catch (IOException e) {
				Log.i("GET", "TakeDate Error");
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			pList.add(tmpP); // 將相片加入相片群
			linelay.addView(getImageView(i, photoCount));
			Log.i("ADD-PHOTO", Integer.toString(photoCount));
			photoCount++;
			pid++;
			//啟用產生電影按鈕
			btnGenerate.setEnabled(true);
			btnGenerate.setBackgroundColor(getResources().getColor(R.color.green_1));
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 藉由requestCode判斷是否為新增音樂而呼叫的，且data不為null
		if (requestCode == MUSIC && data != null) {
			// 取得音樂路徑uri
			Uri uri = data.getData();
			musicPath = MagicFileChooser.getAbsolutePathFromUri(this, uri);
			btnAddMus.setText("移除音樂");
			Log.i("Music - Path", musicPath);
		}
	}

	/**
	 * 點擊確定與取消按鈕都會呼叫這個方法
	 * 
	 * @param view
	 */
	public void onSubmit(View view) {
		// 確定按鈕
		if (view.getId() == R.id.record_ok) {
			// 讀取使用者輸入的標題與內容
			/*
			 * String titleText = title_text.getText().toString(); String
			 * contentText = content_text.getText().toString();
			 */
			pList.get(currentPhoto).setRecSec(getRecTime("/sdcard/PCtemp/" + fileName));
			pList.get(currentPhoto).setRecPath("/sdcard/PCtemp/" + fileName); // 儲存語音路徑
			btnPlay.setEnabled(true);
			btnPlay.setAlpha(1f);
			btnAddSpe.setText("移除語音");
			record_ok.setEnabled(false);// 隱藏確定按鈕
			// 取得回傳資料用的Intent物件
			Intent result = getIntent();
			// 設定標題與內容
			/*
			 * result.putExtra("titleText", titleText);
			 * result.putExtra("contentText", contentText);
			 */

			// 設定回應結果為確定
			setResult(Activity.RESULT_OK, result);
		} else if (view.getId() == R.id.record_cancel) {
			record_ok.setEnabled(false);// 隱藏確定按鈕
		}

		// 結束
		dialog.dismiss();
		// finish();
	}

	/**
	 * 錄音按鈕事件
	 * 
	 * @param view
	 */
	public void clickRecord(View view) {
		// 切換
		isRecording = !isRecording;

		// 開始錄音
		if (isRecording) {
			// 錄音中，關閉確定和取消按鈕
			record_ok.setEnabled(false);
			record_cancel.setEnabled(false);
			// 設定按鈕圖示為錄音中
			record_button.setImageResource(R.drawable.record_red_icon);
			// 建立錄音物件
			myRecoder = new MyRecoder("/PCtemp/" + fileName);
			// 開始錄音
			myRecoder.start();
			// 建立並執行顯示麥克風音量的AsyncTask物件
			// new MicLevelTask().execute();

			// 開始辨識，將此code移到某個按鈕的onClick()裡
			// sr.startListening(recognizerIntent);

		}
		// 停止錄音
		else {
			// 設定按鈕圖示為停止錄音
			record_button.setImageResource(R.drawable.record_dark_icon);
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
	/**
	 * 取得語音長度
	 */
	private int getRecTime(String recName) {
		MediaPlayer mp = new MediaPlayer();
		FileInputStream stream;
		int duration;
		try {
			stream = new FileInputStream(recName);
			mp.setDataSource(stream.getFD());
			stream.close();
			mp.prepare();
			duration = mp.getDuration();
			Log.i("Rec-Time",recName + ":" + duration);
			mp.release();
			return duration;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	// 播放語音事件
	public void onPlay(View view) {
		Uri uri = Uri.parse("/sdcard/PCtemp/"
				+ pList.get(currentPhoto).getPid() + ".3gp");
		mediaPlayer = MediaPlayer.create(this, uri);
		mediaPlayer.start();
	}

	// 設定秒數事件
	public void setSecond(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("設定照片秒數");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setSingleChoiceItems(new String[] { "3", "4", "5", "6", "7",
				"8", "9", "10" }, pList.get(currentPhoto).getSec()/1000 - 3,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						pList.get(currentPhoto).setSec((which + 3) * 1000);
						Log.i("SET-SECOND", which + "");
						dialog.dismiss();
					}
				});
		builder.setNegativeButton("取消", null).show();
	}

	// 翻轉事件
	public void turn(View view) {
		ViewGroup viewGroup = linelay;
		ImageView currentView = (ImageView) viewGroup.getChildAt(currentPhoto);
		Bitmap bitmap = ((BitmapDrawable) currentView.getDrawable())
				.getBitmap();
		Matrix matrix = new Matrix();
		if (view.getId() == R.id.btn_turnLeft)
			matrix.setRotate(-90);
		else if (view.getId() == R.id.btn_turnRight)
			matrix.setRotate(90);
		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		currentView.setImageBitmap(bitmap);
	}

	// 移動事件
	public void move(View view) {
		int flag;
		if (view.getId() == R.id.btn_moveLeft && currentPhoto != 0)
			flag = -1;
		else if (view.getId() == R.id.btn_moveRight
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

	// 刪除事件
	public void delete(View view) {
		ViewGroup viewGroup = linelay;
		View currentView = viewGroup.getChildAt(currentPhoto);
		for (int i = currentPhoto + 1; i < viewGroup.getChildCount(); i++) {
			viewGroup.getChildAt(i).setId(i - 1);
		}
		pList.remove(currentPhoto);
		viewGroup.removeView(currentView);
		photoCount--;
		//判斷選擇的照片
		if( viewGroup.getChildAt(currentPhoto) != null ){
			imgOnClickListener.onClick(viewGroup.getChildAt(currentPhoto));
		}
		else if( viewGroup.getChildAt(currentPhoto - 1) != null ){
			imgOnClickListener.onClick(viewGroup.getChildAt(currentPhoto - 1));
		}
		else{
			initializeBtn();
		}
		
	}

	// 加入特效事件
	public void addEffect(View view) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

	/**
	 * Gets the corresponding path to a file from the given content:// URI
	 * 
	 * @param selectedVideoUri
	 *            The content:// URI to find the file path from
	 * @param contentResolver
	 *            The content resolver to use to perform the query.
	 * @return the file path as a string
	 */
	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };

		// This method was deprecated in API level 11
		// Cursor cursor = managedQuery(contentUri, proj, null, null, null);

		CursorLoader cursorLoader = new CursorLoader(this, contentUri, proj,
				null, null, null);
		Cursor cursor = cursorLoader.loadInBackground();

		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
					//判斷選擇的照片
					if( viewGroup.getChildAt(currentPhoto) != null ){
						imgOnClickListener.onClick(viewGroup.getChildAt(currentPhoto));
					}
					else if( viewGroup.getChildAt(currentPhoto - 1) != null ){
						imgOnClickListener.onClick(viewGroup.getChildAt(currentPhoto - 1));
					}
					else{
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
	/**
	 * 啟用按鈕
	 */
	private void setBtnEnable() {
				btnTime.setEnabled(true);
				btnTime.setBackgroundColor(getResources().getColor(R.color.green_1));
				btnTurnLeft.setEnabled(true);
				btnTurnLeft.setBackgroundColor(getResources().getColor(R.color.green_1));
				btnTurnRight.setEnabled(true);
				btnTurnRight.setBackgroundColor(getResources().getColor(R.color.green_1));
				btnMoveLeft.setEnabled(true);
				btnMoveLeft.setBackgroundColor(getResources().getColor(R.color.green_1));
				btnMoveRight.setEnabled(true);
				btnMoveRight.setBackgroundColor(getResources().getColor(R.color.green_1));
				btnDelete.setEnabled(true);
				btnDelete.setBackgroundColor(getResources().getColor(R.color.green_1));
				btnAddSpe.setEnabled(true);
				btnAddSpe.setBackgroundColor(getResources().getColor(R.color.white));
				btnAddEff.setEnabled(true);
				btnAddEff.setBackgroundColor(getResources().getColor(R.color.white));
	}
	/*
	 * final int newViewGroupHeight = measureViewGroupHeight(viewGroup); if
	 * (viewGroup.getChildCount() > 0) { // Prevent the flash of the new height
	 * before the start of our // animation.
	 * AppUtils.setViewLayoutParamsHeight(viewGroup, oldViewGroupHeight); //
	 * Wait until the OnPreDraw of the last child is called for syncing // the
	 * two animations on // View and ViewGroup. AppUtils.postOnPreDraw(
	 * viewGroup.getChildAt(viewGroup.getChildCount() - 1), new Runnable() {
	 * 
	 * @Override public void run() { animateViewGroupHeight(viewGroup,
	 * oldViewGroupLayoutParamsHeight, oldViewGroupHeight, newViewGroupHeight);
	 * } }); } else { // Animate now since there is no children.
	 * animateViewGroupHeight(viewGroup, oldViewGroupLayoutParamsHeight,
	 * oldViewGroupHeight, newViewGroupHeight); } }
	 */
	/*
	 * private static int measureViewGroupHeight(ViewGroup viewGroup) { View
	 * parent = (View) viewGroup.getParent(); int widthMeasureSpec =
	 * View.MeasureSpec.makeMeasureSpec( parent.getMeasuredWidth() -
	 * parent.getPaddingLeft() - parent.getPaddingRight(),
	 * View.MeasureSpec.AT_MOST); int heightMeasureSpec =
	 * View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
	 * viewGroup.measure(widthMeasureSpec, heightMeasureSpec); return
	 * viewGroup.getMeasuredHeight(); }
	 * 
	 * private static void animateViewGroupHeight(final ViewGroup viewGroup,
	 * final int oldLayoutParamsHeight, int oldHeight, int newHeight) {
	 * ValueAnimator viewGroupAnimator = ValueAnimator.ofInt(oldHeight,
	 * newHeight).setDuration(getDuration(viewGroup)); viewGroupAnimator
	 * .setInterpolator(new AccelerateDecelerateInterpolator());
	 * viewGroupAnimator .addUpdateListener(new
	 * ValueAnimator.AnimatorUpdateListener() {
	 * 
	 * @Override public void onAnimationUpdate(ValueAnimator animation) { int
	 * animatedValue = (int)animation.getAnimatedValue();
	 * AppUtils.setViewLayoutParamsHeight(viewGroup, animatedValue); } });
	 * viewGroupAnimator.addListener(new AnimatorListenerAdapter() {
	 * 
	 * @Override public void onAnimationEnd(Animator animation) {
	 * AppUtils.setViewLayoutParamsHeight(viewGroup, oldLayoutParamsHeight); }
	 * }); viewGroupAnimator.start(); }
	 */
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
}
