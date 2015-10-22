package com.fcu.photocollage.collage;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Switch;


/**
 * Created by Saki on 15/8/31.
 */
public class img extends ImageView {

    private Bitmap bitmap;
    private int countpicture;
    private int ifText;

    img(Context context,Bitmap bitmap,int ifText) {
        super(context);
        this.bitmap = bitmap;
        this.ifText = ifText;
    }

    @Override
    public int hashCode() {
        return ifText;
    }

    public int getIfText(){
        return ifText;
    }
    public void setIfText(int ifText){
        this.ifText = ifText;
    }





    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

//        Log.i("X", String.valueOf(event.getX()));
//        Log.i("Y", String.valueOf(event.getY()));
//        Log.i("width", String.valueOf(bitmap.getWidth()));
//        Log.i("Height", String.valueOf(bitmap.getHeight()));

        int action = event.getAction();
        switch (action & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (bitmap.getPixel((int) event.getX(), ((int) event.getY())) == 0 && event.getX() > 0 && event.getY() > 0 && ifText== 0) {
                    //Log.i("test", "图1透明区域");
                    //Log.d("是否透明", "是");
                    return false;
                }
            default:
                break;

        }

            return super.dispatchTouchEvent(event);

    }



}


