package com.rabbit.backend.Utilities;

import org.springframework.stereotype.Component;

@Component
public class CreditsUtil {
    private final String[] creditsName = {"credits", "credits", "golds", "rmbs"};

    public String getCreditsNameByType(Integer creditsType) {
        return creditsName[creditsType];
    }
}
