package com.fcu.photocollage.menu;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.CBPageAdapter;
import com.bigkoo.convenientbanner.CBViewHolderCreator;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.fcu.photocollage.R;
import com.fcu.photocollage.member.LoginFragment;
import com.lid.lib.LabelView;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by krisonepiece on 2015/8/24.
 */
public class MenuFragment extends Fragment{

    private ConvenientBanner convenientBanner;
    private ImageButton btnMovie;
    private ImageButton btnAlbum;
    private ImageButton btnMember;
    private ImageButton btnCollage;
    private ArrayList<Integer> localImages = new ArrayList<Integer>();
    private View thisView;
    private MyFragment myFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_menu, container, false);

        initViews();
        init();
        initListener();

        return thisView;
    }
    private void initViews() {
        convenientBanner = (ConvenientBanner) thisView.findViewById(R.id.convenientBanner);
        btnMovie = (ImageButton) thisView.findViewById(R.id.menu_btn_movie);
        btnAlbum = (ImageButton) thisView.findViewById(R.id.menu_btn_album);
        btnMember = (ImageButton) thisView.findViewById(R.id.menu_btn_member);
        btnCollage = (ImageButton) thisView.findViewById(R.id.menu_btn_collage);
        LabelView label = new LabelView(getActivity());
        label.setText("NEW");
        label.setBackgroundColor(0xff03a9f4);
        label.setTargetView(thisView.findViewById(R.id.menu_btn_movie), 10, LabelView.Gravity.LEFT_TOP);
    }
    private void initListener() {
        btnMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFragment.switchMenu(R.id.drawer_movie);
            }
        });
        btnAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFragment.switchMenu(R.id.drawer_album);
            }
        });
        btnMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFragment.switchMenu(R.id.drawer_member);
            }
        });
        btnCollage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myFragment.switchMenu(R.id.drawer_collage);
            }
        });
    }
    private void init(){
        loadTestDatas();
        //本地图片例子
        convenientBanner.setPages(
                new CBViewHolderCreator<LocalImageHolderView>() {
                    @Override
                    public LocalImageHolderView createHolder() {
                        return new LocalImageHolderView();
                    }
                }, localImages)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.mipmap.ic_page_indicator, R.mipmap.ic_page_indicator_focused})
                        //设置指示器的方向
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                        //设置翻页的效果，不需要翻页效果可用不设
                .setPageTransformer(ConvenientBanner.Transformer.DefaultTransformer);
    }
    private void loadTestDatas() {
        //本地圖片集合
        for (int position = 0; position < 7; position++)
            localImages.add(getResId("ic_test_" + position, R.mipmap.class));
    }
    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public class LocalImageHolderView implements CBPageAdapter.Holder<Integer>{
        private ImageView imageView;
        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, Integer data) {
            imageView.setImageResource(data);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //點擊事件
                    Toast.makeText(view.getContext(), "第" + (position + 1) + "張圖片", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    // 开始自动翻页
    @Override
    public void onResume() {
        super.onResume();
        //开始自动翻页
        convenientBanner.startTurning(3000);
    }

    // 停止自动翻页
    @Override
    public void onPause() {
        super.onPause();
        //停止翻页
        convenientBanner.stopTurning();
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
