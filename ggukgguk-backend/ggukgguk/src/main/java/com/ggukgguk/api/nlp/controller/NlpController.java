package com.ggukgguk.api.nlp.controller;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ggukgguk.api.common.vo.BasicResp;
import com.ggukgguk.api.nlp.service.TextRankService;
import com.ggukgguk.api.nlp.vo.Keyword;

@RestController
@RequestMapping(value = "/nlp")
public class NlpController {
	private Logger log = LogManager.getLogger("base");

	@Autowired
	TextRankService textRankService;
	
	@PostMapping("/keyword")
	public ResponseEntity<?> getKeywordsHandler(@RequestBody Map<String, String> textInfo) {
		
		String targetText = textInfo.get("text");
		int numOfKeywords = Integer.parseInt(textInfo.get("num"));
		
		List<Keyword> keywordsMap = textRankService.extractKeywords(targetText, numOfKeywords);
		BasicResp<Object> resp = new BasicResp<Object>("success", null, keywordsMap);
		
		return ResponseEntity.ok(resp);
	}
	
}
