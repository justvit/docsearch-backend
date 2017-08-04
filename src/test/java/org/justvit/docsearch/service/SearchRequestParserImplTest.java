package org.justvit.docsearch.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.justvit.docsearch.model.Phrase;
import org.justvit.docsearch.model.SearchCriteria;
import org.justvit.docsearch.model.Token;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;

import static org.justvit.docsearch.model.Token.Type.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class SearchRequestParserImplTest {

    @TestConfiguration
    static class Config {
        @Bean
        SearchRequestParser searchRequestParser(){
            return new SearchRequestParserImpl();
        }
    }

    @Autowired
    SearchRequestParser parser;
    @MockBean
    PhraseConverter phraseConverter;

    private String rq1;
    private Phrase ph1;
    private SearchCriteria sc1;

    private String rq2;
    private Phrase ph2;
    private SearchCriteria sc2;

    private String rq3;
    private SearchCriteria sc3;

    private String rq4;
    private SearchCriteria sc4;

    @Before
    public void setup() {
        ph1 = new Phrase(
                new Token("lazy", WORD),
                new Token(" ", DELIMITER),
                new Token("fox", WORD)
        );

        ph2 = new Phrase(
                new Token("lion", WORD)
        );

        Mockito.when(phraseConverter.parseText("lazy fox"))
                .thenReturn(Arrays.asList(ph1));

        Mockito.when(phraseConverter.parseText("lion"))
                .thenReturn(Arrays.asList(ph2));

        rq1 = "lazy fox";
        sc1 = new SearchCriteria().appendAnyWord(ph1);

        rq2 = "lazy fox -lion";
        sc2 = new SearchCriteria().appendAnyWord(ph1).appendNoWord(ph2);

        rq3 = "\"lazy fox\"";
        sc3 = new SearchCriteria().appendAnyPhrase(ph1);

        rq4 = "lion -\"lazy fox\"";
        sc4 = new SearchCriteria().appendAnyWord(ph2).appendNoPhrase(ph1);
    }

    @Test
    public void parse_ok1() throws Exception {
        SearchCriteria c = parser.parse(rq1);
        Assert.assertEquals(sc1, c);
    }

    @Test
    public void parse_ok2() throws Exception {
        SearchCriteria c = parser.parse(rq2);
        Assert.assertEquals(sc2, c);
    }

    @Test
    public void parse_ok3() throws Exception {
        SearchCriteria c = parser.parse(rq3);
        Assert.assertEquals(sc3, c);
    }

    @Test
    public void parse_ok4() throws Exception {
        SearchCriteria c = parser.parse(rq4);
        Assert.assertEquals(sc4, c);
    }

    @Test(expected = SearchRequestParsingException.class)
    public void parse_fail_unclosedQuote() throws Exception {
        parser.parse("lion \"lazy fox");
    }

    @Test(expected = SearchRequestParsingException.class)
    public void parse_fail_negatedTermMissed() throws Exception {
        parser.parse("lazy fox -");
    }
}