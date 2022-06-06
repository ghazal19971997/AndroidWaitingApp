package com.example.fortest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.fortest.Adapter.RecycleViewAdapter;
import com.example.fortest.Listener.IRestaurantLoadListener;
import com.example.fortest.model.RestaurantModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RestaurantActivity extends AppCompatActivity implements IRestaurantLoadListener{

    private static String TAG = "RestaurantActivity";
    IRestaurantLoadListener restaurantLoadListener;
    List<RestaurantModel> restaurantList = new ArrayList<RestaurantModel>();
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        recyclerView = findViewById(R.id.recycleview);

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar();

        //RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


         fillRestaurantList();

    }


    private void fillRestaurantList() {

        List<RestaurantModel> restaurantModels = new ArrayList<RestaurantModel>();
        FirebaseDatabase.getInstance().getReference("Restaurants")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists())
                        {
                            for(DataSnapshot resSnapshot:snapshot.getChildren())
                            {
                                RestaurantModel restaurantModel= resSnapshot.getValue(RestaurantModel.class);

                                Log.e(TAG, "itme : " + resSnapshot);
                                restaurantModel.setKey(resSnapshot.getKey());
                                restaurantModels.add(restaurantModel);

                            }
                            onRestaurantLoadSuccess(restaurantModels);
                        }
                        else{
                            restaurantLoadListener.onRestaurantLoadFailed("Can't find any restaurant");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        restaurantLoadListener.onRestaurantLoadFailed(error.getMessage());

                    }
                });
    }

    public void onRestaurantLoadSuccess(List<RestaurantModel> restaurantModelList) {
        RecycleViewAdapter adapter= new RecycleViewAdapter(restaurantModelList,this,restaurantLoadListener);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRestaurantLoadFailed(String message) {

    }


    //menu
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {


            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                Toast.makeText(RestaurantActivity.this, "Search is Expanded", Toast.LENGTH_SHORT ).show();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                Toast.makeText(RestaurantActivity.this, "Search is Collapse", Toast.LENGTH_SHORT ).show();
                return true;
            }



        };
        menu.findItem(R.id.search).setOnActionExpandListener(onActionExpandListener);
        SearchView searchView =(SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("주소, 매징명, 메뉴명 검색");


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.distance) {
            Toast.makeText(getApplicationContext(), "You CLick 거리순",
                    Toast.LENGTH_SHORT).show();
        } else if (id == R.id.waiting) {
            Toast.makeText(getApplicationContext(), "You CLick 대기순",
                    Toast.LENGTH_SHORT).show();
        } else if (id == R.id.rating) {
            Toast.makeText(getApplicationContext(), "You CLick 평점순",
                    Toast.LENGTH_SHORT).show();
        } else if (id == R.id.search) {
            Toast.makeText(getApplicationContext(), "You CLick Search",
                    Toast.LENGTH_SHORT).show();
        }
        return true;
    }


}