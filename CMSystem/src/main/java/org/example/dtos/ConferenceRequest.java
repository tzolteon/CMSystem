package org.example.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConferenceRequest {
    private String title;
    private String description;
    private String creatorUsername;
}
