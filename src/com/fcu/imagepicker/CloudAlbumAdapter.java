package com.fcu.imagepicker;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.fcu.R;
import com.fcu.member.AppConfig;
import com.fcu.member.AppController;
import com.fcu.member.LoginActivity;
import com.fcu.member.SQLiteHandler;
import com.squareup.picasso.Picasso;

public class CloudAlbumAdapter extends BaseAdapter{
	private Context context;
    private ArrayList<CloudAlbumItem> list;

//	    private SDCardImageLoader loader;

    public CloudAlbumAdapter(Context context, ArrayList<CloudAlbumItem> list) {
        this.context = context;
        this.list = list;

//	        loader = new SDCardImageLoader(ScreenUtils.getScreenW(), ScreenUtils.getScreenH());
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
                    inflate(R.layout.cloud_album_item, null);
            holder = new ViewHolder();

            holder.firstImageIV = (ImageView) convertView.findViewById(R.id.cloud_album_item_photo);
            holder.pathNameTV = (TextView) convertView.findViewById(R.id.cloud_album_item_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
 		
        //圖片（縮略圖）
        String filePath = list.get(position).getFirstImagePath();
//        holder.firstImageIV.setTag(filePath);
        Glide.with(context)
		.load(filePath)
		.override(100, 100)
		.centerCrop()
		.placeholder(R.drawable.empty_photo)
		.error(R.drawable.empty_photo)
		.into(holder.firstImageIV);
//	        loader.loadImage(4, filePath, holder.firstImageIV);
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
