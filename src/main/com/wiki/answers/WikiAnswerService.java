package com.wiki.answers;

import java.util.Map;

public interface WikiAnswerService {

    /**
     * This Api provides answers for the questions from wiki paragraph.
     *
     * @param paragraph - The wiki paragraph which needs to be searched
     * @param questions - The questions. Each of these questions should be terminated with symbol '?'
     * @param answers - The answers for the questions, which are delimited with symbol ';'
     * @return
     *          The map which provides the question number to answer number mapping.
     */
    public Map<Integer, Integer> getAnswersForQuestionsFromWiki(String paragraph, String questions, String answers);
}
