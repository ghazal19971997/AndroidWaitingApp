package com.example.fortest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.internal.zzx;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText fullName, email, password;
    Button RegisterBtn, GoToLogin;
    boolean valid = false;

    //Firebase
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    //Radio Buttons
    private RadioGroup radioUserTypeGroup;
    private RadioButton radioUserTypeButton;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Define Auth
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fullName = findViewById(R.id.register_name);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_pass);
        RegisterBtn = findViewById(R.id.registerBtn);
        GoToLogin = findViewById(R.id.gotoLogin);

        //Radio Group
        radioUserTypeGroup = (RadioGroup) findViewById(R.id.isAdminOrUser);


        RegisterBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //Input Validation
                checkField();
                Toast.makeText(Register.this, "valid is "+valid, Toast.LENGTH_SHORT).show();

                //Radio validation
                // get selected radio button from radioGroup
                int selectedId = radioUserTypeGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioUserTypeButton = (RadioButton) findViewById(selectedId);
                userType= (String) radioUserTypeButton.getText();


                //If inputs are valid then we start proceeding to register
                if (valid) {

                    //Start to register User
                    fAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    //Now a User Id is created for this new User
                                    Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();

                                    FirebaseUser user = fAuth.getCurrentUser();
                                    DocumentReference df = fStore.collection("Users").document(user.getUid());

                                    Map<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("FullName", fullName.getText().toString());
                                    userInfo.put("UserEmail", email.getText().toString());
                                    userInfo.put("Password", password.getText().toString());

                                    //Specify if User is Admin
                                    if(userType.equals("Normal User")){
                                        userInfo.put("isUser","1");
                                        //Add to DB
                                        df.set(userInfo);
                                        Toast.makeText(Register.this, "UserInfo Added to DB", Toast.LENGTH_SHORT).show();

                                    }
                                    if(userType.equals("Admin")){
                                        Toast.makeText(Register.this, "UserType is Admin", Toast.LENGTH_SHORT).show();
                                        userInfo.put("isAdmin","1");
                                        //Add to DB
                                        df.set(userInfo);
                                        Toast.makeText(Register.this, "UserInfo Added to DB", Toast.LENGTH_SHORT).show();

                                    }
//Start Activity                    //Start Activity
                                    startActivity(new Intent(Register.this, Login.class));
                                    finish();
                                }


                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Failed to create Account"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });

        GoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

    }
        public boolean checkField (){

        //Check If everything is valid
            String name= fullName.getText().toString().trim();
            String Emaill = email.getText().toString().trim();
            String pass = password.getText().toString().trim();

        if (name.isEmpty()) {
            fullName.setError("Name is required!");
            fullName.requestFocus();
            return valid;
        }

        if (Emaill.isEmpty()) {
            email.setError("Email is required!");
            email.requestFocus();
            return valid;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(Emaill).matches()) {
            email.setError("Email is not valid!");
            email.requestFocus();
            return valid;
        }

        if (pass.isEmpty()) {
            password.setError("Password is required!");
            password.requestFocus();
            return valid;
        }

        if (pass.length() < 6) {
            password.setError("Minimum password length is 6 digits!");
            password.requestFocus();
            return valid;
        }

        valid=true;
        return valid;

        }

    }
