package com.example.fortest;

public class User {

    public String name;
    public String password;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String password, String email) {
        this.password = password;
        this.email = email;
    }
    public User(String name, String password, String email) {
        this.name=name;
        this.password = password;
        this.email = email;
    }



    public void setUserName(String name){

        this.name= name;
    }
    public void setUserEmail(String email){

        this.email= email;
    }
    public void setUserPass(String password){

        this.password= password;
    }

}
