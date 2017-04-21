package com.test.feiyun.demo.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.feiyun.demo.Activity.MainActivity;
import com.test.feiyun.demo.Data.Data;
import com.test.feiyun.demo.Data.DietBrief;
import com.test.feiyun.demo.Data.TaskBrief;
import com.test.feiyun.demo.DatabaseUtils;
import com.test.feiyun.demo.NetWorkUtils;
import com.test.feiyun.demo.R;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

/**
 * Created by 飞云 on 2016/8/13.
 */
public class SportFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private List<TaskBrief> tasklist = new ArrayList<>();
    private MyAdapter adapter;
    private DataInitThread thread;
    private Handler datahandler;
    private ProgressDialog loading;
    private SQLiteDatabase db;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sport_pg, container, false);
        listView = (ListView) view.findViewById(R.id.task_list);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.task_swipe);
        thread = new DataInitThread();
        cleanNormalData();
        loading = ProgressDialog.show(getActivity(), "温馨提示", "加载数据中...", false);
        thread.start();
        refreshLayout.setOnRefreshListener(this);
        handleMessageAftData();
        return view;
    }
    //清理非正常的数据，避免更新身高体重信息后数据加载异常
    private void cleanNormalData() {
        tasklist.clear();
    }

    private void handleMessageAftData() {
        datahandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if (msg.what == 1){
                    loading.dismiss();
                    adapter = new MyAdapter();
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        };
    }
    private void initData() {
        ResultSet rs = null,rs1 = null;
        try {
            String sql = "select * from sports where maxBMI>"+ Data.getBMI()+"and minBMI<"+Data.getBMI();
            String sql_current_user_task = "select * from userSportNow where userId=" + Data.getUserID();
            rs1 = DatabaseUtils.getResultSet(sql_current_user_task);
            rs = DatabaseUtils.getResultSet(sql);
            while (rs.next()){
                TaskBrief task = new TaskBrief();
                task.setContent(rs.getString("sDetail"));
                task.setMoney(Integer.parseInt(rs.getString("sValue")));
                task.setTaskID(rs.getInt("sId"));
//                while (rs1.next()){
//                    if (rs.getInt("sId") == rs1.getInt("sId")){
//                        task.setConfirm(true);
//                        Log.i("userid","已经完成的任务");
//                    }
//                }
                Log.i("taskid","taskid:" + task.getTaskID());
                if(!tasklist.contains(task)){
                    tasklist.add(task);
                }else {
                    Log.i(TAG,"刷新内容重复");
                }
            }
            while (rs1.next()){
                for (TaskBrief i : tasklist){
                    if (i.getTaskID() == rs1.getInt("sId")){
                        i.setConfirm(true);
                        Log.i("userid","已经完成的任务");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    class DataInitThread extends Thread{
        @Override
        public void run(){
            Looper.prepare();
            if (!NetWorkUtils.isNetworkConnected(getContext())){
                Toast.makeText(getActivity(),"网络连接不可用",Toast.LENGTH_SHORT).show();
                Log.i(TAG,"网络连接不可用");
                loading.dismiss();
            }
            else {
                Toast.makeText(getContext(),"正在加载数据",Toast.LENGTH_SHORT).show();
                initData();
                datahandler.sendEmptyMessage(1);
            }
            Looper.loop();
        }
    }
    @Override
    public void onRefresh() {
        if (!NetWorkUtils.isNetworkConnected(getContext())){
            Toast.makeText(getActivity(),"网络连接不可用",Toast.LENGTH_SHORT).show();
            refreshLayout.setRefreshing(false);
        }else {
            new DataInitThread().start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    refreshLayout.setRefreshing(false);
                }
            },1000);
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return tasklist.size();
        }

        @Override
        public Object getItem(int position) {
            return tasklist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            MyHolder holder = null;

            if (convertView == null){
                holder = new MyHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item,null);
                holder.money = (TextView) convertView.findViewById(R.id.task_money);
                holder.content = (TextView) convertView.findViewById(R.id.task_content);
                holder.coin_confirm = (ImageButton) convertView.findViewById(R.id.coin_confirm);
                convertView.setTag(holder);
            }else {
                holder = (MyAdapter.MyHolder) convertView.getTag();
            }
            holder.money.setText("" + tasklist.get(position).getMoney());
            holder.content.setText(tasklist.get(position).getContent());
            System.out.println(position);
            final MyHolder finalHolder = holder;
            if (!tasklist.get(position).getConfirm()){
                holder.coin_confirm.setBackgroundResource(R.drawable.coin_confirm);
            }
            else {
                holder.coin_confirm.setBackgroundResource(R.drawable.coin_confirm_pressed);
            }
            holder.coin_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!tasklist.get(position).getConfirm()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("温馨提示");
                        builder.setMessage("确认完成任务以后才可以领取金币哦!");
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (!NetWorkUtils.isNetworkConnected(getContext())){
                                    Toast.makeText(getActivity(),"网络连接不可用",Toast.LENGTH_SHORT).show();
                                    Log.i(TAG,"网络连接不可用");
                                }
                                else {
                                    tasklist.get(position).setConfirm(true);
                                    finalHolder.coin_confirm.setBackgroundResource(R.drawable.coin_confirm_pressed);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Looper.prepare();
                                            String sql = "insert into userSportNow(userId,sId) values(" + Data.getUserID() + "," + tasklist.get(position).getTaskID()+")";
                                            Data.setMoney(Data.getMoney() + tasklist.get(position).getMoney());
                                            //String sql_money = "update user set money =" + Data.getMoney() + "where userId = " + Data.getUserID();
                                            String sql_money = "update user set money = "+Data.getMoney()+ " where userId = "+Data.getUserID();
                                            Log.i("sql_check",sql_money);
                                            DatabaseUtils.executeSql(sql);
                                            DatabaseUtils.executeSql(sql_money);
                                            Looper.loop();
                                        }
                                    }).start();
                                    Toast.makeText(getContext(),"任务完成，成功获得"+ tasklist.get(position).getMoney()+"金币，继续加油哦!",Toast.LENGTH_SHORT).show();
                                }
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Log.i("cancel","btn_cancel");
                            }
                        });
                        builder.show();
                    }
                    else {
                        Log.i(TAG,"点击了已完成的任务");
                        Toast.makeText(getContext(),"已经完成的任务是不能再领金币的哦！",Toast.LENGTH_SHORT).show();
                    }
                }
            });
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(getContext(),"好 知道你已经点了",Toast.LENGTH_SHORT).show();
//                }
//            });
            return convertView;
        }
        class MyHolder{
            public TextView content,money;
            public ImageButton coin_confirm;
        }
    }

}
