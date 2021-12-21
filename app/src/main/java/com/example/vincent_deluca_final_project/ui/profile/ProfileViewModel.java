package com.example.vincent_deluca_final_project.ui.profile;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ProfileViewModel extends ViewModel {
    private final FirebaseUser currentUser;
    private final FirebaseDatabase firebaseDatabase;
    private final MutableLiveData<UpdateResult> updateResult = new MutableLiveData<>();
    private ContentResolver resolver;
    private Uri imageUri = null;

    public ProfileViewModel() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public void setResolver(ContentResolver resolver) {
        this.resolver = resolver;
    }

    public LiveData<UpdateResult> getUpdateResult() {
        return updateResult;
    }

    public Uri getProfilePicture() {
        return currentUser.getPhotoUrl();
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public Intent takePhoto() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        return intent;
    }

    public void updateProfile(String displayName) {
        if (imageUri != null)
            updateProfilePicture(displayName);
        else
            profileBuilder(null, displayName);
    }

    private void updateProfilePicture(String displayName) {
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
        currentUser.updateProfile(profileUpdates).addOnSuccessListener(task -> {
            DatabaseReference usersRef = firebaseDatabase.getReference("Users");
            usersRef.child(currentUser.getUid()).setValue(UUID.randomUUID().toString());
            updateResult.setValue(new UpdateResult(hasProfilePicture, hasDisplayName));
        }).addOnFailureListener(e -> updateResult.setValue(new UpdateResult(e)));
    }
}