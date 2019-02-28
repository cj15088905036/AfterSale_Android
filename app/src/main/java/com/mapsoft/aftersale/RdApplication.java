package com.mapsoft.aftersale;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.mapsoft.aftersale.bean.User;
import com.mapsoft.aftersale.utils.ImmersedStatusBar;
import com.mapsoft.aftersale.utils.LogUtil;

import org.litepal.LitePalApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/9.
 */

public class RdApplication extends LitePalApplication implements Application.ActivityLifecycleCallbacks {
    private User user;//登录用户的信息
    private static RdApplication appInstance;
    private static List<Activity> activities = new ArrayList<>();
    private Map<String, String> orderMap;//存放点击的订单

    public Map<String, String> getOrderMap() {
        return orderMap;
    }

    public void setOrderMap(Map<String, String> orderMap) {
        this.orderMap = orderMap;
    }

    public static RdApplication get() {
        return appInstance;
    }

    public static void clear() {
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            activity.finish();
        }
    }

    //定义全局的SQLite
    private static SQLiteDatabase db;

    public static SQLiteDatabase getDatabase() {
        return db;
    }

    public static void setDatabase(SQLiteDatabase database) {
        RdApplication.db = database;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil logUtil = LogUtil.getInstance();
        logUtil.init(this);
        Thread.setDefaultUncaughtExceptionHandler(logUtil);
        appInstance = this;
        registerActivityLifecycleCallbacks(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterActivityLifecycleCallbacks(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activities.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity.getClass().getSimpleName().contains("MipcaActivityCapture")) {
            ImmersedStatusBar.setImmersedStatusBarColor(activity, Color.parseColor("#000000"));
        } else {
            ImmersedStatusBar.setImmersedStatusBarColor(activity, Color.parseColor("#2f6395"));
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activities.remove(activity);
    }

    /**
     * 调用打电话功能
     */
    public static void phoneCall(Activity activity, String phone_number) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone_number));
        if (ActivityCompat.checkSelfPermission(activity.getApplication(),
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

}
