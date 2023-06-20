DROP DATABASE IF EXISTS ggukgguk;
CREATE DATABASE ggukgguk;
USE ggukgguk;

CREATE TABLE `member_social_type` (
	`member_social_id`    VARCHAR(16)    NOT NULL    PRIMARY KEY,
	`member_social_comment`    VARCHAR(36)    NULL
);

INSERT INTO `member_social_type` VALUES('KAKAO', '카카오');
INSERT INTO `member_social_type` VALUES('NAVER', '네이버');
INSERT INTO `member_social_type` VALUES('GOOGLE', '구글');

CREATE TABLE `member` (
    `member_id`    VARCHAR(16)    NOT NULL    PRIMARY KEY,
    `member_pw`    VARCHAR(128)    NOT NULL,
    `member_name`    VARCHAR(16)    NOT NULL,
    `member_nickname`    VARCHAR(16)    NOT NULL,
    `member_email`    VARCHAR(128)    NULL    UNIQUE,
    `member_phone`    VARCHAR(12)    NOT NULL,
    `member_birth`    DATE    NOT NULL,
    `member_created_at`    DATETIME    NOT NULL    DEFAULT NOW(),
    `member_activated`    BOOLEAN    NOT NULL    DEFAULT TRUE    COMMENT '탈퇴처리 시 아이디를 제외하고 공백 지정 후 본 컬럼 true 업데이트',
	`member_authority`    ENUM('GUEST', 'NORMAL', 'SERVICE_ADMIN', 'SYSTEM_ADMIN')    NOT NULL,
    `member_social`    VARCHAR(16)    NULL,
    `member_allow_email`    BOOLEAN    NOT NULL    DEFAULT TRUE
	FOREIGN KEY (`member_social`) REFERENCES `member_social_type` (`member_social_id`)
);

INSERT INTO `member` VALUES('hong', '$2y$04$G92ppy9s0BVNuuqbLjo.k.4M.EiVMOId0Dm2hYUJgJe13a.pa0lzS',
								'홍길동', '길동길동', 'hong@tales.org', '01012341111', '1443-01-01',
								DEFAULT, TRUE, 'NORMAL', NULL);
INSERT INTO `member` VALUES('admin', '$2y$04$G92ppy9s0BVNuuqbLjo.k.4M.EiVMOId0Dm2hYUJgJe13a.pa0lzS',
								'관리자', '관리자', 'admin@ggukgguk.online', '01012341111', '1998-04-08',
								DEFAULT, TRUE, 'SYSTEM_ADMIN', NULL);
INSERT INTO `member` VALUES('service', '$2y$04$G92ppy9s0BVNuuqbLjo.k.4M.EiVMOId0Dm2hYUJgJe13a.pa0lzS',
								'서비스 관리자', '서비스 관리자', 'admin2@ggukgguk.online', '01012341111', '1998-04-08',
								DEFAULT, TRUE, 'SERVICE_ADMIN', NULL);

CREATE TABLE `member_verify` (
	`verify_id`    INT    NOT NULL    PRIMARY KEY    AUTO_INCREMENT,
	`verify_created_at`    DATETIME    NOT NULL    DEFAULT NOW(),
	`verify_email`    VARCHAR(128)    NOT NULL,
	`verify_code`    CHAR(5)    NOT NULL
);

CREATE TABLE `friend` (
	`friend_id`    INT    NOT NULL    PRIMARY KEY    AUTO_INCREMENT,
	`member1_id`    VARCHAR(16)    NOT NULL,
	`member2_id`    VARCHAR(16)    NOT NULL,
	`friend_created_at`    DATETIME    NOT NULL    DEFAULT NOW(),
	FOREIGN KEY (`member1_id`) REFERENCES `member` (`member_id`),
	FOREIGN KEY (`member2_id`) REFERENCES `member` (`member_id`)
);

CREATE TABLE `friend_request` (
	`friend_request_id`    INT    NOT NULL    PRIMARY KEY    AUTO_INCREMENT,
	`from_member_id`    VARCHAR(16)    NOT NULL,
	`to_member_id`    VARCHAR(16)    NOT NULL,
	`friend_request_time`    DATETIME    NOT NULL    DEFAULT NOW(),
	FOREIGN KEY (`from_member_id`) REFERENCES `member` (`member_id`),
	FOREIGN KEY (`to_member_id`) REFERENCES `member` (`member_id`)
);

CREATE TABLE `media_type` (
    `media_type_id`    VARCHAR(8)    NOT NULL    PRIMARY KEY,
    `media_type_extention`    VARCHAR(4)   NOT NULL
);

INSERT INTO `media_type` VALUES ('VIDEO', 'mp4');
INSERT INTO `media_type` VALUES ('IMAGE', 'jpg');
INSERT INTO `media_type` VALUES ('AUDIO', 'wav');

CREATE TABLE `media_file` (
    `media_file_id`    CHAR(36)    NOT NULL    PRIMARY KEY    COMMENT 'UUID',
    `media_type_id`    VARCHAR(8)    NOT NULL,
    `media_file_blocked`    BOOLEAN    NULL,
    `media_file_checked`    BOOLEAN    NOT NULL    DEFAULT FALSE,
    FOREIGN KEY (`media_type_id`) REFERENCES `media_type` (`media_type_id`)
);

CREATE TABLE `record` (
    `record_id`    INT    NOT NULL    PRIMARY KEY    AUTO_INCREMENT,
    `member_id`    VARCHAR(16)    NOT NULL,
    `record_comment`    VARCHAR(512)    NULL,
    `record_created_at`    DATETIME    NOT NULL    DEFAULT NOW(),
    `media_file_id`    CHAR(36)    NULL    COMMENT 'UUID',
    `record_location_y`    FLOAT    NULL,
    `record_location_x`    FLOAT    NULL,
    `record_is_open`    BOOLEAN    NOT NULL,
    `record_share_to`    VARCHAR(16)    NULL,
    `record_share_accepted`    BOOLEAN    NULL,
	FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`),
	FOREIGN KEY (`media_file_id`) REFERENCES `media_file` (`media_file_id`)
);

CREATE TABLE `reply` (
    `reply_id`    INT    NOT NULL    PRIMARY KEY    AUTO_INCREMENT,
    `reply_content`    VARCHAR(256)    NOT NULL,
    `reply_date`    DATETIME    NOT NULL,
    `record_id`    INT    NOT NULL,
    `member_id`    VARCHAR(16)    NOT NULL,
    FOREIGN KEY (`record_id`) REFERENCES `record` (`record_id`),
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
);

CREATE TABLE `diary` (
    `diary_id`    INT    NOT NULL    PRIMARY KEY    AUTO_INCREMENT,
    `member_id`    VARCHAR(16)    NOT NULL,
    `diary_year`    CHAR(4)    NOT NULL,
    `diary_month`    CHAR(2)    NOT NULL,
    `main_color`    CHAR(10)    NOT NULL,
    `main_keyword`    VARCHAR(16)    NOT NULL,
    FOREIGN KEY (`member_id`) REFERENCES `member` (`member_id`)
);
ALTER TABLE `diary` ADD CONSTRAINT `diary_UN` UNIQUE KEY (`member_id`,`diary_year`,`diary_month`);


CREATE TABLE `record_keyword` (
    `record_keyword_id`    INT    NOT NULL    PRIMARY KEY    AUTO_INCREMENT,
    `record_id`    INT    NOT NULL,
    `record_keyword`    VARCHAR(16)    NOT NULL,
    FOREIGN KEY (`record_id`) REFERENCES `record` (`record_id`)
);
ALTER TABLE record_keyword ADD CONSTRAINT record_keyword_UN UNIQUE KEY (record_id,record_keyword);

CREATE TABLE `diary_keyword` (
    `diary_keyword_id`    INT    NOT NULL    PRIMARY KEY    AUTO_INCREMENT,
    `diary_id`    INT    NOT NULL,
    `diary_keyword`    VARCHAR(16)    NOT NULL,
    `diary_freq`    INT    NOT NULL,
    FOREIGN KEY (`diary_id`) REFERENCES `diary` (`diary_id`)
);
ALTER TABLE diary_keyword ADD CONSTRAINT diary_keyword_UN UNIQUE KEY (diary_keyword,diary_id);

CREATE TABLE `diary_color` (
    `diary_color_id`    INT    NOT NULL    PRIMARY KEY    AUTO_INCREMENT,
    `diary_id`    INT    NOT NULL,
    `diary_color`    VARCHAR(16)    NOT NULL,
    FOREIGN KEY (`diary_id`) REFERENCES `diary` (`diary_id`)
);

CREATE TABLE `notification_type` (
    `notification_type_id`    VARCHAR(16)    NOT NULL    PRIMARY KEY,
	`notification_type_comment`    VARCHAR(36)    NULL
);
INSERT INTO `notification_type` VALUES ('FRIEND_REQUEST', '새로운 친구 요청');
INSERT INTO `notification_type` VALUES ('MONTHLY_ANALYSIS', '월말결산 완료');
INSERT INTO `notification_type` VALUES ('EXCHANGE_DIARY', '교환 일기 요청');
INSERT INTO `notification_type` VALUES ('NEW_REPLY', '새로운  댓글');
INSERT INTO `notification_type` VALUES ('FRIEND_BIRTHDAY', '친구 생일');

CREATE TABLE `notification` (
    `notification_id`    INT    NOT NULL    PRIMARY KEY    AUTO_INCREMENT,
    `receiver_id`    VARCHAR(16)    NOT NULL,
    `notification_type_id`    VARCHAR(16)    NOT NULL,
    `notification_created_at`    DATETIME    NOT NULL    DEFAULT NOW(),
    `reference_id`    INT    NOT NULL,
    `notification_is_read`    BOOLEAN    NOT NULL    DEFAULT FALSE,
    `notification_message`    VARCHAR(128)    NULL,
    FOREIGN KEY (`receiver_id`) REFERENCES `member` (`member_id`),
    FOREIGN KEY (`notification_type_id`) REFERENCES `notification_type` (`notification_type_id`)
);

CREATE TABLE `notice` (
    `notice_id`    INT    NOT NULL    PRIMARY KEY    AUTO_INCREMENT,
    `notice_title`    VARCHAR(32)    NOT NULL,
    `notice_content`    VARCHAR(512)    NOT NULL,
    `notice_created_at`    DATETIME    NOT NULL    DEFAULT NOW(),
    `notice_is_emergency`    BOOLEAN    NOT NULL    DEFAULT FALSE
);

CREATE TABLE `media_file_blocked_history` (
	`media_file_blocked_history_id`    INT    NOT NULL    PRIMARY KEY    AUTO_INCREMENT,
	`media_file_id`    CHAR(36)    NOT NULL,
	`media_file_detected_label`    VARCHAR(256)    NOT NULL,
	`media_file_detected_weights`    FLOAT    NOT NULL,
	`media_file_checked_at`    DATETIME    NOT NULL    DEFAULT NOW(),
	FOREIGN KEY (`media_file_id`) REFERENCES `media_file` (`media_file_id`)
);

CREATE TABLE `media_file_recheck_request` (
	`media_file_recheck_request_id`    INT    NOT NULL    PRIMARY KEY    AUTO_INCREMENT,
	`media_file_id`    CHAR(36)    NOT NULL,
	`media_file_recheck_request_claim`    VARCHAR(1024)    NOT NULL,
	`media_file_recheck_request_reply`    VARCHAR(1024)    NULL,
	`media_file_recheck_request_status`    ENUM('BEFORE', 'PROCEEDING', 'REJECTED', 'PASSED')    NOT NULL,
	`media_file_recheck_request_created_at`    DATETIME    NOT NULL    DEFAULT NOW(),
	FOREIGN KEY (`media_file_id`) REFERENCES `media_file` (`media_file_id`)
);


