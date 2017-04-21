package com.test.feiyun.demo.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mysql.jdbc.Connection;
import com.test.feiyun.demo.Data.Data;
import com.test.feiyun.demo.DatabaseUtils;
import com.test.feiyun.demo.NetWorkUtils;
import com.test.feiyun.demo.R;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginActivity extends AppCompatActivity {

    TextView register,in;
    Button login;
    EditText username, psw;
    static String TAG = "TAG";
    Handler loginHandler;
    ProgressDialog dialog =null;
    String  currentUser = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        netWork();
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });
        //点击登录按钮
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginThread logintd = new LoginThread();

                if (username.getText().length()==0){
                    Toast.makeText(LoginActivity.this,"请输入用户名",Toast.LENGTH_SHORT).show();
                }
                else if (psw.getText().length() == 0){
                    Toast.makeText(LoginActivity.this,"请输入用密码",Toast.LENGTH_SHORT).show();
                }
                else if (!isNetAvailable(getApplicationContext())){
                    Toast.makeText(LoginActivity.this,"网络不可用",Toast.LENGTH_SHORT).show();
                }
                else {
                    //输入框不为空
                    currentUser = username.getText().toString();
                    dialog = ProgressDialog.show(LoginActivity.this, "登录提示", "正在登录，请稍等...", false);
                    logintd.start();
                }
            }
        });
        /**
         * 测试入口
         */
        in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
                Data.setUsername(null);
                Data.setUserID(0x123);
                Data.setPhone("none");
                Data.setBMI(10000);
                Data.setRegisterDate(null);
            }
        });
        //登录判断
        loginHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                switch (msg.what){
                    case 1:
                        Log.i(TAG,"handler传递消息1成功");
                        Toast toast1 = Toast.makeText(LoginActivity.this,"用户名不存在",Toast.LENGTH_SHORT);
                        toast1.show();
                        dialog.dismiss();
                        break;
                    case 2:
                        Log.i(TAG,"handler传递消息2成功");
                        Toast toast2 = Toast.makeText(LoginActivity.this,"密码错误",Toast.LENGTH_SHORT);
                        dialog.dismiss();
                        toast2.show();
                        break;
                    case 3:
                        Log.i(TAG,"handler传递消息3成功");
                        dialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                        LoginActivity.this.finish();
                        Data.setPhone(currentUser);
                        Data.setUsername(currentUser);
                        System.out.println(Data.getUsername());
                        Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    private void netWork() {
        if (!NetWorkUtils.isNetworkConnected(getBaseContext())){
            Toast.makeText(LoginActivity.this,"网络连接不可用",Toast.LENGTH_SHORT).show();
        }
        else{
        }
    }

    public static boolean isNetAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isAvailable());
    }

    private void initView() {
        register = (TextView) findViewById(R.id.register);
        login = (Button) findViewById(R.id.login);
        username = (EditText) findViewById(R.id.username);
        psw = (EditText) findViewById(R.id.psw);
        in = (TextView) findViewById(R.id.in);
    }

    class LoginThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            if (!NetWorkUtils.isNetworkConnected(getBaseContext())){
                Toast.makeText(LoginActivity.this,"网络连接不可用",Toast.LENGTH_SHORT).show();
            }
            else {
                String usernameSql = "select * from user where tell=\"" + username.getText() + "\" or name = \"" + username.getText() + "\"";
                Log.i(TAG,usernameSql);
                //String pswSql = "select password from login where password =\"" + psw.getText() + "\"";
                Connection  conn = (Connection) DatabaseUtils.getConnection();
                try{
                    Statement st = conn.createStatement();
                    ResultSet resultSet = st.executeQuery(usernameSql);
                    Log.i(TAG,"登录数据库查询成功");
                    if (!resultSet.next()){
                        Log.i(TAG,"用户名不存在");
                        loginHandler.sendEmptyMessage(1);
                    }

                    else if (!resultSet.getString("password").equals(psw.getText().toString())){
                        System.out.println(resultSet.getString("password") == psw.getText().toString());
                        Log.i(TAG,"密码"+resultSet.getString("password"));
                        Log.i(TAG,"输入框"+psw.getText().toString());
                        Log.d(TAG,"密码错误");
                        loginHandler.sendEmptyMessage(2);
                    }
                    else{
                        Log.i(TAG,"成功登录");
                        loginHandler.sendEmptyMessage(3);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            Looper.loop();
        }
    }
}
