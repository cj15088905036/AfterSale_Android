package com.mapsoft.aftersale.aftersale.mainfragment.orderfragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapsoft.aftersale.R;
import com.mapsoft.aftersale.RdApplication;
import com.mapsoft.aftersale.aftersale.scan.MipcaActivityCapture;
import com.mapsoft.aftersale.bean.BusProductTable;
import com.mapsoft.aftersale.bean.HigwayProductTable;
import com.mapsoft.aftersale.bean.User;
import com.mapsoft.aftersale.utils.EditDialog;
import com.mapsoft.aftersale.utils.JsonParse;
import com.mapsoft.aftersale.utils.MapSerializable;
import com.mapsoft.aftersale.utils.MyToast;
import com.mapsoft.aftersale.utils.WrappedHttpUtil;

import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/24.
 * 正在维修---订单信息(基本信息，设备信息) 完成订单操作
 */

public class RepairingOrderInfoFragmentActivity extends FragmentActivity implements View.OnClickListener {
    private Button btnBack, btn_scan, btn_grab_rofa, btn_complete_two, btn_create;
    private TextView tv_baseMessage, tv_productMessage, tv_code;
    private LinearLayout ll_baseMessage, ll_productMessage, ll_btn;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private OrderBaseMessageFragment orderBaseMessageFragment;
    private RepairProductFragment repairProductFragment;
    private SharedPreferences sp;
    private Map<String, String> orderMap;//订单信息的map
    private SQLiteDatabase db;
    private String type_code = "";//类型+编号，用于查询数据库
    private String order_type = "";
    private String order_code = "";
    private String user_name = "";
    private String main_type = "";//0：公交   1：公路
    private String order_message = "";

//    user_region


    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getSupportFragmentManager();
        setContentView(R.layout.repairing_order_info);
        init();
        tv_code.setText(order_type + order_code);
        type_code = tv_code.getText().toString().trim();
        if ("complete".equals(order_message)) {
            //已完成
            btn_complete_two.setVisibility(View.GONE);
            btn_scan.setVisibility(View.GONE);
            btn_grab_rofa.setVisibility(View.GONE);
            btn_create.setVisibility(View.GONE);
            if ("T".equals(order_type)) {
                tv_productMessage.setText("安装备注");
            }
        } else if ("grab".equals(order_message)) {
            //抢单
            btn_create.setVisibility(View.GONE);
            btn_complete_two.setVisibility(View.GONE);
            ll_productMessage.setVisibility(View.GONE);
            btn_scan.setVisibility(View.GONE);
            ll_btn.setVisibility(View.GONE);
            btn_grab_rofa.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View view = getLayoutInflater().inflate(R.layout.nullmessage, null);
                    new EditDialog.Builder(RepairingOrderInfoFragmentActivity.this)
                            .setTitle("抢单")
                            .setContentView(view)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    new Thread() {
                                        @Override
                                        public void run() {
                                            super.run();
                                            String method = "RobOrder";
                                            if ("W".equals(order_type)) {
                                                //维修抢单
                                                Map<String, String> params = new HashMap<String, String>();
                                                params.put("UserName", user_name);
                                                params.put("OrderCode", order_code);
                                                new WrappedHttpUtil.Builder(RepairingOrderInfoFragmentActivity.this)
                                                        .dataRepairRequest(method, params)
                                                        .setL(new WrappedHttpUtil.Builder.Listener() {
                                                            @Override
                                                            public void onSuccess(int responseCode, String result) {
                                                                boolean isJson = JsonParse.isJson(result);
                                                                if (isJson) {
                                                                    String message = JsonParse.judgeJson(result);
                                                                    if ("1".equals(message)) {
                                                                        showOnThread("抢单成功");
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                dialog.dismiss();
                                                                                sendBroadcast(new Intent(getString(R.string.refresh)));
//                                                                                sp.edit().putString("grab_tag", "1").apply();
                                                                                new Handler().postDelayed(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        finish();
                                                                                    }
                                                                                }, 500);
                                                                            }
                                                                        });
                                                                    } else if ("0".equals(message)) {
                                                                        showOnThread("抢单失败,该订单已被抢");
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                dialog.dismiss();
                                                                                sp.edit().putString("grab_tag", "2").apply();
                                                                                new Handler().postDelayed(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        finish();
                                                                                    }
                                                                                }, 500);
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showOnThread(getResources().getString(R.string.mistake_unknown));
                                                                    }
                                                                } else {
                                                                    showOnThread(getResources().getString(R.string.mistake_dataResolve));
                                                                }
                                                            }

                                                            @Override
                                                            public void onFail(int responseCode, String result) {
                                                                ToastExceptionMessage("维修单抢单", responseCode, result);
                                                            }
                                                        }).execute();
                                            } else if ("T".equals(order_type)) {
                                                //安装抢单
                                                Map<String, String> params = new HashMap<String, String>();
                                                params.put("UserName", user_name);
                                                params.put("OrderId", order_code);
                                                new WrappedHttpUtil.Builder(RepairingOrderInfoFragmentActivity.this)
                                                        .dataInstallRequest(method, params)
                                                        .setL(new WrappedHttpUtil.Builder.Listener() {
                                                            @Override
                                                            public void onSuccess(int responseCode, String result) {
                                                                boolean isJson = JsonParse.isJson(result);
                                                                if (isJson) {
                                                                    String message = JsonParse.judgeJson(result);
                                                                    if ("1".equals(message)) {
                                                                        showOnThread("抢单成功");
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                dialog.dismiss();
                                                                                sendBroadcast(new Intent(getString(R.string.refresh)));
                                                                                sp.edit().putString("grab_tag", "1").apply();
                                                                                new Handler().postDelayed(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        finish();
                                                                                    }
                                                                                }, 500);
                                                                            }
                                                                        });
                                                                    } else if ("0".equals(message)) {
                                                                        showOnThread("抢单失败,该订单已被抢");
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                dialog.dismiss();
                                                                                sp.edit().putString("grab_tag", "2").apply();
                                                                                new Handler().postDelayed(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        finish();
                                                                                    }
                                                                                }, 500);
                                                                            }
                                                                        });
                                                                    } else {
                                                                        showOnThread(getResources().getString(R.string.mistake_unknown));
                                                                    }
                                                                } else {
                                                                    showOnThread(getResources().getString(R.string.mistake_dataResolve));
                                                                }
                                                            }

                                                            @Override
                                                            public void onFail(int responseCode, String result) {
                                                                ToastExceptionMessage("安装单抢单", responseCode, result);
                                                            }
                                                        }).execute();
                                            }
                                        }
                                    }.start();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create().show();
                }
            });
        } else if ("repairing".equals(order_message)) {
            //维修中
            btn_grab_rofa.setVisibility(View.GONE);
            if ("0".equals(main_type)) {
                //公交
                btn_create.setVisibility(View.GONE);
                if (!"W".equals(order_type)) {
                    //安装调试单
                    tv_productMessage.setText("安装备注");
                }
                btn_scan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(RepairingOrderInfoFragmentActivity.this, MipcaActivityCapture.class));
                    }
                });
            } else if ("1".equals(main_type)) {
//                //公路
                btn_scan.setVisibility(View.GONE);
                if (!"W".equals(order_type)) {
                    //安装调试单
                    btn_scan.setVisibility(View.GONE);
                    tv_productMessage.setText("已安装设备");
                }
                btn_create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.product_list_higway, null);
                        final EditText et_input = (EditText) view.findViewById(R.id.et_input);
                        EditDialog.Builder builder = new EditDialog.Builder(RepairingOrderInfoFragmentActivity.this);
                        builder
                                .setTitle("请输入设备ID")
                                .setContentView(view)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String productList = et_input.getText().toString().trim().toUpperCase();
                                        if ("".equals(productList)) {
                                            showOnUI("设备ID不能为空");
                                            return;
                                        }
                                        Intent intent = new Intent(RepairingOrderInfoFragmentActivity.this
                                                , HighwayUploadingOrSaveActivity.class);
                                        sp.edit()
                                                .putString("flag", "order_list")
                                                .putString("product_list", productList)
                                                .apply();
                                        startActivity(intent);
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setCanTouchOutside(false);
                        builder.create().show();
                    }
                });
            }


            btn_complete_two.setOnClickListener(new View.OnClickListener() {
                //完成订单操作
                @Override
                public void onClick(View v) {
                    //订单类型+编号用于区分保存在本地数据库中的记录
                    View layout = getLayoutInflater().inflate(R.layout.nullmessage, null);
                    new EditDialog.Builder(RepairingOrderInfoFragmentActivity.this)
                            .setTitle("完成订单")
                            .setContentView(layout)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {

                                    if ("0".equals(main_type)) {
                                        //公交
                                        if ("W".equals(order_type)) {
                                            //维修单
                                            /**
                                             * 1、先去服务器查询是否上传过维修设备
                                             * 2、若没有，则不能完成订单
                                             * 3、若有，再去本地数据库查询是否还有设备未上传
                                             * 4、若本地数据库有设备未上传，先让其上传
                                             * 5、若没有设备未完成则完成订单
                                             *      此步骤为防止用其他手机不能完成订单
                                             */
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    super.run();
                                                    //1、
                                                    String method = "QueryReslutInfo";
                                                    Map<String, String> params = new HashMap<String, String>();
                                                    params.put("OrderCode", order_code);
                                                    new WrappedHttpUtil.Builder(RepairingOrderInfoFragmentActivity.this)
                                                            .dataRepairRequest(method, params)
                                                            .setL(new WrappedHttpUtil.Builder.Listener() {
                                                                @Override
                                                                public void onSuccess(int responseCode, String result) {
                                                                    boolean isJson = JsonParse.isJson(result);
                                                                    if (isJson) {
                                                                        int repair_number = -1;
                                                                        String message = JsonParse.judgeJson(result);
                                                                        if ("0".equals(message)) {
                                                                            //没有维修产品
                                                                            showOnThread("请先上传维修设备信息");
                                                                            return;
                                                                        }
                                                                        try {
                                                                            JSONArray array = new JSONArray(message);
                                                                            for (int i = 0; i < array.length(); i++) {
                                                                                repair_number = i + 1;
                                                                            }
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                        if (repair_number < Integer.parseInt(orderMap.get("product_number"))) {
                                                                            showOnThread("维修数量不正确");
                                                                            return;
                                                                        }
                                                                        boolean hasAllUploaded = true;//全部设备已上传
                                                                        if (db != null) {
                                                                            List<BusProductTable> product_state = DataSupport
                                                                                    .select("*")
                                                                                    .where("type_code = ?", type_code)
                                                                                    .find(BusProductTable.class);
                                                                            List<String> stateList = new ArrayList<>();
                                                                            for (int i = 0; i < product_state.size(); i++) {
                                                                                BusProductTable product = product_state.get(i);
                                                                                stateList.add(product.getProduct_state());
                                                                            }
                                                                            if (stateList.size() != 0) {
                                                                                for (int i = 0; i < stateList.size(); i++) {
                                                                                    if (!"已提交".equals(stateList.get(i))) {
                                                                                        hasAllUploaded = false;//有设备未上传
                                                                                    }
                                                                                }
                                                                                if (!hasAllUploaded) {
                                                                                    //4、
                                                                                    showOnThread("有设备未上传,请先上传");
                                                                                } else {
                                                                                    completeRepairOrder(dialog);//5、本地数据已提交所有设备
                                                                                }
                                                                            } else {
                                                                                completeRepairOrder(dialog);//5、1个手机上传设备，另一个手机完成订单
                                                                            }
                                                                        } else {
                                                                            showOnThread(getResources().getString(R.string.sql_database_null));
                                                                        }
                                                                    } else {
                                                                        showOnThread("完成维修单\r\n" + getResources().getString(R.string.mistake_dataResolve));
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFail(int responseCode, String result) {
                                                                    ToastExceptionMessage("完成维修单", responseCode, result);
                                                                }
                                                            }).execute();
                                                }
                                            }.start();
                                        } else if ("T".equals(order_type)) {
                                            //安装调试单完成
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    super.run();
                                                    completeInstallOrder(dialog);//公交
                                                }
                                            }.start();
                                        }
                                    } else if ("1".equals(main_type)) {
                                        //公路
                                        if ("W".equals(order_type)) {
                                            //1、
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    super.run();
                                                    String method = "QueryReslutInfoHighWay";
                                                    Map<String, String> args = new HashMap<String, String>();
                                                    args.put("OrderCode", order_code);
                                                    new WrappedHttpUtil.Builder(RepairingOrderInfoFragmentActivity.this)
                                                            .dataRepairRequest(method, args)
                                                            .setL(new WrappedHttpUtil.Builder.Listener() {
                                                                @Override
                                                                public void onSuccess(int responseCode, String result) {
                                                                    boolean isJson = JsonParse.isJson(result);
                                                                    if (isJson) {
                                                                        String message = "";
                                                                        int repair_number = 0;
                                                                        try {
                                                                            JSONObject object = new JSONObject(result);
                                                                            message = object.getString("result");
                                                                            if (!"0".equals(message)) {
                                                                                JSONArray array = new JSONArray(object.getString("data"));
                                                                                for (int i = 0; i < array.length(); i++) {
                                                                                    repair_number = i + 1;
                                                                                }
                                                                            }
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                        if ("0".equals(message)) {
                                                                            showOnThread("该订单没有上传过设备");
                                                                        } else if ("1".equals(message)) {
                                                                            if (repair_number < Integer.parseInt(orderMap.get("product_number"))) {
                                                                                showOnThread("维修数量不正确");
                                                                                return;
                                                                            }
                                                                            if (db != null) {
                                                                                boolean hasAllUploaded = true;//全部设备已上传
                                                                                List<HigwayProductTable> productTables = DataSupport
                                                                                        .select("*")
                                                                                        .where("type_code = ?", type_code)
                                                                                        .find(HigwayProductTable.class);
                                                                                List<String> stateList = new ArrayList<>();
                                                                                for (int i = 0; i < productTables.size(); i++) {
                                                                                    HigwayProductTable product = productTables.get(i);
                                                                                    stateList.add(product.getProduct_state());
                                                                                }
                                                                                if (stateList.size() != 0) {
                                                                                    for (int i = 0; i < stateList.size(); i++) {
                                                                                        if (!"已提交".equals(stateList.get(i))) {
                                                                                            hasAllUploaded = false;//有设备未上传
                                                                                        }
                                                                                    }
                                                                                    if (!hasAllUploaded) {
                                                                                        showOnUI("有设备未上传维修记录,请先上传");
                                                                                    } else {
                                                                                        //完成维修订单
                                                                                        completeRepairOrder(dialog);//公路 本地数据已提交所有设备
                                                                                    }
                                                                                } else {
                                                                                    //完成维修订单
                                                                                    completeRepairOrder(dialog);//公路 1个手机上传设备，另一个手机完成订单
                                                                                }
                                                                            } else {
                                                                                showOnThread(getResources().getString(R.string.sql_database_null));
                                                                            }
                                                                        } else {
                                                                            showOnThread("完成维修单\r\n" + getResources().getString(R.string.mistake_unknown));
                                                                        }
                                                                    } else {
                                                                        showOnThread("完成维修单\r\n" + getResources().getString(R.string.mistake_dataResolve));
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFail(int responseCode, String result) {
                                                                    ToastExceptionMessage("完成维修单", responseCode, result);
                                                                }
                                                            }).execute();
                                                }
                                            }.start();
                                        } else if ("T".equals(order_type)) {
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    super.run();
                                                    //1、
                                                    String method = "GetInstallInfo";
                                                    Map<String, String> args = new HashMap<String, String>();
                                                    args.put("OrderId", order_code);
                                                    new WrappedHttpUtil.Builder(RepairingOrderInfoFragmentActivity.this)
                                                            .dataInstallRequest(method, args)
                                                            .setL(new WrappedHttpUtil.Builder.Listener() {
                                                                @Override
                                                                public void onSuccess(int responseCode, String result) {
                                                                    boolean isJson = JsonParse.isJson(result);
                                                                    if (isJson) {
                                                                        String message = "";
                                                                        try {
                                                                            JSONObject object = new JSONObject(result);
                                                                            message = object.getString("result");
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                        if ("0".equals(message)) {
                                                                            //没有上传过设备
                                                                            showOnThread("该订单没有上传过设备");
                                                                        } else if ("1".equals(message)) {
                                                                            //有上传过设备，查询本地数据库是否还有未上传设备
                                                                            if (db != null) {
                                                                                boolean hasAllUploaded = true;//全部设备已上传
                                                                                List<HigwayProductTable> productTables = DataSupport
                                                                                        .select("*")
                                                                                        .where("type_code = ?", type_code)
                                                                                        .find(HigwayProductTable.class);
                                                                                List<String> stateList = new ArrayList<>();
                                                                                for (int i = 0; i < productTables.size(); i++) {
                                                                                    HigwayProductTable product = productTables.get(i);
                                                                                    stateList.add(product.getProduct_state());
                                                                                }
                                                                                if (stateList.size() != 0) {
                                                                                    for (int i = 0; i < stateList.size(); i++) {
                                                                                        if (!"已提交".equals(stateList.get(i))) {
                                                                                            hasAllUploaded = false;//有设备未上传
                                                                                        }
                                                                                    }
                                                                                    if (!hasAllUploaded) {
                                                                                        showOnUI("有设备未上传维修记录,请先上传");
                                                                                        return;
                                                                                    } else {
                                                                                        //完成安装订单
                                                                                        completeInstallOrder(dialog);//公路
                                                                                    }
                                                                                } else {
                                                                                    //完成安装订单
                                                                                    completeInstallOrder(dialog);//公路
                                                                                }
                                                                            } else {
                                                                                showOnThread(getResources().getString(R.string.sql_database_null));
                                                                            }

                                                                        } else {
                                                                            showOnThread("完成安装单\r\n" + getResources().getString(R.string.mistake_unknown));
                                                                        }
                                                                    } else {
                                                                        showOnThread("完成安装单\r\n" + getResources().getString(R.string.mistake_dataResolve));
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFail(int responseCode, String result) {
                                                                    ToastExceptionMessage("完成安装单", responseCode, result);
                                                                }
                                                            }).execute();
                                                }
                                            }.start();
                                        }
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                }
            });
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * 完成安装调试单
     *
     * @param dialog
     */
    private void completeInstallOrder(final DialogInterface dialog) {
        String method = "Complete";
        String install_remark = sp.getString(
                order_code, "");
        Map<String, String> params = new HashMap<String, String>();
        params.put("Code", order_code);
        params.put("Name", orderMap.get("order_engineer"));
        params.put("Remark", install_remark);
        new WrappedHttpUtil.Builder(RepairingOrderInfoFragmentActivity.this)
                .dataInstallRequest(method, params)
                .setL(new WrappedHttpUtil.Builder.Listener() {
                    @Override
                    public void onSuccess(int responseCode, String result) {
                        boolean isJson = JsonParse.isJson(result);
                        if (isJson) {
                            String message = JsonParse.judgeJson(result);
                            if ("0".equals(message)) {
                                showOnThread("完成失败,该订单不存在或已被完成");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendBroadcast(new Intent(getString(R.string.refresh)));
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        }, 500);
                                    }
                                });
                            } else if ("1".equals(message)) {//完成成功
                                if ("1".equals(main_type)) {
                                    DataSupport.deleteAll(HigwayProductTable.class, "type_code = ?", type_code);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendBroadcast(new Intent(getString(R.string.refresh)));
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        }, 500);
                                    }
                                });
                            } else {
                                showOnThread("完成安装单\r\n" + getResources().getString(R.string.mistake_unknown));
                            }
                        } else {
                            showOnThread("完成安装单\r\n" + getResources().getString(R.string.mistake_dataResolve));
                        }
                    }

                    @Override
                    public void onFail(int responseCode, String result) {
                        ToastExceptionMessage("完成安装单", responseCode, result);
                    }
                }).execute();
    }


    /**
     * 完成维修单
     *
     * @param dialog 弹窗
     */
    private void completeRepairOrder(final DialogInterface dialog) {
        String method = "Complete";
        Map<String, String> args = new HashMap<String, String>();
        args.put("OrderCode", order_code);
        args.put("Name", orderMap.get("order_engineer"));
        new WrappedHttpUtil.Builder(RepairingOrderInfoFragmentActivity.this)
                .dataRepairRequest(method, args)
                .setL(new WrappedHttpUtil.Builder.Listener() {
                    @Override
                    public void onSuccess(int responseCode, String result) {
                        boolean isJson = JsonParse.isJson(result);
                        if (isJson) {
                            String message = JsonParse.judgeJson(result);
                            if ("1".equals(message)) {//完成成功
                                showOnThread("上传成功");
                                if ("0".equals(main_type)) {
                                    DataSupport.deleteAll(BusProductTable.class, "type_code = ?", type_code);
                                } else if ("1".equals(main_type)) {
                                    DataSupport.deleteAll(HigwayProductTable.class, "type_code = ?", type_code);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendBroadcast(new Intent(getString(R.string.refresh)));
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        }, 500);
                                    }
                                });
                            } else if ("0".equals(message)) {
                                showOnThread("完成失败\r\n该订单不存在或已被完成");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendBroadcast(new Intent(getString(R.string.refresh)));
                                        dialog.dismiss();
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();
                                            }
                                        }, 1000);

                                    }
                                });
                            } else {
                                showOnThread("完成维修单\r\n" + getResources().getString(R.string.mistake_unknown));
                            }
                        } else {
                            showOnThread("完成维修单\r\n" + getResources().getString(R.string.mistake_dataResolve));
                        }
                    }

                    @Override
                    public void onFail(int responseCode, String result) {
                        ToastExceptionMessage("完成维修单", responseCode, result);
                    }
                }).execute();
    }

    /**
     * 初始化控件
     */
    private void init() {
        db = RdApplication.getDatabase();
        orderMap = RdApplication.get().getOrderMap();
        user_name = RdApplication.get().getUser().getUser_name();
        main_type =orderMap.get("main_type");
        order_type = orderMap.get("order_type");
        order_code = orderMap.get("order_code");
        tv_baseMessage = (TextView) findViewById(R.id.tv_baseMessage);
        tv_productMessage = (TextView) findViewById(R.id.tv_productMessage);
        ll_baseMessage = (LinearLayout) findViewById(R.id.ll_baseMessage);
        ll_baseMessage.setOnClickListener(this);
        ll_productMessage = (LinearLayout) findViewById(R.id.ll_productMessage);
        ll_productMessage.setOnClickListener(this);
        ll_btn = (LinearLayout) findViewById(R.id.ll_btn);
        btnBack = (Button) findViewById(R.id.btnBack);
        btn_scan = (Button) findViewById(R.id.btn_scan);
        btn_grab_rofa = (Button) findViewById(R.id.btn_grab_rofa);
        btn_complete_two = (Button) findViewById(R.id.btn_complete_two);
        btn_create = (Button) findViewById(R.id.btn_create);
        tv_code = (TextView) findViewById(R.id.tv_code);
        sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        order_message = sp.getString("order_message", "");
        //设置默认界面
        setDefaultFragment();
    }

    private void setDefaultFragment() {
        ft = fm.beginTransaction();
        orderBaseMessageFragment = new OrderBaseMessageFragment();
        ft.replace(R.id.fl_orderMessage, orderBaseMessageFragment);
        tv_baseMessage.setBackground(ActivityCompat.getDrawable(this, R.drawable.head_tab_1));
        ft.commit();
    }

    @Override
    public void onClick(View v) {
        ft = fm.beginTransaction();
        switch (v.getId()) {
            case R.id.ll_baseMessage:
                if (orderBaseMessageFragment == null) {
                    orderBaseMessageFragment = new OrderBaseMessageFragment();
                }
                tv_baseMessage.setBackground(ActivityCompat.getDrawable(this, R.drawable.head_tab_1));
                tv_productMessage.setBackground(ActivityCompat.getDrawable(this, R.drawable.head_tab_2));
                ft.replace(R.id.fl_orderMessage, orderBaseMessageFragment);
                break;
            case R.id.ll_productMessage:
                if (repairProductFragment == null) {
                    repairProductFragment = new RepairProductFragment();
                }
                ft.replace(R.id.fl_orderMessage, repairProductFragment);
                tv_productMessage.setBackground(ActivityCompat.getDrawable(this, R.drawable.head_tab_1));
                tv_baseMessage.setBackground(ActivityCompat.getDrawable(this, R.drawable.head_tab_2));
                break;
            default:
                break;
        }
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void showOnUI(String message) {
        MyToast.popToast(RepairingOrderInfoFragmentActivity.this, message);
    }

    public void showOnThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.popToast(RepairingOrderInfoFragmentActivity.this, message);
            }
        });
    }

    /**
     * Toast异常提示
     *
     * @param interfaceType 接口类型
     * @param responseCode  返回码
     * @param result        返回结果
     */
    public void ToastExceptionMessage(String interfaceType, int responseCode, String result) {
        if (responseCode == 0) {
            if ("123".equals(result)) {
                showOnThread(getResources().getString(R.string.noNetwork));
            }
        }
        if (responseCode == 404) {
            showOnThread(interfaceType + "\r\n" +
                    getResources().getString(R.string.mistake_404));
        }
        if (responseCode == 500) {
            showOnThread(interfaceType + "\r\n" +
                    getResources().getString(R.string.mistake_500));
        }
    }
}
