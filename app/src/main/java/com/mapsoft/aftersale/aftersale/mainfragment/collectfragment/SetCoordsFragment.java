package com.mapsoft.aftersale.aftersale.mainfragment.collectfragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources.NotFoundException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapsoft.aftersale.R;
import com.mapsoft.aftersale.aftersale.mainfragment.MainAfterSaleFragmentActivity;
import com.mapsoft.aftersale.utils.CustomDialog;
import com.mapsoft.aftersale.utils.EditDialog;
import com.mapsoft.aftersale.utils.FavorDialog;
import com.mapsoft.aftersale.utils.MyAuthenticator;
import com.mapsoft.aftersale.utils.MyFileReader;
import com.mapsoft.aftersale.utils.MyTools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import static android.content.Context.MODE_PRIVATE;
import static com.mapsoft.aftersale.R.string.dirsource;


public class SetCoordsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SetCoordsFragment";

    TextView coordtView, directtView, infotView, addinfotView, updowntView, titletView;
    int orientation = 1000;
    int coefficient = 36000 * 100; //系数 正式结果要乘36000*100
    //	int coefficient=1;
    Date lastdate = new Date();
    Location lastLocation, angleLocation;
    String stationname;

    ListView listView;
    ArrayList<CoordInfo> upcoordInfos = new ArrayList<SetCoordsFragment.CoordInfo>();
    ArrayList<CoordInfo> downcoordInfos = new ArrayList<SetCoordsFragment.CoordInfo>();
    ArrayList<CoordInfo> currcoordInfos = upcoordInfos;
    ListViewAdapter listViewAdapter;

    SimpleDateFormat myformatter = new SimpleDateFormat("HH:mm:ss");
    File coorfile;
    ImageView progressSmall;
    Animation animation;
    boolean flag = false; // 上下行标识false为上行，ture为下行
    SharedPreferences sp;
    private MainAfterSaleFragmentActivity activity;
    private View view;

    private Button
            changed;

    private LinearLayout mail, dir, edit;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach: ");
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.setcoords, container, false);
        Log.e(TAG, "onCreateView");
//        RdApplication.get().setCurrFragmentName(getClass().getSimpleName());
        activity = (MainAfterSaleFragmentActivity) getActivity();
        sp = activity.getSharedPreferences(activity.getPackageName(), MODE_PRIVATE);
        listView = (ListView) view.findViewById(R.id.listView);
        progressSmall = (ImageView) view.findViewById(R.id.progress);
        animation = AnimationUtils.loadAnimation(activity, R.anim.rotate);
        animation.setInterpolator(new LinearInterpolator());
        coordtView = (TextView) view.findViewById(R.id.coord);
        directtView = (TextView) view.findViewById(R.id.direct);
        infotView = (TextView) view.findViewById(R.id.info);
        updowntView = (TextView) view.findViewById(R.id.updown);
        titletView = (TextView) view.findViewById(R.id.title);
        listViewAdapter = new ListViewAdapter(activity);
        listView.setAdapter(listViewAdapter);

        mail = (LinearLayout) view.findViewById(R.id.mail);
        mail.setOnClickListener(this);
        dir = (LinearLayout) view.findViewById(R.id.dir);
        dir.setOnClickListener(this);
        edit = (LinearLayout) view.findViewById(R.id.edit);
        edit.setOnClickListener(this);
        changed = (Button) view.findViewById(R.id.changed);
        changed.setOnClickListener(this);
        // 检查权限
//        checkPermission();

        initLocation();//GPS定位
        return view;
    }

    @Override
    public void onActivityCreated( Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "onActivityCreated: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "销毁");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach: ");
    }


    private void getFileList() {

        File dirsource = new File(Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.dirsource));

        if (!dirsource.exists()) {
            dirsource.mkdirs();
        }

        ArrayList<String> filenames = new ArrayList<String>();
        if (dirsource.listFiles() != null) {
            for (File file : dirsource.listFiles()) {
                filenames.add(file.getName().replace(".txt", ""));
            }
        }
        final FavorDialog.Builder builder = new FavorDialog.Builder(activity);
        final File finalDirsource = dirsource;
        builder.setTitle("线路列表")
                .setFavorStrings(filenames)
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        upcoordInfos.clear();
                        downcoordInfos.clear();
                        stationname = builder.getFileName().replace(getResources().getString(R.string.mk), "");
                        titletView.setText(stationname);
                        getMessage(stationname);
                    }
                }).setPositiveButton("新增线路", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                final EditDialog.Builder builder = new EditDialog.Builder(activity);
                builder.setTitle("输入线路名").setMessage("")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String filename = builder.getMessage();
                                if (!filename.equals("")) {
                                    Intent intent = new Intent(activity, AddLinesActivity.class);
                                    intent.putExtra("linename", filename);
                                    startActivity(intent);
//                                    finish();//原程序
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(activity, "线路名不能为空", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                updateListView(finalDirsource);//新增线路——取消
                            }
                        }).create().show();
            }
        }).setNegativeButton("取消", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                updateListView(finalDirsource);//线路列表——取消
            }
        });
        builder.create().show();
    }

    /**
     * 当线路文件被删除时，更新视图
     *
     * @param finalDirsource 线路文件
     */
    public void updateListView(File finalDirsource) {
        upcoordInfos.clear();
        downcoordInfos.clear();
        listViewAdapter.notifyDataSetChanged();
        String fileName = titletView.getText().toString().trim();
        ArrayList<String> filenames = new ArrayList<String>();
        if (finalDirsource.listFiles() != null) {
            for (File file : finalDirsource.listFiles()) {
                filenames.add(file.getName().replace(".txt", ""));
            }
        }
        if (filenames.size() == 0) {
            titletView.setText("坐标采集");
            currcoordInfos = upcoordInfos;
            listViewAdapter.notifyDataSetChanged();
            updowntView.setText("(上行" + currcoordInfos.size() + "个)");
        } else {
            for (String name : filenames) {
                if (name.equals(fileName)) {
                    getMessage(name);
                } else {
                    titletView.setText("坐标采集");
                    currcoordInfos = upcoordInfos;
                    listViewAdapter.notifyDataSetChanged();
                    updowntView.setText("(上行" + currcoordInfos.size() + "个)");
                }
            }
        }
    }

    /**
     * 将线路文件里的站点，添加到适配器中，更新视图
     *
     * @param filename 文件名称
     */
    protected void getMessage(String filename) {
        MyFileReader fr;
        try {
            String fileName = Environment.getExternalStorageDirectory() + "/" + getResources().getString(dirsource)
                    + "/" + filename + ".txt";
            String encode = MyTools.GetEncoding(fileName);
            fr = new MyFileReader(fileName, encode);
            //可以换成工程目录下的其他文本文件
            BufferedReader br = new BufferedReader(fr);
            String s = "";
            int index = 0;//
            while ((s = br.readLine()) != null) {
                if (s.trim().contains("上行")) {
                    currcoordInfos = upcoordInfos;
                    index = 0;
                } else if (s.trim().contains("下行")) {
                    currcoordInfos = downcoordInfos;
                    index = 0;
                } else if (!s.trim().equals("")) {
                    CoordInfo coordInfo;
                    //多个空格替换成一个
                    String regEx = "['   ']+";   //一个或多个空格
                    Pattern p = Pattern.compile(regEx);
                    Matcher m = p.matcher(s.trim());
                    s = m.replaceAll(" ");
                    //表示纯站点
                    if (s.split(" ").length == 1) {
                        coordInfo = new CoordInfo();
                        coordInfo.stationname = s;
                        currcoordInfos.add(coordInfo);
                    }
                    //biaoshi有时间长度为5 没有为4
                    else if (s.split(" ").length == 5 || s.split(" ").length == 4) {
                        String[] strings = s.split(" ");
                        if (strings[0].contains("进站")) {
                            coordInfo = new CoordInfo();
                            coordInfo.index = index;
                            coordInfo.inlat = Integer.parseInt(strings[1]);
                            coordInfo.inlon = Integer.parseInt(strings[2]);
                            coordInfo.inangle = Integer.parseInt(strings[3]);
                            coordInfo.stationname = strings[0].replace("进站", "");
                            if (strings.length == 5)
                                coordInfo.inloctime = strings[4];
                            currcoordInfos.add(coordInfo);
                            index++;
                        } else if (strings[0].contains("出站")) {
                            coordInfo = findCoord(strings[0].replace("出站", ""), index);
                            if (coordInfo != null) {
                                coordInfo.outlat = Integer.parseInt(strings[1]);
                                coordInfo.outlon = Integer.parseInt(strings[2]);
                                coordInfo.outangle = Integer.parseInt(strings[3]);
                                if (strings.length == 5)
                                    coordInfo.outloctime = strings[4];
                            }
                        }
                    }
                }
            }
            br.close();
            fr.close();
            coorfile = new File(Environment.getExternalStorageDirectory() + "/" + getResources().getString(dirsource) + "/" + filename + ".txt");
            if (!coorfile.exists()) {
                try {
                    //按照指定的路径创建文件夹
                    coorfile.createNewFile();
                } catch (Exception e) {
                    Log.i("", "file" + e.toString());
                }
            }
            currcoordInfos = upcoordInfos;
            listViewAdapter.notifyDataSetChanged();
            updowntView.setText("(上行" + currcoordInfos.size() + "个)");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //根据当前站点名查找
    private CoordInfo findCoord(String name, int index) {
        for (CoordInfo coordInfo : currcoordInfos) {
            if (coordInfo.stationname.equals(name) && coordInfo.index == index - 1) {
                return coordInfo;
            }
        }
        return null;
    }

    /**
     * GPS定位初始化
     */
    protected void initLocation() {
        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(activity, "GPS不可用,请打开GPS后刷新界面", Toast.LENGTH_SHORT).show();
        }
        LocationListener locationListener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    ((ImageView) view.findViewById(R.id.progress)).clearAnimation();
                    view.findViewById(R.id.progress).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.tx1).setVisibility(View.INVISIBLE);
                    if (angleLocation == null) {
                        angleLocation = location;
                    } else {
                        MyTools.Result result = MyTools.getAngle(angleLocation.getLongitude(),
                                angleLocation.getLatitude(), location.getLongitude(),
                                location.getLatitude());
                        if (result.distance > 3) {
                            long interval = location.getTime() - angleLocation.getTime();
                            if ((result.distance * 1000) / interval > 3) {
                                orientation = result.angle;
                                directtView.setText("方位角" + "(" + orientation + ")");
                            }
                            angleLocation = location;
                        }
                    }

                    lastLocation = location;
                    double latitude = location.getLatitude();
                    //获取经度信息
                    double longitude = location.getLongitude();

                    String pr = location.getProvider();
                    coordtView.setText(latitude + " " + longitude);
                    long milliseconds = location.getTime();
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                    lastdate = new Date(milliseconds);
                    infotView.setText("定位方式:" + pr + " 时间:" + formatter.format(lastdate));
                }
            }
        };
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        progressSmall.startAnimation(animation);
    }


    /**
     * 更新文件坐标
     */
    protected void updateFile() {
        if (upcoordInfos.size() > 0) {
            try {
                FileWriter fileWriter = new FileWriter(coorfile);
                BufferedWriter bw = new BufferedWriter(fileWriter);
                bw.write("上行");
                bw.write("\r\n");
                bw.write("站点名称        纬度          经度      角度   定位时间 ");
                bw.write("\r\n");
                for (int i = 0; i < upcoordInfos.size(); i++) {
                    CoordInfo coordInfo = upcoordInfos.get(i);
                    bw.write(coordInfo.stationname + "进站 " + coordInfo.inlat + " " + coordInfo.inlon + " " + coordInfo.inangle + " " + coordInfo.inloctime);
                    bw.write("\r\n");
                    bw.write(coordInfo.stationname + "出站 " + coordInfo.outlat + " " + coordInfo.outlon + " " + coordInfo.outangle + " " + coordInfo.outloctime);
                    bw.write("\r\n");
                }

                bw.write("\r\n");
                bw.write("下行");
                bw.write("站点名称        纬度          经度      角度   定位时间 ");
                bw.write("\r\n");
                for (int i = 0; i < downcoordInfos.size(); i++) {
                    CoordInfo coordInfo = downcoordInfos.get(i);
                    bw.write(coordInfo.stationname + "进站 " + coordInfo.inlat + " " + coordInfo.inlon + " " + coordInfo.inangle + " " + coordInfo.inloctime);
                    bw.write("\r\n");
                    bw.write(coordInfo.stationname + "出站 " + coordInfo.outlat + " " + coordInfo.outlon + " " + coordInfo.outangle + " " + coordInfo.outloctime);
                    bw.write("\r\n");
                }
                bw.flush();
                bw.close();
                fileWriter.close();
            } catch (Exception e) {
                Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dir:
                getFileList();
                break;
            case R.id.mail:
                if (coorfile != null) {
                    try {
                        String email = activity.getSharedPreferences("config", 0).getString("mail", "@qq.com");
                        final EditDialog.Builder builder = new EditDialog.Builder(activity);
                        builder.setTitle("接收邮箱地址").setMessage(email)
                                .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String filename = builder.getMessage();
                                        activity.getSharedPreferences("config", 0).edit().putString("mail", filename).apply();
                                        final MyAuthenticator.MailStruct mailStruct = new MyAuthenticator.MailStruct();
                                        mailStruct.str_to_mail = filename;
                                        mailStruct.str_from_mail = "18770044066@163.com";
                                        mailStruct.str_smtp = "smtp.163.com";
                                        mailStruct.str_user = "18770044066@163.com";
                                        mailStruct.str_pass = "guoping**194117";
                                        mailStruct.str_file_path =
                                                Environment.getExternalStorageDirectory()
                                                        + "/" + getResources().getString(dirsource)
                                                        + "/" + stationname + ".txt";

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    MyAuthenticator.send_mail_file(mailStruct);
                                                    handler.sendEmptyMessage(1);
                                                } catch (AddressException e) {
                                                    handler.sendEmptyMessage(2);
                                                    e.printStackTrace();
                                                } catch (UnsupportedEncodingException e) {
                                                    handler.sendEmptyMessage(2);
                                                    e.printStackTrace();
                                                } catch (MessagingException e) {
                                                    handler.sendEmptyMessage(2);
                                                    e.printStackTrace();
                                                }
                                            }
                                        }).start();
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();
                    } catch (Exception e) {
                    }
                } else {
                    Toast.makeText(activity, "请选择线路文件", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.changed:
                if (flag) { // 当前为true下行，切换上行
                    flag = false;
                    currcoordInfos = upcoordInfos;
                    updowntView.setText("(上行" + currcoordInfos.size() + "个)");
                } else {
                    flag = true;
                    currcoordInfos = downcoordInfos;
                    updowntView.setText("(下行" + currcoordInfos.size() + "个)");
                }
                listViewAdapter.notifyDataSetChanged();
                break;
            case R.id.edit://原程序
                if (coorfile != null) {
                    Intent intent = new Intent(activity, AddLinesActivity.class);
                    intent.putExtra("linename", titletView.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(activity, "请选择线路文件", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    public class CoordInfo {
        public int inlat = 0;
        public int inlon = 0;
        public int outlat = 0;
        public int outlon = 0;
        public int inangle;
        public int outangle;
        public String inloctime = "";
        public String outloctime = "";
        public String stationname = "";
        public int index = -1;
    }

    public class ListViewAdapter extends BaseAdapter {
        Activity activity;

        public ListViewAdapter(Activity activity) {
            super();
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return currcoordInfos.size();
        }


        @Override
        public CoordInfo getItem(int position) {
            return currcoordInfos.get(position);
        }


        @Override
        public long getItemId(int position) {
            return 0;
        }

        private class ViewHolder {
            TextView textView;
            LinearLayout inly, outly;
            ImageView inimgView;
            ImageView outimgView;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            LayoutInflater inflater = activity.getLayoutInflater();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_line2, null);
                holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.line);
                holder.inly = (LinearLayout) convertView.findViewById(R.id.inly);
                holder.outly = (LinearLayout) convertView.findViewById(R.id.outly);
                holder.inimgView = (ImageView) convertView.findViewById(R.id.inmarker);
                holder.outimgView = (ImageView) convertView.findViewById(R.id.outmarker);
                holder.inly.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final CoordInfo coordInfo = (CoordInfo) v.getTag();

                        if (coordInfo.inlat == 0) {
                            if (lastLocation != null) {
                                View view = (View) v.getParent();
                                TextView textView = (TextView) view.findViewById(R.id.line);
                                Date mDate = new Date(lastLocation.getTime());
                                coordInfo.inloctime = myformatter.format(mDate);
                                int p = findIndex(currcoordInfos, coordInfo);
                                textView.setText(p + "  " + coordInfo.stationname + "(" + coordInfo.inloctime + ")");
                                coordInfo.inlat = (int) (lastLocation.getLatitude() * coefficient);
                                coordInfo.inlon = (int) (lastLocation.getLongitude() * coefficient);
                                coordInfo.inangle = orientation;
                                ((ImageButton) v.findViewById(R.id.inmarker)).setImageResource(R.drawable.radio_checked);
                                updateFile();
                            } else {
                                Toast.makeText(activity, "未获取到坐标", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            CustomDialog.Builder builder = new CustomDialog.Builder(activity);
                            builder.setTitle("提示").setMessage("确认重置" + coordInfo.stationname + "进站坐标?")
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            coordInfo.inlat = 0;
                                            coordInfo.inlon = 0;
                                            coordInfo.inangle = 0;
                                            coordInfo.inloctime = "";
                                            ((ImageButton) v.findViewById(R.id.inmarker)).setImageResource(R.drawable.radio_unchecked);
                                            dialog.dismiss();
                                            updateFile();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                        }


                    }


                });

                holder.outly.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final CoordInfo coordInfo = (CoordInfo) v.getTag();

                        if (coordInfo.outlat == 0) {
                            if (lastLocation != null) {
                                View view = (View) v.getParent();
                                TextView textView = (TextView) view.findViewById(R.id.line);
                                Date mDate = new Date(lastLocation.getTime());
                                coordInfo.outloctime = myformatter.format(mDate);
                                int p = findIndex(currcoordInfos, coordInfo);
                                textView.setText(p + "  " + coordInfo.stationname + "(" + coordInfo.outloctime + ")");
                                coordInfo.outlat = (int) (lastLocation.getLatitude() * coefficient);
                                coordInfo.outlon = (int) (lastLocation.getLongitude() * coefficient);
                                coordInfo.outangle = orientation;
                                ((ImageButton) v.findViewById(R.id.outmarker)).setImageResource(R.drawable.radio_checked);
                                updateFile();
                            } else {
                                Toast.makeText(activity, "未获取到坐标", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            CustomDialog.Builder builder = new CustomDialog.Builder(activity);
                            builder.setTitle("提示").setMessage("确认重置" + coordInfo.stationname + "出站坐标?")
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            coordInfo.outlat = 0;
                                            coordInfo.outlon = 0;
                                            coordInfo.outangle = 0;
                                            coordInfo.outloctime = "";
                                            ((ImageButton) v.findViewById(R.id.outmarker)).setImageResource(R.drawable.radio_unchecked);
                                            dialog.dismiss();
                                            updateFile();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).create().show();
                        }

                    }
                });
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            CoordInfo coordInfo = getItem(position);
            if (coordInfo.inloctime.equals("")) {
                holder.textView.setText((position + 1) + "  " + coordInfo.stationname);
//                holder.textView.setText((getCount() - position) + "  " + coordInfo.stationname);
            } else {
                holder.textView.setText((position + 1) + "  " + coordInfo.stationname + "(" + coordInfo.inloctime + ")");
//                holder.textView.setText((getCount() - position) + "  " + coordInfo.stationname + "(" + coordInfo.inloctime + ")");
            }
            if (coordInfo.inlat == 0) {
                holder.inimgView.setImageResource(R.drawable.radio_unchecked);
            } else {
                holder.inimgView.setImageResource(R.drawable.radio_checked);
            }
            if (coordInfo.outlat == 0) {
                holder.outimgView.setImageResource(R.drawable.radio_unchecked);
            } else {
                holder.outimgView.setImageResource(R.drawable.radio_checked);
            }
            holder.inly.setTag(coordInfo);
            holder.outly.setTag(coordInfo);
            return convertView;
        }
    }

    //获取元素在数组内的索引
    private int findIndex(ArrayList<CoordInfo> currcoordInfos,
                          CoordInfo coordInfo) {
        for (int i = 0; i < currcoordInfos.size(); i++) {
            if (currcoordInfos.get(i).equals(coordInfo)) {
                return i + 1;
            }
        }
        return 0;
    }

    Handler handler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            if (msg.what == 1) {
                Toast.makeText(activity, "邮件发送成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "发送失败,请重试", Toast.LENGTH_SHORT).show();
            }
            super.dispatchMessage(msg);
        }

    };

}
