/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fcu.imagepicker;

import java.util.ArrayList;

import com.fcu.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class AlbumMainActivity extends AppCompatActivity {

	static final String LOG_TAG = "SlidingTabsBasicFragment";
	private SlidingTabLayout mSlidingTabLayout;
	private ViewPager mViewPager;
	private Fragment phoneAlbumFg;
	private Fragment phoneFg;
	private Fragment cloudFg;
	private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
	private MyAdapter myAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_main);
		//設置 Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        } 
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
		//phoneAlbumFg = new PhotoAlbumActivity();
		phoneFg = new PhoneFragment();
		cloudFg = new CloudAlbumActivity();
		fragmentList.add(phoneFg);
		fragmentList.add(cloudFg);
		myAdapter = new MyAdapter(this.getSupportFragmentManager(),
				fragmentList);

		mViewPager.setAdapter(myAdapter);
		mViewPager.setCurrentItem(0);
		mSlidingTabLayout.setViewPager(mViewPager);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.album_menu, menu);
        return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_check) {
//			
//			return true;
//		}
		return super.onOptionsItemSelected(item);
	}
	
//    @Override
//    protected void onNewIntent(Intent intent) {     
//        super.onNewIntent(intent);
//        setIntent(intent);
//        getIntent().putExtras(intent);
//    } 
    
//    //重寫返回鍵
//    @Override
//    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//        	
//        	FragmentManager fragmentManager = this.getSupportFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.viewpager, phoneAlbumFg).commit();
//            return true;
//        } else {
//            return super.onKeyDown(keyCode, event);
//        }
//    }


	public class MyAdapter extends FragmentPagerAdapter {

		private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();

		public MyAdapter(FragmentManager fm, ArrayList<Fragment> fList) {
			super(fm);
			this.fragmentList = fList;
		}

		@Override
		public Fragment getItem(int position) {

			return fragmentList.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return fragmentList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return "手機相簿";
			case 1:
				return "雲端相簿";
			default:
				return null;
			}
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;//返回这个表示该对象已改变,需要刷新
			//return POSITION_UNCHANGED;//反之不刷新
		}
	}
}
