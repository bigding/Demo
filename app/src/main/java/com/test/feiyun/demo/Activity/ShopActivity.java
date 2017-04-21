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
import com.test.feiyun.demo.Data.TaskBrief;
import com.test.feiyun.demo.DatabaseUtils;
import com.test.feiyun.demo.Fragment.SportFragment;
import com.test.feiyun.demo.NetWorkUtils;
import com.test.feiyun.demo.R;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ShopActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private List<CloBrief> clolist = new ArrayList<>();
    private MyAdapter adapter;
    private Handler handler;
    private ImageButton back;
    private ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        initView();
        loading = ProgressDialog.show(ShopActivity.this,"温馨提示","数据加载中",false);
        new MyThread().start();
        myHandleMessage();
    }

    private void myHandleMessage() {
        handler = new Handler(){
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 1:
                        adapter = new MyAdapter();
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        loading.dismiss();
                }
            }
        };
    }

    private void initView() {
        listView = (ListView)findViewById(R.id.clo_shop_list);
        back = (ImageButton) findViewById(R.id.shop_back);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.clo_refresh);
        refreshLayout.setOnRefreshListener(this);
        back.setOnClickListener(this);
    }

    private void initData() {
        ResultSet rs = null;
        ResultSet rs2 = null;
        String sql = "select * from clothing";
        String sql_self_cloth = "select * from userCloth where userId = " + Data.getUserID();
        rs = DatabaseUtils.getResultSet(sql);
        rs2 = DatabaseUtils.getResultSet(sql_self_cloth);
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
            //若存在，说明已经购买
            while (rs2.next()){
                for (CloBrief cloth : clolist){
                    if (cloth.getClothID() == rs2.getInt("cId")){
                        cloth.setIsbuy(true);
                        Log.i(TAG,"已经购买的服装");
                    }
                }
            }
            Log.i(TAG,sql_self_cloth);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.shop_back:
                ShopActivity.this.finish();
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
                loading.dismiss();
            } else {
                initData();
                handler.sendEmptyMessage(1);
            }
            Looper.loop();
        }
    }
    //下拉刷新
    @Override
    public void onRefresh() {
        new MyThread().start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        },1000);
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
                holder.hasBought = (TextView) convertView.findViewById(R.id.has_bought);
                holder.btn = (Button) convertView.findViewById(R.id.to_buy_clo);
                convertView.setTag(holder);
            }else {
                holder = (MyHolder) convertView.getTag();
            }
            holder.price.setText(clolist.get(position).getPrice()+"");
            holder.name.setText(clolist.get(position).getSize().toString());
            Picasso.with(getBaseContext()).load(clolist.get(position).getPath()).into(holder.pic);
            Log.i(TAG,""+clolist.get(position).isbuy());
            //如果已经购买
            if (clolist.get(position).isbuy()){
                holder.btn.setVisibility(View.GONE);
                holder.hasBought.setVisibility(View.VISIBLE);
                Log.i(TAG,"更新界面显示");
            }
            //如果尚未购买
            else {
                holder.hasBought.setVisibility(View.GONE);
                holder.btn.setVisibility(View.VISIBLE);
            }
            final MyHolder finalHolder = holder;
            holder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG,"g购买服装");
                    String warnMsg = null;
                    if (Data.getCurrentUserShape() == clolist.get(position).getiSize())
                        warnMsg = "这件衣服正好适合你当前的体型哦，确认购买？";
                    else if (Data.getCurrentUserShape() > clolist.get(position).getiSize())
                        //死胖子 你穿不了这件衣服的 滚去减肥吧！！hiahiahiahia
                        warnMsg = "这件衣服太小啦，穿不下哦，要好好锻炼变瘦才能穿哦，确认购买？";
                    else if (Data.getCurrentUserShape() < clolist.get(position).getiSize())
                        warnMsg = "衣服太大啦，不适合你穿哦，确认购买？";
                    AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
                    builder.setTitle("温馨提示");
                    builder.setMessage(warnMsg);
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
                                //金币不够的情况
                                if (Data.getMoney() < clolist.get(position).getPrice()){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
                                    builder.setTitle("温馨提示");
                                    builder.setMessage("哦吼，金币不够了，快去锻炼做任务获取金币吧！");
                                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builder.show();
                                }
                                else {
                                    finalHolder.btn.setVisibility(View.GONE);
                                    finalHolder.hasBought.setVisibility(View.VISIBLE);
                                    clolist.get(position).setIsbuy(true);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            int cost = clolist.get(position).getPrice();
                                            String insertSql = "insert into userCloth values(" + Data.getUserID() + ", " + clolist.get(position).getClothID() + ")";
                                            Log.i("insert",insertSql);
                                            Data.setMoney(Data.getMoney() - cost);
                                            String updateSql = "update user set money = " + Data.getMoney() + " where userId = " + Data.getUserID();
                                            Log.i("update",updateSql);
                                            DatabaseUtils.executeSql(insertSql);
                                            DatabaseUtils.executeSql(updateSql);
                                        }
                                    }).start();
                                    Toast.makeText(getBaseContext(),"购买成功，花费金币"+ clolist.get(position).getPrice(),Toast.LENGTH_SHORT).show();
                                }
                            }
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
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
            public Button btn;
            public TextView name,price,hasBought;
            public ImageView pic;
        }
    }
}
