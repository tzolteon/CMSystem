package org.example.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaperRequest {
    private String title;
    private String abstractText;
    private String content;
    private Long conferenceId;
    private String requestorUsername;
}

