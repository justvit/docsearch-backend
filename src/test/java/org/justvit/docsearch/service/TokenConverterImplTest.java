package org.justvit.docsearch.service;

import org.junit.Assert;
import org.junit.Test;
import org.justvit.docsearch.model.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit test for {@link TokenConverterImpl}
 */
public class TokenConverterImplTest {

    TokenConverter converter = new TokenConverterImpl();
    String text1 = "object-oriented";
    List<Token> tokens1 = Arrays.asList(new Token("object", Token.Type.WORD),
                                        new Token("-", Token.Type.DELIMITER),
                                        new Token("oriented", Token.Type.WORD));

    String text2 = "++operator !atomic";
    List<Token> tokens2 = Arrays.asList(new Token("++", Token.Type.DELIMITER),
            new Token("operator", Token.Type.WORD),
            new Token(" !", Token.Type.DELIMITER),
            new Token("atomic", Token.Type.WORD));

    String text3 = "ellipsis...";
    List<Token> tokens3 = Arrays.asList(new Token("ellipsis", Token.Type.WORD),
            new Token("...", Token.Type.DELIMITER));

    String text4 = "***don't fix not-broken---";
    List<Token> tokens4 = Arrays.asList(new Token("***", Token.Type.DELIMITER),
            new Token("don", Token.Type.WORD),
            new Token("'", Token.Type.DELIMITER),
            new Token("t", Token.Type.WORD),
            new Token(" ", Token.Type.DELIMITER),
            new Token("fix", Token.Type.WORD),
            new Token(" ", Token.Type.DELIMITER),
            new Token("not", Token.Type.WORD),
            new Token("-", Token.Type.DELIMITER),
            new Token("broken", Token.Type.WORD),
            new Token("---", Token.Type.DELIMITER)
    );


    @Test(expected = NullPointerException.class)
    public void parseNull() throws Exception {
        converter.parseText(null);
    }

    @Test
    public void parseEmpty() throws Exception {
        List<Token> tokens = converter.parseText("");
        Assert.assertTrue(tokens.isEmpty());
    }

    @Test
    public void parse1() throws Exception {
        List<Token> tokens = converter.parseText(text1);
        Assert.assertEquals(tokens1, tokens);
    }

    @Test
    public void parse2() throws Exception {
        List<Token> tokens = converter.parseText(text2);
        Assert.assertEquals(tokens2, tokens);
    }

    @Test
    public void parse3() throws Exception {
        List<Token> tokens = converter.parseText(text3);
        Assert.assertEquals(tokens3, tokens);
    }

    @Test
    public void parse4() throws Exception {
        List<Token> tokens = converter.parseText(text4);
        Assert.assertEquals(tokens4, tokens);
    }

    @Test(expected = NullPointerException.class)
    public void composeNull() throws Exception {
        converter.composeText(null);
    }

    @Test
    public void composeEmpty() throws Exception {
        String text = converter.composeText(new ArrayList<>());
        Assert.assertEquals(0, text.length());
    }

    @Test
    public void compose4() throws Exception {
        String text = converter.composeText(tokens4);
        Assert.assertEquals(text4, text);
    }
}