package com.example.vincent_deluca_final_project.ui.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.vincent_deluca_final_project.R;
import com.example.vincent_deluca_final_project.data.graphics.CircleTransform;
import com.example.vincent_deluca_final_project.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    private final ActivityResultLauncher<String[]> permissionsResult;

    public ProfileFragment() {
        ActivityResultLauncher<Intent> cameraResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK)
                        Picasso.get()
                                .load(profileViewModel.getImageUri())
                                .transform(new CircleTransform())
                                .into(binding.profileImage);
                });
        permissionsResult = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    boolean areAllGranted = true;
                    for (boolean b : result.values()) {
                        areAllGranted = areAllGranted && b;
                    }
                    if (areAllGranted)
                        cameraResult.launch(profileViewModel.takePhoto());
                });
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        profileViewModel.setResolver(getContext().getContentResolver());
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase.getReference("Users")
                .child(currentUser.getUid())
                .get().addOnSuccessListener(dataSnapshot -> {
            binding.displayNameText.setText(dataSnapshot.child("displayName").getValue().toString());
            binding.profileEmail.setText(dataSnapshot.child("email").getValue().toString());
            Picasso.get()
                    .load(dataSnapshot.child("url").getValue().toString())
                    .transform(new CircleTransform())
                    .into(binding.profileImage);
        });

        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        binding.profileImage.setOnClickListener(v -> this.permissionsResult.launch(permissions));

        binding.saveBtn.setOnClickListener(view -> {
            String displayName = binding.displayNameText.getText().toString();
            profileViewModel.updateProfile(displayName);
        });

        profileViewModel.getUpdateResult().observe(getViewLifecycleOwner(), loginResult -> {
            if (loginResult == null) {
                return;
            }
            if (loginResult.getError() != null) {
                showUpdateFailed(loginResult.getError());
                return;
            }
            StringBuilder message = new StringBuilder();
            message.append("Updated ");
            if (loginResult.getHasProfilePicture())
                message.append("profile picture");
            if (loginResult.getHasBoth())
                message.append(" and ");
            if (loginResult.getHasDisplayName())
                message.append("display name");
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigate(R.id.action_nav_profile_to_nav_home);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showUpdateFailed(Exception error) {
        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}