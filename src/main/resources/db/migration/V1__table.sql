CREATE TABLE `Users` (
                         `user_id`	BIGINT AUTO_INCREMENT PRIMARY KEY,
                         `user_email`	varchar(100) NOT NULL,
                         `user_phone_number`	varchar(20)	NOT NULL,
                         `user_lat`	double	NOT NULL,
                         `user_lnt`	double	NOT NULL,
                         `user_role`	varchar(10)	NOT NULL,
                         `user_name`	varchar(100)	NOT NULL,
                         `user_created_at`	timestamp	NOT NULL,
                         `user_updated_at`	timestamp	NOT NULL
);

CREATE TABLE `walker_schedule` (
                                   `walker_sc_id`	BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   `walker_id`	bigint	NOT NULL,
                                   `unavailable_day`	varchar(4) NOT NULL,
                                   `unavailable_time_start`	int	NOT NULL,
                                   `unavailable_time_end`	int	NOT NULL
);

CREATE TABLE `refresh_token` (
                                 `refresh_token`	varchar(40)	NOT NULL,
                                 `refresh_token_user_id`	bigint	NOT NULL,
                                 `refresh_token_expired_at`	timestamp	NOT NULL
);

CREATE TABLE `customer_dog_info` (
                                     `dog_id`	BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     `user_id`	bigint	NOT NULL,
                                     `dog_img_url`	varchar(100) NOT NULL,
                                     `dog_birth_date`	timestamp NOT NULL,
                                     `dog_name`	varchar(10) NOT NULL,
                                     `dog_type`	varchar(20)	NOT NULL,
                                     `dog_description`	varchar(500) NOT NULL
);

CREATE TABLE `walker_service_price` (
                                        `walker_price_id`	BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        `walker_id`	bigint	NOT NULL,
                                        `walker_service_unit`	int	NOT NULL,
                                        `walker_service_fee`	int	NOT NULL
);

CREATE TABLE `walker_reserve_service` (
                                          `walker_reserve_service_id`	bigint AUTO_INCREMENT NOT NULL,
                                          `walker_id`	bigint	NOT NULL,
                                          `customer_id`	bigint	NOT NULL,
                                          `walker_reserve_service_created_at`	timestamp NOT NULL,
                                          `walker_service_date`	timestamp NOT NULL,
                                          `walker_service_time_unit` int NOT NULL,
                                          `walker_service_status`	char(15)	NOT NULL,
                                          `walker_reserve_service_update_at`	timestamp NOT NULL,
                                          `walker_reserve_service_price` int NOT NULL
);

CREATE TABLE `pay_history` (
                               `pay_history_id`	bigint	NOT NULL,
                               `user_id`	bigint	NOT NULL,
                               `walker_reserve_service_id`	bigint	NOT NULL,
                               `pay_price`	int NOT NULL,
                               `pay_status`	char(10) NOT NULL,
                               `pay_created_at`	timestamp NOT NULL,
                               `pay_updated_at`	timestamp NOT NULL,
                               `pay_method`	varchar(50) NOT NULL
);