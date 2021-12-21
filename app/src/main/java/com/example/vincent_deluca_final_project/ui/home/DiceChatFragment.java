package com.example.vincent_deluca_final_project.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vincent_deluca_final_project.data.dice.DiceResults;
import com.example.vincent_deluca_final_project.data.dice.DiceRoller;
import com.example.vincent_deluca_final_project.databinding.DiceButtonsBinding;
import com.example.vincent_deluca_final_project.databinding.FragmentDiceChatBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DiceChatFragment extends Fragment {

    private FragmentDiceChatBinding chatBinding;
    private DiceButtonsBinding buttonsBinding;
    private FirebaseUser currentUser;
    private DatabaseReference diceRef;

    public DiceChatFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        chatBinding = FragmentDiceChatBinding.inflate(inflater, container, false);
        View view = chatBinding.getRoot();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        diceRef = database.getReference("Dice");
        buttonsBinding = chatBinding.diceButtons;
        buttonsBinding.diceNumber.setMinValue(1);
        buttonsBinding.diceNumber.setMaxValue(20);
        buttonsBinding.d4Btn.setOnClickListener(new DiceListener(4));
        buttonsBinding.d6Btn.setOnClickListener(new DiceListener(6));
        buttonsBinding.d8Btn.setOnClickListener(new DiceListener(8));
        buttonsBinding.d10Btn.setOnClickListener(new DiceListener(10));
        buttonsBinding.d12Btn.setOnClickListener(new DiceListener(12));
        buttonsBinding.d20Btn.setOnClickListener(new DiceListener(20));
        return view;
    }

    private class DiceListener implements View.OnClickListener {
        private int d;

        protected DiceListener(int d) {
            this.d = d;
        }

        @Override
        public void onClick(View view) {
            int n = buttonsBinding.diceNumber.getValue();
            DiceResults diceResults = DiceRoller.roll(d, n);
            diceResults.displayName = currentUser.getDisplayName();
            diceResults.uid = currentUser.getUid();
            diceResults.url = currentUser.getPhotoUrl().toString();
            DatabaseReference newDiceRef = diceRef.push();
            newDiceRef.setValue(diceResults).addOnSuccessListener(v -> {
                //TODO: maybe some sort of push notification?
            });
        }
    }
}