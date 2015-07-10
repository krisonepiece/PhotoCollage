package com.fcu.photocollage;

import java.io.File;
import java.util.Date;

public class Photo {
	private int pid;			//相片編號
	private String pname;		//相片名稱
	private String takeDate;	//拍攝日期
	private String pPath;		//相片路徑
	private String recPath;		//語音路徑
	private int albumID;		//相簿編號
	private int sec;			//秒數
	private int turn;			//翻轉 ( 1 為翻轉, 0 為不翻 )
	private int effect;			//特效 ( 1 為加特效, 0 為不加 )
	
	public Photo(String pPath, String recPath, int albumID,
			 int sec, int turn, int effect) {
		this.pPath = pPath;
		this.recPath = recPath;
		this.albumID = albumID;
		this.sec = sec;
		this.turn = turn;
		this.effect = effect;
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