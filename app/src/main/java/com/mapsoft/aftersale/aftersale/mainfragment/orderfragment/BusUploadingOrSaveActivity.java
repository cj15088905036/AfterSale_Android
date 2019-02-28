package com.mapsoft.aftersale.aftersale.mainfragment.orderfragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.mapsoft.aftersale.R;
import com.mapsoft.aftersale.RdApplication;
import com.mapsoft.aftersale.bean.BusProductTable;
import com.mapsoft.aftersale.utils.Analyticbarcode;
import com.mapsoft.aftersale.utils.EditDialog;
import com.mapsoft.aftersale.utils.JsonParse;
import com.mapsoft.aftersale.utils.KeywordAdapter;
import com.mapsoft.aftersale.utils.MapSerializable;
import com.mapsoft.aftersale.utils.MyToast;
import com.mapsoft.aftersale.utils.NoDoubleClickListener;
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
 * 上传或保存维修信息(公交)
 */

public class BusUploadingOrSaveActivity extends Activity {
    private static final String MESSAGE_NULL = "0";
    private static final String MESSAGE_OK = "1";
    private static final String MESSAGE_ERROR = "2";
    private static final String MESSAGE_UNKNOWN = "3";

    private TextView
            tv_title,           //标题
            tvList,             //产品序列号
            tvDate,             //出产时间
            tvRepairName;       //维修人员

    private EditText
            et_fault_remark,    //故障备注
            etVehCode,         //车牌号
            etFaultcause,       //故障原因
            etMaintResult,      //维修结果
            etMaterialCost,     //材料费
            etMaintenanceCost;  //维修费

    private Button
            btn_select,//查询序列号在数据库中是否存在
            btnSave,//保存设备信息到本地数据库
            btnSubmit,//上传信息
            btnBack, //返回
            btnLook;//查看维修记录

    private LinearLayout
            ll_fault_remark,//故障备注
            ll_cost;//维修费用

    private SharedPreferences sp;
    private MultiAutoCompleteTextView mtvPro_Name;//产品名称


    private String repair_code;//维修人员ID
    private String repair_name;//维修人员名称
    private String order_code;//订单编号
    private String product_code;//产品序列号
    private String product_name;//产品名称
    private String veh_code;//车牌号
    private String fault_cause;//故障原因
    private String maint_result;//维修结果
    private String material_cost;//材料费
    private String maintenance_cost;//维修费
    private String input_time;//录入数据库时间
    private String fault_remark;//故障设备备注

    private String string_data = "";//上传故障产品参数
    private String order_type;
    private String type_code = "";//作为本地数据库的订单标识


    //    private Map<String, String> sup_product;//设备补充记录
    private Map<String, String> orderMap;//订单信息

    private Calendar calendar;

    private View sll_height;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.busuploadingorsave);
        sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        init();
        Log.e("UploadingOrSaveMessageActivity", sp.getAll() + "");
        //上传维修结果
        btnSubmit.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
//                product_code = tvList.getText().toString().trim();
                setProduct_message();//上传设备信息赋值
                if ("".equals(product_name)) {
                    showOnUI("产品名称不能为空");
                    return;
                }
                if ("".equals(veh_code)) {
                    showOnUI("车牌号不能为空");
                    return;
                }
                if ("".equals(fault_cause)) {
                    showOnUI("故障原因不能为空");
                    return;
                }
                if ("".equals(maint_result)) {
                    showOnUI("维修结果不能为空");
                    return;
                }
                if ("W".equals(order_type)) {
                    if ("".equals(material_cost)) {
                        showOnUI("材料费不能为空");
                        return;
                    }
                    if ("".equals(maintenance_cost)) {
                        showOnUI("人工费不能为空");
                        return;
                    }
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            String method = "ResultInfo";
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("UserId", repair_code);
                            params.put("OrderCode", order_code);
                            params.put("ProductName", product_name);
                            params.put("VehCode", veh_code);
                            params.put("FaultCause", fault_cause);
                            params.put("MaintResult", maint_result);
                            params.put("ProductNumber", product_code);
                            params.put("Name", repair_name);
                            params.put("MaterialCost", material_cost);
                            params.put("MaintenanceCost", maintenance_cost);
                            new WrappedHttpUtil.Builder(BusUploadingOrSaveActivity.this)
                                    .dataRepairRequest(method, params)
                                    .setL(new WrappedHttpUtil.Builder.Listener() {
                                        @Override
                                        public void onSuccess(int responseCode, String result) {
                                            try {
                                                boolean isJson = JsonParse.isJson(result);
                                                if (isJson) {
                                                    String message = JsonParse.judgeJson(result);
                                                    if ("0".equals(message)) {
                                                        BusProductTable productTable = new BusProductTable();
                                                        productTable.setProduct_state("已保存");
                                                        String result_database = updateLocalSQL(productTable);//上传维修产品失败
                                                        show_localSQL_result(result_database);//上传维修产品失败
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        showOnUI("上传失败,请联系技术人员");
                                                                    }
                                                                }, 1000);
                                                            }
                                                        });
                                                    } else if ("1".equals(message)) {
                                                        showOnThread("上传成功");
                                                        Thread.sleep(1000);
                                                        BusProductTable productTable = new BusProductTable();
                                                        productTable.setProduct_state("已提交");
                                                        String result_database = updateLocalSQL(productTable);//上传维修产品成功
                                                        if ("0".equals(result_database)) {
                                                            showOnThread(getResources().getString(R.string.sql_database_null));
                                                        } else if ("1".equals(result_database)) {
                                                            sp.edit().putString("order_message", "uploading").apply();
                                                            showOnThread(getResources().getString(R.string.sql_save_ok));
                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    new Handler().postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            finish();
                                                                        }
                                                                    }, 500);
                                                                }
                                                            });
                                                        } else if ("2".equals(result_database)) {
                                                            showOnThread(getResources().getString(R.string.sql_save_error));
                                                        } else if ("3".equals(result_database)) {
                                                            showOnThread(getResources().getString(R.string.mistake_unknown));
                                                        }
                                                    } else {
                                                        BusProductTable productTable = new BusProductTable();
                                                        productTable.setProduct_state("已保存");
                                                        String result_database = updateLocalSQL(productTable);//上传维修产品未知错误
                                                        show_localSQL_result(result_database);//上传维修产品未知错误
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        showOnThread("上传维修结果\r\n" +
                                                                                getResources().getString(R.string.mistake_unknown));
                                                                    }
                                                                }, 1000);
                                                            }
                                                        });
                                                    }
                                                } else {
                                                    BusProductTable productTable = new BusProductTable();
                                                    productTable.setProduct_state("已保存");
                                                    String result_database = updateLocalSQL(productTable);//上传维修产品解析数据错误
                                                    show_localSQL_result(result_database);//上传维修产品解析数据错误
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    showOnThread("上传维修结果\r\n" +
                                                                            getResources().getString(R.string.mistake_dataResolve));
                                                                }
                                                            }, 1000);
                                                        }
                                                    });

                                                }
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                        @Override
                                        public void onFail(final int responseCode, final String result) {
                                            BusProductTable productTable = new BusProductTable();
                                            productTable.setProduct_state("已保存");
                                            String result_database = updateLocalSQL(productTable);//上传维修产品异常提示
                                            show_localSQL_result(result_database);//上传维修产品异常提示
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ExceptionMessage("上传维修结果", responseCode, result);
                                                        }
                                                    }, 1000);
                                                }
                                            });
                                        }
                                    }).execute();
                        }
                    }.start();

                } else if ("T".equals(order_type)) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            String method = "AddFaultProuctInfo";
                            string_data = "{\n" +
                                    "    \"InstallContactListId\": \"" + order_code + "\",\n" +
                                    "    \"ProductCode\": \"" + product_code + "\",\n" +
                                    "    \"ProductType\": \"" + "" + "\",\n" +
                                    "    \"ProductName\": \"" + product_name + "\",\n" +
                                    "    \"Remarks\": \"" + fault_remark + "\",\n" +
                                    "    \"FaultCause\": \"" + fault_cause + "\",\n" +
                                    "    \"MaintResult\": \"" + maint_result + "\"\n" +
                                    "}";
                            Map<String, String> args = new HashMap<String, String>();
                            args.put("Data", string_data.replace(" ", ""));
                            new WrappedHttpUtil.Builder(BusUploadingOrSaveActivity.this)
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
                                                if ("1".equals(message)) {
                                                    //上传成功
                                                    showOnThread("上传故障产品成功");
                                                    try {
                                                        Thread.sleep(1000);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    BusProductTable productTable = new BusProductTable();
                                                    productTable.setProduct_state("已提交");
                                                    String result_database = updateLocalSQL(productTable);//上传故障产品成功
                                                    if ("0".equals(result_database)) {
                                                        showOnThread(getResources().getString(R.string.sql_database_null));
                                                    } else if ("1".equals(result_database)) {
                                                        sp.edit().putString("order_message", "uploading").apply();
                                                        showOnThread(getResources().getString(R.string.sql_save_ok));
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        finish();
                                                                    }
                                                                }, 500);
                                                            }
                                                        });
                                                    } else if ("2".equals(result_database)) {
                                                        showOnThread(getResources().getString(R.string.sql_save_error));
                                                    } else if ("3".equals(result_database)) {
                                                        showOnThread(getResources().getString(R.string.mistake_unknown));
                                                    }
                                                } else if ("0".equals(message)) {
                                                    //上传失败
                                                    BusProductTable productTable = new BusProductTable();
                                                    productTable.setProduct_state("已保存");
                                                    String result_database = updateLocalSQL(productTable);//上传故障产品失败
                                                    show_localSQL_result(result_database);//上传故障产品失败
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    showOnUI("上传故障产品信息失败\r\n请联系技术人员");
                                                                }
                                                            }, 1000);
                                                        }
                                                    });
                                                } else if ("-1".equals(message)) {
                                                    //JSON格式错误
                                                    showOnThread("-1:格式错误\r\n请联系技术人员");
                                                } else if ("-2".equals(message)) {
                                                    //重复上传——先删除后台已上传的数据，再上传
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            View view = getLayoutInflater().inflate(R.layout.names_item, null);
                                                            TextView tv_message = (TextView) view.findViewById(R.id.tv_name);
                                                            tv_message.setGravity(Gravity.CENTER);
                                                            tv_message.setText("该设备已上传过信息——是否修改");
                                                            new EditDialog.Builder(BusUploadingOrSaveActivity.this)
                                                                    .setTitle("修改信息")
                                                                    .setContentView(view)
                                                                    .setCancellable(false)
                                                                    .setCanTouchOutside(false)
                                                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(final DialogInterface dialog, int which) {
                                                                            new Thread() {
                                                                                @Override
                                                                                public void run() {
                                                                                    super.run();
                                                                                    String method = "DeleteFaultProuctInfo";
                                                                                    Map<String, String> args = new HashMap<String, String>();
                                                                                    args.put("OrderId", order_code);
                                                                                    args.put("ProductCode", product_code);
                                                                                    new WrappedHttpUtil.Builder(BusUploadingOrSaveActivity.this)
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
                                                                                                            showOnThread("删除故障产品信息失败\r\n请重启软件或联系技术人员");
                                                                                                        } else if ("1".equals(message)) {
                                                                                                            //删除成功——重新上传
                                                                                                            uploadingFaultProduct(dialog);//重新上传
                                                                                                        } else {
                                                                                                            //未知错误
                                                                                                            showOnThread("删除故障产品信息\r\n" +
                                                                                                                    getResources().getString(R.string.mistake_unknown));
                                                                                                        }
                                                                                                    } else {
                                                                                                        showOnThread("删除故障产品信息\r\n" +
                                                                                                                getResources().getString(R.string.mistake_dataResolve));
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onFail(int responseCode, String result) {

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
                                                                            finish();
                                                                        }
                                                                    }).create().show();
                                                        }
                                                    });
                                                } else {
                                                    //未知错误
                                                    BusProductTable productTable = new BusProductTable();
                                                    productTable.setProduct_state("已保存");
                                                    String result_database = updateLocalSQL(productTable);//上传故障产品未知错误
                                                    show_localSQL_result(result_database);//上传故障产品未知错误
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    showOnUI("上传故障产品信息\r\n" +
                                                                            getResources().getString(R.string.mistake_unknown));
                                                                }
                                                            }, 1000);
                                                        }
                                                    });
                                                }
                                            } else {
                                                BusProductTable productTable = new BusProductTable();
                                                productTable.setProduct_state("已保存");
                                                String result_database = updateLocalSQL(productTable);//上传故障产品解析数据错误
                                                show_localSQL_result(result_database);//上传故障产品解析数据错误
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                showOnUI("上传故障产品信息\r\n" +
                                                                        getResources().getString(R.string.mistake_dataResolve));
                                                            }
                                                        }, 1000);
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onFail(final int responseCode, final String result) {
                                            BusProductTable productTable = new BusProductTable();
                                            productTable.setProduct_state("已保存");
                                            String result_database = updateLocalSQL(productTable);//上传维修产品异常提示
                                            show_localSQL_result(result_database);//上传维修产品异常提示
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ExceptionMessage("上传故障产品", responseCode, result);
                                                        }
                                                    }, 1000);
                                                }
                                            });
                                        }
                                    }).execute();
                        }
                    }.start();
                }
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProductTable productTable = new BusProductTable();
                productTable.setProduct_state("已保存");
                String result_database = updateLocalSQL(productTable);//保存数据库
                if ("0".equals(result_database)) {
                    showOnUI(getResources().getString(R.string.sql_database_null));
                } else if ("1".equals(result_database)) {
                    sp.edit().putString("order_message", "uploading").apply();
                    showOnUI(getResources().getString(R.string.sql_save_ok));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 500);
                } else if ("2".equals(result_database)) {
                    showOnUI(getResources().getString(R.string.sql_save_error));
                } else if ("3".equals(result_database)) {
                    showOnUI(getResources().getString(R.string.mistake_unknown));
                }
            }
        });
        btnLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(product_code)) {
                    showOnUI("产品序列号不能为空");
                    return;
                }
                // TODO: 2018/3/26 跳转到产品维修记录界面解析，并给出提示
                Intent intent = new Intent(BusUploadingOrSaveActivity.this, RepairRecordActivity.class);
                intent.putExtra("product_code", product_code);
                startActivity(intent);
            }
        });
        btn_select.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if ("".equals(product_code)) {
                    showOnUI("产品序列号不能为空");
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        String method = "ProductInfo";
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("ProductNumber", product_code);
                        new WrappedHttpUtil.Builder(BusUploadingOrSaveActivity.this)
                                .dataRepairRequest(method, params)
                                .setL(new WrappedHttpUtil.Builder.Listener() {
                                    @Override
                                    public void onSuccess(int responseCode, String result) {
                                        boolean isJson = JsonParse.isJson(result);
                                        if (isJson) {
                                            String message = JsonParse.judgeJson(result);
                                            if ("0".equals(message)) {
                                                //没有记录
                                                showOnThread("不存在记录");
                                            } else {
                                                showOnThread("信息已取出");
                                                final Map<String, String> sup_product = JsonParse.analysisSupProduct(message);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        tvDate.setText(sup_product.get("time_create"));
                                                        mtvPro_Name.setText(sup_product.get("product_type") + sup_product.get("product_name") + "");
                                                        etVehCode.setText(sup_product.get("car_number"));
                                                    }
                                                });
                                            }
                                        } else {
                                            showOnThread("查询补充记录\r\n" + getResources().getString(R.string.mistake_dataResolve));
                                        }
                                    }

                                    @Override
                                    public void onFail(int responseCode, String result) {
                                        ExceptionMessage("查询补充记录", responseCode, result);
                                    }
                                }).execute();
                    }
                }.start();
            }
        });
    }

    /**
     * 重新上传故障产品信息
     *
     * @param dialog
     */
    private void uploadingFaultProduct(final DialogInterface dialog) {
        String method = "AddFaultProuctInfo";
        string_data = "{\n" +
                "    \"InstallContactListId\": \"" + order_code + "\",\n" +
                "    \"ProductCode\": \"" + product_code + "\",\n" +
                "    \"ProductType\": \"" + "" + "\",\n" +
                "    \"ProductName\": \"" + product_name + "\",\n" +
                "    \"Remarks\": \"" + fault_remark + "\",\n" +
                "    \"FaultCause\": \"" + fault_cause + "\",\n" +
                "    \"MaintResult\": \"" + maint_result + "\"\n" +
                "}";
        Map<String, String> args = new HashMap<String, String>();
        args.put("Data", string_data.replace(" ", ""));
        new WrappedHttpUtil.Builder(BusUploadingOrSaveActivity.this)
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
                            if ("1".equals(message)) {
                                //上传成功
                                showOnThread("修改故障产品成功");
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                BusProductTable productTable = new BusProductTable();
                                productTable.setProduct_state("已提交");
                                String result_database = updateLocalSQL(productTable);//上传故障产品成功
                                if ("0".equals(result_database)) {
                                    showOnThread(getResources().getString(R.string.sql_database_null));
                                } else if ("1".equals(result_database)) {
                                    sp.edit().putString("order_message", "uploading").apply();
                                    showOnThread(getResources().getString(R.string.sql_save_ok));
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finish();
                                                    dialog.dismiss();
                                                }
                                            }, 500);
                                        }
                                    });
                                } else if ("2".equals(result_database)) {
                                    showOnThread(getResources().getString(R.string.sql_save_error));
                                } else if ("3".equals(result_database)) {
                                    showOnThread(getResources().getString(R.string.mistake_unknown));
                                }
                            } else if ("0".equals(message)) {
                                //上传失败
                                BusProductTable productTable = new BusProductTable();
                                productTable.setProduct_state("已保存");
                                String result_database = updateLocalSQL(productTable);//上传故障产品失败
                                show_localSQL_result(result_database);//上传故障产品失败
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                showOnUI("修改故障产品信息失败\r\n请联系技术人员");
                                            }
                                        }, 1000);
                                    }
                                });
                            } else if ("-1".equals(message)) {
                                //JSON格式错误
                                showOnThread("-1:格式错误\r\n请联系技术人员");
                            } else {
                                //未知错误
                                BusProductTable productTable = new BusProductTable();
                                productTable.setProduct_state("已保存");
                                String result_database = updateLocalSQL(productTable);//上传故障产品未知错误
                                show_localSQL_result(result_database);//上传故障产品未知错误
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                showOnUI("修改故障产品信息\r\n" +
                                                        getResources().getString(R.string.mistake_unknown));
                                            }
                                        }, 1000);
                                    }
                                });
                            }
                        } else {
                            BusProductTable productTable = new BusProductTable();
                            productTable.setProduct_state("已保存");
                            String result_database = updateLocalSQL(productTable);//上传故障产品解析数据错误
                            show_localSQL_result(result_database);//上传故障产品解析数据错误
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            showOnUI("修改故障产品信息\r\n" +
                                                    getResources().getString(R.string.mistake_dataResolve));
                                        }
                                    }, 1000);
                                }
                            });
                        }
                    }

                    @Override
                    public void onFail(final int responseCode, final String result) {
                        BusProductTable productTable = new BusProductTable();
                        productTable.setProduct_state("已保存");
                        String result_database = updateLocalSQL(productTable);//上传维修产品异常提示
                        show_localSQL_result(result_database);//上传维修产品异常提示
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ExceptionMessage("修改故障产品", responseCode, result);
                                    }
                                }, 1000);
                            }
                        });
                    }
                }).execute();

    }

    /**
     * 初始化控件以及参数赋值
     */
    private void init() {
        orderMap = RdApplication.get().getOrderMap();
        order_code = orderMap.get("order_code");
        order_type = orderMap.get("order_type");
        type_code = order_type.trim() + order_code.trim();
        repair_name = orderMap.get("order_engineer");
        mtvPro_Name = (MultiAutoCompleteTextView) findViewById(R.id.mtvPro_Name);
        List<String> product_name = new ArrayList<>();//所有产品集合
        String[] strings = getResources().getStringArray(R.array.bus_product_name);
        for (int i = 0; i < strings.length; i++) {
            product_name.add(strings[i]);
        }
        KeywordAdapter adapter = new KeywordAdapter(this, R.layout.names_item, product_name);
        mtvPro_Name.setAdapter(adapter);
        calendar = Calendar.getInstance();

        tv_title = (TextView) findViewById(R.id.tv_title);
        mtvPro_Name.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        tvList = (TextView) findViewById(R.id.tvList);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvRepairName = (TextView) findViewById(R.id.tvRepairName);
        etVehCode = (EditText) findViewById(R.id.etVehCode);
        etFaultcause = (EditText) findViewById(R.id.etFaultcause);
        etMaintResult = (EditText) findViewById(R.id.etMaintResult);
        etMaterialCost = (EditText) findViewById(R.id.etMaterialCost);
        etMaterialCost.setInputType(TYPE_CLASS_PHONE);
        etMaintenanceCost = (EditText) findViewById(R.id.etMaintenanceCost);
        etMaintenanceCost.setInputType(TYPE_CLASS_PHONE);
        //公交安装信息——故障设备上传
        et_fault_remark = (EditText) findViewById(R.id.et_fault_remark);
        ll_fault_remark = (LinearLayout) findViewById(R.id.ll_fault_remark);
        ll_cost = (LinearLayout) findViewById(R.id.ll_cost);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnBack = (Button) findViewById(R.id.btnBack);
        btn_select = (Button) findViewById(R.id.btn_select);
        btnLook = (Button) findViewById(R.id.btnLook);
        btnSave = (Button) findViewById(R.id.btnSave);
        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        if (result != null) {
            if (result.startsWith("B") && result.length() == 18) {
                tvList.setText(result);
                btn_select.setVisibility(View.VISIBLE);
                tvDate.setText(calendar.get(Calendar.YEAR) + "年"
                        + (calendar.get(Calendar.MONTH) + 1) + "月"
                        + calendar.get(Calendar.DAY_OF_MONTH) + "日");
            } else if ((result.startsWith("01") || result.startsWith("02")) && result.length() == 18) {
                btn_select.setVisibility(View.GONE);
                Map<String, String> map = Analyticbarcode.arrAnalytic(BusUploadingOrSaveActivity.this, result);
                mtvPro_Name.setText(map.get("Pro_Name").trim() + map.get("Hdv") + map.get("Sdv"));
                tvList.setText(map.get("mList").trim());
                tvDate.setText(map.get("Pro_Date").trim());
            }
        }
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.get("map") != null) {//已上传或保存的信息
            MapSerializable myMap = (MapSerializable) bundle.get("map");
            Map<String, String> saveOverMap = myMap.getMap();
            tvList.setText(saveOverMap.get("product_code"));
            tvDate.setText(saveOverMap.get("input_time"));
            mtvPro_Name.setText(saveOverMap.get("product_name"));
            etVehCode.setText(saveOverMap.get("veh_code"));
            etFaultcause.setText(saveOverMap.get("fault_cause"));
            etMaintResult.setText(saveOverMap.get("maint_result"));
            tvRepairName.setText(saveOverMap.get("repair_name"));
            etMaterialCost.setText(saveOverMap.get("material_cost"));
            etMaintenanceCost.setText(saveOverMap.get("maintenance_cost"));
            et_fault_remark.setText(saveOverMap.get("fault_remark"));
        }

        if ("W".equals(order_type)) {
            ll_fault_remark.setVisibility(View.GONE);
            tv_title.setText("设备维修信息");
        } else if ("T".equals(order_type)) {
            ll_cost.setVisibility(View.GONE);
            tv_title.setText("故障设备信息");
            btnLook.setVisibility(View.GONE);
        }


        tvRepairName.setText(repair_name);
        product_code = tvList.getText().toString().trim();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void showOnThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.popToast(BusUploadingOrSaveActivity.this, message);
            }
        });
    }

    public void showOnUI(String message) {
        MyToast.popToast(BusUploadingOrSaveActivity.this, message);
    }

    /**
     * 将填写的信息赋值
     */
    public void setProduct_message() {
        repair_code = RdApplication.get().getUser().getUser_id();
        product_code = tvList.getText().toString().trim();
        product_name = mtvPro_Name.getText().toString().trim().replace(",", "").replace(" ", "");
        veh_code = etVehCode.getText().toString().trim();
        fault_cause = etFaultcause.getText().toString().trim();
        maint_result = etMaintResult.getText().toString().trim();
        material_cost = etMaterialCost.getText().toString().trim();
        maintenance_cost = etMaintenanceCost.getText().toString().trim();
        input_time = tvDate.getText().toString().trim();
        fault_remark = et_fault_remark.getText().toString().trim();
    }

    /**
     * 更新保存状态(已保存、已提交)
     *
     * @param product_save
     */
    public String updateLocalSQL(BusProductTable product_save) {
        SQLiteDatabase database = RdApplication.getDatabase();
        if (database != null) {
            setProduct_message();//保存本地数据库
            //查询数据
            List<BusProductTable> products = DataSupport
                    .select("*")
                    .where("type_code = ? and product_code = ? ", type_code, product_code)
                    .find(BusProductTable.class);
            BusProductTable productTable = new BusProductTable();
            if (products.size() != 0) {
                //更新
                productTable.setProduct_name(product_name);
                productTable.setVeh_code(veh_code);
                productTable.setFault_cause(fault_cause);
                productTable.setMaint_result(maint_result);
                productTable.setMaterial_cost(material_cost);
                productTable.setMaintenance_cost(maintenance_cost);
                productTable.setFault_remark(fault_remark);
                productTable.setProduct_state(product_save.getProduct_state());
                int i = productTable.updateAll("type_code = ? and product_code = ?",
                        type_code, product_code);
                if (i > 0) {
                    return MESSAGE_OK;
                } else if (i < 0) {
                    return MESSAGE_ERROR;
                }
            } else {
                //插入
                productTable.setRepair_code(repair_code);
                productTable.setRepair_name(repair_name);
                productTable.setType_code(type_code);
                productTable.setProduct_code(product_code);
                productTable.setProduct_name(product_name);
                productTable.setVeh_code(veh_code);
                productTable.setFault_cause(fault_cause);
                productTable.setMaint_result(maint_result);
                productTable.setMaterial_cost(material_cost);
                productTable.setMaintenance_cost(maintenance_cost);
                productTable.setInput_time(input_time);
                productTable.setFault_remark(fault_remark);
                productTable.setProduct_state(product_save.getProduct_state());
                boolean save = productTable.save();
                if (save) {
                    return MESSAGE_OK;
                } else {
                    return MESSAGE_ERROR;
                }
            }
        } else {
            return MESSAGE_NULL;
        }
        return MESSAGE_UNKNOWN;
    }

    /**
     * 展示操作数据库结果
     *
     * @param result_database
     */
    private void show_localSQL_result(String result_database) {
        if ("0".equals(result_database)) {
            showOnThread(getResources().getString(R.string.sql_database_null));
        } else if ("1".equals(result_database)) {
            sp.edit().putString("order_message", "uploading").apply();
            showOnThread(getResources().getString(R.string.sql_save_ok));
        } else if ("2".equals(result_database)) {
            showOnThread(getResources().getString(R.string.sql_save_error));
        } else if ("3".equals(result_database)) {
            showOnThread(getResources().getString(R.string.mistake_unknown));
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
}
