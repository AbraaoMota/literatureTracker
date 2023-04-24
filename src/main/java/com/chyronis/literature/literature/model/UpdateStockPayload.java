package com.chyronis.literature.literature.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UpdateStockPayload {

    List<LiteratureStock> existingStock;
    List<LiteratureStockUpdates> updatedStock;
}
