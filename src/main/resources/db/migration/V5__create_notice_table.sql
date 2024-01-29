CREATE TABLE `notice` (
                          `notice_id`	BIGINT AUTO_INCREMENT PRIMARY KEY,
                          `user_Id`	bigint	NOT NULL,
                          `notice_type`	char(10) NOT NULL,
                          `created_at`	timestamp NOT NULL,
                          `notice_path`	varchar(20)	NULL,
                          `notice_check_date`	timestamp	NULL,
                          `updated_at`	timestamp NOT NULL
);

ALTER TABLE `notice` ADD CONSTRAINT `FK_Users_TO_notice_1` FOREIGN KEY (
                                                                        `user_id`
    )
    REFERENCES `users` (
                        `user_id`
        );
