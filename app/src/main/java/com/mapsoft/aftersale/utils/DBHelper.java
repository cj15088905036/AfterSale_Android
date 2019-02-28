package com.mapsoft.aftersale.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.internal.bind.SqlDateTypeAdapter;

/**
 * Created by Administrator on 2017/11/22.
 * 创建数据库和创建表
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "mapsoft_aftersale.db";
    private static final int DB_VERSION = 2;
    public static final String PRODUCT_TABLE = "ma_product_table";//设备信息表
    public static final String MESSAGE_TABLE = "ma_message_table";//信息表


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String product_table = "CREATE TABLE IF NOT EXISTS " + PRODUCT_TABLE +
                " (" +
                "repair_code TEXT NOT NULL," +//维修人员ID
                "repair_name TEXT NOT NULL," +//维修人员名称
                "product_type TEXT NOT NULL," +//产品来源类型(1、外派维修单；2、安装调试单)
                "order_code TEXT NOT NULL," +//订单编号
                "product_code TEXT NOT NULL," +//产品序列号
                "product_name TEXT NOT NULL," +//产品名称
                "veh_code TEXT ," +//车牌号
                "fault_cause TEXT," +//故障原因
                "maint_result TEXT," +//维修结果
                "material_cost TEXT," +//材料费
                "maintenance_cost TEXT," +//维修费
                "input_time TEXT NOT NULL," +//录入数据库时间
                "product_state TEXT," +//状态(保存/提交)
                "PRIMARY KEY(repair_code,order_code,product_code)" +//联合主键
                ");";
        db.execSQL(product_table);
        String message_table = "CREATE TABLE " + MESSAGE_TABLE +
                " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +//自增性ID
                "message_type TEXT NOT NULL," +//消息类型
                "message_title TEXT NOT NULL," +//消息标题
                "message_ preview TEXT NOT NULL," +//消息预览
                "message_content TEXT NOT NULL, " +//消息内容
                "message_sender TEXT NOT NULL, " +//发送者
                "message_send_time TEXT, " +//接收时间
                "message_state TEXT);";//状态(已读/未读)
        db.execSQL(message_table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            //-- 把原表改成另外一个名字作为暂存表--产品设备信息表
            db.execSQL("ALTER TABLE ma_product_table RENAME TO temp_ma_product_table;");

            //-- 用原表的名字创建新表
            String product_table = "CREATE TABLE IF NOT EXISTS " + PRODUCT_TABLE +
                    " (" +
                    "repair_code TEXT," +//维修人员ID
                    "repair_name TEXT," +//维修人员名称
                    "product_type TEXT," +//产品来源类型(1、外派维修单；2、安装调试单)
                    "order_code TEXT," +//订单编号
                    "product_code TEXT," +//产品序列号
                    "product_name TEXT," +//产品名称
                    "veh_code TEXT," +//车牌号
                    "fault_cause TEXT," +//故障原因
                    "maint_result TEXT," +//维修结果
                    "material_cost TEXT," +//材料费
                    "maintenance_cost TEXT," +//维修费
                    "input_time TEXT," +//录入数据库时间
                    "product_state TEXT" +//状态(保存/提交)
                    ");";
            db.execSQL(product_table);

            //-- 将暂存表数据写入到新表，很方便的是不需要去理会自动增长的 ID
            db.execSQL("INSERT INTO ma_product_table SELECT * FROM temp_ma_product_table");

            //-- 删除暂存表
            db.execSQL("DROP TABLE temp_ma_product_table;");

            //删除信息表
            db.execSQL("DROP TABLE ma_message_table;");
        }
    }
}
