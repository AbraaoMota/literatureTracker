package com.chyronis.literature.literature.model.jwresponses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicationAudioMarker {
    private String duration;
    private String startTime;
}
