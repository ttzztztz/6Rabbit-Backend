package com.rabbit.backend.Bean.Thread;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ThreadDiamondForm {
    @Size(min = 1, max = 20)
    private List<String> tid;

    @Range(min = 0, max = 3)
    private Integer diamond;
}
