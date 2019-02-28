package com.mapsoft.aftersale.aftersale.mainfragment.orderfragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mapsoft.aftersale.RdApplication;
import com.mapsoft.aftersale.utils.JsonParse;
import com.mapsoft.aftersale.utils.ListMapSerializable;


import com.mapsoft.aftersale.R;
import com.mapsoft.aftersale.utils.MyToast;
import com.mapsoft.aftersale.utils.WrappedHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/13.
 * 产品维修记录
 */

public class RepairRecordActivity extends Activity {
    private ListView listView;
    private List<Map<String, String>> repair_record;//维修记录
    private Map<String, String> faultInfo_map;//故障信息
    private SimpleAdapter adapter;
    private boolean hasFaultInfo;
    private boolean hasRecord;

    private View fault_head;//故障信息展示

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    runOnUiThread(new Runnable() {
                        @SuppressLint("InflateParams")
                        @Override
                        public void run() {
                            if (!hasFaultInfo && !hasRecord) {
                                //没有故障信息false、没有维修记录false
                                showOnUI("该设备没有维修记录");
                            } else if (hasFaultInfo && !hasRecord) {
                                //有故障信息true，没有维修记录false
                                adapter = new SimpleAdapter(
                                        RepairRecordActivity.this,
                                        repair_record,
                                        R.layout.fault_product_info,
                                        new String[]{
                                                "installContactListId",
                                                "addTime",
                                                "productCode",
                                                "productName",
                                                "faultCause",
                                                "maintResult",
                                                "remarks"
                                        },
                                        new int[]{
                                                R.id.tv_installContactListId,
                                                R.id.tv_addTime,
                                                R.id.tv_product_code,
                                                R.id.tv_product_name,
                                                R.id.tv_fault_cause,
                                                R.id.tv_result_maint,
                                                R.id.tv_remark
                                        }
                                );
                                showOnUI("该设备没有维修记录");
                            } else if (!hasFaultInfo && hasRecord) {
                                //没有故障信息false，有维修记录true
                                adapter = new SimpleAdapter(
                                        RepairRecordActivity.this,
                                        repair_record,
                                        R.layout.new_repairrecord_bus,
                                        new String[]{
                                                "order_code",
                                                "time_complete",
                                                "customer_address",
                                                "product_name",
                                                "car_number",
                                                "fault_cause",
                                                "result_maint",
                                                "cost_material",
                                                "cost_maintenance",
                                                "cost_all",
                                                "order_engineer",
                                                "maint_number"
                                        },
                                        new int[]{
                                                R.id.tv_order_code,
                                                R.id.tv_time_complete,
                                                R.id.tv_customer_address,
                                                R.id.tv_product_name,
                                                R.id.tv_car_number,
                                                R.id.tv_fault_cause,
                                                R.id.tv_result_maint,
                                                R.id.tv_cost_material,
                                                R.id.tv_cost_maintenance,
                                                R.id.tv_cost_all,
                                                R.id.tv_order_engineer,
                                                R.id.tv_maint_number,
                                        }
                                );
                            } else if (hasFaultInfo && hasRecord) {
                                //有故障信息和维修记录
                                fault_head = getLayoutInflater().inflate(R.layout.fault_product_info, null);
                                ((TextView) fault_head.findViewById(R.id.tv_installContactListId)).setText(faultInfo_map.get("installContactListId"));
                                ((TextView) fault_head.findViewById(R.id.tv_addTime)).setText(faultInfo_map.get("addTime"));
                                ((TextView) fault_head.findViewById(R.id.tv_product_code)).setText(faultInfo_map.get("productCode"));
                                ((TextView) fault_head.findViewById(R.id.tv_product_name)).setText(faultInfo_map.get("productName"));
                                ((TextView) fault_head.findViewById(R.id.tv_fault_cause)).setText(faultInfo_map.get("faultCause"));
                                ((TextView) fault_head.findViewById(R.id.tv_result_maint)).setText(faultInfo_map.get("maintResult"));
                                ((TextView) fault_head.findViewById(R.id.tv_remark)).setText(faultInfo_map.get("remarks"));
                                adapter = new SimpleAdapter(
                                        RepairRecordActivity.this,
                                        repair_record,
                                        R.layout.new_repairrecord_bus,
                                        new String[]{
                                                "order_code",
                                                "time_complete",
                                                "customer_address",
                                                "product_name",
                                                "car_number",
                                                "fault_cause",
                                                "result_maint",
                                                "cost_material",
                                                "cost_maintenance",
                                                "cost_all",
                                                "order_engineer",
                                                "maint_number"
                                        },
                                        new int[]{
                                                R.id.tv_order_code,
                                                R.id.tv_time_complete,
                                                R.id.tv_customer_address,
                                                R.id.tv_product_name,
                                                R.id.tv_car_number,
                                                R.id.tv_fault_cause,
                                                R.id.tv_result_maint,
                                                R.id.tv_cost_material,
                                                R.id.tv_cost_maintenance,
                                                R.id.tv_cost_all,
                                                R.id.tv_order_engineer,
                                                R.id.tv_maint_number,
                                        }
                                );
                                listView.addHeaderView(fault_head);
                            }
                            if (adapter != null)
                                listView.setAdapter(adapter);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repairrecord);
        String main_type = RdApplication.get().getOrderMap().get("main_type");
        repair_record = new ArrayList<>();
        faultInfo_map = new HashMap<>();
        findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        listView = (ListView) findViewById(R.id.lvRepairCord);
        Bundle bundle = getIntent().getExtras();
        ListMapSerializable listMapSerializable = (ListMapSerializable) bundle.getSerializable("repair_record");
        if (listMapSerializable != null)
            repair_record = listMapSerializable.getListMap();
        if ("0".equals(main_type)) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    String method = "Record";
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("ProductNumber", getIntent().getStringExtra("product_code"));
                    new WrappedHttpUtil.Builder(RepairRecordActivity.this)
                            .dataRepairRequest(method, params)
                            .setL(new WrappedHttpUtil.Builder.Listener() {
                                @Override
                                public void onSuccess(int responseCode, String result) {
                                    boolean isJson = JsonParse.isJson(result);
                                    if (isJson) {
                                        try {
                                            JSONArray array = new JSONArray(result);
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject jsonObject = array.getJSONObject(i);
                                                if (i == 0) {
                                                    String fault_message = jsonObject.getString("FaultProuctInfo");
                                                    if (!"0".equals(fault_message)) {
                                                        JSONArray jsonArray = new JSONArray(fault_message);
                                                        for (int j = 0; j < jsonArray.length(); j++) {
                                                            JSONObject faultJson = jsonArray.getJSONObject(j);
                                                            faultInfo_map.put("maintResult", faultJson.getString("MaintResult"));
                                                            faultInfo_map.put("productCode", faultJson.getString("ProductCode"));
                                                            faultInfo_map.put("faultCause", faultJson.getString("FaultCause"));
                                                            faultInfo_map.put("remarks", faultJson.getString("Remarks"));
                                                            faultInfo_map.put("installContactListId", faultJson.getString("InstallContactListId"));
                                                            faultInfo_map.put("productName", faultJson.getString("ProductType") + faultJson.getString("ProductName"));
                                                            faultInfo_map.put("addTime", faultJson.getString("AddTime"));
                                                            hasFaultInfo = true;
                                                        }
                                                    }
                                                }
                                                if (i == 1) {
                                                    String record_message = jsonObject.getString("result");
                                                    if (!"0".equals(record_message)) {
                                                        repair_record = JsonParse.analysisProductRepairRecordByBus(record_message);
                                                        hasRecord = true;
                                                    }
                                                }
                                            }
                                            mHandler.sendMessage(mHandler.obtainMessage(1));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        showOnThread("产品维修记录\r\n" + getResources().getString(R.string.mistake_dataResolve));
                                    }
                                }

                                @Override
                                public void onFail(int responseCode, String result) {
                                    ExceptionMessage("产品维修记录", responseCode, result);
                                }
                            }).execute();
                }
            }.start();
        } else if ("1".equals(main_type)) {
            adapter = new SimpleAdapter(
                    this,
                    repair_record,
                    R.layout.new_repairrecord_higway,
                    new String[]{
                            "order_code",//
                            "time_complete",//
                            "customer_address",//
                            "product_name",//
                            "car_number",//
                            "fault_cause",//
                            "result_maint",//
                            "product_sim",
                            "install_address",
                            "install_time",
                            "order_engineer",//
                            "maint_number"//
                    },
                    new int[]{
                            R.id.tv_order_code,
                            R.id.tv_time_complete,
                            R.id.tv_customer_address,
                            R.id.tv_product_name,
                            R.id.tv_car_number,
                            R.id.tv_fault_cause,
                            R.id.tv_result_maint,
                            R.id.tv_product_sim,
                            R.id.tv_install_address,
                            R.id.tv_install_time,
                            R.id.tv_order_engineer,
                            R.id.tv_maint_number,
                    }
            );
            listView.setAdapter(adapter);
        }
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
        if (responseCode == 400) {
            showOnThread(interfaceType + "\r\n" +
                    getResources().getString(R.string.mistake_400));
        }
    }


    public void showOnThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.popToast(RepairRecordActivity.this, message);
            }
        });
    }

    public void showOnUI(String message) {
        MyToast.popToast(RepairRecordActivity.this, message);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
