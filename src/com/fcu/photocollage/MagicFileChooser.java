package com.fcu.photocollage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
 
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
 
@SuppressLint("NewApi")
public class MagicFileChooser {
 
	public static final int ACTIVITY_FILE_CHOOSER = 9973;
	private Activity activity;
	private boolean choosing = false;
	private boolean mustCanRead;
	private File[] chosenFiles;
 
	public MagicFileChooser(final Activity activity) {
		this.activity = activity;
	}
 
	public boolean showFileChooser() {
		return showFileChooser("*/*");
	}
 
	public boolean showFileChooser(String mimeType) {
		return showFileChooser(mimeType, null);
	}
 
	public boolean showFileChooser(String mimeType, String chooserTitle) {
		return showFileChooser(mimeType, chooserTitle, false);
	}
 
	public boolean showFileChooser(String mimeType, String chooserTitle, boolean allowMultiple) {
		return showFileChooser(mimeType, chooserTitle, allowMultiple, false);
	}
 
	public boolean showFileChooser(String mimeType, String chooserTitle, boolean allowMultiple, boolean mustCanRead) {
		if (mimeType == null || choosing) {
			return false;
		}
		choosing = true;
		// 檢查是否有可用的Activity
		final PackageManager packageManager = activity.getPackageManager();
		final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType(mimeType);
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
		if (list.size() > 0) {
			this.mustCanRead = mustCanRead;
			// 如果有可用的Activity
			Intent picker = new Intent(Intent.ACTION_GET_CONTENT);
			picker.setType(mimeType);
			picker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, allowMultiple);
			picker.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
			// 使用Intent Chooser
			Intent destIntent = Intent.createChooser(picker, chooserTitle);
			activity.startActivityForResult(destIntent, ACTIVITY_FILE_CHOOSER);
			return true;
		} else {
			return false;
		}
	}
 
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTIVITY_FILE_CHOOSER) {
			choosing = false;
			if (resultCode == Activity.RESULT_OK) {
				Uri uri = data.getData();
				if (uri != null) {
					// 單選
					chosenFiles = getFilesFromUris(activity, new Uri[] { uri }, mustCanRead);
					return true;
				} else if (Build.VERSION.SDK_INT >= 16) {
					// 複選
					ClipData clipData = data.getClipData();
					if (clipData != null) {
						int count = clipData.getItemCount();
						if (count > 0) {
							Uri[] uris = new Uri[count];
							for (int i = 0; i < count; i++) {
								uris[i] = clipData.getItemAt(i).getUri();
							}
							chosenFiles = getFilesFromUris(activity, uris, mustCanRead);
							return true;
						}
					}
				}
			}
		}
		return false;
	}
 
	public File[] getChosenFiles() {
		return chosenFiles;
	}
 
	public static String[] getAbsolutePathsFromUris(final Context context, final Uri[] uris) {
		return getAbsolutePathsFromUris(context, uris, false);
	}
 
	public static String[] getAbsolutePathsFromUris(final Context context, final Uri[] uris, final boolean mustCanRead) {
		File[] files = getFilesFromUris(context, uris, mustCanRead);
		int filesLength = files.length;
		String[] paths = new String[filesLength];
		for (int i = 0; i < filesLength; i++) {
			paths[i] = files[i].getAbsolutePath();
		}
		return paths;
	}
 
	public static File[] getFilesFromUris(final Context context, final Uri[] uris) {
		return getFilesFromUris(context, uris, false);
	}
 
	public static File[] getFilesFromUris(final Context context, final Uri[] uris, final boolean mustCanRead) {
		ArrayList<File> fileList = new ArrayList<File>();
		int urisLength = uris.length;
		for (int i = 0; i < urisLength; i++) {
			Uri uri = uris[i];
			File file = getFileFromUri(context, uri, mustCanRead);
			if (file != null) {
				fileList.add(file);
			}
		}
		File[] files = new File[fileList.size()];
		fileList.toArray(files);
		return files;
	}
 
	public static String getAbsolutePathFromUri(final Context context, final Uri uri) {
		return getAbsolutePathFromUri(context, uri, false);
	}
 
	public static String getAbsolutePathFromUri(final Context context, final Uri uri, final boolean mustCanRead) {
		File file = getFileFromUri(context, uri, mustCanRead);
		if (file != null) {
			return file.getAbsolutePath();
		} else {
			return null;
		}
	}
 
	public static File getFileFromUri(final Context context, final Uri uri) {
		return getFileFromUri(context, uri, false);
	}
 
	@SuppressLint("NewApi")
	public static File getFileFromUri(final Context context, final Uri uri, final boolean mustCanRead) {
		if (uri == null) {
			return null;
		}
		// 判斷是否為Android 4.4之後的版本
		final boolean after44 = Build.VERSION.SDK_INT >= 19;
		if (after44 && DocumentsContract.isDocumentUri(context, uri)) {
			// 如果是Android 4.4之後的版本，而且屬於文件URI
			final String authority = uri.getAuthority();
			// 判斷Authority是否為本地端檔案所使用的
			if ("com.android.externalstorage.documents".equals(authority)) {
				// 外部儲存空間
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] divide = docId.split(":");
				final String type = divide[0];
				if ("primary".equals(type)) {
					String path = Environment.getExternalStorageDirectory() + "/" + divide[1];
					return createFileObjFromPath(path, mustCanRead);
				}
			} else if ("com.android.providers.downloads.documents".equals(authority)) {
				// 下載目錄
				final String docId = DocumentsContract.getDocumentId(uri);
				final Uri downloadUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
				String path = queryAbsolutePath(context, downloadUri);
				return createFileObjFromPath(path, mustCanRead);
			} else if ("com.android.providers.media.documents".equals(authority)) {
				// 圖片、影音檔案
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] divide = docId.split(":");
				final String type = divide[0];
				Uri mediaUri = null;
				if ("image".equals(type)) {
					mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					mediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				} else {
					return null;
				}
				mediaUri = ContentUris.withAppendedId(mediaUri, Long.parseLong(divide[1]));
				String path = queryAbsolutePath(context, mediaUri);
				return createFileObjFromPath(path, mustCanRead);
			}
		} else {
			// 如果是一般的URI
			final String scheme = uri.getScheme();
			String path = null;
			if ("content".equals(scheme)) {
				// 內容URI
				path = queryAbsolutePath(context, uri);
			} else if ("file".equals(scheme)) {
				// 檔案URI
				path = uri.getPath();
			}
			return createFileObjFromPath(path, mustCanRead);
		}
		return null;
	}
 
	public static File createFileObjFromPath(final String path) {
		return createFileObjFromPath(path, false);
	}
 
	public static File createFileObjFromPath(final String path, final boolean mustCanRead) {
		if (path != null) {
			try {
				File file = new File(path);
				if (mustCanRead) {
					file.setReadable(true);
					if (!file.canRead()) {
						return null;
					}
				}
				return file.getAbsoluteFile();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return null;
	}
 
	public static String queryAbsolutePath(final Context context, final Uri uri) {
		final String[] projection = { MediaStore.MediaColumns.DATA };
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(uri, projection, null, null, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
				return cursor.getString(index);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}
}