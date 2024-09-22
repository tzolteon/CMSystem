package org.example.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PcMemberRequest {

    private List<Long> pcMemberIds;

    public List<Long> getPcMemberIds() {
        return pcMemberIds;
    }

    public void setPcMemberIds(List<Long> pcMemberIds) {
        this.pcMemberIds = pcMemberIds;
    }
}
