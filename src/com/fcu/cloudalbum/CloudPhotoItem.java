package com.fcu.cloudalbum;

public class CloudPhotoItem {
	private int id;
	private String name;
	private String takeDate;
	private String uploadDate;
	private String pPath;
	private String recPath;

	public CloudPhotoItem(int id, String name, String takeDate,
			String uploadDate, String pPath, String recPath) {
		super();
		this.id = id;
		this.name = name;
		this.takeDate = takeDate;
		this.uploadDate = uploadDate;
		this.pPath = pPath;
		this.recPath = recPath;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTakeDate() {
		return takeDate;
	}

	public void setTakeDate(String takeDate) {
		this.takeDate = takeDate;
	}

	public String getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
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

	@Override
	public String toString() {
		return "SelectImgItem{" + "id=" + id 
				+ ", name=" + name 
				+ ", takeDate="+ takeDate  
				+ ", uploadDate="+ uploadDate
				+ ", pPath="+ pPath
				+ ", recPath="+ recPath
				+ '}';
	}
}
