package com.rabbit.backend.Bean.Credits.Rule;

import com.rabbit.backend.Bean.Credits.CreditsRule;
import org.springframework.context.annotation.Configuration;

@Configuration("CreditsAction_CreatePost")
public class CreatePost extends CreditsRule {

    public CreatePost() {
        super(1, 1, 0, 3);
    }
}
