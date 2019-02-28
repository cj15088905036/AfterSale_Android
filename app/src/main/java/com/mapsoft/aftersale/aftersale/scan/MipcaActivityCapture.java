package com.mapsoft.aftersale.aftersale.scan;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;


import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mapsoft.aftersale.aftersale.mainfragment.orderfragment.BusUploadingOrSaveActivity;
import com.mapsoft.aftersale.aftersale.scan.zxing.camera.MyCameraManager;
import com.mapsoft.aftersale.utils.LightManager;
import com.mapsoft.aftersale.R;
import com.mapsoft.aftersale.aftersale.scan.zxing.decoding.CaptureActivityHandler;
import com.mapsoft.aftersale.aftersale.scan.zxing.decoding.InactivityTimer;
import com.mapsoft.aftersale.aftersale.scan.zxing.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;


import java.io.IOException;
import java.util.Vector;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class MipcaActivityCapture extends Activity implements Callback {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private Button mButtonBack, btnClose;
    private CameraManager manager;// 声明CameraManager对象
    private Camera m_Camera = null;// 声明Camera对象
    private Button btnOpen;
    private boolean flag = false;//true为打开状态，false为关闭
    private LightManager lightManager;

    private Camera.Parameters params;
    private boolean isTorchOpen;//手电筒开关

    private Button btn_ok;
    private EditText et_import_list;

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Called when the activity is first created.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        btn_ok = (Button) findViewById(R.id.btn_ok);
        et_import_list = (EditText) findViewById(R.id.et_import_list);
        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String result = et_import_list.getText().toString().toUpperCase();
                if (result.length() != 18) {
                    showToastMessage("条形码必须是18位");
                } else if (!result.startsWith("B") && !result.startsWith("01") && !result.startsWith("02")) {
                    showToastMessage("输入错误——非公司出产");
                } else {
                    Intent intent = new Intent(MipcaActivityCapture.this, BusUploadingOrSaveActivity.class);
                    intent.putExtra("result", result);
                    startActivity(intent);
                    finish();
                }
            }
        });
        //ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
        MyCameraManager.init(getApplication());
        lightManager = new LightManager(this);
        lightManager.init();
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);

        mButtonBack = (Button) findViewById(R.id.button_back);
        mButtonBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MipcaActivityCapture.this.finish();
            }
        });
        btnOpen = (Button) findViewById(R.id.btnOpen);
        btnOpen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
             /*  if ("打开手电筒".equals(btnOpen.getText())){
                   lightManager.turnOn();
                   btnOpen.setText("关闭手电筒");
               }else {
                   lightManager.turnOff();
                   btnOpen.setText("打开手电筒");
               }*/
                if (isTorchOpen) {
                    isTorchOpen = false;
                    closeLight();
                    btnOpen.setText("打开手电筒");
                } else {
                    isTorchOpen = true;
                    openLight();
                    btnOpen.setText("关闭手电筒");
                }
            }
        });
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    private void showToastMessage(String message) {
        Toast.makeText(MipcaActivityCapture.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        super.finish();
        Log.e("结束时间", System.currentTimeMillis() + "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        MyCameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        if ("关闭手电筒".equals(btnOpen.getText())) {
            lightManager.turnOff();
        }
        super.onDestroy();
        inactivityTimer.shutdown();

    }

    /**
     * 处理扫描结果
     *
     * @param result
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void handleDecode(Result result) {

        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        final String resultString = result.getText();
//        final String resultString ="B10000000000000004";
        Log.i("条形码--", resultString);
        if ((resultString.startsWith("B") && resultString.length() == 18)
                ||
                (resultString.startsWith("01") || resultString.startsWith("02")) && resultString.length() == 18
                ) {
            Intent intent = new Intent(MipcaActivityCapture.this, BusUploadingOrSaveActivity.class);
            intent.putExtra("result", resultString);
            startActivity(intent);
            MipcaActivityCapture.this.finish();
        } else {
            Toast.makeText(MipcaActivityCapture.this, "扫描失败，请重新扫描", Toast.LENGTH_SHORT).show();
            try {
                Thread.sleep(500);
                Message msg = new Message();
                msg.what = R.id.restart_preview;
                handler.handleMessage(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            MyCameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
        m_Camera = MyCameraManager.getCamera();
        if (m_Camera == null) {
            showToastMessage("获取相机异常");
            return;
        }
        params = m_Camera.getParameters();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private void closeLight() //关闪光灯
    {
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        m_Camera.setParameters(params);
        isTorchOpen = false;
    }


    private void openLight() //开闪光灯
    {

        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        m_Camera.setParameters(params);
        m_Camera.startPreview();
        isTorchOpen = true;
    }

}