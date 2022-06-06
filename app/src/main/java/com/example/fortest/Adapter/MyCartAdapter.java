package com.example.fortest.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fortest.R;
import com.example.fortest.eventBus.MyUpdateCartEvent;
import com.example.fortest.model.CartModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder> {

    private FirebaseAuth mAuth;

    private Context context;
    private List<CartModel> cartModelList;

    public MyCartAdapter(Context context, List<CartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
    }

    @NonNull
    @Override
    public MyCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyCartViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyCartViewHolder holder, int position) {
        Glide.with(context)
                .load(cartModelList.get(position).getImage())
                .into(holder.imageView);
        holder.txtPrice.setText(new StringBuilder("$").append(cartModelList.get(position).getPrice()));
        holder.txtName.setText(new StringBuilder().append(cartModelList.get(position).getName()));
        holder.txtQuantity.setText(new StringBuilder().append(cartModelList.get(position).getQuantity()));

        //Event
        holder.btnMinus.setOnClickListener(v-> {
            minusCartItem(holder,cartModelList.get(position));
        });

        holder.btnPlus.setOnClickListener(v-> {
            PlusCartItem(holder,cartModelList.get(position));
        });

        holder.btnDelete.setOnClickListener(v-> {
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("DO you really want to delete the Item? ")
                    .setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss())
                    .setPositiveButton("Ok", (dialog12, which) -> {


                        //Temp remove
                        notifyItemRemoved(position);

                        deleteFromFirebase(cartModelList.get(position));
                        dialog12.dismiss();
                    }).create();
            dialog.show();
        });

    }

    private void deleteFromFirebase(CartModel cartModel) {

        //Get Current User
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //Log.d("TAG","2- Here is cart keyyyyy: "+ cartModel.getKey());
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child(user.getUid())
                .child(cartModel.getKey())
                .removeValue()
                .addOnSuccessListener(aVoid-> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
    }

    private void PlusCartItem(MyCartViewHolder holder, CartModel cartModel) {

        cartModel.setQuantity(cartModel.getQuantity() + 1);
        cartModel.setTotalPrice(cartModel.getQuantity() * Float.parseFloat(cartModel.getPrice()));

        holder.txtQuantity.setText(new StringBuilder().append(cartModel.getQuantity()));
        updateFirebase(cartModel);
    }

    private void minusCartItem(MyCartViewHolder holder, CartModel cartModel) {
        if (cartModel.getQuantity() > 1) {
            cartModel.setQuantity(cartModel.getQuantity() - 1);
            cartModel.setTotalPrice(cartModel.getQuantity() * Float.parseFloat(cartModel.getPrice()));

            //Update Quantity
            holder.txtQuantity.setText(new StringBuilder().append(cartModel.getQuantity()));
            updateFirebase(cartModel);
        }
    }

    private void updateFirebase(CartModel cartModel) {

        //Get Current User
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child(user.getUid())
                .child(cartModel.getKey())
                .setValue(cartModel)
                .addOnSuccessListener(aVoid-> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
    }

    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public class MyCartViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.btnMinus)
        ImageView btnMinus;
        @BindView(R.id.btnPlus)
        ImageView btnPlus;
        @BindView(R.id.btnDelete)
        ImageView btnDelete;
        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtPrice)
        TextView txtPrice;
        @BindView(R.id.txtQuantity)
        TextView txtQuantity;

        Unbinder unbinder;


        public MyCartViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder= ButterKnife.bind(this,itemView);
        }
    }
}
