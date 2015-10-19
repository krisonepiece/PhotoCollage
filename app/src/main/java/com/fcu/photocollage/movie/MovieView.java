package com.fcu.photocollage.movie;



import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;
import com.fcu.photocollage.R;
import com.fcu.photocollage.menu.MyFragment;


public class MovieView extends Fragment {
	private static final String TAG = "MovieView";
	private View thisView;
	private ImageButton btnRefresh;
	private ImageButton btnDownload;
	private VideoView videoMovie;
	private MyFragment myFragment;
	String urlFCU;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//super.onCreateView(inflater, container, savedInstanceState);
		setHasOptionsMenu(true);
		thisView = inflater.inflate(R.layout.movie_view, container, false);
		btnRefresh = (ImageButton)thisView.findViewById(R.id.btn_refresh);
		btnDownload = (ImageButton)thisView.findViewById(R.id.btn_download);
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
			//下載影片事件
			btnDownload.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					myFragment.downloadManager(urlFCU, "PhotoCollage", "final.mp4");
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

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		//當添加Fragment到Activity時調用
		try {
			myFragment = (MyFragment) context;
		} catch (ClassCastException e) {
			throw new ClassCastException(context.toString() + " must implementMyFragment");
		}
	}
}
