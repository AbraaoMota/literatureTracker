package com.chyronis.literature.literature.model.jwresponses;

import lombok.Data;

import java.util.List;

@Data
public class PublicationMarkers {
    private String mepsLanguageSpoken;
    private String mepsLanguageWritten;
    private Long documentId;
    private List<PublicationMepsAudioMarker> markers;
    private String type;
    private String hash;
    private PublicationAudioMarker introduction;
}
