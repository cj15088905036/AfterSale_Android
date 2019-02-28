package com.mapsoft.aftersale.aftersale.mainfragment.orderfragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mapsoft.aftersale.R;
import com.mapsoft.aftersale.RdApplication;
import com.mapsoft.aftersale.aftersale.mainfragment.MainAfterSaleFragmentActivity;
import com.mapsoft.aftersale.aftersale.scan.MipcaActivityCapture;
import com.mapsoft.aftersale.bean.BusProductTable;
import com.mapsoft.aftersale.bean.HigwayProductTable;
import com.mapsoft.aftersale.bean.User;
import com.mapsoft.aftersale.utils.EditDialog;
import com.mapsoft.aftersale.utils.JsonParse;
import com.mapsoft.aftersale.utils.MapSerializable;
import com.mapsoft.aftersale.utils.MyRefreshListView;
import com.mapsoft.aftersale.utils.MyToast;
import com.mapsoft.aftersale.utils.OrderAdapter;
import com.mapsoft.aftersale.utils.WrappedHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/24.
 * 主界面--订单--正在维修：显示正在维修(安装)中的订单
 */

public class RepairingFragment extends Fragment {


    private TextView tv_show_repairMessage, tv_show_installMessage;
    private MyRefreshListView listView;
    private OrderAdapter orderAdapter;
    private List<Map<String, String>> allList;//维修单和安装单的数据源
    private List<Map<String, String>> repairList;//维修单数据源
    private List<Map<String, String>> installList;//安装调试单数据源

    private SharedPreferences sp;
    private MainAfterSaleFragmentActivity mContext;


    private boolean
            isRefreshRepair = false,  //下拉维修单刷新
            isRefreshInstall = false, //下拉安装单刷新
            isRepairOK,
            isInstallOK;

    private boolean
            isNoInstallData,//没有安装调试单数据
            isNoRepairData;//没有维修单订单数据

    private Map<String, String> args = new HashMap<String, String>();
    private String method = "";
    private RefreshBroadcastReceiver refreshBroadcastReceiver;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {

            switch (msg.what) {
                case 1:
                    isRepairOK = true;
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            checkOK();
                        }
                    });
                    break;
                case 2:
                    isInstallOK = true;
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            checkOK();
                        }
                    });
                    break;
                case 3:
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            checkData();
                        }
                    });
                    break;
                case 4:
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            checkData();
                        }
                    });
                    break;
                default:

                    break;
            }


        }
    };


    /**
     * 没有任何订单时，给出提示
     */
    private void checkData() {
        if (isNoRepairData && isNoInstallData && allList.size() == 0) {
            showOnUIToast("该用户没有任何订单,请先抢单");
            isNoRepairData = false;
            isNoInstallData = false;
            listView.setonRefreshListener(new MyRefreshListView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshData();//没有数据时刷新下拉刷新更新
                }
            });
        }
    }

    /**
     * 下拉刷新数据
     */
    private void refreshData() {

        isRefreshRepair = true;
        isRefreshInstall = true;
        if (allList != null && allList.size() != 0) {
            allList.clear();
        }
        final long startTime = System.currentTimeMillis();
        getRepairList();//下拉刷新更新维修单数据
        getInstallList();//下拉刷新更新安装单数据
        final long endTime = System.currentTimeMillis();

        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkOK();
                        listView.onRefreshComplete();
                        isRefreshRepair = false;
                        isRefreshInstall = false;
                    }
                }, (endTime - startTime) + 1000);
            }
        });
    }

    /**
     * 检查任务是否完成,完成则更新listView
     * (不关注是否有数据)
     */
    private void checkOK() {
        if (isRepairOK && isInstallOK) {
            allList.addAll(repairList);
            allList.addAll(installList);
            if (orderAdapter == null) {
                orderAdapter = new OrderAdapter(mContext, allList);
            }
            orderAdapter.setOrderState(true, false, false);
            listView.setAdapter(orderAdapter);
            orderAdapter.notifyDataSetChanged();
            orderAdapter.setScanListener(new OrderAdapter.ScanListener() {
                @Override
                public void onClick(View view, int position) {
                    RdApplication.get().setOrderMap(allList.get(position));
                    startActivity(new Intent().setClass(mContext, MipcaActivityCapture.class));
                }
            });
            orderAdapter.setAddListener(new OrderAdapter.AddListener() {
                @Override
                public void onClick(View view, final int position) {
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View product_list_view = inflater.inflate(R.layout.product_list_higway, null);
                    final EditText et_input = (EditText) product_list_view.findViewById(R.id.et_input);
                    new EditDialog.Builder(getActivity())
                            .setTitle("请输入设备ID")
                            .setContentView(product_list_view)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String pro_list = et_input.getText().toString().trim().toUpperCase();
                                    if ("".equals(pro_list)) {
                                        showOnUIToast("设备ID不能为空");
                                        return;
                                    }
                                    RdApplication.get().setOrderMap(allList.get(position));
                                    sp.edit()
                                            .putString("flag", "order_list")
                                            .putString("product_list", pro_list)
                                            .apply();
                                    startActivity(new Intent().setClass(mContext, HighwayUploadingOrSaveActivity.class));
                                    dialog.dismiss();
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
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    position = position - listView.getHeaderViewsCount();
                    Map<String, String> map = allList.get(position);
                    RdApplication.get().setOrderMap(map);
                    sp.edit().putString("order_message", "repairing").apply();
                    startActivity(new Intent().setClass(mContext, RepairingOrderInfoFragmentActivity.class));
                }
            });
            listView.setonRefreshListener(new MyRefreshListView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshData();//有数据时下拉刷新
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainAfterSaleFragmentActivity) {
            this.mContext = (MainAfterSaleFragmentActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshBroadcastReceiver = new RefreshBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(getString(R.string.refresh));
        mContext.registerReceiver(refreshBroadcastReceiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.repairingfragment, container, false);

        init(view);
        //获取数据
        getRepairList();//初始化获取维修单数据
        getInstallList();//初始化获取安装单数据
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(refreshBroadcastReceiver);
    }

    private void init(View view) {
        tv_show_repairMessage = (TextView) view.findViewById(R.id.tv_show_repairMessage);
        tv_show_installMessage = (TextView) view.findViewById(R.id.tv_show_installMessage);
        listView = (MyRefreshListView) view.findViewById(R.id.lv_repairing);
        allList = new ArrayList<>();
        repairList = new ArrayList<>();
        installList = new ArrayList<>();
        sp = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        //接口请求参数
        args.put("Account", RdApplication.get().getUser().getUser_account());
        args.put("UserId", RdApplication.get().getUser().getUser_id());
        method = "OrderInfo";
    }

    /**
     * 获取维修单数据源并添加到总数据源
     */
    public void getRepairList() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                new WrappedHttpUtil.Builder(mContext)
                        .dataRepairRequest(method, args)
                        .setL(new WrappedHttpUtil.Builder.Listener() {
                            @Override
                            public void onSuccess(int responseCode, String result) {
                                boolean isJson = JsonParse.isJson(result);
                                if (isJson) {
                                    String message = JsonParse.judgeJson(result);
                                    if ("0".equals(message)) {
                                        tv_show_repairMessage.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_show_repairMessage.setVisibility(View.GONE);
                                            }
                                        });
                                        isNoRepairData = true;
                                        mHandler.handleMessage(mHandler.obtainMessage(3));
                                    } else {
                                        if (repairList != null) repairList.clear();
                                        repairList = JsonParse.analysisRepairingList(message);
                                        mContext.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_show_repairMessage.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                    if (isRefreshRepair) {
                                        isRepairOK = true;
                                    } else {
                                        mHandler.handleMessage(mHandler.obtainMessage(1));
                                    }
                                } else {
                                    showOnThreadToast("维修单\r\n" + getResources().getString(R.string.mistake_dataResolve));
                                }
                            }

                            @Override
                            public void onFail(int responseCode, String result) {
                                getListException(tv_show_repairMessage, "获取维修单列表",
                                        responseCode, result);
                                mHandler.handleMessage(mHandler.obtainMessage(1));
                            }
                        }).execute();
            }
        }.start();


    }

    /**
     * 获取安装调试单数据源并添加到总数据源
     */
    public void getInstallList() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                new WrappedHttpUtil.Builder(mContext)
                        .dataInstallRequest(method, args)
                        .setL(new WrappedHttpUtil.Builder.Listener() {
                            @Override
                            public void onSuccess(int responseCode, String result) {
                                boolean isJson = JsonParse.isJson(result);
                                if (isJson) {
                                    String message = JsonParse.judgeJson(result);
                                    if ("0".equals(message)) {
                                        tv_show_installMessage.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_show_installMessage.setVisibility(View.GONE);
                                            }
                                        });
                                        isNoInstallData = true;
                                        mHandler.handleMessage(mHandler.obtainMessage(4));
                                    } else {
                                        if (installList != null) installList.clear();
                                        installList = JsonParse.analysisInstallList(message);
                                        mContext.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_show_installMessage.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                    if (isRefreshInstall) {
                                        isInstallOK = true;
                                    } else {
                                        mHandler.handleMessage(mHandler.obtainMessage(2));
                                    }

                                } else {
                                    showOnThreadToast("安装单\r\n" + getResources()
                                            .getString(R.string.mistake_dataResolve));
                                }
                            }

                            @Override
                            public void onFail(int responseCode, String result) {
                                getListException(tv_show_installMessage, "获取安装单列表",
                                        responseCode, result);
                                mHandler.handleMessage(mHandler.obtainMessage(2));
                            }

                        }).execute();
            }
        }.start();


    }

    public void showOnThreadToast(final String message) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.popToast(mContext, message);
            }
        });
    }

    public void showOnUIToast(String message) {
        MyToast.popToast(mContext, message);
    }


    /**
     * 控件异常提示
     *
     * @param textView      控件
     * @param interfaceType 接口类型
     * @param responseCode  返回码
     * @param result        返回信息
     */
    public void getListException(final TextView textView, final String interfaceType,
                                 int responseCode, String result) {
        if (responseCode == 0) {
            if ("123".equals(result)) {
                showOnThreadToast(getResources().getString(R.string.noNetwork));
            }
        }
        if (responseCode == 404) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(interfaceType + "\r\n" +
                            getResources().getString(R.string.mistake_404));
                }
            });
        }
        if (responseCode == 500) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(interfaceType + "\r\n" +
                            getResources().getString(R.string.mistake_500));
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("RepairingFragment保存信息", sp.getAll() + "");
    }

    /**
     * 接收广播，刷新数据
     */
    class RefreshBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if (intent.getAction().equals(getString(R.string.refresh))) {
                    refreshData();//抢单成功,创建临时维修单成功，完成订单后刷新数据
                }
            }
        }
    }
}
