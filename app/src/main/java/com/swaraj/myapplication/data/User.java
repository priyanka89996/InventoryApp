package com.swaraj.myapplication.data;

public class User {
    public String FirstName;
    public String Email;

    public boolean IsAdmin;


    public User() {
    }

    public User (String firstName, String email, boolean accType ) {
        this.FirstName = firstName;
        this.Email = email;
        this.IsAdmin = accType;

    }


}
