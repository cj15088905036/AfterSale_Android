package com.mapsoft.aftersale.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * Created by Adiministrator on 17/8/27
 * 类似IOS沉浸式状态栏效果
 * 思路:
 * -----1.4.4一下系统不支持,4.4以上设置App Theme为非全屏时可用
 *      5.0以上,可以直接调用  window.setStatusBarColor(statusColor);
 *      4.4~~5.0则根据statusBar的高度来设置填充view,再设置其颜色(透明或者某种特定颜色)
 *
 * -----2.设置的时间点:调度app各activity标题头颜色不同,故在各activity的resume时调用设置该视图的statusBarColor
 *
 */
public class ImmersedStatusBar {
    private static final int INVALID_VAL = Color.TRANSPARENT;//透明色
    private static final int COLOR_DEFAULT = Color.TRANSPARENT;//默认色

    /**
     * 设置当前为某Activity时的statusBarColor
     * @param activity
     * @param statusBarColor
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setImmersedStatusBarColor(Activity activity, int statusBarColor) {

        // 5.0及以上的情况
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        /*      Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(statusColor);
            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 使其不为系统 View 预留空间.
                ViewCompat.setFitsSystemWindows(mChildView, false);
            }*/

            Window window = activity.getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(statusBarColor);

            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
                ViewCompat.setFitsSystemWindows(mChildView, true);
            }
            return;
        }

        //4.4和5.0之间
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

          /*  Window window = activity.getWindow();//全屏
            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
                //首先使 ChildView 不预留空间
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                ViewCompat.setFitsSystemWindows(mChildView, false);
            }

            int statusBarHeight = getStatusBarHeight(activity);
                 //需要设置这个 flag 才能设置状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //避免多次调用该方法时,多次移除了 View
            if (mChildView != null && mChildView.getLayoutParams() != null && mChildView.getLayoutParams().height == statusBarHeight) {
                //移除假的 View.
                mContentView.removeView(mChildView);
                mChildView = mContentView.getChildAt(0);
            }
            if (mChildView != null) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mChildView.getLayoutParams();
                //清除 ChildView 的 marginTop 属性
                if (lp != null && lp.topMargin >= statusBarHeight) {
                    lp.topMargin -= statusBarHeight;
                    mChildView.setLayoutParams(lp);
                }
            }*/

            Window window = activity.getWindow();
            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
            //设置系统状态栏背景色为透明,它会叠加在自己设置的填充view上
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            int statusBarHeight = getStatusBarHeight(activity);
            View mChildView = mContentView.getChildAt(0);
            //判断并为填充view预留出顶部空间
            if (mChildView != null) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mChildView.getLayoutParams();
                //已经设置过 marginTop的就跳过
                if (lp != null && lp.topMargin < statusBarHeight && lp.height != statusBarHeight) {
                    ViewCompat.setFitsSystemWindows(mChildView, false);//不预留系统空间
                    lp.topMargin += statusBarHeight;
                    mChildView.setLayoutParams(lp);
                }
            }
            //已经添加过填充view的只需要按需设置填充view的背景颜色
            View statusBarView = mContentView.getChildAt(0);
            if (statusBarView != null && statusBarView.getLayoutParams() != null &&
                    statusBarView.getLayoutParams().height == statusBarHeight) {
                statusBarView.setBackgroundColor(statusBarColor);//避免重复添加
                return;
            }
            //未添加填充view的执行添加
            statusBarView = new View(activity);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
            statusBarView.setBackgroundColor(statusBarColor);
            mContentView.addView(statusBarView, 0, lp); //添加填充View
        }

    }

    public static void compat(Activity activity) {
        setImmersedStatusBarColor(activity, COLOR_DEFAULT);
    }


    /**
     * 获取系统状态栏的高度
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
