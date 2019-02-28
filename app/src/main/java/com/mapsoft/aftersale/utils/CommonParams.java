package com.mapsoft.aftersale.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * 共用参数
 */
public class CommonParams {
    public static String ANDROID_VERSION = "android_version";
    public static String ANDROID_URL = "android_url";
    public static String DOWN_APK = "图软掌上运维.apk";
    public static String DESC = "下载完成后,点击安装";

    /**
     * 获取应用版本号
     *
     * @return
     */
    public static String getVersionName(Context context) {
        PackageManager manager = context.getPackageManager();
        String versionName = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getRealFilePath(Context context, Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;//   /data/hw_init/version/region_comm/china/media/Pre-loaded/Pictures/01-02.jpg
    }

    /**
     * 获取版本与下载地址
     *
     * @param xml
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static String getVersion(InputStream xml) throws XmlPullParserException, IOException {
        String version = null;
        XmlPullParser pullParser = Xml.newPullParser();
        pullParser.setInput(xml, "utf-8");
        int event = pullParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_TAG:
                    if (pullParser.getName().equals("root")) {
                        version = pullParser.getAttributeValue(null, ANDROID_VERSION)
                                + "," + pullParser.getAttributeValue(null, ANDROID_URL)
                        ;
                    }
                    break;
            }
            event = pullParser.next();
        }
        return version;
    }
}
