package com.example.fortest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fortest.Adapter.MyFoodAdapter;
import com.example.fortest.Listener.ICartLoadListener;
import com.example.fortest.Listener.IFoodLoadListener;
import com.example.fortest.eventBus.MyUpdateCartEvent;
import com.example.fortest.model.CartModel;
import com.example.fortest.model.FoodModel;
import com.example.fortest.utils.SpaceItemDecoration;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ICartLoadListener, IFoodLoadListener{



    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.mainLayout)
    LinearLayout mainLayout;

    @BindView(R.id.badge)
    NotificationBadge badge;

    @BindView(R.id.btnCart)
    FrameLayout btnCart;

    String str;
    IFoodLoadListener foodLoadListener;
    ICartLoadListener cartLoadListener;


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {

        if(EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class));
        EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
        super.onStop();

    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)

    public void onUpdateCart(MyUpdateCartEvent event){
        countCartItem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Button logout= findViewById(R.id.logoutBtn);
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(getApplicationContext(),Login.class));
//                finish();
//            }
//        });

        //restaurant Key
        str = getIntent().getStringExtra("restaurantName");

        try {
            init();
            LoadFoodFormFirebase();
            countCartItem();
        }
        catch (Exception e){
            System.out.println("error is thissssss: "+e.getMessage());
        }
    }



    //Load Menu
    private void LoadFoodFormFirebase() {


        List<FoodModel> foodModels = new ArrayList<>();
     //   FirebaseDatabase.getInstance().getReference("Drink")
        //where to put the data in DB
        DatabaseReference df = FirebaseDatabase
                .getInstance()
                .getReference("menu");

        //key of that specific restaurant
        //When restaurant is Clicked
        df.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists())
                        {
                            for(DataSnapshot foodSnapshot:snapshot.getChildren())
                            {

                                FoodModel foodModel= foodSnapshot.getValue(FoodModel.class);
                                foodModel.setKey(foodSnapshot.getKey());
                                foodModels.add(foodModel);

                            }
                            foodLoadListener.onFoodLoadSuccess(foodModels);
                        }
                        else{
                            foodLoadListener.onFoodLoadFailed("Can't find Food");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        foodLoadListener.onFoodLoadFailed(error.getMessage());

                    }
                });
    }

    private void init(){

        ButterKnife.bind(this);

        foodLoadListener=this;
        cartLoadListener=this;

        GridLayoutManager gridLayoutManager= new GridLayoutManager(this,2);
        recycler.setLayoutManager(gridLayoutManager);
        recycler.addItemDecoration(new SpaceItemDecoration());
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                intent.putExtra("restaurantName",str);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onCartLoadSuccess(List<CartModel> CartModelList) {

        int cartSum=0;
        for(CartModel cartModel: CartModelList){
            cartSum+=cartModel.getQuantity();
            badge.setNumber(cartSum);
        }

    }

    @Override
    public void onCartLoadFailed(String message) {

        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();

    }

    @Override
    protected void onResume(){
        super.onResume();
        countCartItem();
    }

    private void countCartItem() {
        //Get Current User
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        List<CartModel> cartModels = new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot cartSnapshot: snapshot.getChildren()){

                            CartModel cartModel= cartSnapshot.getValue(CartModel.class);
                            cartModel.setKey(cartSnapshot.getKey());
                            cartModels.add(cartModel);
                        }
                        cartLoadListener.onCartLoadSuccess(cartModels);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }

    @Override
    public void onFoodLoadSuccess(List<FoodModel> foodModelList) {
        MyFoodAdapter adapter=new MyFoodAdapter(this,foodModelList,cartLoadListener,str);
        recycler.setAdapter(adapter);
    }

    @Override
    public void onFoodLoadFailed(String message) {
        Snackbar.make(mainLayout,message, Snackbar.LENGTH_LONG).show();
    }
}