package com.example.fortest.Listener;

import com.example.fortest.model.CartModel;
import com.example.fortest.model.FoodModel;

import java.util.List;

public interface ICartLoadListener {

    void onCartLoadSuccess(List<CartModel> CartModelList);
    void onCartLoadFailed(String message);
}
