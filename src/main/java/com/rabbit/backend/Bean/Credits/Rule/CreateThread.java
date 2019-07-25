package com.rabbit.backend.Bean.Credits.Rule;

import com.rabbit.backend.Bean.Credits.CreditsRule;
import org.springframework.context.annotation.Configuration;

@Configuration("CreditsAction_CreateThread")
public class CreateThread extends CreditsRule {

    public CreateThread() {
        super(1, 1, 0, 3);
    }
}
