package com.example.fortest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.fortest.Adapter.MyCartAdapter;
import com.example.fortest.Adapter.MyOrderAdapter;
import com.example.fortest.Listener.ICartLoadListener;
import com.example.fortest.eventBus.MyUpdateCartEvent;
import com.example.fortest.model.CartModel;
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

public class orderActivity extends AppCompatActivity implements ICartLoadListener{


    private FirebaseAuth mAuth;

    @BindView(R.id.recycler_order)
    RecyclerView recyclerOrder;
    @BindView(R.id.orderLayout)
    RelativeLayout orderLayout;
    @BindView(R.id.btnBack)
    ImageView btnBack;
    @BindView(R.id.txtTotal)
    TextView txtTotal;


    ICartLoadListener cartLoadListener;
    //Get Current User
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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
        setContentView(R.layout.activity_order);

        Button logout= findViewById(R.id.logoutBtn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
        });

        init();
        LoadCartFromFirebase();
    }

    private void LoadCartFromFirebase() {
        List<CartModel> cartModels = new ArrayList<>();

        //oon bala raft

        FirebaseDatabase.getInstance()
                .getReference("Cart") //Bring All orders from DB
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterable<DataSnapshot> groupsByUser = snapshot.getChildren();
                        //iterate through the list of group keys associated to the user
                        for (DataSnapshot groupDatasnapshot : groupsByUser) {
                            String groupId = groupDatasnapshot.getKey();
                            //Log.d("TAG","THE KEY ISSSSSSSSSSSS: "+ groupId);

                            FirebaseDatabase.getInstance()
                                    .getReference("Cart").child(groupId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            if(snapshot.exists()){

                                                for(DataSnapshot cartSnapshot: snapshot.getChildren()){

                                                    CartModel cartModel = cartSnapshot.getValue(CartModel.class);
                                                    cartModel.setKey(groupId);
                                                    cartModels.add(cartModel);
                                                }
                                                cartLoadListener.onCartLoadSuccess(cartModels);

                                            }else{
                                                cartLoadListener.onCartLoadFailed("Order is Empty");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            cartLoadListener.onCartLoadFailed(error.getMessage());
                                        }
                                    });

                        }

                }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    };


    });
    }


    private void init(){

        ButterKnife.bind(this);
        cartLoadListener = this;
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        recyclerOrder.setLayoutManager(layoutManager);
        recyclerOrder.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        btnBack.setOnClickListener(v-> finish());


    }

    @Override
    public void onCartLoadSuccess(List<CartModel> CartModelList) {

        //txtTotal.setText(new StringBuilder("$").append(sum));
        MyOrderAdapter adapter= new MyOrderAdapter(this,CartModelList);
        recyclerOrder.setAdapter(adapter);

    }

    @Override
    public void onCartLoadFailed(String message) {

        Snackbar.make(orderLayout,message,Snackbar.LENGTH_LONG).show();
    }
}