CREATE TABLE `walker_adjust` (
                                 `walker_adjust_id`	bigint	 AUTO_INCREMENT	PRIMARY KEY,
                                 `user_id`	bigint	NOT NULL,
                                 `walker_adjust_date`	timestamp  NOT NULL,
                                 `walker_ttlprice`	bigint  NOT NULL,
                                 `walker_adjust_status`	char(10)  NOT NULL,
                                 `walker_adjust_period_start` timestamp NOT NULL,
                                 `walker_adjust_period_end` timestamp NOT NULL
);

CREATE TABLE `walker_adjust_detail` (
                                        `walker_adjust_detail_id`	bigint	 AUTO_INCREMENT	PRIMARY KEY,
                                        `walker_adjust_id`	bigint	NOT NULL,
                                        `walker_reserve_service_id`	bigint	NOT NULL,
                                        `walker_adjust_price`	bigint NOT 	NULL,
                                        `walker_adjust_status`	char(10) NOT NULL
);