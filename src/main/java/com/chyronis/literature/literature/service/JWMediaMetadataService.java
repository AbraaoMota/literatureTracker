package com.chyronis.literature.literature.service;

import com.chyronis.literature.literature.model.jwresponses.AllLanguagesResponse;
import com.chyronis.literature.literature.model.jwresponses.PublicationMediaLinksResponse;
import com.chyronis.literature.literature.mongo.LanguagesRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JWMediaMetadataService {

//    @Autowired
//    CodecConfigurer codecConfigurer;

    WebClient webClient;

    LanguagesRepository languagesRepository;

    @Autowired
    public JWMediaMetadataService(LanguagesRepository languagesRepository) {
        final int size = 16 * 1024 * 1024;
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();
        this.webClient = WebClient.builder()
                .exchangeStrategies(strategies)

                .build();
        this.languagesRepository = languagesRepository;
    }


    public AllLanguagesResponse getAllLanguages() {
        WebClient.RequestHeadersUriSpec<?> x = WebClient.create("https://b.jw-cdn.org/apis/mediator/v1/languages/E/all").get();
        return x.retrieve().bodyToMono(AllLanguagesResponse.class).block();
    }

    public void populateLanguagesInDB(boolean refresh) {
        if (refresh) {
            System.out.println("Refreshing all langs in DB");
            languagesRepository.deleteAll();
        }
        if (languagesRepository.count() == 0) {
            System.out.println("Fetching all langs from JW CDN");
            languagesRepository.saveAll(getAllLanguages().getLanguages());
        }
    }

    public PublicationMediaLinksResponse getText() {
        WebClient.RequestHeadersUriSpec<?> x = WebClient.create("https://b.jw-cdn.org/apis/pub-media/GETPUBMEDIALINKS?output=json&pub=es23&langwritten=E").get();
        return x.retrieve().bodyToMono(PublicationMediaLinksResponse.class).block();
    }

    public String callWolPublicationsPage() throws IOException {
        String relativeUrlBase = "/en/wol/library/r1/lp-e/all-publications";
        String rootPubURL = "https://wol.jw.org"+relativeUrlBase;

        WebClient.RequestHeadersUriSpec<?> initialWebClient = WebClient.create(rootPubURL).get();
        String response = initialWebClient.retrieve().bodyToMono(String.class).block();

        Document doc = Jsoup.parse(response);

        Elements links = doc.select("a[href]"); // a with href
        Elements pngs = doc.select("img[src$=.png]");

        String baseUrlHrefRegex = relativeUrlBase+"/(.*)";
        Pattern regexMatch = Pattern.compile(baseUrlHrefRegex);

        List<String> publicationPageLinks = new ArrayList<>();

        for (Element element : links) {
            if (element.attributes().hasDeclaredValueForKey("href")) {
                String hrefAttr = element.attributes().get("href");
                if (hrefAttr.contains(relativeUrlBase)) {
                    Matcher regexMatcher = regexMatch.matcher(hrefAttr);
                    regexMatcher.find();
                    if (regexMatcher.matches()) {
                        String furtherPublicationPages = regexMatcher.group(1);
                        publicationPageLinks.add(furtherPublicationPages);
                    }
                }
            }
        }

        List<String> pubIDs = new ArrayList<>();

        for (String publicationType : publicationPageLinks) {
            String newBaseURl = rootPubURL +"/" + publicationType;

            WebClient.RequestHeadersUriSpec<?> secondClient = WebClient.create(newBaseURl).get();
            String pubCategoryTypeResponse = secondClient.retrieve().bodyToMono(String.class).block();
            Document pubCategoryResponseDoc = Jsoup.parse(pubCategoryTypeResponse);

            Elements pubCategoryResponseLinks = pubCategoryResponseDoc.select("a[href]"); // a with href

            String pubCategoryUrlHrefRegex = newBaseURl+"(.*)";
            Pattern publicationIdPattern = Pattern.compile(pubCategoryUrlHrefRegex);

            for (Element publicationNode : pubCategoryResponseLinks) {
                if (publicationNode.attributes().hasDeclaredValueForKey("href")) {
                    String hrefAttr = publicationNode.attributes().get("href");
                    if (hrefAttr.contains(newBaseURl)) {
                        Matcher regexMatcher = publicationIdPattern.matcher(hrefAttr);
                        regexMatcher.find();
                        if (regexMatcher.matches()) {
                            String publicationId = regexMatcher.group(1);
                            pubIDs.add(publicationId);
                        }
                    }
                }
            }

        }

        return pubIDs.toString();
    }

    public PublicationMediaLinksResponse getPublicationDetailsById(String publicationId) throws JsonProcessingException {


        WebClient.RequestHeadersUriSpec<?> x = WebClient
                .create("https://b.jw-cdn.org/apis/pub-media/GETPUBMEDIALINKS?output=json&pub="+publicationId+"&langwritten=E")
                .get();


        Mono<String> response = x.retrieve().bodyToMono(String.class);


        Flux<String> y = x.retrieve().bodyToFlux(DataBuffer.class)
                .map(buffer -> {
                    String string = buffer.toString(Charset.forName("UTF-8"));
                    DataBufferUtils.release(buffer);
                    return string;
                });

        ObjectMapper om = new ObjectMapper();
        return om.readValue(y.blockFirst(), PublicationMediaLinksResponse.class);




//        return x.retrieve().bodyToMono(PublicationMediaLinksResponse.class).block();
    }

    public String callJWOrgBooksPage() {
        String url = "https://www.jw.org/en/library/books/";
        WebClient.RequestHeadersUriSpec<?> initialWebClient = WebClient.create(url).get();
        String response = initialWebClient.retrieve().bodyToMono(String.class).block();

        Document doc = Jsoup.parse(response);

        Elements divs = doc.select("div[class]"); // a with href
        Elements pngs = doc.select("img[src$=.png]");

        List<String> classesToLookFor = new ArrayList<>();
        classesToLookFor.add("synopsis");
        classesToLookFor.add("publication");

        String classRegex = "pub-(.*)";
        Pattern regexMatch = Pattern.compile(classRegex);

        List<String> pubClassIds = new ArrayList<>();

        for (Element element : divs) {
            if (element.classNames().containsAll(classesToLookFor)) {
                Set<String> elClassNames = element.classNames();
                for (String s : elClassNames) {
                    Matcher m = regexMatch.matcher(s);
                    m.find();
                    if (m.matches()) {
                        pubClassIds.add(m.group(1));
                    }
                }
            }
        }

        return pubClassIds.toString();
    }


    /*
PUBLICATIONS
 */
    // Daily text

    //https://b.jw-cdn.org/apis/pub-media/GETPUBMEDIALINKS?output=json&pub=es23&fileformat=JWPUB&langwritten=E

    //https://b.jw-cdn.org/apis/pub-media/GETPUBMEDIALINKS?output=json&pub=es23&langwritten=E
/*

     constants.JW_API +
       (mediaItem.pubSymbol ?
         "&pub=" + mediaItem.pubSymbol +
         (mediaItem.track ? "&track=" + mediaItem.track : "") +
         (mediaItem.issue ? "&issue=" + mediaItem.issue : "") +
         (mediaItem.format ? "&fileformat=" + mediaItem.format : "")
       :
       (mediaItem.docId ?
         "&docid=" + mediaItem.docId : "")) +

     "&langwritten=" + (mediaItem.lang || get("prefs").lang);


*/
    // Valid base URL
    // https://b.jw-cdn.org/apis/pub-media/GETPUBMEDIALINKS?

    // API query params:
    //output=json


    // pub = publicationID

    // fileFormat = enum. so far: PDF / JWPUB

    // langwritten = language. E = english.

    // Awake
    // https://b.jw-cdn.org/apis/pub-media/GETPUBMEDIALINKS?
    // docid=102022003
    // &issue=202207
    // &output=json
    // &fileformat=MP3
    // &alllangs=0
    // &langwritten=E
    // &txtCMSLang=E

/*
    https://www.jw.org/download/?output=html&pub=wfg&

    fileformat=
      PDF
    EPUB
    JWPUB
    RTF
    TXT
    BRL
    CBES
            DAISY
&alllangs=0
            &langwritten=E
&txtCMSLang=E
&isBible=0

Awakes
    https://www.jw.org/download/?

    issue=202111
    &output=html
    &pub=g
    &fileformat=PDF%2CEPUB%2CJWPUB%2CRTF%2CTXT%2CBRL%2CBES%2CDAISY
    &alllangs=0
    &langwritten=E
    &txtCMSLang=E
    &isBible=0

    https://www.jw.org/download/?
    issue=202107
    &output=html
    &pub=g
    &fileformat=PDF%2CEPUB%2CJWPUB%2CRTF%2CTXT%2CBRL%2CBES%2CDAISY&alllangs=0&langwritten=E&txtCMSLang=E&isBible=0

WT's

    https://www.jw.org/download/?
    issue=202205
    &output=html
    &pub=wp
    &fileformat=PDF%2CEPUB%2CJWPUB%2CRTF%2CTXT%2CBRL%2CBES%2CDAISY
    &alllangs=0
    &langwritten=E
    &txtCMSLang=E
    &isBible=0


Bible

    https://www.jw.org/download/?
    booknum=0
    &output=html
    &pub=nwtsty
    &fileformat=PDF%2CEPUB%2CJWPUB%2CRTF%2CTXT%2CBRL%2CBES%2CDAISY
    &alllangs=0
    &langwritten=E
    &txtCMSLang=E
    &isBible=1
*/

/*
VIDEOS
 */

    // Sample video url:
    //https://b.jw-cdn.org/apis/mediator/v1/media-items/E/docid-502017150_1_VIDEO?clientType=www
    /*
    E = english
    docid + ID of video
    _VIDEO (could also be _AUDIO)


    clientType = www (browser).


     */




    /*
    OTHER
     */

    // Full list of Languages available:
    //https://b.jw-cdn.org/apis/mediator/v1/languages/E/all

    // Button to naming map:
    //https://b.jw-cdn.org/apis/mediator/v1/translations/E


}
