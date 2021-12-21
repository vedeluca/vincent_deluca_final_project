package com.example.vincent_deluca_final_project.data.dice;

public class DiceRoller {
    public DiceResults roll(int d, int n) {
        int total = 0;
        StringBuilder calculations = new StringBuilder();
        for (int i = 0; i < n; i++) {

        }
        return new DiceResults(total, calculations.toString());
    }
}
