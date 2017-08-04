package org.justvit.docsearch.model;

import com.google.common.base.Joiner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.justvit.docsearch.service.PhraseConverter;
import org.justvit.docsearch.service.PhraseConverterImpl;
import org.justvit.docsearch.service.TokenConverter;
import org.justvit.docsearch.service.TokenConverterImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.justvit.docsearch.model.Token.Type.WORD;

@RunWith(SpringJUnit4ClassRunner.class)
public class DocumentTest {

    @TestConfiguration
    static class Config {
        @Bean
        public TokenConverter tokenConverter(){
            return new TokenConverterImpl();
        }

        @Bean
        public PhraseConverter phraseConverter(){
            return new PhraseConverterImpl();
        }
    }

    @Autowired
    PhraseConverter phraseConverter;

    @Autowired
    TokenConverter tokenConverter;

    private Document doc;
    private List<Token> docTokens;

    @Before
    public void setUp() throws Exception {
        doc = new Document(phraseConverter.parseText("Tom Sawyer").get(0),
                           phraseConverter.parseText("Books that children love, Books you might laugh at, Adventure books"),
                           phraseConverter.parseText("Mark Twain, Huckleberry Finn")
                );

        docTokens = tokenConverter.parseText("Tom Sawyer Books that children love Books you might laugh at Adventure books Mark Twain Huckleberry Finn");
        docTokens.removeIf(t -> t.getType() != WORD);
    }

    @Test
    public void wordStream() throws Exception {
        List<Token> tokens = doc.wordStream().collect(Collectors.toList());
        Assert.assertEquals(docTokens, tokens);
    }

    @Test
    public void ngramStream() throws Exception {

        // 1..5
        List<Phrase> types = IntStream.iterate(1, i -> i + 1)
                .limit(5)
                .mapToObj(i -> new Phrase(new Token(Integer.toString(i), WORD)))
                .collect(Collectors.toList());

        // 6..9
        List<Phrase> designers = IntStream.iterate(6, i -> i + 1)
                .limit(4)
                .mapToObj(i -> new Phrase(new Token(Integer.toString(i), WORD)))
                .collect(Collectors.toList());

        System.out.printf("*** types = %s\n", types);
        System.out.printf("*** designers = %s\n", designers);

        Document document = new Document(new Phrase(new Token("0", WORD)), types, designers);

        List<String> result = document.ngramStream(3)
                .map(p -> Joiner.on(", ").join(p.tokens().stream().map(Token::getBody).collect(Collectors.toList())))
                .collect(Collectors.toList());

        List<String> expected
                = Arrays.asList("0, 1, 2", "1, 2, 3", "2, 3, 4", "3, 4, 5", "4, 5, 6", "5, 6, 7", "6, 7, 8", "7, 8, 9");

        Assert.assertEquals(expected, result);

    }
}