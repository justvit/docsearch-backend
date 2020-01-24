package org.justvit.docsearch.web.controller;

import org.justvit.docsearch.model.Document;
import org.justvit.docsearch.model.SearchCriteria;
import org.justvit.docsearch.model.XDocument;
import org.justvit.docsearch.service.DocumentService;
import org.justvit.docsearch.service.SearchRequestParser;
import org.justvit.docsearch.service.SearchRequestParsingException;
import org.justvit.docsearch.service.XDocumentConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Document controller
 */
@RestController
@RequestMapping("/documents")
public class DocumentController {
    private static Logger log = LoggerFactory.getLogger(DocumentController.class);

    @Autowired
    private SearchRequestParser searchRequestParser;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private XDocumentConverter documentConverter;

    @RequestMapping(path = "/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    List<XDocument> search(@RequestParam("what") String searchRequest) throws SearchRequestParsingException {
        log.debug("search(what={})", searchRequest);

        SearchCriteria request = searchRequestParser.parse(searchRequest);

        List<Document> docs = documentService.search(request);

        return docs.stream()
                .map(doc -> documentConverter.convert(doc))
                .collect(Collectors.toList());
    }
}

