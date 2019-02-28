package com.mapsoft.aftersale.utils;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


/**
 * Created by Administrator on 2017/10/16.
 */

public class UpdateService extends Service {
    private MyBinder myBinder=new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("服务创建","onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("执行任务","");
        myBinder.startDownload();

        stopSelf();
        Log.e("执行结束","2");
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("服务开始","onBind");
        return myBinder;
    }



}

class MyBinder extends Binder {

    public void startDownload(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                Log.e("开始下载","1");
            }
        }.start();
    }
}
