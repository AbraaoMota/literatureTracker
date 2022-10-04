package com.chyronis.literature.literature.controller;

import com.chyronis.literature.literature.model.LiteratureItem;
import com.chyronis.literature.literature.model.LiteratureType;
import com.chyronis.literature.literature.mongo.LiteratureItemRepository;
import io.swagger.models.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LiteratureController {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    LiteratureItemRepository literatureItemRepository;

    @GetMapping(value = "/hello")
    public String helloWorld() {

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

        literatureItemRepository.insert(myFirstItem);

        return "Beep Boop";
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
