package com.example.vincent_deluca_final_project.ui.home.meetings;

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

public class MeetingsRecyclerAdapter extends RecyclerView.Adapter<MeetingsRecyclerAdapter.ViewHolder> {
    private final FirebaseUser currentUser;
    private final FirebaseDatabase firebaseDatabase;
    private final List<MeetingKey> meetingList;

    public MeetingsRecyclerAdapter(RecyclerView recyclerView, DatabaseReference meetings) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        meetingList = new ArrayList<>();

        meetings.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                meetingList.add(new MeetingKey(snapshot.getKey(), new MeetingModel(
                        snapshot.child("title").getValue().toString(),
                        snapshot.child("description").getValue().toString(),
                        snapshot.child("month").getValue().toString(),
                        snapshot.child("day").getValue().toString(),
                        snapshot.child("year").getValue().toString()
                )));
                int position = meetingList.size() - 1;
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
    public MeetingsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meeting_item, parent, false);
        return new MeetingsRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingsRecyclerAdapter.ViewHolder holder, int position) {
        MeetingKey meetingKey = meetingList.get(position);
        MeetingModel meetingModel = meetingKey.meetingModel;
        holder.title.setText(meetingModel.title);
        holder.description.setText(meetingModel.description);
        StringBuilder date = new StringBuilder();
        date.append("Campaign starts on ");
        date.append(meetingModel.month);
        date.append("/");
        date.append(meetingModel.day);
        date.append("/");
        date.append(meetingModel.year);
        holder.date.setText(date);
    }

    @Override
    public int getItemCount() {
        return meetingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.meeting_title);
            description = itemView.findViewById(R.id.meeting_description);
            date = itemView.findViewById(R.id.meeting_date);
        }
    }

    private static class MeetingKey {
        private final String key;
        private final MeetingModel meetingModel;

        protected MeetingKey(String key, MeetingModel meetingModel) {
            this.key = key;
            this.meetingModel = meetingModel;
        }
    }

}
