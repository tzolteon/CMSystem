package org.example.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PcChairRequest {

    private List<Long> pcChairIds;

    public List<Long> getPcChairIds() {
        return pcChairIds;
    }

    public void setPcChairIds(List<Long> pcChairIds) {
        this.pcChairIds = pcChairIds;
    }
}

