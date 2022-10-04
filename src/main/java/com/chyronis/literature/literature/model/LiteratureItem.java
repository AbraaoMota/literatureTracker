package com.chyronis.literature.literature.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LiteratureItem  {

    private String id;
    private String name;
    private LiteratureType literatureType;
    private boolean isLineItemOnReport;
    private boolean fallsUnderOtherLineItem;
    private boolean isLargePrint;
    private String language;
    private String urlForPublicationCoverImage;
    private boolean isPrintedInDoubles;

    public LiteratureItem(String id, String name, LiteratureType literatureType) {
        this.id = id;
        this.name = name;
        this.literatureType = literatureType;
    }
}
