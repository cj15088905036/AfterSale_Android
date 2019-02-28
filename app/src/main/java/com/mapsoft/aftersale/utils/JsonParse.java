package com.mapsoft.aftersale.utils;

/**
 * Created by Administrator on 2017/8/31.
 */


import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求服务器后解析返回的json
 */

public class JsonParse {
    /**
     * 解析json头部
     *
     * @param json
     * @return
     */
    public static String judgeJson(String json) {
        String message = "";
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                message = item.getString("result");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * 判断字符串是否为json
     *
     * @param json
     * @return
     */
    public static boolean isJson(String json) {
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }

    /**
     * 解析登录数据
     *
     * @param json
     * @return
     * @throws Exception
     */
    public static Map<String, String> analysisLogin(String json) throws Exception {
        JSONArray array = new JSONArray(new String(json));
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject item = array.getJSONObject(i);
            map.put("user_id", item.getString("user_id"));
            map.put("user_department", item.getString("user_department"));
            map.put("user_account", item.getString("user_account"));
            map.put("user_name", item.getString("user_name"));
            map.put("user_region", item.getString("user_region"));
        }
        return map;
    }


    /**
     * 新接口解析未派单维修订单列表
     *
     * @param json
     * @return
     * @throws Exception
     */
    public static List<Map<String, String>> analysisRepairGrab(String json) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            JSONArray array = new JSONArray(new String(json));
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                Map<String, String> map = new HashMap<String, String>();
                map.put("order_type", item.getString("order_type"));
                map.put("customer_name", item.getString("customer_name"));
                map.put("product_info", item.getString("product_info"));
                map.put("city_id", item.getString("city_id"));
                map.put("singler_phone", item.getString("singler_phone"));
//                map.put("user_id", item.getString("user_id"));
                map.put("product_number", item.getString("product_number"));
                map.put("order_singler", item.getString("oder_singler"));
                map.put("order_statu", item.getString("order_statu"));
                map.put("time_deadline", item.getString("time_deadline"));
                map.put("customer_personphone", item.getString("customer_personphone"));
                map.put("order_code", item.getString("order_code"));
                map.put("customer_person", item.getString("customer_person"));
                map.put("customer_id", item.getString("customer_id"));
                map.put("time_single", item.getString("time_single"));
                map.put("order_remark",
                        "null".equals(item.getString("order_remark"))
                                || null == item.getString("order_remark") ?
                                "" : item.getString("order_remark"));
                map.put("customer_address",
                        "null".equals(item.getString("customer_address"))
                                || null == item.getString("customer_address") ?
                                "" : item.getString("customer_address"));
                map.put("province_id", item.getString("province_id"));
                map.put("main_type", item.getString("main_type"));//订单所属区域
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 解析历史维修单信息
     *
     * @param json
     * @return
     * @throws Exception
     */
    public static List<Map<String, String>> analysisHistoryRepairOrder(String json) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            JSONArray array = new JSONArray(new String(json));
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                Map<String, String> map = new HashMap<String, String>();
                map.put("order_type", item.getString("order_type"));
                map.put("order_code", item.getString("order_code"));
                map.put("product_info", item.getString("product_info"));
                map.put("singler_phone", item.getString("singler_phone"));
                map.put("order_engineer", item.getString("order_engineer"));
                map.put("userid", item.getString("userid"));
                map.put("order_singler", item.getString("oder_singler"));
                map.put("time_deadline", item.getString("time_deadline"));
                map.put("customer_personphone", item.getString("customer_personphone"));
                map.put("customer_name", item.getString("customer_name"));
                map.put("time_complete", "null".equals(item.getString("time_complete")) ?
                        "" : item.getString("time_complete"));
                map.put("customer_person", item.getString("customer_person"));
                map.put("customer_id", item.getString("customer_id"));
                map.put("time_single", item.getString("time_single"));
                map.put("order_remark",
                        "null".equals(item.getString("order_remark"))
                                || null == item.getString("order_remark") ?
                                "" : item.getString("order_remark"));
                map.put("customer_address",
                        "null".equals(item.getString("customer_address"))
                                || null == item.getString("customer_address") ?
                                "" : item.getString("customer_address"));
                map.put("product_count", item.getString("product_count"));
                map.put("order_statu", item.getString("order_statu"));
                map.put("time_maint", item.getString("time_maint"));
                map.put("product_customer", item.getString("product_customer"));
                map.put("main_type", item.getString("main_type"));//订单所属区域
                list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 解析历史安装单信息
     *
     * @param json
     * @return
     * @throws Exception
     */
    public static List<Map<String, String>> analysisHistoryInstallOrder(String json) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            JSONArray array = new JSONArray(new String(json));
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                Map<String, String> map = new HashMap<String, String>();
                map.put("order_type", item.getString("order_type"));
                map.put("c_address", item.getString("c_address"));
                map.put("time_single", item.getString("time_single"));
                map.put("singler_phone", item.getString("singler_phone"));
                map.put("order_code", item.getString("code"));
                map.put("order_engineer", item.getString("order_engineer"));
                map.put("userid", item.getString("userid"));
                map.put("time_deadline", item.getString("time_deadline"));
                map.put("c_personname", item.getString("c_personname"));
                map.put("p_address", item.getString("p_address"));
                map.put("c_phone", item.getString("c_phone"));
                map.put("order_singler", item.getString("order_single"));
                map.put("p_customer", item.getString("p_customer"));
                map.put("p_phone", item.getString("p_phone"));
                map.put("c_customer", item.getString("c_customer"));
                map.put("product_info", item.getString("productinfo"));
                map.put("p_personname", item.getString("p_personname"));
                map.put("time_complete", item.getString("time_complete"));
                map.put("order_remark",
                        "null".equals(item.getString("order_remark"))
                                || null == item.getString("order_remark") ?
                                "" : item.getString("order_remark"));
                map.put("order_statu", item.getString("order_statu"));
                list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 新接口解析公交产品维修记录
     *
     * @param json
     * @return
     */
    public static List<Map<String, String>> analysisProductRepairRecordByBus(String json) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            JSONArray array = new JSONArray(new String(json));
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                Map<String, String> map = new HashMap<String, String>();
                map.put("order_code", item.getString("order_code"));
                map.put("order_type", item.getString("order_type"));
                map.put("order_engineer", item.getString("order_engineer"));
                map.put("time_complete", item.getString("time_complete"));
                map.put("customer_address", item.getString("customer_address"));
                map.put("car_number", item.getString("car_number"));
                map.put("product_name", item.getString("product_name"));
                map.put("fault_cause", item.getString("fault_cause"));
                map.put("result_maint", item.getString("result_maint"));
                map.put("cost_material", item.getString("cost_material"));
                map.put("cost_maintenance", item.getString("cost_maintenance"));
                map.put("cost_all", item.getString("cost_all"));
                map.put("maint_number", item.getString("maint_number"));//维修次数
                list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 新接口解析公交产品维修记录
     *
     * @param json
     * @return
     */
    public static List<Map<String, String>> analysisProductRepairRecordByHigway(String json) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            JSONArray array = new JSONArray(new String(json));
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                Map<String, String> map = new HashMap<String, String>();
                map.put("order_code", item.getString("order_code"));
                map.put("order_type", item.getString("order_type"));
                map.put("product_name", item.getString("product_name"));
                map.put("car_number", item.getString("car_number"));
                map.put("fault_cause", item.getString("fault_cause"));
                map.put("result_maint", item.getString("result_maint"));
                map.put("product_sim", item.getString("product_sim"));
                map.put("install_address", item.getString("install_address"));
                map.put("install_time", item.getString("install_time"));
                map.put("maint_number", item.getString("maint_number"));//维修次数
                map.put("order_engineer", item.getString("order_engineer"));
                map.put("time_complete", item.getString("time_complete"));
                map.put("customer_address", item.getString("customer_address"));
                list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 新接口解析补充设备
     *
     * @param json
     * @return
     * @throws Exception
     */
    public static Map<String, String> analysisSupProduct(String json) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            JSONArray array = new JSONArray(new String(json));
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                map.put("product_type", item.getString("product_type"));
                map.put("product_name", item.getString("product_name"));
                map.put("car_number", item.getString("car_number"));
                map.put("time_create", item.getString("time_creat"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return map;
    }


    /**
     * 新接口未派单安装调试单信息解析
     *
     * @param json
     * @return
     * @throws Exception
     */
    public static List<Map<String, String>> analysisInstallGrab(String json) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            JSONArray array = new JSONArray(new String(json));
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                Map<String, String> map = new HashMap<String, String>();
                map.put("order_type", item.getString("order_type"));
                map.put("c_address", item.getString("c_address"));
                map.put("order_remark",
                        "null".equals(item.getString("order_remark"))
                                || null == item.getString("order_remark") ?
                                "" : item.getString("order_remark"));
                map.put("time_single", item.getString("time_single"));
                map.put("singler_phone", item.getString("singler_phone"));
                map.put("order_code", item.getString("code"));
                map.put("order_singler", item.getString("order_singler"));
                map.put("userid", item.getString("userid"));
                map.put("time_deadline", item.getString("time_deadline"));
                map.put("c_personname", item.getString("c_personname"));
                map.put("p_address", item.getString("p_address"));
                map.put("c_phone", item.getString("c_phone"));
                map.put("p_customer", item.getString("p_customer"));
                map.put("p_phone", item.getString("p_phone"));
                map.put("c_customer", item.getString("c_customer"));
                map.put("product_info", item.getString("productinfo"));
                map.put("p_personname", item.getString("p_personname"));
                map.put("order_statu", "待处理...");
                map.put("main_type", item.getString("main_type"));//订单所属区域
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 新接口解析维修单列表
     *
     * @param json
     * @return
     * @throws Exception
     */
    public static List<Map<String, String>> analysisRepairingList(String json) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            JSONArray array = new JSONArray(new String(json));
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                Map<String, String> map = new HashMap<String, String>();
                map.put("order_code", item.getString("order_code"));
                map.put("order_type", item.getString("order_type"));
                map.put("order_statu", item.getString("order_statu"));
                map.put("order_remark",
                        "null".equals(item.getString("order_remark"))
                                || null == item.getString("order_remark") ?
                                "" : item.getString("order_remark"));
                map.put("order_engineer", item.getString("order_engineer"));
                map.put("order_singler", item.getString("oder_singler"));
                map.put("singler_phone", item.getString("singler_phone"));
                map.put("time_single", item.getString("time_single"));
                map.put("time_deadline", item.getString("time_deadline"));
                map.put("customer_name", item.getString("customer_name"));
                map.put("customer_id", item.getString("customer_id"));
                map.put("customer_person", item.getString("customer_person"));
                map.put("customer_personphone", item.getString("customer_personphone"));
                map.put("customer_address",
                        "null".equals(item.getString("customer_address"))
                                || null == item.getString("customer_address") ?
                                "" : item.getString("customer_address"));
                map.put("product_info", item.getString("product_info"));
                map.put("product_number", item.getString("product_number"));
                map.put("city_id", item.getString("city_id"));
                map.put("province_id", item.getString("province_id"));
                map.put("main_type", item.getString("main_type"));//订单所属区域
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 新接口解析安装调试单列表
     *
     * @param json
     * @return
     */
    public static List<Map<String, String>> analysisInstallList(String json) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                Map<String, String> map = new HashMap<String, String>();
                map.put("order_type", item.getString("order_type"));
                map.put("c_address", item.getString("c_address"));
                map.put("singler_phone", item.getString("singler_phone"));
                map.put("order_engineer", item.getString("order_engineer"));
                map.put("order_singler", item.getString("order_singler"));
                map.put("userid", item.getString("userid"));
                map.put("order_code", item.getString("code"));
                map.put("time_deadline", item.getString("time_deadline"));
                map.put("c_personname", item.getString("c_personname"));
                map.put("p_address", item.getString("p_address"));
                map.put("c_phone", item.getString("c_phone"));
                map.put("p_customer", item.getString("p_customer"));
                map.put("p_phone", item.getString("p_phone"));
                map.put("c_customer", item.getString("c_customer"));
                map.put("product_info", item.getString("productinfo"));
                map.put("p_personname", item.getString("p_personname"));
                map.put("time_single", item.getString("time_single"));
                map.put("order_remark",
                        "null".equals(item.getString("order_remark"))
                                || null == item.getString("order_remark") ?
                                "" : item.getString("order_remark"));
                map.put("main_type", item.getString("main_type"));//订单所属区域
                list.add(map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 客户信息解析
     *
     * @param json
     * @return
     * @throws Exception
     */

    public static List<Map<String, String>> analysisCustomerMessage(String json) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            JSONArray array = new JSONArray(new String(json));
            for (int i = 0; i < array.length(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                JSONObject item = array.getJSONObject(i);
                map.put("customer_person", item.getString("customer_person").trim());
                map.put("customer_cityid", item.getString("customer_cityid").trim());
                map.put("customer_provinceid", item.getString("customer_provinceid").trim());
                map.put("customer_name", item.getString("customer_name").trim());
                map.put("customer_id", item.getString("customer_id").trim());
                map.put("customer_count", item.getString("customer_count").trim());
                map.put("customer_phone", item.getString("customer_phone").trim());
                map.put("customer_address", item.getString("customer_address").trim());
                list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 解析订单已上传的设备
     *
     * @param json
     * @return
     */
    public static List<Map<String, String>> analysisProductMessage(String json) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            JSONArray array = new JSONArray(new String(json));
            for (int i = 0; i < array.length(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                JSONObject item = array.getJSONObject(i);
                map.put("product_name", item.getString("product_name").trim());
                map.put("product_number", item.getString("product_number").trim());
                map.put("car_number", item.getString("car_number").trim());
                map.put("fault_cause", item.getString("fault_cause").trim());
                map.put("result_maint", item.getString("result_maint").trim());
                map.put("cost_maintenance", item.getString("cost_maintenance").trim());
                map.put("cost_material", item.getString("cost_material").trim());
                map.put("cost_all", item.getString("cost_all").trim());
                list.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

}
