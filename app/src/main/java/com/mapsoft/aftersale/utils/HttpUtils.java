package com.mapsoft.aftersale.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/29.
 * http请求工具类
 */

public class HttpUtils {
    /**
     * 获取连接，返回json对象
     *
     * @param path   地址
     * @param params 参数
     * @param encode 编码
     * @return 返回值:json
     * @throws MalformedURLException
     * @throws IOException
     */
    public static String sendGETRequest(String path, Map<String, String> params, String encode)
            throws IOException {
        StringBuffer buffer = null;
        HttpURLConnection httpUrlConn = null;
        try {
            StringBuilder url = new StringBuilder(path);
            url.append("&");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                url.append(entry.getKey()).append("=");
                url.append(URLEncoder.encode(entry.getValue(), encode));
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
            int responseCode = httpUrlConn.getResponseCode();
            if (responseCode == 200) {
                // 将返回的输入流转换成字符串
                InputStream inputStream = httpUrlConn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, encode);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String str = null;
                while ((str = bufferedReader.readLine()) != null) {
                    buffer.append(str);
                }
                inputStream.close();
                inputStreamReader.close();
                bufferedReader.close();
                return buffer.toString();
            } else if (responseCode == 500) {
                return "500";
            } else if (responseCode == 404) {
                return "404";
            } else {
                return null;
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接
            httpUrlConn.disconnect();
        }
        return "";
    }
}


