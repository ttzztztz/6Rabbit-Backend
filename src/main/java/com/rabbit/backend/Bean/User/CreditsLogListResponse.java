package com.rabbit.backend.Bean.User;

import lombok.Data;

import java.util.List;

@Data
public class CreditsLogListResponse {
    private Integer count;
    private List<CreditsLog> list;
}
