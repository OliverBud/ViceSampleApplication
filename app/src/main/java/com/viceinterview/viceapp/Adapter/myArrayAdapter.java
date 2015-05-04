package com.viceinterview.viceapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viceinterview.viceapp.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by oliverbud on 4/22/15.
 */
public class myArrayAdapter extends ArrayAdapter {

    Context context;
    public ArrayList<DataItem> values;
    public ImageManager imageManager;
    ViewHolder holder;

    ArrayList<Integer> colors;

    public static class ViewHolder{
        ImageView image;
        FrameLayout cellFrame;
        int backgroundColor = -1;
    }

    public myArrayAdapter(Context context, int resourceId, ArrayList<DataItem> values) {
        super(context, resourceId, values);
        this.context = context;
        this.values = values;
        colors = new ArrayList<Integer>();
        imageManager =
                new ImageManager(((Activity)context).getApplicationContext());
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.d("........", "getView: " + position);
        View cellView = convertView;

            Log.d("........", "convertView null");

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            cellView = inflater.inflate(R.layout.cell_layout, parent, false);
            holder = new ViewHolder();
            holder.cellFrame = (FrameLayout)cellView.findViewById(R.id.cellFrame);
            holder.image = (ImageView)cellView.findViewById(R.id.image);

            cellView.setTag(holder);


        if (colors.size() <= position || colors.get(position) == null) {
            Random rand = new Random();
            int r = rand.nextInt(255);
            int g = rand.nextInt(255);
            int b = rand.nextInt(255);
            int randomColor = Color.rgb(r, g, b);
            colors.add(position, randomColor);
        }

        int width = values.get(position).getWidth();
        int height = values.get(position).getHeight();

        Log.d("......", "position: " + position + "width: " + width + "height: " + height);


        holder.cellFrame.setBackgroundColor(colors.get(position));

        if (width != 0 && height != 0 ){
            holder.cellFrame.setLayoutParams(new ViewGroup.LayoutParams(300, (int)((300/(float)width) * height)));
            holder.image.setLayoutParams(new FrameLayout.LayoutParams(300, (int)((300/(float)width) * height)));
            holder.image.setBackgroundColor(Color.TRANSPARENT);
        }


        FrameLayout cellFrame = (FrameLayout) cellView.findViewById(R.id.cellFrame);


        Log.d("........", "position: "+ position);
        Log.d("........", "values.get(position).getUrl(): "+ values.get(position).getUrl());


        holder.image.setTag(values.get(position).getUrl());




        imageManager.displayImage(values.get(position), (Activity) this.context, holder);






        cellFrame.setTag(holder);


        return cellFrame;
    }

    public void clearColors(){
        colors.clear();

    }

    public void resetImageQueue(){
        imageManager.resetImageQueue();
    }

}
