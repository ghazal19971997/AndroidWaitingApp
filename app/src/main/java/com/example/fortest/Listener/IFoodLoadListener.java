package com.example.fortest.Listener;

import com.example.fortest.model.FoodModel;

import java.util.List;

public interface IFoodLoadListener {

    void onFoodLoadSuccess(List<FoodModel> foodModelList);
    void onFoodLoadFailed(String message);

}
