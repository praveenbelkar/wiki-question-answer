package com.wiki.answers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Map;

public class WikiQuestionAnswersTest {

    private WikiAnswerService wikiAnswerService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        wikiAnswerService = new WikiAnswerServiceImpl();
    }

    @Test
    public void testSuccess() {
        //Arrange
        String paragraph = WikiQuestionAnswersFixtures.getParagraph();
        String questions = WikiQuestionAnswersFixtures.getQuestions();
        String answers = WikiQuestionAnswersFixtures.getAnswers();

        //Act
        Map<Integer, Integer> questionsToAnswersMapping = wikiAnswerService.getAnswersForQuestionsFromWiki(paragraph, questions, answers);

        //Assert
        Assert.assertNotNull(questionsToAnswersMapping);
        Assert.assertEquals(questionsToAnswersMapping.size(), 5);
        Assert.assertEquals(questionsToAnswersMapping.get(0), new Integer(4));
        Assert.assertEquals(questionsToAnswersMapping.get(1), new Integer(3));
        Assert.assertEquals(questionsToAnswersMapping.get(2), new Integer(2));
        Assert.assertEquals(questionsToAnswersMapping.get(3), new Integer(1));
        Assert.assertEquals(questionsToAnswersMapping.get(4), new Integer(0));
    }

    @Test
    public void testForEmptyInputs() {
        //Expect
        thrown.expect(Exception.class);

        //Act
        WikiAnswerService wikiAnswerService = new WikiAnswerServiceImpl();
        Map<Integer, Integer> questionsToAnswersMapping = wikiAnswerService.getAnswersForQuestionsFromWiki(null, null, null);

    }
}
