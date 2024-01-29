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

ALTER TABLE `walker_adjust` ADD CONSTRAINT `FK_Users_TO_walker_adjust_1` FOREIGN KEY (
                                                                                      `user_id`
    )
    REFERENCES `users` (
                        `user_id`
        );

ALTER TABLE `walker_adjust_detail` ADD CONSTRAINT `FK_walker_adjust_TO_walker_adjust_detail_1` FOREIGN KEY (
                                                                                                            `walker_adjust_id`
    )
    REFERENCES `walker_adjust` (
                                `walker_adjust_id`
        );

ALTER TABLE `walker_adjust_detail` ADD CONSTRAINT `FK_walker_reserve_service_TO_walker_adjust_detail_1` FOREIGN KEY (
                                                                                                                     `walker_reserve_service_id`
    )
    REFERENCES `walker_reserve_service` (
                                         `walker_reserve_service_id`
        );


