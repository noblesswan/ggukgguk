package com.ggukgguk.api.diary.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ggukgguk.api.diary.dao.DiaryDao;
import com.ggukgguk.api.diary.vo.Diary;
import com.ggukgguk.api.diary.vo.DiaryColor;
import com.ggukgguk.api.diary.vo.DiaryMonth;
import com.ggukgguk.api.diary.vo.DiarySearch;

@Service
public class DiaryServiceImpl implements DiaryService {
	
	@Autowired
	DiaryDao dao;
	
	@Override
	public List<Diary> getDiaries(DiarySearch diarySearch) {
		
		return dao.selectDairyList(diarySearch);
	}
	
	@Override
	public DiaryMonth getDiary(DiarySearch diarySearch) {
		
		return dao.selectDiary(diarySearch);
	}
	
	@Override
	public List<String> getColors(int diaryId) {
		
		return dao.selectColor(diaryId);
	}
	
	@Override
	public boolean editDiary(Diary diary) {
		
		try {
			dao.updateDiary(diary);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Diary> getnotifyMonthDiaries(int referenceId) {
		return dao.selectNotifyDiary(referenceId);
	}
}
