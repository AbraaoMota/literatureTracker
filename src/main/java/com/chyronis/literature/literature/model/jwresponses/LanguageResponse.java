package com.chyronis.literature.literature.model.jwresponses;

import lombok.Data;

@Data
public class LanguageResponse {
    private String code;
    private String locale;
    private String vernacular;
    private String script;
    private String name;
    private boolean isLangPair;
    private boolean isSignLanguage;
    private boolean isRTL;
}