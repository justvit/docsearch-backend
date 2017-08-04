package org.justvit.docsearch.service;

import com.google.common.base.Joiner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.justvit.docsearch.model.Document;
import org.justvit.docsearch.model.Phrase;
import org.justvit.docsearch.model.Token;
import org.justvit.docsearch.model.XDocument;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;

import static org.justvit.docsearch.model.Token.Type.DELIMITER;
import static org.justvit.docsearch.model.Token.Type.WORD;

@RunWith(SpringJUnit4ClassRunner.class)
public class XDocumentConverterImplTest {
    static final String RUSSIAN_LANGUAGE = "Russian language";
    static final List<Token> RUSSIAN_LANGUAGE_TOKENS = Arrays.asList(new Token("Russian", WORD),
                                                                     new Token(" ", DELIMITER),
                                                                     new Token("language", WORD));

    static final String WRITTEN = "Written";
    static final Token WRITTEN_TOKEN = new Token(WRITTEN, WORD);

    static final String SPOKEN = "Spoken";
    static final Token SPOKEN_TOKEN = new Token(SPOKEN, WORD);

    static final String WELL_KNOWN = "Well-known in the world";
    private static final String TYPES = Joiner.on(", ").join(WRITTEN, SPOKEN, WELL_KNOWN);
    static final Token[] WELL_KNOWN_TOKENS = {new Token("Well", WORD),
                                              new Token("-", DELIMITER),
                                              new Token("known", WORD),
                                              new Token(" ", DELIMITER),
                                              new Token("in", WORD),
                                              new Token(" ", DELIMITER),
                                              new Token("the", WORD),
                                              new Token(" ", DELIMITER),
                                              new Token("world", WORD)};

    static final Token[] LOMONOSOV_TOKENS = {new Token("Michael", WORD), new Token(" ", DELIMITER), new Token("Lomonosov", WORD)};
    static final Token[] PUSHKIN_TOKENS = {new Token("Alexander", WORD), new Token(" ", DELIMITER), new Token("Pushkin", WORD)};
    static final Token[] KARAMZIN_TOKENS = {new Token("Nicholas", WORD), new Token(" ", DELIMITER), new Token("Karamzin", WORD)};
    static final String MICHAEL_LOMONOSOV = "Michael Lomonosov";
    static final String ALEXANDER_PUSHKIN = "Alexander Pushkin";
    static final String NICHOLAS_KARAMZIN = "Nicholas Karamzin";
    static final String DESIGNERS = Joiner.on(", ").join(MICHAEL_LOMONOSOV, ALEXANDER_PUSHKIN, NICHOLAS_KARAMZIN);

    @TestConfiguration
    static class Config {
        @Bean XDocumentConverter xdocConverter() { return new XDocumentConverterImpl(); }
    }

    @Autowired
    XDocumentConverter xdocConverter;

    @MockBean
    PhraseConverter phraseConverter;

    @MockBean
    TokenConverter tokenConverter;


    private XDocument sourceXdoc;
    private Document expectedDoc;
    private Document sourceDoc;
    private XDocument expectedXdoc;

    @Before
    public void setUp() throws Exception {
        sourceXdoc = new XDocument();
        sourceXdoc.setName(RUSSIAN_LANGUAGE);
        sourceXdoc.setType(TYPES);
        sourceXdoc.setDesignedBy(DESIGNERS);

        expectedDoc = new Document(new Phrase(RUSSIAN_LANGUAGE_TOKENS),
                                   Arrays.asList(new Phrase(WRITTEN_TOKEN),
                                                 new Phrase(SPOKEN_TOKEN),
                                                 new Phrase(WELL_KNOWN_TOKENS)),
                Arrays.asList(new Phrase(LOMONOSOV_TOKENS),
                        new Phrase(PUSHKIN_TOKENS),
                        new Phrase(KARAMZIN_TOKENS)
                ));

        sourceDoc = expectedDoc;
        expectedXdoc = sourceXdoc;

        mockTokenConverter();
        mockPhraseConverter();
    }

    private void mockTokenConverter() {
        Mockito.when(tokenConverter.parseText(RUSSIAN_LANGUAGE))
               .thenReturn(RUSSIAN_LANGUAGE_TOKENS);

        Mockito.when(tokenConverter.composeText(RUSSIAN_LANGUAGE_TOKENS))
               .thenReturn(RUSSIAN_LANGUAGE);
    }

    private void mockPhraseConverter() {
        Mockito.when(phraseConverter.parseText(TYPES))
                .thenReturn(Arrays.asList(new Phrase(WRITTEN_TOKEN), new Phrase(SPOKEN_TOKEN), new Phrase(WELL_KNOWN_TOKENS)));

        Mockito.when(phraseConverter.composeText(Arrays.asList(new Phrase(WRITTEN_TOKEN),
                                                        new Phrase(SPOKEN_TOKEN), new Phrase(WELL_KNOWN_TOKENS))))
                .thenReturn(TYPES);

        Mockito.when(phraseConverter.parseText(DESIGNERS))
                .thenReturn(Arrays.asList(new Phrase(LOMONOSOV_TOKENS), new Phrase(PUSHKIN_TOKENS), new Phrase(KARAMZIN_TOKENS)));

        Mockito.when(phraseConverter.composeText(Arrays.asList(new Phrase(LOMONOSOV_TOKENS), new Phrase(PUSHKIN_TOKENS),
                                                        new Phrase(KARAMZIN_TOKENS))))
                .thenReturn(DESIGNERS);
    }

    @Test
    public void convertFromXDocument() throws Exception {
        Assert.assertEquals(expectedDoc, xdocConverter.convert(sourceXdoc));
    }

    @Test
    public void convertToXDocument() throws Exception {
        Assert.assertEquals(expectedXdoc, xdocConverter.convert(sourceDoc));
    }
}