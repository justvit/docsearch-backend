package org.justvit.docsearch.service;

import org.justvit.docsearch.model.Document;
import org.justvit.docsearch.model.Phrase;
import org.justvit.docsearch.model.XDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of XDocumentConverter
 */
@Service
public class XDocumentConverterImpl implements XDocumentConverter {

    @Autowired
    private TokenConverter tokenConverter;

    @Autowired
    private PhraseConverter phraseConverter;

    /**
     * {@inheritDoc}
     */
    @Override
    public Document convert(XDocument xdocument) {
        Phrase name = new Phrase(tokenConverter.parseText(xdocument.getName()));
        List<Phrase> types = phraseConverter.parseText(xdocument.getType());
        List<Phrase> designedBys = phraseConverter.parseText(xdocument.getDesignedBy());

        return new Document(name, types, designedBys);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XDocument convert(Document document) {
        XDocument xdoc = new XDocument();

        xdoc.setName(tokenConverter.composeText(document.getName().tokens()));
        xdoc.setType(phraseConverter.composeText(document.getTypes()));
        xdoc.setDesignedBy(phraseConverter.composeText(document.getDesigners()));

        return xdoc;
    }
}
