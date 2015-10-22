package com.fcu.photocollage.collage;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.view.View;
import android.widget.ImageView;

//基础类
public abstract class Action {
	public int color;
	public View v;
	public Bitmap drawBitmap;
	Action() {
		color = Color.BLACK;
	}

	protected Action(int color) {
		this.color = color;
	}

	public abstract void draw(Canvas canvas);

	public abstract void move(float mx, float my);

	public void view(View v) {
		// TODO 自動產生的方法 Stub
		this.v=v;
		
	}

	public void bitmap(Bitmap drawBitmap) {
		// TODO 自動產生的方法 Stub
		this.drawBitmap = drawBitmap;
		
	}
}


// 点
class MyPoint extends Action {
	public float x;
	public float y;
	int size;

	MyPoint(float px, float py,int size, int color) {
		super(color);
		this.size = size;
		this.x = px;
		this.y = py;
	}


	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setStrokeWidth(size);
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		canvas.drawPoint(x, y, paint);
		
	}

	@Override
	public void move(float mx, float my) {
		
	}
}

// 自由曲线
class MyPath extends Action {
	Path path;
	int size;

	MyPath() {
		path = new Path();
		size = 1;
	}

	MyPath(float x, float y, int size, int color) {
		super(color);
		path = new Path();
		this.size = size;
		path.moveTo(x, y);
		path.lineTo(x, y);
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(color);
		paint.setStrokeWidth(size);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		canvas.drawPath(path, paint);
	}

	public void move(float mx, float my) {
		path.lineTo(mx, my);
	}
}

class Myeraser extends Action {
	Path path;
	int size;

	Myeraser() {
		path = new Path();
		size = 1;
	}

	Myeraser(float x, float y, int size, int color) {
		super(color);
		path = new Path();
		this.size = size;
		path.moveTo(x, y);
		path.lineTo(x, y);
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setAlpha(0);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		paint.setAntiAlias(true); 
		paint.setStrokeWidth(size);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		canvas.drawPath(path, paint);
	}

	public void move(float mx, float my) {
		path.lineTo(mx, my);
	}
}

// 直线
class MyLine extends Action {
	float startX;
	float startY;
	float stopX;
	float stopY;
	int size;

	MyLine() {
		startX = 0;
		startY = 0;
		stopX = 0;
		stopY = 0;
	}

	MyLine(float x, float y, int size, int color) {
		super(color);
		startX = x;
		startY = y;
		stopX = x;
		stopY = y;
		this.size = size;
	}
	

	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(color);
		paint.setStrokeWidth(size);
		canvas.drawLine(startX, startY, stopX, stopY, paint);
		//((ImageView) v).setImageBitmap(drawBitmap);
	}

	public void move(float mx, float my) {
		stopX = mx;
		stopY = my;
	}
}

// 方框
class MyRect extends Action {
	float startX;
	float startY;
	float stopX;
	float stopY;
	int size;

	MyRect() {
		startX = 0;
		startY = 0;
		stopX = 0;
		stopY = 0;
	}

	MyRect(float x, float y, int size, int color) {
		super(color);
		startX = x;
		startY = y;
		stopX = x;
		stopY = y;
		this.size = size;
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(color);
		paint.setStrokeWidth(size);
		canvas.drawRect(startX, startY, stopX, stopY, paint);
	}

	public void move(float mx, float my) {
		stopX = mx;
		stopY = my;
	}
}

// 圆框
class MyCircle extends Action {
	float startX;
	float startY;
	float stopX;
	float stopY;
	float radius;
	int size;

	MyCircle() {
		startX = 0;
		startY = 0;
		stopX = 0;
		stopY = 0;
		radius = 0;
	}

	MyCircle(float x, float y, int size, int color) {
		super(color);
		startX = x;
		startY = y;
		stopX = x;
		stopY = y;
		radius = 0;
		this.size = size;
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(color);
		paint.setStrokeWidth(size);
		canvas.drawCircle((startX + stopX) / 2, (startY + stopY) / 2, radius,
				paint);
	}

	public void move(float mx, float my) {
		stopX = mx;
		stopY = my;
		radius = (float) ((Math.sqrt((mx - startX) * (mx - startX)
				+ (my - startY) * (my - startY))) / 2);
	}
}

// 方块
class MyFillRect extends Action {
	float startX;
	float startY;
	float stopX;
	float stopY;
	int size;

	MyFillRect() {
		startX = 0;
		startY = 0;
		stopX = 0;
		stopY = 0;
	}

	MyFillRect(float x, float y, int size, int color) {
		super(color);
		startX = x;
		startY = y;
		stopX = x;
		stopY = y;
		this.size = size;
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(color);
		paint.setStrokeWidth(size);
		canvas.drawRect(startX, startY, stopX, stopY, paint);
	}

	public void move(float mx, float my) {
		stopX = mx;
		stopY = my;
	}
}

// 圆饼
class MyFillCircle extends Action {
	float startX;
	float startY;
	float stopX;
	float stopY;
	float radius;
	int size;

	MyFillCircle() {
		startX = 0;
		startY = 0;
		stopX = 0;
		stopY = 0;
		radius = 0;
	}

	MyFillCircle(float x, float y, int size, int color) {
		super(color);
		startX = x;
		startY = y;
		stopX = x;
		stopY = y;
		radius = 0;
		this.size = size;
	}

	public void draw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(color);
		paint.setStrokeWidth(size);
		canvas.drawCircle((startX + stopX) / 2, (startY + stopY) / 2, radius,
				paint);
	}

	public void move(float mx, float my) {
		stopX = mx;
		stopY = my;
		radius = (float) ((Math.sqrt((mx - startX) * (mx - startX)
				+ (my - startY) * (my - startY))) / 2);
	}
}

//愛心
class MyLove extends Action {
	float startX;
	float startY;
	float stopX;
	float stopY;
	float radius;
	int size;

	MyLove() {
		startX = 0;
		startY = 0;
		stopX = 0;
		stopY = 0;
		radius = 0;
	}

	MyLove(float x, float y, int size, int color) {
		super(color);
		startX = x;
		startY = y;
		stopX = x;
		stopY = y;
		radius = 0;
		this.size = size;
	}

	@Override
	public void draw(Canvas mCanvas) {
		// TODO 自動產生的方法 Stub
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setColor(color);
		float r = (stopY - startY)/4 ;
		/* 心两半圆结点处 */
		float topX = startX+100;
		float topY = startY - r+100;
		/* 左上半圆 */
		RectF leftOval = new RectF(topX - 2 * r, topY - r, topX, topY + r);
				mCanvas.drawArc(leftOval, 180f, 180f, false, paint);
		/* 右上半圆 */
		RectF rightOval = new RectF(topX, topY - r, topX + 2 * r, topY + r);
				mCanvas.drawArc(rightOval, 180f, 180f, false, paint);
		/* 下半两sin曲线 */
		float base = 3 * r;
		double argu = Math.PI / 2 / base;
		float y = base, value;
		while (y >= 0) {
					value = (float) (2 * r * Math.sin(argu * (base - y)));
					mCanvas.drawPoint(topX - value, topY + y, paint);
					mCanvas.drawPoint(topX + value, topY + y, paint);
					y -= 1;
				}
		
	}

	@Override
	public void move(float mx, float my) {
		// TODO 自動產生的方法 Stub
		stopX = mx;
		stopY = my;
	}


	

}



