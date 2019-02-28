package com.mapsoft.aftersale.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapsoft.aftersale.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OrderAdapter extends BaseAdapter {
    private Context mContext;
    private boolean isBus;//是否为公交
    private List<Map<String, String>> list;//订单集合
    private boolean isRepairing;//是否为维修中订单
    private boolean isGrab;//是否为待处理订单
    private boolean isComplete;//是否为已完成订单
    private ScanListener scanListener;//公路添加设备监听
    private AddListener addListener;//公交扫描监听
    private GrabListener grabListener;//抢单按钮监听
    private int[] resIDs = {R.id.tv_date1, R.id.tv_date2, R.id.tv_date3, R.id.tv_date4, R.id.tv_date5,
            R.id.tv_date6, R.id.tv_date7
    };

    public interface ScanListener {
        void onClick(View view, int position);
    }

    public interface AddListener {
        void onClick(View view, int position);
    }

    public interface GrabListener {
        void onClick(View view, int position);
    }


    public void setGrabListener(GrabListener grabListener) {
        this.grabListener = grabListener;
    }

    public void setAddListener(AddListener addListener) {
        this.addListener = addListener;
    }

    public void setScanListener(ScanListener scanListener) {
        this.scanListener = scanListener;
    }

    public OrderAdapter(Context mContext, List<Map<String, String>> list) {
        this.mContext = mContext;
        this.list = list;
    }

    /**
     * 订单状态
     */
    public void setOrderState(boolean isRepairing, boolean isGrab, boolean isComplete) {
        this.isRepairing = isRepairing;
        this.isGrab = isGrab;
        this.isComplete = isComplete;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Map<String, String> getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        TextView tv_order_type;//订单类型
        TextView tv_order_state;//订单状态
        TextView tv_order_code;//订单编号
        LinearLayout ll_date;//日期计算显示框
        TextView tv_time_single;//下单时间
        TextView tv_time_deadline;//截止时间
        TextView tv_customer_name;//客户名称
        TextView tv_customer_person;//客户联系人
        TextView tv_customer_personphone;//客户联系人的联系方式
        TextView tv_customer_address;//维修地址
        TextView tv_order_singler;//派单员
        TextView tv_singler_phone;//派单员联系方式
        TextView tv_order_remark_hint;//下单备注提示
        TextView tv_order_remark;//下单备注
        //公交扫描布局
        LinearLayout ll_scan;
        Button btn_scan;//扫描按钮
        Button btn_grab_one;//抢单按钮
        //公路
        LinearLayout ll_add;//公路添加设备或安装设备布局
        Button btn_add;//公路添加按钮
        TextView tv_addOrInstall;//公路按钮提示文字

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.allorderlist, parent, false);
            holder.tv_order_type = (TextView) convertView.findViewById(R.id.tv_order_type);
            holder.tv_order_state = (TextView) convertView.findViewById(R.id.tv_order_state);
            holder.tv_order_code = (TextView) convertView.findViewById(R.id.tv_order_code);
            holder.ll_date = (LinearLayout) convertView.findViewById(R.id.ll_date);
            holder.tv_time_single = (TextView) convertView.findViewById(R.id.tv_time_single);
            holder.tv_time_deadline = (TextView) convertView.findViewById(R.id.tv_time_deadline);
            holder.tv_customer_name = (TextView) convertView.findViewById(R.id.tv_customer_name);
            holder.tv_customer_person = (TextView) convertView.findViewById(R.id.tv_customer_person);
            holder.tv_customer_personphone = (TextView) convertView.findViewById(R.id.tv_customer_personphone);
            holder.tv_customer_address = (TextView) convertView.findViewById(R.id.tv_customer_address);
            holder.tv_order_singler = (TextView) convertView.findViewById(R.id.tv_order_singler);
            holder.tv_singler_phone = (TextView) convertView.findViewById(R.id.tv_singler_phone);
            holder.tv_order_remark_hint = (TextView) convertView.findViewById(R.id.tv_order_remark_hint);
            holder.tv_order_remark = (TextView) convertView.findViewById(R.id.tv_order_remark);
            holder.ll_scan = (LinearLayout) convertView.findViewById(R.id.ll_scan);
            holder.btn_scan = (Button) convertView.findViewById(R.id.btn_scan);
            holder.btn_grab_one = (Button) convertView.findViewById(R.id.btn_grab_one);
            holder.ll_add = (LinearLayout) convertView.findViewById(R.id.ll_add);
            holder.btn_add = (Button) convertView.findViewById(R.id.btn_add);
            holder.tv_addOrInstall = (TextView) convertView.findViewById(R.id.tv_addOrInstall);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.btn_grab_one.setVisibility(View.VISIBLE);
        holder.ll_scan.setVisibility(View.VISIBLE);
        holder.ll_add.setVisibility(View.VISIBLE);
        holder.ll_date.setVisibility(View.VISIBLE);
        Map<String, String> item = getItem(position);
        holder.tv_order_code.setText(item.get("order_code"));
        holder.tv_order_state.setText(item.get("order_statu"));
        holder.tv_time_single.setText(item.get("time_single"));
        holder.tv_time_deadline.setText(item.get("time_deadline"));
        holder.tv_order_singler.setText(item.get("order_singler"));
        holder.tv_singler_phone.setText(item.get("singler_phone"));
        holder.tv_order_remark.setText(item.get("order_remark"));
        if (item.get("main_type").equals("1")) {
            //公路
            isBus = false;
            holder.tv_order_type.setText("公路-维修单");
            holder.tv_customer_name.setText(item.get("customer_name"));
            holder.tv_customer_person.setText(item.get("customer_person"));
            holder.tv_customer_personphone.setText(item.get("customer_personphone"));
            holder.tv_customer_address.setText(item.get("customer_address"));
            holder.tv_order_remark_hint.setText("维 修备注:");
        } else if (item.get("main_type").equals("0")) {
            isBus = true;
            //公交
            if ("W".equals(item.get("order_type"))) {
                //维修单
                holder.tv_order_type.setText("公交-维修单");
                holder.tv_customer_name.setText(item.get("customer_name"));
                holder.tv_customer_person.setText(item.get("customer_person"));
                holder.tv_customer_personphone.setText(item.get("customer_personphone"));
                holder.tv_customer_address.setText(item.get("customer_address"));
                holder.tv_order_remark_hint.setText("维 修备注:");
            } else if ("T".equals(item.get("order_type"))) {
                //安装单
                holder.tv_order_type.setText("公交-安装单");
                holder.tv_customer_name.setText(item.get("p_customer"));
                holder.tv_customer_person.setText(item.get("p_personname"));
                holder.tv_customer_personphone.setText(item.get("p_phone"));
                holder.tv_customer_address.setText(item.get("p_address"));
                holder.tv_order_remark_hint.setText("安 装备注:");
            }
        }
        holder.btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scanListener != null)
                    scanListener.onClick(v, position);
            }
        });

        holder.btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addListener != null)
                    addListener.onClick(v, position);
            }
        });
        holder.btn_grab_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (grabListener != null)
                    grabListener.onClick(v, position);
            }
        });
        if (isRepairing) {//正在维修
            holder.btn_grab_one.setVisibility(View.GONE);
            holder.tv_singler_phone.setAutoLinkMask(Linkify.PHONE_NUMBERS);
            holder.tv_singler_phone.setMovementMethod(LinkMovementMethod.getInstance());
            holder.tv_customer_personphone.setAutoLinkMask(Linkify.PHONE_NUMBERS);
            holder.tv_customer_personphone.setMovementMethod(LinkMovementMethod.getInstance());
            if (isBus) {
                holder.ll_add.setVisibility(View.GONE);
            } else {
                holder.ll_scan.setVisibility(View.GONE);
                if ("W".equals(item.get("order_type"))) {
                    holder.tv_addOrInstall.setText("维修设备");
                } else if ("T".equals(item.get("order_type"))) {
                    holder.tv_addOrInstall.setText("安装设备");
                }
            }
            String time_single = holder.tv_time_single.getText().toString().trim();//获取下单时间   2017
            String time_deadline = holder.tv_time_deadline.getText().toString().trim();//获取截止时间
            String time_single_hour = time_single;//带时分秒的时间
            String[] str = time_single.trim().split(" ");
            time_single = str[0];
            Date deadTime_date = null, time_single_date = null, time_single_hour_date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                deadTime_date = sdf.parse(time_deadline);//将截止时间字符串转换成日期格式，例如：1970-01-01
                time_single_date = sdf.parse(time_single);//将下单时间字符串转换成日期格式，例如：1970-01-01
                time_single_hour_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time_single_hour);//将下单时间字符串转换成日期格式，例如：1970-01-01 14:12:00
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long nowTime = System.currentTimeMillis();//获取当前时间毫秒数
            long singleTime = time_single_date.getTime();//下单时间毫秒数
            long singleTime_hour = time_single_hour_date.getTime();//带时分秒的下单时间毫秒数
            long deadTime = deadTime_date.getTime();//将截止日期转换成毫秒数
            long oneDayTime = 24 * 60 * 60 * 1000;//一天的毫秒数
            //维修总天数
            int day_repair_all = (int) ((deadTime - singleTime) / oneDayTime);
            //第几天维修
            int now_day_repairing = (int) ((nowTime - singleTime_hour) / oneDayTime);
            //超时天数
            int over_day = now_day_repairing - day_repair_all;
            if (over_day < 0) {
                //未超时
                holder.tv_order_state.setTextColor(ContextCompat.getColor(mContext, R.color.time));
                if ("W".equals(item.get("order_type"))) {
                    holder.tv_order_state.setText("正常维修中");
                } else {
                    holder.tv_order_state.setText("正常安装中");
                }
                for (int i = 0; i < resIDs.length; i++) {
                    //恢复设置
                    TextView recover = (TextView) convertView.findViewById(resIDs[i]);
                    if (i == 0) {
                        recover.setBackgroundResource(R.drawable.tv_date_first_shape);
                    } else if (i == 6) {
                        recover.setBackgroundResource(R.drawable.tv_date_last_shape);
                    } else {
                        recover.setBackgroundResource(R.drawable.tv_date_shape);
                    }
                    recover.setText((i + 1) + "");
                    recover.setTextColor(ContextCompat.getColor(mContext, R.color.time));
                }

                if (day_repair_all > 7) {
                    for (int i = 0; i < resIDs.length; i++) {
                        //初始化控件
                        TextView tv_date_normal_more7_last1 = (TextView) convertView.findViewById(resIDs[resIDs.length - 1]);
                        tv_date_normal_more7_last1.setText(day_repair_all + "");
                        TextView tv_date_normal_more7_last2 = (TextView) convertView.findViewById(resIDs[resIDs.length - 2]);
                        tv_date_normal_more7_last2.setText("...");
                    }
                    for (int i = 0; i <= now_day_repairing; i++) {
                        for (int j = 0; j < resIDs.length; j++) {
                            TextView tv_date_normal_more7 = (TextView) convertView.findViewById(resIDs[j]);
                            if (i == 0) {
                                // 1
                                if (j == 0) {
                                    tv_date_normal_more7.setBackgroundResource(R.drawable.firstday_shape);
                                    tv_date_normal_more7.setTextColor(Color.WHITE);
                                }
                            }
                            if (i >= 1 && i <= 4) {
                                if (j == i) {
                                    tv_date_normal_more7.setBackgroundResource(R.drawable.normal_repairing_shape);
                                    tv_date_normal_more7.setTextColor(Color.WHITE);
                                }
                            }
                            if (i > 4 && i < day_repair_all - 2) {
                                if (j == 3) {
                                    tv_date_normal_more7.setText("...");
                                }
                                if (j == 4) {
                                    tv_date_normal_more7.setText((i + 1) + "");
                                    tv_date_normal_more7.setBackgroundResource(R.drawable.normal_repairing_shape);
                                    tv_date_normal_more7.setTextColor(Color.WHITE);
                                }
                            }
                            if (i == day_repair_all - 2) {
                                if (j == 5) {
                                    tv_date_normal_more7.setText((i + 1) + "");
                                    tv_date_normal_more7.setBackgroundResource(R.drawable.normal_repairing_shape);
                                    tv_date_normal_more7.setTextColor(Color.WHITE);
                                }
                            }
                            if (i == day_repair_all - 1) {
                                if (j == 6) {
                                    tv_date_normal_more7.setBackgroundResource(R.drawable.normal_repair_lastday_shape);
                                    tv_date_normal_more7.setTextColor(Color.WHITE);
                                }
                            }
                        }
                    }

                } else {
                    //正常维修天数小于等于7天的
                    if (day_repair_all < 7) {
                        for (int i = 0; i < resIDs.length; i++) {
                            //恢复设置
                            TextView recover = (TextView) convertView.findViewById(resIDs[i]);
                            if (i == 0) {
                                recover.setBackgroundResource(R.drawable.tv_date_first_shape);
                                recover.setText((i + 1) + "");
                            } else if (i == 6) {
                                recover.setText("");
                            } else {
                                recover.setBackgroundResource(R.drawable.tv_date_shape);
                                recover.setText((i + 1) + "");
                            }
                            recover.setTextColor(ContextCompat.getColor(mContext, R.color.time));
                        }
                        for (int i = 0; i < resIDs.length; i++) {
                            //初始化控件
                            TextView tv_date_normal_less6_last = (TextView) convertView.findViewById(resIDs[resIDs.length - 1]);
                            tv_date_normal_less6_last.setText("");
                        }
                        for (int i = 0; i <= now_day_repairing; i++) {
                            for (int j = 0; j < resIDs.length; j++) {
                                TextView tv_date_normal_less6 = (TextView) convertView.findViewById(resIDs[j]);
                                if (i == 0) {
                                    //1
                                    if (j == 0) {
                                        tv_date_normal_less6.setBackgroundResource(R.drawable.firstday_shape);
                                        tv_date_normal_less6.setTextColor(Color.WHITE);
                                    }
                                }
                                if (i > 0 && i <= now_day_repairing) {
                                    if (j > 0 && j <= now_day_repairing) {
                                        tv_date_normal_less6.setBackgroundResource(R.drawable.normal_repairing_shape);
                                        tv_date_normal_less6.setTextColor(Color.WHITE);
                                    }
                                }

                            }
                        }
                    } else {
                        for (int i = 0; i <= now_day_repairing; i++) {
                            for (int j = 0; j < resIDs.length; j++) {
                                TextView tv_date_normal_equal7 = (TextView) convertView.findViewById(resIDs[j]);
                                if (i == 0) {
                                    //第一天 1
                                    if (j == 0) {
                                        tv_date_normal_equal7.setBackgroundResource(R.drawable.firstday_shape);
                                        tv_date_normal_equal7.setTextColor(Color.WHITE);
                                    }
                                }
                                if (i > 0 && i < 6) {
                                    if (i == j) {
                                        tv_date_normal_equal7.setBackgroundResource(R.drawable.normal_repairing_shape);
                                        tv_date_normal_equal7.setTextColor(Color.WHITE);
                                    }
                                }
                                if (i == 6) {
                                    //第7天 7
                                    if (j == 6) {
                                        tv_date_normal_equal7.setBackgroundResource(R.drawable.normal_repair_lastday_shape);
                                        tv_date_normal_equal7.setTextColor(Color.WHITE);
                                    }
                                }
                            }
                        }
                    }

                }
            } else {
                //超时
                holder.tv_order_state.setTextColor(Color.RED);
                if ("W".equals(item.get("order_type"))) {
                    holder.tv_order_state.setText("超期维修中");
                } else {
                    holder.tv_order_state.setText("超期安装中");
                }
                //设置正常维修的控件
                for (int i = 0; i < resIDs.length - 1; i++) {
                    TextView tv_date_over_normal = (TextView) convertView.findViewById(resIDs[i]);
                    if (i == 0) {
                        tv_date_over_normal.setBackgroundResource(R.drawable.firstday_shape);
                    } else {
                        tv_date_over_normal.setBackgroundResource(R.drawable.normal_repairing_shape);
                    }
                    tv_date_over_normal.setTextColor(Color.WHITE);
                }

                for (int i = 0; i <= over_day; i++) {
                    for (int j = 0; j < resIDs.length; j++) {
                        //初始化省略号和最后一个空格
                        TextView tv_date_over4 = (TextView) convertView.findViewById(resIDs[3]);
                        tv_date_over4.setText("...");
                        TextView tv_date_last = (TextView) convertView.findViewById(resIDs[resIDs.length - 1]);
                        tv_date_last.setText((now_day_repairing + 1) + "");
                        tv_date_last.setBackgroundResource(R.drawable.over_lastday_shape);
                        tv_date_last.setTextColor(Color.WHITE);
                        TextView tv_date_over = (TextView) convertView.findViewById(resIDs[j]);
                        if (i == 0) {
                            //超时第一天
                            if (j == 4) {
                                tv_date_over.setText((day_repair_all - 1) + "");
                            }
                            if (j == 5) {
                                tv_date_over.setText(day_repair_all + "");
                            }
                        }
                        if (i == 1) {
                            //超时第二天
                            if (j == 4) {
                                tv_date_over.setText(day_repair_all + "");
                            }
                            if (j == 5) {
                                tv_date_over.setText(day_repair_all + over_day + "");
                                tv_date_over.setBackgroundResource(R.drawable.overtime_shape);
                            }
                        } else if (i > 1) {
                            //超时第3天以后
                            if (j == 5) {
                                tv_date_over.setText("...");
                                tv_date_over.setBackgroundResource(R.drawable.overtime_shape);
                            }
                        }
                    }
                }
            }
        }
        if (isGrab || isComplete) {//抢单
            if (isComplete) holder.btn_grab_one.setVisibility(View.GONE);
            holder.ll_scan.setVisibility(View.GONE);
            holder.ll_add.setVisibility(View.GONE);
            holder.ll_date.setVisibility(View.GONE);
        }
        return convertView;
    }
}
