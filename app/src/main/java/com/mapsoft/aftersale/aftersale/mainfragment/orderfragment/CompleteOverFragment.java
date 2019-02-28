package com.mapsoft.aftersale.aftersale.mainfragment.orderfragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mapsoft.aftersale.R;
import com.mapsoft.aftersale.RdApplication;
import com.mapsoft.aftersale.aftersale.mainfragment.MainAfterSaleFragmentActivity;
import com.mapsoft.aftersale.bean.User;
import com.mapsoft.aftersale.utils.JsonParse;
import com.mapsoft.aftersale.utils.MapSerializable;
import com.mapsoft.aftersale.utils.MyToast;
import com.mapsoft.aftersale.utils.NoDoubleClickListener;
import com.mapsoft.aftersale.utils.OrderAdapter;
import com.mapsoft.aftersale.utils.WrappedHttpUtil;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/24.
 * 订单--历史维修：查看已完成的订单
 */

public class CompleteOverFragment extends Fragment {
    private TextView tv_first_day, tv_last_day, tv_repair_over, tv_install_over;
    private Button btn_seek;
    private ListView lv_completeOver;
    //    private SimpleAdapter adapter;
    private OrderAdapter orderAdapter;


    private Calendar c;
    private int year = 0;
    private int month = 0;
    private int dayOfMonth = 0;

    private List<Map<String, String>> repairOverList;
    private List<Map<String, String>> installOverList;
    private List<Map<String, String>> allOverList;
    private SharedPreferences sp;

    private transient boolean isRepairCompleteOK, isInstallCompleteOK;

    private boolean isNoRepairRecord;
    private boolean isNoInstallRecord;
    private MainAfterSaleFragmentActivity activity;

    private String user_account;
    private String user_id;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
//                case 1:
//                    isRepairCompleteOK = true;
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            checkOK();
//                        }
//                    });
//                    break;
//                case 2:
//                    isInstallCompleteOK = true;
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            checkOK();
//                        }
//                    });
//                    break;
//                case 3:
//                    isNoRepairRecord = true;
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            noRecordOK();
//                        }
//                    });
//                    break;
//                case 4:
//                    isNoInstallRecord = true;
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            noRecordOK();
//                        }
//                    });
//                    break;
                case 5:
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            checkData();
                            mHandler.sendMessage(mHandler.obtainMessage(6));
                        }
                    });
                    break;
                case 6:
                    getInstallOverList(
                            user_account,
                            user_id,
                            sp.getString("first_day", ""),
                            sp.getString("last_day", "")
                    );
//                    Log.e("运行？", "123");

//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            checkData();
//                        }
//                    });
                    break;
                case 7:
                    activity.runOnUiThread(new Runnable() {
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
     * 检查数据和更新视图
     */
    private void checkData() {
        btn_seek.setVisibility(View.VISIBLE);
        if (isNoRepairRecord && isNoInstallRecord && allOverList.size() == 0) {
            showOnUI("该时间段内没有记录");
            isNoInstallRecord = false;
            isNoRepairRecord = false;
            return;
        }
        if (isRepairCompleteOK && isInstallCompleteOK && allOverList.size() != 0
                && repairOverList.size() == 0 && installOverList.size() == 0) {
            //数据展示
            Log.e("已完成订单数据集： ", allOverList.toString());
            if (orderAdapter == null) {
                orderAdapter = new OrderAdapter(activity, allOverList);
            }
            orderAdapter.setOrderState(false, false, true);
            lv_completeOver.setAdapter(orderAdapter);
            lv_completeOver.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    RdApplication.get().setOrderMap(allOverList.get(position));
                    sp.edit().putString("order_message", "complete").apply();
                    startActivity(new Intent().setClass(activity, RepairingOrderInfoFragmentActivity.class));
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainAfterSaleFragmentActivity)
            this.activity = (MainAfterSaleFragmentActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User user = activity.getUser();
        user_account = user.getUser_account();
        user_id = user.getUser_id();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.completeoverfragment, container, false);
        init(view);
        /*设置开始时间*/
        final DatePickerDialog firstDialog = new DatePickerDialog(activity, R.style.DateTime,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tv_first_day.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                }, year, month, dayOfMonth);
        tv_first_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tv_repair_over.setVisibility(View.VISIBLE);
                tv_repair_over.setText("");
                tv_install_over.setVisibility(View.VISIBLE);
                tv_install_over.setText("");

                firstDialog.show();
            }
        });
        /**
         * 截至时间设置
         */
        final DatePickerDialog lastDialog = new DatePickerDialog(activity, R.style.DateTime,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tv_last_day.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                }, year, month, dayOfMonth);

        tv_last_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tv_repair_over.setVisibility(View.VISIBLE);
                tv_repair_over.setText("");
                tv_install_over.setVisibility(View.VISIBLE);
                tv_install_over.setText("");

                lastDialog.show();
            }
        });

        btn_seek.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                btn_seek.setVisibility(View.GONE);
                isRepairCompleteOK = false;
                isInstallCompleteOK = false;
                isNoRepairRecord = false;
                isNoInstallRecord = false;
                tv_repair_over.setVisibility(View.VISIBLE);
                tv_install_over.setVisibility(View.VISIBLE);
                tv_repair_over.setText("正在获取历史维修记录");
                tv_install_over.setText("正在获取历史安装调试记录");
                allOverList.clear();
                String first_day = tv_first_day.getText().toString().trim();
                String last_day = tv_last_day.getText().toString().trim();
                if ("".equals(first_day) && "".equals(last_day)) {
                    first_day = "0";
                    last_day = "0";
                } else if ("".equals(last_day)) {
                    last_day = c.get(Calendar.YEAR) + "-"
                            + (c.get(Calendar.MONTH) + 1) + "-"
                            + c.get(Calendar.DAY_OF_MONTH);
                } else if ("".equals(first_day) && !"".equals(last_day)) {
                    showOnUI("开始日期不能为空");
                    return;
                }
                Log.e("日期", first_day + "~~~~" + last_day);
                sp.edit().putString("first_day", first_day).putString("last_day", last_day).apply();
                getRepairOverList(user_account, user_id,
                        first_day, last_day);
            }
        });
        return view;
    }

    private void init(View view) {
        installOverList = new ArrayList<Map<String, String>>();
        repairOverList = new ArrayList<Map<String, String>>();
        tv_first_day = (TextView) view.findViewById(R.id.tv_first_day);
        tv_last_day = (TextView) view.findViewById(R.id.tv_last_day);
        btn_seek = (Button) view.findViewById(R.id.btn_seek);
        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        lv_completeOver = (ListView) view.findViewById(R.id.lv_completeOver);
        sp = activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE);
        tv_repair_over = (TextView) view.findViewById(R.id.tv_repair_over);
        tv_install_over = (TextView) view.findViewById(R.id.tv_install_over);
        allOverList = new ArrayList<>();
        tv_first_day.setText(year + "-" + (month + 1) + "-" + c.getActualMinimum(Calendar.DAY_OF_MONTH));
        tv_last_day.setText(year + "-" + (month + 1) + "-" + c.getActualMaximum(Calendar.DAY_OF_MONTH));

    }

    /**
     * 获取历史维修订单
     *
     * @param user_account
     * @param user_id
     * @param first_day
     * @param last_day
     */
    public void getRepairOverList(final String user_account, final String user_id, final String first_day, final String last_day) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String method = "OldOrder";
                Map<String, String> params = new HashMap<String, String>();
                params.put("Account", user_account);
                params.put("UserId", user_id);
                params.put("StartTime", first_day);
                params.put("EndTime", last_day);
                new WrappedHttpUtil.Builder(getActivity())
                        .dataRepairRequest(method, params)
                        .setL(new WrappedHttpUtil.Builder.Listener() {
                            @Override
                            public void onSuccess(int responseCode, String result) {
                                boolean isJson = JsonParse.isJson(result);
                                if (isJson) {
                                    String message = JsonParse.judgeJson(result);
                                    if ("0".equals(message)) {
                                        tv_repair_over.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_repair_over.setVisibility(View.GONE);
                                            }
                                        });
                                        isNoRepairRecord = true;
                                    } else {
                                        tv_repair_over.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_repair_over.setVisibility(View.GONE);
                                            }
                                        });
                                        repairOverList = JsonParse.analysisHistoryRepairOrder(message);
                                        Log.e("改变allOverList", "");
                                        allOverList.addAll(repairOverList);
                                        repairOverList.clear();

                                    }
                                } else {
                                    tv_repair_over.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_repair_over.setText("历史维修单\r\n" +
                                                    getActivity().getResources()
                                                            .getString(R.string.mistake_dataResolve));
                                        }
                                    });
                                }
                                isRepairCompleteOK = true;
//                                mHandler.handleMessage(mHandler.obtainMessage(1));
                                mHandler.handleMessage(mHandler.obtainMessage(5));
                            }

                            @Override
                            public void onFail(int responseCode, String result) {
                                getListException(tv_repair_over, "历史维修单", responseCode, result);
//                                mHandler.handleMessage(mHandler.obtainMessage(1));
                                mHandler.handleMessage(mHandler.obtainMessage(5));
                            }
                        }).execute();
            }
        }.start();
    }

    /**
     * 获取历史安装调试单
     *
     * @param user_account
     * @param user_id
     * @param first_day
     * @param last_day
     */
    public void getInstallOverList(final String user_account, final String user_id, final String first_day, final String last_day) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                String method = "OldOrder";
                Map<String, String> params = new HashMap<String, String>();
                params.put("Account", user_account);
                params.put("UserId", user_id);
                params.put("FirstDay", first_day);
                params.put("LastDay", last_day);

                new WrappedHttpUtil.Builder(getActivity())
                        .dataInstallRequest(method, params)
                        .setL(new WrappedHttpUtil.Builder.Listener() {
                            @Override
                            public void onSuccess(int responseCode, String result) {
                                boolean isJson = JsonParse.isJson(result);
                                if (isJson) {
                                    String message = JsonParse.judgeJson(result);
                                    if ("0".equals(message)) {
                                        tv_install_over.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_install_over.setVisibility(View.GONE);
                                            }
                                        });
//                                        mHandler.handleMessage(mHandler.obtainMessage(4));
                                        isNoInstallRecord = true;
                                    } else {
                                        tv_install_over.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                tv_install_over.setVisibility(View.GONE);
                                            }
                                        });

                                        installOverList = JsonParse.analysisHistoryInstallOrder(message);
                                        Log.e("改变allOverList", "");
                                        allOverList.addAll(installOverList);
                                        installOverList.clear();

                                    }
                                } else {
                                    tv_install_over.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv_install_over.setText("历史安装调试单\r\n" +
                                                    getActivity().getResources()
                                                            .getString(R.string.mistake_dataResolve));
                                        }
                                    });
                                }
                                isInstallCompleteOK = true;
//                                mHandler.handleMessage(mHandler.obtainMessage(2));
                                mHandler.handleMessage(mHandler.obtainMessage(7));
                            }

                            @Override
                            public void onFail(int responseCode, String result) {
                                getListException(tv_install_over, "历史安装单", responseCode, result);
//                                mHandler.handleMessage(mHandler.obtainMessage(2));
                                mHandler.handleMessage(mHandler.obtainMessage(7));
                            }
                        }).execute();
            }
        }.start();
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
                showOnThread(getResources().getString(R.string.noNetwork));
            }
        }
        if (responseCode == 404) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(interfaceType + "\r\n" +
                            getResources().getString(R.string.mistake_404));
                }
            });
        }
        if (responseCode == 500) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(interfaceType + "\r\n" +
                            getResources().getString(R.string.mistake_500));
                }
            });
        }
    }


    public void showOnThread(final String message) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.popToast(activity, message);
            }
        });
    }

    public void showOnUI(String message) {
        MyToast.popToast(activity, message);
    }
}
