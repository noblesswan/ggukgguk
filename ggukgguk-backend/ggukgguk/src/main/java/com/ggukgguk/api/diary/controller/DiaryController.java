package com.ggukgguk.api.diary.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ggukgguk.api.common.vo.BasicResp;
import com.ggukgguk.api.diary.service.DiaryService;
import com.ggukgguk.api.diary.vo.Diary;
import com.ggukgguk.api.diary.vo.DiaryColor;
import com.ggukgguk.api.diary.vo.DiaryMonth;
import com.ggukgguk.api.diary.vo.DiarySearch;

@RestController
@RequestMapping("/diary")
public class DiaryController {

	private Logger log = LogManager.getLogger("base");
	
	@Autowired
	DiaryService service;
	
	@GetMapping
	public ResponseEntity<?> getDiaries(@ModelAttribute DiarySearch diarySearch){
		
		log.debug(diarySearch);
		
		BasicResp<Object> respBody;	
		
		if(diarySearch.getDiaryYear()==null) {
			log.debug("다이어리 리스트 조회 실패1");
			respBody = new BasicResp<Object>("error", "연도가 비어있습니다.", null);		
			return ResponseEntity.badRequest().body(respBody);
		} else if(diarySearch.getDiaryYear()!=null && diarySearch.getDiaryMonth()==null) {
			List<Diary> diaryList = service.getDiaries(diarySearch);
			if (diaryList != null) {
				log.debug("다이어리 리스트 조회 성공2");
				respBody = new BasicResp<Object>("success", null, diaryList);
				return ResponseEntity.ok(respBody);
			} else {
				log.debug("다이어리 리스트 조회 실패2");
				respBody = new BasicResp<Object>("error", "해당 연도의 다이어리가 없습니다.", null);		
				return ResponseEntity.ok(respBody);
			}
		} else if(diarySearch.getDiaryYear()!=null && diarySearch.getDiaryMonth()!=null) {
			DiaryMonth diaryMonth = service.getDiary(diarySearch);
			log.debug(diaryMonth);
			if (diaryMonth != null) {
				log.debug("다이어리 리스트 조회 성공3");
				respBody = new BasicResp<Object>("success", null, diaryMonth);
				return ResponseEntity.ok(respBody);
			} else {
				log.debug("다이어리 리스트 조회 실패3");
				respBody = new BasicResp<Object>("error", "해당 월의 다이어리가 없습니다.", null);		
				return ResponseEntity.ok(respBody);
			}
		} else {
			log.debug("다이어리 리스트 조회 실패4");
			respBody = new BasicResp<Object>("error", "다이어리 조회에 실패하였습니다.", null);		
			return ResponseEntity.badRequest().body(respBody);
		}
	}
	
	// 추천 컬러 조회
	@GetMapping("/{diaryId}")
	public ResponseEntity<?> getColors(@PathVariable int diaryId){
		
		BasicResp<Object> respBody;	
		List<String> colorList = service.getColors(diaryId);
		if (colorList != null) {
			log.debug("색상 리스트 조회 성공");
			respBody = new BasicResp<Object>("success", null, colorList);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("색상 리스트 조회 실패");
			respBody = new BasicResp<Object>("error", "추천 색상 조회에 실패하였습니다.", null);		
			return ResponseEntity.badRequest().body(respBody);
		}
		
	}
	
	
	// 메인 컬러 수정
	@PutMapping("/{diaryId}")
	public ResponseEntity<?> editDiary(@PathVariable int diaryId, @RequestBody Diary diary){
		
		log.debug(diary);
		BasicResp<Object> respBody;	
		
		boolean result = service.editDiary(diary);
		
		if(result) {
			log.debug("다이어리 수정 성공");
			respBody = new BasicResp<Object>("success", "색상 수정에 성공했습니다", null);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("게시글 작성 실패");
			respBody = new BasicResp<Object>("error", "색상 수정에 실패하였습니다.", null);		
			return ResponseEntity.badRequest().body(respBody);
		}
		
	}
	
	//월말 결산 다이어리 조회 (알림)
	@GetMapping("/monthNotify")
	public ResponseEntity<?> getMonthDiaries(@RequestParam int referenceId){

		BasicResp<Object> respBody;	
		List<Diary> diary = service.getnotifyMonthDiaries(referenceId);
		if (diary != null) {
			log.debug("다이어리 리스트 조회 성공");
			respBody = new BasicResp<Object>("success", "다이어리 리스트 조회 성공하였습니다.", diary);
			return ResponseEntity.ok(respBody);
		} else {
			log.debug("다이어리 리스트 조회 실패");
			respBody = new BasicResp<Object>("error", "다이어리 리스트 조회 실패하였습니다.", null);		
			return ResponseEntity.badRequest().body(respBody);
		}
	}
	
}