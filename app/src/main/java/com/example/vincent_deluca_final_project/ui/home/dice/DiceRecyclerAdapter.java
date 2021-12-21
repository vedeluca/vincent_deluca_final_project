package com.example.vincent_deluca_final_project.ui.home.dice;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vincent_deluca_final_project.R;
import com.example.vincent_deluca_final_project.data.dice.DiceResults;
import com.example.vincent_deluca_final_project.data.graphics.CircleTransform;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DiceRecyclerAdapter extends RecyclerView.Adapter<DiceRecyclerAdapter.ViewHolder> {
    private final FirebaseUser currentUser;
    private final FirebaseDatabase firebaseDatabase;
    private final RecyclerView recyclerView;
    private final DatabaseReference diceRef;
    private final List<DiceKey> diceList;

    public DiceRecyclerAdapter(RecyclerView recyclerView, DatabaseReference diceRef) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        this.recyclerView = recyclerView;
        this.diceRef = diceRef;
        diceList = new ArrayList<>();

        diceRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                diceList.add(new DiceKey(snapshot.getKey(), new DiceResults(
                        snapshot.child("uid").getValue().toString(),
                        snapshot.child("total").getValue().toString(),
                        snapshot.child("calculations").getValue().toString()
                )));
                int position = diceList.size() - 1;
                notifyItemInserted(position);
                recyclerView.scrollToPosition(position);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public DiceRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dice_chat_item, parent, false);
        return new DiceRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiceRecyclerAdapter.ViewHolder holder, int position) {
        DiceKey diceKey = diceList.get(position);
        DiceResults diceResults = diceKey.diceResults;
        holder.total.setText(diceResults.total);
        holder.calculations.setText(diceResults.calculations);
        firebaseDatabase.getReference("Users")
                .child(diceResults.uid)
                .get().addOnSuccessListener(dataSnapshot -> {
            holder.displayName.setText(dataSnapshot.child("displayName").getValue().toString());
            Picasso.get()
                    .load(dataSnapshot.child("url").getValue().toString())
                    .transform(new CircleTransform())
                    .into(holder.profilePicture);
        });
        if (currentUser.getUid().equals(diceResults.uid)) {
            holder.message.setBackgroundResource(R.drawable.rounded_message_dark);
            holder.total.setTextColor(holder.itemView.getResources().getColor(R.color.white));
            holder.calculations.setTextColor(holder.itemView.getResources().getColor(R.color.white));
        } else {
            holder.message.setBackgroundResource(R.drawable.rounded_message_gray);
            holder.total.setTextColor(holder.itemView.getResources().getColor(R.color.black));
            holder.calculations.setTextColor(holder.itemView.getResources().getColor(R.color.black));
        }

        holder.message.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(holder.message, new AutoTransition());
            if (holder.calculations.getVisibility() == View.VISIBLE)
                holder.calculations.setVisibility(View.GONE);
            else
                holder.calculations.setVisibility(View.VISIBLE);
        });

        ChildEventListener usersRefListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(diceResults.uid)) {
                    String photoUrl = snapshot.child("url").getValue().toString();
                    String displayName = snapshot.child("displayName").getValue().toString();
                    Picasso.get()
                            .load(photoUrl)
                            .transform(new CircleTransform())
                            .into(holder.profilePicture);
                    holder.displayName.setText(displayName);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        DatabaseReference usersRef = firebaseDatabase.getReference("Users");
        usersRef.addChildEventListener(usersRefListener);
    }

    @Override
    public int getItemCount() {
        return diceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout message;
        public TextView total;
        public TextView calculations;
        public TextView displayName;
        public ImageView profilePicture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message);
            total = itemView.findViewById(R.id.chat_total);
            calculations = itemView.findViewById(R.id.chat_calculations);
            displayName = itemView.findViewById(R.id.chat_display_name);
            profilePicture = itemView.findViewById(R.id.chat_profile_picture);
        }
    }

    private static class DiceKey {
        private final String key;
        private final DiceResults diceResults;

        protected DiceKey(String key, DiceResults diceResults) {
            this.key = key;
            this.diceResults = diceResults;
        }
    }
}
