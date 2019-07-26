package com.rabbit.backend.Bean.Credits.Rule;

import com.rabbit.backend.Bean.Credits.CreditsRule;
import org.springframework.context.annotation.Configuration;

@Configuration("CreditsAction_DeleteThread")
public class DeleteThread extends CreditsRule {

    public DeleteThread() {
        super(-1, -1, 0, -1, false);
    }
}
