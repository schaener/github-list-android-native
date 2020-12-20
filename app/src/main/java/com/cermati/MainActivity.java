package com.cermati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.cermati.Adapter.UsersAdapter;
import com.cermati.model.UsersModel;
import com.github.ybq.android.spinkit.SpinKitView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    static final String URL_DATA = "https://api.github.com/users?since=";
    static final String URL_DATA_SEARCH = "https://api.github.com/users/";
    EditText search;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    List<UsersModel> usersLists;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageView notfoundImg,close;
    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems,per_page =15,page=1;
    LinearLayoutManager manager;
    SpinKitView progress;
    ConnectivityManager conMgr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializedValue();
        widgetHandler();


    }

    @Override
    protected void onStart() {
        super.onStart();
        connectionChecking();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectionChecking();
    }

    public void initializedValue(){
    usersLists = new ArrayList<>();
    search = findViewById(R.id.ETsearch);
    close = findViewById(R.id.Bclose);
    progress =  findViewById(R.id.spin_kit);
    notfoundImg=findViewById(R.id.notFoundImg);
    recyclerView = findViewById(R.id.RVusers);
    swipeRefreshLayout =  findViewById(R.id.swipe);

}
public Boolean connectionChecking(){
        Boolean connect;
    conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    {
        if (conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected()) {
            loadUrlData(page,per_page);
            connect=true;
              } else {
            Toast.makeText(getApplicationContext(), getString(R.string.connection),
                    Toast.LENGTH_LONG).show();
            notfoundImg.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
            connect=false;
        }
    }
    return connect;
}
public void  widgetHandler(){
    close.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (search.getText().toString().length()>0){
                search.setText("");
                close.setVisibility(View.GONE);
            }
        }
    });

    search.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (connectionChecking()){
                if (search.getText().toString().isEmpty()){
                    usersLists.clear();
                    close.setVisibility(View.INVISIBLE);
                }
                if (search.getText().toString().length()>0){

                    close.setVisibility(View.VISIBLE);
                }
                loadUrlData(page,per_page);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    });

    manager = new LinearLayoutManager(this);
    adapter = new UsersAdapter(usersLists, getApplicationContext());
    recyclerView.setLayoutManager(manager);
    recyclerView.setAdapter(adapter);
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
            {
                isScrolling = true;
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (connectionChecking()) {
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollOutItems = manager.findFirstVisibleItemPosition();
                if (isScrolling && (currentItems + scrollOutItems == totalItems)) {
                    isScrolling = false;
                    per_page = per_page + 5;
                    page = page + 1;
                    loadUrlData(page, per_page);
                }
            }

        }

    });
    swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blues);
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            loadUrlData(1,15);

            swipeRefreshLayout.setRefreshing(false);
        }
    });



}

    private void loadUrlData(int page,int per_page) {

        String urlCheck;
    if (search.getText().toString().isEmpty()){
     urlCheck = URL_DATA+page+"&per_page="+per_page;
    }
    else {
    urlCheck = URL_DATA_SEARCH+search.getText().toString();
    }
    swipeRefreshLayout.setRefreshing(true);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                 urlCheck, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {



                try {
    notfoundImg.setVisibility(View.INVISIBLE);
    if (!search.getText().toString().isEmpty()){
         usersLists.clear();
          JSONObject obj = new JSONObject(response);




        UsersModel users = new UsersModel(obj.getString(getString(R.string.avatar_name))
                ,obj.getString(getString(R.string.avatar_url)));
        usersLists.add(users);
    swipeRefreshLayout.setRefreshing(false);
    progress.setVisibility(View.GONE);
    swipeRefreshLayout.setVisibility(View.VISIBLE);
    notfoundImg.setVisibility(View.GONE);
    }
    else{
    JSONArray array = new JSONArray(response);

    for (int i = 0; i < array.length(); i++){

        JSONObject jo = array.getJSONObject(i);

        UsersModel users = new UsersModel(jo.getString(getString(R.string.avatar_name))
                ,jo.getString(getString(R.string.avatar_url)));
        usersLists.add(users);

        }


                    }


                  adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    progress.setVisibility(View.GONE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    notfoundImg.setVisibility(View.GONE);
                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse.statusCode==403){
                    Toast.makeText(MainActivity.this, getString(R.string.limit_access), Toast.LENGTH_SHORT).show();

                }
                else {
                    Toast.makeText(MainActivity.this, getString(R.string.user_notFound), Toast.LENGTH_SHORT).show();

                }
        swipeRefreshLayout.setRefreshing(false);
         swipeRefreshLayout.setVisibility(View.GONE);
        notfoundImg.setVisibility(View.VISIBLE);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}