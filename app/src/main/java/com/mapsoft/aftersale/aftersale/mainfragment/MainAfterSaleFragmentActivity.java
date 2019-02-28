package com.mapsoft.aftersale.aftersale.mainfragment;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.mapsoft.aftersale.R;
import com.mapsoft.aftersale.RdApplication;
import com.mapsoft.aftersale.aftersale.mainfragment.collectfragment.SetCoordsFragment;
import com.mapsoft.aftersale.bean.BusProductTable;
import com.mapsoft.aftersale.bean.Ma_product_table;
import com.mapsoft.aftersale.bean.User;
import com.mapsoft.aftersale.utils.MyToast;
import com.mapsoft.aftersale.utils.NoSlideViewPager;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/27.
 * 主界面--
 */


public class MainAfterSaleFragmentActivity extends FragmentActivity implements View.OnClickListener {
    private SQLiteDatabase database;
    private List<Fragment> fragments = new ArrayList<>();
    private NoSlideViewPager viewPager;
    private long exitTime;

    private List<LinearLayout> ll_lists = new ArrayList<>();
    private LinearLayout
            ll_order,
            ll_grab,
            ll_collect,
            ll_person;
    private User user;

    public User getUser() {
        return user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.e("测试bug",fragments.get(0).toString());
        super.onCreate(savedInstanceState);
        database = LitePal.getDatabase();
        new Thread(new SQLThread()).start();
        RdApplication.get().setDatabase(database);
        user = RdApplication.get().getUser();
        setContentView(R.layout.main_fragment);
        initView();
        fragments.add(new OrderFragment());
        fragments.add(new MessageFragment());
        fragments.add(new SetCoordsFragment());
        fragments.add(new PersonFragment());
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
        viewPager.setCurrentItem(3);
        viewPager.setOffscreenPageLimit(3);
        changeView(false, false, false, true);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                boolean[] state = new boolean[ll_lists.size()];
                state[position] = true;
                changeView(state[0], state[1], state[2], state[3]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 根据当前展示的页面改变底部按钮的颜色
     *
     * @param b  订单
     * @param b1 抢单
     * @param b2 采点
     * @param b3 个人
     */
    private void changeView(boolean b, boolean b1, boolean b2, boolean b3) {
        ll_order.setSelected(b);
        ll_grab.setSelected(b1);
        ll_collect.setSelected(b2);
        ll_person.setSelected(b3);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        viewPager = (NoSlideViewPager) findViewById(R.id.main_fragment);
        ll_order = (LinearLayout) findViewById(R.id.ll_order);
        ll_order.setOnClickListener(this);
        ll_lists.add(ll_order);
        ll_grab = (LinearLayout) findViewById(R.id.ll_grab);
        ll_grab.setOnClickListener(this);
        ll_lists.add(ll_grab);
        ll_collect = (LinearLayout) findViewById(R.id.ll_collect);
        ll_collect.setOnClickListener(this);
        ll_lists.add(ll_collect);
        ll_person = (LinearLayout) findViewById(R.id.ll_person);
        ll_person.setOnClickListener(this);
        ll_lists.add(ll_person);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showOnUI("再按一次将退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finishAffinity();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void showOnUI(String message) {
        MyToast.popToast(this, message);
    }

    public void showThreadToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.popToast(MainAfterSaleFragmentActivity.this, message);
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_order:
                viewPager.setCurrentItem(0);
                changeView(true, false, false, false);
                break;
            case R.id.ll_grab:
                viewPager.setCurrentItem(1);
                changeView(false, true, false, false);
                break;
            case R.id.ll_collect:
                viewPager.setCurrentItem(2);
                changeView(false, false, true, false);
                break;
            case R.id.ll_person:
                viewPager.setCurrentItem(3);
                changeView(false, false, false, true);
                break;
            default:
                break;
        }

    }

    private class SQLThread implements Runnable {
        @Override
        public void run() {

            Cursor cursor = database.rawQuery("select name from sqlite_master where type='table'", null);
            String name = "";
            while (cursor.moveToNext()) {
                //遍历出表名
                name = cursor.getString(0);
                Log.e("表名:", name);
                if ("ma_product_table".equals(name)) {
                    //将原先ma_product_table表里面的内容取出来，存到新表 BusProductTable 中
                    Cursor bySQL = DataSupport.findBySQL("select * from ma_product_table");
                    if (bySQL.getCount() > 0) {
                        while (bySQL.moveToNext()) {
                            BusProductTable productTable = new BusProductTable();
                            productTable.setRepair_code(bySQL.getString(bySQL.getColumnIndex("repair_code")));
                            productTable.setRepair_name(bySQL.getString(bySQL.getColumnIndex("repair_name")));
                            productTable.setType_code("W" + bySQL.getString(bySQL.getColumnIndex("order_code")));
                            productTable.setProduct_code(bySQL.getString(bySQL.getColumnIndex("product_code")));
                            productTable.setProduct_name(bySQL.getString(bySQL.getColumnIndex("product_name")));
                            productTable.setVeh_code(bySQL.getString(bySQL.getColumnIndex("veh_code")));
                            productTable.setFault_cause(bySQL.getString(bySQL.getColumnIndex("fault_cause")));
                            productTable.setMaint_result(bySQL.getString(bySQL.getColumnIndex("maint_result")));
                            productTable.setMaterial_cost(bySQL.getString(bySQL.getColumnIndex("material_cost")));
                            productTable.setMaintenance_cost(bySQL.getString(bySQL.getColumnIndex("maintenance_cost")));
                            productTable.setInput_time(bySQL.getString(bySQL.getColumnIndex("input_time")));
                            productTable.setProduct_state(bySQL.getString(bySQL.getColumnIndex("product_state")));
                            boolean save = productTable.save();
                            if (save) {
//                                showThreadToast("数据库更新成功\r\n不必理会");
                                //删除信息表
                                database.execSQL("DROP TABLE IF EXISTS ma_message_table;");
                            } else {
                                showThreadToast("数据库更新失败\r\n请联系技术人员");
                            }
                        }
                        int deleteAll = DataSupport.deleteAll(Ma_product_table.class);
                        Log.e("删除数据？", deleteAll + "");
                    }
                }
            }
        }
    }
}
