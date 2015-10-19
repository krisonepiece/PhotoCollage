package com.fcu.photocollage.cloudalbum;

public class CloudAlbumItem {
	private int albumId;
	private String pathName;
	private int pcode;
	private int fileCount;
	private String firstImagePath;
	private String createDate;
	private String userId;



	public CloudAlbumItem(int albumId, String pathName, int pcode, int fileCount,
			String firstImagePath, String userId, String createDate) {
		this.albumId = albumId;
		this.pathName = pathName;
		this.pcode = pcode;
		this.fileCount = fileCount;
		this.firstImagePath = firstImagePath;
		this.userId = userId;
		this.createDate = createDate;
	}

	public int getAlbumId() {
		return albumId;
	}

	public void setAlbumId(int albumId) {
		this.albumId = albumId;
	}
	
	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	public int getFileCount() {
		return fileCount;
	}

	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}

	public String getFirstImagePath() {
		return firstImagePath;
	}

	public void setFirstImagePath(String firstImagePath) {
		this.firstImagePath = firstImagePath;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getPcode() {
		return pcode;
	}

	public void setPcode(int pcode) {
		this.pcode = pcode;
	}

	@Override
	public String toString() {
		return "SelectImgGVItem{" + "pathName='" + pathName + '\''
				+ ", fileCount=" + fileCount + ", firstImagePath='"
				+ firstImagePath + '\'' + '}';
	}
}
