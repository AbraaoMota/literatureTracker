package com.chyronis.literature.literature.model.jwresponses;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PublicationMediaLinksResponse {
    private String pubName;
    private String parentPubName;
    private String booknum;
    private String pub;
    private String issue;
    private String formattedDate;
    private List<String> fileformat;
    private String track;
    private String specialty;
    private PublicationImageLinksResponse pubImage;
    private Map<String, PublicationLanguageSummaryResponse> languages;
    private Map<String, Map<String, List<PublicationFileFormatDetails>>> files;
}
