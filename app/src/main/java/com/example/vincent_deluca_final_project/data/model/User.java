package com.example.vincent_deluca_final_project.data.model;

import com.google.firebase.database.ServerValue;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class User {
    public String displayName;
    public String email;
    public String url;

    public User(String displayName, String email, String url) {
        this.displayName = displayName;
        this.email = email;
        this.url = url;
    }

    public User() {

    }
}