package com.example.vincent_deluca_final_project.data.dice;

import java.io.Serializable;

public class DiceResults implements Serializable {
    public String uid;
    public String total;
    public String calculations;

    public DiceResults(String total, String calculations) {
        this.total = total;
        this.calculations = calculations;
    }

    public DiceResults(
            String uid,
            String total,
            String calculations
    ) {
        this.uid = uid;
        this.total = total;
        this.calculations = calculations;
    }
}