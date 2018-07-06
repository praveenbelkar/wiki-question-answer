package com.wiki.answers;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class WikiQuestionAnswersTest {

    @Test
    public void testSuccess() {
        //Arrange
        String paragraph = WikiQuestionAnswersFixtures.getParagraph();
        String questions = WikiQuestionAnswersFixtures.getQuestions();
        String answers = WikiQuestionAnswersFixtures.getAnswers();

        //Act
        WikiAnswerService wikiAnswerService = new WikiAnswerServiceImpl();
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
}
