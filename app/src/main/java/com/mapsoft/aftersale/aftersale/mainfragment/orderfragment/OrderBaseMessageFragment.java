package com.mapsoft.aftersale.aftersale.mainfragment.orderfragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mapsoft.aftersale.R;
import com.mapsoft.aftersale.RdApplication;
import com.mapsoft.aftersale.utils.MapSerializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/24.
 * 正在维修--订单基本信息
 */

public class OrderBaseMessageFragment extends Fragment {

    //头布局
    View list_head;

    private TextView
            tv_time_complete_bf,//完成时间
            tv_repairOrInstall_message,//维修或者安装设备的信息
            tv_singler_phone_bf,//派单员--联系方式
            tv_order_singler_bf,//派单员
            tv_c_address_bf,//安装--使用单位--维修地址
            tv_c_phone_bf,//安装--使用单位--联系方式
            tv_c_personname_bf,//安装--使用单位--联系人
            tv_c_customer_bf,//安装--使用单位--客户
            tv_p_address_bf,//安装--订单单位--维修地址
            tv_p_phone_bf,//安装--订单单位--联系方式
            tv_p_personname_bf,//安装--订单单位--联系人
            tv_p_customer_bf,//安装--订单单位--客户
            tv_customer_address_bf,//维修--维修地址
            tv_customer_personphone_bf,//维修--联系方式
            tv_customer_person_bf,//维修--客户联系人
            tv_customer_name_bf,//维修--客户名称
            tv_time_deadline_bf,//截至日期
            tv_time_single_bf,//下单日期
            tv_repair_remark_bf,//维修备注
            tv_customer_feedback_bf,//客户反馈
            tv_customer_appraise_bf,//客户评价
            tv_order_state_bf,//维修状态
            tv_order_type_bf; //订单类型

    private ListView lv_order_message;
    private BaseAdapter adapter;
    private List<Map<String, String>> order_productList;//产品信息集合
    private Map<String, String> orderMap;//传过来的值

    private boolean isAnalysisRepairOK;
    private boolean isAnalysisInstallOK;
    private boolean isAnalysisHistoryRepairOK;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isAnalysisRepairOK = true;
                            checkOK();
                        }
                    });
                    break;
                case 2:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isAnalysisInstallOK = true;
                            checkOK();
                        }
                    });
                    break;
                case 3:
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isAnalysisHistoryRepairOK = true;
                            checkOK();
                        }
                    });
                    break;
                case 4:
                    break;
                default:
                    break;
            }
        }
    };
    private String order_type;//订单类型

    /**
     * 解析完成。展示数据
     */
    private void checkOK() {
        if (isAnalysisRepairOK || isAnalysisInstallOK || isAnalysisHistoryRepairOK) {
            adapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return order_productList.size();
                }

                @Override
                public Object getItem(int position) {
                    return order_productList.get(position);
                }

                @Override
                public long getItemId(int position) {
                    return position;
                }

                class ViewHolder {
                    TextView textView1_1;
                    TextView textView1;
                    TextView textView2_1;
                    TextView textView2;
                    TextView textView3_1;
                    TextView textView3;
                    TextView textView4_1;
                    TextView textView4;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    Map<String, String> item = order_productList.get(position);
                    LayoutInflater inflater = LayoutInflater.from(getActivity());
                    ViewHolder viewHolder;
                    if (convertView == null) {
                        convertView = inflater.inflate(R.layout.new_installproductdata, parent, false);
                        viewHolder = new ViewHolder();
                        viewHolder.textView1_1 = (TextView) convertView.findViewById(R.id.tv_producttype_1);
                        viewHolder.textView1 = (TextView) convertView.findViewById(R.id.tv_producttype);
                        viewHolder.textView2_1 = (TextView) convertView.findViewById(R.id.tv_productname_1);
                        viewHolder.textView2 = (TextView) convertView.findViewById(R.id.tv_productname);
                        viewHolder.textView3_1 = (TextView) convertView.findViewById(R.id.tv_productnum_1);
                        viewHolder.textView3 = (TextView) convertView.findViewById(R.id.tv_productnum);
                        viewHolder.textView4_1 = (TextView) convertView.findViewById(R.id.tv_productremark_1);
                        viewHolder.textView4 = (TextView) convertView.findViewById(R.id.tv_productremark);
                        convertView.setTag(viewHolder);
                    } else {
                        viewHolder = (ViewHolder) convertView.getTag();
                    }
                    if ("W".equals(order_type)) {
                        viewHolder.textView1_1.setText("车  牌  号:");
                        viewHolder.textView1.setText(item.get("car_number"));
                        viewHolder.textView2_1.setText("产品名称:");
                        viewHolder.textView2.setText(item.get("product_name"));
                        viewHolder.textView3_1.setText("故障描述:");
                        viewHolder.textView3.setText(item.get("fault_description"));
                        viewHolder.textView4_1.setVisibility(View.GONE);
                        viewHolder.textView4.setVisibility(View.GONE);
                    } else {
                        viewHolder.textView1_1.setText("产品类型:");
                        viewHolder.textView1.setText(item.get("producttype"));
                        viewHolder.textView2_1.setText("产品名称:");
                        viewHolder.textView2.setText(item.get("productname"));
                        viewHolder.textView3_1.setText("产品数量:");
                        viewHolder.textView3.setText(item.get("productnum"));
                        viewHolder.textView4_1.setText("产品备注:");
                        viewHolder.textView4.setText(item.get("productremark"));
                    }
                    return convertView;
                }

            };
            lv_order_message.addHeaderView(list_head);//给listView 添加头布局
            lv_order_message.setAdapter(adapter);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.basefragment, container, false);

        lv_order_message = (ListView) view.findViewById(R.id.lv_order_message);
        list_head = LayoutInflater.from(getActivity()).inflate(R.layout.mylistview_head, null);
        order_productList = new ArrayList<>();
        String order_message = getActivity()
                .getSharedPreferences(getActivity().getPackageName(), Context.MODE_PRIVATE)
                .getString("order_message", "");//传值：维修，抢单，历史订单
        orderMap = RdApplication.get().getOrderMap();
        String orderType = orderMap.get("main_type").equals("0") ? "公交-维修单" : "公路-维修单";//区域维修单
        order_type = orderMap.get("order_type");
        String product_info = orderMap.get("product_info");
        LinearLayout ll_install_order = (LinearLayout) list_head.findViewById(R.id.ll_install_order);//安装单联系人信息
        LinearLayout ll_repair_order = (LinearLayout) list_head.findViewById(R.id.ll_repair_order);//维修单联系人信息
        LinearLayout ll_customer_over = (LinearLayout) list_head.findViewById(R.id.ll_customer_over);//客户评价和反馈
        LinearLayout ll_time_complete_bf = (LinearLayout) list_head.findViewById(R.id.ll_time_complete_bf);//完成时间
        tv_order_type_bf = (TextView) list_head.findViewById(R.id.tv_order_type_bf);

        tv_order_state_bf = (TextView) list_head.findViewById(R.id.tv_order_state_bf);
        tv_order_state_bf.setText(orderMap.get("order_statu"));

        TextView tv_remark_RepairOrInstall = (TextView) list_head.findViewById(R.id.tv_remark_RepairOrInstall);

        tv_repair_remark_bf = (TextView) list_head.findViewById(R.id.tv_repair_remark_bf);
        tv_repair_remark_bf.setText(orderMap.get("order_remark"));
        tv_time_single_bf = (TextView) list_head.findViewById(R.id.tv_time_single_bf);
        tv_time_single_bf.setText(orderMap.get("time_single"));
        tv_time_deadline_bf = (TextView) list_head.findViewById(R.id.tv_time_deadline_bf);
        tv_time_deadline_bf.setText(orderMap.get("time_deadline"));
        tv_order_singler_bf = (TextView) list_head.findViewById(R.id.tv_order_singler_bf);
        tv_order_singler_bf.setText(orderMap.get("order_singler"));
        tv_singler_phone_bf = (TextView) list_head.findViewById(R.id.tv_singler_phone_bf);
        tv_singler_phone_bf.setText(orderMap.get("singler_phone"));


        tv_customer_name_bf = (TextView) list_head.findViewById(R.id.tv_customer_name_bf);
        tv_customer_person_bf = (TextView) list_head.findViewById(R.id.tv_customer_person_bf);
        tv_customer_personphone_bf = (TextView) list_head.findViewById(R.id.tv_customer_personphone_bf);
        tv_customer_address_bf = (TextView) list_head.findViewById(R.id.tv_customer_address_bf);

        tv_p_customer_bf = (TextView) list_head.findViewById(R.id.tv_p_customer_bf);
        tv_p_personname_bf = (TextView) list_head.findViewById(R.id.tv_p_personname_bf);
        tv_p_phone_bf = (TextView) list_head.findViewById(R.id.tv_p_phone_bf);
        tv_p_address_bf = (TextView) list_head.findViewById(R.id.tv_p_address_bf);
        tv_c_customer_bf = (TextView) list_head.findViewById(R.id.tv_c_customer_bf);
        tv_c_personname_bf = (TextView) list_head.findViewById(R.id.tv_c_personname_bf);
        tv_c_phone_bf = (TextView) list_head.findViewById(R.id.tv_c_phone_bf);
        tv_c_address_bf = (TextView) list_head.findViewById(R.id.tv_c_address_bf);

        tv_time_complete_bf = (TextView) list_head.findViewById(R.id.tv_time_complete_bf);

        TextView tv_repairOrInstall_message = (TextView) list_head.findViewById(R.id.tv_repairOrInstall_message);


        if ("W".equals(order_type)) {
            tv_repairOrInstall_message.setText("客户报修设备");
            tv_remark_RepairOrInstall.setText("维修备注");
        } else if ("T".equals(order_type)) {
            tv_repairOrInstall_message.setText("安装设备");
            tv_remark_RepairOrInstall.setText("安装备注");
        }


        if ("repairing".equals(order_message)) {

            tv_singler_phone_bf.setAutoLinkMask(Linkify.PHONE_NUMBERS);
            tv_singler_phone_bf.setMovementMethod(LinkMovementMethod.getInstance());

            //维修中
            ll_customer_over.setVisibility(View.GONE);
            ll_time_complete_bf.setVisibility(View.GONE);

            if ("W".equals(order_type)) {
                //外派维修单
                analysisRepairProduct(product_info);
                ll_install_order.setVisibility(View.GONE);
                tv_order_type_bf.setText(orderType);
                tv_customer_name_bf.setText(orderMap.get("customer_name"));
                tv_customer_person_bf.setText(orderMap.get("customer_person"));
                tv_customer_personphone_bf.setText(orderMap.get("customer_personphone"));
                tv_customer_address_bf.setText(orderMap.get("customer_address"));

                tv_customer_personphone_bf.setAutoLinkMask(Linkify.PHONE_NUMBERS);
                tv_customer_personphone_bf.setMovementMethod(LinkMovementMethod.getInstance());

            } else {
                //安装调试单
                analysisInstallProduct(product_info);
                ll_repair_order.setVisibility(View.GONE);
                tv_order_type_bf.setText("公交-安装单");
                tv_p_customer_bf.setText(orderMap.get("p_customer"));
                tv_p_personname_bf.setText(orderMap.get("p_personname"));
                tv_p_phone_bf.setText(orderMap.get("p_phone"));//订单客户联系方式

                tv_p_phone_bf.setAutoLinkMask(Linkify.PHONE_NUMBERS);
                tv_p_phone_bf.setMovementMethod(LinkMovementMethod.getInstance());

                tv_p_address_bf.setText(orderMap.get("p_address"));
                tv_c_customer_bf.setText(orderMap.get("c_customer"));
                tv_c_personname_bf.setText(orderMap.get("c_personname"));
                tv_c_phone_bf.setText(orderMap.get("c_phone"));

                tv_c_phone_bf.setAutoLinkMask(Linkify.PHONE_NUMBERS);
                tv_c_phone_bf.setMovementMethod(LinkMovementMethod.getInstance());
                tv_c_address_bf.setText(orderMap.get("c_address"));
            }
        } else if ("complete".equals(order_message)) {
            //已完成


            tv_time_complete_bf.setText(orderMap.get("time_complete"));
            if ("W".equals(order_type)) {
                //外派维修单
                String product_customer = orderMap.get("product_customer");
                analysisRepairProduct(product_customer);
                ll_install_order.setVisibility(View.GONE);
                tv_order_type_bf.setText(orderType);
                tv_customer_name_bf.setText(orderMap.get("customer_name"));
                tv_customer_person_bf.setText(orderMap.get("customer_person"));
                tv_customer_personphone_bf.setText(orderMap.get("customer_personphone"));
                tv_customer_address_bf.setText(orderMap.get("customer_address"));

//                mHandler.handleMessage(mHandler.obtainMessage(3));
            } else {//安装调试单
                analysisInstallProduct(product_info);
                ll_repair_order.setVisibility(View.GONE);
                tv_order_type_bf.setText("公交-安装单");
                tv_p_customer_bf.setText(orderMap.get("p_customer"));
                tv_p_personname_bf.setText(orderMap.get("p_personname"));
                tv_p_phone_bf.setText(orderMap.get("p_phone"));
                tv_p_address_bf.setText(orderMap.get("p_address"));
                tv_c_customer_bf.setText(orderMap.get("c_customer"));
                tv_c_personname_bf.setText(orderMap.get("c_personname"));
                tv_c_phone_bf.setText(orderMap.get("c_phone"));
                tv_c_address_bf.setText(orderMap.get("c_address"));
            }
        } else {
            //抢单数据
            ll_time_complete_bf.setVisibility(View.GONE);
            ll_customer_over.setVisibility(View.GONE);
            if ("W".equals(order_type)) {//外派维修单
                ll_install_order.setVisibility(View.GONE);
                analysisRepairProduct(product_info);
                tv_order_type_bf.setText(orderType);
                tv_customer_name_bf.setText(orderMap.get("customer_name"));
                tv_customer_person_bf.setText(orderMap.get("customer_person"));
                tv_customer_personphone_bf.setText(orderMap.get("customer_personphone"));
                tv_customer_address_bf.setText(orderMap.get("customer_address"));
            } else {//安装调试单
                analysisInstallProduct(product_info);
                ll_repair_order.setVisibility(View.GONE);
                tv_order_type_bf.setText("公交-安装单");
                tv_p_customer_bf.setText(orderMap.get("p_customer"));
                tv_p_personname_bf.setText(orderMap.get("p_personname"));
                tv_p_phone_bf.setText(orderMap.get("p_phone"));
                tv_p_address_bf.setText(orderMap.get("p_address"));
                tv_c_customer_bf.setText(orderMap.get("c_customer"));
                tv_c_personname_bf.setText(orderMap.get("c_personname"));
                tv_c_phone_bf.setText(orderMap.get("c_phone"));
                tv_c_address_bf.setText(orderMap.get("c_address"));
            }
        }
        return view;
    }


    /**
     * 解析订单自带的维修设备信息
     *
     * @param product_info
     */
    private void analysisRepairProduct(final String product_info) {
        try {
            JSONArray ja = new JSONArray(product_info);
            for (int i = 0; i < ja.length(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                JSONObject js = ja.getJSONObject(i);
                map.put("product_name", js.getString("product_name"));
                map.put("car_number", js.getString("car_number"));
                map.put("fault_description", js.getString("fault_description"));
                order_productList.add(map);
            }
            mHandler.handleMessage(mHandler.obtainMessage(1));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析订单自带安装设备信息
     *
     * @param product_info
     */
    private void analysisInstallProduct(final String product_info) {

        try {
            JSONArray ja = new JSONArray(product_info);
            for (int i = 0; i < ja.length(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                JSONObject js = ja.getJSONObject(i);
                map.put("producttype", js.getString("producttype"));
                map.put("productname", js.getString("productname"));
                map.put("productnum", js.getString("productnum"));
                map.put("productremark", js.getString("productremark"));
                order_productList.add(map);
            }
            mHandler.handleMessage(mHandler.obtainMessage(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
