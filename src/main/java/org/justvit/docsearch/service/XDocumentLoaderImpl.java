package org.justvit.docsearch.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.justvit.docsearch.model.XDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * Implementation of XDocumentLoader
 */
@Service
public class XDocumentLoaderImpl implements XDocumentLoader {
    private static final TypeReference<List<XDocument>> XDOC_LIST_TYPEREF = new TypeReference<List<XDocument>>() {};

    @Autowired
    private ObjectMapper jsonObjMapper;

    @Override
    public List<XDocument> load(Reader jsonReader) throws IOException {
        String json = String.join("\n", IOUtils.readLines(jsonReader));
        return jsonObjMapper.readValue(json, XDOC_LIST_TYPEREF);
    }
}
