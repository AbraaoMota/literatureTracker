package com.chyronis.literature.literature.model.jwresponses;

import lombok.Data;

@Data
public class PublicationFileFormatDetails {

    private String title;
    private PublicationFileFormatDownloadDetails file;
    private Long filesize;
    private PublicationFileFormatTrackImage trackImage;
    private PublicationMarkers markers;
    private String label;
    private int track;
    private boolean hasTrack;
    private String pub;
    private int docid;
    private int booknum;
    private String mimetype;
    private String edition;
    private String editionDescr;
    private String format;
    private String formatDescr;
    private String specialty;
    private String specialtyDescr;
    private boolean subtitled;
    private Integer frameWidth;
    private Integer frameHeight;
    private Integer frameRate;
    private Double duration;
    private Double bitRate;
}
