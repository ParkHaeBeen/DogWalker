CREATE TABLE `walker_adjust` (
                                 `walker_adjust_id`	bigint	 AUTO_INCREMENT	PRIMARY KEY,
                                 `user_id`	bigint	NOT NULL,
                                 `walker_adjust_date`	timestamp  NOT NULL,
                                 `walker_ttlprice`	bigint  NOT NULL,
                                 `walker_adjust_status`	char(30)  NOT NULL,
                                 `walker_adjust_period_start` timestamp NOT NULL,
                                 `walker_adjust_period_end` timestamp NOT NULL,
                                 `created_at`	timestamp	NOT NULL,
                                 `updated_at`	timestamp	NOT NULL,
                                 FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)

);

CREATE TABLE `walker_adjust_detail` (
                                        `walker_adjust_detail_id`	bigint	 AUTO_INCREMENT	PRIMARY KEY,
                                        `walker_adjust_id`	bigint	NOT NULL,
                                        `walker_adjust_price`	bigint NOT 	NULL,
                                        `walker_adjust_status`	char(30) NOT NULL,
                                        FOREIGN KEY (`walker_adjust_id`) REFERENCES `walker_adjust` (`walker_adjust_id`)
);

