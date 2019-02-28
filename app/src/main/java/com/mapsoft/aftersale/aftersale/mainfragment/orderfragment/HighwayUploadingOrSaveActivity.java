package com.mapsoft.aftersale.aftersale.mainfragment.orderfragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.mapsoft.aftersale.R;
import com.mapsoft.aftersale.RdApplication;
import com.mapsoft.aftersale.bean.HigwayProductTable;
import com.mapsoft.aftersale.bean.User;
import com.mapsoft.aftersale.utils.EditDialog;
import com.mapsoft.aftersale.utils.JsonParse;
import com.mapsoft.aftersale.utils.KeywordAdapter;
import com.mapsoft.aftersale.utils.ListMapSerializable;
import com.mapsoft.aftersale.utils.MapSerializable;
import com.mapsoft.aftersale.utils.MyToast;
import com.mapsoft.aftersale.utils.WrappedHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.InputType.TYPE_CLASS_PHONE;

/**
 * Created by Administrator on 2017/11/25.
 * 订单信息--扫描(查找数据库是否有保存的设备信息)--上传或保存维修信息
 */

public class HighwayUploadingOrSaveActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "HighwayUploadingOrSaveA";
    private static final String DATABASE_NULL = "0";
    private static final String OPERATE_OK = "1";
    private static final String OPERATE_ERROR = "2";
    private static final String UNKNOWN = "3";


    private TextView
            tv_install_time,//安装时间
            tv_title,       //标题
            tvList,         //产品序列号
            tvDate,         //出产时间
            tvRepairName;   //维修人员

    private EditText
            et_product_remark,//安装产品备注
            etVehCode,         //车牌号
            etFaultcause,       //故障原因
            etMaintResult,      //维修结果
            et_SimCard,     //SIM卡号
            et_install_address;  //安装地址

    private Button
//            btn_select,//查询序列号在数据库中是否存在
            btnSave,//保存设备信息到本地数据库
            btnSubmit,//上传信息
            btnBack, //返回
            btnLook;//查看维修记录

    private LinearLayout
            ll_pro_type,    //产品类型
            ll_install_remark,//安装备注
            ll_install_time,//安装时间
            ll_cause,//故障原因
            ll_result,//维修结果
            ll_install_address;//安装地址
    private SharedPreferences sp;
    private MultiAutoCompleteTextView mtvPro_Type;//产品类型
    private MultiAutoCompleteTextView mtvPro_Name;//产品名称


    private SQLiteDatabase db;

    private String order_type = "";//订单类型
    private String order_code = "";//订单编号
    private String order_engineer = "";//维修人员
    private String userID = "";//维修人员ID
    private String product_type = "";
    private String product_name = "";//产品名称
    private String product_code = "";//产品序列号
    private String product_sim = "";//sim卡号
    private String faultCause = "";//故障原因
    private String maintResult = "";//维修结果
    private String vehCode = "";//车牌号
    private String installAddress = "";//维修地址
    private String installTime = "";//安装时间
    private String pro_remark = "";//安装备注
    private String sql_time = "";//录入本地数据库时间

    private String type_code = "";//订单类型+订单编号

    private String string_data = "";//上传的产品信息(安装)


    private Calendar calendar;
    private int year = 0;
    private int month = 0;
    private int dayOfMonth = 0;
    private Map<String, String> orderMap;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highwayuploadingorsave);
        sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        db = RdApplication.getDatabase();
        init();
        /*设置开始时间*/
        final DatePickerDialog firstDialog = new DatePickerDialog(HighwayUploadingOrSaveActivity.this, R.style.DateTime,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        tv_install_time.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                    }
                }, year, month, dayOfMonth);
        tv_install_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstDialog.show();
            }
        });
        Log.e(TAG, sp.getAll() + "");
    }

    private void init() {
        orderMap = RdApplication.get().getOrderMap();
        order_type = orderMap.get("order_type");
        order_code = orderMap.get("order_code");
        type_code = orderMap.get("order_type").trim() + order_code.trim();
        order_engineer = orderMap.get("order_engineer");
        tv_title = (TextView) findViewById(R.id.tv_title);
        tvRepairName = (TextView) findViewById(R.id.tvRepairName);
        tvRepairName.setText(order_engineer);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvList = (TextView) findViewById(R.id.tvList);
        mtvPro_Type = (MultiAutoCompleteTextView) findViewById(R.id.mtvPro_Type);
        mtvPro_Name = (MultiAutoCompleteTextView) findViewById(R.id.mtvPro_Name);
        etVehCode = (EditText) findViewById(R.id.etVehCode);

        et_SimCard = (EditText) findViewById(R.id.et_SimCard);
        et_SimCard.setInputType(TYPE_CLASS_PHONE);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
//        btn_select = (Button) findViewById(R.id.btn_select);
//        btn_select.setOnClickListener(this);

        btnLook = (Button) findViewById(R.id.btnLook);
        btnLook.setOnClickListener(this);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);

        ll_install_address = (LinearLayout) findViewById(R.id.ll_install_address);
        ll_cause = (LinearLayout) findViewById(R.id.ll_cause);
        ll_result = (LinearLayout) findViewById(R.id.ll_result);
        ll_install_time = (LinearLayout) findViewById(R.id.ll_install_time);
        ll_pro_type = (LinearLayout) findViewById(R.id.ll_pro_type);//产品类型——公路

        ll_install_remark = (LinearLayout) findViewById(R.id.ll_install_remark);
        et_product_remark = (EditText) findViewById(R.id.et_product_remark);

        //产品名称设置
        List<String> product_list = new ArrayList<>();
        String[] products = getResources().getStringArray(R.array.higway_product_name);
        for (int i = 0; i < products.length; i++) {
            product_list.add(products[i]);
        }
        KeywordAdapter name_adapter = new KeywordAdapter(this, R.layout.names_item, product_list);
        mtvPro_Name.setAdapter(name_adapter);
        mtvPro_Name.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        etFaultcause = (EditText) findViewById(R.id.etFaultcause);
        etMaintResult = (EditText) findViewById(R.id.etMaintResult);
        et_install_address = (EditText) findViewById(R.id.et_install_address);
        tv_install_time = (TextView) findViewById(R.id.tv_install_time);
        tv_install_time.setOnClickListener(this);

        //维修人员，录入时间，产品序列号设置--未保存
        calendar = Calendar.getInstance();
        if ("order_list".equals(sp.getString("flag", ""))) {
            //订单列表点击按钮传值
            tvDate.setText(calendar.get(Calendar.YEAR) + "年"
                    + (calendar.get(Calendar.MONTH) + 1) + "月"
                    + calendar.get(Calendar.DAY_OF_MONTH) + "日");
            tvRepairName.setText(orderMap.get("order_engineer"));
            tvList.setText(sp.getString("product_list", ""));
        } else if ("save_over".equals(sp.getString("flag", ""))) {
            //已保存信息传值
            Bundle bundle = getIntent().getExtras();
            MapSerializable maMap = (MapSerializable) bundle.get("map");
            Map<String, String> map = maMap.getMap();
            tvRepairName.setText(map.get("order_engineer"));
            tvDate.setText(map.get("sql_time"));
            tvList.setText(map.get("productId"));
            mtvPro_Type.setText(map.get("productType"));
            mtvPro_Name.setText(map.get("productName"));
            etVehCode.setText(map.get("vehCode"));
            etFaultcause.setText(map.get("faultCause"));
            etMaintResult.setText(map.get("maintResult"));
            et_install_address.setText(map.get("installAddress"));
            tv_install_time.setText(map.get("installTime"));
            et_SimCard.setText(map.get("productSim"));
            et_product_remark.setText(map.get("installRemark"));
        }

        if ("W".equals(order_type)) {
            ll_install_address.setVisibility(View.GONE);
            tv_title.setText("维修设备");
            ll_install_time.setVisibility(View.GONE);
            ll_install_remark.setVisibility(View.GONE);
            ll_pro_type.setVisibility(View.GONE);

        } else {
            ll_cause.setVisibility(View.GONE);
            ll_result.setVisibility(View.GONE);
            tv_title.setText("安装设备");

            //产品类型设置
            List<String> product_type_list = new ArrayList<>();
            String[] type_products = getResources().getStringArray(R.array.higway_product_type);
            for (int i = 0; i < type_products.length; i++) {
                product_type_list.add(type_products[i]);
            }
            KeywordAdapter type_adapter = new KeywordAdapter(this, R.layout.names_item, product_type_list);
            mtvPro_Type.setAdapter(type_adapter);
            mtvPro_Type.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        }
        userID = RdApplication.get().getUser().getUser_id();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnSubmit:
                saveOrSubmitMessage();//上传维修结果
                if ("".equals(product_name)) {
                    showOnUI("产品名不能为空");
                    return;
                }
                if ("".equals(vehCode)) {
                    showOnUI("车牌号不能为空");
                    return;
                }
                if ("W".equals(order_type)) {
                    if ("".equals(faultCause)) {
                        showOnUI("故障原因不能为空");
                        return;
                    }
                } else if ("T".equals(order_type)) {
                    if ("".equals(product_type)) {
                        showOnUI("产品类型不能为空");
                        return;
                    }
                    if ("".equals(installAddress)) {
                        showOnUI("安装地址不能为空");
                        return;
                    }
                    if ("".equals(installTime)) {
                        showOnUI("安装时间不能为空");
                        return;
                    }
                }
                if ("".equals(product_sim)) {
                    showOnUI("产品SIM卡号不能为空");
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        if ("W".equals(order_type)) {
                            String method = "ResultInfoHighWay";
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("OrderCode", order_code);
                            params.put("Name", order_engineer);
                            params.put("UserId", userID);
                            params.put("ProductName", product_name);
                            params.put("ProductId", product_code);
                            params.put("ProductSim", product_sim);
                            params.put("FaultCause", faultCause);
                            params.put("MaintResult", maintResult);
                            params.put("VehCode", vehCode);
                            params.put("InstallAddress", installAddress);
                            params.put("InstallTime", installTime);
                            new WrappedHttpUtil.Builder(HighwayUploadingOrSaveActivity.this)
                                    .dataRepairRequest(method, params)
                                    .setL(new WrappedHttpUtil.Builder.Listener() {
                                        @Override
                                        public void onSuccess(int responseCode, String result) {
                                            boolean isJson = JsonParse.isJson(result);
                                            if (isJson) {
                                                String message = JsonParse.judgeJson(result);
                                                if ("0".equals(message)) {
                                                    showOnThread("上传失败\r\n请重启软件或联系技术人员");
                                                    HigwayProductTable product_table = new HigwayProductTable();
                                                    product_table.setProduct_state("已保存");
                                                    String result_sql = updateLocalSQL(product_table);//上传失败保存数据库
                                                    if ("0".equals(result_sql)) {
                                                        runOnUiThread(getResources().getString(R.string.sql_database_null));//数据库未打开--上传失败
                                                    } else if ("1".equals(result_sql)) {
                                                        sp.edit().putString("order_message", "uploading").apply();
                                                        runOnUiThread(getResources().getString(R.string.sql_save_ok));//保存成功--上传失败
                                                    } else if ("2".equals(result_sql)) {
                                                        runOnUiThread(getResources().getString(R.string.sql_save_error));//保存失败--上传失败
                                                    } else {
                                                        runOnUiThread(getResources().getString(R.string.mistake_unknown));//未知错误--上传失败
                                                    }
                                                } else if ("1".equals(message)) {
                                                    showOnThread("上传成功");
                                                    HigwayProductTable productTable = new HigwayProductTable();
                                                    productTable.setProduct_state("已提交");
                                                    String result_sql = updateLocalSQL(productTable);//上传成功保存数据库
                                                    if ("0".equals(result_sql)) {
                                                        runOnUiThread(getResources().getString(R.string.sql_database_null));//数据库未打开--上传成功
                                                    } else if ("1".equals(result_sql)) {
                                                        sp.edit().putString("order_message", "uploading").apply();
                                                        runOnUiThread(getResources().getString(R.string.sql_save_ok));//保存成功--上传成功
                                                    } else if ("2".equals(result_sql)) {
                                                        runOnUiThread(getResources().getString(R.string.sql_save_error));//保存失败--上传成功
                                                    } else {
                                                        runOnUiThread(getResources().getString(R.string.mistake_unknown));//未知错误--上传成功
                                                    }
                                                } else {
                                                    showOnThread("上传维修信息\r\n" + getResources().getString(R.string.mistake_unknown));
                                                }
                                            } else {
                                                showOnThread("上传维修信息\r\n" + getResources().getString(R.string.mistake_dataResolve));
                                            }
                                        }

                                        @Override
                                        public void onFail(int responseCode, String result) {
                                            ExceptionMessage("上传维修信息", responseCode, result);
                                            saveLocalSQLByException();//维修单
                                        }
                                    }).execute();
                        } else if ("T".equals(orderMap.get("order_type"))) {
                            String method = "AddInstallInfo";
                            string_data = "{\n" +
                                    "    \"InstallContactListId\": \"" + order_code + "\",\n" +
                                    "    \"CarNumber\": \"" + vehCode + "\",\n" +
                                    "    \"ProductCode\": \"" + product_code + "\",\n" +
                                    "    \"ProductType\": \"" + product_type + "\",\n" +
                                    "    \"ProductName\": \"" + product_name + "\",\n" +
                                    "    \"ProductNumber\": \"1\",\n" +
                                    "    \"SimCard\": \"" + product_sim + "\",\n" +
                                    "    \"Remarks\": \"" + pro_remark + "\"\n" +
                                    "}";
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("Data", string_data.replace(" ", ""));
                            new WrappedHttpUtil.Builder(HighwayUploadingOrSaveActivity.this)
                                    .dataInstallRequest(method, params)
                                    .setL(new WrappedHttpUtil.Builder.Listener() {
                                        @Override
                                        public void onSuccess(int responseCode, String result) {
                                            boolean isJson = JsonParse.isJson(result);
                                            if (isJson) {
                                                String message = "";
                                                try {
                                                    JSONObject msg = new JSONObject(result);
                                                    for (int i = 0; i < msg.length(); i++) {
                                                        message = msg.getString("result");
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                if ("0".equals(message)) {
                                                    uploadingError("上传失败");
                                                } else if ("1".equals(message)) {
                                                    sp.edit().putString("order_message", "uploading").apply();
                                                    uploadingOK("上传成功");
                                                } else if ("-1".equals(message)) {
                                                    uploadingMistake("上传失败-1");//-1上传安装信息 字符串格式错误
                                                } else if ("-2".equals(message)) {
                                                    //已上传过设备信息，覆盖修改
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            View view = getLayoutInflater().inflate(R.layout.names_item, null);
                                                            TextView tv_message = (TextView) view.findViewById(R.id.tv_name);
                                                            tv_message.setGravity(Gravity.CENTER);
                                                            tv_message.setText("该设备已上传过信息——是否修改");
                                                            new EditDialog.Builder(HighwayUploadingOrSaveActivity.this)
                                                                    .setTitle("修改信息")
                                                                    .setContentView(view)
                                                                    .setCancellable(false)
                                                                    .setCanTouchOutside(false)
                                                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            new Thread() {
                                                                                @Override
                                                                                public void run() {
                                                                                    super.run();
                                                                                    //修改安装信息
                                                                                    String method = "UpdateInstallInfo";
                                                                                    string_data = "{\n" +
                                                                                            "    \"InstallContactListId\": \"" + order_code + "\",\n" +
                                                                                            "    \"CarNumber\": \"" + vehCode + "\",\n" +
                                                                                            "    \"ProductCode\": \"" + product_code + "\",\n" +
                                                                                            "    \"ProductType\": \"" + product_type + "\",\n" +
                                                                                            "    \"ProductName\": \"" + product_name + "\",\n" +
                                                                                            "    \"ProductNumber\": \"1\",\n" +
                                                                                            "    \"SimCard\": \"" + product_sim + "\",\n" +
                                                                                            "    \"Remarks\": \"" + pro_remark + "\"\n" +
                                                                                            "}";
                                                                                    Map<String, String> params = new HashMap<String, String>();
                                                                                    params.put("Data", string_data.replace(" ", ""));
                                                                                    new WrappedHttpUtil.Builder(HighwayUploadingOrSaveActivity.this)
                                                                                            .dataInstallRequest(method, params)
                                                                                            .setL(new WrappedHttpUtil.Builder.Listener() {
                                                                                                @Override
                                                                                                public void onSuccess(int responseCode, String result) {
                                                                                                    boolean isJson = JsonParse.isJson(result);
                                                                                                    if (isJson) {
                                                                                                        String message = "";
                                                                                                        try {
                                                                                                            JSONObject jsonObject = new JSONObject(result);
                                                                                                            for (int i = 0; i < jsonObject.length(); i++) {
                                                                                                                message = jsonObject.getString("result");
                                                                                                            }
                                                                                                        } catch (JSONException e) {
                                                                                                            e.printStackTrace();
                                                                                                        }
                                                                                                        if ("0".equals(message)) {
                                                                                                            uploadingError("修改失败");
                                                                                                        } else if ("1".equals(message)) {
                                                                                                            sp.edit().putString("order_message", "uploading").apply();
                                                                                                            uploadingOK("修改成功");
                                                                                                        } else if ("-1".equals(message)) {
                                                                                                            uploadingMistake("修改失败-1");//修改 字符串格式错误
                                                                                                        } else {
                                                                                                            showOnThread("修改安装信息\r\n" + getResources().getString(R.string.mistake_unknown));
                                                                                                        }
                                                                                                    } else {
                                                                                                        showOnThread("修改安装信息\r\n" + getResources().getString(R.string.mistake_dataResolve));
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onFail(int responseCode, String result) {
                                                                                                    ExceptionMessage("修改安装信息", responseCode, result);
                                                                                                    saveLocalSQLByException();//安装单——修改设备信息
                                                                                                }
                                                                                            }).execute();
                                                                                }
                                                                            }.start();
                                                                        }
                                                                    })
                                                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            dialog.dismiss();
                                                                            LayoutInflater inflater = getLayoutInflater();
                                                                            View product_list_view = inflater.inflate(R.layout.product_list_higway, null);
                                                                            final EditText et_input = (EditText) product_list_view.findViewById(R.id.et_input);
                                                                            new EditDialog.Builder(HighwayUploadingOrSaveActivity.this)
                                                                                    .setTitle("请输入设备ID")
                                                                                    .setContentView(product_list_view)
                                                                                    .setCanTouchOutside(false)
                                                                                    .setCancellable(false)
                                                                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                            String pro_list = et_input.getText().toString().trim().toUpperCase();
                                                                                            if ("".equals(pro_list)) {
                                                                                                showOnUI("设备ID不能为空");
                                                                                                return;
                                                                                            }
//                                                                                        showOnUI("123123");
                                                                                            tvList.setText(pro_list);
                                                                                            mtvPro_Type.setText("");
                                                                                            mtvPro_Name.setText("");
                                                                                            dialog.dismiss();
                                                                                        }
                                                                                    })
                                                                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                            dialog.dismiss();
                                                                                            finish();
                                                                                        }
                                                                                    })
                                                                                    .create().show();
                                                                        }
                                                                    }).create().show();
                                                        }
                                                    });
                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    showOnUI(getResources().getString(R.string.mistake_unknown));
                                                                }
                                                            }, 1000);
                                                        }
                                                    });
                                                }
                                            } else {
                                                showOnThread("上传安装信息\r\n" + getResources().getString(R.string.mistake_dataResolve));
                                            }
                                        }

                                        @Override
                                        public void onFail(int responseCode, String result) {
                                            ExceptionMessage("上传安装信息", responseCode, result);
                                            saveLocalSQLByException();//安装单
                                        }
                                    }).execute();
                        }
                    }
                }.start();
                break;
            case R.id.btnSave:
                saveOrSubmitMessage();//保存本地数据库
                HigwayProductTable product_table = new HigwayProductTable();
                product_table.setProduct_state("已保存");
                String result_sql = updateLocalSQL(product_table);//保存数据库
                if ("0".equals(result_sql)) {
                    showOnUI(getResources().getString(R.string.sql_database_null));
                    return;
                } else if ("1".equals(result_sql)) {
                    showOnUI(getResources().getString(R.string.sql_save_ok));
                    sp.edit().putString("order_message", "uploading").apply();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);
                } else if ("2".equals(result_sql)) {
                    showOnUI(getResources().getString(R.string.sql_save_error));
                } else {
                    showOnUI(getResources().getString(R.string.mistake_unknown));
                }
                break;
            case R.id.btnLook:
                //查看产品维修记录
                product_code = tvList.getText().toString().trim();
                if ("".equals(product_code)) {
                    showOnUI("产品序列号不能为空");
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        String method = "RecordHighway";
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("ProductNumber", product_code);
                        new WrappedHttpUtil.Builder(HighwayUploadingOrSaveActivity.this)
                                .dataRepairRequest(method, params)
                                .setL(new WrappedHttpUtil.Builder.Listener() {
                                    @Override
                                    public void onSuccess(int responseCode, String result) {
                                        boolean isJson = JsonParse.isJson(result);
                                        if (isJson) {
                                            String message = JsonParse.judgeJson(result);
                                            if ("0".equals(message)) {
                                                showOnThread("该设备不存在维修记录");
                                            } else {
                                                List<Map<String, String>> repair_record = JsonParse.analysisProductRepairRecordByHigway(message);
                                                ListMapSerializable listMapSerializable = new ListMapSerializable();
                                                listMapSerializable.setListMap(repair_record);
                                                Intent intent = new Intent(HighwayUploadingOrSaveActivity.this, RepairRecordActivity.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putSerializable("repair_record", listMapSerializable);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
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
                break;
            default:
                break;
        }
    }

    /**
     * 上传(修改)安装信息字符串格式错误
     */
    private void uploadingMistake(String interface_message) {
        showOnThread(interface_message + "\r\n请联系技术人员");
        HigwayProductTable product_table = new HigwayProductTable();
        product_table.setProduct_state("已保存");
        String result_sql = updateLocalSQL(product_table);//上传失败保存数据库
        if ("0".equals(result_sql)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOnUI(getResources().getString(R.string.sql_database_null));
                        }
                    }, 1000);
                }
            });
        } else if ("1".equals(result_sql)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOnUI(getResources().getString(R.string.sql_save_ok));
                        }
                    }, 1000);
                }
            });
        } else if ("2".equals(result_sql)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOnUI(getResources().getString(R.string.sql_save_error));
                        }
                    }, 1000);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOnUI(getResources().getString(R.string.mistake_unknown));
                        }
                    }, 1000);
                }
            });
        }
    }

    /**
     * 上传(修改)安装信息成功
     */
    private void uploadingOK(String interface_message) {
        showOnThread(interface_message);
        HigwayProductTable productTable = new HigwayProductTable();
        productTable.setProduct_state("已提交");
        String result_sql = updateLocalSQL(productTable);//上传成功保存数据库
        if ("0".equals(result_sql)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOnUI(getResources().getString(R.string.sql_database_null));
                        }
                    }, 1000);
                }
            });
            return;
        } else if ("1".equals(result_sql)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sp.edit().putString("order_message", "uploading").apply();
                            showOnUI(getResources().getString(R.string.sql_save_ok));
                            LayoutInflater inflater = getLayoutInflater();
                            View product_list_view = inflater.inflate(R.layout.product_list_higway, null);
                            final EditText et_input = (EditText) product_list_view.findViewById(R.id.et_input);
                            new EditDialog.Builder(HighwayUploadingOrSaveActivity.this)
                                    .setTitle("请输入设备ID")
                                    .setContentView(product_list_view)
                                    .setCanTouchOutside(false)
                                    .setCancellable(false)
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String pro_list = et_input.getText().toString().trim().toUpperCase();
                                            if ("".equals(pro_list)) {
                                                showOnUI("设备ID不能为空");
                                                return;
                                            }
                                            tvList.setText(pro_list);
                                            mtvPro_Type.setText("");
                                            mtvPro_Name.setText("");
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    })
                                    .create().show();
                        }
                    }, 1000);
                }
            });
        } else if ("2".equals(result_sql)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOnUI(getResources().getString(R.string.sql_save_error));
                        }
                    }, 1000);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOnUI(getResources().getString(R.string.mistake_unknown));
                        }
                    }, 1000);
                }
            });
        }
    }

    /**
     * 上传(修改)安装信息失败
     */
    private void uploadingError(String interface_message) {
        showOnThread(interface_message + "\r\n请重启软件或联系技术人员");
        HigwayProductTable product_table = new HigwayProductTable();
        product_table.setProduct_state("已保存");
        String result_sql = updateLocalSQL(product_table);//上传失败保存数据库
        if ("0".equals(result_sql)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOnUI(getResources().getString(R.string.sql_database_null));
                        }
                    }, 1000);
                }
            });
        } else if ("1".equals(result_sql)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOnUI(getResources().getString(R.string.sql_save_ok));
                        }
                    }, 1000);
                }
            });
        } else if ("2".equals(result_sql)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOnUI(getResources().getString(R.string.sql_save_error));
                        }
                    }, 1000);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOnUI(getResources().getString(R.string.mistake_unknown));
                        }
                    }, 1000);
                }
            });
        }
    }

    /**
     * 保存数据库——上传(修改)信息异常
     */
    private void saveLocalSQLByException() {
        HigwayProductTable product_table = new HigwayProductTable();
        product_table.setProduct_state("已保存");
        String result_sql = updateLocalSQL(product_table);//异常错误--保存数据库
        if ("0".equals(result_sql)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOnUI(getResources().getString(R.string.sql_database_null));
                        }
                    }, 1000);
                }
            });
        } else if ("1".equals(result_sql)) {
            sp.edit().putString("order_message", "uploading").apply();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOnUI(getResources().getString(R.string.sql_save_ok));
                        }
                    }, 1000);
                }
            });
        } else if ("2".equals(result_sql)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOnUI(getResources().getString(R.string.sql_save_error));
                        }
                    }, 1000);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showOnUI(getResources().getString(R.string.mistake_unknown));
                        }
                    }, 1000);
                }
            });
        }
    }

    /**
     * 更新保存状态(已保存、已提交)
     *
     * @param product_table
     * @return
     */
    private String updateLocalSQL(HigwayProductTable product_table) {
        if (db != null && db.isOpen()) {
            List<HigwayProductTable> productTableList = DataSupport
                    .select("*")
                    .where("type_code = ? and productId = ?", type_code, product_code)
                    .find(HigwayProductTable.class);
            if (productTableList.size() == 0) {
                //保存
                product_table.setType_code(type_code);
                product_table.setOrder_engineer(order_engineer);
                product_table.setUserId(userID);
                product_table.setProductType(product_type);
                product_table.setProductName(product_name);
                product_table.setProductId(product_code);
                product_table.setProductSim(product_sim);
                product_table.setFaultCause(faultCause);
                product_table.setMaintResult(maintResult);
                product_table.setVehCode(vehCode);
                product_table.setInstallAddress(installAddress);
                product_table.setInstallTime(installTime);
                product_table.setSql_time(sql_time);
                product_table.setInstallRemark(pro_remark);
                boolean save = product_table.save();
                if (save) {
                    return OPERATE_OK;
                } else {
                    return OPERATE_ERROR;
                }
            } else {
                //更新
                product_table.setProductType(product_type);
                product_table.setProductName(product_name);
                product_table.setProductId(product_code);
                product_table.setProductSim(product_sim);
                product_table.setFaultCause(faultCause);
                product_table.setMaintResult(maintResult);
                product_table.setVehCode(vehCode);
                product_table.setInstallAddress(installAddress);
                product_table.setInstallTime(installTime);
                product_table.setSql_time(sql_time);
                product_table.setInstallRemark(pro_remark);
                int i = product_table.updateAll("type_code = ? and productId = ?", type_code, product_code);
                if (i > 0) {
                    return OPERATE_OK;
                } else {
                    return OPERATE_ERROR;
                }
            }
        } else {
            return DATABASE_NULL;
        }
    }

    /**
     * 上传或保存信息——赋值
     */
    private void saveOrSubmitMessage() {
        product_type = mtvPro_Type.getText().toString().trim().replace(",", "");
        product_name = mtvPro_Name.getText().toString().trim().replace(",", "");
        product_code = tvList.getText().toString().trim();
        product_sim = et_SimCard.getText().toString().trim();
        faultCause = etFaultcause.getText().toString().trim();
        maintResult = etMaintResult.getText().toString().trim();
        installAddress = et_install_address.getText().toString().trim();
        installTime = tv_install_time.getText().toString().trim();
        vehCode = etVehCode.getText().toString().trim();
        sql_time = tvDate.getText().toString().trim();
        pro_remark = et_product_remark.getText().toString().trim();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void showOnThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.popToast(HighwayUploadingOrSaveActivity.this, message);
            }
        });
    }

    public void showOnUI(String message) {
        MyToast.popToast(HighwayUploadingOrSaveActivity.this, message);
    }

    /**
     * 对应方法 runOnUiThread(new Runnable() {});
     *
     * @param message
     */
    private void runOnUiThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showOnUI(message);
                        finish();
                    }
                }, 1000);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sp.edit()
                .remove("flag")
                .remove("product_list")
                .apply();
    }


}
