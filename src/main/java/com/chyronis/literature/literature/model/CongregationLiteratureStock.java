package com.chyronis.literature.literature.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CongregationLiteratureStock {

    LocalDateTime snapshotAsOfDate;
    Map<String, Integer> literatureItemIdToQuantityMap;
}
