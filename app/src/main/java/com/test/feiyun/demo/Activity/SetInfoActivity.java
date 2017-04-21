package com.test.feiyun.demo.Activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.feiyun.demo.Data.Data;
import com.test.feiyun.demo.DatabaseUtils;
import com.test.feiyun.demo.NetWorkUtils;
import com.test.feiyun.demo.R;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class SetInfoActivity extends AppCompatActivity {

    private LinearLayout select_birth,select_gender;
    private TextView birth,gender;
    private String[] sexArry = new String[] { "女孩", "男孩" };// 性别选择
    private int intGender;
    private Date birthday;
    private Button confirm;
    private RegisterThread thread;
    private ProgressDialog dialog;
    private EditText username,email,height,weight;
    //(name,password,tell,mail,height,weight,sex,sign,money,birthday)
    private String password,tell;
    private int money;
    private Handler usernameHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_info);
        initView();
        thread = new RegisterThread();
        select_gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSexChooseDialog();
            }
        });
        select_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                thread.start();
                doSaveInfo();
            }
        });
    }

    private void doSaveInfo() {
        if (username.getText().length() == 0){
            Toast.makeText(getBaseContext(),"请设置你的用户名",Toast.LENGTH_SHORT).show();
        }
        else if (email.getText().length() == 0){
            Toast.makeText(getBaseContext(),"请设置您的邮箱地址",Toast.LENGTH_SHORT).show();
        }
        else if (height.getText().length() == 0){
            Toast.makeText(getBaseContext(),"请输入你的身高",Toast.LENGTH_SHORT).show();
        }
        else if (weight.getText().length() == 0){
            Toast.makeText(getBaseContext(),"请输入你的体重",Toast.LENGTH_SHORT).show();
        }
        else if (gender.getText().length() == 0){
            Toast.makeText(getBaseContext(),"请设置你的性别",Toast.LENGTH_SHORT).show();
        }
        else if (birth.getText().length() == 0){
            Toast.makeText(getBaseContext(),"请设置你的生日",Toast.LENGTH_SHORT).show();
        }
        else {
            dialog = ProgressDialog.show(SetInfoActivity.this, "温馨提示", "注册中，请稍等...", false);
            usernameTestThread usernameThread = new usernameTestThread();
            usernameThread.start();
            usernameHandler = new Handler(){
                @Override
                public void handleMessage(Message msg){
                    switch (msg.what){
                        case 5:
                            dialog.dismiss();
                            Toast.makeText(getBaseContext(),"用户名已存在",Toast.LENGTH_SHORT).show();
                            Log.i("username_test","收到消息5");
                            break;
                        case 6:
                            dialog.dismiss();
                            initData();
                            thread.start();
                            Toast.makeText(getBaseContext(),"信息保存成功",Toast.LENGTH_SHORT).show();
                            Data.setPhone(tell);
                            startActivity(new Intent(SetInfoActivity.this,MainActivity.class));
                            SetInfoActivity.this.finish();
                            Log.i("username_test","收到消息6");
                            break;

                        default:
                            break;
                    }
                }
            };

        }
    }

    private void initView() {
        select_birth = (LinearLayout) findViewById(R.id.select_birth);
        select_gender = (LinearLayout) findViewById(R.id.select_gender);
        birth = (TextView) findViewById(R.id.birth);
        gender = (TextView) findViewById(R.id.gender);
        confirm = (Button) findViewById(R.id.confirm_to_save);
        username = (EditText) findViewById(R.id.user_name);
        email = (EditText) findViewById(R.id.email);
        height = (EditText) findViewById(R.id.height);
        weight = (EditText) findViewById(R.id.weight);
    }
    /* 性别选择框 */
    private void showSexChooseDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);// 自定义对话框
        builder.setSingleChoiceItems(sexArry, 0, new DialogInterface.OnClickListener() {// 2默认的选中

            @Override
            public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                // showToast(which+"");
                gender.setText(sexArry[which]);
                intGender = which;
                dialog.dismiss();// 随便点击一个item消失对话框，不用点击确认取消
            }
        });
        builder.show();// 让弹出框显示
    }

//     “日期”按钮onClick

   protected void showDatePickerDialog() {
    Calendar calendar = Calendar.getInstance();
    DatePickerDialog datePickerDialog = new DatePickerDialog(SetInfoActivity.this, new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear+=1;
            birth.setText(year + "-" + monthOfYear + "-" + dayOfMonth);
            SimpleDateFormat simFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                birthday = simFormat.parse(birth.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    datePickerDialog.show();
    }
    //进行数据库插入
    class RegisterThread extends Thread{
        @Override
        public void run(){
            Looper.prepare();
            //生成日期对象 SimpleDateFormat.format(birthday.getTime())
            SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String sql="insert into user(name,password,tell,mail,height,weight,sex,money,birthday,registerDate) values(\""+username.getText()+"\","+ "\"" +password+ "\","+ "\"" +tell+ "\","+ "\"" +email.getText()+ "\","+ Integer.parseInt(height.getText().toString()) + "," + Integer.parseInt(weight.getText().toString()) + "," + intGender + "," + money + "," + "\"" +SimpleDateFormat.format(birthday.getTime())+"\",\"" + SimpleDateFormat.format(new Date())+"\")";
            Log.i("test123",sql);
            DatabaseUtils.executeSql(sql);
//            float fWeight = (float)Integer.parseInt(weight.getText().toString());
//            float fHeight = ((float)Integer.parseInt(height.getText().toString()))/100;
//            float bmi = fWeight / (fHeight * fHeight);
//            int shape = 0;
//            if (bmi < 24){
//               shape =1;
//                Log.i(TAG,"正常"+ Data.getCurrentUserShape());
//            }
//            else if (bmi >= 24 && bmi <= 27 ){
//               shape = 2;
//                Log.i(TAG,"过重"+ Data.getCurrentUserShape());
//            }
//            else if(bmi > 27 ){
//                shape = 3;
//                Log.i(TAG,"肥胖"+ Data.getCurrentUserShape());
//            }
//            String defaultPetSql = "insert into userPetClo values(";
            Looper.loop();
//            try {
//                Data.setBirth(SimpleDateFormat.parse(birth.getText().toString()));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            Data.setHeight(Integer.parseInt(height.getText().toString()));
//            Data.setWeight(Integer.parseInt(weight.getText().toString()));
//            Data.setGender(intGender);
//            Data.setUsername(username.getText().toString());
        }
    }

    class usernameTestThread extends Thread{
        @Override
        public void run() {
            Looper.prepare();
            if (!NetWorkUtils.isNetworkConnected(getBaseContext())){
                Toast.makeText(SetInfoActivity.this,"网络连接不可用",Toast.LENGTH_SHORT).show();
                Log.i(TAG,"网络连接不可用");
            }
            else {
                String sql = "select * from user";
                Boolean isDup = false;
                ResultSet rs = DatabaseUtils.getResultSet(sql);
                try {
                    while (rs.next()){
                        if (rs.getString("name").equals(username.getText().toString())){
                            isDup = true;
                        }
                    }
                    if (isDup){
                        usernameHandler.sendEmptyMessage(5);
                        Log.i("username_test","发出消息5");
                    }
                    else {
                        usernameHandler.sendEmptyMessage(6);
                        Log.i("username_test","发出消息6");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            Looper.loop();
        }
    }
    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        password = bundle.getString("password");
        tell = bundle.getString("phone");
        Log.i("test123",password);
        Log.i("test123",tell);
        money = 100;
    }
}
