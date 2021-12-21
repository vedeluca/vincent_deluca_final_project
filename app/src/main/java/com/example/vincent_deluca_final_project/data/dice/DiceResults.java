package com.example.vincent_deluca_final_project.data.dice;

import java.io.Serializable;

public class DiceResults implements Serializable {
    public String displayName;
    public String uid;
    public String url;
    public int total;
    public String calculations;

    public DiceResults(int total, String calculations) {
        this.total = total;
        this.calculations = calculations;
    }

    public DiceResults(
            String displayName,
            String uid,
            String url,
            int total,
            String calculations
    ) {
        this.displayName = displayName;
        this.uid = uid;
        this.url = url;
        this.total = total;
        this.calculations = calculations;
    }
}