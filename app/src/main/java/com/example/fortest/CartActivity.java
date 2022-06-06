package com.example.fortest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fortest.Adapter.MyCartAdapter;
import com.example.fortest.Listener.ICartLoadListener;
import com.example.fortest.eventBus.MyUpdateCartEvent;
import com.example.fortest.model.CartModel;
import com.example.fortest.utils.SpaceItemDecoration;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartActivity extends AppCompatActivity implements ICartLoadListener {

    private FirebaseAuth mAuth;

    @BindView(R.id.recycler_cart)
    RecyclerView recyclerCart;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.txtTotal)
    TextView txtTotal;
    @BindView(R.id.orderBtn)
    Button order_btn;

    String mRestaurantKey;


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
        LoadCartFromFirebase();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        init();
        LoadCartFromFirebase();

        //restaurant Key
        mRestaurantKey = getIntent().getStringExtra("restaurantName");


    }

    private void LoadCartFromFirebase() {
        List<CartModel> cartModels = new ArrayList<>();

        //Get Current User
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){

                            for(DataSnapshot cartSnapshot: snapshot.getChildren()){

                                CartModel cartModel = cartSnapshot.getValue(CartModel.class);
                                cartModel.setKey(cartSnapshot.getKey());
                                //cartModel.restaurantKey = mRestaurantKey;
                                cartModels.add(cartModel);
                            }
                            cartLoadListener.onCartLoadSuccess(cartModels);

                        }else{
                            cartLoadListener.onCartLoadFailed("Cart Empty");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }

    private void init(){

        ButterKnife.bind(this);
        cartLoadListener = this;
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        recyclerCart.setLayoutManager(layoutManager);
        recyclerCart.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        btnBack.setOnClickListener(v-> {
            finish();
        });
        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CartActivity.this, orderActivity.class);
                intent.putExtra("restaurantName",mRestaurantKey);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onCartLoadSuccess(List<CartModel> CartModelList) {
        double sum=0;
                for(CartModel cartModel: CartModelList){
                    sum+=cartModel.getTotalPrice();
                }
                txtTotal.setText(new StringBuilder("$").append(sum));
                MyCartAdapter adapter= new MyCartAdapter(this,CartModelList);
                recyclerCart.setAdapter(adapter);
    }

    @Override
    public void onCartLoadFailed(String message) {

        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();
    }
}