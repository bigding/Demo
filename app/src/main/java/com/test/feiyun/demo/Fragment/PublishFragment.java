package com.test.feiyun.demo.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mysql.jdbc.DatabaseMetaDataUsingInfoSchema;
import com.squareup.picasso.Picasso;
import com.test.feiyun.demo.Activity.ShopActivity;
import com.test.feiyun.demo.Activity.UpdateHWActivity;
import com.test.feiyun.demo.Activity.WardrobeActivity;
import com.test.feiyun.demo.Data.Data;
import com.test.feiyun.demo.DatabaseUtils;
import com.test.feiyun.demo.NetWorkUtils;
import com.test.feiyun.demo.R;
import com.test.feiyun.demo.RefreshLayout;

import org.w3c.dom.Text;

import java.sql.ResultSet;
import java.sql.SQLException;

import cn.smssdk.gui.layout.Res;

/**
 * Created by 飞云 on 2016/8/13.
 */
public class PublishFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private ImageButton shop,wardrobe,update_hw;
    private TextView money,nickname,shape,sign;
    private SwipeRefreshLayout refresh;
    private Bundle bundle = new Bundle();
    private Handler myHandler;
    private ImageView petPic;
    private final static String TAG = "TAG";
    private ProgressDialog loading;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        return initView(inflater,container);
    }

    //初始化界面控件函数
    private View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.set_pg2, container, false);
        shop = (ImageButton) view.findViewById(R.id.shop);
        money = (TextView) view.findViewById(R.id.user_money);
        wardrobe = (ImageButton) view.findViewById(R.id.wardrobe);
        update_hw = (ImageButton) view.findViewById(R.id.update_hw);
        nickname = (TextView) view.findViewById(R.id.pet_nickname);
        shape = (TextView) view.findViewById(R.id.pet_shape);
        sign = (TextView) view.findViewById(R.id.pet_sign);
        petPic = (ImageView) view.findViewById(R.id.pet);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh_pet);
        refresh.setOnRefreshListener(this);
        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShopActivity.class);
                startActivity(intent);
            }
        });
        wardrobe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WardrobeActivity.class);
                startActivity(intent);
            }
        });
        update_hw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UpdateHWActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
    @Override
    public void onResume(){
        Log.i("TAG","onResume()");
        super.onResume();
        if (!NetWorkUtils.isNetworkConnected(getContext())){
            Toast.makeText(getContext(),"网络错误",Toast.LENGTH_SHORT).show();
        }
        else {
            loading = ProgressDialog.show(getContext(),"温馨提示","数据加载中...",false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    updatePet();
                    updateInfo();
                    updateMoney();
                    Message msg = new Message();
                    msg.what = 1;
                    msg.setData(bundle);
                    myHandler.sendMessage(msg);
                    Log.i("message","message 1 已经发送出");
                    Looper.loop();
                }
            }).start();
            MyHandleMessage();
        }
    }

    private void MyHandleMessage() {
        myHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 1:
                        Log.i("message","handler 消息 1 ");
                        nickname.setText(msg.getData().getString("nickname"));
                        shape.setText(msg.getData().getString("shape"));
                        sign.setText(msg.getData().getString("sign"));
                        money.setText(msg.getData().getString("money"));
                        Picasso.with(getContext()).load(Data.getPetPath()).into(petPic);
                        //Log.i("img0",Data.getPetPath());
                        loading.dismiss();
                        break;
                }
            }
        };
    }

    //更新宠物名称、签名信息
    private void updateInfo() {
        String sql = "select * from userPet where userId = " + Data.getUserID();
        ResultSet rs = DatabaseUtils.getResultSet(sql);
        try {
            while (rs.next()){
                bundle.putString("nickname",rs.getString("petNickName"));
                int ishape = rs.getInt("shape");
                if (ishape == 1)
                    bundle.putString("shape","完美体型");
                else if (ishape == 2)
                    bundle.putString("shape","有点小胖");
                else if (ishape == 3)
                    bundle.putString("shape","太胖了");

                bundle.putString("sign",rs.getString("signature"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //更新宠物图片
    private void updatePet() {
        ResultSet rs = null;
        String sql = "select * from userPetClo where userId = " + Data.getUserID();
        rs = DatabaseUtils.getResultSet(sql);
        int cId = 0x123;
        try {
            while (rs.next()){
                cId = rs.getInt("cId");
            }
            Log.i(TAG,cId + "");
            if (cId == 0 || !Data.isWear()){
                String noClo = "select * from petShape where pId = " + Data.getUserPetID() + " and shape = " + Data.getCurrentUserShape();
                ResultSet rs2 = DatabaseUtils.getResultSet(noClo);
                Log.i(TAG,noClo);
                while(rs2.next()){
                    Data.setPetPath("http://www.feiyunamy.cn/mengxiangshou/"+rs2.getString("path").toString());
                    Log.i("img0",Data.getPetPath());
                }
            }
            else {
                String hasClo = "select * from petClo where pId = " + Data.getUserPetID() + " and cId = " + cId + " and pShape = " + Data.getCurrentUserShape();
                ResultSet rs3 = DatabaseUtils.getResultSet(hasClo);
                Log.i(TAG,hasClo);
                while (rs3.next()){
                    Data.setPetPath("http://www.feiyunamy.cn/mengxiangshou/" + rs3.getString("path"));
                    Log.i("img0",Data.getPetPath());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //更新金钱显示
    private void updateMoney() {
       bundle.putString("money",Data.getMoney()+"");
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        if (!NetWorkUtils.isNetworkConnected(getContext())){
            Toast.makeText(getContext(),"网络错误",Toast.LENGTH_SHORT).show();
            refresh.setRefreshing(false);
        }
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    updatePet();
                    updateInfo();
                    updateMoney();
                }
            }).start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    nickname.setText(bundle.getString("nickname"));
                    shape.setText(bundle.getString("shape"));
                    sign.setText(bundle.getString("sign"));
                    money.setText(bundle.getString("money"));
                    Picasso.with(getContext()).load(Data.getPetPath()).error(R.drawable.no_image).placeholder(R.drawable.loading).into(petPic);
                    refresh.setRefreshing(false);
                }
            },1000);
        }

    }
}
