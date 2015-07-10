package com.fcu.photocollage;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.fcu.speechtag.MyRecoder;
import com.fcu.R;
import com.fcu.imagepicker.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.SpeechRecognizer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ViewSwitcher.ViewFactory;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements ViewFactory{	
	private ArrayList<String> paths;	//儲存圖片路徑
	private ArrayList<Photo> pList;		//照片清單
	private String musicPath;			//儲存音樂路徑
	private String user = "Kris";		//使用者名稱
	private DisplayMetrics mPhone;		//手機解析度
	private TextView textSec;			//秒數文字
	private Button btnGenerate;			//產生按鈕
	private Button btnAddPic;			//增加圖片按鈕
	private Button btnAddMus;			//增加音樂按鈕
	private Button btnAddSpe;			//增加語音按鈕
	private SeekBar seekBarSec;			//秒數滑動桿
	private LinearLayout linelay;		//圖片縮圖線性布局
	private ImageSwitcher imgSwi2;		//圖片選擇器
	private int currentPhoto;			//當前選擇的照片編號
	private final static int MUSIC = 11 ;
	private int photoCount = 0;
	//上傳圖片
	//private HttpPhotoUpload photoUpload = new HttpPhotoUpload("http://192.168.0.100/php/UploadPhoto.php", user);
	private HttpPhotoUpload photoUpload = new HttpPhotoUpload("http://140.134.26.13/PhotoCollage/php/UploadPhoto.php", user);
	//增加語音
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
    private ImageButton btnPlay;
    //private Intent recognizerIntent;
    //private SpeechRecognizer sr;
    private LayoutInflater inflater;
 
    
	private void initializeVariables() {
		textSec = (TextView)findViewById(R.id.text_sec);
		btnGenerate = (Button)findViewById(R.id.btn_generate);
		btnAddPic = (Button)findViewById(R.id.btn_addPic);
		btnAddMus = (Button)findViewById(R.id.btn_addMus);
		btnAddSpe = (Button)findViewById(R.id.btn_addSpe);
		seekBarSec = (SeekBar)findViewById(R.id.seekBar_sec);
		musicPath = null;
		
		inflater = LayoutInflater.from(MainActivity.this);
		login_view = inflater.inflate(R.layout.activity_record,null);
		record_button = (ImageButton)login_view.findViewById(R.id.record_button);
        record_volumn = (ProgressBar)login_view.findViewById(R.id.record_volumn);
        record_ok = (Button)login_view.findViewById(R.id.record_ok);
        record_cancel = (Button)login_view.findViewById(R.id.record_cancel);
        btnPlay = (ImageButton)findViewById(R.id.btn_play);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
        ScreenUtils.initScreen(this);
    	//titleTV = (TextView) findViewById(R.id.text_bar);
		initializeVariables();				
		//圖片畫廊
		linelay = (LinearLayout)findViewById(R.id.anogallery);        
        imgSwi2 = (ImageSwitcher)findViewById(R.id.imgSw2);
        imgSwi2.setFactory(this);
        imgSwi2.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        imgSwi2.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));  
        pList = new ArrayList<Photo>();
        /*
        for(int i=0; i < thumbImgArr.length; i++)
        {
        	linelay.addView(getImageView(i));
        }
		*/
        //加入圖片
        btnAddPic.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {								
				Intent intent = new Intent(MainActivity.this, com.fcu.imagepicker.PhotoWallActivity.class);
		        startActivity(intent);				
			}
		});
        
        //加入音樂
        btnAddMus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("audio/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(intent, MUSIC);				
			}	        	
        });
        //加入語音
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("語音標籤");
		builder.setView(login_view);
		dialog = builder.create();
        btnAddSpe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(btnAddSpe.getText().equals("加入語音")){
					dialog.show();
					//pList.get( currentPhoto ).setRecPath("/sdcard/" + fileName);	//儲存語音路徑
					//btnPlay.setVisibility(View.VISIBLE);
					//btnAddSpe.setText("移除語音");
				}
				else{
					fileName = currentPhoto + ".3gp";
					Log.i("DEL",fileName);
	            	File gpFile = new File("/sdcard/" + fileName);
	            	if( gpFile.delete() ){
	            		pList.get( currentPhoto ).setRecPath(null);	//移除語音路徑
	            		Toast.makeText(MainActivity.this, "移除成功", Toast.LENGTH_SHORT).show();	            		
	            		btnPlay.setVisibility(View.INVISIBLE);
	            		btnAddSpe.setText("加入語音");
	            	}	            	
				}				
			}        	
        });
        //播放語音
        // 建立指定資源的MediaPlayer物件
        /*
        btnPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("/sdcard/" + fileName);
		        mediaPlayer = MediaPlayer.create(this, uri);
				mediaPlayer.start();				
			}        	
        });        
        */
		//產生電影
		btnGenerate.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				photoUpload.upload(pList, musicPath);
				/*RunPhp ffmpegShell = new RunPhp();
				String urlFCU = "http://140.134.26.13/PhotoCollage/php/ffmpegShell.php";
				String urlHOME = "http://192.168.0.100/php/ffmpegShell.php";
		        String msg = ffmpegShell.stringQuery(urlHOME);
		        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();*/
				Intent it = new Intent();
				it.setClass(MainActivity.this, MovieView.class);
				startActivity(it);
				
			}
		});
		//SeekBar秒數限制
		seekBarSec.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				textSec.setText("秒數：" + (progress + 1) );
				pList.get(currentPhoto).setSec(progress + 1);
				
			}
		});
	}
	/**
	 *  點選縮圖事件
	 */
	private ImageView getImageView(int i, int pCount){
		//讀取手機解析度
		//mPhone = new DisplayMetrics();
	    //getWindowManager().getDefaultDisplay().getMetrics(mPhone);
	    
	    ImageView img = new ImageView(this);
	    img.setBackgroundColor(Color.BLACK);	//設定 ImageView 背景顏色
	    img.setPadding(5, 2, 5, 2);	//設定 ImageView 內縮
	    //將 ImageView 置中
	    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
	    lp.gravity = Gravity.CENTER;
	    img.setLayoutParams(lp);

        File imgFile = new File(paths.get(i));
        //圖片壓縮
        Bitmap myBitmap = ScalePicEx(imgFile.getAbsolutePath(), 768, 1024);
        //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        //Bitmap sBitmap = ScalePic(myBitmap, 1.3f, 1.4f);
        img.setImageBitmap(myBitmap);
        img.setId(pCount);
        img.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
            	currentPhoto = v.getId();	//設定當前選擇的照片編號
            	btnAddSpe.setEnabled(true);
            	seekBarSec.setEnabled(true);
                //imgSwi2.setImageResource(imgArr[v.getId()]);
            	//File sFile = new File(paths.get(currentPhoto));
            	//fileName = currentPhoto + ".3gp";
            	//File gpFile = new File("/sdcard/" + fileName);
            	Log.i("onClickView", Integer.toString(currentPhoto));
            	seekBarSec.setProgress(pList.get(currentPhoto).getSec() - 1);	//設定SeekBar秒數            	
                record_ok.setEnabled(false);//隱藏確定按鈕
            	//判斷加入語音
            	if( pList.get(currentPhoto).getRecPath() == null ){
            		//隱藏播放圖示
                    btnPlay.setVisibility(View.INVISIBLE);                    
                    Log.i("TEST", "File not exist");
                    //加入語音
                    btnAddSpe.setText("加入語音");
                }
            	else{
            		btnPlay.setVisibility(View.VISIBLE);
            		Log.i("TEST", "File exist");
            		//移除語音
            		btnAddSpe.setText("移除語音");
            	}
            	//btnAddSpe.setId(v.getId());
            	//Bitmap sBitmap = ScalePicEx(sFile.getAbsolutePath(), 800f, 500f);
            	
            	/*Bitmap sBitmap = ScalePic(BitmapFactory.decodeFile(sFile.getAbsolutePath()), 12, 17);
            	Drawable sDrawable = new BitmapDrawable(getResources(), sBitmap);
                imgSwi2.setImageDrawable(sDrawable);*/
                Toast.makeText(v.getContext(), "您選擇了"+( currentPhoto + 1 ), Toast.LENGTH_SHORT).show();                
            }
        });
        
        return img;
    }    
    
    public View makeView(){
        ImageView v1 = new ImageView(this);
        v1.setBackgroundColor(0xFF000000);	//設定背景顏色
        v1.setScaleType(ImageView.ScaleType.FIT_CENTER);	//設定填充方式        
        v1.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        return v1;
    }
    
    /**
     * 壓縮圖片
     * @param bitmap
     * @param w
     * @param h
     * @return
     */
    public Bitmap ScalePic(Bitmap bitmap, float w, float h){
    	//轉換為圖片指定大小
        //獲得圖片的寬高
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
    	Bitmap bmResult = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    	return bmResult;
    }
    /**
     * 不失真壓縮圖片
     * @param path
     * @param h
     * @param w
     * @return
     */
    public Bitmap ScalePicEx(String path, int height, int width){

        BitmapFactory.Options opts = null;  
        opts = new BitmapFactory.Options();  
        opts.inJustDecodeBounds = true;  
        BitmapFactory.decodeFile(path, opts);  
        // 計算圖片縮放比例 
        final int minSideLength = Math.min(width, height);  
        opts.inSampleSize = computeSampleSize(opts, minSideLength,  
                width * height);  
        opts.inJustDecodeBounds = false;  
        opts.inInputShareable = true;  
        opts.inPurgeable = true;  
        return BitmapFactory.decodeFile(path, opts);  

    	/*BitmapFactory.Options options =new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
	    //計算縮放比
	    int be = (int)(options.outHeight / h);
	    if (be <= 0)
	    	be = 1;
	    options.inSampleSize = be;
	    //重新讀入圖片，注意這次要把options.inJustDecodeBounds 設為 false哦
	    bitmap = BitmapFactory.decodeFile(path, options);
	    int wg = bitmap.getWidth();
	    int hg = bitmap.getHeight();
	    Log.i("PIC",wg + "," + hg);
	    return bitmap;*/
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
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math  
                .floor(w / minSideLength), Math.floor(h / minSideLength));  
      
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
        paths = bundle.getStringArrayList("paths1");
        Log.i("PHOTO - URI",paths.get(0));        
    	for(int i = 0; i < paths.size(); i++){
    		Photo tmpP = new Photo(paths.get(i), null, 1, 3, 0, 1);
    		//取得拍攝日期    		
    		try {
				ExifInterface exifInterface = new ExifInterface(tmpP.getpPath());
				String FDateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
				if( FDateTime != null){
					//先行定義時間格式
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
					
					//轉換時間格式
					Date date = (Date) sdf.parse(FDateTime);
					Log.i("GET1",date.toString());
					sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					String date2 = sdf.format(date);				
					Log.i("GET2",date2);
					//加入拍攝時間
					tmpP.setTakeDate(date2);
				}
				else{
					tmpP.setTakeDate("0000-00-00 00:00:00");
				}
			} catch (IOException e) {
				Log.i("GET","TakeDate Error");
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
    		pList.add(tmpP);	//將相片加入相片群    		
    		linelay.addView(getImageView(i,photoCount));
    		Log.i("ADD-PHOTO",Integer.toString(photoCount));
    		photoCount++;
    	}

    }
    
	@Override 
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	//藉由requestCode判斷是否為新增音樂而呼叫的，且data不為null
    	if (requestCode == MUSIC && data != null)
    	{
    		//取得音樂路徑uri
    		Uri uri = data.getData();
    		Log.i("Music - URI",uri.toString());
    		musicPath = uri.toString();
    	}
    }
	
	/**
	 *  點擊確定與取消按鈕都會呼叫這個方法
	 * @param view
	 */
    public void onSubmit(View view) {
        // 確定按鈕
        if (view.getId() == R.id.record_ok) {
            // 讀取使用者輸入的標題與內容
            /*String titleText = title_text.getText().toString();
            String contentText = content_text.getText().toString();*/
        	pList.get( currentPhoto ).setRecPath("/sdcard/" + fileName);	//儲存語音路徑
			btnPlay.setVisibility(View.VISIBLE);
			btnAddSpe.setText("移除語音");
			record_ok.setEnabled(false);//隱藏確定按鈕
            // 取得回傳資料用的Intent物件
            Intent result = getIntent();
            // 設定標題與內容
            /*result.putExtra("titleText", titleText);
            result.putExtra("contentText", contentText);*/
 
            // 設定回應結果為確定
            setResult(Activity.RESULT_OK, result);
        }
        else if(view.getId() == R.id.record_cancel) {
        	record_ok.setEnabled(false);//隱藏確定按鈕
        }
 
        // 結束
        dialog.dismiss(); 
        //finish();
    }
    /**
     * 錄音按鈕事件
     * @param view
     */
    public void clickRecord(View view) {
        // 切換
        isRecording = !isRecording;
 
        // 開始錄音
        if (isRecording) {
        	//錄音中，關閉確定和取消按鈕
        	record_ok.setEnabled(false);
        	record_cancel.setEnabled(false);
            // 設定按鈕圖示為錄音中
            record_button.setImageResource(R.drawable.record_red_icon);
            // 建立錄音物件
            myRecoder = new MyRecoder(fileName);
            // 開始錄音
            myRecoder.start();
            // 建立並執行顯示麥克風音量的AsyncTask物件
            //new MicLevelTask().execute();
            
    		 
            //開始辨識，將此code移到某個按鈕的onClick()裡            
            //  sr.startListening(recognizerIntent);
            
        }
        // 停止錄音
        else {
            // 設定按鈕圖示為停止錄音
            record_button.setImageResource(R.drawable.record_dark_icon);
            // 麥克風音量歸零
            record_volumn.setProgress(0);
            // 停止錄音
            myRecoder.stop();
            //錄音完成，開啟確定和取消按鈕
            record_cancel.setEnabled(true);
            record_ok.setEnabled(true);
            
	        //停止辨識，將此code移到某個按鈕的onClick()裡
	        //我們平常不需要做這個處理，語音辨識完畢它自己會停
	        //但如果我們想中斷它，或者有異常發生時，就可以呼叫底下的程式碼
	        //sr.stopListening();
	        //sr.cancel();            
        }
    } 
    
    //播放語音事件
    public void onPlay(View view) {
    	Uri uri = Uri.parse("/sdcard/" + fileName);
        mediaPlayer = MediaPlayer.create(this, uri);
		mediaPlayer.start();
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
}
