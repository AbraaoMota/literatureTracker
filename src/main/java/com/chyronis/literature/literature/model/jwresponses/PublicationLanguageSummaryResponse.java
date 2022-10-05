package com.chyronis.literature.literature.model.jwresponses;

import lombok.Data;

@Data
public class PublicationLanguageSummaryResponse {
    private String name;
    private String direction;
    private String locale;
}
