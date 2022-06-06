package com.example.fortest;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivityMain extends AppCompatActivity {

    //Initialize variable
    private static int REQUEST_PERMISSION = 10001;
    private String[] permissions = { Manifest.permission.ACCESS_FINE_LOCATION };
    private ArrayList<String> checkPermissions = new ArrayList();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_main);

        //Assign variable
       // imageSlider = findViewById(R.id.image_slider);

        //Imageslider
//        ArrayList<SlideModel> image = new ArrayList<>();
//        image.add(new SlideModel(R.drawable.one, image.FIT));
//        image.add(new SlideModel(R.drawable.two, image.FIT));
//        image.add(new SlideModel(R.drawable.three, image.FIT));
//        image.add(new SlideModel(R.drawable.four, image.FIT));
//
//        imageSlider.setImageList(image);


        //location
        if(checkPermission()){
            init();



        }else {
            ActivityCompat.requestPermissions(this, checkPermissions.toArray(permissions), REQUEST_PERMISSION);
        }


    }

    //RecycleView


    //location
    private void init(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        startActivity(new Intent(MainActivityMain.this, Login.class));
        finish();
    }


    private boolean checkPermission() {
        boolean result = true;


        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
                checkPermissions.add(permission);
            }
        }

        if(!checkPermissions.isEmpty()){
            result = false;
        }

        return result;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION){
            boolean isAllChecked = true;
            for(int i=0; i<permissions.length -1; i++){
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if(permission != null){
                    boolean isChecked = false;
                    switch (permission){
                        case Manifest.permission.ACCESS_FINE_LOCATION: isChecked = checkGrantResults(grantResult, "ACCESS_FINE_LOCATION");
                        case Manifest.permission.READ_PHONE_STATE: isChecked = checkGrantResults(grantResult, "READ_PHONE_STATE"); break;
                        case Manifest.permission.WRITE_EXTERNAL_STORAGE: isChecked = checkGrantResults(grantResult, "WRITE_EXTERNAL_STORAGE"); break;
                        case Manifest.permission.READ_EXTERNAL_STORAGE: isChecked = checkGrantResults(grantResult, "READ_EXTERNAL_STORAGE"); break;
                        case Manifest.permission.READ_SMS: isChecked = checkGrantResults(grantResult, "READ_SMS"); break;
                        case Manifest.permission.RECEIVE_SMS: isChecked = checkGrantResults(grantResult, "RECEIVE_SMS"); break;
                    }
                    if(!isChecked) isAllChecked = false;
                }

            }

            if(!isAllChecked) finish();
            else init();



        }
    }
    private boolean checkGrantResults(int grantResult, String str) {
        boolean isChecked = true;
        String resultMsg = "";

        if(grantResult == PackageManager.PERMISSION_GRANTED){
            resultMsg = str + " permission authorized";
            isChecked = true;
        }else{
            resultMsg = str + " permission denied";
            isChecked = false;
        }
        //setToast(resultMsg);
        Log.e("" , resultMsg);
        return isChecked;
    }


}