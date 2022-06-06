package com.example.fortest.Listener;



import com.example.fortest.model.RestaurantModel;

import java.util.List;

public interface IRestaurantLoadListener {

    void onRestaurantLoadSuccess(List<RestaurantModel> restaurantModelList);
    void onRestaurantLoadFailed(String message);

}
