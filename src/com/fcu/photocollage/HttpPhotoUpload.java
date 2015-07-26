package com.fcu.photocollage;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
import android.net.Uri;
import android.util.Log;

public class HttpPhotoUpload{
	private String url;
	private String user;
	private String pAbPath;
	private String recAbPath;
	private String musicAbPath = "C:\\\\inetpub\\\\wwwroot\\\\PhotoCollage\\\\temp";	//音樂上傳絕對路徑
	private String musicPath = "../temp/";		//音樂上傳相對路徑
	private String picRoot = "../pictures/";	//照片上傳相對路徑
	private String recRoot = "../Record/";		//語音上傳相對路徑
	private String uploadUrl = "http://140.134.26.13/PhotoCollage/php/uploadfile.php";
	//private String uploadUrl = "http://192.168.0.100/php/uploadfile.php";
	private String commend;
	
	/*	[張數]
		[Pid][秒數][翻轉][特效][語音]
		.
		.
		[Pid][秒數][翻轉][特效][語音]
		[音樂]
	*/
	
	public HttpPhotoUpload(String url,String user) {
		this.url = url;
		this.user = user;
		//this.pPath = "D:\\\\PhotoCollage\\\\pictures\\\\" + user + "\\\\";
		//this.recPath = "D:\\\\PhotoCollage\\\\Record\\\\" + user + "\\\\";
		this.pAbPath = "C:\\\\inetpub\\\\wwwroot\\\\PhotoCollage\\\\pictures\\\\" + user;	//上傳相片路徑(不含相簿資料夾)
		this.recAbPath = "C:\\\\inetpub\\\\wwwroot\\\\PhotoCollage\\\\Record\\\\" + user;	//上傳語音完整路徑(不含檔名)
		//this.musicPath = "C:\\\\inetpub\\\\wwwroot\\\\PhotoCollage\\\\temp\\\\";
		//this.pPath = "C:\\\\xampp\\\\htdocs\\\\pictures\\\\" + user + "\\\\";
		//this.recPath = "C:\\\\xampp\\\\htdocs\\\\Record\\\\" + user + "\\\\";
		//this.musicPath = "C:\\\\xampp\\\\htdocs\\\\temp\\\\";
	}

	public String upload(ArrayList<Photo> pList, String music){	
		commend = Integer.toString( pList.size() );	//命令 [相片張數]
		
		for(int i = 0 ; i < pList.size() ; i++){
			String msg;	
			String pName = pList.get(i).getAlbumID() + "_" + getCurrentTime("yyyyMMddHHmmssSS") + ".jpg";	//上傳相片檔名
			String pPathPlus = pAbPath + "\\\\" + pList.get(i).getAlbumID();	//上傳相片完整路徑(不含檔名)
			String pFullPath = pPathPlus + "\\\\" + pName;	//上傳相片完整路徑(含檔名)
			String recName = pList.get(i).getAlbumID() + "_" + getCurrentTime("yyyyMMddHHmmssSS") + ".3gp";	//上傳語音檔名
			String recFullPath;		//上傳語音完整路徑(含檔名)
			//判斷是否有語音
			if( pList.get(i).getRecPath() != null ){
				recFullPath = recAbPath + "\\\\" + recName;
			}
			else{
				recFullPath = null;
			}
			
			try {
				// 連線到 url網址
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost method = new HttpPost(url);

				// 傳值給PHP，上傳資料到資料庫
				List<NameValuePair> vars = new ArrayList<NameValuePair>();
				vars.add(new BasicNameValuePair("Pname", pName));
				vars.add(new BasicNameValuePair("TakeDate", pList.get(i).getTakeDate() ));
				vars.add(new BasicNameValuePair("UploadDate", getCurrentTime("yyyy/MM/dd HH:mm:ss") ));
				vars.add(new BasicNameValuePair("Ppath", pFullPath));
				vars.add(new BasicNameValuePair("RecPath", recFullPath));
				vars.add(new BasicNameValuePair("AlbumID", Integer.toString( pList.get(i).getAlbumID() )));
				method.setEntity(new UrlEncodedFormEntity(vars, HTTP.UTF_8));

				// 接收PHP回傳的資料
				HttpResponse response = httpclient.execute(method);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					msg = EntityUtils.toString(entity, "utf-8");// 如果成功將網頁內容存入key
					pList.get(i).setPid( Integer.parseInt(msg) ); 
					commend += " " + msg;						//命令[Pid]
					if(pList.get(i).getRecSec() > pList.get(i).getSec())	//當語音秒數比照片秒數長，則使用語音秒數
						commend += " " + pList.get(i).getRecSec();	//命令[秒數]
					else
						commend += " " + pList.get(i).getSec();		//命令[秒數]
					commend += " " + pList.get(i).getTurn();	//命令[翻轉]
					commend += " " + pList.get(i).getEffect();	//命令[特效]
					if( pList.get(i).getRecPath() != null ){
						commend += " " + 1;	//命令[語音]
					}
					else{
						commend += " " + 0;	//命令[語音]
					}
					
					Log.i("Pid",msg);
				}
				
				//上傳照片
				String picRootPlus = picRoot + user + "/" + pList.get(i).getAlbumID() + "/" + pName;	//照片上傳相對路徑(含檔名)
				FileUpload fileUpload = new FileUpload() ;				
		    	File picFile = new File( pList.get(i).getpPath() );
				String picFileResponse = fileUpload.executeMultiPartRequest(uploadUrl, picFile, pPathPlus, picRootPlus ) ;
				Log.i("FILE - pic",picFileResponse);
				//上傳語音
				if( pList.get(i).getRecPath() != null ){
					String recRootPlus = recRoot + user + "/" + recName;	//語音上傳相對路徑(含檔名)
					File recFile = new File( pList.get(i).getRecPath() );
					String recFileResponse = fileUpload.executeMultiPartRequest(uploadUrl, recFile, recAbPath, recRootPlus ) ;
					Log.i("FILE - rec",recFileResponse);
				}
				
			} catch (Exception e) {
				return "連線失敗";
			}
		}	
		//上傳音樂
		if( music != null ){
			commend += " " + 1;	//命令[音樂]
			FileUpload fileUpload = new FileUpload() ;	
			String musRootPlus = musicPath + pList.get(0).getPid() + ".mp3";	//音樂上傳相對路徑(含檔名)
			Uri musicUri = Uri.parse(music);
			File musFile = new File( musicUri.getPath() );
			String musFileResponse = fileUpload.executeMultiPartRequest(uploadUrl, musFile, musicAbPath, musRootPlus ) ;
			Log.i("FILE - music",musFileResponse);
		}
		else{
			commend += " " + 0;	//命令[音樂]
		}
		Log.i("CMD",commend);
		try {
			// 連線到 url網址

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost method = new HttpPost("http://140.134.26.13/PhotoCollage/php/ffmpegShell.php");

			// 傳值給PHP
			List<NameValuePair> vars = new ArrayList<NameValuePair>();			
			vars.add(new BasicNameValuePair("commend", commend));
			method.setEntity(new UrlEncodedFormEntity(vars, HTTP.UTF_8));
			HttpResponse response = httpclient.execute(method);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String msg;
				msg = EntityUtils.toString(entity, "utf-8");// 如果成功將網頁內容存入key
				Log.i("FF",msg);
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
		
		
		return "沒有相片";
		
	}
	
	public String getCurrentTime(String format){
		//先行定義時間格式("yyyy/MM/dd HH:mm:ss")
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		//取得現在時間
		Date dt = new Date(System.currentTimeMillis());

		return sdf.format(dt);
	}
}
