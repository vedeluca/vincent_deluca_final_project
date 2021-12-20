package com.example.vincent_deluca_final_project.data.model;

import com.google.firebase.database.ServerValue;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
public class User {
    public String displayName;
    public String email;

    public User(String displayName, String email) {
        this.displayName = displayName;
        this.email = email;
    }

    public User() {

    }
}