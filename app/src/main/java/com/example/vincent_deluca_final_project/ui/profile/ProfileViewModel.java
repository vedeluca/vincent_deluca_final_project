package com.example.vincent_deluca_final_project.ui.profile;

import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ProfileViewModel extends ViewModel {
    private final FirebaseUser currentUser;
    private final MutableLiveData<UpdateResult> updateResult = new MutableLiveData<>();

    public ProfileViewModel() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public LiveData<UpdateResult> getUpdateResult() {
        return updateResult;
    }

    public void updateProfile(Uri imageUri, String displayName) {
        if (imageUri != null)
            updateProfilePicture(imageUri, displayName);
        else
            profileBuilder(null, displayName);
    }

    private void updateProfilePicture(Uri imageUri, String displayName) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final String fileNameInStorage = UUID.randomUUID().toString();
        String path = fileNameInStorage + ".jpg";
        final StorageReference imageRef = storage.getReference(path);
        imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> profileBuilder(uri, displayName))
                        .addOnFailureListener(e -> updateResult.setValue(new UpdateResult(e))))
                .addOnFailureListener(e -> updateResult.setValue(new UpdateResult(e)));
    }

    private void profileBuilder(@Nullable Uri uri, String displayName) {
        boolean hasProfilePicture = uri != null;
        boolean hasDisplayName = !displayName.equals("");
        if (!hasProfilePicture && !hasDisplayName) {
            updateResult.setValue(new UpdateResult(new Exception("No changes made to profile")));
            return;
        }
        UserProfileChangeRequest.Builder profileBuilder = new UserProfileChangeRequest.Builder();
        if (hasProfilePicture)
            profileBuilder = profileBuilder.setPhotoUri(uri);
        if (hasDisplayName)
            profileBuilder = profileBuilder.setDisplayName(displayName);
        UserProfileChangeRequest profileUpdates = profileBuilder.build();
        currentUser.updateProfile(profileUpdates).addOnSuccessListener(task ->
                updateResult.setValue(new UpdateResult(hasProfilePicture, hasDisplayName)))
                .addOnFailureListener(e -> updateResult.setValue(new UpdateResult(e)));
    }
}