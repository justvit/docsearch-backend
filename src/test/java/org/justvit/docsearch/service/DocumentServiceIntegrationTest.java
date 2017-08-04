package org.justvit.docsearch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.justvit.docsearch.model.Document;
import org.justvit.docsearch.model.Phrase;
import org.justvit.docsearch.model.SearchCriteria;
import org.justvit.docsearch.repository.DocumentRepository;
import org.justvit.docsearch.repository.DocumentRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.justvit.docsearch.model.Token.Type.WORD;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = "data.file=data/data.json")
public class DocumentServiceIntegrationTest {

    @TestConfiguration
    static class Config {

        @Bean
        public PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }

        @Bean
        @Primary
        public TokenConverter tokenConverter(){
            return new TokenConverterImpl();
        }

        @Bean
        PhraseConverter phraseConverter(){
            return new PhraseConverterImpl();
        }

        @Bean
        public ObjectMapper jsonObjMapper() {
            return new ObjectMapper();
        }


        @Bean
        XDocumentLoader xDocumentLoader() { return new XDocumentLoaderImpl(); }

        @Bean
        XDocumentConverter xDocumentConverter() { return new XDocumentConverterImpl(); }

        @Bean
        DocumentRepository documentRepository() { return new DocumentRepositoryImpl(); }

        @Bean
        DocumentService documentService() { return new DocumentServiceImpl(); }
    }

    @Autowired
    PhraseConverter phraseConverter;

    @Autowired
    DocumentService docService;

    private List<String> listNames(List<Document> docs) {
        return docs.stream()
                .map(wdoc -> phraseConverter.composeText(wdoc.getName()))
                .collect(Collectors.toList());
    }

    private List<Phrase> parseOnlyWords(String text) {
        return phraseConverter.parseText(text).stream()
                .map(phrase -> phrase.tokens().stream()
                                        .filter(t -> t.getType() == WORD)
                                        .collect(Collectors.toList()))
                .map(ts -> new Phrase(ts))
                .collect(Collectors.toList());
    }

    @Test
    public void test_LispCommon() throws Exception {
        SearchCriteria criteria = new SearchCriteria()
                                    .appendAnyWord(parseOnlyWords("Lisp Common"));

        List<Document> found = docService.search(criteria);
        List<String> foundDocNames = listNames(found);
        Assert.assertEquals(Arrays.asList("Common Lisp", "Lisp"), foundDocNames);
    }

    @Test
    public void test_InterpretedThomasEugene() throws Exception {
        SearchCriteria criteria = new SearchCriteria()
                                    .appendAnyWord(parseOnlyWords("Interpreted"))
                                    .appendAnyPhrase(parseOnlyWords("Thomas Eugene"));

        List<Document> found = docService.search(criteria);
        List<String> foundDocNames = listNames(found);

        List<String> onlyBasic = Arrays.asList("BASIC");

        Assert.assertEquals(onlyBasic, foundDocNames);
    }

    @Test
    public void test_ScriptingMicrosoft() throws Exception {
        SearchCriteria criteria = new SearchCriteria()
                                    .appendAnyWord(parseOnlyWords("Scripting Microsoft"));

        List<Document> found = docService.search(criteria);
        List<String> foundDocNames = listNames(found);

        List<String> allScriptLangsByMicrosoft = Arrays.asList("VBScript", "JScript", "Windows PowerShell");

        Assert.assertTrue(foundDocNames.containsAll(allScriptLangsByMicrosoft));
    }

    @Test
    public void test_JohnMinusArray() throws Exception {
        SearchCriteria criteria = new SearchCriteria()
                                    .appendAnyWord(parseOnlyWords("john"))
                                    .appendNoWord(parseOnlyWords("array"));

        List<Document> found = docService.search(criteria);
        List<String> foundNames = listNames(found);

        List<String> matched = Arrays.asList("BASIC", "Haskell", "Lisp", "S-Lang");
        List<String> notMatched = Arrays.asList("Chapel", "Fortran", "S");

        Assert.assertTrue(foundNames.containsAll(matched));
        Assert.assertTrue(foundNames.stream().noneMatch(name -> notMatched.contains(name)));
    }
}