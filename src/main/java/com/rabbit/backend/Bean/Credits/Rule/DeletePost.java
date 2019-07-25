package com.rabbit.backend.Bean.Credits.Rule;

import com.rabbit.backend.Bean.Credits.CreditsRule;
import org.springframework.context.annotation.Configuration;

@Configuration("CreditsAction_DeletePost")
public class DeletePost extends CreditsRule {

    public DeletePost() {
        super(-1, -1, 0, -1);
    }
}
