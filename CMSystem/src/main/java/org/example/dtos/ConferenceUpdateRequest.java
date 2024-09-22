package org.example.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConferenceUpdateRequest {
    private String title;
    private String description;
    private List<Long> pcMemberIds;
    private List<Long> pcChairIds;
}

