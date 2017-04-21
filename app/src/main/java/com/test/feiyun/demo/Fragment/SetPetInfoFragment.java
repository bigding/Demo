package com.test.feiyun.demo.Fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.test.feiyun.demo.Data.Data;
import com.test.feiyun.demo.Data.MyFragment;
import com.test.feiyun.demo.DatabaseUtils;
import com.test.feiyun.demo.NetWorkUtils;
import com.test.feiyun.demo.R;

import static android.content.Context.MODE_PRIVATE;

public class SetPetInfoFragment extends Fragment implements View.OnClickListener {
    private EditText petName,signature;
    private Button btnConfirm;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private FragmentManager fragmentManager;
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        return initView(inflater,container);
    }
//初始化界面控件函数
    private View initView(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.set_pg, container, false);
        petName = (EditText) view.findViewById(R.id.pet_name);
        signature = (EditText) view.findViewById(R.id.signature);
        btnConfirm = (Button) view.findViewById(R.id.pet_btn_confirm);
        btnConfirm.setOnClickListener(this);
        return view;
    }
//更新登录记录
private void updatePreferences() {
    preferences = getActivity().getSharedPreferences("LoginLog",MODE_PRIVATE);
    editor = preferences.edit();
    Log.i("isFirstLogin",""+preferences.getBoolean(Data.getUserID() + "isFirstLogin",true));
    if (Data.getUserID() != 0x123 && preferences.getBoolean(Data.getUserID() + "isFirstLogin",true)){
        boolean isFirstLogin = false;
        Log.i("TAG","updatesharedprefeences()");
        editor.putBoolean(Data.getUserID() + "isFirstLogin",isFirstLogin);
        editor.commit();
    }
}
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.pet_btn_confirm:
                /*
                宠物相关信息的设置
                 */
                if (!NetWorkUtils.isNetworkConnected(getContext())){
                    Toast.makeText(getContext(),"网络出错",Toast.LENGTH_SHORT).show();
                }
                else{
                    setPetInfo();
                    updatePreferences();
                    changedFragment(MyFragment.publishFragment,true);//从设置宠物界面切换到宠物显示界面
                    Log.i("test","<<<<<<<<<<<<<<<<<<<<<<<<<test<<<<<<<<<<<<<");
                }
        }
    }
    //设置宠物的信息
    private void setPetInfo() {
        if (petName.getText().length() == 0)
            Toast.makeText(getContext(),"请填写宠物昵称",Toast.LENGTH_SHORT).show();
        else if (signature.getText().length() == 0)
            Toast.makeText(getContext(),"请填写个性签名",Toast.LENGTH_SHORT).show();
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    String defaultPetSql = "insert into userPetClo values(" + Data.getUserID() + "," + Data.getUserPetID() + "," + 0 + "," + Data.getCurrentUserShape() + ")";
                    String sql = "insert into userPet values(" + Data.getUserID() + "," + Data.getUserPetID() + "," + Data.getCurrentUserShape() + ",\"" + petName.getText().toString() + "\",\"" + signature.getText().toString()+"\")";
                    DatabaseUtils.executeSql(sql);
                    DatabaseUtils.executeSql(defaultPetSql);
                    Log.i("sql",sql);
                    Looper.loop();
                }
            }).start();
        }
    }
    //切换不同的fragment
    public void changedFragment(Fragment fragment, boolean isFirst){
        fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transition = fragmentManager.beginTransaction();
        transition.replace(R.id.content,fragment);
        if (!isFirst){
            transition.addToBackStack(null);
        }
        transition.commit();
    }
}
