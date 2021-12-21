package com.example.vincent_deluca_final_project.ui.home;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.vincent_deluca_final_project.ui.home.dice.DiceChatFragment;
import com.example.vincent_deluca_final_project.ui.home.meetings.MeetingsFragment;


/**
 * A [FragmentStateAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentStateAdapter {

    public SectionsPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
            return new MeetingsFragment();
        else
            return new DiceChatFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}