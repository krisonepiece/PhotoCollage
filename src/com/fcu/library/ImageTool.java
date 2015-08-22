package com.fcu.library;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;

public class ImageTool {
	/**
	 * 不失真壓縮圖片
	 * 
	 * @param path
	 * @param h
	 * @param w
	 * @return
	 */
	public static Bitmap ScalePicEx(String path, int height, int width) {

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
	public static boolean compressAndCreatePhoto(String path, Bitmap bmp,
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
}
