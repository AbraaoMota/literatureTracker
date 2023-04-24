package com.chyronis.literature.literature.model;

import io.swagger.models.auth.In;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiteratureStockUpdates {

    private String literatureCode;
    private Integer count;
    private boolean toBeRemoved = false;

    public LiteratureStockUpdates(String literatureCode, Integer count) {
        this.literatureCode = literatureCode;
        this.count = count;
    }
}
