package com.test.feiyun.demo.Data;

import java.util.Date;

/**
 * Created by 飞云 on 2017/2/16.
 */

public class RecordBrief {
    private Date date;
    private String num;
    private String content;
    private int rcdID;

    public int getRcdID() {
        return rcdID;
    }

    public void setRcdID(int rcdID) {
        this.rcdID = rcdID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RecordBrief) {
            RecordBrief u = (RecordBrief) obj;
            return this.getRcdID()== u.getRcdID();
        }
        return super.equals(obj);
    }
}
