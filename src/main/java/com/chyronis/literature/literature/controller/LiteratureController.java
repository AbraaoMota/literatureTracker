package com.chyronis.literature.literature.controller;

import com.chyronis.literature.literature.model.LiteratureItem;
import com.chyronis.literature.literature.model.LiteratureType;
import com.chyronis.literature.literature.model.jwresponses.AllLanguagesResponse;
import com.chyronis.literature.literature.model.jwresponses.PublicationMediaLinksResponse;
import com.chyronis.literature.literature.mongo.LiteratureItemRepository;
import com.chyronis.literature.literature.service.JWMediaMetadataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class LiteratureController {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    JWMediaMetadataService jwMediaMetadataService;

    @Autowired
    LiteratureItemRepository literatureItemRepository;

    @GetMapping(value = "/helloWorld")
    public String helloWorld() {
        List<LiteratureItem> allLiteratureItems = literatureItemRepository.findAll();

        StringBuilder stringBuilder = new StringBuilder();

        allLiteratureItems.forEach(x -> stringBuilder.append(x.getName()).append(" , Quantity: ").append(x.getName()));

        return stringBuilder.toString();
    }

    @GetMapping(value = "/hello")
    public String hello() {

        LiteratureItem myFirstItem = LiteratureItem.builder()
                .id("123")
                .literatureType(LiteratureType.BROCHURE)
                .name("TEACH")
                .fallsUnderOtherLineItem(true)
                .isLineItemOnReport(true)
                .isLargePrint(true)
                .language("English")
                .urlForPublicationCoverImage("123")
                .isPrintedInDoubles(true)
                .build();

//        literatureItemRepository.insert(myFirstItem);

        return "Beep Boop";
    }

    @GetMapping(value = "/allLangs")
    public AllLanguagesResponse allLanguages() {
        return jwMediaMetadataService.getAllLanguages();
    }

    @GetMapping(value = "/populateAllLanguages")
    public String allLanguages(@RequestParam(required = false) boolean refresh) {
        jwMediaMetadataService.populateLanguagesInDB(refresh);
        return "All languages populated in DB!";
    }

    @GetMapping(value = "/getText")
    public PublicationMediaLinksResponse getText() {
        return jwMediaMetadataService.getText();
    }

    @GetMapping(value = "/getPub/{pubId}")
    public PublicationMediaLinksResponse getPublicationById(@PathVariable(value = "pubId") String pubId) throws JsonProcessingException {
        return jwMediaMetadataService.getPublicationDetailsById(pubId);
    }

    @GetMapping(value = "/getWolPubs")
    public String getWolPubs() throws IOException {
//        return jwMediaMetadataService.callWolPublicationsPage();
        return jwMediaMetadataService.callJWOrgBooksPage();

    }



    @GetMapping(value = "/getall")
    public String getall() {

        List<LiteratureItem> allItems = literatureItemRepository.findAll();

        StringBuilder output = new StringBuilder();
        for (LiteratureItem item : allItems) {
            output.append(item.toString());
        }

        return output.toString();
    }


}
