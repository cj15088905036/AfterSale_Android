package com.mapsoft.aftersale.aftersale.mainfragment.orderfragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.mapsoft.aftersale.R;
import com.mapsoft.aftersale.RdApplication;
import com.mapsoft.aftersale.utils.KeywordAdapter;
import com.mapsoft.aftersale.utils.EditDialog;
import com.mapsoft.aftersale.utils.JsonParse;
import com.mapsoft.aftersale.utils.MyToast;
import com.mapsoft.aftersale.utils.WrappedHttpUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.InputType.TYPE_CLASS_PHONE;


/**
 * Created by Administrator on 2017/10/17.
 * 临时维修单
 * <p>
 */

public class TemporaryActivity extends Activity implements View.OnClickListener {
    private Button btnBack, btnCreate, btnAddShebei;
    private AutoCompleteTextView act_Client, act_contact_name;
    private SimpleAdapter adapterProductList;
    private List<Map<String, String>> productList;//添加的产品列表
    private ListView lv_product;
    private EditDialog dialog_customer_person;//客户联系人弹窗选择
    private SharedPreferences sp;
    private EditText etRemark, et_contact_phone, et_address;

    private List<Map<String, String>> allCustomersList;//所有客户信息
    private List<String> allCustomerNameList;//所有客户名称信息
    private List<Map<String, String>> allCustomer_personList;//指定客户下所有客户联系人信息
    List<String> person_name_phone_list;//客户联系人名称+电话


    private String customerID = "";//客户ID
    private String customerAddress = "";//客户地址
    private String contactPersonID = "";//联系人ID
    private String customerPersonPhone = "";//联系人电话
    private String order_remark = "";//下单备注
    private String productMessage = "";//产品信息
    private String repairAddress = "";

    private KeywordAdapter adapter;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddShebei:
                LayoutInflater li = getLayoutInflater();
                final View v1 = li.inflate(R.layout.shebeimessage, null);
                new EditDialog.Builder(TemporaryActivity.this)
                        .setTitle("添加设备")
                        .setContentView(v1)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText etPro_Name = (EditText) v1.findViewById(R.id.etPro_Name);
                                EditText etVehCode = (EditText) v1.findViewById(R.id.etVehCode);
                                EditText etShowCause = (EditText) v1.findViewById(R.id.et_ShowCause);
                                if ("".equals(etPro_Name.getText().toString())) {
                                    showToastOnUI("产品名称不能为空");
                                    return;
                                }
                                if ("".equals(etVehCode.getText().toString())) {
                                    showToastOnUI("车牌号不能为空");
                                    return;
                                }
                                if ("".equals(etShowCause.getText().toString())) {
                                    showToastOnUI("故障描述不能为空");
                                    return;
                                }
                                Map<String, String> mapProduct = new HashMap<String, String>();//产品列表
                                mapProduct.put("product_name", etPro_Name.getText().toString());
                                mapProduct.put("car_number", etVehCode.getText().toString().toUpperCase());
                                mapProduct.put("fault_description", etShowCause.getText().toString());
                                productList.add(mapProduct);
                                showProductMessage(productList);
                                showToastOnUI("添加成功");
                                dialog.dismiss();
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
                break;
            case R.id.btnBack:
                back();
                break;
            case R.id.btnCreate:
                customerPersonPhone = et_contact_phone.getText().toString().trim();
                repairAddress = et_address.getText().toString().trim();
                order_remark = etRemark.getText().toString().trim();
                if ("".equals(customerID)) {
                    showToastOnUI("客户名称不能为空");
                    return;
                }
                if ("".equals(contactPersonID)) {
                    showToastOnUI("联系人不能为空");
                    return;
                }
                if ("".equals(customerPersonPhone)) {
                    showToastOnUI("联系方式不能为空");
                    return;
                }
                if ("".equals(repairAddress)) {
                    showToastOnUI("维修地址不能为空");
                    return;
                }
                if ("".equals(order_remark)) {
                    showToastOnUI("下单备注不能为空");
                    return;
                }
                if (productList.size() == 0) {
                    showToastOnUI("报修设备不能为空");
                    return;
                } else {
                    productMessage = getStringProduct(productList);
                }
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        String method = "NewOrder";
                        Map<String, String> args = new HashMap<String, String>();
                        args.put("UserName", RdApplication.get().getUser().getUser_name());
                        args.put("UserId", RdApplication.get().getUser().getUser_id());
                        args.put("CustomerId", customerID);
                        args.put("CustomerAddress", customerAddress);
                        args.put("CustomerPersonId", contactPersonID);
                        args.put("CustomerPersonPhone", customerPersonPhone);
                        args.put("Remark", order_remark);
                        args.put("ProductList", productMessage);
                        new WrappedHttpUtil.Builder(TemporaryActivity.this)
                                .dataRepairRequest(method, args)
                                .setL(new WrappedHttpUtil.Builder.Listener() {
                                    @Override
                                    public void onSuccess(int responseCode, String result) {
                                        boolean isJson = JsonParse.isJson(result);
                                        if (isJson) {
                                            String message = JsonParse.judgeJson(result);
                                            if ("0".equals(message)) {
                                                showToastThread("创建失败");
                                            } else if ("1".equals(message)) {
//                                                sp.edit().putString("create", "1").apply();
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        sendBroadcast(new Intent(getString(R.string.refresh)));
                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                finish();
                                                            }
                                                        }, 500);
                                                    }
                                                });
                                            } else {
                                                showToastThread("创建维修单\r\n" + getResources().getString(R.string.mistake_unknown));
                                            }
                                        } else {
                                            showToastThread("创建维修单\r\n" + getResources().getString(R.string.mistake_dataResolve));
                                        }
                                    }

                                    @Override
                                    public void onFail(int responseCode, String result) {
                                        ExceptionMessage("创建维修单", responseCode, result);
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
     * Toast异常提示
     *
     * @param interfaceType 接口类型
     * @param responseCode  返回码
     * @param result        返回结果
     */
    public void ExceptionMessage(String interfaceType, int responseCode, String result) {
        if (responseCode == 0) {
            if ("123".equals(result)) {
                showToastThread(getResources().getString(R.string.noNetwork));
            }
        }
        if (responseCode == 404) {
            showToastThread(interfaceType + "\r\n" +
                    getResources().getString(R.string.mistake_404));
        }
        if (responseCode == 500) {
            showToastThread(interfaceType + "\r\n" +
                    getResources().getString(R.string.mistake_500));
        }
        if (responseCode == 400) {
            showToastThread(interfaceType + "\r\n" +
                    getResources().getString(R.string.mistake_400));
        }
    }

    /**
     * 获取所有客户信息
     */
    class CustomersAsyncTask extends AsyncTask<String, Integer, Boolean> {
        private List<Map<String, String>> mapList;
        private String str_exception = "";
        private ProgressDialog pd_dialog;

        @Override
        protected void onPreExecute() {
            mapList = new ArrayList<>();
            pd_dialog = new ProgressDialog(TemporaryActivity.this);
            pd_dialog.setTitle("正在搜索请稍后...");
            pd_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd_dialog.setCancelable(false);
            pd_dialog.show();
        }

        @Override
        protected Boolean doInBackground(final String... params) {
            int responseCode = 0;
            HttpURLConnection httpUrlConn = null;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                URL newUrl = new URL(params[0]);
                httpUrlConn = (HttpURLConnection) newUrl.openConnection();
                httpUrlConn.setDoOutput(false);
                httpUrlConn.setDoInput(true);
                httpUrlConn.setUseCaches(false);
                httpUrlConn.setRequestMethod("GET");
                Log.e("访问URL", params[0]);
                httpUrlConn.connect();
                responseCode = httpUrlConn.getResponseCode();
                if (responseCode == 0) {
                    str_exception = getResources().getString(R.string.noNetwork);
                } else if (responseCode == 200) {
                    // 将返回的输入流转换成字符串
                    InputStream inputStream = httpUrlConn.getInputStream();
                    //获取json总长度
                    long totalLength = httpUrlConn.getContentLength();
                    //当前字节长度
                    int curLength = 0;

                    byte[] data = new byte[1024];
                    int temp = 0;
                    while ((temp = inputStream.read(data)) != -1) {
                        curLength += temp;//将每次循环读取的长度添加到当前长度变量中
                        // 计算当前进度:根据文件总长度与当前下载的长度
                        int progress = (int) ((curLength / (float) totalLength) * 100);
                        //将进度发布到主线程中
                        publishProgress(progress);
                        outputStream.write(data, 0, temp);
                        outputStream.flush();
                    }
                    inputStream.close();
                    outputStream.close();
                    String result = outputStream.toString();
                    boolean isJson = JsonParse.isJson(result);
                    if (isJson) {
                        String message = JsonParse.judgeJson(result);
                        if ("0".equals(message)) {
                            str_exception = "没有客户信息";
                        } else {
                            mapList = JsonParse.analysisCustomerMessage(message);
                        }
                    } else {
                        str_exception = "搜索客户:" + getResources().getString(R.string.mistake_dataResolve);
                    }
                } else if (responseCode == 400) {
                    str_exception = "搜索客户:" +
                            getResources().getString(R.string.mistake_400);
                } else if (responseCode == 404) {
                    str_exception = "搜索客户:" +
                            getResources().getString(R.string.mistake_404);
                } else if (responseCode == 500) {
                    str_exception = "搜索客户:" +
                            getResources().getString(R.string.mistake_500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 关闭连接
                httpUrlConn.disconnect();
            }
            return mapList.size() > 0;
        }

        /**
         * 显示更新进度
         *
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            pd_dialog.setProgress(values[0]);//设置更新进度
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                showToastOnUI("搜索完毕");
                allCustomersList.clear();
                allCustomersList = mapList;
                //提取客户名称，按关键字搜索
                for (int i = 0; i < allCustomersList.size(); i++) {
                    allCustomerNameList.add(allCustomersList.get(i).get("customer_name"));
                }
                adapter = new KeywordAdapter(TemporaryActivity.this, R.layout.names_item, allCustomerNameList);
                act_Client.setAdapter(adapter);

                act_contact_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            //获得焦点
                            String customer_name = act_Client.getText().toString().trim();
                            for (int i = 0; i < allCustomersList.size(); i++) {
                                if (customer_name.equals(allCustomersList.get(i).get("customer_name"))) {
                                    customerID = allCustomersList.get(i).get("customer_id");//客户ID
                                    customerAddress = allCustomersList.get(i).get("customer_address");//客户地址
                                    String customer_person = allCustomersList.get(i).get("customer_person");
                                    try {
                                        JSONArray array = new JSONArray(new String(customer_person));
                                        for (int j = 0; j < array.length(); j++) {
                                            JSONObject item = array.getJSONObject(j);
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("person_name", item.getString("person_name"));
                                            map.put("person_id", item.getString("person_id"));
                                            map.put("person_phone", item.getString("person_phone"));
                                            allCustomer_personList.add(map);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            if (allCustomer_personList.size() != 0) {
                                //客户联系人列表
                                for (int i = 0; i < allCustomer_personList.size(); i++) {
                                    person_name_phone_list.add(allCustomer_personList.get(i).get("person_name"));
                                }
                                LayoutInflater inflater = getLayoutInflater();
                                EditDialog.Builder builder = new EditDialog.Builder(TemporaryActivity.this);
                                builder.setTitle("选择联系人");
                                if (person_name_phone_list.size() != 0) {
                                    //有客户联系人
                                    View view = inflater.inflate(R.layout.contact_namedialog, null);
                                    builder.setContentView(view);
                                    ListView listView = (ListView) view.findViewById(R.id.lvShowCustomerName);
                                    SimpleAdapter simpleAdapter = new SimpleAdapter(
                                            TemporaryActivity.this,
                                            allCustomer_personList,
                                            R.layout.contact_name,
                                            new String[]{"person_name", "person_phone"},
                                            new int[]{R.id.tvContact_name, R.id.tvContact_phone}
                                    ) {
                                        @Override
                                        public View getView(final int position, View convertView, ViewGroup parent) {
                                            View view = super.getView(position, convertView, parent);
                                            view.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    //客户联系人id
                                                    contactPersonID = allCustomer_personList.get(position).get("person_id").trim();
                                                    act_contact_name.setText(allCustomer_personList.get(position).get("person_name").trim());
                                                    et_contact_phone.setText(allCustomer_personList.get(position).get("person_phone").trim());
                                                    dialog_customer_person.dismiss();
                                                }
                                            });
                                            return view;
                                        }
                                    };
                                    listView.setAdapter(simpleAdapter);

                                } else {//没有客户联系人
                                    View view = inflater.inflate(R.layout.null_contact_name, null);
                                    builder.setContentView(view);

                                }
                                dialog_customer_person = builder.create();
                                dialog_customer_person.show();
                            } else {
                                //不存在客户时，清空信息
                                customerID = "";//客户ID
                                customerAddress = "";//客户地址
                                contactPersonID = "";//联系人ID
                            }
                        } else {
                            if (allCustomer_personList.size() != 0) {
                                allCustomer_personList.clear();
                                person_name_phone_list.clear();
                            }
                        }
                    }
                });
            } else {
                View view = getLayoutInflater().inflate(R.layout.names_item, null);
                TextView exception = (TextView) view.findViewById(R.id.tv_name);
                exception.setText(str_exception);
                new EditDialog.Builder(TemporaryActivity.this)
                        .setTitle("出错提示")
                        .setContentView(view)
                        .setCancellable(false)
                        .setCanTouchOutside(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                TemporaryActivity.this.finish();
                            }
                        }).create().show();
            }
            pd_dialog.dismiss();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temporary);
        init();
        String requestPath = "http://www.56gps.cn/oaweb/WebApi/api_customer.aspx?FLAG=CustomerInfo";
        new CustomersAsyncTask().execute(requestPath);
    }

    /**
     * 初始化控件
     */
    private void init() {
        productList = new ArrayList<>();
        act_Client = (AutoCompleteTextView) findViewById(R.id.act_Client);
        act_contact_name = (AutoCompleteTextView) findViewById(R.id.act_contact_name);
        et_contact_phone = (EditText) findViewById(R.id.et_contact_phone);
        et_contact_phone.setInputType(TYPE_CLASS_PHONE);
        et_address = (EditText) findViewById(R.id.et_address);
        etRemark = (EditText) findViewById(R.id.etRemark);
        //报修设备列表
        lv_product = (ListView) findViewById(R.id.lv_product);

        btnAddShebei = (Button) findViewById(R.id.btnAddShebei);
        btnAddShebei.setOnClickListener(this);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(this);
        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnCreate.setOnClickListener(this);

        allCustomersList = new ArrayList<>();
        allCustomerNameList = new ArrayList<>();
        allCustomer_personList = new ArrayList<>();
        person_name_phone_list = new ArrayList<String>();
        dialog_customer_person = new EditDialog(TemporaryActivity.this);
        sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
    }

    /**
     * 展示数据
     *
     * @param productList
     */
    private void showProductMessage(final List<Map<String, String>> productList) {
        adapterProductList = new SimpleAdapter(
                TemporaryActivity.this,
                productList,
                R.layout.sbmessagedata,
                new String[]{"product_name", "car_number", "fault_description"},
                new int[]{R.id.tv_productName,
                        R.id.tv_productCarNumber,
                        R.id.tv_fault_description}
        ) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                LinearLayout ll_num = (LinearLayout) view.findViewById(R.id.ll_num);
                LinearLayout ll_productName = (LinearLayout) view.findViewById(R.id.ll_productName);
                LinearLayout ll_productCarNumber = (LinearLayout) view.findViewById(R.id.ll_productCarNumber);
                LinearLayout ll_fault_description = (LinearLayout) view.findViewById(R.id.ll_fault_description);
                LinearLayout ll_delete = (LinearLayout) view.findViewById(R.id.ll_delete);
                TextView tv_num = (TextView) view.findViewById(R.id.tv_num);
                tv_num.setText((position + 1) + "");
                //行数颜色变化
                if (position % 2 == 0) {//偶数
                    ll_num.setBackgroundResource(R.drawable.product_every1);
                    ll_productName.setBackgroundResource(R.drawable.product_every1);
                    ll_productCarNumber.setBackgroundResource(R.drawable.product_every1);
                    ll_fault_description.setBackgroundResource(R.drawable.product_every1);
                    ll_delete.setBackgroundResource(R.drawable.product_every1);
                } else {
                    //奇数
                    ll_num.setBackgroundResource(R.drawable.product_every2);
                    ll_productName.setBackgroundResource(R.drawable.product_every2);
                    ll_productCarNumber.setBackgroundResource(R.drawable.product_every2);
                    ll_fault_description.setBackgroundResource(R.drawable.product_every2);
                    ll_delete.setBackgroundResource(R.drawable.product_every2);
                }
                ImageButton ib_delete = (ImageButton) view.findViewById(R.id.ib_delete);
                ib_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater l = getLayoutInflater();
                        View vv = l.inflate(R.layout.nullmessage, null);
                        new EditDialog.Builder(TemporaryActivity.this)
                                .setTitle("确认删除？")
                                .setContentView(vv)
                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        productList.remove(position);
                                        adapterProductList.notifyDataSetChanged();
                                        dialog.dismiss();
                                        showToastOnUI("删除成功");
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
                return view;
            }
        };
        lv_product.setAdapter(adapterProductList);
        adapterProductList.notifyDataSetChanged();
        lv_product.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                LayoutInflater inflater = getLayoutInflater();
                View product_view = inflater.inflate(R.layout.shebeimessage, null);
                final EditText etPro_Name = (EditText) product_view.findViewById(R.id.etPro_Name);
                etPro_Name.setText(productList.get(position).get("product_name"));
                final EditText etVehCode = (EditText) product_view.findViewById(R.id.etVehCode);
                etVehCode.setText(productList.get(position).get("car_number"));
                final EditText et_ShowCause = (EditText) product_view.findViewById(R.id.et_ShowCause);
                et_ShowCause.setText(productList.get(position).get("fault_description"));
                new EditDialog.Builder(TemporaryActivity.this)
                        .setTitle("修改信息")
                        .setContentView(product_view)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                productList.get(position).put("product_name", etPro_Name.getText().toString());
                                productList.get(position).put("car_number", etVehCode.getText().toString());
                                productList.get(position).put("fault_description", et_ShowCause.getText().toString());
                                adapterProductList.notifyDataSetChanged();
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
    }


    /**
     * 退出
     */
    public void back() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater layoutInflater = getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.nullmessage, null);
                new EditDialog.Builder(TemporaryActivity.this)
                        .setTitle("退出后,不保存信息")
                        .setContentView(view)
                        .setPositiveButton("确定退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                TemporaryActivity.this.finish();

                            }
                        })
                        .setNegativeButton("继续创建", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });
    }

    /**
     * 将List<Map<String, String>> 里面的产品拼接成一串字符串
     *
     * @param mapProduct
     * @return
     */
    private String getStringProduct(List<Map<String, String>> mapProduct) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mapProduct.size(); i++) {
            Map<String, String> map = mapProduct.get(i);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append(entry.getValue()).append("~");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    /**
     * 在后台线程里面使用提示信息
     *
     * @param message
     */
    public void showToastThread(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.popToast(TemporaryActivity.this, message);
            }
        });
    }

    /**
     * 在UI线程里使用
     *
     * @param message
     */
    public void showToastOnUI(String message) {
        MyToast.popToast(TemporaryActivity.this, message);
    }

}
