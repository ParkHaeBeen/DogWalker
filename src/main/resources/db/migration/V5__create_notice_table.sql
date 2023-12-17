CREATE TABLE `notice` (
                          `notice_id`	BIGINT AUTO_INCREMENT PRIMARY KEY,
                          `user_Id`	bigint	NOT NULL,
                          `notice_type`	char(10) NOT NULL,
                          `notice_created_at`	timestamp NOT NULL,
                          `notice_path`	varchar(20)	NULL,
                          `notice_check_date`	timestamp	NULL,
                          `notice_updated_at`	timestamp NOT NULL
);