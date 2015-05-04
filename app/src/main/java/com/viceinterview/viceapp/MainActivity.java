package com.viceinterview.viceapp;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.viceinterview.viceapp.Adapter.DataItem;
import com.viceinterview.viceapp.Adapter.EndlessScrollListener;
import com.viceinterview.viceapp.Adapter.myArrayAdapter;
import com.viceinterview.viceapp.Display.ImageActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;


public class MainActivity extends Activity {

    public myArrayAdapter adapter;
    ArrayList<DataItem> list;
    StaggeredGridView gridView;

    private ClientInterface service;

    public String searchString;
    public ArrayList<String> searchHistory;


    public int count;

    EndlessScrollListener scrollLoadListener;

    int rSize = 8;
    int initIncrements = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchHistory = new ArrayList<String>();
        this.count = 0;
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://ajax.googleapis.com")
                .build();
        this.service = restAdapter.create(ClientInterface.class);

        gridView = (StaggeredGridView) findViewById(R.id.grid_view);

        final Activity activity = this;
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                    ImageView image = (ImageView) view.findViewById(R.id.image);

                    Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
                    if (bitmap == null) {
                        return;
                    }

                    String transitionName = "sharedElement";
                    image.setTransitionName(transitionName);
                    getWindow().setSharedElementExitTransition(new Explode());

                    Log.d("...........", "transition name from image: " + image.getTransitionName());

                    Intent intent = new Intent(activity, ImageActivity.class);
                    ByteArrayOutputStream bs = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                    intent.putExtra("byteArray", bs.toByteArray());

                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(activity, image, transitionName);
                    startActivity(intent, options.toBundle());
                }
            }
        });

        this.scrollLoadListener = new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

                Log.d("......", "OnLoadMore");
                final Callback callback1 = new Callback() {
                    @Override
                    public void success(Object o, retrofit.client.Response response) {

                        if (o.getClass() == LinkedTreeMap.class) {
                            Gson gson = new Gson();
                            String responseJson = gson.toJson(o);
                            final JSONObject jsonList;
                            try {
                                jsonList = new JSONObject(responseJson);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        addResponse(jsonList);
                                    }
                                });
                            } catch (JSONException e) {
                                Log.d("......", "JSONException: " + e.getMessage());

                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("......", "ONLoadMoreNetworkError: " + error.getMessage());
                    }
                };
                count += 1;
                Runnable networkRun = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            service.search(1.0f, searchString, rSize, rSize * initIncrements + (count * rSize), InetAddress.getLocalHost().getHostAddress(), "small|medium", callback1);
                        }
                        catch(UnknownHostException e){
                            service.search(1.0f, searchString, rSize, rSize * initIncrements + (count * rSize), null, "small|medium",callback1);

                        }
                    }
                };
                Thread networkThread = new Thread(networkRun);
                networkThread.start();


            }
        };

        gridView.setOnScrollListener(this.scrollLoadListener);

        this.list = new ArrayList<DataItem>();
        adapter = new myArrayAdapter(this, android.R.layout.simple_list_item_1, this.list);
        gridView.setAdapter(this.adapter);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("history", searchHistory);
        outState.putString("search", this.searchString);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(".......", "onRestoreInstanceState");

        super.onRestoreInstanceState(savedInstanceState);
        searchHistory = (ArrayList<String>)savedInstanceState.getSerializable("history");
        this.searchString = savedInstanceState.getString("search");
        searchForText(this.searchString);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(".......", "onConfigurationChanged, searchString: " + this.searchString);

    }

    public void searchForText(final String searchText){

        clearList();
        this.searchString = searchText;
        if (!searchHistory.contains(searchText)) {
            searchHistory.add(searchText);
        }

        count = 0;

        final Callback callback = new Callback() {
            @Override
            public void success(Object o, retrofit.client.Response response) {
                String responseString = null;

                if (o.getClass() == LinkedTreeMap.class){
                    Gson gson = new Gson();

                    responseString = gson.toJson(o);

                }

                Log.d("..........", "response String: " + responseString);


                try {
                    if (responseString != null) {
                        final JSONObject jsonList = new JSONObject(responseString);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateAdapter(jsonList);

                            }
                        });
                        for (int i = 1; i < initIncrements; i ++){
                            final Callback callback1 = new Callback() {
                                @Override
                                public void success(Object o, retrofit.client.Response response) {
                                    if (o.getClass() == LinkedTreeMap.class){
                                        Gson gson = new Gson();

                                        String responseJson = gson.toJson(o);
                                        final JSONObject jsonList;
                                        try {
                                            jsonList = new JSONObject(responseJson);
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addResponse(jsonList);

                                                }
                                            });

                                        }
                                        catch(JSONException e){

                                        }
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {

                                }
                            };
                            final int j = i;
                            Runnable networkRun = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        service.search(1.0f, searchText, rSize, j * rSize, InetAddress.getLocalHost().getHostAddress(),"small|medium", callback1);
                                    }
                                    catch(UnknownHostException e){
                                        service.search(1.0f, searchText, rSize, j * rSize, null, "small|medium",callback1);

                                    }
                                }
                            };
                            Thread networkThread = new Thread(networkRun);
                            networkThread.start();

                        }
                    }
                }
                catch (JSONException e){
                    Log.d(".......", "JSONException: " + e.getMessage());
                    return;
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(".......", "NetworkFailure: " + error.getMessage());
            }
        };

        Log.d(".......", "searchText: " + searchText);

        Runnable netowkrRun = new Runnable() {
            @Override
            public void run() {
                try {
                    service.search(1.0f, searchText, rSize, 0, InetAddress.getLocalHost().getHostAddress(), "small|medium",callback);
                }
                catch(UnknownHostException e){
                    service.search(1.0f, searchText, rSize, 0, null, "small|medium",callback);

                }
            }
        };
        Thread networkThread = new Thread(netowkrRun);
        networkThread.start();


    }

    public void updateAdapter(JSONObject jsonResponse) {

        ArrayList<String> urls = new ArrayList<String>();
        ArrayList<Integer> widths = new ArrayList<Integer>();
        ArrayList<Integer> heights = new ArrayList<Integer>();

        try {
            JSONObject dataArray = (JSONObject) jsonResponse.get("responseData");
            JSONArray results = (JSONArray) dataArray.get("results");
            Log.d("......", "resultsSize: " + results.length());

            for (int i = 0; i < results.length(); i++) {
                JSONObject dataItem = results.getJSONObject(i);
                if (dataItem.has("unescapedUrl")) {
                    String url = (String) dataItem.get("unescapedUrl");
                    urls.add(url);
                }
                if (dataItem.has("width")){
                    Integer width = Integer.parseInt((String)dataItem.get("width"));
                    widths.add(width);
                }
                if (dataItem.has("height")){
                    Integer height = Integer.parseInt((String)dataItem.get("height"));
                    heights.add(height);
                }

            }
        } catch (JSONException e) {
            Log.d(".......", "JSONException: " + e.getMessage());

        }

        Log.d(".......", "urls.size(): " + urls.size());

        for (int i = 0; i < urls.size(); i++) {
            this.list.add(new DataItem(urls.get(i), widths.get(i), heights.get(i)));


        }

        Log.d(".......", "updateAdapter");
//        adapter.notifyDataSetChanged();

    }

    public void addResponse(JSONObject jsonResponse){
        Log.d("......", "addResponse" );

        ArrayList<String> urls = new ArrayList<String>();
        ArrayList<Integer> widths = new ArrayList<Integer>();
        ArrayList<Integer> heights = new ArrayList<Integer>();


        try{
            JSONObject dataArray = (JSONObject)jsonResponse.get("responseData");
            JSONArray results = (JSONArray)dataArray.get("results");
            Log.d("......", "resultsSize: " + results.length());

            for (int i = 0; i < results.length(); i ++){
                JSONObject dataItem = results.getJSONObject(i);
                if (dataItem.has("unescapedUrl")){
                    String url = (String)dataItem.get("unescapedUrl");
                    urls.add(url);
                }
                if (dataItem.has("width")){
                    Integer width = Integer.parseInt((String) dataItem.get("width"));
                    widths.add(width);
                }
                if (dataItem.has("height")){
                    Integer height = Integer.parseInt((String)dataItem.get("height"));
                    heights.add(height);
                }
            }
        }
        catch(JSONException e){
            Log.d(".......", "JSONException: " + e.getMessage());

        }

        Log.d(".......", "urls.size(): " + urls.size());

        for (int i = 0; i < urls.size(); i ++){
            this.list.add(new DataItem(urls.get(i), widths.get(i), heights.get(i)));

        }

        adapter.notifyDataSetChanged();
        for (int i = 0; i < this.list.size(); i ++){
            Log.d(".......", "data at position:  " + i + " : " + this.list.get(i).getUrl());

        }

    }

    DataItem[] concat(DataItem[] first, DataItem[] second) {
        List<DataItem> both = new ArrayList<DataItem>(first.length + second.length);
        Collections.addAll(both, first);
        Collections.addAll(both, second);
        return both.toArray(new DataItem[both.size()]);
    }


    public void clearList(){
        final Context context = this;
        adapter.clearColors();
        adapter.resetImageQueue();
        this.list.clear();
        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
