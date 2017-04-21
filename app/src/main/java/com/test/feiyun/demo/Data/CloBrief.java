package com.test.feiyun.demo.Data;

/**
 * Created by 飞云 on 2017/3/20.
 */

public class CloBrief {
    private String size;
    private int iSize;
    private int price;
    private String path;
    private boolean isbuy = false;
    private boolean iswear = false;
    private int clothID;

    public int getClothID() {
        return clothID;
    }

    public void setClothID(int clothID) {
        this.clothID = clothID;
    }
    public int getiSize() {
        return iSize;
    }

    public void setiSize(int iSize) {
        this.iSize = iSize;
    }


    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isbuy() {
        return isbuy;
    }

    public void setIsbuy(boolean isbuy) {
        this.isbuy = isbuy;
    }

    public boolean iswear() {
        return iswear;
    }

    public void setIswear(boolean iswear) {
        this.iswear = iswear;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CloBrief) {
            CloBrief u = (CloBrief) obj;
            return this.getClothID() == u.getClothID();
        }
        return super.equals(obj);
    }
}
