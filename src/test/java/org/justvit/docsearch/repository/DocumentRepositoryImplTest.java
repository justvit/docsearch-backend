package org.justvit.docsearch.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.justvit.docsearch.model.Document;
import org.justvit.docsearch.model.Phrase;
import org.justvit.docsearch.service.PhraseConverter;
import org.justvit.docsearch.service.PhraseConverterImpl;
import org.justvit.docsearch.service.TokenConverter;
import org.justvit.docsearch.service.TokenConverterImpl;
import org.justvit.docsearch.util.Nlp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DocumentRepositoryImplTest {

    @TestConfiguration
    static class DocTestConfig {
        @Bean
        public TokenConverter tokenConverter(){
            return new TokenConverterImpl();
        }

        @Bean
        public PhraseConverter phraseConverter(){
            return new PhraseConverterImpl();
        }

        @Bean
        public DocumentRepository documentRepository() {
            return new DocumentRepositoryImpl();
        }
    }

    @Autowired
    private DocumentRepository docRepo;

    @Autowired
    private PhraseConverter phraseConverter;

    @Autowired
    private TokenConverter tokenConverter;

    @Before
    public void setUp() throws Exception {

    }

    private Phrase mkPhrase(String text) {
        return new Phrase(tokenConverter.parseText(text));
    }

    private List<Phrase> mkPhrases(String text) {
        return phraseConverter.parseText(text);
    }

    private List<String> listDocNames(Map<Document, Double> weighedDocs) {
        return Nlp.sortByWeight(weighedDocs).stream()
                .map(wdoc -> phraseConverter.composeText(wdoc.getKey().getName()))
                .collect(Collectors.toList());
    }

    @Test
    public void empty() throws Exception {
        Assert.assertEquals(0, docRepo.countWords());
        Assert.assertEquals(0, docRepo.countDocuments());
    }


    private void addDoc1() {
        Document doc = new Document(mkPhrase("Tom Sawyer"),
                                    mkPhrases("Adventure, Children, Comedy, American"),
                                    mkPhrases("Mark Twain"));
        docRepo.add(doc);
    }

    private void addDoc2() {
        Document doc = new Document(mkPhrase("Candide"),
                                    mkPhrases("Adventure, Classic, French, Comedy"),
                                    mkPhrases("Voltaire"));
        docRepo.add(doc);
    }

    private void addDoc3() {
        Document doc = new Document(mkPhrase("Macbeth"),
                                    mkPhrases("Crime, Classic, Tragedy, Fantasy, British"),
                                    mkPhrases("William Shakespeare"));
        docRepo.add(doc);
    }

    private void addDoc4() {
        Document doc = new Document(mkPhrase("In cold blood"),
                                    mkPhrases("Crime, Biography, Documentary, American"),
                                    mkPhrases("Truman Capote"));
        docRepo.add(doc);
    }

    private void addDoc5() {
        Document doc = new Document(mkPhrase("Memoirs"),
                                    mkPhrases("Auto-biography, Documentary, American"),
                                    mkPhrases("Harry Truman"));
        docRepo.add(doc);
    }

    @Test
    public void add1() throws Exception {
        addDoc1();
        Assert.assertEquals(8, docRepo.countWords());
        Assert.assertEquals(1, docRepo.countDocuments());
    }

    @Test
    public void add2() throws Exception {
        addDoc1();
        addDoc2();

        Assert.assertEquals(12, docRepo.countWords());
        Assert.assertEquals(2, docRepo.countDocuments());
    }

    @Test
    public void add3() throws Exception {
        addDoc1();
        addDoc2();
        addDoc3();

        Assert.assertEquals(19, docRepo.countWords());
        Assert.assertEquals(3, docRepo.countDocuments());
    }

    @Test
    public void addTheSameDocTwice() throws Exception {
        addDoc1();
        addDoc1();

        Assert.assertEquals(8, docRepo.countWords());
        Assert.assertEquals(1, docRepo.countDocuments());
    }

    @Test
    public void search0() throws Exception {
        addDoc1();
        addDoc2();
        addDoc3();
        addDoc4();
        addDoc5();

        docRepo.prepareForSearching();

        Map<Document, Double> weighedDocs = docRepo.searchAnyWord(tokenConverter.parseText("Abra-cadabra"));

        List<String> docNames = listDocNames(weighedDocs);

        Assert.assertTrue(docNames.isEmpty());
    }

    @Test
    public void search1() throws Exception {
        addDoc1();
        addDoc2();
        addDoc3();
        addDoc4();
        addDoc5();

        docRepo.prepareForSearching();

        Map<Document, Double> weighedDocs = docRepo.searchAnyWord(tokenConverter.parseText("Truman Crime Biography"));

        weighedDocs.forEach((doc, wgt) -> System.out.printf("$$$ [%s] = %f\n", phraseConverter.composeText(doc.getName()), wgt));
        List<String> docNames = listDocNames(weighedDocs);

        Assert.assertEquals(Arrays.asList("In cold blood", "Memoirs", "Macbeth"), docNames);
    }

    @Test
    public void search2() throws Exception {
        addDoc1();
        addDoc2();
        addDoc3();
        addDoc4();
        addDoc5();

        docRepo.prepareForSearching();

        Map<Document, Double> weighedDocs = docRepo.searchAnyWord(tokenConverter.parseText("Classic Crime blood"));

        weighedDocs.forEach((doc, wgt) -> System.out.printf("$$$ [%s] = %f\n", phraseConverter.composeText(doc.getName()), wgt));
        List<String> docNames = listDocNames(weighedDocs);

        Assert.assertEquals(Arrays.asList("In cold blood", "Macbeth", "Candide"), docNames);
    }

    @Test
    public void search3() throws Exception {
        addDoc1();
        addDoc2();
        addDoc3();
        addDoc4();
        addDoc5();

        docRepo.prepareForSearching();

        Map<Document, Double> weighedDocs = docRepo.searchAnyWord(tokenConverter.parseText("Classic Adventure Children"));

        weighedDocs.forEach((doc, wgt) -> System.out.printf("$$$ [%s] = %f\n", phraseConverter.composeText(doc.getName()), wgt));
        List<String> docNames = listDocNames(weighedDocs);

        Assert.assertEquals(Arrays.asList("Tom Sawyer", "Candide", "Macbeth"), docNames);
    }

}