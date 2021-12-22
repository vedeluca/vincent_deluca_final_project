package com.example.vincent_deluca_final_project.ui.home.meetings;

import java.io.Serializable;

public class MeetingModel implements Serializable {
    public String title;
    public String description;
    public String month;
    public String day;
    public String year;
    public MeetingModel(String title, String description, String month, String day, String year){
        this.title = title;
        this.description = description;
        this.month = month;
        this.day = day;
        this.year = year;
    }
}
