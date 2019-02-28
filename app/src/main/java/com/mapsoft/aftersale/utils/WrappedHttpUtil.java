package com.mapsoft.aftersale.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.mapsoft.aftersale.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/22.
 */

public class WrappedHttpUtil {

    public static class Builder {
        private Context context;
        private static String ENCODE = "utf-8";
        private String apiUrl;//api_maintorder.aspx?
        private String methodUrl;//FLAG=Login;
        private Map<String, String> args;//一般是装载在map中传过来

        Builder setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
            return this;
        }


        Builder setMethodUrl(String methodUrl) {
            this.methodUrl = methodUrl;
            return this;
        }


        Builder setArgs(Map<String, String> args) {
            this.args = args;
            return this;
        }


        public Builder(Context context) {
            this.context = context;
        }

        public Builder setL(Listener l) {
            this.l = l;
            return this;
        }

        private Listener l;


        public interface Listener {
            void onSuccess(int responseCode, String result);

            void onFail(int responseCode, String result);
        }

        public void execute() {
            int responseCode = 0;
            StringBuffer buffer = null;
            HttpURLConnection httpUrlConn = null;
            try {
                StringBuilder url = new StringBuilder(context.getResources().getString(R.string.interface_address) + apiUrl + methodUrl);
                url.append("&");
                for (Map.Entry<String, String> entry : args.entrySet()) {
                    url.append(entry.getKey()).append("=");
                    url.append(URLEncoder.encode(entry.getValue(), ENCODE));
                    url.append("&");
                }
                url.deleteCharAt(url.length() - 1);
                buffer = new StringBuffer();
                URL newUrl = new URL(url.toString());
                httpUrlConn = (HttpURLConnection) newUrl.openConnection();
                httpUrlConn.setDoOutput(false);
                httpUrlConn.setDoInput(true);
                httpUrlConn.setUseCaches(false);
                httpUrlConn.setRequestMethod("GET");
                Log.e("访问URL", url.toString());
                httpUrlConn.connect();
                responseCode = httpUrlConn.getResponseCode();
                if (responseCode == 200) {
                    // 将返回的输入流转换成字符串
                    InputStream inputStream = httpUrlConn.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, ENCODE);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String str = null;
                    while ((str = bufferedReader.readLine()) != null) {
                        buffer.append(str);
                    }
                    inputStream.close();
                    inputStreamReader.close();
                    bufferedReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 关闭连接
                httpUrlConn.disconnect();
            }

            if (!TextUtils.isEmpty(buffer.toString()) && l != null) {
                l.onSuccess(responseCode, buffer.toString());
            } else {
                if (l != null)
                    l.onFail(responseCode, "123");
            }
        }

        /**
         * 登录/维修单  接口请求
         *
         * @param method
         * @param args
         */
        public Builder dataRepairRequest(String method, Map<String, String> args) {
            setApiUrl(context.getResources().getString(R.string.interface_name));
            setMethodUrl("FLAG=" + method);
            setArgs(args);
            return this;
        }

        /**
         * 安装调试单  接口请求
         *
         * @param method
         * @param args
         */
        public Builder dataInstallRequest(String method, Map<String, String> args) {
            setApiUrl(context.getResources().getString(R.string.interface_install_name));
            setMethodUrl("FLAG=" + method);
            setArgs(args);
            return this;
        }

        /**
         * 客户信息  接口请求
         *
         * @param method
         */
        public Builder dataSeekCustomer(String method, Map<String, String> args) {
            setApiUrl(context.getResources().getString(R.string.interface_seek_customer));
            setMethodUrl("FLAG=" + method);
            setArgs(args);
            return this;
        }

    }

    /**
     * 密码加密
     *
     * @param content
     * @return
     */
    public static String md5(String content) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString().substring(8, 24).toUpperCase();
    }


}

