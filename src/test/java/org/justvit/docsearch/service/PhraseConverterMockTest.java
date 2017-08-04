package org.justvit.docsearch.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.justvit.docsearch.model.Phrase;
import org.justvit.docsearch.model.Token;
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
public class PhraseConverterMockTest {

    @TestConfiguration
    static class Config {
        @Bean
        PhraseConverter phraseConverter(){
            return new PhraseConverterImpl();
        }
    }

    @Autowired
    PhraseConverter phraseConverter;

    @MockBean
    TokenConverter tokenConverter;

    String text0 = "Napoleon I";
    List<Token> tokens0 = Arrays.asList(new Token("Napoleon", WORD), new Token(" ", DELIMITER), new Token("I", WORD));

    String text1 = "Louis XVIII";
    List<Token> tokens1 = Arrays.asList(new Token("Louis", WORD), new Token(" ", DELIMITER), new Token("XVIII", WORD));

    String text2 = "Charles X";
    List<Token> tokens2 = Arrays.asList(new Token("Charles", WORD), new Token(" ", DELIMITER), new Token("X", WORD));

    String text = Joiner.on(", ").join(text0, text1, text2);
    List<Phrase> phrases = Lists.newArrayList(new Phrase(tokens0), new Phrase(tokens1), new Phrase(tokens2));

    @Before
    public void setUp() throws Exception {
        Mockito.when(tokenConverter.parseText(text0)).thenReturn(tokens0);
        Mockito.when(tokenConverter.parseText(text1)).thenReturn(tokens1);
        Mockito.when(tokenConverter.parseText(text2)).thenReturn(tokens2);

        Mockito.when(tokenConverter.composeText(tokens0)).thenReturn(text0);
        Mockito.when(tokenConverter.composeText(tokens1)).thenReturn(text1);
        Mockito.when(tokenConverter.composeText(tokens2)).thenReturn(text2);
    }

    @Test
    public void parseText() throws Exception {
        List<Phrase> result = phraseConverter.parseText(text);
        Assert.assertEquals(phrases, result);
    }

    @Test
    public void composeText() throws Exception {
        String result = phraseConverter.composeText(phrases);
        Assert.assertEquals(text, result);
    }

    @Test
    public void composeText0() throws Exception {
        String result = phraseConverter.composeText(new Phrase(tokens0));
        Assert.assertEquals(text0, result);
    }
}