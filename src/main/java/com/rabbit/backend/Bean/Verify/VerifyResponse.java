package com.rabbit.backend.Bean.Verify;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class VerifyResponse {
    private int success;
    private int score;
    private String msg;
}
