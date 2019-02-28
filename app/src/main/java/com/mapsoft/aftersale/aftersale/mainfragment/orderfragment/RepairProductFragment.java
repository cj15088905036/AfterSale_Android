package com.mapsoft.aftersale.aftersale.mainfragment.orderfragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.mapsoft.aftersale.R;
import com.mapsoft.aftersale.RdApplication;
import com.mapsoft.aftersale.bean.BusProductTable;
import com.mapsoft.aftersale.bean.HigwayProductTable;
import com.mapsoft.aftersale.bean.User;
import com.mapsoft.aftersale.utils.EditDialog;
import com.mapsoft.aftersale.utils.JsonParse;
import com.mapsoft.aftersale.utils.MapSerializable;
import com.mapsoft.aftersale.utils.MyToast;
import com.mapsoft.aftersale.utils.WrappedHttpUtil;

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
 * 正在维修--维修设备(安装备注)
 */

public class RepairProductFragment extends Fragment {
    private static final String TAG = "RepairProductFragment";
    private SQLiteDatabase db;
    private SharedPreferences sp;
    private List<Map<String, String>> repairOverList;//完成订单后的List
    private ListView lv_specific_repair;
    private SimpleAdapter adapter;

    private LinearLayout
            ll_seek_plateNumbers,//搜索车牌号
            ll_remark;//订单备注

    private TextView tv_install_remark;
    private Button
            btn_seek_veh,
            btn_install_remark;
    private EditText et_veh_code;

    private String type_code = "";
    private String main_type;//0公交，1公路
    private Map<String, String> orderMap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.repair_product_fragment, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        db = RdApplication.getDatabase();
        orderMap = RdApplication.get().getOrderMap();
        main_type = orderMap.get("main_type");
        sp = getActivity().getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE);
        repairOverList = new ArrayList<>();
        lv_specific_repair = (ListView) view.findViewById(R.id.lv_specific_repair);
        ll_remark = (LinearLayout) view.findViewById(R.id.rl_remark);
        ll_seek_plateNumbers = (LinearLayout) view.findViewById(R.id.ll_seek_plateNumbers);
        tv_install_remark = (TextView) view.findViewById(R.id.tv_install_remark);
        btn_install_remark = (Button) view.findViewById(R.id.btn_install_remark);
        btn_seek_veh = (Button) view.findViewById(R.id.btn_seek_veh);
        et_veh_code = (EditText) view.findViewById(R.id.et_veh_code);
        type_code = orderMap.get("order_type").trim() + orderMap.get("order_code");

    }

    public void showOnUI(String message) {
        MyToast.popToast(getActivity(), message);
    }

    public void showTreadToast(final String message) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.popToast(getActivity(), message);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //        testDialog(order_message);
        String order_message = sp.getString("order_message", "");
        Log.e(TAG, sp.getAll().toString() + "");
        if ("uploading".equals(order_message) || "repairing".equals(order_message)) {
            final String order_code = orderMap.get("order_code");
            if ("T".equals(orderMap.get("order_type"))) {
                //公路的安装备注和保存在数据库的资料同时显示，公交只显示安装备注
                tv_install_remark.setText(sp.getString(order_code, ""));
                btn_install_remark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final View view = getActivity().getLayoutInflater().inflate(R.layout.remark_edit, null);
                        final EditText et_remark = (EditText) view.findViewById(R.id.et_remark);
                        et_remark.setText(tv_install_remark.getText().toString().trim());
                        new EditDialog.Builder(getActivity())
                                .setTitle("添加/修改备注")
                                .setContentView(view)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String install_remark = et_remark.getText().toString().trim();
                                        tv_install_remark.setText(install_remark);
                                        sp.edit().putString(order_code, install_remark).apply();
                                        showOnUI("保存成功");
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                    }
                });
                if ("1".equals(main_type)) {
                    List<HigwayProductTable> productTables = DataSupport
                            .select("*")
                            .where("type_code = ?", type_code)
                            .find(HigwayProductTable.class);
                    showHigWaySQLDataRepairOrInstall(productTables);//公路安装调试单
                } else if ("0".equals(main_type)) {
                    ll_seek_plateNumbers.setVisibility(View.GONE);
                    showBusProductSQLDataRepairOrInstall();//安装单
                }
                /**
                 * 根据车牌号搜索保存在本地数据库里面的该车辆安装设备信息，
                 *
                 * */
                btn_seek_veh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String veh_code = et_veh_code.getText().toString().trim();
                        if ("".equals(veh_code)) {
                            //如果输入的车牌号为空，则显示该订单下所有安装的设备信息
                            List<HigwayProductTable> productTables = DataSupport
                                    .select("*")
                                    .where("type_code = ?", type_code)
                                    .find(HigwayProductTable.class);
                            if (productTables.size() != 0) {
                                showHigWaySQLDataRepairOrInstall(productTables);//显示所有安装设备
                            } else {
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            List<HigwayProductTable> productTables = DataSupport
                                    .select("*")
                                    .where("type_code = ? and vehCode = ?", type_code, veh_code)
                                    .find(HigwayProductTable.class);
                            if (productTables.size() != 0) {
                                showHigWaySQLDataRepairOrInstall(productTables);//显示与车牌号有关的数据
                            } else {
                                showOnUI("暂无该车牌的安装记录");
                            }
                        }
                    }
                });
            } else if ("W".equals(orderMap.get("order_type"))) {
                ll_remark.setVisibility(View.GONE);
                ll_seek_plateNumbers.setVisibility(View.GONE);
                //维修中界面和上传界面传值
                if (db != null) {
                    if ("0".equals(main_type)) {
                        //获取订单下具体维修的产品信息(不同手机)
                        /**
                         * 获取订单内具体维修产品信息：
                         *  1、保存集合，然后和本地数据库里面的数据进行判断，
                         *  若数据库里有数据，则不处理，若没有则将服务器获取的数据添加到数据库中并展示在界面中
                         */
                        showBusProductSQLDataRepairOrInstall();//维修单
                    } else if ("1".equals(main_type)) {
                        List<HigwayProductTable> productTables = DataSupport
                                .select("*")
                                .where("type_code = ?", type_code)
                                .find(HigwayProductTable.class);
                        showHigWaySQLDataRepairOrInstall(productTables);//公路维修单
                    }
                } else {
                    showOnUI(getActivity().getResources().getString(R.string.sql_database_null));
                }
            }
        } else if ("complete".equals(order_message)) {
            //已完成界面传值
            ll_seek_plateNumbers.setVisibility(View.GONE);
            if ("T".equals(orderMap.get("order_type"))) {
                tv_install_remark.setText(orderMap.get("order_remark"));
                btn_install_remark.setVisibility(View.GONE);
            } else if ("W".equals(orderMap.get("order_type"))) {
                ll_remark.setVisibility(View.GONE);
                final String product_info = orderMap.get("product_info");
                try {
                    JSONArray ja = new JSONArray(product_info);
                    for (int i = 0; i < ja.length(); i++) {
                        if ("1".equals(main_type)) {
                            //公路
                            Map<String, String> repair_over_map = new HashMap<String, String>();
                            JSONObject js = ja.getJSONObject(i);
                            repair_over_map.put("product_name", js.getString("product_name"));//产品名称
                            repair_over_map.put("product_number", js.getString("product_id"));//产品序列号
                            repair_over_map.put("car_number", js.getString("vehcode"));//车牌号
                            repair_over_map.put("product_sim", js.getString("product_sim"));//SIM卡号
                            repair_over_map.put("fault_description", js.getString("faultcause"));//故障原因
                            repair_over_map.put("result_maint", js.getString("maintresult"));//维修结果
                            repair_over_map.put("installaddress", js.getString("installaddress"));//安装地址
                            repair_over_map.put("installtime", js.getString("installtime"));//安装时间
                            repairOverList.add(repair_over_map);
                        } else if ("0".equals(main_type)) {
                            //公交
                            Map<String, String> repair_over_map = new HashMap<String, String>();
                            JSONObject js = ja.getJSONObject(i);
                            repair_over_map.put("product_name", js.getString("product_name"));//产品名称
                            repair_over_map.put("product_number", js.getString("product_number"));//产品序列号
                            repair_over_map.put("car_number", js.getString("car_number"));//车牌号
                            repair_over_map.put("fault_description", js.getString("fault_description"));//故障原因
                            repair_over_map.put("result_maint", js.getString("result_maint"));//维修结果
                            repair_over_map.put("cost_material", js.getString("cost_material"));//材料费
                            repair_over_map.put("cost_maintenance", js.getString("cost_maintenance"));//人工费
                            repair_over_map.put("cost_all", js.getString("cost_all"));//总费用
                            repairOverList.add(repair_over_map);
                        }
                    }
                    RepairOverAdapter repairOverAdapter = new RepairOverAdapter(repairOverList);
                    lv_specific_repair.setAdapter(repairOverAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 公交数据展示与操作
     */
    private void showBusProductSQLDataRepairOrInstall() {
        List<BusProductTable> products = DataSupport
                .select("*")
                .where("type_code = ?", type_code)
                .find(BusProductTable.class);
        final List<Map<String, String>> list = new ArrayList<>();//设备保存或上传后的list
        for (int i = 0; i < products.size(); i++) {
            BusProductTable product_bean = products.get(i);
            Map<String, String> map = new HashMap<>();
            map.put("repair_code", product_bean.getRepair_code());
            map.put("repair_name", product_bean.getRepair_name());
            map.put("type_code", product_bean.getType_code());
            map.put("product_code", product_bean.getProduct_code());
            map.put("product_name", product_bean.getProduct_name());
            map.put("input_time", product_bean.getInput_time());//出厂时间
            map.put("veh_code", product_bean.getVeh_code());
            map.put("fault_cause", product_bean.getFault_cause());
            map.put("maint_result", product_bean.getMaint_result());
            map.put("material_cost", product_bean.getMaterial_cost());
            map.put("maintenance_cost", product_bean.getMaintenance_cost());
            map.put("product_state", product_bean.getProduct_state());
            map.put("fault_remark", product_bean.getFault_remark());
            list.add(map);
        }

        if (list.size() != 0) {
            adapter = new SimpleAdapter(
                    getActivity(),
                    list,
                    R.layout.savesqlitedata,
                    new String[]{"product_code", "veh_code", "product_state", "fault_cause"},
                    new int[]{
                            R.id.tv_product_code, R.id.tv_veh_code,
                            R.id.tv_product_state, R.id.tv_fault_cause,
                    }
            );
            lv_specific_repair.setAdapter(adapter);
            lv_specific_repair.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Map<String, String> map = list.get(position);
                    MapSerializable myMap = new MapSerializable();
                    myMap.setMap(map);
                    Intent intent = new Intent().setClass(getActivity(), BusUploadingOrSaveActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("map", myMap);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            lv_specific_repair.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    View null_message = LayoutInflater.from(getActivity()).inflate(R.layout.nullmessage, null);
                    new EditDialog.Builder(getActivity())
                            .setTitle("确认删除")
                            .setContentView(null_message)
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    if ("已提交".equals(list.get(position).get("product_state"))) {
//                                        final String type_code = list.get(position).get("type_code");
                                        if (type_code.contains("W")) {
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    super.run();
                                                    String method = "DeleteResultInfo";
                                                    Map<String, String> args = new HashMap<String, String>();
                                                    args.put("OrderCode", type_code.replace("W", ""));
                                                    args.put("ProductId", list.get(position).get("product_code"));
                                                    new WrappedHttpUtil.Builder(getActivity())
                                                            .dataRepairRequest(method, args)
                                                            .setL(new WrappedHttpUtil.Builder.Listener() {
                                                                @Override
                                                                public void onSuccess(int responseCode, String result) {
                                                                    boolean isJson = JsonParse.isJson(result);
                                                                    if (isJson) {
                                                                        String message = "";
                                                                        try {
                                                                            JSONObject jsonObject = new JSONObject(result);
                                                                            message = jsonObject.getString("result");
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                        if ("0".equals(message)) {
                                                                            //删除失败
                                                                            showTreadToast("后台数据删除失败\r\n重启软件或联系技术人员");
                                                                        } else if ("1".equals(message)) {
                                                                            //删除成功
                                                                            int deleteAll = DataSupport.deleteAll(
                                                                                    BusProductTable.class,
                                                                                    "product_code = ?",
                                                                                    list.get(position).get("product_code"));
                                                                            if (deleteAll > 0) {
                                                                                getActivity().runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        list.remove(list.get(position));
                                                                                        adapter.notifyDataSetChanged();
                                                                                        showOnUI("删除成功");
                                                                                        dialog.dismiss();
                                                                                    }
                                                                                });
                                                                            } else {
                                                                                showTreadToast("本地数据删除失败\r\n重启软件或联系技术人员");
                                                                            }
                                                                        } else {
                                                                            //未知错误
                                                                            showTreadToast(getActivity().getResources().getString(R.string.mistake_unknown));
                                                                        }
                                                                    } else {
                                                                        showTreadToast(getActivity().getResources().getString(R.string.mistake_dataResolve));
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFail(int responseCode, String result) {
                                                                    ExceptionMessage("删除设备信息", responseCode, result);//公交维修产品
                                                                }
                                                            }).execute();
                                                }
                                            }.start();
                                        } else if (type_code.contains("T")) {
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    super.run();
                                                    String method = "DeleteFaultProuctInfo";
                                                    Map<String, String> args = new HashMap<String, String>();
                                                    args.put("OrderId", type_code.replace("T", ""));
                                                    args.put("ProductCode", list.get(position).get("product_code"));
                                                    new WrappedHttpUtil.Builder(getActivity())
                                                            .dataInstallRequest(method, args)
                                                            .setL(new WrappedHttpUtil.Builder.Listener() {
                                                                @Override
                                                                public void onSuccess(int responseCode, String result) {
                                                                    boolean isJson = JsonParse.isJson(result);
                                                                    if (isJson) {
                                                                        String message = "";
                                                                        try {
                                                                            JSONObject jsonObject = new JSONObject(result);
                                                                            message = jsonObject.getString("result");
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                        if ("0".equals(message)) {
                                                                            //删除失败
                                                                            showTreadToast("后台数据删除失败\r\n重启软件或联系技术人员");
                                                                        } else if ("1".equals(message)) {
                                                                            //删除成功
                                                                            int deleteAll = DataSupport.deleteAll(
                                                                                    BusProductTable.class,
                                                                                    "product_code = ?",
                                                                                    list.get(position).get("product_code"));
                                                                            if (deleteAll > 0) {
                                                                                getActivity().runOnUiThread(new Runnable() {
                                                                                    @Override
                                                                                    public void run() {
                                                                                        list.remove(list.get(position));
                                                                                        adapter.notifyDataSetChanged();
                                                                                        showOnUI("删除成功");
                                                                                        dialog.dismiss();
                                                                                    }
                                                                                });
                                                                            } else {
                                                                                showTreadToast("本地数据删除失败\r\n重启软件或联系技术人员");
                                                                            }
                                                                        } else {
                                                                            //未知错误
                                                                            showTreadToast(getActivity().getResources().getString(R.string.mistake_unknown));
                                                                        }
                                                                    } else {
                                                                        showTreadToast(getActivity().getResources().getString(R.string.mistake_dataResolve));
                                                                    }
                                                                }

                                                                @Override
                                                                public void onFail(int responseCode, String result) {
                                                                    ExceptionMessage("删除设备信息", responseCode, result);//公交故障产品
                                                                }
                                                            }).execute();
                                                }
                                            }.start();
                                        }
                                    } else if ("已保存".equals(list.get(position).get("product_state"))) {
                                        int deleteAll = DataSupport.deleteAll(
                                                BusProductTable.class,
                                                "product_code = ?",
                                                list.get(position).get("product_code"));
                                        if (deleteAll > 0) {
                                            list.remove(list.get(position));
                                            adapter.notifyDataSetChanged();
                                            showOnUI("删除成功");
                                            dialog.dismiss();
                                        } else {
                                            showTreadToast("删除失败\r\n重启软件或联系技术人员");
                                        }
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                    return true;
                }
            });
        }
    }

    /**
     * 公路数据展示与操作
     *
     * @param productTables
     */
    private void showHigWaySQLDataRepairOrInstall(List<HigwayProductTable> productTables) {
        final List<Map<String, String>> mapList = new ArrayList<>();
        for (int i = 0; i < productTables.size(); i++) {
            HigwayProductTable higway = productTables.get(i);
            Map<String, String> map = new HashMap<>();
            map.put("type_code", higway.getType_code());
            map.put("order_engineer", higway.getOrder_engineer());
            map.put("userId", higway.getUserId());
            map.put("productType", higway.getProductType());
            map.put("productName", higway.getProductName());
            map.put("productId", higway.getProductId());
            map.put("productSim", higway.getProductSim());
            map.put("faultCause", higway.getFaultCause());
            map.put("maintResult", higway.getMaintResult());
            map.put("vehCode", higway.getVehCode());
            map.put("installAddress", higway.getInstallAddress());
            map.put("installTime", higway.getInstallTime());
            map.put("product_state", higway.getProduct_state());
            map.put("sql_time", higway.getSql_time());
            map.put("installRemark", higway.getInstallRemark());
            mapList.add(map);
        }
        adapter = new SimpleAdapter(
                getActivity(),
                mapList,
                R.layout.savesqlitedata,
                new String[]{"productId", "vehCode", "product_state", "faultCause"},
                new int[]{
                        R.id.tv_product_code, R.id.tv_veh_code,
                        R.id.tv_product_state, R.id.tv_fault_cause,
                }
        );

        lv_specific_repair.setAdapter(adapter);
        lv_specific_repair.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> map = mapList.get(position);
                MapSerializable myMap = new MapSerializable();
                myMap.setMap(map);
                Intent intent = new Intent().setClass(getActivity(), HighwayUploadingOrSaveActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("map", myMap);
                intent.putExtras(bundle);
                sp.edit().putString("flag", "save_over").apply();
                startActivity(intent);
            }
        });
        lv_specific_repair.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                View null_message = LayoutInflater.from(getActivity()).inflate(R.layout.nullmessage, null);
                new EditDialog.Builder(getActivity())
                        .setTitle("确认删除")
                        .setContentView(null_message)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                if (db != null) {
                                    if ("已提交".equals(mapList.get(position).get("product_state"))) {
//                                        final String orderId = mapList.get(position).get("type_code");
                                        if (type_code.contains("T")) {
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    super.run();
                                                    String method = "DeleteInstallInfo";
                                                    Map<String, String> args = new HashMap<String, String>();
                                                    args.put("OrderId", type_code.replace("T", ""));
                                                    args.put("ProductCode", mapList.get(position).get("productId"));
                                                    new WrappedHttpUtil.Builder(getActivity())
                                                            .dataInstallRequest(method, args)
                                                            .setL(new WrappedHttpUtil.Builder.Listener() {
                                                                @Override
                                                                public void onSuccess(int responseCode, String result) {
                                                                    deleteProduct(result, mapList, position, dialog);//删除安装设备
                                                                }

                                                                @Override
                                                                public void onFail(int responseCode, String result) {
                                                                    ExceptionMessage("删除设备信息", responseCode, result);//公路安装产品
                                                                }
                                                            }).execute();
                                                }
                                            }.start();
                                        } else if (type_code.contains("W")) {
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    super.run();
                                                    String method = "DeleteResultInfoHighWay";
                                                    Map<String, String> args = new HashMap<String, String>();
                                                    args.put("OrderCode", type_code.replace("W", ""));
                                                    args.put("ProductId", mapList.get(position).get("productId"));
                                                    new WrappedHttpUtil.Builder(getActivity())
                                                            .dataRepairRequest(method, args)
                                                            .setL(new WrappedHttpUtil.Builder.Listener() {
                                                                @Override
                                                                public void onSuccess(int responseCode, String result) {
                                                                    deleteProduct(result, mapList, position, dialog);//删除维修设备
                                                                }

                                                                @Override
                                                                public void onFail(int responseCode, String result) {
                                                                    ExceptionMessage("删除设备信息", responseCode, result);//公路维修产品
                                                                }
                                                            }).execute();
                                                }
                                            }.start();
                                        }
                                    } else if ("已保存".equals(mapList.get(position).get("product_state"))) {
                                        int deleteAll = DataSupport.deleteAll(
                                                HigwayProductTable.class,
                                                "type_code = ? and productId = ?",
                                                mapList.get(position).get("type_code"),
                                                mapList.get(position).get("productId"));
                                        if (deleteAll > 0) {
                                            showOnUI("删除成功");
                                            mapList.remove(mapList.get(position));
                                            adapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                        } else {
                                            showOnUI("删除失败\r\n请联系技术人员");
                                        }
                                    }
                                } else {
                                    showOnUI(getActivity().getResources().getString(R.string.sql_database_null));
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                return true;
            }
        });
    }

    /**
     * 公路删除设备信息操作提示
     *
     * @param result   返回值
     * @param mapList  信息集合
     * @param position 一条信息对应的位置
     * @param dialog   弹窗
     */
    private void deleteProduct(String result, final List<Map<String, String>> mapList,
                               final int position, final DialogInterface dialog) {
        boolean isJson = JsonParse.isJson(result);
        if (isJson) {
            String message = "";
            try {
                JSONObject jsonObject = new JSONObject(result);
                message = jsonObject.getString("result");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if ("0".equals(message)) {
                showTreadToast("后台数据删除失败\r\n重启软件或联系技术人员");
            } else if ("1".equals(message)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int deleteAll = DataSupport.deleteAll(
                                HigwayProductTable.class,
                                "type_code = ? and productId = ?",
                                mapList.get(position).get("type_code"),
                                mapList.get(position).get("productId"));
                        if (deleteAll > 0) {
                            showOnUI("删除成功");
                            mapList.remove(mapList.get(position));
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        } else {
                            showOnUI("本地数据删除失败\r\n请联系技术人员");
                        }
                    }
                });
            } else {
                showTreadToast(getActivity().getResources().getString(R.string.mistake_unknown));
            }
        } else {
            showTreadToast(getActivity().getResources().getString(R.string.mistake_dataResolve));
        }
    }

    /**
     * Toast异常提示
     *
     * @param interfaceType 接口类型
     * @param responseCode  返回码
     * @param result        返回结果
     */
    private void ExceptionMessage(String interfaceType, int responseCode, String result) {
        if (responseCode == 0) {
            if ("123".equals(result)) {
                showTreadToast(getResources().getString(R.string.noNetwork));
            }
        }
        if (responseCode == 404) {
            showTreadToast(interfaceType + "\r\n" +
                    getResources().getString(R.string.mistake_404));
        }
        if (responseCode == 500) {
            showTreadToast(interfaceType + "\r\n" +
                    getResources().getString(R.string.mistake_500));
        }
        if (responseCode == 400) {
            showTreadToast(interfaceType + "\r\n" +
                    getResources().getString(R.string.mistake_400));
        }
    }

    class RepairOverAdapter extends BaseAdapter {
        private List<Map<String, String>> lists;

        public RepairOverAdapter(List<Map<String, String>> lists) {
            this.lists = lists;
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Map<String, String> getItem(int position) {
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            TextView tv_product_number;//设备编号
            TextView tv_product_name;//产品名称
            TextView tv_car_number;//车牌号
            TextView tv_fault_description;//维修原因
            TextView tv_result_maint;//维修结果

            LinearLayout ll_product_sim;//SIM卡号(公路)
            TextView tv_product_sim;//SIM卡号(公路)
            LinearLayout ll_installaddress;//安装地址(公路)
            TextView tv_installaddress;//安装地址(公路)
            LinearLayout ll_installtime;//安装时间(公路)
            TextView tv_installtime;//安装时间(公路)

            LinearLayout ll_cost_material;//材料费(公交)
            TextView tv_cost_material;//材料费(公交)
            LinearLayout ll_cost_maintenance;//人工费(公交)
            TextView tv_cost_maintenance;//人工费(公交)
            LinearLayout ll_cost_all;//总费用(公交)
            TextView tv_cost_all;//总费用(公交)

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Map<String, String> map = getItem(position);
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.new_history_repairproduct, parent, false);
                holder.tv_product_number = (TextView) convertView.findViewById(R.id.tv_product_number);
                holder.tv_product_name = (TextView) convertView.findViewById(R.id.tv_product_name);
                holder.tv_car_number = (TextView) convertView.findViewById(R.id.tv_car_number);
                holder.tv_fault_description = (TextView) convertView.findViewById(R.id.tv_fault_description);
                holder.tv_result_maint = (TextView) convertView.findViewById(R.id.tv_result_maint);

                holder.ll_cost_material = (LinearLayout) convertView.findViewById(R.id.ll_cost_material);
                holder.ll_cost_maintenance = (LinearLayout) convertView.findViewById(R.id.ll_cost_maintenance);
                holder.ll_cost_all = (LinearLayout) convertView.findViewById(R.id.ll_cost_all);
                holder.tv_cost_material = (TextView) convertView.findViewById(R.id.tv_cost_material);
                holder.tv_cost_maintenance = (TextView) convertView.findViewById(R.id.tv_cost_maintenance);
                holder.tv_cost_all = (TextView) convertView.findViewById(R.id.tv_cost_all);

                holder.ll_product_sim = (LinearLayout) convertView.findViewById(R.id.ll_product_sim);
                holder.ll_installaddress = (LinearLayout) convertView.findViewById(R.id.ll_installaddress);
                holder.ll_installtime = (LinearLayout) convertView.findViewById(R.id.ll_installtime);
                holder.tv_product_sim = (TextView) convertView.findViewById(R.id.tv_product_sim);
                holder.tv_installaddress = (TextView) convertView.findViewById(R.id.tv_installaddress);
                holder.tv_installtime = (TextView) convertView.findViewById(R.id.tv_installtime);

                if ("0".equals(main_type)) {
                    //公交
                    holder.ll_product_sim.setVisibility(View.GONE);
                    holder.ll_installaddress.setVisibility(View.GONE);
                    holder.ll_installtime.setVisibility(View.GONE);
                } else if ("1".equals(main_type)) {
                    //公路
                    holder.ll_cost_material.setVisibility(View.GONE);
                    holder.ll_cost_maintenance.setVisibility(View.GONE);
                    holder.ll_cost_all.setVisibility(View.GONE);
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();

            }
            holder.tv_product_number.setText(map.get("product_number"));
            holder.tv_product_name.setText(map.get("product_name"));
            holder.tv_car_number.setText(map.get("car_number"));
            holder.tv_fault_description.setText(map.get("fault_description"));
            holder.tv_result_maint.setText(map.get("result_maint"));
            //公交
            holder.tv_cost_material.setText(map.get("cost_material"));
            holder.tv_cost_maintenance.setText(map.get("cost_maintenance"));
            holder.tv_cost_all.setText(map.get("cost_all"));
            //公路
            holder.tv_product_sim.setText(map.get("product_sim"));
            holder.tv_installaddress.setText(map.get("installaddress"));
            holder.tv_installtime.setText(map.get("installtime"));
            return convertView;
        }
    }

    private void testDialog(String test) {
        new AlertDialog.Builder(getActivity())
                .setTitle(test)
                .setView(null)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }
}
