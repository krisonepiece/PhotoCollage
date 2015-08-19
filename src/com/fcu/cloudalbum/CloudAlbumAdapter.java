package com.fcu.cloudalbum;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.fcu.R;

public class CloudAlbumAdapter extends BaseAdapter{
	private Context context;
    private ArrayList<CloudAlbumItem> list;
    private String filePath;

    public CloudAlbumAdapter(Context context, ArrayList<CloudAlbumItem> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
	    if (convertView == null) {
	        convertView = LayoutInflater.from(context).
	                inflate(R.layout.cloud_album_item, null);
	        holder = new ViewHolder();
	
	        holder.firstImageIV = (ImageView) convertView.findViewById(R.id.cloud_album_item_photo);
	        holder.pathNameTV = (TextView) convertView.findViewById(R.id.cloud_album_item_text);
	        convertView.setTag(holder);

	    } else {
	        holder = (ViewHolder) convertView.getTag();
	    }
		
	    //圖片（縮略圖）
	    if(list.get(position).getFileCount() == 0){
	    	Glide.with(context)
			.load(R.drawable.ic_add_circle_outline_white_36dp_bg)
			.centerCrop()
			.placeholder(R.drawable.empty_photo)
			.error(R.drawable.empty_photo)
			.into(holder.firstImageIV);
	    }
	    else{
	    	filePath = list.get(position).getFirstImagePath();
	    	Glide.with(context)
			.load(filePath)
			.override(100, 100)
			.centerCrop()
			.placeholder(R.drawable.empty_photo)
			.error(R.drawable.empty_photo)
			.into(holder.firstImageIV);
	    }	
	    
	    //文字
	    holder.pathNameTV.setText(getPathNameToShow(list.get(position)));
	    
        return convertView;
    }

    private class ViewHolder {
        ImageView firstImageIV;
        TextView pathNameTV;
    }

    /**根據完整路徑，獲取最後一級路徑，並拼上文件數用以顯示。*/
    private String getPathNameToShow(CloudAlbumItem item) {
        String absolutePath = item.getPathName();
        int lastSeparator = absolutePath.lastIndexOf(File.separator);
        return absolutePath.substring(lastSeparator + 1) + "(" + item.getFileCount() + ")";
    } 

}
