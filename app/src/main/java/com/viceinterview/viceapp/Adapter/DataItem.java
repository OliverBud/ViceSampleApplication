package com.viceinterview.viceapp.Adapter;

/**
 * Created by oliverbud on 5/3/15.
 */
public class DataItem {

    String url ;
    int givenHeight;
    int givenWidth;
    int scaledHeight;
    int scaledWidth;

    public DataItem(String url){
        this.url = url;
    }

    public DataItem(String url, int width, int height){
        this.url = url;
        this.givenHeight = height;
        this.givenWidth = width;
    }

    public String getUrl(){
        return url;
    }

    public int getWidth(){
        return givenWidth;
    }

    public int getHeight(){
        return givenHeight;
    }

}
