package com.wiki.answers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

public class WikiAnswerServiceImpl implements WikiAnswerService {

    private WikiAnswerHelper wikiAnswerHelper = new WikiAnswerHelper();
    private static final List<String> excludeWords = Arrays.asList("is", "the", "of", "a", "in", "are", "and");

    @Override
    public Map<Integer, Integer> getAnswersForQuestionsFromWiki(String paragraph, String questions, String answers) {
        //Validate input
        if(invalidInputs(paragraph, questions, answers)) {
            throw new RuntimeException("please pass all three inputs - paragraph, questions, answers");
        }

        //Get the word count map for each sentence in paragraph, for each question and for each answer
        Map<Integer, Map<String, Integer>> sentenceWordCountMaps = getSentenceToWordCountMap(paragraph, ".");
        Map<Integer, Map<String, Integer>> questionsWordCountMaps = getSentenceToWordCountMap(questions, "?");
        Map<Integer, Map<String, Integer>> answersWordCountMaps = getSentenceToWordCountMap(answers, ";");

        Map<Integer, Integer> questionsToAnswerMapping = getAnswersForQuestions(sentenceWordCountMaps, questionsWordCountMaps, answersWordCountMaps);
        wikiAnswerHelper.printQuestionToAnswerMap(questionsToAnswerMapping, paragraph, questions, answers);
        return questionsToAnswerMapping;
    }

    /**
     * Returns the question index to answer index mapping
     * @param sentenceWordCountMaps - word count map for each sentence
     * @param questionsWordCountMaps - word count map for each question
     * @param answersWordCountMaps - word count map for each answer
     * @return the question index to answer index mapping
     */
    private Map<Integer, Integer> getAnswersForQuestions(Map<Integer, Map<String, Integer>> sentenceWordCountMaps, Map<Integer, Map<String, Integer>> questionsWordCountMaps, Map<Integer, Map<String, Integer>> answersWordCountMaps) {
        Map<Integer, List<Integer>> questionToSentenceMap = new HashMap<>();
        Map<Integer, List<Integer>> answerToSentenceMap = new HashMap<>();
        questionsWordCountMaps.forEach(getSentenceForGivenQuestion(sentenceWordCountMaps, questionToSentenceMap));
        answersWordCountMaps.forEach(getSentenceForGivenAnswers(sentenceWordCountMaps, answerToSentenceMap));
        //questionToSentenceMap contains the map of question index to sentence index mapping, more than one sentence can map to a given question
        //answerToSentenceMap contains the map of answer index to sentence index mapping, more than one sentence can map to a given answer

        return getQuestionToAnswerMappingBasedOnQuestionToSentenceAndAnswerToSentenceMapping(questionToSentenceMap, answerToSentenceMap);
    }

    private Map<Integer, Integer> getQuestionToAnswerMappingBasedOnQuestionToSentenceAndAnswerToSentenceMapping(Map<Integer, List<Integer>> questionToSentenceMap, Map<Integer, List<Integer>> answerToSentenceMap) {
        System.out.println("Answers #####");
        Map<Integer, Integer> questionToAnswerMap = new HashMap<>();
        for(Map.Entry<Integer, List<Integer>> questionToSentenceEntry: questionToSentenceMap.entrySet()) {
            for(Map.Entry<Integer, List<Integer>> answerToSentenceMapEntry: answerToSentenceMap.entrySet()) {
                if(wikiAnswerHelper.bothListsHaveOneElementAndSameValue(questionToSentenceEntry.getValue(), answerToSentenceMapEntry.getValue())) {
                    System.out.println(questionToSentenceEntry.getKey() + ": " + answerToSentenceMapEntry.getKey());
                    questionToAnswerMap.put(questionToSentenceEntry.getKey(), answerToSentenceMapEntry.getKey());
                    break;
                } else if(wikiAnswerHelper.eitherOfListsContainMoreThanOneElement(questionToSentenceEntry.getValue(), answerToSentenceMapEntry.getValue())){
                    //if the questionToSentence or answerToSentence has more than one mapping then take the common elements from them
                    Integer index = wikiAnswerHelper.getCommonElementFromLists(questionToSentenceEntry.getValue(), answerToSentenceMapEntry.getValue());
                    if(null != index) {
                        System.out.println(questionToSentenceEntry.getKey() + ": " + answerToSentenceMapEntry.getKey());
                        questionToAnswerMap.put(questionToSentenceEntry.getKey(), answerToSentenceMapEntry.getKey());
                        break;
                    }
                }
            }
        }
        return questionToAnswerMap;
    }


    private BiConsumer<? super Integer,? super Map<String,Integer>> getSentenceForGivenAnswers(Map<Integer, Map<String, Integer>> sentenceWordCountMaps, Map<Integer, List<Integer>> answerToSentenceMap) {
        return getSentenceForGivenQuestion(sentenceWordCountMaps, answerToSentenceMap);
    }

    //Identifies which sentence in the paragraph corresponds to which question/answer. Gets the mapping of each question/answer to one particular sentence.
    //This identification is based on the maximum matching words in the given question/answer.
    //There can be more than one sentence match for a given question/answer
    private BiConsumer<? super Integer,? super Map<String,Integer>> getSentenceForGivenQuestion(Map<Integer, Map<String, Integer>> sentenceWordCountMaps, Map<Integer, List<Integer>> questionToSentenceMap) {
        AtomicInteger maxMatch = new AtomicInteger(0);
        AtomicInteger maxMatchIndex = new AtomicInteger(0);
        return (questionIndex, questionWordCountFrequency) -> {
            List<Integer> matchedSentences = null;
            for(Map.Entry<Integer, Map<String, Integer>> entry: sentenceWordCountMaps.entrySet()){
                int matchedWords = getCommonWords(questionWordCountFrequency, entry.getValue());
                if(matchedWords > maxMatch.get()) {
                    maxMatch.set(matchedWords);
                    maxMatchIndex.set(entry.getKey());
                    matchedSentences = new ArrayList<>();
                    matchedSentences.add(maxMatchIndex.get());
                } else if(matchedWords == maxMatch.get()) {
                    maxMatch.set(matchedWords);
                    maxMatchIndex.set(entry.getKey());
                    if(null == matchedSentences) {
                        matchedSentences = new ArrayList<>();
                    }
                    matchedSentences.add(maxMatchIndex.get());
                }
            }
            questionToSentenceMap.put(questionIndex, matchedSentences);
            maxMatchIndex.set(0);
            maxMatch.set(0);
        };
    }

    //Returns number of words that are common in two word count maps
    private int getCommonWords(Map<String, Integer> map1, Map<String, Integer> map2) {
        int commonWords = 0;
        String word = null;

        for(Map.Entry<String,Integer> map1EntrySet: map1.entrySet()) {
            word = map1EntrySet.getKey();
            Integer count = getMatchingWordsCount(word, map2);
            if(null != count && count > 0){
                commonWords++;
            }
        }
        return commonWords;
    }

    /**
     * For a given input, based on delimiter it splits the input into array, and for each element in the array
     *  it identifies the number of occurences of each word in that element. e.g. if the input is set of questions, with delimiter '?'
     *  then it creates map of the format
     *  { index_of_question : {(word1, frequency of word1), (word2: frequenct of word2).... } for each word in the question}
     *
     * It returns similar output for answers and paragraph as well. Answers are delimited with ';' and paragraph is delimited with '.'
     */
    private Map<Integer, Map<String, Integer>> getSentenceToWordCountMap(String text, String delimiter) {
        StringTokenizer st = new StringTokenizer(text, delimiter);
        String sentence = null;
        Map<Integer, Map<String, Integer>> sentenceWordCountMaps = new HashMap<>();
        Map<String, Integer> sentenceWordCountMap = null;

        Integer index = 0;
        while(st.hasMoreTokens()) {
            sentence = st.nextToken();
            sentenceWordCountMap = calculateWordFrequency(sentence);
            sentenceWordCountMaps.put(index, sentenceWordCountMap);
            index++;
        }

        //wikiAnswerHelper.printSentenceWordCountMaps(sentenceWordCountMaps);
        return sentenceWordCountMaps;
    }

    /**
     * For a given sentence provides the map of number of occurence for each word in the sentence.
     * {
     *  word1:<number_of_occurrences_of_word1_in_sentence>,
     *  word2:<number_of_occurrences_of_word2_in_sentence>
     *      ... so on
     * }
     */
    private Map<String,Integer> calculateWordFrequency(String sentence) {
        Map<String, Integer> sentenceWordCountMap = new HashMap<>();
        StringTokenizer st = new StringTokenizer(sentence, " ");
        String word = null;
        Integer count = 0;
        while (st.hasMoreTokens()) {
            word = st.nextToken();
            word = word.toLowerCase(); //zebra and Zebra are considred as same word i.e. case insensitive
            word = word.replaceAll(",|\\(|\\)",""); // if the word contains characters like "," or brackets symbols like (,) then that such symbols are removed from the word
            //If the word is from the list of words like - is, of, the, etc then it is not considered
            if(excludeWords.contains(word)) {
                continue;
            }

            count = getMatchingWordsCount(word,sentenceWordCountMap);
            if(count == null) {
                count = 1;
            } else {
                count++;
            }
            sentenceWordCountMap.put(word, count);
        }
        return sentenceWordCountMap;
    }

    /**
     * It returns number of words that match similar word in plural or corresponding singular word in a word count map of a sentence
     * e.g. for the sentence - "Which Zebras are endangered :  Grévy's zebra and the mountain zebra" - for the word zebra it returns the count of 3
     *  the word Zebras and zebra are considered same
     */
    private Integer getMatchingWordsCount(String word, Map<String, Integer> sentenceWordCountMap) {
        int cnt1 = sentenceWordCountMap.get(word)==null ? 0 : sentenceWordCountMap.get(word);
        int cnt2 = sentenceWordCountMap.get(word+"s")==null ? 0 : sentenceWordCountMap.get(word+"s");
        int cnt3 = 0;
        if(word.endsWith("s")) {
            cnt3 = sentenceWordCountMap.get(word.substring(0,word.length()-1)) == null ? 0 : sentenceWordCountMap.get(word.substring(0, word.length()-1));
        }

        return cnt1 + cnt2 + cnt3;
    }

    private boolean invalidInputs(String paragraph, String questions, String answers) {
        return isEmpty(paragraph) || isEmpty((questions)) || isEmpty(answers);
    }

    private boolean isEmpty(String input) {
        return null == input || input.trim().length() == 0 ;
    }


}
