package com.example.vincent_deluca_final_project.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.vincent_deluca_final_project.R;
import com.example.vincent_deluca_final_project.databinding.FragmentProfileBinding;
import com.example.vincent_deluca_final_project.ui.drawer.DrawerActivity;
import com.example.vincent_deluca_final_project.ui.login.LoginActivity;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    private FragmentProfileBinding binding;
    private Uri uri = null;//TODO: change image on click and show current profile pic

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.saveBtn.setOnClickListener(view -> {
            String displayName = binding.displayNameText.getText().toString();
            profileViewModel.updateProfile(uri, displayName);
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