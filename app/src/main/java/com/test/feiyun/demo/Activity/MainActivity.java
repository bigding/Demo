package com.test.feiyun.demo.Activity;

import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.test.feiyun.demo.Data.Data;
import com.test.feiyun.demo.Data.MyFragment;
import com.test.feiyun.demo.DatabaseUtils;
import com.test.feiyun.demo.Fragment.DietFragment;
import com.test.feiyun.demo.Fragment.HomeFragment;
import com.test.feiyun.demo.Fragment.PublishFragment;
import com.test.feiyun.demo.Fragment.SetPetInfoFragment;
import com.test.feiyun.demo.NetWorkUtils;
import com.test.feiyun.demo.R;
import com.test.feiyun.demo.Fragment.RecordFragment;
import com.test.feiyun.demo.Fragment.SportFragment;
import com.test.feiyun.demo.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private RadioGroup group;
    private RadioButton btn_home;
    private FragmentManager fragmentManager;
    public static String TAG = "TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        preferences = getSharedPreferences("LoginLog",MODE_PRIVATE);
        editor = preferences.edit();
        //new userIDThread().start();//设置userID
        fragmentManager = getSupportFragmentManager();
        btn_home.setChecked(true);//首页默认选中
        group.setOnCheckedChangeListener(this);
        changedFragment(new HomeFragment(),false);//设置首页默认选中
    }
    @Override
    protected void onResume(){
        Log.i("onResume","onResume");
        if (!NetWorkUtils.isNetworkConnected(getBaseContext())){
            Toast.makeText(MainActivity.this,"网络连接不可用",Toast.LENGTH_SHORT).show();
            Log.i(TAG,"网络连接不可用");
        }
        else {
            new userIDThread().start();//设置userID
        }
        //Log.i("onResume",Data.getPhone());
        super.onResume();
    }

    //获取userID
    class userIDThread extends Thread{
        @Override
        public void run(){
            Looper.prepare();
            if (!NetWorkUtils.isNetworkConnected(getBaseContext())){
                Toast.makeText(MainActivity.this,"网络连接不可用",Toast.LENGTH_SHORT).show();
                Log.i(TAG,"网络连接不可用");
            }
            else {
                String sql = "select * from user where tell=\"" + Data.getPhone() + "\" or name =\""+ Data.getUsername() + "\"";
                ResultSet rs = DatabaseUtils.getResultSet(sql);
                //ResultSet rs_pet = DatabaseUtils.getResultSet(sql_pet);
                try {
                    while (rs.next()){
                        Log.i("userid",""+rs.getInt("userId"));
                        Data.setPhone(rs.getString("tell"));
                        Data.setUserID(rs.getInt("userId"));
                        Data.setHeight(rs.getInt("height"));
                        Data.setWeight(rs.getInt("weight"));
                        Data.setGender(rs.getInt("sex"));
                        Data.setUsername(rs.getString("name"));
                        Data.setMoney(rs.getInt("money"));
                        Data.setBMI(rs.getInt("weight")/((rs.getInt("height")*rs.getInt("height"))/10000));
                        Data.setRegisterDate(rs.getDate("registerDate"));
                        //DecimalFormat df = new DecimalFormat("#.0");
                        float height = (float) rs.getInt("height") / 100;
                        float bmi = (float) rs.getInt("weight")/ height / height;
                        if (bmi < 24){
                            Data.setCurrentUserShape(1);
                            Log.i(TAG,"正常"+ Data.getCurrentUserShape());
                        }
                        else if (bmi >= 24 && bmi <= 27 ){
                            Data.setCurrentUserShape(2);
                            Log.i(TAG,"过重"+ Data.getCurrentUserShape());
                        }
                        else if(bmi > 27 ){
                            Data.setCurrentUserShape(3);
                            Log.i(TAG,"肥胖"+ Data.getCurrentUserShape());
                        }
                        /**--------------------------宠物暂时固定---------------------**/
                        Data.setUserPetID(17);
                        Data.setBMI(bmi);

                        //更新宠物体型信息
                        String sql_pet = "update userPet set shape = " + Data.getCurrentUserShape() + " where userId = " + Data.getUserID();
                        DatabaseUtils.executeSql(sql_pet);
                        Log.i("userid","height :" + height);
                        Log.i("userid"," bmi : "+ bmi);
                        Log.i("userid",Data.getRegisterDate()+" "+Data.getHeight()+" " + Data.getWeight() + " " + Data.getUserID()+ " "+ Data.getGender()+ " " + Data.getUsername()+" "+Data.getBMI());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private void initView() {
        group = (RadioGroup) findViewById(R.id.main_bottom_tabs);
        btn_home = (RadioButton) findViewById(R.id.btn_home);
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.btn_home://首页
                changedFragment(MyFragment.homeFragment,true);
                break;
            case R.id.btn_diet://食物
                changedFragment(MyFragment.dietFragment,true);
             /*   doSearch();*/
                break;
            case R.id.btn_publish://发布
                boolean isFirsLogin = preferences.getBoolean(Data.getUserID() + "isFirstLogin",true);
                Log.i(Data.getUserID() + "isFirstLogin",""+isFirsLogin);
                if (isFirsLogin){
                    changedFragment(MyFragment.setPetInfoFragment,true);
                }
                else
                changedFragment(MyFragment.publishFragment,true);
                break;
            case R.id.btn_sport://运动
                changedFragment(MyFragment.sportFragment,true);
                break;
            case R.id.btn_record://记录
                changedFragment(MyFragment.recordFragment,true);
                break;
            default:
                break;
        }
    }
    //切换不同的fragment
    public void changedFragment(Fragment fragment, boolean isFirst){
        FragmentTransaction transition = fragmentManager.beginTransaction();
        transition.replace(R.id.content,fragment);
        if (!isFirst){
            transition.addToBackStack(null);
        }
        transition.commit();
    }

}
