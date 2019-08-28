package com.rabbit.backend.Bean.Verify;

import lombok.Data;

@Data
public class VerifyResponse {
    private int success;
    private int score;
    private String msg;
}
