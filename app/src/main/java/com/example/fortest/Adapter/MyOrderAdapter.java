package com.example.fortest.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fortest.R;
import com.example.fortest.eventBus.MyUpdateCartEvent;
import com.example.fortest.model.CartModel;
import com.example.fortest.model.RestaurantModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.protobuf.StringValue;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyOrderViewHolder> {


    private Context context;
    private List<CartModel> cartModelList;
    public String restaurantName;
    public String name = "";
    FirebaseFirestore fStore;

    public MyOrderAdapter(Context context, List<CartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
    }

    @NonNull
    @Override
    public MyOrderAdapter.MyOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyOrderAdapter.MyOrderViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_order_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrderViewHolder holder, int position) {

        holder.onBindOrderViewHolder(cartModelList.get(position));

    }


    private void deleteFromFirebase(CartModel cartModel) {

        //Get Current User
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child(user.getUid())
                .child(cartModel.getKey())
                .removeValue()
                .addOnSuccessListener(aVoid-> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));
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

    public class MyOrderViewHolder extends RecyclerView.ViewHolder{


        private String TAG = "MyViewHolder";
        //ImageView imageView;
        TextView PersonName;
        TextView foodName;
        TextView foodQuantity;
        TextView resName;
        TextView TotalPrice;
        CardView cv;

        public MyOrderViewHolder(@NonNull View itemView){

            super(itemView);
            PersonName = itemView.findViewById(R.id.personName);
            resName = itemView.findViewById(R.id.resName);
            TotalPrice = itemView.findViewById(R.id.totalPrice);
            foodQuantity = itemView.findViewById(R.id.foodQuantity);
            foodName = itemView.findViewById(R.id.foodName);
            cv = itemView.findViewById(R.id.cardView);
        }


        public void onBindOrderViewHolder(CartModel cart) {

            //search for names by UID

            //userName= "Man";
            findUsername(cart.getKey());
            resName.setText(new StringBuilder().append(cart.getRestaurantKey()));
            TotalPrice.setText(new StringBuilder().append(cart.getTotalPrice()));
            foodQuantity.setText(new StringBuilder().append(cart.getQuantity()));
            foodName.setText(new StringBuilder().append(cart.getName()));
//            Log.d("TAG", "user name: " +name);


            //Event

//        holder.btnDelete.setOnClickListener(v-> {
//            AlertDialog dialog = new AlertDialog.Builder(context)
//                    .setTitle("Delete Item")
//                    .setMessage("DO you really want to delete the Item? ")
//                    .setNegativeButton("Cancel", (dialog1, which) -> dialog1.dismiss())
//                    .setPositiveButton("Ok", (dialog12, which) -> {
//
//
//                        //Temp remove
//                        notifyItemRemoved(position);
//
//                        deleteFromFirebase(cartModelList.get(position));
//                        dialog12.dismiss();
//                    }).create();
//            dialog.show();
//        });

        }

        private void findUsername(String key) {
            try {

                fStore= FirebaseFirestore.getInstance();
                DocumentReference Ref =fStore.collection("Users").document(key);
                Ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d("TAG", "this is user data: " + documentSnapshot.getData());
                        name =documentSnapshot.getString("FullName");
                        Log.d("TAG", "Here is user name: " + name);
                        PersonName.setText(name+" 님");

                    }
                });

            }catch (Exception e){
                Log.e("TAG",e.getMessage());
            }

        }
    }


}
