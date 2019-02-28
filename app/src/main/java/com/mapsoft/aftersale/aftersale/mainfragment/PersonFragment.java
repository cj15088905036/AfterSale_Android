package com.mapsoft.aftersale.aftersale.mainfragment;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapsoft.aftersale.MainLoginActivity;
import com.mapsoft.aftersale.R;
import com.mapsoft.aftersale.RdApplication;
import com.mapsoft.aftersale.utils.CommonParams;
import com.mapsoft.aftersale.utils.EditDialog;
import com.mapsoft.aftersale.utils.MyToast;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by Administrator on 2017/11/24.
 * 个人界面
 */

public class PersonFragment extends Fragment {
    private TextView
            tv_show,    //部门+姓名
            tv_phone;   //技术支持联系方式
    private ImageView iv_update;
    private MainAfterSaleFragmentActivity mContext;
    private long download_id = 0;
    private DownloadReceiver downloadReceiver;
    private DownloadManager downloadManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainAfterSaleFragmentActivity) {
            this.mContext = (MainAfterSaleFragmentActivity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        downloadReceiver = new DownloadReceiver();
        mContext.registerReceiver(downloadReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(downloadReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.personmessagefragment, container, false);
        tv_show = (TextView) view.findViewById(R.id.tv_show);
        tv_phone = (TextView) view.findViewById(R.id.tv_phone);
        iv_update = (ImageView) view.findViewById(R.id.iv_update);
        new VersionTask(false).execute();
        view.findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VersionTask(true).execute();
            }
        });
        tv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = tv_phone.getText().toString().trim();
                RdApplication.phoneCall(getActivity(), phone);
            }
        });
        String iname = RdApplication.get().getUser().getUser_name();
        String user_department = RdApplication.get().getUser().getUser_department();
        tv_show.setText(user_department + " " + iname);
        tv_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(getActivity(), MainLoginActivity.class));
                mContext.finish();
            }
        });
        return view;
    }

    /**
     * 版本请求
     */
    class VersionTask extends AsyncTask<String, Integer, String> {

        private boolean isUserAsk;//是自动检查还是用户主动检查

        private VersionTask(boolean userAskVersion) {
            this.isUserAsk = userAskVersion;
        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            try {
                URL url = new URL(getString(R.string.service_address));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                // 主服务器请求
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    result = CommonParams.getVersion(connection.getInputStream());
                }

            } catch (Exception e) {
                result = null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {
            if (!TextUtils.isEmpty(result)) {
                String[] strings = result.split(",");
                if (strings.length == 2) {
                    String onlineVersion = strings[0];
                    String localVersion = CommonParams.getVersionName(mContext);//AndroidManifest.xml中的versionName
                    if (!TextUtils.isEmpty(localVersion) &&
                            Integer.parseInt(onlineVersion.replace(".", ""))
                                    > Integer.parseInt(localVersion.replace(".", ""))) {
                        iv_update.setVisibility(View.VISIBLE);
                        //发现新版本，提示用户更新
                        showUpdateDialog(strings[0], strings[1]);
                    } else {
                        if (isUserAsk)
                            showOnUI("未发现新版本...");
                    }
                }
            } else {
                showOnUI("获取服务器版本失败!");
            }
        }
    }

    /**
     * Toast 提示
     *
     * @param message
     */
    public void showOnUI(String message) {
        MyToast.popToast(mContext, message);
    }

    /**
     * 版本更新弹窗
     *
     * @param version 版本号
     * @param url     下载地址
     */
    public void showUpdateDialog(String version, final String url) {
        View view = mContext.getLayoutInflater().inflate(R.layout.update_version, null, false);
        new EditDialog.Builder(mContext)
                .setTitle("发现新版本:" + version + "建议更新!")
                .setContentView(view)
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        downloadAppUnderWifi(url);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    //从服务器下载app新版本
    private void downloadAppUnderWifi(String url) {
        MyToast.popToast(mContext, "下载安装包,请稍候...");
        String appName = CommonParams.DOWN_APK;
        File apkFile = new File(mContext.getExternalFilesDir(null), appName);//即/storage/emulated/0/Android/data/包名/files/app名.apk
        if (apkFile.exists())
            apkFile.delete();
        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalFilesDir(mContext.getBaseContext(), null, appName);
        request.setTitle(appName);
        request.setVisibleInDownloadsUi(true);
        request.setAllowedOverRoaming(false);//漫游
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        download_id = downloadManager.enqueue(request);
    }


    class DownloadReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (download_id == reference) {
                    // installAPK(context, getRealFilePath(context, downloadManager.getUriForDownloadedFile(download_id)));
                    installAPK(context, CommonParams.getRealFilePath(context, downloadManager.getUriForDownloadedFile(download_id)));
                }
            }
        }
    }

    /**
     * 安装apk
     *
     * @param context
     * @param realFilePath
     */
    private void installAPK(Context context, String realFilePath) {
        File file = new File(realFilePath);
        if (file.exists()) {
            openFile(file, context);
        } else {
            showOnUI("下载失败");
        }
    }

    public void openFile(File var0, Context var1) {
        Intent var2 = new Intent();
        var2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        var2.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uriForFile = FileProvider.getUriForFile(var1, var1.getApplicationContext().getPackageName() + ".provider", var0);
            var2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            var2.setDataAndType(uriForFile, var1.getContentResolver().getType(uriForFile));
        } else {
            var2.setDataAndType(Uri.fromFile(var0), getMIMEType(var0));
        }
        try {
            var1.startActivity(var2);
        } catch (Exception e) {
            e.printStackTrace();
            showOnUI("没有找到打开此类文件的程序");
        }
    }

    public String getMIMEType(File var0) {
        String var1;
        String var2 = var0.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }


}
