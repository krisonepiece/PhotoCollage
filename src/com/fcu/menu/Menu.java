package com.fcu.menu;

import com.fcu.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Menu extends Fragment{
	
	private View thisView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		thisView = inflater.inflate(R.layout.fragment_menu, container, false);
		return thisView;
	}

}
