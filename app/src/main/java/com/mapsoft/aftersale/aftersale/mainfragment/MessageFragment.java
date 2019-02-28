package com.mapsoft.aftersale.aftersale.mainfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mapsoft.aftersale.R;
import com.mapsoft.aftersale.RdApplication;
import com.mapsoft.aftersale.aftersale.mainfragment.messagefragment.GrabFragment;

/**
 * Created by Administrator on 2017/11/24.
 * 信息界面
 */

public class MessageFragment extends Fragment implements View.OnClickListener{
    private FragmentManager fm;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fm=getFragmentManager();
        View view=inflater.inflate(R.layout.messagefragment,container,false);
//        RdApplication.get().setCurrFragmentName(getClass().getSimpleName());
        //设置默认的布局
        setDefaultFragment();
        return view;
    }

    private void setDefaultFragment() {
        fm.beginTransaction().replace(R.id.fl_message_manager,new GrabFragment()).commit();
    }


    @Override
    public void onClick(View v) {
        /*ft=fm.beginTransaction();
        switch (v.getId()){
            case R.id.ll_grab_message:
                if (grabFragment==null){
                    grabFragment=new GrabFragment();
                }
                ft.replace(R.id.fl_message_manager,grabFragment);
                btn_grab_message.setBackgroundResource(R.drawable.head_tab_1);
                break;
            default:
                break;
        }
        ft.commit();*/
    }
}
