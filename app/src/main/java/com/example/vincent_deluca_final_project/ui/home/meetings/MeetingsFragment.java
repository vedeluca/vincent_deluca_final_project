package com.example.vincent_deluca_final_project.ui.home.meetings;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.vincent_deluca_final_project.R;
import com.example.vincent_deluca_final_project.databinding.FragmentDiceChatBinding;
import com.example.vincent_deluca_final_project.databinding.FragmentMeetingsBinding;
import com.example.vincent_deluca_final_project.ui.home.dice.DiceChatFragment;
import com.example.vincent_deluca_final_project.ui.home.dice.DiceRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class MeetingsFragment extends Fragment {
    private DatabaseReference meetings;

    public MeetingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        com.example.vincent_deluca_final_project.databinding.FragmentMeetingsBinding binding = FragmentMeetingsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        meetings = database.getReference("Meetings");

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        binding.meetingsRecycler.setLayoutManager(layoutManager);
        MeetingsRecyclerAdapter recyclerAdapter = new MeetingsRecyclerAdapter(binding.meetingsRecycler, meetings);
        binding.meetingsRecycler.setAdapter(recyclerAdapter);

        binding.addMeetingBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            Context context = v.getContext();
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);

            final EditText titleBox = new EditText(context);
            titleBox.setHint("Title");
            layout.addView(titleBox);

            final EditText descriptionBox = new EditText(context);
            descriptionBox.setHint("Description");
            layout.addView(descriptionBox);

            DatePicker datePicker = new DatePicker(context);
            layout.addView(datePicker);

            builder.setView(layout);
            builder.setPositiveButton("OK", (dialog, which) -> {
                String title = titleBox.getText().toString();
                String description = descriptionBox.getText().toString();
                MeetingModel meetingModel = new MeetingModel(
                        title,
                        description,
                        String.valueOf(datePicker.getDayOfMonth()),
                        String.valueOf(datePicker.getMonth()),
                        String.valueOf(datePicker.getYear())
                );
                meetings.push().setValue(meetingModel);
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                cal.set(Calendar.MONTH, datePicker.getMonth());
                cal.set(Calendar.YEAR, datePicker.getYear());
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("allDay", false);
                intent.putExtra("rrule", "FREQ=WEEKLY");
                intent.putExtra("title", title);
                intent.putExtra("description", description);
                startActivity(intent);
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        });

        return view;
    }
}