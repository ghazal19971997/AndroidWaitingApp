package com.example.fortest.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fortest.Listener.ICartLoadListener;
import com.example.fortest.Listener.IRecyclerViewClickListener;
import com.example.fortest.R;
import com.example.fortest.eventBus.MyUpdateCartEvent;
import com.example.fortest.model.CartModel;
import com.example.fortest.model.FoodModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyFoodAdapter extends RecyclerView.Adapter<MyFoodAdapter.MyFoodViewHolder> {

    private FirebaseAuth mAuth;

    private Unbinder unbinder;
    private Context context;
    private List<FoodModel> foodModelList;
    private ICartLoadListener iCartLoadListener;
    private String resName;

    public MyFoodAdapter(Context context, List<FoodModel> foodModelList, ICartLoadListener iCartLoadListener, String resName) {
        this.context = context;
        this.foodModelList = foodModelList;
        this.iCartLoadListener = iCartLoadListener;
        this.resName = resName;
    }

    @NonNull
    @Override
    public MyFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyFoodViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_food_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyFoodViewHolder holder, int position) {
        Glide.with(context)
                .load(foodModelList.get(position).getImage())
                        .into(holder.imageView);
        holder.textPrice.setText(new StringBuilder("$").append(foodModelList.get(position).getPrice()));
        holder.textName.setText(new StringBuilder().append(foodModelList.get(position).getName()));



        holder.setListener(new IRecyclerViewClickListener() {
            @Override
            public void onRecyclerClick(View view, int adapterPosition1) {
                MyFoodAdapter.this.addToCart(foodModelList.get(position));
            }
        });
    }





    private void addToCart(FoodModel foodModel) {

        //Get Current User
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference userCart = FirebaseDatabase
                .getInstance()
                .getReference("Cart")
                .child(user.getUid());

        userCart.child(foodModel.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){  //if user already have item in cart
                            //just update the quantity and total price
                            CartModel cartModel = snapshot.getValue(CartModel.class);
                            cartModel.setQuantity(cartModel.getQuantity()+1);
                            cartModel.restaurantKey = resName;
                            Map<String,Object> updateData = new HashMap<>();
                            updateData.put("Quantity", cartModel.getQuantity());
                            updateData.put("TotalPrice", cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));

                            userCart.child(foodModel.getKey())
                                    .updateChildren(updateData)
                                    .addOnSuccessListener(unused -> iCartLoadListener.onCartLoadFailed("Add to Cart Success"))
                                    .addOnFailureListener(e -> iCartLoadListener.onCartLoadFailed(e.getMessage()));



                        }
                        else{  //If no item found in Cart, add new
                            CartModel cartModel= new CartModel();
                            cartModel.setName(foodModel.getName());
                            cartModel.setImage(foodModel.getImage());

                            cartModel.restaurantKey = resName;
                            cartModel.setQuantity(1);
//                            cartModel.setKey(foodModel.getKey());
                            cartModel.setPrice(foodModel.getPrice());
                            cartModel.setTotalPrice(Float.parseFloat(foodModel.getPrice()));



                            userCart.child(foodModel.getKey())
                                    .setValue(cartModel)
                                    .addOnSuccessListener(unused -> iCartLoadListener.onCartLoadFailed("Add to Cart Success"))
                                    .addOnFailureListener(e -> iCartLoadListener.onCartLoadFailed(e.getMessage()));

                        }

                        EventBus.getDefault().postSticky(new MyUpdateCartEvent());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        iCartLoadListener.onCartLoadFailed(error.getMessage());
                    }
                });
    }


    @Override
    public int getItemCount() {

        return foodModelList.size();
    }

    public class MyFoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.txtName)
        TextView textName;
        @BindView(R.id.txtPrice)
        TextView textPrice;

        IRecyclerViewClickListener listener;

        public void setListener(IRecyclerViewClickListener listener){

            this.listener=listener;
        }


        public MyFoodViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){

            listener.onRecyclerClick(v, getAdapterPosition());
        }


    }
}
