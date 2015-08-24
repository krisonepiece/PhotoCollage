package com.fcu.photocollage.menu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fcu.photocollage.R;

/**
 * Created by krisonepiece on 2015/8/24.
 */
public class MenuFragment extends Fragment {

    private View thisView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_menu, container, false);
        return thisView;
    }

}
