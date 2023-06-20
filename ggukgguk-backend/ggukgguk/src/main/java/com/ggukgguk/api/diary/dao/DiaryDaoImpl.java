package com.ggukgguk.api.diary.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ggukgguk.api.diary.vo.Diary;
import com.ggukgguk.api.diary.vo.DiaryColor;
import com.ggukgguk.api.diary.vo.DiaryMonth;
import com.ggukgguk.api.diary.vo.DiarySearch;

@Repository
public class DiaryDaoImpl implements DiaryDao {

	private Logger log = LogManager.getLogger("base");
	
	@Autowired
	SqlSession session;
	
	@Override
	public List<Diary> selectDairyList(DiarySearch diarySearch) {
		
		return session.selectList("com.ggukgguk.api.Diary.selectList", diarySearch);
	}
	
	@Override
	public DiaryMonth selectDiary(DiarySearch diarySearch) {
		
		log.debug(diarySearch);
		return session.selectOne("com.ggukgguk.api.Diary.selectOne", diarySearch);
	}
	
	@Override
	public List<String> selectColor(int diaryId) {
		
		return session.selectList("com.ggukgguk.api.Diary.selectColorList", diaryId);
	}
	
	@Override
	public void updateDiary(Diary diary) throws Exception {
		
		int affectedRow = session.update("com.ggukgguk.api.Diary.update", diary);
		
		if(affectedRow != 1) {
			throw new Exception();
		}
		
	}

	@Override
	public List<Diary> selectNotifyDiary(int referenceId) {
		return session.selectList("com.ggukgguk.api.Diary.selectdiaryNotify", referenceId);
	}
}
