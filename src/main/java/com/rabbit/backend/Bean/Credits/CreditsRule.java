package com.rabbit.backend.Bean.Credits;

import lombok.Data;

@Data
public abstract class CreditsRule {
    private int credits = 0;
    private int golds = 0;
    private int rmbs = 0;

    private int dailyLimit = 3;

    public CreditsRule(int credits, int golds, int rmbs, int dailyLimit) {
        this.credits = credits;
        this.golds = golds;
        this.rmbs = rmbs;
        this.dailyLimit = dailyLimit;
    }
}
