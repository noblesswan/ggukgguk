package com.ggukgguk.api.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Main {
		
	int totalMember; 
	// SELECT COUNT(*) AS member_count FROM member;
	
	int todayMember; 
//	SELECT COUNT(*) AS num_members FROM member
//	WHERE member_created_at >= CONCAT(CURDATE(), ' 00:00:00') AND member_created_at <= CONCAT(CURDATE(), ' ', TIME(NOW()));
	
	int totalContent; 
	// SELECT COUNT(record_id) AS total_content FROM record
	
	int todayContent; 
//	SELECT COUNT(record_id) as today_content FROM record
//	WHERE DATE(record_created_at) = CURDATE();
	
	
	
	
//	int mainResult = new mainResult(totalMember, todayMember, totalContent, todayContent) {
//		this.totalMember = totalMember;
//		this.todayContent = todayContent;
//		this.totalCotent = totalContent;
//		this.todayMember = todayMember;
//		
//		return ;
//	}
	
	
}
