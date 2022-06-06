package com.example.fortest.Adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fortest.GlideApp;
import com.example.fortest.Listener.IFoodLoadListener;
import com.example.fortest.Listener.IRecyclerViewClickListener;
import com.example.fortest.Listener.IRestaurantLoadListener;
import com.example.fortest.Listener.RecyclerViewClickListener;
import com.example.fortest.MainActivity;
import com.example.fortest.R;
import com.example.fortest.Register;
import com.example.fortest.RestaurantActivity;
import com.example.fortest.eventBus.MyUpdateCartEvent;
import com.example.fortest.model.CartModel;
import com.example.fortest.model.FoodModel;
import com.example.fortest.model.RestaurantModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {

    int i=0;
    List<RestaurantModel> restaurantList;
    Context context;
    private IRestaurantLoadListener restaurantLoadListener;
    private List<FoodModel> foodModelList;


    public RecycleViewAdapter(List<RestaurantModel> restaurantList, Context context, IRestaurantLoadListener listener) {
        this.restaurantList = restaurantList;
        this.context = context;
        this.restaurantLoadListener= listener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rcv_row,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;


    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
     holder.onBind(restaurantList.get(position));

    }


    @Override
    public int getItemCount() {

        return restaurantList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private String TAG = "MyViewHolder";
        ImageView imageView;
        TextView textView1;
        TextView textView2;
        TextView textView3;
        TextView textView4;
        CardView cv;


        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            imageView = itemView.findViewById(R.id.resimg);
            textView1 = itemView.findViewById(R.id.resnm);
            textView2 = itemView.findViewById(R.id.grade);
            textView3 = itemView.findViewById(R.id.distance);
            textView4 = itemView.findViewById(R.id.waitingst);
            cv = itemView.findViewById(R.id.cardView);

        }

        public void onBind( RestaurantModel item){

            Glide.with(context)
                    .load(item.getImage())
                    .into(imageView);

//        Log.d(String.valueOf(RecycleViewAdapter.this), String.valueOf(restaurantList.get(position)));
            Log.e(TAG,"item: " + item.getImage());
            //Log.d("TAG","jajaja    res   Name is : " + item.getResname());
            textView1.setText(item.getName());
            textView2.setText(new StringBuilder().append(item.getGrade()));
            textView3.setText(new StringBuilder().append(item.getAddress()));
            textView4.setText(new StringBuilder().append(item.getWaiting_situation()));


            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MainActivity.class);
                    //Log.d("TAG","here resName is : " + item.getName());
                    intent.putExtra("restaurantName",item.getName());
                    context.startActivity(intent);
                }
            });

            //listener for every restaurant



        }


    }
}

