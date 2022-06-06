package com.example.fortest;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    EditText Email,password;
    Button loginBtn,gotoRegister;
    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Email = findViewById(R.id.email);
        password = findViewById(R.id.pass);
        loginBtn = findViewById(R.id.signIn_btn);
        gotoRegister = findViewById(R.id.signUp_btn);

        //define
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();



        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkField(Email);
                checkField(password);
                Log.d("TAG","onClick"+ Email.getText().toString());

                if(valid){

                    fAuth.signInWithEmailAndPassword(Email.getText().toString(), password.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(Login.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                           //Check If Admin
                            checkUserAccessLevel(authResult.getUser().getUid());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });


        gotoRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });



    }

    private void checkUserAccessLevel(String uid) {

        DocumentReference df= fStore.collection("Users").document(uid);
        //Extract the data from Document
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) { //This snapshot contains all the data about that UID


                //Identify the access level
                if(documentSnapshot.getString("isAdmin") != null){

                 //User is Admin
                    Log.d("TAG","onSuccess: "+ documentSnapshot.getData());
                    startActivity(new Intent(getApplicationContext(),orderActivity.class));
                    finish();
                }
                if(documentSnapshot.getString("isUser") != null){

                    //User is not Admin
                    Log.d("TAG","onSuccess: "+ documentSnapshot.getData());
                    startActivity(new Intent(getApplicationContext(),RestaurantActivity.class));
                    finish();
                }
            }
        });
    }

    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }

    @Override
    protected void onStart() {

        super.onStart();

//        if(FirebaseAuth.getInstance().getCurrentUser() != null){ //If another User is currently Logged in
//            DocumentReference df= FirebaseFirestore.getInstance().collection("Users")
//                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//            df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    if(documentSnapshot.getString("isAdmin")!=null){
//                        Intent intent = new Intent(Login.this, AdminActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//
//                    if(documentSnapshot.getString("isUser")!=null){
//                        Intent intent = new Intent(Login.this, RestaurantActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    FirebaseAuth.getInstance().signOut();
//                    startActivity(new Intent( getApplicationContext(),Login.class));
//                    finish();
//                }
//            });
//
//
//
//        }//If not then they have to login first
        //else{
            Toast.makeText(Login.this, "Please Login", Toast.LENGTH_SHORT).show();

        //}
    }
}