package com.chyronis.literature.literature.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Congregation {

    private String id;
    private String name;
    private String languageName;
    private CongregationLiteratureStock congregationLiteratureStock;
}
