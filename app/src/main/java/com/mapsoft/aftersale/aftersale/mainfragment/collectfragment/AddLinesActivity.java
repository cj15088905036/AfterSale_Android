package com.mapsoft.aftersale.aftersale.mainfragment.collectfragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.Resources.NotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnKeyListener;
import android.view.inputmethod.CorrectionInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapsoft.aftersale.R;
import com.mapsoft.aftersale.utils.CustomDialog;
import com.mapsoft.aftersale.utils.EditDialog;
import com.mapsoft.aftersale.utils.MyFileReader;
import com.mapsoft.aftersale.utils.MyToast;
import com.mapsoft.aftersale.utils.MyTools;

public class AddLinesActivity extends Activity {

    private EditText msgText;
    private TextView updowntView, remindView;
    private ListView listView;
    private ArrayList<CoordInfo> upcoordInfos = new ArrayList<>();
    private ArrayList<CoordInfo> downcoordInfos = new ArrayList<>();
    private ArrayList<CoordInfo> currcoordInfos = upcoordInfos;
    private File file;
    private ListViewAdapter listViewAdapter = new ListViewAdapter(this);
    private View lastconvertView = null;//最后一次点击的列 用于切换点击列时改变背景颜色
    private boolean isDownTrip = false; // false为上行，true为下行
    private int currentindex;
    private List<CoordInfo> tem_coords = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linedetails);
        initView();
        if (getIntent().getStringExtra("linename") != null) {
            ((TextView) findViewById(R.id.title)).setText("编辑站点(" + getIntent().getStringExtra("linename") + ")");
            initLineFile(getIntent().getStringExtra("linename"));
            currentindex = currcoordInfos.size();//设置当前插入位置 即最大索引
        }
    }

    /**
     * 初始化试图
     */
    private void initView() {
        updowntView = (TextView) findViewById(R.id.updown);
        remindView = (TextView) findViewById(R.id.remind);
        msgText = (EditText) findViewById(R.id.msg_rec);

        //设置搜索输入框回车键的响应事件
        msgText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                    btn_click(findViewById(R.id.add));
                    return true;
                }
                return false;
            }
        });

        setRemindInfo(null);

        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                currentindex = (currcoordInfos.size() - arg2);
                setRemindInfo(arg1);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                Toast.makeText(AddLinesActivity.this, currcoordInfos.get(position).stationname, Toast.LENGTH_SHORT).show();
                final View update_view = getLayoutInflater().inflate(R.layout.remark_edit, null);
                final EditText et_remark = (EditText) update_view.findViewById(R.id.et_remark);
                et_remark.setText(currcoordInfos.get(position).stationname);
                et_remark.setSelection(currcoordInfos.get(position).stationname.length());
                new EditDialog.Builder(AddLinesActivity.this)
                        .setTitle("修改站点名称")
                        .setContentView(update_view)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                String station_name = ;
                                currcoordInfos.get(position).stationname = et_remark.getText().toString().trim();
                                Toast.makeText(AddLinesActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                updateFile();//修改站点名称
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                return true;
            }
        });
    }

    /**
     * 根据线路名执行初始化
     */
    private void initLineFile(String linename) {
        file = new File(Environment.getExternalStorageDirectory() +
                "/" + getResources().getString(R.string.dirsource) + "/" + linename + ".txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                MyFileReader fr = new MyFileReader(
                        Environment.getExternalStorageDirectory()
                                + "/" + getResources().getString(R.string.dirsource) + "/" + linename + ".txt",
                        MyTools.GetEncoding(Environment.getExternalStorageDirectory()
                                + "/" + getResources().getString(R.string.dirsource) + "/" + linename + ".txt"));
                //可以换成工程目录下的其他文本文件
                BufferedReader br = new BufferedReader(fr);
                String s = "";
                while ((s = br.readLine()) != null) {
                    if (s.trim().contains("上行")) {
                        currcoordInfos = upcoordInfos;
                        while ((s = br.readLine()) != null) {
                            if (s.trim().equals("")) {
                                break;
                            }
                            Log.e("上行线路,", s.toString());
                            //读取站点
                            readStation(s);
                        }
                        //站点倒序显示
                        if (currcoordInfos != null)
                            Collections.reverse(currcoordInfos);
                    } else if (s.trim().contains("下行")) {
                        currcoordInfos = downcoordInfos;
                        while ((s = br.readLine()) != null) {
                            if (s.trim().equals("")) {
                                break;
                            }
                            Log.e("下行线路,", s.toString());
                            //读取站点
                            readStation(s);
                        }
                        //站点倒序显示
                        if (currcoordInfos != null)
                            Collections.reverse(currcoordInfos);
                    } else if (s.trim().equals("")) {
                        return;
                    }
                }
                br.close();
                fr.close();
                currcoordInfos = upcoordInfos;
                updowntView.setText("上行" + currcoordInfos.size() + "个");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (NotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int index;

    /**
     * 读取站点
     *
     * @param s
     */
    private void readStation(String s) {
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
        //表示有时间长度为5 没有为4
        else if (s.split(" ").length == 5 || s.split(" ").length == 4) {
            String[] strings = s.split(" ");
            if (strings[0].contains("进站")) {
                coordInfo = new CoordInfo();
                coordInfo.index = index;
                coordInfo.inlat = Integer.parseInt(strings[1]);
                coordInfo.inlon = Integer.parseInt(strings[2]);
                coordInfo.inangle = Integer.parseInt(strings[3]);
                coordInfo.stationname = strings[0].replace("进站", "");
                if (strings.length == 5) coordInfo.inloctime = strings[4];
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


    /**
     * 设置提醒信息 显示下一次插入的序号
     */
    private void setRemindInfo(View view) {
        remindView.setVisibility(View.VISIBLE);
        remindView.setText("点击站点改变插入位置(下次插入:" + (currentindex + 1) + ")");
        if (view != null) {
            if (lastconvertView != null) {
                lastconvertView.setBackgroundColor(Color.WHITE);//修改上次点击行的背景颜色为白色
                lastconvertView = null;
            }
            view.setBackgroundColor(Color.YELLOW);
            lastconvertView = view;//重新设置最后一次点击事件
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

    public void btn_click(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.changed:
                if (isDownTrip) { //当前为true为下行，切换成上行
                    isDownTrip = false;
                    currcoordInfos = upcoordInfos;
                    updowntView.setText("上行" + upcoordInfos.size() + "个");
                } else {//当前为false为上行，切换成下行
                    isDownTrip = true;
                    currcoordInfos = downcoordInfos;
                    updowntView.setText("下行" + downcoordInfos.size() + "个");
                }
                currentindex = 0;//每次添加后索引加1
                setRemindInfo(null);//重新设置提醒信息
                listViewAdapter.notifyDataSetChanged();
                break;
            case R.id.add:
                if ("".equals(msgText.getText().toString().trim())) {
                    MyToast.popToast(AddLinesActivity.this, "输入的站点名为空");
                } else {
                    CoordInfo coordInfo = new CoordInfo();
                    coordInfo.stationname = msgText.getText().toString().trim();
                    currcoordInfos.add(currcoordInfos.size() - currentindex, coordInfo);//1234
//                    123
                    listViewAdapter.notifyDataSetChanged();
                    msgText.setText("");
                    if (isDownTrip) {//true为下行
                        updowntView.setText("下行" + currcoordInfos.size() + "个");
                    } else {//false为上行
                        updowntView.setText("上行" + currcoordInfos.size() + "个");
                    }
                    currentindex++;//每次添加后索引加1
                    setRemindInfo(listView.getChildAt(currentindex));//重新设置提醒信息
                    // setRemindInfo(listView.getChildAt(currentindex));//重新设置提醒信息
                    updateFile();//自动保存
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


    /**
     * 更新文件坐标
     */
    protected void updateFile() {
        if (currcoordInfos.size() >= 0) {
            try {
                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fileWriter);
                bw.write("上行    站点名称        纬度          经度      角度   定位时间 ");
                bw.write("\r\n");
                for (int i = upcoordInfos.size() - 1; i >= 0; i--) {
                    CoordInfo coordInfo = upcoordInfos.get(i);
                    bw.write(coordInfo.stationname + "进站 " + coordInfo.inlat + " " + coordInfo.inlon + " " + coordInfo.inangle + " " + coordInfo.inloctime);
                    bw.write("\r\n");
                    bw.write(coordInfo.stationname + "出站 " + coordInfo.outlat + " " + coordInfo.outlon + " " + coordInfo.outangle + " " + coordInfo.outloctime);
                    bw.write("\r\n");
                }
                bw.write("\r\n");
                bw.write("下行     站点名称        纬度          经度      角度   定位时间 ");
                bw.write("\r\n");
                for (int i = downcoordInfos.size() - 1; i >= 0; i--) {
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
                e.printStackTrace();
            }
        }
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
            LinearLayout ly;
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            CoordInfo coordInfo = getItem(position);
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = activity.getLayoutInflater().inflate(R.layout.list_item_line, null);
                holder.textView = (TextView) convertView.findViewById(R.id.line);
                holder.ly = (LinearLayout) convertView.findViewById(R.id.ly);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText((currcoordInfos.size() + -position) + "  " + coordInfo.stationname);
            holder.ly.setTag(coordInfo);
            if (currcoordInfos.size() - position == currentindex) {
                if (lastconvertView != null) {
                    lastconvertView.setBackgroundColor(Color.WHITE);
                    lastconvertView = null;
                }
                convertView.setBackgroundColor(Color.YELLOW);
                lastconvertView = convertView;
            } else {
                convertView.setBackgroundColor(Color.WHITE);
            }
            holder.ly.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    new CustomDialog.Builder(AddLinesActivity.this)
                            .setTitle("提示")
                            .setMessage("确认删除?删除后不可恢复")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    currcoordInfos.remove(position);
                                    if (position < currentindex) {
                                        currentindex--;
                                    } else if (position == currentindex) {
                                        currentindex = 0;
                                    }
                                    remindView.setText("点击站点改变插入位置(下次插入:" + (currentindex + 1) + ")");
                                    listViewAdapter.notifyDataSetChanged();
                                    updateFile();//删除站点
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                }
            });
            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
