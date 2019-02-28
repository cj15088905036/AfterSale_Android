package com.mapsoft.aftersale.utils;

import android.view.View;

/**
 * Created by Administrator on 2017/10/24.
 */

public abstract class NoDoubleClickListener implements View.OnClickListener {
    public static final int MIN_CLICK_DELAY_TIME = 3000;//这里设置不能超过多长时间
    private long lastClickTime = 0;

    protected abstract void onNoDoubleClick(View v);
    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }
}
