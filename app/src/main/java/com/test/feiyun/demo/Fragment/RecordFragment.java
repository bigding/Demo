package com.test.feiyun.demo.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.test.feiyun.demo.Activity.PublishRcdActivity;
import com.test.feiyun.demo.Data.Data;
import com.test.feiyun.demo.Data.RecordBrief;
import com.test.feiyun.demo.Data.TaskBrief;
import com.test.feiyun.demo.DatabaseUtils;
import com.test.feiyun.demo.NetWorkUtils;
import com.test.feiyun.demo.R;
import com.test.feiyun.demo.Utils;

import org.w3c.dom.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by 飞云 on 2016/8/13.
 */
public class RecordFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private List<RecordBrief> recordlist = new ArrayList<>();
    private MyAdapter adapter;
    private ImageButton do_record;
    private ProgressDialog loading;
    private Handler myHandler;
    private TextView empty;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_pg, container, false);
        listView = (ListView) view.findViewById(R.id.record_list);
        do_record = (ImageButton) view.findViewById(R.id.do_record);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.record_swipe);
        empty = (TextView) view.findViewById(R.id.empty);
        DataThread thread = new DataThread();
        cleanNormalData();
        loading = ProgressDialog.show(getActivity(), "温馨提示", "加载数据中...", false);
        thread.start();
        myHandleMessage();
        refreshLayout.setOnRefreshListener(this);
        do_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PublishRcdActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    private void cleanNormalData() {
        recordlist.clear();
    }

    private void myHandleMessage() {
        myHandler = new Handler(){
          @Override
            public void handleMessage(Message msg){
              switch (msg.what){
                  case 1:
                      loading.dismiss();
                      adapter = new MyAdapter();
                      //如果没有内容，显示提示文字
                      if (recordlist.isEmpty()){
                          empty.setVisibility(View.VISIBLE);
                      }else {
                          empty.setVisibility(View.GONE);
                          listView.setAdapter(adapter);
                          adapter.notifyDataSetChanged();
                      }
              }
          }
        };
    }

    //初始化数据
    private void initData() {
        ResultSet rs = null;
        try {
            String sql = "select * from record where userId="+ Data.getUserID();
            rs = DatabaseUtils.getResultSet(sql);
            while (rs.next()){
                RecordBrief record = new RecordBrief();
                record.setDate(rs.getDate("date"));
                record.setRcdID(rs.getInt("rId"));
                record.setNum("宝宝锻炼的第" + rs.getInt("pastDays") + "天");
                record.setContent("\u3000\u3000"+rs.getString("rDetail"));
                Log.i("taskid","recordid:" + record.getRcdID());
                if(!recordlist.contains(record)){
                    recordlist.add(record);
                }else {
                    Log.i(TAG,"刷新内容重复");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//加载数据的线程
    class DataThread extends Thread{
    @Override
    public void run(){
        Looper.prepare();
        if (!NetWorkUtils.isNetworkConnected(getContext())){
            Toast.makeText(getActivity(),"网络连接不可用",Toast.LENGTH_SHORT).show();
            Log.i(TAG,"网络连接不可用");
            loading.dismiss();
        }
        else {
            initData();
            myHandler.sendEmptyMessage(1);
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
            DataThread dataThread = new DataThread();
            dataThread.start();
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
            return recordlist.size();
        }

        @Override
        public Object getItem(int position) {
            return recordlist.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyHolder holder = null;

            if (convertView == null){
                holder = new MyHolder();
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item,null);
                holder.num = (TextView) convertView.findViewById(R.id.record_num);
                holder.content = (TextView) convertView.findViewById(R.id.record_content);
                holder.date= (TextView) convertView.findViewById(R.id.record_date);
                convertView.setTag(holder);
            }else {
                holder = (MyHolder) convertView.getTag();
            }

            holder.date.setText(Utils.simpleDateFormat.format(recordlist.get(position).getDate()));
            holder.num.setText(recordlist.get(position).getNum());
            holder.content.setText(recordlist.get(position).getContent());
            System.out.println(position);
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(getContext(),"好 知道你已经点了",Toast.LENGTH_SHORT).show();
//                }
//            });
            return convertView;
        }
        class MyHolder{
            public TextView num,content,date;
        }
    }
}
