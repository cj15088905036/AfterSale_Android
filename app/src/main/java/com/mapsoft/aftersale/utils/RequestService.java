package com.mapsoft.aftersale.utils;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/28.
 * 请求服务器
 */

public class RequestService {
    private static final String MESSAGE_INTERNET = "0";
    private static final String MESSAGE_OK = "1";
    private static final String MESSAGE_ERROR = "2";

    /**
     * 新接口登录
     */
    public static class NewLogin {
        private static final String MESSAGE_PWD = "2";
        private static final String MESSAGE_NAME = "1";

        public String check(String name, String password) throws NoSuchAlgorithmException {
            String path = "http://60.191.59.10:19000/WebApi/api_maintorder.aspx?FLAG=Login";
            String md5Pwd = MD5.md5(password);
            Map<String, String> params = new HashMap<String, String>();
            params.put("Account", name);
            params.put("PassWord", md5Pwd);
            try {
                return sendGETRequest(path, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public String sendGETRequest(String path, Map<String, String> params)
                throws IOException {
            String jsonLogin = HttpUtils.sendGETRequest(path, params, "UTF-8");
            if ("".equals(jsonLogin)) {
                return MESSAGE_INTERNET;
            } else if (jsonLogin == null) {
                return null;
            } else if ("404".equals(jsonLogin) || "500".equals(jsonLogin)) {
                return jsonLogin;
            } else {
                String[] res = jsonLogin.split(":");
                if (res.length == 2) {
                    if (res[1].contains("1")) {
                        return MESSAGE_PWD;
                    } else if (res[1].contains("0")) {
                        return MESSAGE_NAME;
                    } else {
                        return jsonLogin;
                    }
                }
                return jsonLogin;
            }
        }
    }


    /**
     * 新接口获取维修单列表
     */
    public static class NewGetRepairList {
        public static String check(String account, String userId) throws NoSuchAlgorithmException {
            String path = "http://60.191.59.10:19000/WebApi/api_maintorder.aspx?FLAG=OrderInfo";
            Map<String, String> params = new HashMap<String, String>();
            params.put("Account", account);
            params.put("UserId", userId);
            try {
                return sendGETRequest(path, params, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String sendGETRequest(String path, Map<String, String> params, String encode)
                throws IOException {
            String jsonRepairListResult = HttpUtils.sendGETRequest(path, params, encode);
            if ("".equals(jsonRepairListResult)) {
                return MESSAGE_INTERNET;
            } else if (jsonRepairListResult == null) {
                return null;
            } else if ("404".equals(jsonRepairListResult) || "500".equals(jsonRepairListResult)) {
                return jsonRepairListResult;
            } else {
                String[] str = jsonRepairListResult.split(":");
                if (str[1].contains("0")) {
                    return MESSAGE_ERROR;
                }
                return jsonRepairListResult;
            }
        }
    }


    /**
     * 新接口产品维修记录
     */
    public static class NewRepairRecord {
        public static String check(String mList) throws NoSuchAlgorithmException {
            String path = "http://60.191.59.10:19000/WebApi/api_maintorder.aspx?FlAG=Record";
            Map<String, String> params = new HashMap<String, String>();
            params.put("ProductNumber", mList);
            try {
                return sendGETRequest(path, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String sendGETRequest(String path, Map<String, String> params)
                throws IOException {
            String jsonRepairRecord = HttpUtils.sendGETRequest(path, params, "UTF-8");
            if ("".equals(jsonRepairRecord)) {
                return MESSAGE_INTERNET;
            } else if (jsonRepairRecord == null) {
                return null;
            } else if ("404".equals(jsonRepairRecord) || "500".equals(jsonRepairRecord)) {
                return jsonRepairRecord;
            } else {
                String[] res = jsonRepairRecord.split(":");
                if (res[1].contains("0")) {
                    return MESSAGE_ERROR;//2不存在
                } else {
                    return jsonRepairRecord;
                }
            }
        }
    }


    /**
     * 新接口历史维修记录
     */
    public static class NewPersonRepairRecord {
        public static String check(String iname,
                                   String userId,
                                   String firstDay,
                                   String lastDay) throws NoSuchAlgorithmException {
            String path = "http://60.191.59.10:19000/WebApi/api_maintorder.aspx?FLAG=OldOrder";
            Map<String, String> params = new HashMap<String, String>();
            params.put("Account", iname);
            params.put("UserId", userId);
            params.put("StartTime", firstDay);
            params.put("EndTime", lastDay);
            try {
                return sendGETRequest(path, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String sendGETRequest(String path, Map<String, String> params)
                throws IOException {
            String jsonPersonRepairRecord = HttpUtils.sendGETRequest(path, params, "UTF-8");
            if ("".equals(jsonPersonRepairRecord)) {
                return MESSAGE_INTERNET;
            } else if (jsonPersonRepairRecord == null) {
                return null;
            } else if ("404".equals(jsonPersonRepairRecord) || "500".equals(jsonPersonRepairRecord)) {
                return jsonPersonRepairRecord;
            } else {
                String[] res = jsonPersonRepairRecord.split(":");
                if (!res[1].contains("0")) {
                    return jsonPersonRepairRecord;
                } else {
                    return MESSAGE_ERROR;//2不存在
                }
            }

        }
    }


    /**
     * 新接口上传维修结果
     */
    public static class UploadingRepairResult {
        public static String check(String user_id, String code, String pro_name, String vehcode,
                                   String faultcause, String maintresult,
                                   String materialcost, String maintenancecost,
                                   String list, String name) throws NoSuchAlgorithmException {
            String path = "http://60.191.59.10:19000/WebApi/api_maintorder.aspx?FLAG=ResultInfo";
            Map<String, String> params = new HashMap<String, String>();
            params.put("UserId", user_id);
            params.put("OrderCode", code);
            params.put("ProductName", pro_name);
            params.put("VehCode", vehcode);
            params.put("FaultCause", faultcause);
            params.put("MaintResult", maintresult);
            params.put("MaterialCost", materialcost);
            params.put("MaintenanceCost", maintenancecost);
            params.put("ProductNumber", list);
            params.put("Name", name);
            try {
                return sendGETRequest(path, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String sendGETRequest(String path, Map<String, String> params)
                throws IOException {
            String jsonRepairResult = HttpUtils.sendGETRequest(path, params, "UTF-8");

            if ("".equals(jsonRepairResult)) {
                return MESSAGE_INTERNET;
            } else if (jsonRepairResult == null) {
                return null;
            } else if ("404".equals(jsonRepairResult) || "500".equals(jsonRepairResult)) {
                return jsonRepairResult;
            } else {
                String[] res = jsonRepairResult.split(":");
                if (res[1].contains("1")) {
                    return MESSAGE_OK;
                } else {
                    return MESSAGE_ERROR;
                }
            }
        }
    }


    /**
     * 新接口查询补充设备
     */
    public static class NewSelectSupProduct {

        public static String check(String pro_list) throws NoSuchAlgorithmException {
            String path = "http://60.191.59.10:19000/WebApi/api_maintorder.aspx?FLAG=ProductInfo";
            Map<String, String> params = new HashMap<String, String>();
            params.put("ProductNumber", pro_list);
            try {
                return sendGETRequest(path, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "";
        }

        public static String sendGETRequest(String path, Map<String, String> params)
                throws IOException {
            String jsonRepairSup = HttpUtils.sendGETRequest(path, params, "UTF-8");
            if ("".equals(jsonRepairSup)) {
                return MESSAGE_INTERNET;
            } else if (jsonRepairSup == null) {
                return null;
            } else if ("404".equals(jsonRepairSup) || "500".equals(jsonRepairSup)) {
                return jsonRepairSup;
            } else {
//                String[] res = jsonRepairSup.split(":");
                if (jsonRepairSup.length() == 2) {
                    return MESSAGE_ERROR;
                } else {
                    return jsonRepairSup;
                }
            }
        }
    }


    /**
     * 新接口维修单完成
     */
    public static class RequestRepairComplete {
        public static String check(String name, String code) throws NoSuchAlgorithmException {
            String path = "http://60.191.59.10:19000/WebApi/api_maintorder.aspx?FLAG=Complete";
            Map<String, String> params = new HashMap<String, String>();
            params.put("OrderCode", code);
            params.put("Name", name);
            try {
                return sendGETRequest(path, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String sendGETRequest(String path, Map<String, String> params)
                throws IOException {
            String jsonComplete = HttpUtils.sendGETRequest(path, params, "UTF-8");
            if ("".equals(jsonComplete)) {
                return MESSAGE_INTERNET;
            } else if (jsonComplete == null) {
                return null;
            } else if ("404".equals(jsonComplete) || "500".equals(jsonComplete)) {
                return jsonComplete;
            } else {
                String[] res = jsonComplete.split(":");
                if (res[1].contains("1")) {
                    return MESSAGE_OK;
                } else if (res[1].contains("0")) {
                    return MESSAGE_ERROR;
                } else {
                    return null;
                }
            }

        }
    }


    /**
     * 新接口查询订单下的维修产品
     */
    public static class SelectRepairByOrderCode {
        public static String check(String code) throws NoSuchAlgorithmException {
            String path = "http://60.191.59.10:19000/WebApi/api_maintorder.aspx?FLAG=ReslutInfo";
            Map<String, String> params = new HashMap<String, String>();
            params.put("OrderCode", code);
            try {
                return sendGETRequest(path, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String sendGETRequest(String path, Map<String, String> params)
                throws IOException {
            String jsonSelect = HttpUtils.sendGETRequest(path, params, "UTF-8");
            if ("".equals(jsonSelect)) {
                return MESSAGE_INTERNET;
            } else if (jsonSelect == null) {
                return null;
            } else if ("404".equals(jsonSelect) || "500".equals(jsonSelect)) {
                return jsonSelect;
            } else {
                String[] res = jsonSelect.split(":");
                if (res[1].contains("0")) {
                    return MESSAGE_ERROR;
                } else {
                    return jsonSelect;
                }
            }

        }
    }


    /**
     * 新接口未派单维修单列表
     */
    public static class NewRepairGrabList {
        public static String check(String iname, String UserId) throws NoSuchAlgorithmException {
            String path = "http://60.191.59.10:19000/WebApi/api_maintorder.aspx?FLAG=NewOrderInfo";
            Map<String, String> params = new HashMap<String, String>();
            params.put("Account", iname);
            params.put("UserId", UserId);
            try {
                return sendGETRequest(path, params, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String sendGETRequest(String path, Map<String, String> params, String encode)
                throws IOException {
            String jsonRepairGrab = HttpUtils.sendGETRequest(path, params, encode);
            if ("".equals(jsonRepairGrab)) {
                return MESSAGE_INTERNET;
            } else if (jsonRepairGrab == null) {
                return null;
            } else if ("404".equals(jsonRepairGrab) || "500".equals(jsonRepairGrab)) {
                return jsonRepairGrab;
            } else {
                String[] str = jsonRepairGrab.split(":");
                if (str[0].contains("result") && str[1].contains("0")) {
                    return MESSAGE_ERROR;
                } else {
                    return jsonRepairGrab;
                }
            }
        }
    }


    /**
     * 新接口维修单抢单
     */
    public static class NewRepairGrab {
        public static String check(String name, String code) throws NoSuchAlgorithmException {
            String path = "http://60.191.59.10:19000/WebApi/api_maintorder.aspx?FLAG=RobOrder";
            Map<String, String> params = new HashMap<String, String>();
            params.put("UserName", name);
            params.put("OrderId", code);
            try {
                return sendGETRequest(path, params, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return MESSAGE_INTERNET;
        }

        public static String sendGETRequest(String path, Map<String, String> params, String encode)
                throws IOException {
            String jsonGrab = HttpUtils.sendGETRequest(path, params, encode);
            if ("".equals(jsonGrab)) {
                return MESSAGE_INTERNET;
            } else if (jsonGrab == null) {
                return null;
            } else if ("404".equals(jsonGrab) || "500".equals(jsonGrab)) {
                return jsonGrab;
            } else {
                String[] res = jsonGrab.split("：");
                if (res[0].contains("result") && res[1].contains("1")) {
                    return MESSAGE_OK;
                } else if (res[0].contains("result") && res[1].contains("0")) {
                    return MESSAGE_ERROR;
                } else {
                    return null;
                }
            }
        }
    }


    /**
     * 新安装调试订单列表
     */
    public static class NewGetInstallList {
        public static String check(String iname, String UserId) throws NoSuchAlgorithmException {
            String path = "http://60.191.59.10:19000/WebApi/api_installorders.aspx?FLAG=OrderInfo";
            Map<String, String> params = new HashMap<String, String>();
            params.put("Account", iname);
            params.put("UserId", UserId);
            try {
                return sendGETRequest(path, params, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return MESSAGE_INTERNET;
        }

        public static String sendGETRequest(String path, Map<String, String> params, String encode)
                throws IOException {
            String jsonInstall = HttpUtils.sendGETRequest(path, params, encode);
            if ("".equals(jsonInstall)) {
                return MESSAGE_INTERNET;
            } else if (jsonInstall == null) {
                return null;
            } else if ("404".equals(jsonInstall) || "500".equals(jsonInstall)) {
                return jsonInstall;
            } else {
                String[] res = jsonInstall.split(":");
                if (res[0].contains("result") && res[1].contains("0")) {
                    return MESSAGE_ERROR;
                } else {
                    return jsonInstall;
                }
            }

        }
    }


    /**
     * 新接口请求未派单安装单列表
     */
    public static class NewInstallGrabList {
        public static String check(String iname, String UserId) throws NoSuchAlgorithmException {
            String path = "http://60.191.59.10:19000/webapi/api_installorders.aspx?FLAG=NewOrderInfo";
            Map<String, String> params = new HashMap<String, String>();
            params.put("Account", iname);
            params.put("UserId", UserId);
            try {
                return sendGETRequest(path, params, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return MESSAGE_INTERNET;
        }

        public static String sendGETRequest(String path, Map<String, String> params, String encode)
                throws IOException {
            String jsonInstallGrab = HttpUtils.sendGETRequest(path, params, encode);
            if ("".equals(jsonInstallGrab)) {
                return MESSAGE_INTERNET;
            } else if (jsonInstallGrab == null) {
                return null;
            } else if ("404".equals(jsonInstallGrab) || "500".equals(jsonInstallGrab)) {
                return jsonInstallGrab;
            } else {
                String[] res = jsonInstallGrab.split(":");
                if (res[0].contains("result") && res[1].contains("0")) {
                    return MESSAGE_ERROR;
                } else {
                    return jsonInstallGrab;
                }
            }

        }
    }


    /**
     * 新接口安装调试单单抢单
     */
    public static class NewInstallGrab {
        public static String check(String name, String code) throws NoSuchAlgorithmException {
            String path = "http://60.191.59.10:19000/WebApi/api_installorders.aspx?FLAG=RobOrder";
            Map<String, String> params = new HashMap<String, String>();
            params.put("UserName", name);
            params.put("OrderId", code);
            try {
                return sendGETRequest(path, params, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return MESSAGE_INTERNET;
        }

        public static String sendGETRequest(String path, Map<String, String> params, String encode)
                throws IOException {
            String jsonGrab = HttpUtils.sendGETRequest(path, params, encode);
            if ("".equals(jsonGrab)) {
                return MESSAGE_INTERNET;
            } else if (jsonGrab == null) {
                return null;
            } else if ("404".equals(jsonGrab) || "500".equals(jsonGrab)) {
                return jsonGrab;
            } else {
                String[] res = jsonGrab.split(":");
                if (res[0].contains("result") && res[1].contains("1")) {
                    return MESSAGE_OK;
                } else if (res[0].contains("result") && res[1].contains("0")) {
                    return MESSAGE_ERROR;
                } else {
                    return null;
                }
            }
        }
    }


    /**
     * 新接口安装调试单完成
     */
    public static class RequestInstallComplete {
        public static String check(String name, String code, String remark) throws NoSuchAlgorithmException {
            String path = "http://60.191.59.10:19000/webapi/api_installorder.aspx?FLAG=Complete";
            Map<String, String> params = new HashMap<String, String>();
            params.put("Code", code);
            params.put("Name", name);
            params.put("Remark", remark);
            try {
                return sendGETRequest(path, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String sendGETRequest(String path, Map<String, String> params)
                throws IOException {
            String jsonComplete = HttpUtils.sendGETRequest(path, params, "UTF-8");
            if ("".equals(jsonComplete)) {
                return MESSAGE_INTERNET;
            } else if (jsonComplete == null) {
                return null;
            } else if ("404".equals(jsonComplete) || "500".equals(jsonComplete)) {
                return jsonComplete;
            } else {
                String[] res = jsonComplete.split(":");
                if (res[1].contains("1")) {
                    return MESSAGE_OK;
                } else {
                    return MESSAGE_ERROR;
                }
            }
        }
    }


    /**
     * 新接口历史安装记录
     */
    public static class NewPersonInstallRecord {
        public static String check(String iname,
                                   String userId,
                                   String firstDay,
                                   String lastDay) throws NoSuchAlgorithmException {
            String path = "http://60.191.59.10:19000/WebApi/api_installorders.aspx?FLAG=OldOrder";
            Map<String, String> params = new HashMap<String, String>();
            params.put("Account", iname);
            params.put("UserId", userId);
            params.put("FirstDay", firstDay);
            params.put("LastDay", lastDay);
            try {
                return sendGETRequest(path, params);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static String sendGETRequest(String path, Map<String, String> params)
                throws IOException {
            String jsonPersonRepairRecord = HttpUtils.sendGETRequest(path, params, "UTF-8");
            if ("".equals(jsonPersonRepairRecord)) {
                return MESSAGE_INTERNET;
            } else if (jsonPersonRepairRecord == null) {
                return null;
            } else if ("404".equals(jsonPersonRepairRecord) || "500".equals(jsonPersonRepairRecord)) {
                return jsonPersonRepairRecord;
            } else {
                String[] res = jsonPersonRepairRecord.split(":");
                if (res[1].contains("0")) {
                    return MESSAGE_ERROR;//2不存在
                } else {
                    return jsonPersonRepairRecord;
                }
            }
        }
    }
}
