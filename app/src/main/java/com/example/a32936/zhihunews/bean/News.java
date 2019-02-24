package com.example.a32936.zhihunews.bean;

import android.graphics.Bitmap;

public class News {
    private String name;
    private String image;
    private String url;
    private String id;

    public News() {
    }

    public void setName(String name){this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setImage(String image){
        this.image = image;
    }

    public String getImage(){
        return image;
    }

    public String getId(){ return id; }

    public void setId(String id) { this.id = id; }

    public String getUrl(){ return url; }

    public void setUrl(String url){ this.url = url; }
}
