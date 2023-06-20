package com.ggukgguk.api.diary.service;

import java.util.List;

import com.ggukgguk.api.diary.vo.Diary;
import com.ggukgguk.api.diary.vo.DiaryColor;
import com.ggukgguk.api.diary.vo.DiaryMonth;
import com.ggukgguk.api.diary.vo.DiarySearch;

public interface DiaryService {

	public List<Diary> getDiaries(DiarySearch diarySearch);

	public DiaryMonth getDiary(DiarySearch diarySearch);

	public List<String> getColors(int diaryId);
	
	public boolean editDiary(Diary diary);

	public List<Diary> getnotifyMonthDiaries(int referenceId);


}
