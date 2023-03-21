package com.chyronis.literature.literature.controller;

import com.chyronis.literature.literature.model.LiteratureItem;
import com.chyronis.literature.literature.model.LiteratureType;
import com.chyronis.literature.literature.model.jwresponses.AllLanguagesResponse;
import com.chyronis.literature.literature.model.jwresponses.PublicationMediaLinksResponse;
import com.chyronis.literature.literature.mongo.LiteratureItemRepository;
import com.chyronis.literature.literature.service.JWMediaMetadataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
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


    @GetMapping(value = "/literatureItemCreation")
    public String literatureItemCreationForm() {
        List<LiteratureItem> allLiteratureItems = literatureItemRepository.findAll();

        StringBuilder stringBuilder = new StringBuilder();

        allLiteratureItems.forEach(x -> stringBuilder.append(x.getName()).append(" , Quantity: ").append(x.getName()));

        return stringBuilder.toString();
    }

    @RequestMapping("/")
    public String welcome() {
        return "stock-page.html";
    }

    @RequestMapping(value = "/newLiteratureType", method = RequestMethod.GET)
    public String newLiteratureType(Model model) {
        model.addAttribute("literatureItem", new LiteratureItem());
        model.addAttribute("msg.id", "Beep boop");
        return "add-literature-type.html";
    }


    @RequestMapping(value = "/addNewLiteratureType", method = RequestMethod.POST)
    @ResponseBody
    public String addNewLiteratureType(@ModelAttribute LiteratureItem literatureItem, BindingResult errors, Model model) {
        System.out.println("literatureItem = " + literatureItem + ", errors = " + errors + ", model = " + model);
        literatureItemRepository.insert(literatureItem);
        System.out.println("Added literature item successfully!");
        return "Added literature item successfully!";
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
