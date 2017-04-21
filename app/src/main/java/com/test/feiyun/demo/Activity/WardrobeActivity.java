package com.test.feiyun.demo.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.test.feiyun.demo.Data.CloBrief;
import com.test.feiyun.demo.Data.Data;
import com.test.feiyun.demo.DatabaseUtils;
import com.test.feiyun.demo.NetWorkUtils;
import com.test.feiyun.demo.R;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class WardrobeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private List<CloBrief> clolist = new ArrayList<>();
    private MyAdapter adapter;
    private Handler handler;
    private ImageButton back;
    private ProgressDialog loading;
    private TextView empty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wardrobe);
        initView();
        loading = ProgressDialog.show(WardrobeActivity.this,"温馨提示","数据加载中",false);
        new MyThread().start();
        myHandleMessage();
    }
    private void myHandleMessage() {
        handler = new Handler(){
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 1:
                        adapter = new MyAdapter();
                        loading.dismiss();
                        if (clolist.isEmpty()){
                            empty.setVisibility(View.VISIBLE);
                        }
                        else {
                            empty.setVisibility(View.GONE);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                }
            }
        };
    }

    private void initView() {
        empty = (TextView) findViewById(R.id.empty_ward);
        listView = (ListView)findViewById(R.id.clo_wardrobe_list);
        back = (ImageButton) findViewById(R.id.back_wardrobe);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.wardrobe_refresh);
        refreshLayout.setOnRefreshListener(this);
        back.setOnClickListener(this);
    }
    private void initData() {
        ResultSet rs = null;
        ResultSet rs2 = null;
        String sql = " select * from clothing where cId = any(select cId from userCloth where userId = " + Data.getUserID() + ")";
        String judgeSql = "select * from userPetClo where userId = " + Data.getUserID();
        rs = DatabaseUtils.getResultSet(sql);
        rs2 = DatabaseUtils.getResultSet(judgeSql);
        try {
            while(rs.next()){
                CloBrief clothing = new CloBrief();
                clothing.setPath("http://www.feiyunamy.cn/mengxiangshou/" + rs.getString("cPath"));
                clothing.setPrice(rs.getInt("cPrice"));
                clothing.setClothID(rs.getInt("cId"));
                Log.i("clothing path",clothing.getPath());
                int size = rs.getInt("cSize");
                clothing.setiSize(size);
                if (size == 1)
                    clothing.setSize("小号");
                else if (size == 2)
                    clothing.setSize("中号");
                else if (size == 3)
                    clothing.setSize("大号");
                Log.i("clothing size",clothing.getSize());
                if(!clolist.contains(clothing)){
                    clolist.add(clothing);
                }else {
                    Log.i(TAG,"刷新内容重复");
                }
            }

            //若存在，说明已经穿戴
            while (rs2.next()){
                for (CloBrief cloth : clolist){
                    if (cloth.getClothID() == rs2.getInt("cId")){
                        cloth.setIswear(true);
                        Log.i(TAG,"已经购买的服装");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    public void onRefresh() {
        new MyThread().start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();
            }
        },2000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_wardrobe:
                WardrobeActivity.this.finish();
                break;
            default:
                break;
        }
    }

    class MyThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            if (!NetWorkUtils.isNetworkConnected(getBaseContext())) {
                Toast.makeText(getBaseContext(), "网络错误", Toast.LENGTH_SHORT).show();
            } else {
                initData();
                handler.sendEmptyMessage(1);
            }
            Looper.loop();
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return clolist.size();
        }

        @Override
        public Object getItem(int position) {
            return clolist.get(position);
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.clothes_item,null);
                holder.name = (TextView) convertView.findViewById(R.id.clothes_name);
                holder.pic = (ImageView) convertView.findViewById(R.id.clothes_pic);
                holder.price = (TextView) convertView.findViewById(R.id.clo_price);
                holder.btn = (Button) convertView.findViewById(R.id.to_buy_clo);
                holder.cancel = (Button) convertView.findViewById(R.id.cancel_to_wear);
                convertView.setTag(holder);
            }else {
                holder = (MyHolder) convertView.getTag();
            }
            holder.btn.setText("点击穿戴");
            holder.price.setText(clolist.get(position).getPrice()+"");
            holder.name.setText(clolist.get(position).getSize().toString());
            Picasso.with(getBaseContext()).load(clolist.get(position).getPath()).into(holder.pic);
            if (clolist.get(position).iswear()){
                holder.btn.setVisibility(View.GONE);
                holder.cancel.setVisibility(View.VISIBLE);
            }
            else {
                holder.cancel.setVisibility(View.GONE);
                holder.btn.setVisibility(View.VISIBLE);
            }
            //点击穿戴

            final MyHolder finalHolder = holder;
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(WardrobeActivity.this);
                    builder.setTitle("温馨提示");
                    builder.setMessage("确认穿戴？");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Log.i(TAG,"btn_cancel");
                        }
                    });
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!NetWorkUtils.isNetworkConnected(getBaseContext())){
                                Toast.makeText(getBaseContext(),"网络连接不可用",Toast.LENGTH_SHORT).show();
                                Log.i(TAG,"网络连接不可用");
                            }
                            else {
                                String warn = null;
                                if (clolist.get(position).getiSize() == Data.getCurrentUserShape()){

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String sql = "update userPetClo set cId = " + clolist.get(position).getClothID() + " , pShape = " + clolist.get(position).getiSize() + " where userId = " + Data.getUserID();
                                            Log.i(TAG,sql);
                                            DatabaseUtils.executeSql(sql);
                                        }
                                    }).start();
                                    finalHolder.btn.setVisibility(View.GONE);
                                    finalHolder.cancel.setVisibility(View.VISIBLE);
                                    clolist.get(position).setIswear(true);
                                    Data.setIsWear(true);
                                    Toast.makeText(getBaseContext(),"穿戴成功",Toast.LENGTH_SHORT).show();
                                   // WardrobeActivity.this.finish();
                                }
                                else {
                                    if (clolist.get(position).getiSize() > Data.getCurrentUserShape())
                                        warn = "衣服尺码太大了，快去商店购买合适的衣服吧！";
                                    else if (clolist.get(position).getiSize() < Data.getCurrentUserShape())
                                        warn = "衣服尺码太小了，快去完成运动任务变得更瘦，能穿更多好看的衣服哦";
                                    AlertDialog.Builder builder = new AlertDialog.Builder(WardrobeActivity.this);
                                    builder.setTitle("温馨提示");
                                    builder.setMessage(warn);
                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder.show();
                                }
                            }
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }
            });
            if (clolist.get(position).iswear()){
                holder.btn.setVisibility(View.GONE);
                holder.cancel.setVisibility(View.VISIBLE);
            }
//            点击取消穿戴
            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(WardrobeActivity.this);
                    builder.setTitle("温馨提示");
                    builder.setMessage("取消穿戴？");
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Log.i(TAG,"btn_cancel");
                        }
                    });
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String sql = "update userPetClo set cId = " + 0 + " , pShape = " + clolist.get(position).getiSize() + " where userId = " + Data.getUserID();
                                    Log.i(TAG,sql);
                                    DatabaseUtils.executeSql(sql);
                                }
                            }).start();
                            finalHolder.btn.setVisibility(View.VISIBLE);
                            finalHolder.cancel.setVisibility(View.GONE);
                            clolist.get(position).setIswear(false);
                            Toast.makeText(getBaseContext(),"取消穿戴成功",Toast.LENGTH_SHORT).show();
                            //WardrobeActivity.this.finish();
                        }
                    });
                    builder.show();
                }
            });

            return convertView;
        }
        class MyHolder{
            public Button btn,cancel;
            public TextView name,price,hasBought;
            public ImageView pic;
        }
    }
}
