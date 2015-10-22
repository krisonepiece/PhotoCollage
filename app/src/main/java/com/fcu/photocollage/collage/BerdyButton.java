package com.fcu.photocollage.collage;

/**
 * Created by Lighting on 2015/9/1.
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Button;

import com.fcu.photocollage.R;

public class BerdyButton extends Button {
    // 创建一个字体缓存，使用LRU缓存策略
    private static final LruCache<String, Typeface> typefaceCache = new LruCache<String, Typeface>(6);

    public BerdyButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Typeface, 0, 0);
        try {
            // 取得自定义Button的typeface属性
            String typefaceName = ta.getString(R.styleable.Typeface_typeface);
            if (!isInEditMode() && !TextUtils.isEmpty(typefaceName)) {
                Typeface typeface = typefaceCache.get(typefaceName);

                if (typeface == null) {
                    typeface = Typeface.createFromAsset(context.getAssets(), String.format("fonts/%s", typefaceName));
                    typefaceCache.put(typefaceName, typeface);
                }

                setTypeface(typeface);
            }
        } finally {
            ta.recycle();
        }
    }

}
