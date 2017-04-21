package com.test.feiyun.demo.Activity;

import android.os.Looper;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class PublishRcdActivity extends AppCompatActivity implements View.OnClickListener {
    private Button publish;
    private EditText record;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_rcd);
        initView();
        publish.setOnClickListener(this);
    }

    private void initView() {
        record = (EditText) findViewById(R.id.record_content_to_publish);
        publish = (Button) findViewById(R.id.rcd_submit);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rcd_submit:
                if (record.getText().length() == 0){
                    Toast.makeText(getBaseContext(),"请输入内容后再发送",Toast.LENGTH_SHORT).show();
                }
                else if (Data.getUsername() == null || Data.getUserID() == 0x123){
                    Toast.makeText(getBaseContext(),"未登录状态不允许发送内容哦",Toast.LENGTH_SHORT).show();
                }
                else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Looper.prepare();
                            if (!NetWorkUtils.isNetworkConnected(getBaseContext())){
                                Toast.makeText(PublishRcdActivity.this,"网络连接不可用",Toast.LENGTH_SHORT).show();
                                Log.i(TAG,"网络连接不可用");
                            }
                            else {
                                Date date = new Date();
                                SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                String sql = null;
                                try {
                                    Log.i("sql_test",""+Data.getRegisterDate());
                                    sql = "insert into record (userId,rDetail,date,pastDays) values("+ Data.getUserID() + ",\"" + record.getText().toString()+"\",\""+SimpleDateFormat.format(date)+"\","+ Utils.daysBetween(Data.getRegisterDate(),date)+ ")";
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Log.i("sql_test",sql);
                                DatabaseUtils.executeSql(sql);
                                Toast.makeText(getBaseContext(),"发送成功",Toast.LENGTH_SHORT).show();
                                PublishRcdActivity.this.finish();
                            }
                            Looper.loop();
                        }
                    }).start();
                }
        }
    }
}
