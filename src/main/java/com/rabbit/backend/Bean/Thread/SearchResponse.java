package com.rabbit.backend.Bean.Thread;

import lombok.Data;

import java.util.List;

@Data
public class SearchResponse {
    private Integer count;
    private List<SearchItem> list;
}
