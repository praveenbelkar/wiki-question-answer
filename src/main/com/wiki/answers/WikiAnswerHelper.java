package com.wiki.answers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WikiAnswerHelper {

    public void printQuestionToAnswerMap(Map<Integer, Integer> questionsToAnswerMap, String paragraphString, String questionString, String answerString ) {
        String[] questions = null;
        String[] answers = null;
        String[] sentences = null;

        sentences = paragraphString.split("\\.");
        questions = questionString.split("\\?");
        answers = answerString.split(";");

        for(Map.Entry<Integer, Integer> mapEntry: questionsToAnswerMap.entrySet()) {
            System.out.println(questions[mapEntry.getKey()] + " : " + answers[mapEntry.getValue()]);
        }
    }

    public Integer getCommonElementFromLists(List<Integer> list1, List<Integer> list2) {
        List<Integer> commonElements = new ArrayList<>();
        for(Integer element1:list1) {
            if(list2.contains(element1)) {
                commonElements.add(element1);
            }
        }
        return commonElements.isEmpty() ? null : commonElements.get(0);
    }

    public void printSentenceWordCountMaps(Map<Integer, Map<String, Integer>> sentenceWordCountMaps) {
        System.out.println("-------------------");
        sentenceWordCountMaps.forEach( (index, wordCountMap)->{
            wordCountMap.forEach((word, count) -> {
                System.out.println(word + ":" + count);
            });
            System.out.println("-------------------");
        });
    }

    public boolean eitherOfListsContainMoreThanOneElement(List<Integer> questionToSentenceList, List<Integer> answerToSentenceList) {
        return questionToSentenceList.size() > 1 || answerToSentenceList.size() > 1;
    }

    public boolean bothListsHaveOneElementAndSameValue(List<Integer> questionToSentenceList, List<Integer> answerToSentenceList) {
        return (questionToSentenceList.size() == 1 && answerToSentenceList.size() == 1)
                &&
                questionToSentenceList.get(0) == answerToSentenceList.get(0);
    }


}
