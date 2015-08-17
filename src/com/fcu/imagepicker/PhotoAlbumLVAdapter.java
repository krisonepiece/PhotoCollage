package com.fcu.imagepicker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.fcu.R;
import com.squareup.picasso.Picasso;

/**
 * 選擇相冊頁面,ListView的adapter
 */
public class PhotoAlbumLVAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<PhotoAlbumLVItem> list;

//    private SDCardImageLoader loader;

    public PhotoAlbumLVAdapter(Context context, ArrayList<PhotoAlbumLVItem> list) {
        this.context = context;
        this.list = list;

//        loader = new SDCardImageLoader(ScreenUtils.getScreenW(), ScreenUtils.getScreenH());
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.photo_album_lv_item, null);
            holder = new ViewHolder();

            holder.firstImageIV = (ImageView) convertView.findViewById(R.id.select_img_gridView_img);
            holder.pathNameTV = (TextView) convertView.findViewById(R.id.select_img_gridView_path);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //圖片（縮略圖）
        String filePath = list.get(position).getFirstImagePath();
        //holder.firstImageIV.setTag(filePath);
//        Picasso.with(context)
//        .load(new File(filePath))
//        .resize(100, 100)
//        .centerCrop()
//        .placeholder(R.drawable.empty_photo)
//        .error(R.drawable.empty_photo)
//        .into(holder.firstImageIV);
		Glide.with(context)
		.load(new File(filePath))
		.override(100, 100)
		.centerCrop()
		.placeholder(R.drawable.empty_photo)
		.error(R.drawable.empty_photo)
		.into(holder.firstImageIV);
//        loader.loadImage(4, filePath, holder.firstImageIV);
        //文字
        holder.pathNameTV.setText(getPathNameToShow(list.get(position)));

        return convertView;
    }

    private class ViewHolder {
        ImageView firstImageIV;
        TextView pathNameTV;
    }

    /**根據完整路徑，獲取最後一級路徑，並拼上文件數用以顯示。*/
    private String getPathNameToShow(PhotoAlbumLVItem item) {
        String absolutePath = item.getPathName();
        int lastSeparator = absolutePath.lastIndexOf(File.separator);
        return absolutePath.substring(lastSeparator + 1) + "(" + item.getFileCount() + ")";
    }

}
