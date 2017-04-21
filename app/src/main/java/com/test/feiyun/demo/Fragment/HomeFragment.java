package com.test.feiyun.demo.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.feiyun.demo.Data.Data;
import com.test.feiyun.demo.DatabaseUtils;
import com.test.feiyun.demo.NetWorkUtils;
import com.test.feiyun.demo.R;
import com.test.feiyun.demo.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by 飞云 on 2016/8/13.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView pastDays;
    private Date date;
    private Handler handler;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_pg, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        pastDays = (TextView) view.findViewById(R.id.pastdays);
        swipeRefreshLayout.setOnRefreshListener(this);//设置下拉刷新事件监听
        return view;
    }

    @Override
    public void onResume(){
        updateData();
        super.onResume();
    }
    private void updateData() {
        if (Data.getRegisterDate() != null){
            try {
                pastDays.setText("" + Utils.daysBetween(Data.getRegisterDate(),new Date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else {
            if (Data.getUserID() == 0x123){
                pastDays.setText(" ");
            }else {
                new updateThread().start();
                handler = new Handler(){
                    public void handleMessage(Message msg){
                        switch (msg.what){
                            case 1:
                                try {
                                    pastDays.setText("" + Utils.daysBetween(date,new Date()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Log.i("date","msg.what == 1");
                                break;
                            default:
                                break;
                        }
                    }
                };
            }

        }
    }
    class updateThread extends Thread{
        public void run(){
            Looper.prepare();
            if (!NetWorkUtils.isNetworkConnected(getContext())){
                Toast.makeText(getContext(),"网络连接出错",Toast.LENGTH_SHORT).show();
            }else {
                ResultSet rs = null;
                String sql = "select * from user where name = " + "\"" + Data.getUsername() + "\" or tell = \"" + Data.getPhone() + "\"";
                rs = DatabaseUtils.getResultSet(sql);
                try {
                    while (rs.next()){
                       date = rs.getDate("registerDate");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            Log.i("date","msg.what == 1");
            handler.sendEmptyMessage(1);
            Looper.loop();
        }
    }

    //首页下拉刷新动作调用函数
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                updateData();
                swipeRefreshLayout.setRefreshing(false);
            }
        },2000);
    }
}
