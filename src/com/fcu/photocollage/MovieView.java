package com.fcu.photocollage;

import com.fcu.R;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

public class MovieView extends Activity {
	private Button btnFinish;
	private ImageButton btnRefresh;
	private VideoView videoMovie;
	String urlFCU = "http://140.134.26.13/PhotoCollage/final/final.mp4";
	String urlHOME = "http://192.168.0.100/pictures/Kris/movie_tmp/out.mp4";
	String src = "http://192.168.0.100/pictures/Kris/movie_tmp/out.mp4";
	
	private void initializeVariables() {
		btnFinish = (Button)findViewById(R.id.btn_fin);
		btnRefresh = (ImageButton)findViewById(R.id.btn_refresh);
		videoMovie = (VideoView)findViewById(R.id.video_movie);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movie_view);
		
		initializeVariables();	
		
		videoPlay(Uri.parse(urlFCU));
		
		//重新整理事件
		btnRefresh.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				videoPlay(Uri.parse(urlFCU));		
			}
		});
		//完成事件
		btnFinish.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				MovieView.this.finish();			
			}
		});
	}

	public void videoPlay(Uri uri){
		videoMovie.setVideoURI(Uri.parse(urlFCU));
		videoMovie.setMediaController(new MediaController(this));
		videoMovie.requestFocus();
		videoMovie.start();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
