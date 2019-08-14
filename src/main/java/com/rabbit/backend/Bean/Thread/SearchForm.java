package com.rabbit.backend.Bean.Thread;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class SearchForm {

    @NotNull
    @Size(max = 32)
    private String keywords;
}
