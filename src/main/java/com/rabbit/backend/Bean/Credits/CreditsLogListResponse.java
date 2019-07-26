package com.rabbit.backend.Bean.Credits;

import lombok.Data;

import java.util.List;

@Data
public class CreditsLogListResponse {
    private Integer count;
    private List<CreditsLog> list;
}
