package com.mapsoft.aftersale.aftersale.mainfragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapsoft.aftersale.R;
import com.mapsoft.aftersale.aftersale.mainfragment.orderfragment.RepairingFragment;
import com.mapsoft.aftersale.aftersale.mainfragment.orderfragment.CompleteOverFragment;
import com.mapsoft.aftersale.aftersale.mainfragment.orderfragment.TemporaryActivity;


/**
 * Created by Administrator on 2017/11/24.
 * 订单界面
 */

public class OrderFragment extends Fragment implements View.OnClickListener {
    private TextView tv_Repairing, tv_CompleteOver;
    private LinearLayout ll_repairing, ll_completeOver;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private RepairingFragment repairingFragment;
    private CompleteOverFragment completeOverFragment;
    private MainAfterSaleFragmentActivity mContext;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainAfterSaleFragmentActivity) {
            this.mContext = (MainAfterSaleFragmentActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fm = getFragmentManager();
        View view = inflater.inflate(R.layout.orderfragment, container, false);

        tv_Repairing = (TextView) view.findViewById(R.id.tv_Repairing);
        tv_CompleteOver = (TextView) view.findViewById(R.id.tv_CompleteOver);
        ll_repairing = (LinearLayout) view.findViewById(R.id.ll_repairing);
        ll_repairing.setOnClickListener(this);
        ll_completeOver = (LinearLayout) view.findViewById(R.id.ll_completeOver);
        ll_completeOver.setOnClickListener(this);
        ImageView iv_create = (ImageView) view.findViewById(R.id.iv_create);//创建订单按钮
        iv_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(getActivity(), TemporaryActivity.class));
            }
        });
        //设置默认界面
        setDefaultOrderFragment();
        return view;
    }

    private void setDefaultOrderFragment() {
        fm.beginTransaction()
                .replace(R.id.fl_orderList, repairingFragment == null ? new RepairingFragment() : repairingFragment)
                .commit();
        tv_Repairing.setBackground(ActivityCompat.getDrawable(getActivity(), R.drawable.head_tab_1));
//        RdApplication.get().setCurrFragmentName(getClass().getSimpleName());
    }

    @Override
    public void onClick(View v) {
        ft = fm.beginTransaction();
        switch (v.getId()) {
            case R.id.ll_repairing:
                if (repairingFragment == null) {
                    repairingFragment = new RepairingFragment();
                }
                tv_CompleteOver.setBackground(ActivityCompat.getDrawable(getActivity(), R.drawable.head_tab_2));
                tv_Repairing.setBackground(ActivityCompat.getDrawable(getActivity(), R.drawable.head_tab_1));
                ft.replace(R.id.fl_orderList, repairingFragment);
                break;
            case R.id.ll_completeOver:
                if (completeOverFragment == null) {
                    completeOverFragment = new CompleteOverFragment();

                }
                tv_CompleteOver.setBackground(ActivityCompat.getDrawable(getActivity(), R.drawable.head_tab_1));
                tv_Repairing.setBackground(ActivityCompat.getDrawable(getActivity(), R.drawable.head_tab_2));
                ft.replace(R.id.fl_orderList, completeOverFragment);
                break;
            default:
                break;
        }
        ft.commit();
    }
}
