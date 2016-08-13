package com.test.feiyun.demo;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends FragmentActivity implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup group;
    private RadioButton btn_home;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        fragmentManager = getSupportFragmentManager();
        btn_home.setChecked(true);//首页默认选中
        group.setOnCheckedChangeListener(this);
        changedFragment(new HomeFragment(),false);//设置首页默认选中
    }

    private void initView() {
        group = (RadioGroup) findViewById(R.id.main_bottom_tabs);
        btn_home = (RadioButton) findViewById(R.id.btn_home);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.btn_home://首页
                changedFragment(new HomeFragment(),true);
                break;
            case R.id.btn_diet://食物
                changedFragment(new DietFragment(),true);
             /*   doSearch();*/
                break;
            case R.id.btn_publish://发布

                changedFragment(new PublishFragment(),true);
                break;
            case R.id.btn_sport://运动
                changedFragment(new SportFragment(),true);
                break;
            case R.id.btn_record://记录
                changedFragment(new RecordFragment(),true);
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
