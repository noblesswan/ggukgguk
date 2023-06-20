package com.ggukgguk.api.nlp.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ggukgguk.api.nlp.vo.Keyword;

public interface TextRankService {

	public List<Keyword> extractKeywords(String text, int numOfKeywords);
}
