package com.rabbit.backend.Bean.Thread;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class BatchDeleteForm {
    @Size(min = 1, max = 20)
    List<String> tid;
}
