package com.test.feiyun.demo.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.test.feiyun.demo.Activity.DietContentActivity;
import com.test.feiyun.demo.Activity.MainActivity;
import com.test.feiyun.demo.Data.Data;
import com.test.feiyun.demo.Data.DietBrief;
import com.test.feiyun.demo.DatabaseUtils;
import com.test.feiyun.demo.NetWorkUtils;
import com.test.feiyun.demo.PRoundedCornersTransformation;
import com.test.feiyun.demo.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;

/**
 * Created by 飞云 on 2016/8/13.
 */
public class DietFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private List<DietBrief> dietlist = new ArrayList<>();
    private MyAdapter adapter;
    private static String TAG = "TAG";
    private DataInitThread thread;
    private Handler datahandler;
    private ProgressDialog loading;
    public boolean isLoaded = false;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.diet_page, container, false);
        listView = (ListView) view.findViewById(R.id.diet_list);
        //loading = (TextView) view.findViewById(R.id.loading_text);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.diet_swipe);
        thread = new DataInitThread();
        clearNormalData();
        loading = ProgressDialog.show(getActivity(), "温馨提示", "加载数据中...", false);
        thread.start();
        refreshLayout.setOnRefreshListener(this);
        handleMessageAftData();
        return view;
    }
    //保证更新身高体重信息以后，食谱内容按照BMI值正确加载
    private void clearNormalData() {
        dietlist.clear();
    }

    private void handleMessageAftData() {
        datahandler = new Handler(){
            @Override
            public void handleMessage(Message msg){

                switch (msg.what){
                    case 1:
                        loading.dismiss();
                        adapter = new MyAdapter();
                        listView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        break;
                    case 2:
                        refreshLayout.setRefreshing(false);
                }


            }
        };
    }

    private void initData() {
        Log.i(TAG,"加载数据完成之前:"+isLoaded);
        ResultSet rs = null;
            try {
                String sql = "select * from diet where maxBMI>"+ Data.getBMI()+"and minBMI<"+Data.getBMI();
                rs = DatabaseUtils.getResultSet(sql);
                while (rs.next()){
                    DietBrief diet = new DietBrief();
                    diet.setTitle(rs.getString("dName"));
                    diet.setInfo(rs.getString("dDesc"));
                    diet.setPic("http://www.feiyunamy.cn/mengxiangshou/" + rs.getString("dPath"));
                    diet.setContentUrl("http://www.feiyunamy.cn/mengxiangshou/"+rs.getString("dLink"));
                    if(!dietlist.contains(diet)){
                        dietlist.add(diet);
                    }else {
                        Log.i(TAG,"刷新内容重复");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            isLoaded = true;
            Log.i(TAG,""+dietlist.size());
        Log.i(TAG,"数据加载是否完成"+isLoaded);
    }
    class DataInitThread extends Thread{
        @Override
        public void run(){
            Looper.prepare();
            if (!NetWorkUtils.isNetworkConnected(getContext())){
                loading.dismiss();
                Toast.makeText(getActivity(),"网络连接不可用",Toast.LENGTH_SHORT).show();
                Log.i(TAG,"网络连接不可用");
            }
            else {
                isLoaded = false;
                Toast.makeText(getContext(),"正在加载数据",Toast.LENGTH_SHORT).show();
                initData();
                datahandler.sendEmptyMessage(1);
                Log.i(TAG,""+isLoaded);
            }
            Looper.loop();
        }
    }
//下拉刷新
    @Override
    public void onRefresh() {
        //dietlist.clear();
        if (!NetWorkUtils.isNetworkConnected(getContext())){
            Toast.makeText(getActivity(),"网络连接不可用",Toast.LENGTH_SHORT).show();
            refreshLayout.setRefreshing(false);
        }else {
            DataInitThread refreshThread = new DataInitThread();
            refreshThread.start();
            Log.i(TAG,"out "+isLoaded);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                    datahandler.sendEmptyMessage(2);
                    Log.i(TAG,"加载完成，停止刷新");
                }
            },1000);
        }
    }
    class MyAdapter extends BaseAdapter{

      @Override
      public int getCount() {
          return dietlist.size();
      }

      @Override
      public Object getItem(int position) {
          return dietlist.get(position);
      }

      @Override
      public long getItemId(int position) {
          return 0;
      }

      @Override
      public View getView(final int position, View convertView, ViewGroup parent) {
          ViewHolder holder = null;
          Handler handler = new Handler();
          if (convertView == null){
              holder = new ViewHolder();
              convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.diet_item,null);
              holder.title = (TextView) convertView.findViewById(R.id.diet_title);
              holder.info = (TextView) convertView.findViewById(R.id.diet_info);
              holder.bg= (LinearLayout) convertView.findViewById(R.id.bg_pic);
              convertView.setTag(holder);
          }else {
              holder = (ViewHolder) convertView.getTag();
          }
          holder.title.setText(dietlist.get(position).getTitle());
          holder.info.setText(dietlist.get(position).getInfo());
          //holder.bg.setBackgroundResource(R.drawable.temp01);
          final ViewHolder finalHolder = holder;
          Picasso.with(getContext()).load(dietlist.get(position).getPic()).error(R.drawable.no_image).placeholder(R.drawable.no_image).transform(new PRoundedCornersTransformation(15,0)).into(new Target() {
                  @Override
                  public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                      Drawable drawable =new BitmapDrawable(bitmap);
                      finalHolder.bg.setBackground(drawable);
                      Log.i(TAG,"picasso加载完成");
                  }
                  @Override
                  public void onBitmapFailed(Drawable errorDrawable) {
                      finalHolder.bg.setBackground(errorDrawable);
                      Log.i(TAG,"picasso网络访问出错");
                  }
                  @Override
                  public void onPrepareLoad(Drawable placeHolderDrawable) {
                      finalHolder.bg.setBackground(placeHolderDrawable);
                      Log.i(TAG,"picasso准备加载图片");
                  }
              });
          convertView.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  Intent intent = new Intent(getActivity(), DietContentActivity.class);
                  intent.putExtra("diet_content_url",dietlist.get(position).getContentUrl());
                  startActivity(intent);
              }
          });
          return convertView;
      }
      class ViewHolder{
          public TextView title,info;
          public LinearLayout bg;
      }
  }
}
