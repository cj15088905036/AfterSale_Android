package com.mapsoft.aftersale.aftersale.mainfragment.messagefragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mapsoft.aftersale.R;
import com.mapsoft.aftersale.RdApplication;
import com.mapsoft.aftersale.aftersale.mainfragment.MainAfterSaleFragmentActivity;
import com.mapsoft.aftersale.aftersale.mainfragment.orderfragment.RepairingOrderInfoFragmentActivity;
import com.mapsoft.aftersale.bean.User;
import com.mapsoft.aftersale.utils.EditDialog;
import com.mapsoft.aftersale.utils.JsonParse;
import com.mapsoft.aftersale.utils.MapSerializable;
import com.mapsoft.aftersale.utils.MyRefreshListView;
import com.mapsoft.aftersale.utils.MyToast;
import com.mapsoft.aftersale.utils.OrderAdapter;
import com.mapsoft.aftersale.utils.WrappedHttpUtil;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/5.
 */

public class GrabFragment extends Fragment {
    private TextView tv_grab_repair, tv_grab_install, tv_no_data;
    private SharedPreferences sp;
    List<Map<String, String>> grab_repairList;
    List<Map<String, String>> grab_installList;
    List<Map<String, String>> grab_allList;

    private boolean
            isGrabInstall,      //获取安装抢单数据
            isGrabRepair,       //获取维修抢单数据
            isNoGrabRepair,     //没有维修抢单数据
            isNoGrabInstall,    //没有安装抢单数据
            isRefreshRepair,    //刷新维修抢单数据
            isRefreshInstall;   //刷新安装抢单数据

    private MainAfterSaleFragmentActivity mContext;

    //    SimpleAdapter adapter;
    private OrderAdapter orderAdapter;

    private MyRefreshListView lv_grab_all;
    private String user_account = "";
    private String user_id = "";
    private String user_name = "";

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isGrabRepair = true;
                            checkOK();
                        }
                    });
                    break;
                case 2:
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isGrabInstall = true;
                            checkOK();
                        }
                    });
                    break;
                case 3:
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isNoGrabRepair = true;
                            checkNODate();
                        }
                    });
                    break;
                case 4:
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isNoGrabInstall = true;
                            checkNODate();
                        }
                    });
                    break;

                default:
                    break;
            }
        }
    };


    /**
     * 没有任何数据
     */
    private void checkNODate() {
        if (isNoGrabRepair && isNoGrabInstall && grab_allList.size() == 0) {
//            showOnUI("暂时没有任何未派单数据");
            isNoGrabRepair = false;
            isNoGrabInstall = false;
            tv_no_data.setVisibility(View.VISIBLE);
            if (orderAdapter != null) orderAdapter.notifyDataSetChanged();
            lv_grab_all.setonRefreshListener(new MyRefreshListView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    tv_no_data.setVisibility(View.GONE);
                    refreshData();//没有数据时
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
        if (grab_allList != null && grab_allList.size() != 0) {
            grab_allList.clear();
        }
        long startTime = System.currentTimeMillis();
        String method = "NewOrderInfo";
        Map<String, String> params = new HashMap<String, String>();
        params.put("Account", user_account);
        params.put("UserId", user_id);
        //获取未派单的维修单
        getRepairData(method, params);
        //获取未派单的安装调试单
        getInstallData(method, params);
        long endTime = System.currentTimeMillis();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkOK();
                lv_grab_all.onRefreshComplete();
                isRefreshRepair = false;
                isRefreshInstall = false;
            }
        }, endTime - startTime + 1000);
    }


    /**
     * 展示数据
     */
    private void checkOK() {
        if (isGrabRepair && isGrabInstall) {
//        if (isGrabRepair && isGrabInstall && grab_allList.size() != 0) {
//            Log.e("抢单数据",grab_allList.toString());
            if (orderAdapter == null) {
                orderAdapter = new OrderAdapter(mContext, grab_allList);
            }
            orderAdapter.setOrderState(false, true, false);
            lv_grab_all.setAdapter(orderAdapter);
            orderAdapter.notifyDataSetChanged();
            orderAdapter.setGrabListener(new OrderAdapter.GrabListener() {
                @Override
                public void onClick(View view, final int position) {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LayoutInflater inflater = mContext.getLayoutInflater();
                            View v = inflater.inflate(R.layout.nullmessage, null);
                            new EditDialog.Builder(mContext)
                                    .setTitle("抢单")
                                    .setContentView(v)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(final DialogInterface dialog, int which) {
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    super.run();
                                                    String method = "RobOrder";
                                                    if ("W".equals(grab_allList.get(position).get("order_type"))) {
                                                        //维修单抢单
                                                        Map<String, String> params = new HashMap<String, String>();
                                                        params.put("UserName", user_name);
                                                        params.put("OrderCode", grab_allList.get(position).get("order_code"));
                                                        new WrappedHttpUtil.Builder(mContext)
                                                                .dataRepairRequest(method, params)
                                                                .setL(new WrappedHttpUtil.Builder.Listener() {
                                                                    @Override
                                                                    public void onSuccess(int responseCode, String result) {
                                                                        boolean isJson = JsonParse.isJson(result);
                                                                        if (isJson) {
                                                                            String message = JsonParse.judgeJson(result);
                                                                            if ("1".equals(message)) {
                                                                                showOnThread("抢单成功");
                                                                                mContext.runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        mContext.sendBroadcast(new Intent(getString(R.string.refresh)));
                                                                                        dialog.dismiss();
                                                                                        grab_allList.remove(position);
                                                                                        orderAdapter.notifyDataSetChanged();
                                                                                    }
                                                                                });
                                                                            } else if ("0".equals(message)) {
                                                                                showOnThread("抢单失败,该订单不存在或已被抢");
                                                                                mContext.runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        dialog.dismiss();
                                                                                        grab_allList.remove(position);
                                                                                        orderAdapter.notifyDataSetChanged();
                                                                                    }
                                                                                });
                                                                            } else {
                                                                                showOnThread(mContext.getResources().getString(R.string.mistake_unknown));
                                                                            }
                                                                        } else {
                                                                            showOnThread(mContext.getResources().getString(R.string.mistake_dataResolve));
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFail(int responseCode, String result) {
                                                                        ExceptionMessage("维修单抢单", responseCode, result);
                                                                    }
                                                                }).execute();
                                                    } else {
                                                        //安装单抢单
                                                        Map<String, String> params = new HashMap<String, String>();
                                                        params.put("UserName", user_name);
                                                        params.put("OrderId", grab_allList.get(position).get("order_code"));
                                                        new WrappedHttpUtil.Builder(mContext)
                                                                .dataInstallRequest(method, params)
                                                                .setL(new WrappedHttpUtil.Builder.Listener() {
                                                                    @Override
                                                                    public void onSuccess(int responseCode, String result) {
                                                                        boolean isJson = JsonParse.isJson(result);
                                                                        if (isJson) {
                                                                            String message = JsonParse.judgeJson(result);
                                                                            if ("1".equals(message)) {
                                                                                showOnThread("抢单成功");
                                                                                mContext.runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        mContext.sendBroadcast(new Intent(getString(R.string.refresh)));
                                                                                        dialog.dismiss();
                                                                                        grab_allList.remove(position);
                                                                                        orderAdapter.notifyDataSetChanged();
                                                                                    }
                                                                                });
                                                                            } else if ("0".equals(message)) {
                                                                                showOnThread("抢单失败,该订单不存在或已被抢");
                                                                                mContext.runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        dialog.dismiss();
                                                                                        grab_allList.remove(position);
                                                                                        orderAdapter.notifyDataSetChanged();
                                                                                    }
                                                                                });
                                                                            } else {
                                                                                showOnThread(mContext.getResources().getString(R.string.mistake_unknown));
                                                                            }
                                                                        } else {
                                                                            showOnThread(mContext.getResources().getString(R.string.mistake_dataResolve));
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onFail(int responseCode, String result) {
                                                                        ExceptionMessage("安装单抢单", responseCode, result);
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
                                            orderAdapter.notifyDataSetChanged();
                                        }
                                    })
                                    .create().show();
                        }
                    });
                }
            });
            lv_grab_all.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    position = position - lv_grab_all.getHeaderViewsCount();
                    Map<String, String> map = grab_allList.get(position);
                    RdApplication.get().setOrderMap(map);
                    sp.edit().putString("order_message", "grab").apply();
                    startActivity(new Intent(mContext, RepairingOrderInfoFragmentActivity.class));
                }
            });

            lv_grab_all.setonRefreshListener(new MyRefreshListView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshData();//有数据时
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
        User user = mContext.getUser();
        user_account = user.getUser_account();
        user_id = user.getUser_id();
        user_name = user.getUser_name();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_grab_message, container, false);
        init(view);
        String method = "NewOrderInfo";
        Map<String, String> params = new HashMap<String, String>();
        params.put("Account", user_account);
        params.put("UserId", user_id);
        //获取未派单的维修单
        getRepairData(method, params);
        //获取未派单的安装调试单
        getInstallData(method, params);
        return view;
    }

    /**
     * 获取未派维修单的维修单
     *
     * @param method 请求方法名称
     * @param params 请求参数
     */
    private void getRepairData(final String method, final Map<String, String> params) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                new WrappedHttpUtil.Builder(mContext)
                        .dataRepairRequest(method, params)
                        .setL(new WrappedHttpUtil.Builder.Listener() {
                            @Override
                            public void onSuccess(int responseCode, String result) {
                                boolean isJson = JsonParse.isJson(result);
                                if (isJson) {
                                    String message = JsonParse.judgeJson(result);
                                    if ("0".equals(message)) {
                                        tv_grab_repair.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_grab_repair.setVisibility(View.GONE);
                                            }
                                        });
                                        mHandler.handleMessage(mHandler.obtainMessage(3));
                                    } else {
                                        grab_repairList = JsonParse.analysisRepairGrab(message);
                                        Log.e("公交，未派单的维修单", grab_repairList.toString());
                                        grab_allList.addAll(grab_repairList);
                                        tv_grab_repair.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_grab_repair.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                } else {
                                    tv_grab_repair.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_grab_repair.setText("未派维修单\r\n" +
                                                    mContext.getResources()
                                                            .getString(R.string.mistake_dataResolve) + "");
                                        }
                                    });
                                }
                                if (isRefreshRepair) {
                                    isGrabRepair = true;
                                } else {
                                    mHandler.handleMessage(mHandler.obtainMessage(1));
                                }
                            }

                            @Override
                            public void onFail(int responseCode, String result) {
                                getListException(tv_grab_repair, "未派维修单", responseCode, result);
                                mHandler.handleMessage(mHandler.obtainMessage(1));
                            }

                        }).execute();
            }
        }.start();

    }

    /**
     * 获取未派安装单的维修单
     *
     * @param method 请求方法名称
     * @param params 请求参数
     */
    private void getInstallData(final String method, final Map<String, String> params) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                new WrappedHttpUtil.Builder(mContext)
                        .dataInstallRequest(method, params)
                        .setL(new WrappedHttpUtil.Builder.Listener() {
                            @Override
                            public void onSuccess(int responseCode, String result) {
                                boolean isJson = JsonParse.isJson(result);
                                if (isJson) {
                                    String message = JsonParse.judgeJson(result);
                                    if ("0".equals(message)) {
                                        tv_grab_install.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_grab_install.setVisibility(View.GONE);
                                            }
                                        });
                                        mHandler.handleMessage(mHandler.obtainMessage(4));
                                    } else {
                                        grab_installList = JsonParse.analysisInstallGrab(message);
                                        grab_allList.addAll(grab_installList);
                                        tv_grab_install.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_grab_install.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                } else {
                                    tv_grab_install.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_grab_install.setText("未派安装单\r\n" +
                                                    mContext.getResources()
                                                            .getString(R.string.mistake_dataResolve) + "");
                                        }
                                    });
                                }
                                if (isRefreshInstall) {
                                    isGrabInstall = true;
                                } else {
                                    mHandler.handleMessage(mHandler.obtainMessage(2));
                                }
                            }

                            @Override
                            public void onFail(int responseCode, String result) {
                                getListException(tv_grab_install, "未派安装单", responseCode, result);
                                mHandler.handleMessage(mHandler.obtainMessage(2));
                            }

                        }).execute();
            }
        }.start();

    }

    private void init(View view) {
        lv_grab_all = (MyRefreshListView) view.findViewById(R.id.lv_grab_all);
        tv_grab_repair = (TextView) view.findViewById(R.id.tv_grab_repair);
        tv_grab_install = (TextView) view.findViewById(R.id.tv_grab_install);
        tv_no_data = (TextView) view.findViewById(R.id.tv_no_data);
        sp = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        grab_allList = new ArrayList<>();

    }


    public void showOnThread(final String message) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.popToast(mContext, message);
            }
        });
    }

    public void showOnUI(String message) {
        MyToast.popToast(mContext, message);
    }


    /**
     * Toast异常提示
     *
     * @param interfaceType 接口类型
     * @param responseCode  返回码
     * @param result        返回结果
     */
    public void ExceptionMessage(String interfaceType, int responseCode, String result) {
        if (responseCode == 0) {
            if ("123".equals(result)) {
                showOnThread(mContext.getResources().getString(R.string.noNetwork));
            }
        }
        if (responseCode == 404) {
            showOnThread(interfaceType + "\r\n" +
                    mContext.getResources().getString(R.string.mistake_404));
        }
        if (responseCode == 500) {
            showOnThread(interfaceType + "\r\n" +
                    mContext.getResources().getString(R.string.mistake_500));
        }
    }

    /**
     * 获取订单列表异常提示
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
                showOnThread(mContext.getResources().getString(R.string.noNetwork));
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
        String grab_tag = sp.getString("grab_tag", "");
        Map<String, String> orderMap = RdApplication.get().getOrderMap();
        if (!TextUtils.isEmpty(grab_tag) && orderMap != null) {
            if ("1".equals(grab_tag)) {//抢单成功
                mContext.sendBroadcast(new Intent(getString(R.string.refresh)));
                for (int i = 0; i < grab_allList.size(); i++) {
                    if (orderMap.get("order_code").equals(grab_allList.get(i).get("order_code"))) {
                        grab_allList.remove(i);
                    }
                }
            }
            sp.edit().remove("grab_tag").apply();
            orderAdapter.notifyDataSetChanged();
        }
    }
}
