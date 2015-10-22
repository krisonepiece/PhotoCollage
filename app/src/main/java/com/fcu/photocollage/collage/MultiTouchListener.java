package com.fcu.photocollage.collage;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fcu.photocollage.R;

public class MultiTouchListener extends Activity implements OnTouchListener {

    private static final int INVALID_POINTER_ID = -1;
    public boolean isRotateEnabled = true;
    public boolean isTranslateEnabled = true;
    public boolean isScaleEnabled = true;
    public float minimumScale = 0.1f;
    public float maximumScale = 10.0f;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mPrevX;
    private float mPrevY;
    private ScaleGestureDetector mScaleGestureDetector;
    private  long Down;
    private  long touchTime = 2000;




    public MultiTouchListener() {
        mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());;
    }

    private static float adjustAngle(float degrees) {
        if (degrees > 180.0f) {
            degrees -= 360.0f;
        } else if (degrees < -180.0f) {
            degrees += 360.0f;
        }

        return degrees;
    }

    private static void move(View view, TransformInfo info) {
        computeRenderOffset(view, info.pivotX, info.pivotY);
        adjustTranslation(view, info.deltaX, info.deltaY);

        // Assume that scaling still maintains aspect ratio.
        float scale = ((View) view).getScaleX() * info.deltaScale;
        scale = Math.max(info.minimumScale, Math.min(info.maximumScale, scale));
        view.setScaleX(scale);
        view.setScaleY(scale);

        float rotation = adjustAngle(view.getRotation() + info.deltaAngle);
        view.setRotation(rotation);
    }

    private static void adjustTranslation(View view, float deltaX, float deltaY) {
        float[] deltaVector = {deltaX, deltaY};
        view.getMatrix().mapVectors(deltaVector);
        view.setTranslationX(view.getTranslationX() + deltaVector[0]);
        view.setTranslationY(view.getTranslationY() + deltaVector[1]);
    }

    private static void computeRenderOffset(View view, float pivotX, float pivotY) {
        if (view.getPivotX() == pivotX && view.getPivotY() == pivotY) {
            return;
        }

        float[] prevPoint = {0.0f, 0.0f};
        view.getMatrix().mapPoints(prevPoint);

        view.setPivotX(pivotX);
        view.setPivotY(pivotY);

        float[] currPoint = {0.0f, 0.0f};
        view.getMatrix().mapPoints(currPoint);

        float offsetX = currPoint[0] - prevPoint[0];
        float offsetY = currPoint[1] - prevPoint[1];

        
        view.setTranslationX(view.getTranslationX() - offsetX);
        view.setTranslationY(view.getTranslationY() - offsetY);
    }


    
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(view, event);




        if (!isTranslateEnabled) {
            return true;
        }



            int action = event.getAction();
            switch (action & event.getActionMasked()) {
                // MotionEvent.ACTION_DOWN: 手指压下屏幕
                case MotionEvent.ACTION_DOWN: {
                    mPrevX = event.getX();
                    mPrevY = event.getY();
                    // Save the ID of this pointer.
                    mActivePointerId = event.getPointerId(0);

                    Down = event.getDownTime();

                    break;

                }
                //MotionEvent.ACTION_MOVE: 手指在屏幕移动，该事件会不断地触发
                case MotionEvent.ACTION_MOVE: {
                    // Find the index of the active pointer and fetch its position.
                    int pointerIndex = event.findPointerIndex(mActivePointerId);
                    if (pointerIndex != -1) {
                        float currX = event.getX(pointerIndex);
                        float currY = event.getY(pointerIndex);
                        // Only move if the ScaleGestureDetector isn't processing a
                        // gesture.
                        if (!mScaleGestureDetector.isInProgress()) {
                            adjustTranslation(view, currX - mPrevX, currY - mPrevY);
                        }

                    }
                    isLongPressed(mPrevX, mPrevY, event.getX(), event.getY(), Down, event.getEventTime(), touchTime);
                    break;


                }

                case MotionEvent.ACTION_CANCEL:
                    mActivePointerId = INVALID_POINTER_ID;
                    break;
                //MotionEvent.ACTION_UP: 手指离开屏
                case MotionEvent.ACTION_UP:
                    mActivePointerId = INVALID_POINTER_ID;
                    //Log.d("平均",String.valueOf(event.getDownTime()-event.getEventTime()));

                    //touchTime = event.getDownTime()-event.getEventTime();


                /*if(event.getEventTime()-event.getDownTime() < 50)
                {
                	ImageTouchMove(view);
                }*/
                    break;
                //MotionEvent.ACTION_POINTER_UP: 有手指离开屏幕,但屏幕还有触点（手指）
                case MotionEvent.ACTION_POINTER_UP: {
                    // Extract the index of the pointer that left the touch sensor.
                    int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                    int pointerId = event.getPointerId(pointerIndex);
                    if (pointerId == mActivePointerId) {
                        // This was our active pointer going up. Choose a new
                        // active pointer and adjust accordingly.
                        int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        mPrevX = event.getX(newPointerIndex);
                        mPrevY = event.getY(newPointerIndex);
                        mActivePointerId = event.getPointerId(newPointerIndex);

                    }


                    break;
                }

            }

            return false;
       // }
    }

    public class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        private float mPivotX;
        private float mPivotY;
        private Vector2D mPrevSpanVector = new Vector2D();

        @Override
        public boolean onScaleBegin(View view, ScaleGestureDetector detector) {
            mPivotX = detector.getFocusX();
            mPivotY = detector.getFocusY();
            mPrevSpanVector.set(detector.getCurrentSpanVector());
            return true;
        }

        @Override
        public boolean onScale(View view, ScaleGestureDetector detector) {
            TransformInfo info = new TransformInfo();
            info.deltaScale = isScaleEnabled ? detector.getScaleFactor() : 1.0f;
            info.deltaAngle = isRotateEnabled ? Vector2D.getAngle(mPrevSpanVector, detector.getCurrentSpanVector()) : 0.0f;
            info.deltaX = isTranslateEnabled ? detector.getFocusX() - mPivotX : 0.0f;
            info.deltaY = isTranslateEnabled ? detector.getFocusY() - mPivotY : 0.0f;
            info.pivotX = mPivotX;
            info.pivotY = mPivotY;
            info.minimumScale = minimumScale;
            info.maximumScale = maximumScale;

            move(view, info);
            return false;
        }
    }
    
    private class TransformInfo {

        public float deltaX;
        public float deltaY;
        public float deltaScale;
        public float deltaAngle;
        public float pivotX;
        public float pivotY;
        public float minimumScale;
        public float maximumScale;
    } 
    private boolean isLongPressed(float lastX,float lastY, float thisX,float thisY, long lastDownTime,long thisEventTime, long longPressTime)
    { 
		float offsetX = Math.abs(thisX - lastX); 
		float offsetY = Math.abs(thisY - lastY); 
		long intervalTime = thisEventTime - lastDownTime; 
		if(offsetX <=10 && offsetY<=10 && intervalTime >= longPressTime){
            Log.d("long","long");
			return true; 
		} 
		return false; 
	}

   


}
