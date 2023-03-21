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
    private String literatureCode;
    private LiteratureType literatureType;
    private boolean isLineItemOnReport;
    private boolean fallsUnderOtherLineItem;
    private boolean isLargePrint;
    private String language;
    private String urlForPublicationCoverImage;
    private boolean isPrintedInDoubles;

    public boolean isLineItemOnReport() {
        return isLineItemOnReport;
    }

    public void setLineItemOnReport(boolean lineItemOnReport) {
        isLineItemOnReport = lineItemOnReport;
    }

    public boolean isFallsUnderOtherLineItem() {
        return fallsUnderOtherLineItem;
    }

    public void setFallsUnderOtherLineItem(boolean fallsUnderOtherLineItem) {
        this.fallsUnderOtherLineItem = fallsUnderOtherLineItem;
    }

    public boolean isLargePrint() {
        return isLargePrint;
    }

    public void setLargePrint(boolean largePrint) {
        isLargePrint = largePrint;
    }

    public boolean isPrintedInDoubles() {
        return isPrintedInDoubles;
    }

    public void setPrintedInDoubles(boolean printedInDoubles) {
        isPrintedInDoubles = printedInDoubles;
    }
}
