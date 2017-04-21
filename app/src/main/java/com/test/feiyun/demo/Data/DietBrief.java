package com.test.feiyun.demo.Data;

/**
 * Created by 飞云 on 2017/2/16.
 */

public class DietBrief {
    private String title;
    private String info;
    private String pic;
    private String contentUrl;


    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DietBrief) {
            DietBrief u = (DietBrief) obj;
            return this.getContentUrl().equals(u.getContentUrl())
                    && this.getInfo().equals(u.getInfo())&&this.getPic().equals(u.getPic())&&this.getTitle().equals(u.getTitle());
        }
        return super.equals(obj);
    }
}
