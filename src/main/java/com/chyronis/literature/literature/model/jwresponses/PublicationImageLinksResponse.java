package com.chyronis.literature.literature.model.jwresponses;

import lombok.Data;

@Data
public class PublicationImageLinksResponse {
    private String url;
    private String modifiedDateTime;
    private String checksum;
}
