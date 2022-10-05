package com.chyronis.literature.literature.model.jwresponses;

import lombok.Data;

@Data
public class PublicationFileFormatDownloadDetails {
    private String url;
    private String stream;
    private String modifiedDatetime;
    private String checksum;
}
