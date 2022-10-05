package com.chyronis.literature.literature.model.jwresponses;

import lombok.Data;

import java.util.List;

@Data
public class AllLanguagesResponse {

    private List<LanguageResponse> languages;
}
