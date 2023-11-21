CREATE TABLE `Users` (
                         `user_Id`	bigint	NOT NULL,
                         `user_email`	varchar(100) NOT NULL,
                         `user_phoneNumber`	varchar(20)	NOT NULL,
                         `user_lat`	double	NOT NULL,
                         `user_lnt`	double	NOT NULL,
                         `user_role`	varchar(10)	NOT NULL,
                         `user_name`	varchar(100)	NOT NULL,
                         `user_created_at`	timestamp	NOT NULL,
                         `user_updated_at`	timestamp	NOT NULL
);

CREATE TABLE `walker_schedule` (
                                   `walker_sc_Id`	bigint	NOT NULL,
                                   `walker_id`	bigint	NOT NULL,
                                   `unavailable_day`	varchar(4) NOT NULL,
                                   `unavailable_time_start`	int	NOT NULL,
                                   `unavailable_time_end`	int	NOT NULL
);

CREATE TABLE `refresh_token` (
                                 `refresh_token`	varchar(40)	NOT NULL,
                                 `refresh_token_userId`	bigint	NOT NULL,
                                 `refresh_token_expired_at`	timestamp	NOT NULL
);

CREATE TABLE `customer_dog_info` (
                                     `dog_id`	bigint	NOT NULL,
                                     `user_Id`	bigint	NOT NULL,
                                     `dog_img_url`	varchar(100) NOT NULL,
                                     `dog_birth_date`	timestamp NOT NULL,
                                     `dog_name`	varchar(10) NOT NULL,
                                     `dog_type`	varchar(20)	NOT NULL,
                                     `dog_description`	varchar(500) NOT NULL
);