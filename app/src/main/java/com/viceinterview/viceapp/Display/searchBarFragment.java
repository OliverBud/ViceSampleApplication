package com.viceinterview.viceapp.Display;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.viceinterview.viceapp.Adapter.myArrayAdapter;
import com.viceinterview.viceapp.ClientInterface;
import com.viceinterview.viceapp.MainActivity;
import com.viceinterview.viceapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by oliverbud on 5/2/15.
 */
public class searchBarFragment extends Fragment {

    private FrameLayout searchbarLayout;
    private EditText searchEditText;

    private Button button;

    private CardView card;
    private CardView historyList;
    ListView searchHistory;

    public TextView.OnEditorActionListener searchListener;

    private ClientInterface service;




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.searchbar_fragment, container, false);

        searchbarLayout = (FrameLayout)view;
        searchEditText = (EditText)view.findViewById(R.id.searchEditText);

        card = (CardView)view.findViewById(R.id.cardView);
        historyList = (CardView)view.findViewById(R.id.historyListCard);

        card.setCardElevation(20);
        historyList.setCardElevation(3);


        button = (Button)view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySearchHistory();
            }
        });

        searchListener = new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    ((MainActivity)getActivity()).searchForText(searchEditText.getText().toString().toLowerCase());

                }

                return false;
            }
        };

        searchEditText.setOnEditorActionListener(searchListener);

        return view;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://ajax.googleapis.com")
                .build();
        this.service = restAdapter.create(ClientInterface.class);

    }

    int mTargetHeight;

    boolean isExpanded = false;

    public void displaySearchHistory(){
        if (!isExpanded) {
            expand(historyList);
            isExpanded = true;
        }
        else{
            contract(historyList);
            isExpanded = false;
        }
    }

    public void expand(final View view)
    {
        mTargetHeight = 700;
        view.getLayoutParams().height = 0;

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t)
            {
                view.getLayoutParams().height = (int)(mTargetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(1000);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                searchHistory = new ListView(getActivity());
                CardView.LayoutParams slp = new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.BOTTOM);
                int lstMarginTop = (searchEditText.getHeight() + 15);
                slp.setMargins(0, lstMarginTop, 0, 0);
                final ArrayList<String> list = ((MainActivity)getActivity()).searchHistory;
                ArrayAdapter<String> listAdpter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
                searchHistory.setAdapter(listAdpter);

                searchHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final String searchText = list.get(position);
                        searchEditText.setText(searchText);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                ((MainActivity) getActivity()).searchForText(searchText.toLowerCase());

                            }
                        }, 600);
                        displaySearchHistory();

                    }
                });

                historyList.addView(searchHistory, slp);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(a);
    }

    public void contract(final View view)
    {
        mTargetHeight = 700;
        view.getLayoutParams().height = FrameLayout.LayoutParams.WRAP_CONTENT;

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t){
                view.getLayoutParams().height = (int)(mTargetHeight * (1 - interpolatedTime));
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        a.setDuration(600);
        a.setInterpolator(new AccelerateDecelerateInterpolator());
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                historyList.removeView(searchHistory);
                searchHistory = null;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(a);
    }

}
