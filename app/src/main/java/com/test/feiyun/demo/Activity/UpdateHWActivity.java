package com.test.feiyun.demo.Activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.test.feiyun.demo.Data.Data;
import com.test.feiyun.demo.DatabaseUtils;
import com.test.feiyun.demo.NetWorkUtils;
import com.test.feiyun.demo.R;
import com.test.feiyun.demo.Utils;

public class UpdateHWActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_confirm;
    private EditText height,weight;
    private ProgressDialog loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_hw);
        initView();
    }
//更新数据
    private void updateData() {
        if (height.getText().length() == 0){
            Toast.makeText(getBaseContext(),"请输入身高",Toast.LENGTH_SHORT).show();
        }
        else if (weight.getText().length() == 0){
            Toast.makeText(getBaseContext(),"请输入体重",Toast.LENGTH_SHORT).show();
        }else {
            //loading = ProgressDialog.show(UpdateHWActivity.this,"温馨提示","数据更新中，请稍后...",false);
            if (!NetWorkUtils.isNetworkConnected(getBaseContext())){
                Toast.makeText(getBaseContext(),"网络错误",Toast.LENGTH_SHORT).show();
            }
            else {
                final int iHeight = Integer.parseInt(height.getText().toString());
                final int iWeight = Integer.parseInt(weight.getText().toString());
                float fHeight = (float) iHeight / 100;
                float bmi = (float)iWeight / (fHeight * fHeight);
                Data.setHeight(iHeight);
                Data.setWeight(iWeight);
                Data.setBMI(bmi);
                Log.i("BMI",bmi+"");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String sql = "update user set weight = " + iWeight + ", height = " + iHeight + " where userId = " + Data.getUserID();
                        Log.i("sql",sql);
                        String udSql = "update userPetClo set cId = 0 where userId = " + Data.getUserID();
                        DatabaseUtils.executeSql(udSql);
                        DatabaseUtils.executeSql(sql);
                        Data.setIsWear(false);
                        //loading.dismiss();
                    }
                }).start();
                UpdateHWActivity.this.finish();
                Toast.makeText(getBaseContext(),"数据更新成功",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initView() {
        btn_confirm = (Button) findViewById(R.id.update_confirm);
        height = (EditText) findViewById(R.id.update_height);
        weight = (EditText) findViewById(R.id.update_weight);
        btn_confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.update_confirm:
                updateData();
        }
    }
}
