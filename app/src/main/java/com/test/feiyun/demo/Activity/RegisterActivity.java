package com.test.feiyun.demo.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mysql.jdbc.Connection;
import com.test.feiyun.demo.DatabaseUtils;
import com.test.feiyun.demo.NetWorkUtils;
import com.test.feiyun.demo.R;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static android.content.ContentValues.TAG;
import static com.mob.tools.utils.ResHelper.getStringRes;

public class RegisterActivity extends Activity implements View.OnClickListener {

    private EditText phone;
    private EditText cord;
    private TextView now;
    private Button getCord;
    private Button saveCord,test;
    private EditText password;
    private String iPhone;
    private String iCord;
    private int time = 60;
    private boolean flag = true;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        init();
        SMSSDK.initSDK(this, "1b61413c29274", "0a3adfa06bff8f7028026a7b76001f2c");
        EventHandler eh=new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }

        };
        SMSSDK.registerEventHandler(eh);

    }

    private void init() {
        phone = (EditText) findViewById(R.id.phone);
        cord = (EditText) findViewById(R.id.cord);
        now = (TextView) findViewById(R.id.now);
        getCord = (Button) findViewById(R.id.getcord);
        saveCord = (Button) findViewById(R.id.savecord);
        test = (Button) findViewById(R.id.test);
        getCord.setOnClickListener(this);
        saveCord.setOnClickListener(this);
        test.setOnClickListener(this);
        password = (EditText) findViewById(R.id.password);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getcord:
                if(!NetWorkUtils.isNetworkConnected(getBaseContext())){
                    Toast.makeText(getBaseContext(),"网络连接不可用",Toast.LENGTH_SHORT).show();
                }else {
                    usernameThread usernamethread = new usernameThread();
                    if(!TextUtils.isEmpty(phone.getText().toString().trim())){
                        if(phone.getText().toString().trim().length()==11){
                            //判断用户名是否已经存在的进程
                            usernamethread.start();
                            mHandler = new Handler(){
                                @Override
                                public void handleMessage(Message msg){
                                    if (msg.what == 5){
                                        Log.i(TAG,"hello 123");
                                        Toast.makeText(RegisterActivity.this,"此手机已注册",Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        iPhone = phone.getText().toString().trim();
                                        SMSSDK.getVerificationCode("86",iPhone);
                                        cord.requestFocus();
                                        getCord.setVisibility(View.GONE);
                                        now.setVisibility(View.VISIBLE);
                                        reminderText();
                                    }
                                }
                            };

                        }else{
                            Toast.makeText(RegisterActivity.this, "请输入完整电话号码", Toast.LENGTH_LONG).show();
                            phone.requestFocus();
                        }
                    }else {
                        Toast.makeText(RegisterActivity.this, "请输入您的电话号码", Toast.LENGTH_LONG).show();
                        phone.requestFocus();
                    }
                }
                break;
            case R.id.savecord:
                doRegister();
                break;
            case R.id.test:
                Intent intent = new Intent(RegisterActivity.this,SetInfoActivity.class);
                intent.putExtra("phone",phone.getText().toString());
                intent.putExtra("password",password.getText().toString());
                startActivity(intent);
                RegisterActivity.this.finish();
                break;
            default:
                break;
        }
    }
//进行注册
    private void doRegister() {

        //注册检查
        if(phone.getText().length()==0){
            Toast.makeText(getApplicationContext(),"请输入手机号",Toast.LENGTH_SHORT).show();
        }
        else if(cord.getText().length() ==0){
            Toast.makeText(RegisterActivity.this, "请输入验证码", Toast.LENGTH_LONG).show();
            cord.requestFocus();
        }
        else if(password.getText().length()==0){
            Toast.makeText(getApplicationContext(),"请设置登录密码",Toast.LENGTH_SHORT).show();
        }
        else{
                //向下一个activity传值，准备进行数据库插入
                //thread.start();
                iCord = cord.getText().toString().trim();
                SMSSDK.submitVerificationCode("86", iPhone, iCord);
                flag = false;
        }
    }


    //验证码送成功后提示文字
    private void reminderText() {
        handlerText.sendEmptyMessageDelayed(1, 1000);
    }

    Handler handlerText =new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what==1){
                if(time>0){
                    now.setText("剩余"+time+"秒");
                    time--;
                    handlerText.sendEmptyMessageDelayed(1, 1000);
                }else{
                    now.setText("");
                    time = 60;
                    now.setVisibility(View.GONE);
                    getCord.setVisibility(View.VISIBLE);
                }
            }else{
//                cord.setText("");
//                now.setText("提示信息");
                time = 60;
                now.setVisibility(View.GONE);
                getCord.setVisibility(View.VISIBLE);
            }
        };
    };

    Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event="+event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功,验证通过
                    Toast.makeText(getApplicationContext(), "验证码校验成功", Toast.LENGTH_SHORT).show();
                    handlerText.sendEmptyMessage(2);
                    //new RegisterThread().start();
                    Intent intent = new Intent(RegisterActivity.this,SetInfoActivity.class);
                    intent.putExtra("phone",phone.getText().toString());
                    intent.putExtra("password",password.getText().toString());
                    startActivity(intent);
                    RegisterActivity.this.finish();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){//服务器验证码发送成功
                    //reminderText();
                    Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){//返回支持发送验证码的国家列表
                    Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
                }
            } else {
                if(flag){
//                    getCord.setVisibility(View.VISIBLE);
//                    now.setVisibility(View.GONE);
//                    time = 60;
                    handlerText.sendEmptyMessage(3);//失败后发送message
                    Toast.makeText(RegisterActivity.this, "验证码获取失败，请重新获取", Toast.LENGTH_SHORT).show();
                    phone.requestFocus();
                }else{
                    ((Throwable) data).printStackTrace();
                    int resId = getStringRes(RegisterActivity.this, "smssdk_network_error");
                    Toast.makeText(RegisterActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
                    cord.selectAll();
                    if (resId > 0) {
                        Toast.makeText(RegisterActivity.this, resId, Toast.LENGTH_SHORT).show();
                    }
                }

            }

        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
 class usernameThread extends Thread{
     @Override
     public void run(){
         if (!NetWorkUtils.isNetworkConnected(getBaseContext())){
             Toast.makeText(RegisterActivity.this,"网络连接不可用",Toast.LENGTH_SHORT).show();
             Log.i(TAG,"网络连接不可用");
         }
         else {
             String usernameSql = "select * from user where tell=\"" + phone.getText() + "\"";
             Log.i(TAG,usernameSql);
             Connection conn= null;
             try {
                 conn = (Connection) DatabaseUtils.getConnection();
                 Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(usernameSql);
                 //用户名已经存在
                 if (rs.next()&&rs.getString("tell").equals( phone.getText().toString())){
                     Log.d(TAG,"hello");
                     mHandler.sendEmptyMessage(5);
                     //
                 }
                 else {
                     mHandler.sendEmptyMessage(6);
                 }
             } catch (SQLException e) {
                 e.printStackTrace();
             }
         }

     }

 }
}