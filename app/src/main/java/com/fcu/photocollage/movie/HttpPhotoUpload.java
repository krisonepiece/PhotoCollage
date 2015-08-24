package com.fcu.photocollage.movie;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class HttpPhotoUpload implements Runnable {
	private String uploadDbUrl = "http://140.134.26.13/PhotoCollage/php/UploadPhoto.php";
	private String uploadFileUrl = "http://140.134.26.13/PhotoCollage/php/uploadfile.php";
	private String ffmpegShellUrl = "http://140.134.26.13/PhotoCollage/php/ffmpegShell.php";
	private String user;
	private String pAbPath;
	private String recAbPath;
	private String musicAbPath = "C:\\\\inetpub\\\\wwwroot\\\\PhotoCollage\\\\temp"; // 音樂上傳絕對路徑
	private String musicPath = "../temp/"; // 音樂上傳相對路徑
	private String picRoot = "../pictures/"; // 照片上傳相對路徑
	private String recRoot = "../Record/"; // 語音上傳相對路徑
	private ArrayList<Photo> pList;
	private String music;
	private String commend;
	private String pid;
	private String pName; // 上傳相片檔名
	private String pPathPlus; // 上傳相片完整路徑(不含檔名)
	private String pFullPath; // 上傳相片完整路徑(含檔名)
	private String recName; // 上傳語音檔名
	private String recFullPath; // 上傳語音完整路徑(含檔名)
	private Handler handler;

	/*
	 * [張數] [Pid][秒數][翻轉][特效][語音] . . [Pid][秒數][翻轉][特效][語音] [音樂]
	 */
	public HttpPhotoUpload(Handler handler, String user) {
		this.handler = handler;
		this.user = user;
		this.pAbPath = "C:\\\\inetpub\\\\wwwroot\\\\PhotoCollage\\\\pictures\\\\"
				+ user; // 上傳相片路徑(不含相簿資料夾)
		this.recAbPath = "C:\\\\inetpub\\\\wwwroot\\\\PhotoCollage\\\\Record\\\\"
				+ user; // 上傳語音完整路徑(不含檔名)

	}

	@Override
	public void run() {				
		// 命令 [相片張數]
		commend = Integer.toString(pList.size());

		for (int i = 0; i < pList.size(); i++) {
			pName = pList.get(i).getAlbumID() + "_"
					+ getCurrentTime("yyyyMMddHHmmssSS") + ".jpg"; // 上傳相片檔名
			pPathPlus = pAbPath + "\\\\" + pList.get(i).getAlbumID(); // 上傳相片完整路徑(不含檔名)
			pFullPath = pPathPlus + "\\\\" + pName; // 上傳相片完整路徑(含檔名)
			recName = pList.get(i).getAlbumID() + "_"
					+ getCurrentTime("yyyyMMddHHmmssSS") + ".3gp"; // 上傳語音檔名
			
			//上傳照片資訊到資料庫
			pid = uploadPhotoToDb(pList.get(i));			
			
			// 命令[Pid]
			commend += " " + pid; 
			// 命令[秒數], 當語音秒數比照片秒數長，則使用語音秒數
			if (pList.get(i).getRecSec() > pList.get(i).getSec())
				commend += " " + pList.get(i).getRecSec();
			else
				commend += " " + pList.get(i).getSec();			
			// 命令[翻轉]
			commend += " " + pList.get(i).getTurn(); 
			// 命令[特效]
			commend += " " + pList.get(i).getEffect(); 
			// 命令[語音]
			if (pList.get(i).getRecPath() != null)
				commend += " " + 1;
			else
				commend += " " + 0;			

			updateHandleMessage("value","上傳照片...("+ i + "/" + pList.size() + ")");
			//上傳照片檔案到 Server
			uploadPhotoToServer(pList.get(i));
		}
		// 上傳音樂
		if (music != null) {
			updateHandleMessage("value","上傳音樂...");	        
			uploadMusicToServer(music);
			commend += " " + 1; // 命令[音樂]			
		} else {
			commend += " " + 0; // 命令[音樂]
		}			
		Log.i("CMD", commend);
		//輸出影片
		updateHandleMessage("value","產生電影...");
		ffmpegShell(commend);
		
		updateHandleMessage("value","完成");
  
	}
	protected void updateHandleMessage(String key, String value) {
		Message msg = new Message();;
        Bundle data = new Bundle();
		data.putString(key,value);        
        msg.setData(data);
        handler.sendMessage(msg);
	}
	protected void setUploadFile(ArrayList<Photo> pList, String music) {
		this.pList = pList;
		this.music = music;
	}

	protected String uploadPhotoToDb(Photo photo) {
		// 判斷是否有語音
		if (photo.getRecPath() != null) {
			recFullPath = recAbPath + "\\\\" + recName;
		} else {
			recFullPath = null;
		}

		try {
			// 連線到 uploadDbUrl網址
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost method = new HttpPost(uploadDbUrl);

			// 傳值給PHP，上傳資料到資料庫
			List<NameValuePair> vars = new ArrayList<NameValuePair>();
			vars.add(new BasicNameValuePair("Pname", pName));
			vars.add(new BasicNameValuePair("TakeDate", photo.getTakeDate()));
			vars.add(new BasicNameValuePair("UploadDate",
					getCurrentTime("yyyy/MM/dd HH:mm:ss")));
			vars.add(new BasicNameValuePair("Ppath", pFullPath));
			vars.add(new BasicNameValuePair("RecPath", recFullPath));
			vars.add(new BasicNameValuePair("AlbumID", Integer.toString(photo
					.getAlbumID())));
			method.setEntity(new UrlEncodedFormEntity(vars, HTTP.UTF_8));

			// 接收PHP回傳的資料
			HttpResponse response = httpclient.execute(method);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				pid = EntityUtils.toString(entity, "utf-8");// 如果成功將網頁內容存入key
				photo.setPid(Integer.parseInt(pid));
				Log.i("Pid", pid);
				return pid;
			}
		} catch (ClientProtocolException e) {
			Log.i("HttpClient", "ClientProtocol failed");
		} catch (IOException e) {
			Log.i("HttpClient", "IO failed");
		} catch (NetworkOnMainThreadException e) {
			Log.i("HttpClient", "NetworkOnMainThread failed");
		}
		return null;
	}

	protected void uploadPhotoToServer(Photo photo) {
		// 上傳照片
		String picRootPlus = picRoot + user + "/" + photo.getAlbumID() + "/"
				+ pName; // 照片上傳相對路徑(含檔名)
		FileUpload fileUpload = new FileUpload();
		File picFile = new File(photo.getpPath());
		String picFileResponse = fileUpload.executeMultiPartRequest(uploadFileUrl,
				picFile, pPathPlus, picRootPlus);
		Log.i("FILE - pic", picFileResponse);
		// 上傳語音
		if (photo.getRecPath() != null) {
			String recRootPlus = recRoot + user + "/" + recName; // 語音上傳相對路徑(含檔名)
			File recFile = new File(photo.getRecPath());
			String recFileResponse = fileUpload.executeMultiPartRequest(
					uploadFileUrl, recFile, recAbPath, recRootPlus);
			Log.i("FILE - rec", recFileResponse);
		}
	}

	protected void uploadMusicToServer(String music) {
		// 上傳音樂
		FileUpload fileUpload = new FileUpload();
		String musRootPlus = musicPath + pList.get(0).getPid() + ".mp3"; // 音樂上傳相對路徑(含檔名)
		Uri musicUri = Uri.parse(music);
		File musFile = new File(musicUri.getPath());
		String musFileResponse = fileUpload.executeMultiPartRequest(
				uploadFileUrl, musFile, musicAbPath, musRootPlus);
		Log.i("FILE - music", musFileResponse);
	}

	protected void ffmpegShell(String commend) {
		try {
			// 連線到 url網址
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost method = new HttpPost(ffmpegShellUrl);
			// 傳值給PHP
			List<NameValuePair> vars = new ArrayList<NameValuePair>();
			vars.add(new BasicNameValuePair("commend", commend));
			method.setEntity(new UrlEncodedFormEntity(vars, HTTP.UTF_8));
			HttpResponse response = httpclient.execute(method);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String msg;
				msg = EntityUtils.toString(entity, "utf-8");// 如果成功將網頁內容存入key
				Log.i("FF", msg);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public String getCurrentTime(String format) {
		// 先行定義時間格式("yyyy/MM/dd HH:mm:ss")
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		// 取得現在時間
		Date dt = new Date(System.currentTimeMillis());

		return sdf.format(dt);
	}

}
