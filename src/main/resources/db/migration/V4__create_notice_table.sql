CREATE TABLE `notice` (
                          `notice_id`	BIGINT AUTO_INCREMENT PRIMARY KEY,
                          `user_id`	bigint	NOT NULL,
                          `notice_type`	char(30) NOT NULL,
                          `created_at`	timestamp NOT NULL,
                          `notice_path`	varchar(50)	NULL,
                          `notice_check_date`	timestamp	NULL,
                          `updated_at`	timestamp NOT NULL,
                          FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
);
