package com.rabbit.backend.Bean.Credits;

import lombok.Data;

@Data
public abstract class CreditsRule {
    private int credits;
    private int golds;
    private int rmbs;

    private int dailyLimit;
    private boolean aboveZero;

    public CreditsRule(int credits, int golds, int rmbs, int dailyLimit, boolean aboveZero) {
        this.credits = credits;
        this.golds = golds;
        this.rmbs = rmbs;
        this.dailyLimit = dailyLimit;
        this.aboveZero = aboveZero;
    }
}
