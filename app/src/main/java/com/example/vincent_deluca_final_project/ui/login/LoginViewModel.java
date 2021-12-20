package com.example.vincent_deluca_final_project.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.vincent_deluca_final_project.R;
import com.example.vincent_deluca_final_project.data.model.User;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final FirebaseAuth firebaseAuth;
    private final FirebaseDatabase firebaseDatabase;

    LoginViewModel() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password) {
        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnSuccessListener(result -> {
                    List<String> signInMethods = result.getSignInMethods();
                    if (signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                        firebaseAuth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener(authResult -> loginResult.setValue(new LoginResult(new LoggedInUserView(email))))
                                .addOnFailureListener(el -> loginResult.setValue(new LoginResult(el)));
                    } else {
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener(authResult -> {
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    FirebaseUser user = authResult.getUser();
                                    DatabaseReference usersRef = database.getReference("Users");
                                    usersRef.child(user.getUid()).setValue(new User(email, email));
                                    loginResult.setValue(new LoginResult(new LoggedInUserView(email)));
                                })
                                .addOnFailureListener(el -> loginResult.setValue(new LoginResult(el)));
                    }
                })
                .addOnFailureListener(e -> loginResult.setValue(new LoginResult(e)));
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}