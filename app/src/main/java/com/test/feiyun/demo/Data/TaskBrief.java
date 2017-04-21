package com.test.feiyun.demo.Data;

/**
 * Created by 飞云 on 2017/3/10.
 */

public class TaskBrief {
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public Boolean getConfirm() {
        return isConfirm;
    }

    public void setConfirm(Boolean confirm) {
        isConfirm = confirm;
    }
    private String content;
    private int money;
    private Boolean isConfirm = false;
    private int taskID;
    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TaskBrief) {
            TaskBrief u = (TaskBrief) obj;
            return this.getContent().equals(u.getContent())
                    && this.getMoney() == u.getMoney()&&this.getTaskID() == u.getTaskID();
        }
        return super.equals(obj);
    }
}
