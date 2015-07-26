package com.fcu.imagepicker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.fcu.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.widget.ImageView;


/**
 * 從SDCard異步加載圖片
 */
public class SDCardImageLoader {
    //緩存
    private LruCache<String, Bitmap> imageCache;
    // 固定2個線程來執行任務
    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    private Handler handler = new Handler();

    private int screenW, screenH;

    public SDCardImageLoader(int screenW, int screenH) {
        this.screenW = screenW;
        this.screenH = screenH;

        // 獲取應用程序最大可用內存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;

        // 設置圖片緩存大小為程序最大可用內存的1/8
        imageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    private Bitmap loadDrawable(final int smallRate, final String filePath,
                                final ImageCallback callback) {
        // 如果緩存過就從緩存中取出數據
        if (imageCache.get(filePath) != null) {
            return imageCache.get(filePath);
        }

        // 如果緩存沒有則讀取SD卡
        executorService.submit(new Runnable() {
            public void run() {
                try {
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(filePath, opt);

                    // 獲取到這個圖片的原始寬度和高度
                    int picWidth = opt.outWidth;
                    int picHeight = opt.outHeight;

                    //讀取圖片失敗時直接返回
                    if (picWidth == 0 || picHeight == 0) {
                        return;
                    }

                    //初始壓縮比例
                    opt.inSampleSize = smallRate;
                    // 根據屏的大小和圖片大小計算出縮放比例
                    if (picWidth > picHeight) {
                        if (picWidth > screenW)
                            opt.inSampleSize *= picWidth / screenW;
                    } else {
                        if (picHeight > screenH)
                            opt.inSampleSize *= picHeight / screenH;
                    }

                    //這次再真正地生成一個有像素的，經過縮放了的bitmap
                    opt.inJustDecodeBounds = false;
                    final Bitmap bmp = BitmapFactory.decodeFile(filePath, opt);
                    //存入map
                    imageCache.put(filePath, bmp);

                    handler.post(new Runnable() {
                        public void run() {
                            callback.imageLoaded(bmp);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return null;
    }

    /**
     * 異步讀取SD卡圖片，並按指定的比例進行壓縮（最大不超過屏幕像素數）
     *
     * @param smallRate 壓縮比例，不壓縮時輸入1，此時將按屏幕像素數進行輸出
     * @param filePath  圖片在SD卡的全路徑
     * @param imageView 組件
     */
    public void loadImage(int smallRate, final String filePath, final ImageView imageView) {

        Bitmap bmp = loadDrawable(smallRate, filePath, new ImageCallback() {

            @Override
            public void imageLoaded(Bitmap bmp) {
                if (imageView.getTag().equals(filePath)) {
                    if (bmp != null) {
                        imageView.setImageBitmap(bmp);
                    } else {
                        imageView.setImageResource(R.drawable.empty_photo);
                    }
                }
            }
        });

        if (bmp != null) {
            if (imageView.getTag().equals(filePath)) {
                imageView.setImageBitmap(bmp);
            }
        } else {
            imageView.setImageResource(R.drawable.empty_photo);
        }

    }


    // 對外界開放的回調接口
    public interface ImageCallback {
        // 注意 此方法是用來設置目標對象的圖像資源
        public void imageLoaded(Bitmap imageDrawable);
    }
}
