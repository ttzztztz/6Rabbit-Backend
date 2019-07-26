package com.rabbit.backend.Bean.Credits.Rule;

import com.rabbit.backend.Bean.Credits.CreditsRule;
import org.springframework.context.annotation.Configuration;

@Configuration("CreditsAction_DownloadAttach")
public class DownloadAttach extends CreditsRule {

    public DownloadAttach() {
        super(0, -1, 0, -1, true);
    }
}
