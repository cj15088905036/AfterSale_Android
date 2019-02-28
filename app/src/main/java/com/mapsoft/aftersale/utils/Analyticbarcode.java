package com.mapsoft.aftersale.utils;

/**
 * Created by Administrator on 2017/9/4.
 */

import android.content.Context;


import com.mapsoft.aftersale.R;

import java.util.HashMap;
import java.util.Map;


/**
 * 解析扫描条形码
 */
public class Analyticbarcode {


    public static Map<String, String> arrAnalytic(Context context, String message) {
        String[] s_Pro_Name1 = context.getResources().getStringArray(R.array.Pro_Name1);
        String[] s_Pro_Name2 = context.getResources().getStringArray(R.array.Pro_Name2);
        String[] s_Pro_Name3 = context.getResources().getStringArray(R.array.Pro_Name3);
        String[] s_Pro_Date = context.getResources().getStringArray(R.array.Pro_Date);
        String[] bus_station_broadcasters = context.getResources().getStringArray(R.array.bus_station_broadcaster);//电脑报站器版本
        String[] ddps = context.getResources().getStringArray(R.array.DDP);//调度屏
        String[] s_Sdv = context.getResources().getStringArray(R.array.Sdv);
        Map<String, String> map = new HashMap<String, String>();
        String Pro_Name1 = message.substring(0, 2);//品牌
        String Pro_Name2 = message.substring(2, 4);//产品
        String Pro_Name3 = message.substring(4, 6);//型号
        String Pro_Year1 = message.substring(6, 7);//年份
        String Pro_Year2 = message.substring(7, 8);//年份
        String Pro_Month = message.substring(8, 9);//月份
        String mList = message.substring(9, 14);//产品序列号
        String Hdv = message.substring(14, 16);//硬件版本号
        String Sdv = message.substring(16, message.length());//软件版本号
        if ("06".equals(Pro_Name2)) {
            switch (Hdv) {//电脑报站器产品对应的硬件版本
                case "01":
                    map.put("Hdv", bus_station_broadcasters[0]);
                    break;
                case "02":
                    map.put("Hdv", bus_station_broadcasters[1]);
                    break;
            }
        } else if ("18".equals(Pro_Name2)) {
            switch (Hdv) {//调度屏产品对应的硬件版本
                case "01":
                    map.put("Hdv", ddps[0]);
                    break;
                case "02":
                    map.put("Hdv", ddps[1]);
                    break;
            }
        } else {
            map.put("Hdv", "");
        }

        switch (Pro_Name1) {
            case "01":
                Pro_Name1 = s_Pro_Name1[0];
                break;
            case "02":
                Pro_Name1 = s_Pro_Name1[1];
                break;
        }
        switch (Pro_Name2) {
            case "01":
                Pro_Name2 = s_Pro_Name2[0];
                break;
            case "02":
                Pro_Name2 = s_Pro_Name2[1];
                break;
            case "03":
                Pro_Name2 = s_Pro_Name2[2];
                break;
            case "04":
                Pro_Name2 = s_Pro_Name2[3];
                break;
            case "05":
                Pro_Name2 = s_Pro_Name2[4];
                break;
            case "06":
                Pro_Name2 = s_Pro_Name2[5];
                break;
            case "07":
                Pro_Name2 = s_Pro_Name2[6];
                break;
            case "08":
                Pro_Name2 = s_Pro_Name2[7];
                break;
            case "09":
                Pro_Name2 = s_Pro_Name2[8];
                break;
            case "10":
                Pro_Name2 = s_Pro_Name2[9];
                break;
            case "11":
                Pro_Name2 = s_Pro_Name2[10];
                break;
            case "12":
                Pro_Name2 = s_Pro_Name2[11];
                break;
            case "13":
                Pro_Name2 = s_Pro_Name2[12];
                break;
            case "14":
                Pro_Name2 = s_Pro_Name2[13];
                break;
            case "15":
                Pro_Name2 = s_Pro_Name2[14];
                break;
            case "16":
                Pro_Name2 = s_Pro_Name2[15];
                break;
            case "17":
                Pro_Name2 = s_Pro_Name2[16];
                break;
            case "18":
                Pro_Name2 = s_Pro_Name2[17];
                break;
            case "19":
                Pro_Name2 = s_Pro_Name2[18];
                break;
            case "20":
                Pro_Name2 = s_Pro_Name2[19];
                break;
            case "21":
                Pro_Name2 = s_Pro_Name2[20];
                break;
            case "22":
                Pro_Name2 = s_Pro_Name2[21];
                break;
            case "23":
                Pro_Name2 = s_Pro_Name2[22];
                break;
            case "24":
                Pro_Name2 = s_Pro_Name2[23];
                break;
            case "25":
                Pro_Name2 = s_Pro_Name2[24];
                break;
        }
        switch (Pro_Name3) {
            case "A0":
                Pro_Name3 = s_Pro_Name3[0];
                break;
            case "A1":
                Pro_Name3 = s_Pro_Name3[1];
                break;
            case "A2":
                Pro_Name3 = s_Pro_Name3[2];
                break;
            case "A3":
                Pro_Name3 = s_Pro_Name3[3];
                break;
            case "A4":
                Pro_Name3 = s_Pro_Name3[4];
                break;
            case "A5":
                Pro_Name3 = s_Pro_Name3[5];
                break;
            case "A6":
                Pro_Name3 = s_Pro_Name3[6];
                break;
            case "A7":
                Pro_Name3 = s_Pro_Name3[7];
                break;
            case "A8":
                Pro_Name3 = s_Pro_Name3[8];
                break;
            case "A9":
                Pro_Name3 = s_Pro_Name3[9];
                break;
            case "AA":
                Pro_Name3 = s_Pro_Name3[10];
                break;
            case "AB":
                Pro_Name3 = s_Pro_Name3[11];
                break;
            case "AC":
                Pro_Name3 = s_Pro_Name3[12];
                break;
            case "AD":
                Pro_Name3 = s_Pro_Name3[13];
                break;
            case "B0":
                Pro_Name3 = s_Pro_Name3[14];
                break;
            case "B1":
                Pro_Name3 = s_Pro_Name3[15];
                break;
            case "B2":
                Pro_Name3 = s_Pro_Name3[16];
                break;
            case "B3":
                Pro_Name3 = s_Pro_Name3[17];
                break;
            case "C0":
                Pro_Name3 = s_Pro_Name3[18];
                break;
            case "C1":
                Pro_Name3 = s_Pro_Name3[19];
                break;
            case "C2":
                Pro_Name3 = s_Pro_Name3[20];
                break;
            case "D0":
                Pro_Name3 = s_Pro_Name3[21];
                break;
            case "D1":
                Pro_Name3 = s_Pro_Name3[22];
                break;
            case "E0":
                Pro_Name3 = s_Pro_Name3[23];
                break;
            case "E1":
                Pro_Name3 = s_Pro_Name3[24];
                break;
            case "E2":
                Pro_Name3 = s_Pro_Name3[25];
                break;
            case "F0":
                Pro_Name3 = s_Pro_Name3[26];
                break;
            case "F1":
                Pro_Name3 = s_Pro_Name3[27];
                break;
            case "F2":
                Pro_Name3 = s_Pro_Name3[28];
                break;
            case "F3":
                Pro_Name3 = s_Pro_Name3[29];
                break;
            case "G0":
                Pro_Name3 = s_Pro_Name3[30];
                break;
            case "G1":
                Pro_Name3 = s_Pro_Name3[31];
                break;
            case "H0":
                Pro_Name3 = s_Pro_Name3[32];
                break;
            case "H1":
                Pro_Name3 = s_Pro_Name3[33];
                break;
            case "I0":
                Pro_Name3 = s_Pro_Name3[34];
                break;
            case "I1":
                Pro_Name3 = s_Pro_Name3[35];
                break;
            case "J0":
                Pro_Name3 = s_Pro_Name3[36];
                break;
            case "AE":
                Pro_Name3 = s_Pro_Name3[37];
                break;
            case "I2":
                Pro_Name3 = s_Pro_Name3[38];
                break;
            case "I3":
                Pro_Name3 = s_Pro_Name3[39];
                break;
            case "K0":
                Pro_Name3 = s_Pro_Name3[40];
                break;
            case "K1":
                Pro_Name3 = s_Pro_Name3[41];
                break;
            case "00":
                Pro_Name3 = "";
                break;
        }
        String Pro_Name = Pro_Name1 + Pro_Name2 + Pro_Name3;
        map.put("Pro_Name", Pro_Name);
        switch (Pro_Year1) {
            case "A":
                Pro_Year1 = s_Pro_Date[0];
                break;
            case "B":
                Pro_Year1 = s_Pro_Date[1];
                break;
            case "C":
                Pro_Year1 = s_Pro_Date[2];
                break;
            case "D":
                Pro_Year1 = s_Pro_Date[3];
                break;
            case "E":
                Pro_Year1 = s_Pro_Date[4];
                break;
            case "F":
                Pro_Year1 = s_Pro_Date[5];
                break;
            case "G":
                Pro_Year1 = s_Pro_Date[6];
                break;
            case "H":
                Pro_Year1 = s_Pro_Date[7];
                break;
            case "I":
                Pro_Year1 = s_Pro_Date[8];
                break;
            case "J":
                Pro_Year1 = s_Pro_Date[9];
                break;
        }
        switch (Pro_Year2) {
            case "A":
                Pro_Year2 = s_Pro_Date[0];
                break;
            case "B":
                Pro_Year2 = s_Pro_Date[1];
                break;
            case "C":
                Pro_Year2 = s_Pro_Date[2];
                break;
            case "D":
                Pro_Year2 = s_Pro_Date[3];
                break;
            case "E":
                Pro_Year2 = s_Pro_Date[4];
                break;
            case "F":
                Pro_Year2 = s_Pro_Date[5];
                break;
            case "G":
                Pro_Year2 = s_Pro_Date[6];
                break;
            case "H":
                Pro_Year2 = s_Pro_Date[7];
                break;
            case "I":
                Pro_Year2 = s_Pro_Date[8];
                break;
            case "J":
                Pro_Year2 = s_Pro_Date[9];
                break;
        }
        switch (Pro_Month) {
            case "A":
                Pro_Month = s_Pro_Date[1];
                break;
            case "B":
                Pro_Month = s_Pro_Date[2];
                break;
            case "C":
                Pro_Month = s_Pro_Date[3];
                break;
            case "D":
                Pro_Month = s_Pro_Date[4];
                break;
            case "E":
                Pro_Month = s_Pro_Date[5];
                break;
            case "F":
                Pro_Month = s_Pro_Date[6];
                break;
            case "G":
                Pro_Month = s_Pro_Date[7];
                break;
            case "H":
                Pro_Month = s_Pro_Date[8];
                break;
            case "I":
                Pro_Month = s_Pro_Date[9];
                break;
            case "J":
                Pro_Month = s_Pro_Date[10];
                break;
            case "K":
                Pro_Month = s_Pro_Date[11];
                break;
            case "L":
                Pro_Month = s_Pro_Date[12];
                break;
        }
        String Pro_Date = "20" + Pro_Year1 + Pro_Year2 + "年" + Pro_Month + "月";
        map.put("Pro_Date", Pro_Date);
        map.put("mList", mList);

        switch (Sdv) {
            case "00":
                Sdv = "";
                break;
            case "01":
                Sdv = s_Sdv[0];
                break;
//            case "02":
//                Sdv = s_Sdv[1];
//                break;
        }
        map.put("Sdv", Sdv);
        return map;
    }
}
