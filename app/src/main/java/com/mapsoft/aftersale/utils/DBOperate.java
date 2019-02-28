package com.mapsoft.aftersale.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/11/24.
 * 数据库操作类
 */


public class DBOperate {

    private static final String MESSAGE_INSERT_OK = "1";//插入成功
    private static final String MESSAGE_INSERT_ERROR_DB = "2";//插入失败
    private static final String MESSAGE_INSERT_ERROR_DBTA = "3";//插入失败

    private static final String MESSAGE_UPDATE_OK = "1";//更新成功
    private static final String MESSAGE_UPDATE_ERROR = "2";//更新失败


    /**
     * 插入
     *
     * @param db
     * @param map
     * @return
     */

    public static String insertMessage(SQLiteDatabase db, Map<String, String> map) {
        if (db != null) {
            ContentValues cValues = new ContentValues();
            cValues.put("repair_code", map.get("repair_code"));
            cValues.put("repair_name", map.get("repair_name"));
            cValues.put("product_type", map.get("product_type"));
            cValues.put("order_code", map.get("order_code"));
            cValues.put("product_code", map.get("product_code"));
            cValues.put("product_name", map.get("product_name"));
            cValues.put("veh_code", map.get("veh_code"));
            cValues.put("fault_cause", map.get("fault_cause"));
            cValues.put("maint_result", map.get("maint_result"));
            cValues.put("material_cost", map.get("material_cost"));
            cValues.put("maintenance_cost", map.get("maintenance_cost"));
            cValues.put("input_time", map.get("input_time"));//出厂时间
            cValues.put("product_state", map.get("product_state"));
            cValues.put("sim_card", map.get("sim_card"));
            cValues.put("install_address", map.get("install_address"));
            long insert = db.insert(DBHelper.PRODUCT_TABLE, null, cValues);
            Log.e("插入成功？", insert + "");
            if (insert != -1) {
                return MESSAGE_INSERT_OK;
            }else {
                return MESSAGE_INSERT_ERROR_DBTA;
            }
        }
        return MESSAGE_INSERT_ERROR_DB;
    }

    /**
     * 更新
     *
     * @param db
     * @param map
     * @return
     */

    public static String updateMessage(SQLiteDatabase db, Map<String, String> map) {
        if (db != null) {
            ContentValues cValues = new ContentValues();
            // 更新数据,设置修改参数
            cValues.put("product_name", map.get("product_name"));
            cValues.put("veh_code", map.get("veh_code"));
            cValues.put("fault_cause", map.get("fault_cause"));
            cValues.put("maint_result", map.get("maint_result"));
            cValues.put("material_cost", map.get("material_cost"));
            cValues.put("maintenance_cost", map.get("maintenance_cost"));
            cValues.put("product_state", map.get("product_state"));
            cValues.put("sim_card", map.get("sim_card"));
            cValues.put("install_address", map.get("install_address"));
            //更新条件
            String whereClause = "repair_code=? and order_code=? and product_code=?";
            String[] whereArgs = {String.valueOf(map.get("repair_code")),
                    String.valueOf(map.get("order_code")),
                    String.valueOf(map.get("product_code"))
            };
            int update = db.update(DBHelper.PRODUCT_TABLE, cValues, whereClause, whereArgs);
            Log.e("更新成功?", update + "");
            return MESSAGE_UPDATE_OK;

        }
        return MESSAGE_UPDATE_ERROR;
    }

    /**
     * 查找并取出表中的数据
     */
    public static List<Map<String, String>> selectAndGetMessage(SQLiteDatabase db,
                                                                String order_code) {
        if (db != null) {
            Cursor cursor = db.rawQuery(
                    "select * from " + DBHelper.PRODUCT_TABLE + " where order_code=?",
                    new String[]{String.valueOf(order_code)});

            if (cursor.getCount() != 0) {//该订单号下有存设备信息
                List<Map<String, String>> list = new ArrayList<>();
                while (cursor.moveToNext()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("repair_code", cursor.getString(cursor.getColumnIndex("repair_code")));
                    map.put("repair_name", cursor.getString(cursor.getColumnIndex("repair_name")));
                    map.put("product_code", cursor.getString(cursor.getColumnIndex("product_code")));
                    map.put("product_name", cursor.getString(cursor.getColumnIndex("product_name")));
                    map.put("product_type", cursor.getString(cursor.getColumnIndex("product_type")));
                    map.put("input_time", cursor.getString(cursor.getColumnIndex("input_time")));//出厂时间
                    map.put("veh_code", cursor.getString(cursor.getColumnIndex("veh_code")));
                    map.put("fault_cause", cursor.getString(cursor.getColumnIndex("fault_cause")));
                    map.put("maint_result", cursor.getString(cursor.getColumnIndex("maint_result")));
                    map.put("material_cost", cursor.getString(cursor.getColumnIndex("material_cost")));
                    map.put("maintenance_cost", cursor.getString(cursor.getColumnIndex("maintenance_cost")));
                    map.put("product_state", cursor.getString(cursor.getColumnIndex("product_state")));
//                    map.put("sim_card", cursor.getString(cursor.getColumnIndex("sim_card")));//公路
//                    map.put("install_address", cursor.getString(cursor.getColumnIndex("install_address")));//公路
                    list.add(map);
                }
                return list;
            }
            return new ArrayList<>();
        }
        return null;
    }

    /**
     * 查找数据
     *
     * @param db
     * @param order_code
     * @param product_code
     * @return
     */


    public static boolean selectMessage(SQLiteDatabase db, String order_code, String product_code) {
        if (db != null) {
            Cursor cursor = db.rawQuery(
                    "select * from " + DBHelper.PRODUCT_TABLE + " where order_code=? and product_code=?",
                    new String[]{String.valueOf(order_code), String.valueOf(product_code)});
            if (cursor.getCount() != 0) {//有数据
                return true;
            }
        }
        return false;
    }

    /**
     * 完成订单时删除对应订单编号的数据
     *
     * @param db
     * @param order_code 订单编号
     * @return
     */

    public static void deleteByOrderCode(SQLiteDatabase db, String order_code) {
        if (db != null) {
            db.delete(DBHelper.PRODUCT_TABLE, "order_code=?", new String[]{String.valueOf(order_code)});
        }
    }


    /**
     * 完成订单时，获取该订单下的所有产品的保存状态
     *
     * @param db
     * @param order_code
     * @return
     */
    public static List<Map<String, String>> selectStateList(SQLiteDatabase db, String order_code) {
        List<Map<String, String>> stateList = new ArrayList<>();
        if (db != null) {
            Cursor cursor = db.rawQuery("select * from " + DBHelper.PRODUCT_TABLE + " where order_code=?",
                    new String[]{String.valueOf(order_code)});
            if (cursor.getCount() != 0) {
                while (cursor.moveToNext()) {
                    Map<String, String> map = new HashMap<>();
                    map.put("product_state", cursor.getString(cursor.getColumnIndex("product_state")));
                    stateList.add(map);
                }
                return stateList;
            }
            return new ArrayList<>();
        }
        return null;
    }
}
