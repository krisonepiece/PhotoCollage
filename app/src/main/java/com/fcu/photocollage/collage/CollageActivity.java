package com.fcu.photocollage.collage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.fcu.photocollage.R;
import com.fcu.photocollage.imagepicker.ImagePickerActivity;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * 主頁面，可跳轉至相冊選擇照片，並在此頁面顯示所選擇的照片。
 */
public class CollageActivity extends Fragment {

	//region 定義
	//private final getActivity() getActivity() = getActivity();
	private static Toast toast;                    //氣泡訊息
	private Bitmap tempBitmap;                        //中間暫存的畫布
	private Canvas tempCanvas;                        //畫布
	private int countpicture = 1;                    //計算照片張數
	private int choosepicture = 0;                    //選擇照片ID
	private int choosepictureZ = 0;
	private int choosePaintSize = 15;                //畫筆粗細
	private int addViewLeft = 50, addViewTop = 50;    //加入照片上下初始設定
	private String textBundle;
	private File dirFile = null;                    //照片要存的路徑
	private RelativeLayout Relativelay;                //相對佈局
	//所有工具列
	private LinearLayout choosecolorlinr, chooseshape, paintSizelinr, eraserSizelinr, drawtool, maintool, choosebackground;
	private ImageView img = null;                        //imageview
	private ImageView backimage;                    //初始背景
	private ImageView Img = null;
	private ImageView paintImgSize, erasersize;        //調整畫筆同時顯示粗，橡皮擦大小

	private Button DeleteButton;                    //刪除按鈕
	private Button DeleteAllButton;                    //清空按鈕
	private Button SaveButton;                        //儲存按鈕
	private Button selectImgBtn;                    //選擇圖片按鈕
	private Button drawBtn;                            //畫筆按鈕
	private Button colorBtn;                        //顏色選擇按鈕
	private Button shapeBtn;                        //圖形選擇按鈕
	private Button drawover;                        //結束畫布模式按鈕
	private Button eraser;                            //橡皮擦按鈕
	private Button undoBtn;                            //上一步按鈕
	private Button cleanAll;                        //清除畫布按鈕
	private Button drawstart;                        //開始畫布按鈕
	private Button copy;                            //複製按鈕
	private Button backgroundBtn;                    //設置背景按鈕

	private Button redBtn;
	private Button orangeBtn;
	private Button yellowBtn;
	private Button greenBtn;
	private Button blueBtn;
	private Button purpleBtn;
	private Button brownBtn;
	private Button grayBtn;
	private Button blackBtn;
	private Button paletteBtn;

	private Button pathBtn;
	private Button pointBtn;
	private Button lineBtn;
	private Button rectangleBtn;
	private Button circleBtn;
	private Button rectanglefallBtn;
	private Button circlefallBtn;
	private Button loveBtn;

	private Button bgBtn;
	private Button bgBtn1;
	private Button bgBtn2;
	private Button bgBtn3;
	private Button bgBtn4;
	private Button bgBtn5;
	private Button bgBtn6;
	private Button bgBtn7;
	private Button bgBtn8;
	private Button bgBtn9;
	private Button bgBtn10;
	private Button bgBtn11;
	private Button bgBtn12;
	private Button bgBtn13;
	private Button bgBtn14;
	private Button bgBtn15;
	private Button bgBtn16;
	private Button bgBtn17;

	//private Button pictureSetBackground;
	private Button upBtn;
	private Button downBtn;
	private Button textBtn;
	private View lastView;                            //上一個選擇的imageview
	private Bitmap drawBitmap = null, saveBitmap = null, tempImage, bitmapcopy;        //畫布，暫存畫布，最終儲存畫布
	private ProgressDialog progressDialog;            //儲存彈出等待視窗
	private Handler mThreadHandler;                    //save(的經紀人)，多執行緒
	private Canvas canvasDraw, canvasSave;            //畫畫工具，畫暫存畫布，畫最終畫布
	private Paint paint;                            //畫筆
	private SeekBar paintSize, eraserSize;            //畫筆粗細seekbar，橡皮擦大小seekbar
	private float startX, BigX, smallX, cutBigX = 0;        //畫布模式下手觸碰開始的Ｘ位置，最大Ｘ位置，最小Ｘ位置，最後切割Ｘ最大位置
	private float startY, BigY, smallY, cutBigY = 0;        //畫布模式下手觸碰開始的Ｙ位置，最大Ｙ位置，最小Ｙ位置，最後切割Ｙ最大位置
	private float cutSmallX = 10000, cutSmallY = 10000;    //切割最小Ｘ跟Ｙ
	private Action curAction = null;                //畫圖方法
	private ActionType type = ActionType.Path;        //儲存畫筆資訊
	private int currentColor = Color.BLACK;            //預設畫筆為黑色为黑色
	private int currentSize = 15;                    //預設的粗细
	private List<Action> mActions;                    //儲存畫布模式動作
	private float[][] undoCutXY;
	private int undoCutXYIndex1 = 1, undoCutXYIndex2 = 0;
	private Boolean photo = false, draw = false;
	private Matrix drawMatrix;
	View thisView;
	//private MediaPlayer mPlayer;

	private void initVariables() {
		//region 佈局設定
		//region 顏色按鈕
		redBtn = (Button) thisView.findViewById(R.id.colorRed);
		orangeBtn = (Button) thisView.findViewById(R.id.colorOrange);
		yellowBtn = (Button) thisView.findViewById(R.id.colorYellow);
		greenBtn = (Button) thisView.findViewById(R.id.colorGreen);
		blueBtn = (Button) thisView.findViewById(R.id.colorBlue);
		purpleBtn = (Button) thisView.findViewById(R.id.colorPurple);
		brownBtn = (Button) thisView.findViewById(R.id.colorBrown);
		grayBtn = (Button) thisView.findViewById(R.id.colorGray);
		blackBtn = (Button) thisView.findViewById(R.id.colorBlack);
		paletteBtn = (Button) thisView.findViewById(R.id.palette);
		//endregion

		//region 形狀按鈕
		pathBtn = (Button) thisView.findViewById(R.id.path);
		pointBtn = (Button) thisView.findViewById(R.id.point);
		lineBtn = (Button) thisView.findViewById(R.id.line);
		rectangleBtn = (Button) thisView.findViewById(R.id.rectangle);
		circleBtn = (Button) thisView.findViewById(R.id.circle);
		rectanglefallBtn = (Button) thisView.findViewById(R.id.rectanglefall);
		circlefallBtn = (Button) thisView.findViewById(R.id.circlefall);
		loveBtn = (Button) thisView.findViewById(R.id.love);
		//endregion

		//region 背景按鈕
		bgBtn = (Button) thisView.findViewById(R.id.background);
		bgBtn1 = (Button) thisView.findViewById(R.id.background1);
		bgBtn2 = (Button) thisView.findViewById(R.id.background2);
		bgBtn3 = (Button) thisView.findViewById(R.id.background3);
		bgBtn4 = (Button) thisView.findViewById(R.id.background4);
		bgBtn5 = (Button) thisView.findViewById(R.id.background5);
		bgBtn6 = (Button) thisView.findViewById(R.id.background6);
		bgBtn7 = (Button) thisView.findViewById(R.id.background7);
		bgBtn8 = (Button) thisView.findViewById(R.id.background8);
		bgBtn9 = (Button) thisView.findViewById(R.id.background9);
		bgBtn10 = (Button) thisView.findViewById(R.id.background10);
		bgBtn11 = (Button) thisView.findViewById(R.id.background11);
		bgBtn12 = (Button) thisView.findViewById(R.id.background12);
		bgBtn13 = (Button) thisView.findViewById(R.id.background13);
		bgBtn14 = (Button) thisView.findViewById(R.id.background14);
		bgBtn15 = (Button) thisView.findViewById(R.id.background15);
		bgBtn16 = (Button) thisView.findViewById(R.id.background16);
		bgBtn17 = (Button) thisView.findViewById(R.id.background17);
		//endregion

		textBtn = (Button) thisView.findViewById(R.id.text);

		//刪除按鈕
		DeleteButton = (Button) thisView.findViewById(R.id.delete);
		//刪除全部
		DeleteAllButton = (Button) thisView.findViewById(R.id.deleteAll);
		//儲存按鈕
		SaveButton = (Button) thisView.findViewById(R.id.save);
		//選擇相簿按鈕
		selectImgBtn = (Button) thisView.findViewById(R.id.main_select_image);
		//複製按鈕
		copy = (Button) thisView.findViewById(R.id.copy);
		//上推按鈕
		upBtn = (Button) thisView.findViewById(R.id.up);
		//下推按鈕
		downBtn = (Button) thisView.findViewById(R.id.down);
		//換背景按鈕
		backgroundBtn = (Button) thisView.findViewById(R.id.backgroundBtn);
		//當前照片設為背景
		//pictureSetBackground = (Button)thisView.findViewById(R.id.pictureSetBackground);
		//初始背景
		backimage = (ImageView) thisView.findViewById(R.id.imageView1);
		backimage.setId(0);
		//畫筆大小顯示
		paintImgSize = (ImageView) thisView.findViewById(R.id.paintsize);
		//橡皮擦大小顯示
		erasersize = (ImageView) thisView.findViewById(R.id.erasersize);
		//相對佈局
		Relativelay = (RelativeLayout) thisView.findViewById(R.id.anogallery);
		//整個畫布工具列
		drawtool = (LinearLayout) thisView.findViewById(R.id.drawtool);
		drawtool.setVisibility(View.GONE);
		//選擇顏色工具列
		choosecolorlinr = (LinearLayout) thisView.findViewById(R.id.choosecolorlinr);
		choosecolorlinr.setVisibility(View.GONE);
		//選擇形狀工具列
		chooseshape = (LinearLayout) thisView.findViewById(R.id.chooseshape);
		chooseshape.setVisibility(View.GONE);
		//選擇畫筆大小工具列
		paintSizelinr = (LinearLayout) thisView.findViewById(R.id.paintSizelinr);
		paintSizelinr.setVisibility(View.GONE);
		//橡皮擦大小工具列
		eraserSizelinr = (LinearLayout) thisView.findViewById(R.id.eraserSizelinr);
		eraserSizelinr.setVisibility(View.GONE);
		//初始工具列
		maintool = (LinearLayout) thisView.findViewById(R.id.maintool);
		//選擇背景工具列
		choosebackground = (LinearLayout) thisView.findViewById(R.id.choosebackground);
		choosebackground.setVisibility(View.GONE);
		//畫筆粗細seekBar
		paintSize = (SeekBar) thisView.findViewById(R.id.paintSize);
		//橡皮擦粗細seekBar
		eraserSize = (SeekBar) thisView.findViewById(R.id.eraserSize);
		//開始畫布按鈕
		drawstart = (Button) thisView.findViewById(R.id.drawstart);
		drawBtn = (Button) thisView.findViewById(R.id.draw);
		//結束畫布按鈕
		drawover = (Button) thisView.findViewById(R.id.drawover);
		drawover.setEnabled(false);
		//橡皮擦按鈕
		eraser = (Button) thisView.findViewById(R.id.eraser);
		eraser.setEnabled(false);
		//清空全部按鈕
		cleanAll = (Button) thisView.findViewById(R.id.cleanAll);
		//選擇顏色按鈕
		colorBtn = (Button) thisView.findViewById(R.id.color);
		colorBtn.setEnabled(false);
		//上一步按鈕
		undoBtn = (Button) thisView.findViewById(R.id.undo);
		undoBtn.setEnabled(false);
		//選擇形狀按鈕
		shapeBtn = (Button) thisView.findViewById(R.id.shape);
		shapeBtn.setEnabled(false);
		//新增畫筆
		paint = new Paint();
		//endregion
	}

	private void initListener() {

		//region 選擇顏色
		redBtn.setOnClickListener(colorchoose);
		orangeBtn.setOnClickListener(colorchoose);
		yellowBtn.setOnClickListener(colorchoose);
		greenBtn.setOnClickListener(colorchoose);
		blueBtn.setOnClickListener(colorchoose);
		purpleBtn.setOnClickListener(colorchoose);
		brownBtn.setOnClickListener(colorchoose);
		grayBtn.setOnClickListener(colorchoose);
		blackBtn.setOnClickListener(colorchoose);
		paletteBtn.setOnClickListener(colorchoose);
		//endregion

		//region 選擇形狀
		pathBtn.setOnClickListener(shapechoose);
		pointBtn.setOnClickListener(shapechoose);
		lineBtn.setOnClickListener(shapechoose);
		rectangleBtn.setOnClickListener(shapechoose);
		circleBtn.setOnClickListener(shapechoose);
		rectanglefallBtn.setOnClickListener(shapechoose);
		circlefallBtn.setOnClickListener(shapechoose);
		loveBtn.setOnClickListener(shapechoose);
		//endregion

		//region 選擇背景
		bgBtn.setOnClickListener(backGroundChange);
		bgBtn1.setOnClickListener(backGroundChange);
		bgBtn2.setOnClickListener(backGroundChange);
		bgBtn3.setOnClickListener(backGroundChange);
		bgBtn4.setOnClickListener(backGroundChange);
		bgBtn5.setOnClickListener(backGroundChange);
		bgBtn6.setOnClickListener(backGroundChange);
		bgBtn7.setOnClickListener(backGroundChange);
		bgBtn8.setOnClickListener(backGroundChange);
		bgBtn9.setOnClickListener(backGroundChange);
		bgBtn10.setOnClickListener(backGroundChange);
		bgBtn11.setOnClickListener(backGroundChange);
		bgBtn12.setOnClickListener(backGroundChange);
		bgBtn13.setOnClickListener(backGroundChange);
		bgBtn14.setOnClickListener(backGroundChange);
		bgBtn15.setOnClickListener(backGroundChange);
		bgBtn16.setOnClickListener(backGroundChange);
		bgBtn17.setOnClickListener(backGroundChange);
		//endregion

		//region 顯示畫筆粗細imageview點擊也能開啟調色盤
		paintImgSize.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//調色盤
				ColorPickerDialog dialog = new ColorPickerDialog(getActivity(), currentColor, "Choose Color",
						new ColorPickerDialog.OnColorChangedListener() {
							public void colorChanged(int color2) {
								//set your color variable
								setColor(color2);
								if (paintSizelinr.getVisibility() == View.VISIBLE) {
									//選完顏色同時也要把畫筆大小的顏色換成所選的
									Bitmap paintSizeBitmap = Bitmap.createBitmap(paintImgSize.getWidth(), paintImgSize.getHeight(), Bitmap.Config.ARGB_8888);
									Canvas paintSizeCanvas = new Canvas(paintSizeBitmap);

									Paint cleancanvas = new Paint();
									cleancanvas.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
									paintSizeCanvas.drawPaint(cleancanvas);
									cleancanvas.setXfermode(new PorterDuffXfermode(Mode.SRC));

									setSize(choosePaintSize);
									Paint seekBarShowPaintSize = new Paint();
									seekBarShowPaintSize.setAntiAlias(true);
									seekBarShowPaintSize.setColor(currentColor);
									seekBarShowPaintSize.setStyle(Paint.Style.FILL);
									seekBarShowPaintSize.setStrokeWidth(choosePaintSize);

									paintSizeCanvas.drawCircle(paintImgSize.getWidth() / 2, paintImgSize.getHeight() / 2, choosePaintSize / 2, seekBarShowPaintSize);
									paintImgSize.setImageBitmap(paintSizeBitmap);
								}
							}
						});
				dialog.show();
			}
		});
		//endregion

		//region 開始畫布模式
		drawstart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("開啟畫布");
				builder.setMessage("要開新畫布還是編輯圖片");
				// Add the buttons
				builder.setPositiveButton("編輯照片", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User clicked OK button
						if (choosepicture != 0) {


							//設定按鈕可否使用
							drawover.setEnabled(true);
							eraser.setEnabled(true);
							colorBtn.setEnabled(true);
							shapeBtn.setEnabled(true);
							undoBtn.setEnabled(true);
							//把主工具列隱藏
							maintool.setVisibility(View.GONE);
							//把畫布工具列顯示
							drawtool.setVisibility(View.VISIBLE);
							//把選擇背景工具列隱藏
							choosebackground.setVisibility(View.GONE);

							//設定畫筆初始模式是為自由曲線
							setType(ActionType.Path);
							setSize(currentSize);
							//新增紀錄陣列
							mActions = new ArrayList<Action>();
							//如果在之前有選擇圖片，把圖片還原
							if (lastView != null)
								lastView.setBackgroundColor(getResources().getColor(R.color.alpha));

							//創建準備要畫畫的Bitmap
							drawBitmap = Bitmap.createBitmap(backimage.getWidth(), backimage.getHeight(), Bitmap.Config.ARGB_8888);
							saveBitmap = Bitmap.createBitmap(backimage.getWidth(), backimage.getHeight(), Bitmap.Config.ARGB_8888);
							canvasDraw = new Canvas(drawBitmap);
							canvasSave = new Canvas(saveBitmap);
							canvasDraw.drawColor(Color.TRANSPARENT);
							ViewGroup viewGroup = Relativelay;
							View currentView = viewGroup.getChildAt(choosepicture);
							//取得照片資訊
							currentView.setDrawingCacheEnabled(true);
							currentView.buildDrawingCache();
							tempImage = currentView.getDrawingCache();


							Img = new ImageView(getActivity());
							//img = new img(getActivity(),tempImage);
							Img.setId(countpicture);
							Img.setZ(countpicture + 11);
							backimage.setZ(countpicture + 10);
							Relativelay.addView(Img);

							//將照片等比放大到跟螢幕大小一樣
							float ScaleX = 0, ScaleY = 0;
							for (float sX = 1; tempImage.getWidth() * sX <= backimage.getWidth(); sX += 0.1)
								ScaleX = sX;
							for (float sY = 1; tempImage.getHeight() * sY <= backimage.getHeight(); sY += 0.1)
								ScaleY = sY;
							if (ScaleX > ScaleY)
								ScaleX = ScaleY;
							else if (ScaleX < ScaleY)
								ScaleY = ScaleX;
							else ;

							//將照片移到正中間
							drawMatrix = new Matrix();
							drawMatrix.postTranslate(backimage.getWidth() / 2 - tempImage.getWidth() / 2, backimage.getHeight() / 2 - tempImage.getHeight() / 2);
							drawMatrix.postScale(ScaleX, ScaleY, backimage.getWidth() / 2, backimage.getHeight() / 2);

							canvasSave.drawBitmap(tempImage, drawMatrix, null);

							Img.setImageBitmap(saveBitmap);
							Img.setOnTouchListener(null);
							Img.setOnTouchListener(DrawOnTouchListener);

//							setCurAction(0,0);
//							curAction.move(0,0);
//							curAction.draw(canvasSave);
//							mActions.add(curAction);

							countpicture++;
							photo = true;

							makeTextAndShow(getActivity(), "編輯模式", android.widget.Toast.LENGTH_SHORT);
						} else {
							makeTextAndShow(getActivity(), "沒有選擇相片", android.widget.Toast.LENGTH_SHORT);

						}
					}
				});
				builder.setNegativeButton("開新畫布", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog

						//設定按鈕可否使用
						drawover.setEnabled(true);
						eraser.setEnabled(true);
						colorBtn.setEnabled(true);
						shapeBtn.setEnabled(true);
						undoBtn.setEnabled(true);
						//把主工具列隱藏
						maintool.setVisibility(View.GONE);
						//把畫布工具列顯示
						drawtool.setVisibility(View.VISIBLE);
						//把選擇背景工具列隱藏
						choosebackground.setVisibility(View.GONE);

						//設定畫筆初始模式是為自由曲線
						setType(ActionType.Path);
						setSize(currentSize);
						//新增紀錄陣列
						mActions = new ArrayList<Action>();
						//如果在之前有選擇圖片，把圖片還原
						if (lastView != null)
							lastView.setBackgroundColor(getResources().getColor(R.color.alpha));

						//如果一開始畫布為空的，創建新畫布
						if (drawBitmap == null) {


							drawBitmap = Bitmap.createBitmap(backimage.getWidth(), backimage.getHeight(), Bitmap.Config.ARGB_8888);
							saveBitmap = Bitmap.createBitmap(backimage.getWidth(), backimage.getHeight(), Bitmap.Config.ARGB_8888);
							canvasDraw = new Canvas(drawBitmap);
							canvasSave = new Canvas(saveBitmap);
							canvasDraw.drawColor(Color.TRANSPARENT);
							canvasSave.drawColor(Color.TRANSPARENT);
							img = new ImageView(getActivity());
							//img = new img(getActivity(),saveBitmap,0);
							img.setId(countpicture);
							img.setZ(countpicture);
							img.setImageBitmap(saveBitmap);
							Relativelay.addView(img);
							countpicture++;
							img.setOnTouchListener(DrawOnTouchListener);
							draw = true;
							Log.d("aa", "test");
							makeTextAndShow(getActivity(), "畫布模式", android.widget.Toast.LENGTH_SHORT);
						}
					}
				});
				builder.create().show();


				//紀錄步驟順序
				undoCutXY = new float[1000][4];

				makeTextAndShow(getActivity(), "畫布模式", android.widget.Toast.LENGTH_SHORT);

			}
		});
		//endregion

		//region 畫筆seekbar
		paintSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			//當seekBar在拉動時
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser) {
				// TODO 自動產生的方法 Stub
				//畫筆大小設置，progress為當下seekbar數值
				if (progress < 15) {
					progress = 15;

				}
				choosePaintSize = progress;
				//創建一個Bitmap與顯示畫筆粗細ImageView一樣大小
				Bitmap paintSizeBitmap = Bitmap.createBitmap(paintImgSize.getWidth(), paintImgSize.getHeight(), Bitmap.Config.ARGB_8888);
//				Log.d("imgwidth", String.valueOf(paintImgSize.getWidth()));
//				Log.d("imgHeigh", String.valueOf(paintImgSize.getHeight()));
				//把Bitmap加入Canvas中
				Canvas paintSizeCanvas = new Canvas(paintSizeBitmap);
				//新增畫筆
				Paint cleancanvas = new Paint();
				//設定每當seekBar有變動時清除原有畫布上的東西，避免縮小會沒有感覺
				cleancanvas.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
				paintSizeCanvas.drawPaint(cleancanvas);
				cleancanvas.setXfermode(new PorterDuffXfermode(Mode.SRC));
				//設定畫筆粗細
				setSize(progress);
				//新增畫筆
				Paint seekBarShowPaintSize = new Paint();
				//畫筆抗齒
				seekBarShowPaintSize.setAntiAlias(true);
				//設定顏色為當前選的顏色
				seekBarShowPaintSize.setColor(currentColor);
				//設定畫筆模式為填滿模式
				seekBarShowPaintSize.setStyle(Paint.Style.FILL);
				//設定畫筆寬度
				seekBarShowPaintSize.setStrokeWidth(choosePaintSize);
				//用畫筆畫圓(Ｘ軸中心，Ｙ軸中心，畫筆資訊)
				paintSizeCanvas.drawCircle(paintImgSize.getWidth() / 2, paintImgSize.getHeight() / 2, progress / 2, seekBarShowPaintSize);
				//顯示
				paintImgSize.setImageBitmap(paintSizeBitmap);

//				Paint seekBarShowPaintSizeCircile = new Paint();
//				seekBarShowPaintSizeCircile.setAntiAlias(true);
//				seekBarShowPaintSizeCircile.setColor(getResources().getColor(R.color.black));
//				seekBarShowPaintSizeCircile.setStyle(Paint.Style.STROKE);
//				seekBarShowPaintSizeCircile.setStrokeWidth(1);
//				paintSizeCanvas.drawCircle(paintImgSize.getWidth() / 2,  paintImgSize.getHeight() / 2, progress / 2+1,
//						seekBarShowPaintSizeCircile);


			}

			//當seeBar開始拉動時
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO 自動產生的方法 Stub

			}

			//當seekBar停止拉動時
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO 自動產生的方法 Stub

			}

		});
		//endregion

		//region 複製
		copy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//初始設定為path

				choosebackground.setVisibility(View.GONE);
//

				if (choosepicture != 0) {
					ViewGroup viewGroup = Relativelay;
					img currentView = (img) viewGroup.getChildAt(choosepicture);
					currentView.setDrawingCacheEnabled(true);
					currentView.buildDrawingCache();


					currentView.setBackgroundColor(getResources().getColor(R.color.alpha));
					currentView.setAlpha(1f);


					Matrix Matrix = new Matrix();
					Matrix.postScale(currentView.getScaleX(), currentView.getScaleY(), currentView.getWidth() / 2, currentView.getHeight() / 2);
					Matrix.postRotate(currentView.getRotation(), (currentView.getWidth() * currentView.getScaleX()) / 2, (currentView.getHeight() * currentView.getScaleY()) / 2);
					currentView.buildDrawingCache();
					Bitmap copy = currentView.getDrawingCache();

					if (currentView.getIfText() == 0)
						bitmapcopy = Bitmap.createBitmap(copy, 0, 0, copy.getWidth(), copy.getHeight(), Matrix, false);
					else {
						bitmapcopy = Bitmap.createBitmap(copy.getWidth(), copy.getHeight(), Bitmap.Config.ARGB_8888);
						Canvas copycanvas = new Canvas(bitmapcopy);
						copycanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
						copycanvas.drawBitmap(copy, 0, 0, null);
					}
					//bitmapcopy = getMagicDrawingCache(currentView);

					//取得亂數介於-200~200之間
					int copyX, copyY;
					copyX = (int) (Math.random() * 400 - 200);
					copyY = (int) (Math.random() * 400 - 200);

					//設定照片要隨機放的位置，不能超過螢幕四周
					float setX, setY;
					setX = currentView.getX() + copyX;
					setY = currentView.getY() + copyY;
					if (currentView.getX() + copyX > backimage.getWidth()) {
						setX = setX - currentView.getWidth() - 100;
					}
					if (currentView.getX() + copyX < 0) {
						setX = -(currentView.getX() + copyX);
					}
					if (currentView.getY() + copyY > backimage.getHeight()) {
						setY = setY - currentView.getHeight() - 100;
					}
					if (currentView.getY() + copyY < 0) {
						setY = -(currentView.getY() + copyY);
					}


					if (currentView.getIfText() == 1)
						img = new img(getActivity(), bitmapcopy, 1);
					else
						img = new img(getActivity(), bitmapcopy, 0);
					//為這個imageview設定ID
					img.setId(countpicture);
					img.setZ(countpicture);

					img.setX(setX);
					img.setY(setY);


					img.setImageBitmap(bitmapcopy);

					img.setOnTouchListener(new MultiTouchListener());
					//imageview點擊事件
					img.setOnClickListener(imgOnClickListener);
					//把照片加入RelativeLayout中，座標位置為0,0
					Relativelay.addView(img);
					//照片ID數加1
					countpicture++;
					//choosepicture=0;
					makeTextAndShow(getActivity(), "複製", android.widget.Toast.LENGTH_SHORT);
				} else
					makeTextAndShow(getActivity(), "沒有選擇圖片", android.widget.Toast.LENGTH_SHORT);

			}
		});
		//endregion

		//region 加入文字按鈕
		textBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setIcon(R.mipmap.ic_format_size_white_24dp);
				builder.setTitle("請輸入想要加入的文字");
				//    通過LayoutInflater來載入一個xml作為一個View對象
				View view = LayoutInflater.from(getActivity()).inflate(R.layout.textdialog, null);
				//    設置我们自己定義的布局文件作為彈出框的Content
				builder.setView(view);

				final EditText text = (EditText) view.findViewById(R.id.text);

				builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						textBundle = text.getText().toString().trim();
						if ("".equals(text.getText().toString().trim())) {
							makeTextAndShow(getActivity(), "請輸入文字", android.widget.Toast.LENGTH_SHORT);
						} else {
							Intent it = new Intent();
							it.setClass(getActivity(), AddText.class);

							Bundle bundle = new Bundle();
							bundle.putString("text", textBundle);//傳遞String
							//將Bundle物件傳給intent
							it.putExtras(bundle);
							//startActivity(it);
							startActivityForResult(it, 0);
						}


					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
				builder.show();


			}
		});
		//endregion

		//region 上推按鈕
		upBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {


				ViewGroup viewGroup = Relativelay;
				View currentView = viewGroup.getChildAt(choosepicture);
				View changetView = null;
				//要上推的照片一定要比照片張數小
				if (currentView.getZ() < countpicture - 1) {
					//找到要上推照片的上面那一張照片
					for (int i = 1; i < viewGroup.getChildCount(); i++) {
						if (currentView.getZ() + 1 != viewGroup.getChildAt(i).getZ()) {

						} else
							changetView = viewGroup.getChildAt(i);
					}
					currentView.setZ((int) currentView.getZ() + 1);
					changetView.setZ((int) changetView.getZ() - 1);
				}


			}
		});
		//endregion

		//region 下推按鈕
		downBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewGroup viewGroup = Relativelay;
				View currentView = viewGroup.getChildAt(choosepicture);
				View changetView = null;
				//要下推的Z一定要比1大
				if (currentView.getZ() > 1) {
					//找出要下推的照片原本的下面那張照片
					for (int i = 1; i < viewGroup.getChildCount(); i++) {
						if (currentView.getZ() - 1 != viewGroup.getChildAt(i).getZ()) {

						} else
							changetView = viewGroup.getChildAt(i);
					}
					currentView.setZ((int) currentView.getZ() - 1);
					changetView.setZ((int) changetView.getZ() + 1);
				}
			}
		});
		//endregion

		//region 更換背景按鈕
		backgroundBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//初始設定為path
				if (choosebackground.getVisibility() == View.VISIBLE)
					choosebackground.setVisibility(View.GONE);
				else
					choosebackground.setVisibility(View.VISIBLE);
			}
		});
		//endregion

		//region 畫筆粗細
		drawBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				paint.setAntiAlias(true);
				setColor(currentColor);
				setSize(choosePaintSize);

				//創建一個Bitmap與顯示畫筆粗細ImageView一樣大小
				Bitmap paintSizeBitmap = Bitmap.createBitmap(210, 210, Bitmap.Config.ARGB_8888);
				//把Bitmap加入Canvas中
				Canvas paintSizeCanvas = new Canvas(paintSizeBitmap);

				//新增畫筆
				Paint seekBarShowPaintSize = new Paint();
				//畫筆抗齒
				seekBarShowPaintSize.setAntiAlias(true);
				//設定顏色為當前選的顏色
				seekBarShowPaintSize.setColor(currentColor);
				//設定畫筆模式為填滿模式
				seekBarShowPaintSize.setStyle(Paint.Style.FILL);
				//設定畫筆寬度
				seekBarShowPaintSize.setStrokeWidth(choosePaintSize);
				//用畫筆畫圓(Ｘ軸中心，Ｙ軸中心，畫筆資訊)
				paintSizeCanvas.drawCircle(210 / 2, 210 / 2, choosePaintSize / 2, seekBarShowPaintSize);
				//顯示
				paintImgSize.setImageBitmap(paintSizeBitmap);

				if (choosecolorlinr.getVisibility() == View.VISIBLE)
					choosecolorlinr.setVisibility(View.VISIBLE);
				else
					choosecolorlinr.setVisibility(View.GONE);
				if (chooseshape.getVisibility() == View.VISIBLE)
					chooseshape.setVisibility(View.VISIBLE);
				else
					chooseshape.setVisibility(View.GONE);
				if (paintSizelinr.getVisibility() == View.VISIBLE)
					paintSizelinr.setVisibility(View.GONE);
				else
					paintSizelinr.setVisibility(View.VISIBLE);

				if (eraserSizelinr.getVisibility() == View.VISIBLE) {
					setType(ActionType.Path);
					eraserSizelinr.setVisibility(View.GONE);
				}

			}
		});
		//endregion

		//region 選擇畫筆顏色
		colorBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//初始設定為path
				paint.setAntiAlias(true);
				setSize(currentSize);

				if (paintSizelinr.getVisibility() == View.VISIBLE)
					paintSizelinr.setVisibility(View.VISIBLE);
				else
					paintSizelinr.setVisibility(View.GONE);
				if (chooseshape.getVisibility() == View.VISIBLE)
					chooseshape.setVisibility(View.VISIBLE);
				else
					chooseshape.setVisibility(View.GONE);
				if (choosecolorlinr.getVisibility() == View.VISIBLE)
					choosecolorlinr.setVisibility(View.GONE);
				else
					choosecolorlinr.setVisibility(View.VISIBLE);
				if (eraserSizelinr.getVisibility() == View.VISIBLE) {
					setType(ActionType.Path);
					eraserSizelinr.setVisibility(View.GONE);
				}


			}
		});
		//endregion

		//region 選擇圖形按鈕
		shapeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//showShapeDialog();
				//0是有顯示時
				if (choosecolorlinr.getVisibility() == View.VISIBLE)
					choosecolorlinr.setVisibility(View.VISIBLE);
				else
					choosecolorlinr.setVisibility(View.GONE);
				if (paintSizelinr.getVisibility() == View.VISIBLE)
					paintSizelinr.setVisibility(View.VISIBLE);
				else
					paintSizelinr.setVisibility(View.GONE);
				if (chooseshape.getVisibility() == View.VISIBLE)
					chooseshape.setVisibility(View.GONE);
				else
					chooseshape.setVisibility(View.VISIBLE);

				eraserSizelinr.setVisibility(View.GONE);
			}
		});
		//endregion

		//region 上一步
		undoBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自動產生的方法 Stub


				if (mActions != null && mActions.size() > 0) {
					mActions.remove(mActions.size() - 1);
					Paint cleancanvas = new Paint();
					cleancanvas.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
					canvasSave.drawPaint(cleancanvas);
					cleancanvas.setXfermode(new PorterDuffXfermode(Mode.SRC));
					//如果為編輯照片模式，每次都必須先把原圖片在畫上去一次
					if (photo == true)
						canvasSave.drawBitmap(tempImage, drawMatrix, null);
					for (Action a : mActions) {
						a.draw(canvasSave);
					}
					img.setImageBitmap(saveBitmap);
					for (int i = 0; i < 4; i++)
						undoCutXY[undoCutXYIndex1 - 1][i] = 0;

					undoCutXYIndex1--;

				}
				if (undoCutXYIndex1 == 0) {
					cutBigX = 0;
					cutBigY = 0;
					cutSmallX = 10000;
					cutSmallY = 10000;
				}
			}


		});
		//endregion

		//region 清空整個畫布
		cleanAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO 自動產生的方法 Stub

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("清空畫布");
				builder.setMessage("確定要清除!!");
				// Add the buttons
				builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User clicked OK button
						Paint cleancanvas = new Paint();
						cleancanvas.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
						canvasSave.drawPaint(cleancanvas);
						cleancanvas.setXfermode(new PorterDuffXfermode(Mode.SRC));
						if (photo == true) {
							canvasSave.drawBitmap(tempImage, drawMatrix, null);
							//再次畫上編輯前的照片樣子
							Img.setImageBitmap(saveBitmap);
						}
						else if(draw == true)
						{
							img.setImageBitmap(saveBitmap);
						}
						mActions.clear();

						cutBigX = 0;
						cutBigY = 0;
						cutSmallX = 10000;
						cutSmallY = 10000;
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
				builder.create().show();

			}
		});
		//endregion

		//region 開啟橡皮擦
		eraser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自動產生的方法 Stub
				//eraserChoose();
				setType(ActionType.Eraser);
				choosecolorlinr.setVisibility(View.GONE);
				paintSizelinr.setVisibility(View.GONE);
				chooseshape.setVisibility(View.GONE);
				if (eraserSizelinr.getVisibility() == View.VISIBLE)
					eraserSizelinr.setVisibility(View.GONE);
				else
					eraserSizelinr.setVisibility(View.VISIBLE);
			}
		});
		//endregion

		//region 橡皮擦seekBar
		eraserSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
										  boolean fromUser) {
				// TODO 自動產生的方法 Stub
				//setType(ActionType.Eraser);
				setSize(progress);

				Bitmap paintSizeBitmap = Bitmap.createBitmap(erasersize.getWidth(), erasersize.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas paintSizeCanvas = new Canvas(paintSizeBitmap);

				//不斷的清除bitmap才不會造成縮小時圖情看不出變化
				Paint cleancanvas = new Paint();
				cleancanvas.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
				paintSizeCanvas.drawPaint(cleancanvas);
				cleancanvas.setXfermode(new PorterDuffXfermode(Mode.SRC));

				//設定畫筆
				Paint seekBarShowPaintSize = new Paint();
				seekBarShowPaintSize.setAntiAlias(true);
				seekBarShowPaintSize.setColor(Color.parseColor("#FFFFFF"));
				seekBarShowPaintSize.setStyle(Paint.Style.FILL);
				seekBarShowPaintSize.setStrokeWidth(progress);

				paintSizeCanvas.drawCircle(erasersize.getWidth() / 2, erasersize.getHeight() / 2, progress / 2, seekBarShowPaintSize);
				erasersize.setImageBitmap(paintSizeBitmap);

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO 自動產生的方法 Stub

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO 自動產生的方法 Stub

			}

		});
		//endregion

		//region 結束畫布
		drawover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				drawover.setEnabled(false);
				shapeBtn.setEnabled(false);
				eraser.setEnabled(false);
				colorBtn.setEnabled(false);
				undoBtn.setEnabled(false);


				choosecolorlinr.setVisibility(View.GONE);
				chooseshape.setVisibility(View.GONE);
				paintSizelinr.setVisibility(View.GONE);
				eraserSizelinr.setVisibility(View.GONE);
				drawtool.setVisibility(View.GONE);
				maintool.setVisibility(View.VISIBLE);

				Log.d("smallX", String.valueOf(smallX));
				Log.d("smallY", String.valueOf(smallY));
				Log.d("BigX", String.valueOf(BigX));
				Log.d("BigY", String.valueOf(BigY));

				//判斷圖片片是否為空
				Boolean clean = true;
				for (int x = 0; x < saveBitmap.getWidth(); x++) {
					for (int y = 0; y < saveBitmap.getHeight(); y++) {
						if (saveBitmap.getPixel(x, y) != 0) {
							clean = false;
						}

					}
				}
				//如果為空圖片
				if (clean == true)
//					if (cutBigX == 0 && cutBigY == 0 && cutSmallX == 10000 && cutSmallY == 10000)
				{
					ViewGroup viewGroup = Relativelay;
					View currentView = viewGroup.getChildAt(countpicture - 1);
					viewGroup.removeView(currentView);
					img.setOnTouchListener(null);
					countpicture--;
					choosepicture = 0;
					backimage.setZ(0);
					makeTextAndShow(getActivity(), "結束畫布模式畫布為空", android.widget.Toast.LENGTH_SHORT);
				} else {
					//如果為畫畫模式
					if (draw == true) {
						int leftX, rightX, topY, bottomY;


						//切割值
						if (undoCutXY[undoCutXYIndex1 - 1][0] - currentSize - 100 < 0)
							leftX = 0;
						else
							leftX = (int) undoCutXY[undoCutXYIndex1 - 1][0] - currentSize - 100;
						if (undoCutXY[undoCutXYIndex1 - 1][2] - currentSize - 100 < 0)
							topY = 0;
						else
							topY = (int) undoCutXY[undoCutXYIndex1 - 1][2] - currentSize - 100;
						if (undoCutXY[undoCutXYIndex1 - 1][1] + currentSize + 100 > saveBitmap.getWidth())
							rightX = saveBitmap.getWidth() - leftX;
						else {
							rightX = (int) (undoCutXY[undoCutXYIndex1 - 1][1] - undoCutXY[undoCutXYIndex1 - 1][0] + 2 * currentSize);
							rightX += 100;
						}
						if (undoCutXY[undoCutXYIndex1 - 1][3] + currentSize + 100 > saveBitmap.getHeight())
							bottomY = saveBitmap.getHeight() - topY;
						else {
							bottomY = (int) (undoCutXY[undoCutXYIndex1 - 1][3] - undoCutXY[undoCutXYIndex1 - 1][2] + 2 * currentSize);
							bottomY += 100;
						}


							/*Log.d("imgW",String.valueOf(saveBitmap.getWidth()));
							Log.d("imgH",String.valueOf(saveBitmap.getHeight()));*/
//							Log.d("bottomy",String.valueOf(topY));
//							Log.d("rightX",String.valueOf(leftX));

						//將saveBitmap裡面的內容存到另一個bitmap以免saveBitmap被覆蓋時出錯
						Bitmap drawOverBitmap = Bitmap.createBitmap(saveBitmap
								, leftX
								, topY
								, rightX
								, bottomY
								, null, false);

						ViewGroup viewGroup = Relativelay;
						View currentView = viewGroup.getChildAt(countpicture - 1);
						Relativelay.removeView(currentView);
						//創建一個可以分發事件的imageview用來判斷是否有摸到透明的地方
						img = new img(getActivity(), drawOverBitmap, 0);
						img.setId(countpicture - 1);
						img.setZ(countpicture - 1);
						img.setX(leftX);
						img.setY(topY);
						img.setImageBitmap(drawOverBitmap);
						Relativelay.addView(img);
						img.setOnTouchListener(null);
						img.setOnTouchListener(new MultiTouchListener());
						img.setOnClickListener(imgOnClickListener);
						//countpicture++;
						makeTextAndShow(getActivity(), "結束畫布模式", android.widget.Toast.LENGTH_SHORT);
					}
					//如果為編輯照片模式
					else if (photo == true) {


						ViewGroup viewGroup = Relativelay;
						View currentView = viewGroup.getChildAt(choosepicture);

						//原本照片後的ID都往前移1
						for (int i = choosepicture + 1; i < viewGroup.getChildCount(); i++) {
							viewGroup.getChildAt(i).setId(i - 1);
						}
						//currentView.setDrawingCacheEnabled(false);
						//須將原本的照片刪掉
						Relativelay.removeView(currentView);

						//將saveBitmap裡面的內容存到另一個bitmap以免saveBitmap被覆蓋時出錯
						Bitmap overBitmap = Bitmap.createBitmap(saveBitmap.getWidth() / 2, saveBitmap.getHeight() / 2, Bitmap.Config.ARGB_8888);
						Canvas overCanvas = new Canvas(overBitmap);
						float a = 0.5f, b = 0.5f;
						Matrix ma = new Matrix();
						ma.postScale(a, b);

						overCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
						overCanvas.drawBitmap(saveBitmap, ma, null);

						//再把畫好的照片的imageview刪掉因為還要再創一個能分發事件的imageview
						currentView = viewGroup.getChildAt(countpicture - 2);
						Relativelay.removeView(currentView);

						img = new img(getActivity(), overBitmap, 0);
						img.setId(countpicture - 2);
						img.setZ(choosepictureZ);
						img.setX(backimage.getWidth() / 2 - backimage.getWidth() / 4);
						img.setY(backimage.getHeight() / 2 - backimage.getHeight() / 4);
						img.setImageBitmap(overBitmap);

						Relativelay.addView(img);
						img.setOnTouchListener(null);
						img.setOnTouchListener(new MultiTouchListener());
						img.setOnClickListener(imgOnClickListener);
						countpicture--;
						makeTextAndShow(getActivity(), "結束畫布模式", android.widget.Toast.LENGTH_SHORT);
					}
				}

				backimage.setZ(0);

				drawBitmap = null;
				saveBitmap = null;

				photo = false;
				draw = false;

//					cutBigX=0;
//					cutBigY=0;
//					cutSmallX=10000;
//					cutSmallY=10000;
			}


		});
		//endregion

		//region 儲存圖片至SD卡
		SaveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				choosebackground.setVisibility(View.GONE);

				ViewGroup viewGroup = Relativelay;

				if (lastView != null) {
					lastView.setAlpha(1f);
				}

				if (viewGroup.getChildCount() != 1) {
					for (int i = 1; i < viewGroup.getChildCount(); i++) {
						//靠標籤取得要儲存的imageview
						img = (img) viewGroup.getChildAt(i);
						//設定imageview背景為透明
						img.setBackgroundColor(getResources().getColor(R.color.alpha));
					}

					HandlerThread save = new HandlerThread("name");
					save.start();
					mThreadHandler = new Handler(save.getLooper());
					mThreadHandler.post(saveRun);

					progressDialog = new ProgressDialog(getActivity());
					progressDialog.requestWindowFeature(Window.FEATURE_PROGRESS);

					progressDialog.setTitle("建立拼貼");
					progressDialog.setMessage("請稍後...");
					progressDialog.setCanceledOnTouchOutside(false);
					progressDialog.setIcon(R.drawable.progress);
					progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					progressDialog.show();
				} else {
					//Toast.makeText(CollageActivity.this, "目前沒有圖片", Toast.LENGTH_LONG).show();
					makeTextAndShow(getActivity(), "目前沒有圖片", android.widget.Toast.LENGTH_SHORT);
				}
			}
		});
		//endregion

		//region 選擇照片
		selectImgBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//跳轉至最終的選擇圖片頁面

				choosebackground.setVisibility(View.GONE);

				Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
				startActivity(intent);
			}
		});
		//endregion

		//region 刪除事件
		DeleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				choosebackground.setVisibility(View.GONE);

				ViewGroup viewGroup = Relativelay;
				View currentView = viewGroup.getChildAt(choosepicture);
				if (choosepicture != 0) {
					//將目前選去要刪除的圖片的後面每一張圖片的ID都往前移一個
					for (int i = choosepicture + 1; i < viewGroup.getChildCount(); i++) {
						viewGroup.getChildAt(i).setId(i - 1);
					}
					//將目前選擇要刪除的照片比他大的Z都要減1
					for (int z = 1; z < viewGroup.getChildCount(); z++) {
						if ((int) currentView.getZ() < (int) viewGroup.getChildAt(z).getZ()) {
							viewGroup.getChildAt(z).setZ((int) viewGroup.getChildAt(z).getZ() - 1);
						}
					}
					//Debug
					int picturenum = currentView.getId();
					String tag = "Deletechoosepicture";
					Log.d(tag, Integer.toString(picturenum));
					//刪除所選的imageview
					viewGroup.removeView(currentView);

					//設定照片的ID要減1
					countpicture--;
					//設定旗標如果為false不能刪除照片
					choosepicture = 0;
				} else
					makeTextAndShow(getActivity(), "沒有選擇圖片", android.widget.Toast.LENGTH_SHORT);
			}
		});
		//endregion

		//region 全部清空
		DeleteAllButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				choosebackground.setVisibility(View.GONE);
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle("清空拼貼");
				builder.setMessage("確定要清除!!");
				// Add the buttons
				builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User clicked OK button
						ViewGroup viewGroup = Relativelay;
						if (viewGroup.getChildCount() > 1) {
							for (int i = 1, count = viewGroup.getChildCount(); i < count; i++) {
								viewGroup.removeView(viewGroup.getChildAt(1));
							}
							//設定照片的ID要減1
							countpicture = 1;
							//設定旗標如果為false不能刪除照片
							choosepicture = 0;
							makeTextAndShow(getActivity(), "清空", android.widget.Toast.LENGTH_SHORT);
						} else
							makeTextAndShow(getActivity(), "沒有圖片", android.widget.Toast.LENGTH_SHORT);
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// User cancelled the dialog
					}
				});
				builder.create().show();


			}
		});
		//endregion		
	}


	//endregion
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		thisView = inflater.inflate(R.layout.activity_collage, container, false);

		initVariables();

		initListener();

		//region 播放音樂(可以重覆播放)
//		try {
//			mPlayer = MediaPlayer.create(this, R.raw.backgroundmusic);
//			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//			mPlayer.setLooping(true);
//			//重複播放
//
//			//mPlayer.prepare();
//			//特別使用註解的方式, 是為了提醒大家, 由於我們先前使用create method建立MediaPlayer
//			//create method會自動的call prepare(), 所以我們再call prepare() method會發生 prepareAsync called in state 8的錯誤
//
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//endregion

		return thisView;
	}

	//region 加入照片
	@Override
	public void onResume() {
		super.onResume();

		int code = getActivity().getIntent().getIntExtra("code", -1);
		Log.d("code", Integer.toString(code));
		if (code != 100) {
			return;
		}

		ArrayList<String> paths = getActivity().getIntent().getStringArrayListExtra("paths");

		for (String path : paths) {
			//把照片加入陣列中
			//imagePathList.add(path);
			Bitmap bitmap = BitmapFactory.decodeFile(path);


			File imgFile = new File(path);
			// 圖片壓縮
			tempBitmap = ScalePicEx(imgFile.getAbsolutePath(), 600, 800);
			//使用createBitmap畫入照片，用於後面判斷圖片大小
			//tempBitmap = createBitmap(myBitmap);
			//創建一個新的imageview
			//img = new ImageView(getActivity());
			img = new img(getActivity(), tempBitmap, 0);

			//為這個imageview設定ID
			img.setId(countpicture);
			img.setZ(countpicture);
			//img.setTag(1);
			Log.d("ADD-PHOTO", Integer.toString(countpicture));

			//使用Glide方法把圖片顯示在imagevieww上
			Glide.with(getActivity())
					//圖片來源
					.load(new File(path))
							//imageview大小
							//.resize(tempBitmap.getWidth(),tempBitmap.getHeight())
					.override(tempBitmap.getWidth(), tempBitmap.getHeight())
					.centerCrop()
							//照片載入時所顯示圖片
					.placeholder(R.drawable.passage_of_time_32)
							//照片載入錯誤時所顯示圖片
					.error(R.drawable.no)
							//所要放到的imageview
					.into(img);

			//設定要放入RelatriveLayout時的大小，如妥超過螢幕大小會自動縮小
			RelativeLayout.LayoutParams lp1;
			if (tempBitmap.getWidth() > Relativelay.getWidth() || tempBitmap.getHeight() > Relativelay.getHeight()) {
				lp1 = new RelativeLayout.LayoutParams(tempBitmap.getWidth() / 3, tempBitmap.getHeight() / 3);
			} else {
				lp1 = new RelativeLayout.LayoutParams(tempBitmap.getWidth(), tempBitmap.getHeight());
			}
			//設定圖片加入時的位置
			lp1.leftMargin = addViewLeft;
			lp1.topMargin = addViewTop;

			//觸控時監聽
			img.setOnTouchListener(new MultiTouchListener());
			//imageview點擊事件
			img.setOnClickListener(imgOnClickListener);
			//把照片加入RelativeLayout中，座標位置為0,0
			Relativelay.addView(img, lp1);
			//照片ID數加1
			countpicture++;
			//圖片加入位置計算
			addViewTop += 150;
			if (addViewTop > 800) {
				addViewTop = 50;
				addViewLeft += 300;
			}
			if (addViewLeft > 650) {
				addViewLeft = 150;
			}
		}
		getActivity().getIntent().removeExtra("code");
	}


	//endregion

	//region 自訂氣泡訊息，不會與上一個訊息衝突
    private static void makeTextAndShow(final Context context, final String text, final int duration) {
    	if (toast == null) {
    		//如果還沒有用過makeText方法，才使用
    		toast = android.widget.Toast.makeText(context, text, duration);
    	} else {
    		toast.setText(text);
    		toast.setDuration(duration);
    	}
    	toast.show();
    }
	//endregion

	//region 取得目前時間
    public String getCurrentTime(String format) {
		// 先行定義時間格式("yyyy/MM/dd HH:mm:ss")
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		// 取得現在時間
		Date dt = new Date(System.currentTimeMillis());
		return sdf.format(dt);
	}
	//endregion

	//region ImageView 點擊事件
	private OnClickListener imgOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			//取得所選照片的ID

			//取得照片ID
			choosepicture = v.getId();
			//取得照片Z方便之後設定用
			choosepictureZ = (int) v.getZ();
			Log.d("Z", String.valueOf(v.getZ()));
			ViewGroup viewGroup = Relativelay;
			if (lastView != null) {
				lastView.setBackgroundColor(getResources().getColor(R.color.alpha));
				lastView.setAlpha(1f);
				//ViewGroup viewGroup = Relativelay;
//				if(lastView!=v)
//				{
//					for(int i = 1; i < viewGroup.getChildCount(); i++)
//	    	 		   {
//	    	 			   //靠標籤取得要儲存的imageview
//	    	 			   img = (ImageView) viewGroup.getChildAt(i);
//	    	 			   if(img.getZ()>=v.getZ())
//	    	 			   {
//		    	 			   if(img.getZ()==1)
//		    	 				   img.setZ(1);
//		    	 			   else
//		    	 				   img.setZ(img.getZ()-1);
//	    	 			   }
//	    	 		   }
//				}
			}
			//v.setBackgroundResource(R.drawable.shape_image_border);
			//設定透明度
			v.setAlpha(0.9f);
			//v.setZ(countpicture-1);
			//設定此imageview為選擇新imageview時變成上一個imageview
			lastView = v;
			//Debug
			String tag = "choosepicture";
			Log.d( tag,  Integer.toString(choosepicture));
		}
	};
	//endregion

	//region 多執行緒儲存
	private Runnable saveRun = new Runnable() {
		
		@Override
		public void run() {
			// TODO 自動產生的方法 Stub
 		   Relativelay.setDrawingCacheEnabled(true); 
 		   Relativelay.buildDrawingCache();   
 		   Bitmap relatsave = Relativelay.getDrawingCache(); 
 		   updateHandleMessage("value","產生暫存");
     	    //取得緩存圖片的Bitmap檔
     	    Bitmap savebitmap=relatsave ;
		   
 	    if (Environment.getExternalStorageState()//確定SD卡可讀寫
 	    		.equals(Environment.MEDIA_MOUNTED))
 	    {
 	    		// 取得外部儲存裝置路徑 
 	    		File sdFile = android.os.Environment.getExternalStorageDirectory();
 	    		//要建立資料夾的路徑
 	    		String path = sdFile.getPath() + File.separator + "PhotoCollage";
 	    		dirFile = new File(path);
 	    		updateHandleMessage("value","判斷資料夾是否存在");
 	    		if(!dirFile.exists()){//如果資料夾不存在
 	    		dirFile.mkdir();//建立資料夾
 	    		}
 	    }
 	    // 開啟檔案
			File file = new File(dirFile, "PhotoCollage_"+getCurrentTime("yyyyMMddHHmmssSS")+".PNG");
		if(savebitmap!=null)
 	    {
     	    try {
     	    	 // 開啟檔案串流
     	    	OutputStream outStream = new FileOutputStream(file);
	        	     // 將 Bitmap壓縮成指定格式的圖片並寫入檔案串流
	        	     //compress中間數字為壓縮率100表示不壓縮 90表示壓縮10%以此類推
     	    	 updateHandleMessage("value","儲存圖片");
     	    	 savebitmap.compress(Bitmap.CompressFormat.PNG, 20, outStream);
	        	 outStream.flush();
     	         outStream.close();
     	         Relativelay.destroyDrawingCache();
     	         updateHandleMessage("value","圖片產生完成");
     	         Toast.makeText(getActivity(),"PhotoCollage_"+getCurrentTime("yyyyMMddHHmmssSS"), Toast.LENGTH_LONG).show();
     	    } 
     	    catch (FileNotFoundException e) {
	        	     // TODO Auto-generated catch block
	        	     e.printStackTrace();
	        	     Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
     	    } 
     	    catch (IOException e) {
	        	     // TODO Auto-generated catch block
	        	     e.printStackTrace();
	        	     Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
     	    }
     	 }
 	    else
 	    {
 	    	Toast.makeText(getActivity(), "目前沒有圖片", Toast.LENGTH_LONG).show();
 	    }
		}
	};
	protected void updateHandleMessage(String key, String value) {
		Message msg = new Message();;
        Bundle data = new Bundle();
		data.putString(key,value);        
        msg.setData(data);
        handler.sendMessage(msg);
	}
	//endregion

	//region 接收上傳檔案進度
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();
	        String value = data.getString("value");
			progressDialog.setMessage(value);
			
			if (value.equals("圖片產生完成")) {
				progressDialog.dismiss();
//				// 切換預覽頁面
//				Intent it = new Intent();
//				it.setClass(getActivity(), CollageActivity.class);
//				startActivity(it);
			}
		}
	};
	//endregion

	//region 圖片轉換大小
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
	//endregion

	//region 畫筆觸摸事件
	private OnTouchListener DrawOnTouchListener = new OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO 自動產生的方法 Stub

			switch (event.getAction()) {
			// 用户按下动作
			case MotionEvent.ACTION_DOWN:
				// 紀錄開始触摸的点的坐标
				startX = event.getX();
				startY = event.getY();
				//裁切判斷
				smallX = startX;
				smallY = startY;
				BigX = startX;
				BigY = startY;
				
				setCurAction(startX, startY);
				if(type == ActionType.Point)
				{
					curAction.draw(canvasDraw);
				}
				break;
			// 用戶手指在螢幕上移動
			case MotionEvent.ACTION_MOVE:
				// 记录移动位置的点的坐标
				float stopX = event.getX();
				float stopY = event.getY();
					if(BigX < stopX)
						BigX = stopX;
					if(BigY < stopY)
						BigY = stopY;
					if(smallX > stopX)
						smallX = stopX;
					if(smallY > stopY)
						smallY = stopY;
				//Log.d("stopX",String.valueOf(stopX));

				//清空上一個canvas避免殘影
				if(canvasDraw!=null){ 
					Paint cleancanvas = new Paint(); 
					cleancanvas.setXfermode(new PorterDuffXfermode(Mode.CLEAR));  
					canvasDraw.drawPaint(cleancanvas); 
					cleancanvas.setXfermode(new PorterDuffXfermode(Mode.SRC));
				}
				//將上一個畫好的bitmap先畫到canvasdraw避免繪製新的bitmap舊的會暫時消失
				canvasDraw.drawBitmap(saveBitmap, 0, 0, null);
				//傳移動值
				curAction.move(stopX, stopY);
				//開始畫畫
				curAction.draw(canvasDraw);
				// 把图片展示到ImageView中
				((ImageView) v).setImageBitmap(drawBitmap);
				break;
			case MotionEvent.ACTION_UP:

				//把畫好的圖存到canvasSave跟canvasDraw分開
				curAction.draw(canvasSave);
				//顯示saveBitmap
				((ImageView) v).setImageBitmap(saveBitmap);
				mActions.add(curAction);
				if(cutBigX < BigX)
					cutBigX = BigX;
				if(cutBigY < BigY )
					cutBigY = BigY;
				if(cutSmallX > smallX)
					cutSmallX = smallX;
				if(cutSmallY > smallY)
					cutSmallY = smallY;
				undoCutXY[undoCutXYIndex1][undoCutXYIndex2] = cutSmallX;
				undoCutXY[undoCutXYIndex1][undoCutXYIndex2+1] = cutBigX;
				undoCutXY[undoCutXYIndex1][undoCutXYIndex2+2] = cutSmallY;
				undoCutXY[undoCutXYIndex1][undoCutXYIndex2+3] = cutBigY;
				undoCutXYIndex1++;
				undoCutXYIndex2=0;

//				Log.d("cutsmallX",String.valueOf(cutSmallX));
//				Log.d("cutsmallY",String.valueOf(cutSmallY));
//				Log.d("cutBigX",String.valueOf(cutBigX));
//				Log.d("cutBigY",String.valueOf(cutBigY));
				break;
			default:
				break;
			}
			return true;
		}
		
	};
	//endregion

	//region 設定畫筆資訊
	public void setCurAction(float x, float y) {
		switch (type) {
		case Point:
			curAction = new MyPoint(x, y, currentSize, currentColor);
			break;
		case Path:
			curAction = new MyPath(x, y, currentSize, currentColor);
			break;
		case Line:
			curAction = new MyLine(x, y, currentSize, currentColor);
			break;
		case Rect:
			curAction = new MyRect(x, y, currentSize, currentColor);
			break;
		case Circle:
			curAction = new MyCircle(x, y, currentSize, currentColor);
			break;
		case FillecRect:
			curAction = new MyFillRect(x, y, currentSize, currentColor);
			break;
		case FilledCircle:
			curAction = new MyFillCircle(x, y, currentSize, currentColor);
			break;
		case Eraser:
			curAction = new Myeraser(x, y, currentSize, currentColor);
			break;
		case Love:
			curAction = new MyLove(x, y, currentSize, currentColor);
			default:
			break;
		}
	}
	//endregion

	/**
	 * 畫筆形狀定義
	 */
	public enum ActionType {
		Point, Path, Line, Rect, Circle, FillecRect, FilledCircle, Eraser, Love
	}

	/**
	 * 設置畫筆的颜色
	 */
	public void setColor(int color) {
//		currentColor = Color.parseColor(color);
		currentColor = color;
	}

	/**
	 * 設置畫筆的粗细
	 */
	public void setSize(int size) {
		currentSize = size;
	}

	/**
	 * 设置当前画笔的形状
	 */
	public void setType(ActionType type) {
		this.type = type;
	}

	//region 從linearlayout中偵測我按的是哪一個顏色
	public View.OnClickListener colorchoose = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			// TODO 自動產生的方法 Stub
			switch (view.getId()) {
				case R.id.colorRed:
					setColor(getResources().getColor(R.color.md_red_500));
					break;
				case R.id.colorGreen:
					setColor(getResources().getColor(R.color.md_green_500));
					break;
				case R.id.colorBlue:
					setColor(getResources().getColor(R.color.md_blue_500));
					break;
				case R.id.colorPurple:
					setColor(getResources().getColor(R.color.md_purple_500));
					break;
				case R.id.colorYellow:
					setColor(getResources().getColor(R.color.md_yellow_500));
					break;
				case R.id.colorOrange:
					setColor(getResources().getColor(R.color.md_orange_500));
					break;
				case R.id.colorBrown:
					setColor(getResources().getColor(R.color.md_brown_500));
					break;
				case R.id.colorGray:
					setColor(getResources().getColor(R.color.md_grey_500));
					break;
				case R.id.colorBlack:
					setColor(getResources().getColor(R.color.md_black_1000));
					break;
				case R.id.palette:
					//調色盤
					ColorPickerDialog dialog = new ColorPickerDialog(getActivity(), currentColor, "Choose Color",
							new ColorPickerDialog.OnColorChangedListener() {
								public void colorChanged(int color2) {
									//set your color variable
									setColor(color2);
									if (paintSizelinr.getVisibility() == View.VISIBLE) {
										//選完顏色同時也要把畫筆大小的顏色換成所選的
										Bitmap paintSizeBitmap = Bitmap.createBitmap(paintImgSize.getWidth(), paintImgSize.getHeight(), Bitmap.Config.ARGB_8888);
										Canvas paintSizeCanvas = new Canvas(paintSizeBitmap);

										Paint cleancanvas = new Paint();
										cleancanvas.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
										paintSizeCanvas.drawPaint(cleancanvas);
										cleancanvas.setXfermode(new PorterDuffXfermode(Mode.SRC));

										setSize(choosePaintSize);
										Paint seekBarShowPaintSize = new Paint();
										seekBarShowPaintSize.setAntiAlias(true);
										seekBarShowPaintSize.setColor(currentColor);
										seekBarShowPaintSize.setStyle(Paint.Style.FILL);
										seekBarShowPaintSize.setStrokeWidth(choosePaintSize);

										paintSizeCanvas.drawCircle(paintImgSize.getWidth() / 2, paintImgSize.getHeight() / 2, choosePaintSize / 2, seekBarShowPaintSize);
										paintImgSize.setImageBitmap(paintSizeBitmap);
									}
								}
							});
					dialog.show();
					break;
				default:
					break;
			}

			if (paintSizelinr.getVisibility() == View.VISIBLE) {
				//選完顏色同時也要把畫筆大小的顏色換成所選的
				Bitmap paintSizeBitmap = Bitmap.createBitmap(paintImgSize.getWidth(), paintImgSize.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas paintSizeCanvas = new Canvas(paintSizeBitmap);

				Paint cleancanvas = new Paint();
				cleancanvas.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
				paintSizeCanvas.drawPaint(cleancanvas);
				cleancanvas.setXfermode(new PorterDuffXfermode(Mode.SRC));

				setSize(choosePaintSize);
				Paint seekBarShowPaintSize = new Paint();
				seekBarShowPaintSize.setAntiAlias(true);
				seekBarShowPaintSize.setColor(currentColor);
				seekBarShowPaintSize.setStyle(Paint.Style.FILL);
				seekBarShowPaintSize.setStrokeWidth(choosePaintSize);

				paintSizeCanvas.drawCircle(paintImgSize.getWidth() / 2, paintImgSize.getHeight() / 2, choosePaintSize / 2, seekBarShowPaintSize);
				paintImgSize.setImageBitmap(paintSizeBitmap);
			}
		}
	};
	//endregion

	//region 從linearlayout中偵測我按的是哪一個圖形
	public View.OnClickListener shapechoose = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.point:
					setType(ActionType.Point);
					break;
				case R.id.path:
					setType(ActionType.Path);
					break;
				case R.id.line:
					setType(ActionType.Line);
					break;
				case R.id.rectangle:
					setType(ActionType.Rect);
					break;
				case R.id.circle:
					setType(ActionType.Circle);
					break;
				case R.id.rectanglefall:
					setType(ActionType.FillecRect);
					break;
				case R.id.circlefall:
					setType(ActionType.FilledCircle);
					break;
				case R.id.love:
					setType(ActionType.Love);
				default:
					break;
			}
		}
	};
	//endregion

	//region 偵測要設定那個背景
	public View.OnClickListener backGroundChange = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			switch (view.getId()) {
				case R.id.background:
					//backimage.setBackground(getResources().getDrawable(R.drawable.background));
					backimage.setImageDrawable(getResources().getDrawable(R.drawable.background));
					break;
				case R.id.background1:
					//backimage.setBackground(getResources().getDrawable(R.drawable.background1));
					backimage.setImageDrawable(getResources().getDrawable(R.drawable.background1));
					break;
				case R.id.background2:
					//backimage.setBackground(getResources().getDrawable(R.drawable.background2));
					backimage.setImageDrawable(getResources().getDrawable(R.drawable.background2));
					break;
				case R.id.background3:
					//backimage.setBackground(getResources().getDrawable(R.drawable.background3));
					backimage.setImageDrawable(getResources().getDrawable(R.drawable.background3));
					break;
				case R.id.background4:
					//backimage.setBackground(getResources().getDrawable(R.drawable.background4));
					backimage.setImageDrawable(getResources().getDrawable(R.drawable.background4));
					break;
				case R.id.background5:
					//backimage.setBackground(getResources().getDrawable(R.drawable.background5));
					backimage.setImageDrawable(getResources().getDrawable(R.drawable.background5));
					break;
				case R.id.background6:
					//backimage.setBackground(getResources().getDrawable(R.drawable.background6));
					backimage.setImageDrawable(getResources().getDrawable(R.drawable.background6));
					break;
				case R.id.background7:
					//backimage.setBackground(getResources().getDrawable(R.drawable.background7));
					backimage.setImageDrawable(getResources().getDrawable(R.drawable.background7));
					break;
				case R.id.background8:
					//backimage.setBackground(getResources().getDrawable(R.drawable.background8));
					backimage.setImageDrawable(getResources().getDrawable(R.drawable.background8));
					break;
				case R.id.background9:
					//backimage.setBackground(getResources().getDrawable(R.drawable.background9));
					backimage.setImageDrawable(getResources().getDrawable(R.drawable.background9));
					break;
				case R.id.background10:
					//backimage.setBackground(getResources().getDrawable(R.drawable.background10));
					backimage.setImageDrawable(getResources().getDrawable(R.drawable.background10));
					break;
				case R.id.background11:
					//backimage.setBackground(getResources().getDrawable(R.drawable.background11));
					backimage.setImageDrawable(getResources().getDrawable(R.drawable.background11));
					break;
				case R.id.background12:
					//backimage.setBackground(getResources().getDrawable(R.drawable.background12));
					backimage.setImageDrawable(getResources().getDrawable(R.drawable.background12));
					break;
				case R.id.background13:
					//backimage.setBackground(getResources().getDrawable(R.drawable.background13));
					backimage.setImageDrawable(getResources().getDrawable(R.drawable.background13));
					break;
				case R.id.background14:
					//backimage.setBackground(getResources().getDrawable(R.drawable.background14));
					backimage.setImageDrawable(getResources().getDrawable(R.drawable.background14));
					break;
				case R.id.background15:
					//backimage.setBackground(getResources().getDrawable(R.drawable.background15));
					backimage.setImageDrawable(getResources().getDrawable(R.drawable.background15));
					break;
				case R.id.background16:
					//backimage.setBackground(getResources().getDrawable(R.drawable.background16));
					backimage.setImageDrawable(getResources().getDrawable(R.drawable.background16));
					break;
				case R.id.background17:
					if (choosepicture != 0) {
						ViewGroup viewGroup = Relativelay;
						img currentView = (img) viewGroup.getChildAt(choosepicture);
						//將目前選去要刪除的圖片的後面每一張圖片的ID都往前移一個
						for (int i = choosepicture + 1; i < viewGroup.getChildCount(); i++) {
							viewGroup.getChildAt(i).setId(i - 1);
						}
						//將目前選擇要刪除的照片比他大的Z都要減1
						for (int z = 1; z < viewGroup.getChildCount(); z++) {
							if ((int) currentView.getZ() < (int) viewGroup.getChildAt(z).getZ()) {
								viewGroup.getChildAt(z).setZ((int) viewGroup.getChildAt(z).getZ() - 1);
							}
						}
						currentView.buildDrawingCache();
						backimage.setScaleType(ImageView.ScaleType.CENTER_CROP);
						backimage.setImageDrawable(currentView.getDrawable());
						viewGroup.removeView(currentView);
						countpicture--;
						choosepicture = 0;
					} else
						makeTextAndShow(getActivity(), "沒有選擇圖片", android.widget.Toast.LENGTH_SHORT);
					break;
				default:
					break;
			}
		}
	};


	//endregion

	//region 清空View暫存
	/*
	public static void recycleImageView(View view){
		if(view==null) return;
		if(view instanceof ImageView){
			Drawable drawable=((ImageView) view).getDrawable();
			if(drawable instanceof BitmapDrawable){
				Bitmap bmp = ((BitmapDrawable)drawable).getBitmap();
				if (bmp != null && !bmp.isRecycled()){
					((ImageView) view).setImageBitmap(null);
					bmp.recycle();
					bmp=null;
				}
			}
		}
	}  */
	//endregion

	//region 音樂控制區
//	@Override
//	protected void onResume()
//	{
//		// TODO Auto-generated method stub
//
//		super.onResume();
//		mPlayer.start();
//	}
//
//	@Override
//	protected void onPause()
//	{
//		// TODO Auto-generated method stub
//
//		super.onPause();
//		mPlayer.pause();
//	}
//
//	@Override
//	protected void onDestroy()
//	{
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		mPlayer.release();
//	}
	//endregion

	//region 切換頁面回傳值的地方
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {



		super.onActivityResult(requestCode, resultCode, data);



		if (requestCode == 0 && resultCode == getActivity().RESULT_OK) {

			Bundle bundle =  data.getExtras();

			Bitmap textBitmap = Bitmap.createBitmap(bundle.getInt("textWidths")+300,200, Bitmap.Config.ARGB_8888);
			Canvas textCanvas = new Canvas(textBitmap);


			Paint textPaint = new Paint();
			textPaint.setAntiAlias(true);//抗鋸齒
			textPaint.setColor(bundle.getInt("color"));
			textPaint.setTextSize(bundle.getInt("textSize") - 10);
			textPaint.setFakeBoldText(bundle.getBoolean("FakeBold")); //true為粗體，false為非粗體
			textPaint.setTextSkewX(bundle.getFloat("skewX")); //負數表示右斜，整数左斜
			textPaint.setUnderlineText(bundle.getBoolean("underLine"));
			Typeface type = Typeface.createFromAsset(getActivity().getAssets(), bundle.getString("textFonts"));
			textPaint.setTypeface(type);
			//textPaint.setStyle(Paint.Style.FILL);
			textPaint.setStrokeWidth(30);

			int x = (int)(Math.random() * 300  );
			int y = (int)(Math.random() * 1300 +200 );


			textCanvas.drawText(bundle.getString("text"), 130,170, textPaint);
			img = new img(getActivity(),textBitmap,1);
			//不讓字體有點擊透明穿越的能力所以繼承原有的imageview
			//img = new ImageView(getActivity());
			img.setId(countpicture);
			img.setZ(countpicture);
			img.setX(x);
			img.setY(y);
			//img.setTag(2);

			img.setImageBitmap(textBitmap);
			Relativelay.addView(img);
			img.setOnTouchListener(new MultiTouchListener());
			img.setOnClickListener(imgOnClickListener);

			countpicture++;


		}}
	//endregion
//
//	public static Bitmap getMagicDrawingCache(View view) {
//	Bitmap.Config bitmap_quality = Bitmap.Config.ARGB_8888;
//	Boolean quick_cache = false;
//	int color_background = Color.BLACK;
//	Bitmap bitmap = (Bitmap) view.getTag(R.id.cacheBitmapKey);
//	Boolean dirty = (Boolean) view.getTag(R.id.cacheBitmapDirtyKey);
//	if (view.getWidth() + view.getHeight() == 0) {
//		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//	}
//	int viewWidth = (int) (view.getWidth()*view.getScaleX());
//	int viewHeight = (int) (view.getHeight()*view.getScaleY());
//	if (bitmap == null || bitmap.getWidth() != viewWidth || bitmap.getHeight() != viewHeight) {
//		if (bitmap != null && !bitmap.isRecycled()) {
//			bitmap.recycle();
//		}
//		bitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
//		view.setTag(R.id.cacheBitmapKey, bitmap);
//		dirty = true;
//	}
//	if (dirty == true || !quick_cache) {
//		bitmap.eraseColor(color_background);
//		Canvas canvas = new Canvas(bitmap);
//		view.draw(canvas);
//		view.setTag(R.id.cacheBitmapDirtyKey, false);
//	}
//	return bitmap;
//}

}
