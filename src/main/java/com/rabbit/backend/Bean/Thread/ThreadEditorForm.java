package com.rabbit.backend.Bean.Thread;

import com.rabbit.backend.Bean.Attach.ThreadAttachForm;
import com.rabbit.backend.Utilities.SafeHtml;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ThreadEditorForm {
    // for mybatis
    private String tid;
    private String uid;
    private String firstpid;

    @NotBlank
    private String fid;

    @NotBlank
    @Size(max = 255)
    private String subject;

    @NotBlank
    private String message;

    private List<ThreadAttachForm> attach;

    public String getMessage() {
        return SafeHtml.sanitize(this.message);
    }
}
