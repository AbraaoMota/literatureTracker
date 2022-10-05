package com.chyronis.literature.literature.service;

import com.chyronis.literature.literature.model.jwresponses.AllLanguagesResponse;
import com.chyronis.literature.literature.model.jwresponses.PublicationMediaLinksResponse;
import com.chyronis.literature.literature.mongo.LanguagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class JWMediaMetadataService {


    WebClient webClient;

    LanguagesRepository languagesRepository;

    @Autowired
    public JWMediaMetadataService(LanguagesRepository languagesRepository) {
        this.webClient = WebClient.create();
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

    public PublicationMediaLinksResponse getPublicationDetailsById(String publicationId) {
        WebClient.RequestHeadersUriSpec<?> x = WebClient.create("https://b.jw-cdn.org/apis/pub-media/GETPUBMEDIALINKS?output=json&pub="+publicationId+"&langwritten=E").get();
        return x.retrieve().bodyToMono(PublicationMediaLinksResponse.class).block();
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
