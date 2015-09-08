package com.fcu.photocollage.movie;



import android.app.FragmentTransaction;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;
import com.fcu.photocollage.R;


public class MovieView extends Fragment {
	private static final String TAG = "MovieView";
	private View thisView;
	private Button btnFinish;
	private ImageButton btnRefresh;
	private VideoView videoMovie;
	String urlFCU;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);
		thisView = inflater.inflate(R.layout.movie_view, container, false);
		btnRefresh = (ImageButton)thisView.findViewById(R.id.btn_refresh);
		videoMovie = (VideoView)thisView.findViewById(R.id.video_movie);

		int uid = getArguments().getInt("uid", -1);
		if( uid != -1) {
			urlFCU = "http://140.134.26.13/PhotoCollage/Data/" + uid + "/Video/final.mp4";
			videoPlay(Uri.parse(urlFCU));

			//重新整理事件
			btnRefresh.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					videoPlay(Uri.parse(urlFCU));
				}
			});
		}
		return thisView;
	}

	public void videoPlay(Uri uri){
		videoMovie.setVideoURI(uri);
		videoMovie.setMediaController(new MediaController(getActivity()));
		videoMovie.requestFocus();
		videoMovie.start();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
			case android.R.id.home:
				backAction();
				return true;
		}
		return false;
	}
	private void backAction() {
		Fragment photoMovieFg = new PhotoMovieMain();
		FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.content_frame, photoMovieFg)
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack(null).commit();
	}
}
