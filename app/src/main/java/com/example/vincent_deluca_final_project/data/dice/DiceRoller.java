package com.example.vincent_deluca_final_project.data.dice;

import java.util.concurrent.ThreadLocalRandom;

public class DiceRoller {
    public static DiceResults roll(int d, int n) {
        int total = 0;
        StringBuilder calculations = new StringBuilder();
        calculations.append(n);
        calculations.append("d");
        calculations.append(d);
        calculations.append(" = ");
        for (int i = 0; i < n; i++) {
            int random = ThreadLocalRandom.current().nextInt(1, d);
            total += random;
            calculations.append(random);
            if (i + 1 < n)
                calculations.append(" + ");
        }
        return new DiceResults(total, calculations.toString());
    }
}
