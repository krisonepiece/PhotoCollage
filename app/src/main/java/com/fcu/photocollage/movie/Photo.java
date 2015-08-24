package com.fcu.photocollage.movie;

import com.fcu.photocollage.library.DateTool;

public class Photo {
	private int pid;			//相片編號
	private String pname;		//相片名稱
	private String takeDate;	//拍攝日期
	private String uploadDate;	//上傳日期
	private String pPath;		//相片路徑
	private String pServerPath;	//相片Server路徑
	private String recPath;		//語音路徑
	private String recServerPath;//語音Server路徑
	private int recSec;			//語音秒數
	private int albumID;		//相簿編號
	private int UserID;		//相簿編號
	private int sec;			//秒數
	private int turn;			//翻轉 ( 1 為翻轉, 0 為不翻 )
	private int effect;			//特效 ( 1 為加特效, 0 為不加 )
	
	public Photo(int pid, String pPath, String recPath, int albumID,
			 int sec, int turn, int effect) {
		this.pid = pid;
		this.pPath = pPath;
		this.recPath = recPath;
		this.recSec = 0;
		this.albumID = albumID;
		this.sec = sec;
		this.turn = turn;
		this.effect = effect;
	}
	public Photo(String pPath, int albumID, int userID) {
		this.pPath = pPath;		
		this.albumID = albumID;
		this.UserID = userID;
	}
	public Photo(String pPath, String takeDate, int albumID, int userID) {
		this.pPath = pPath;		
		this.takeDate = takeDate;
		this.albumID = albumID;
		this.UserID = userID;
		this.pServerPath = "C:\\\\inetpub\\\\wwwroot\\\\PhotoCollage\\\\Data\\\\" + userID + "\\\\picture\\\\" + albumID + "\\\\";
	}
	
	public Photo(int pid, String pname, String takeDate, String uploadDate, String pPath, String recPath) {
		this.pid = pid;
		this.pname = pname;
		this.takeDate = takeDate;
		this.uploadDate = uploadDate;
		this.pPath = pPath;
		this.recPath = recPath;
	}
	
	public void createPname() {
		pname = UserID + "_" + albumID + "_" + DateTool.getCurrentTime("yyyyMMddHHmmssSS") + ".jpg";
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getTakeDate() {
		return takeDate;
	}

	public void setTakeDate(String takeDate) {
		this.takeDate = takeDate;
	}

	public String getpPath() {
		return pPath;
	}

	public void setpPath(String pPath) {
		this.pPath = pPath;
	}

	public String getRecPath() {
		return recPath;
	}

	public void setRecPath(String recPath) {
		this.recPath = recPath;
	}

	public int getAlbumID() {
		return albumID;
	}

	public void setAlbumID(int albumID) {
		this.albumID = albumID;
	}

	public int getSec() {
		return sec;
	}

	public void setSec(int sec) {
		this.sec = sec;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public int getEffect() {
		return effect;
	}

	public void setEffect(int effect) {
		this.effect = effect;
	}

	public int getRecSec() {
		return recSec;
	}

	public void setRecSec(int recSec) {
		this.recSec = recSec;
	}
	
	public int getUserID() {
		return UserID;
	}
	
	public void setUserID(int userID) {
		UserID = userID;
	}
	
	public String getpServerPath() {
		return pServerPath;
	}
	
	public void setpServerPath(String pServerPath) {
		this.pServerPath = pServerPath;
	}
	
	public String getRecServerPath() {
		return recServerPath;
	}
	
	public void setRecServerPath(String recServerPath) {
		this.recServerPath = recServerPath;
	}
/*
	@Override
	public String toString() {
		return "Photo [pid=" + pid + ", pname=" + pname + ", takeDate="
				+ takeDate + ", pPath=" + pPath + ", recPath=" + recPath
				+ ", albumID=" + albumID + ", userID=" + userID + ", sec="
				+ sec + ", turn=" + turn + ", effect=" + effect + "]";
	}
*/
}