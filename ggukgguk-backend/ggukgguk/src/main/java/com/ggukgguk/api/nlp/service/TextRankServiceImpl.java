/*
 * Original Author: WuLC
 * Original Source Code: https://github.com/WuLC/KeywordExtraction/blob/master/src/com/lc/nlp/keyword/algorithm/TextRank.java
 *     (MIT License)
 * Modified By: 0tak
 *     TextRank 산출 고도화, 한국어 전처리 과정 추가
 * */
package com.ggukgguk.api.nlp.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ggukgguk.api.nlp.vo.Keyword;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;

@Service
public class TextRankServiceImpl implements TextRankService {
	private Logger log = LogManager.getLogger("base");

	@Value("${file.komoranUserDic}")
	private String KOMORAN_USER_DIC_PATH;
	
	// PageRank 계열 알고리즘 모델의 damping factor
	// 임의 연결 (link, vote)을 제외하기 위한 보정치이며, 0.85를 많이 쓴다.
	private final float DAMPING_FACTOR = 0.85f;
	
	// 재귀 반복의 최대 횟수
	// PageRank는 재귀적으로 노드의 중요도를 산출하기 때문에,
	// 재귀 반복의 최대 횟수를 지정해준다.
	private final int MAX_ITERATIONS = 200;
	
	// 수렴 여부를 판단하기 위한 기준값
	private final float MIN_DIFFERENCES = 0.0001f;
	
	// TextRank 알고리즘에서는 각 단어별 유사도를 판단하기 위해
	// co-occurrence (동시출현수)를 산출하게 되는데,
	// 그 기준으로 활용되는 윈도우의 사이즈
	private final int CO_OCCURRENCE_WINDOWS = 3;
	
	private final String[] STOPWORDS = {"테스트", "업로드", "조각", "이미지", "마스트", "동영상", "!!", "!!!", "오늘"};

	/**
	 * 텍스트에서 키워드를 추출
	 * 
	 * @param title(String): 텍스트 원본
	 * @param numOfKeywords(int): 추출할 키워드의 개수
	 * @return (List<Keyword>): 키워드 리스트. Keyword라는 VO에 각 값이 담겨 반환된다.
	 */
	@Override
	public List<Keyword> extractKeywords(String text, int numOfKeywords) {
		List<Keyword> keywordList = new ArrayList<Keyword>();
		if ("".equals(text)) return keywordList;

		Map<String, Float> score = getWordScore(text);

		// rank keywords in terms of their score
		List<Map.Entry<String, Float>> entryList = new ArrayList<Map.Entry<String, Float>>(score.entrySet());
		Collections.sort(entryList, new Comparator<Map.Entry<String, Float>>() {
			@Override
			public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
				return (o1.getValue() - o2.getValue() > 0 ? -1 : 1);
			}
		});

		for (int i = 0; i < numOfKeywords; ++i) {
			try {
				Keyword keyword = new Keyword(entryList.get(i).getKey(), entryList.get(i).getValue());
				keywordList.add(keyword);
			} catch (IndexOutOfBoundsException e) {
				continue;
			}
		}
		return keywordList;
	}

	/**
	 * 텍스트를 토큰화하고, TextRank 알고리즘을 통해 각 토큰의 중요도를 점수화한다.
	 * 
	 * @param text(String): 텍스트 원본
	 * @return (Map<String,Float>): 각 토큰의 점수
	 */
	public Map<String, Float> getWordScore(String text) {
		// 품사에 바탕하여 토큰화 (일반명사, 고유명사, 어근)
		List<String> wordList = new ArrayList<String>();
				
		Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
		log.debug(KOMORAN_USER_DIC_PATH);
		komoran.setUserDic(KOMORAN_USER_DIC_PATH);
		KomoranResult analyzeResultList = komoran.analyze(text);
		List<Token> tokenList = analyzeResultList.getTokenList();
		
    	log.debug("형태소 분석 결과: ");
        for (Token token : tokenList) {
            log.debug(String.format("(%2d, %2d) %s/%s\n", token.getBeginIndex(), token.getEndIndex(), token.getMorph(), token.getPos()));
            if ((token.getPos().equals("NNP")
            		|| token.getPos().equals("NNG"))
            		&& token.getMorph().length() > 1) {
            	
            	boolean verified = true;
            	for (String stopword : STOPWORDS) {
            		if (token.getMorph().equals(stopword)) {
            			verified = false;
            			break;
            		}
            	}
            	if (verified) wordList.add(token.getMorph());
            }
        }
		
		// 동시 출현수에 바탕하여 그래프를 만든다
		Map<String, Set<String>> words = new HashMap<String, Set<String>>();
		Queue<String> que = new LinkedList<String>();
		for (String w : wordList) {
			if (!words.containsKey(w)) {
				words.put(w, new HashSet<String>());
			}
			que.offer(w); // insert into the end of the queue
			if (que.size() > CO_OCCURRENCE_WINDOWS) {
				que.poll(); // pop from the queue
			}

			for (String w1 : que) {
				for (String w2 : que) {
					if (w1.equals(w2)) {
						continue;
					}

					words.get(w1).add(w2);
					words.get(w2).add(w1);
				}
			}
		}

		// 재귀적 반복
		Map<String, Float> score = new HashMap<String, Float>();
		for (int i = 0; i < MAX_ITERATIONS; ++i) {
			Map<String, Float> m = new HashMap<String, Float>();
			float max_diff = 0;
			for (Map.Entry<String, Set<String>> entry : words.entrySet()) {
				String key = entry.getKey();
				Set<String> value = entry.getValue();
				m.put(key, 1 - DAMPING_FACTOR);
				for (String other : value) {
					int size = words.get(other).size();
					if (key.equals(other) || size == 0)
						continue;
					m.put(key, m.get(key) + DAMPING_FACTOR / size * (score.get(other) == null ? 0 : score.get(other)));
				}

				max_diff = Math.max(max_diff, Math.abs(m.get(key) - (score.get(key) == null ? 1 : score.get(key))));
			}
			score = m;

			// 수렴시 종료
			if (max_diff <= MIN_DIFFERENCES)
				break;
		}
		return score;
	}
}