package com.fcu.photocollage.cloudalbum;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fcu.photocollage.R;
import com.fcu.photocollage.library.DateTool;
import com.fcu.photocollage.movie.Photo;

import java.util.ArrayList;

/**
 * PhotoWall中GridView的適配器
 */

public class CloudPhotoAdapter extends BaseAdapter {
    final private String TAG = "CloudPhotoAdapter";
    private Context context;
    private ArrayList<Photo> imagePathList = null;
    public Menu menu;
    private String pcode;
    private MenuItem miDelete;
    private MenuItem miEdit;
    private MenuItem miDownload;

    //記錄是否被選擇
    private SparseBooleanArray selectionMap;

    public CloudPhotoAdapter(Context context, ArrayList<Photo> imagePathList, Menu menu, String pcode) {
        this.context = context;
        this.imagePathList = imagePathList;
        this.menu = menu;
        this.pcode = pcode;
        miDelete = menu.findItem(R.id.action_delete);
        miEdit = menu.findItem(R.id.action_edit);
        miDownload = menu.findItem(R.id.action_download);
        selectionMap = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return imagePathList == null ? 0 : imagePathList.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePathList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String filePath = imagePathList.get(position).getpPath();
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cloud_photo_item, null);
            holder = new ViewHolder();

            holder.imageView = (ImageView) convertView.findViewById(R.id.cloud_photo_item_photo);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.cloud_photo_item_cb);
            holder.textView = (TextView) convertView.findViewById(R.id.cloud_photo_item_text);
            convertView.setTag(holder);            
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //tag的key必須使用id的方式定義以保證唯一，否則會出現IllegalArgumentException.
        holder.checkBox.setTag(R.id.tag_first, position);
        holder.checkBox.setTag(R.id.tag_second, holder.imageView);
    
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Integer position = (Integer) buttonView.getTag(R.id.tag_first);
                ImageView image = (ImageView) buttonView.getTag(R.id.tag_second);


                if (isChecked) {
                    selectionMap.put(position, isChecked);
                    Log.d(TAG, "isChecked selectionMap: " + selectionMap.size());
                    if(selectionMap.size() == 1){
                        showMenuItem();
                    }
                    image.setColorFilter(context.getResources().getColor(R.color.image_checked_bg));
                } else {
                    selectionMap.delete(position);
                    Log.d(TAG, "selectionMap: " + selectionMap.size());
                    if(selectionMap.size() == 0){
                        hideMenuItem();
                    }
                    image.setColorFilter(null);
                }

            }
        });

        holder.checkBox.setChecked(selectionMap.get(position));

        Glide.with(context)
	    .load(filePath)
	    .override(150, 150)
	    .centerCrop()
	    .placeholder(R.mipmap.empty_photo)
	    .error(R.mipmap.empty_photo)
	    .into(holder.imageView);
        //holder.imageView.setTag(filePath);
        //取得拍攝日期
        holder.textView.setText( DateTool.dateFormat(imagePathList.get(position).getTakeDate(), "M月d日") );

//	        loader.loadImage(4, filePath, holder.imageView);
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
        CheckBox checkBox;
        TextView textView;
    }

    public SparseBooleanArray getSelectionMap() {
        return selectionMap;
    }

    public void clearSelectionMap() {
        selectionMap.clear();
    }

    public void showMenuItem(){
        if(pcode.contains("3"))
            miDelete.setVisible(true);
        if(pcode.contains("4"))
            miEdit.setVisible(true);
        if(pcode.contains("5"))
            miDownload.setVisible(true);
    }

    public void hideMenuItem(){
        miDelete.setVisible(false);
        miEdit.setVisible(false);
        miDownload.setVisible(false);
    }
}

