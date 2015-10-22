package com.fcu.photocollage.collage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.fcu.photocollage.R;

/**
 * Created by Lighting on 2015/8/31.
 */
public class AddText extends Activity {
    //region 定義
    private static 	Toast toast;
    private ImageView textShow;                                         //顯示目前字體樣式的imageview
    private Context Context=AddText.this;
    private int currentColor;                                           //目前顏色
    private int textShowW,textShowH;                                    //imageview的長跟寬
    private String currentText;                                         //目前所打的文字
    private int currentTextSize=100;                                    //目前的字體大小
    private String currentTextFonts = "fonts/AdobeArabic-Bold.otf";     //目前的字型
    private float currentSkewX=0.0f;                                    //目前的字型傾斜度
    private SeekBar skewSeekBar;                                        //傾斜度seekbar
    private CheckBox skewXcheckBox;                                     //左斜或右斜
    private CheckBox FakeBoldTextTruecheckBox;                          //是否要粗體
    private CheckBox UnderlineTextcheckBox;                             //是否要加底線
    private Boolean skewRightLeft = true;                               //是否為左斜
    private Button textFinish;                                          //確定產生字體按鈕
    private Boolean ifFirst = true;                                     //是否為第一次進入這個activity
    private Boolean currentUnderLine = false;                           //底線
    private Boolean cuurentFakeBold = false;                            //粗體
    private Bundle bundle;                                              //接收上一個activity所傳的值
    private int textWidths;
    private MediaPlayer mPlayer;
    //endregion


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_add);

        //region 布局設定
        skewSeekBar = (SeekBar)findViewById(R.id.skewXseekBar);
        skewXcheckBox = (CheckBox)findViewById(R.id.skewXcheckBox);
        FakeBoldTextTruecheckBox = (CheckBox)findViewById(R.id.FakeBoldTextTruecheckBox);
        UnderlineTextcheckBox = (CheckBox)findViewById(R.id.UnderlineTextcheckBox);
        textFinish = (Button)findViewById(R.id.textFinish);
        textShow = (ImageView) findViewById(R.id.textShow);
        //endregion

        //全螢幕模式
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //取得上一個Activity傳來的資訊
        bundle = getIntent().getExtras();
        setText(bundle.getString("text"));


        //region 確定產生字體
        textFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setClass(AddText.this, CollageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("color", currentColor);
                bundle.putFloat("skewX", currentSkewX);
                bundle.putString("text", currentText);
                bundle.putInt("textSize", currentTextSize);
                bundle.putString("textFonts", currentTextFonts);
                bundle.putBoolean("FakeBold", cuurentFakeBold);
                bundle.putBoolean("underLine", currentUnderLine);
                bundle.putInt("textWidths",textWidths);
                intent.putExtras(bundle);
                AddText.this.setResult(RESULT_OK, intent); //回傳RESULT_OK
                AddText.this.finish(); //關閉Activity
                ifFirst = true;


            }
        });
        //endregion

        //region 粗細體
        FakeBoldTextTruecheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(FakeBoldTextTruecheckBox.isChecked()) {
                    setFakeBold(true);
                    drawFonts();
                }
                else {
                    setFakeBold(false);
                    drawFonts();
                }

            }
        });
        //endregion

        //region 是否加底線
        UnderlineTextcheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(UnderlineTextcheckBox.isChecked()) {
                    setUnderLine(true);
                    drawFonts();
                }
                else {
                    setUnderLine(false);
                    drawFonts();
                }
            }
        });
        //endregion

        //region 判斷是否左右斜的checkbox
        skewXcheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
             if(skewXcheckBox.isChecked()) {
                 setTextSkewX(-currentSkewX);
                 //選完顏色同時也要把畫筆大小的顏色換成所選的
                 drawFonts();
                 skewRightLeft = false;
             }
             else {
                 setTextSkewX(-currentSkewX);
                 drawFonts();
                 skewRightLeft = true;
             }

         }
     });
        //endregion

        //region 傾斜度seekbar
        skewSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

        //當seekBar在拉動時
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO 自動產生的方法 Stub


            if(skewRightLeft==true)
                setTextSkewX((float) -progress / 10);
            else
                setTextSkewX((float) progress / 10);

            if(progress==0)
                setTextSkewX(0.0f);

            drawFonts();

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

        //region 點擊秀出文字的imageview可以更改輸入的文字
        textShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Context);
                builder.setIcon(R.mipmap.ic_format_size_white_24dp);
                builder.setTitle("請輸入想要加入的文字");
                //    通过LayoutInflater来載入一个xml的布局文件作為View對象
                View view = LayoutInflater.from(Context).inflate(R.layout.textdialog, null);
                //    自己定義的布局文件作為弹出框的Content
                builder.setView(view);

                final EditText text = (EditText)view.findViewById(R.id.text);
                text.setText(currentText);

                builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if("".equals(text.getText().toString().trim()))
                        {
                            makeTextAndShow(Context,"請輸入文字",android.widget.Toast.LENGTH_SHORT);
                        }
                        else {
                            currentText = text.getText().toString().trim();
                            countTextSize();
                            drawFonts();
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

    }

    //region 等待View載好才去使用
    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        // 在這裡getWidth()或getHeight()
        if (ifFirst == true) {
            textShowW = textShow.getMeasuredWidth();
            textShowH = textShow.getMeasuredHeight();
            ifFirst = false;
            setColor(getResources().getColor(R.color.md_black_1000));
            countTextSize();

        }
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

    //region 設定畫筆
    public void setColor(int color) {
        currentColor = color;
    }
    public  void setText(String Text){
        currentText = Text;
    }
    public  void setTextSkewX(float SkewX){
        currentSkewX = SkewX;
    }
    public void setTextFonts(String textFonts){
        currentTextFonts = textFonts;
    }
    public void setUnderLine(Boolean underLine){
        currentUnderLine = underLine;
    }
    public void setFakeBold(Boolean FakeBold){
        cuurentFakeBold = FakeBold;
    }
    public void setTextSize(int Size){
        currentTextSize = Size;
    }
    //endregion

    //region 從linearlayout中偵測我按的是哪一個顏色
    public void colorchoose(View view) {
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
                ColorPickerDialog dialog = new ColorPickerDialog(Context, currentColor, "Choose Color",
                        new ColorPickerDialog.OnColorChangedListener() {
                            public void colorChanged(int color2) {
                                //set your color variable
                                setColor(color2);

                                drawFonts();
                             }
                        });
                dialog.show();
                break;
            default:
                break;
        }
        drawFonts();


    }
    //endregion

    //region 從linearlayout中偵測我按的是哪一種字體
    public void chooseFonts(View view) {
        // TODO 自動產生的方法 Stub
        switch (view.getId()) {
            case R.id.AdobeArabicBold:
                setTextFonts("fonts/AdobeArabic-Bold.otf");
                break;
            case R.id.VLADIMIR:
                setTextFonts("fonts/VLADIMIR.TTF");
            case R.id.AdobeArabicRegular:
                setTextFonts("fonts/AdobeArabic-Regular.otf");
                break;
            case R.id.AspergitBold:
                setTextFonts("fonts/Aspergit Bold.ttf");
                break;
            case R.id.Aspergit:
                setTextFonts("fonts/Aspergit.ttf");
                break;
            case R.id.BodoniFLFBold:
                setTextFonts("fonts/BodoniFLF-Bold.ttf");
                break;
            case R.id.font2:
                setTextFonts("fonts/font2.TTF");
                break;
            case R.id.custom:
                setTextFonts("fonts/custom.ttf");
                break;
            default:
                break;
        }
        countTextSize();
        drawFonts();
    }
    //endregion

    //region 把所選的任何資訊都畫到imageview上
    public void drawFonts()
    {
        Bitmap textBitmap = Bitmap.createBitmap(textShowW,textShowH, Bitmap.Config.ARGB_8888);
        Canvas textCanvas = new Canvas(textBitmap);

        Paint cleancanvas = new Paint();
        cleancanvas.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        textCanvas.drawPaint(cleancanvas);
        cleancanvas.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(currentColor);
        textPaint.setTextSize(currentTextSize);
        textPaint.setFakeBoldText(cuurentFakeBold); //true為粗體，false為非粗體
        textPaint.setTextSkewX(currentSkewX); //負數表示右斜，整数左斜
        textPaint.setUnderlineText(currentUnderLine);
        Typeface type = Typeface.createFromAsset(getAssets(), currentTextFonts);
        textPaint.setTypeface(type);
        textPaint.setStrokeWidth(30);

        textCanvas.drawText(currentText, 50, 400, textPaint);
        textShow.setImageBitmap(textBitmap);
    }
    //endregion

    //region dip to pixel
    public static int DipToPixels(Context context,int dip) {
        final float SCALE = context.getResources().getDisplayMetrics().density/2.8f;
        float valueDips =  dip;
        int valuePixels = (int)(valueDips * SCALE + 0.5f);
        return valuePixels;

    }
    //endregion

    //region 計算字體大小
    public void countTextSize(){
        currentTextSize=200;
        if (currentText.length() > 0) {
            Paint testPaint = new Paint();
            //獲得TextView的寬度
            int availableWidth = textShow.getWidth();
            testPaint.setTextSize(currentTextSize);
            Typeface type = Typeface.createFromAsset(getAssets(), currentTextFonts);
            testPaint.setTypeface(type);
            testPaint.setTextSkewX(currentSkewX); //負數表示右斜，整数左斜
            textWidths = (int)testPaint.measureText(currentText);
            textWidths = DipToPixels(Context,textWidths);
            while (textWidths > availableWidth) {
                currentTextSize--;
                setTextSize(currentTextSize);
                testPaint.setTextSize(currentTextSize);
                testPaint.setTypeface(type);
                testPaint.setTextSkewX(currentSkewX); //負數表示右斜，整数左斜
                textWidths = (int)testPaint.measureText(currentText);
                textWidths = DipToPixels(Context,textWidths);
            }
            setTextSize(currentTextSize);
            drawFonts();

        }
    }
    //endregion

}
