package com.mapsoft.aftersale;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mapsoft.aftersale.aftersale.mainfragment.MainAfterSaleFragmentActivity;
import com.mapsoft.aftersale.bean.User;
import com.mapsoft.aftersale.utils.EditDialog;
import com.mapsoft.aftersale.utils.JsonParse;
import com.mapsoft.aftersale.utils.MyToast;
import com.mapsoft.aftersale.utils.WrappedHttpUtil;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/30.
 */


public class MainLoginActivity extends Activity {

    private EditText etName, etPwd;
    private EditDialog dialog;

    /**
     * 权限获取
     */
    boolean isAllAllowed = true;
    public static int REQUEST_PERMISSION = 10000;
    public String[] requestPermission = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //检查相关权限
        checkPermission();
        etName = (EditText) findViewById(R.id.etName);
        etPwd = (EditText) findViewById(R.id.etPwd);
        User user = RdApplication.get().getUser();
        String user_account = getSharedPreferences(getPackageName(), MODE_PRIVATE).getString("user_account", "");
        String user_pwd = getSharedPreferences(getPackageName(), MODE_PRIVATE).getString("user_pwd", "");
        if (user != null) {
            if (!"".equals(user.getUser_account()) && !"".equals(user.getUser_pwd())) {
                etName.setText(user.getUser_account());
                etPwd.setText(user.getUser_pwd());
            }
        } else {
            if (!TextUtils.isEmpty(user_account) && !TextUtils.isEmpty(user_pwd)) {
                login(user_account, user_pwd);
            }
        }
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(etName.getText().toString(), etPwd.getText().toString());//点击登录
            }
        });

        etPwd.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                    login(etName.getText().toString(), etPwd.getText().toString());//使用键盘的enter键
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 登录方式：1、点击登录。2、在输入密码后用软键盘的enter键3、自动登录
     */
    public void login(final String login_name, final String login_pwd) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    final String password = WrappedHttpUtil.md5(login_pwd);
                    Log.e("用户密码",password);
                    String method = "Login";
                    Map<String, String> args = new HashMap<String, String>();
                    args.put("Account", login_name);
                    args.put("PassWord", password);
                    new WrappedHttpUtil.Builder(MainLoginActivity.this)
                            .dataRepairRequest(method, args)
                            .setL(new WrappedHttpUtil.Builder.Listener() {
                                @Override
                                public void onSuccess(int responseCode, String result) {
                                    try {
                                        boolean isJson = JsonParse.isJson(result);
                                        if (isJson) {
                                            //是Json格式
                                            String message = JsonParse.judgeJson(result);
                                            if ("0".equals(message)) {
                                                //用户名错误
                                                showOnThread("用户名不存在");
                                            } else if ("1".equals(message)) {
                                                //密码错误
                                                showOnThread("密码错误");
                                            } else {
                                                final Map<String, String> map = JsonParse.analysisLogin(message);
//                                                if ("陈杰".equals(map.get("user_name"))) {
//                                                    map.put("user_region", "2");
//                                                }
                                                User user = new User();
                                                user.setUser_pwd(login_pwd);
                                                user.setUser_department(map.get("user_department"));
                                                user.setUser_account(map.get("user_account"));
                                                user.setUser_name(map.get("user_name"));
                                                user.setUser_region(map.get("user_region"));
                                                user.setUser_id(map.get("user_id"));
                                                RdApplication.get().setUser(user);
                                                getSharedPreferences(getPackageName(), MODE_PRIVATE).edit()
                                                        .putString("user_account", login_name)
                                                        .putString("user_pwd", login_pwd)
                                                        .putString("user_name", map.get("user_name"))
                                                        .apply();
                                                startActivity(new Intent(MainLoginActivity.this, MainAfterSaleFragmentActivity.class));
                                                finish();
                                            }
                                        } else {
                                            showOnThread("登录\r\n" + getResources().getString(R.string.mistake_dataResolve));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFail(int responseCode, String result) {
                                    if (responseCode == 0) {
                                        if ("123".equals(result)) {
                                            showOnThread(getResources().getString(R.string.noNetwork));
                                        }
                                    }
                                    if (responseCode == 404) {
                                        showOnThread("登录\r\n" + getResources().getString(R.string.mistake_404));
                                    }
                                    if (responseCode == 500) {
                                        showOnThread("登录\r\n" + getResources().getString(R.string.mistake_500));
                                    }
                                }

                            }).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 请求开启权限
     */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean need = false;
            // GPS权限
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                need = true;
            }
            // SD卡读取权限
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                need = true;
            }
            //拍照权限
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.CAMERA)) {
                need = true;
            }
            //打电话权限
            if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.CALL_PHONE)) {
                need = true;
            }
            if (need) {
                requestPermissions(requestPermission, REQUEST_PERMISSION);
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           final String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //申请最后一个权限的时候，执行检查
        if (requestCode == REQUEST_PERMISSION && grantResults.length == requestPermission.length) {
            // 权限筛选判断
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) { // 权限未被允许
                    isAllAllowed = false;
                }
            }
            //有必需权限未被授予，则显示dialog，屏蔽用户其他操作
            if (!isAllAllowed) {
                showSettingDialog();
            } else {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }

        }
    }


    public void showOnThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.popToast(MainLoginActivity.this, message);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 权限设置弹窗
     */
    public void showSettingDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_message, null);
        EditDialog.Builder builder = new EditDialog.Builder(this);
        TextView tv_show_message = (TextView) view.findViewById(R.id.tv_show_message);
        tv_show_message.setText(R.string.notifyMsg);
        if (dialog == null) {
            dialog = builder.setTitle(R.string.notifyTitle)
                    .setContentView(view)
                    .setPositiveButton(R.string.setting,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                                    String pkg = "com.android.settings";
                                    String cls = "com.android.settings.applications.InstalledAppDetails";
                                    i.setComponent(new ComponentName(pkg, cls));
                                    i.setData(Uri.parse("package:" + getPackageName()));
                                    startActivity(i);
                                }
                            })
                    .setCanTouchOutside(false)
                    .setCancellable(false)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (checkPermission())
                                dialog.dismiss();
                        }
                    })
                    .create();
        }
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.e("MainLoginActivity", "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("MainLoginActivity", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("MainLoginActivity", "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (checkPermission() && dialog != null) {
            if (dialog.isShowing())
                dialog.dismiss();
        }
        Log.e("MainLoginActivity", "onRestart");
    }
}
