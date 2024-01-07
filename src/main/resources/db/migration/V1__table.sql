use walker;
CREATE TABLE `users` (
                         `user_id`	BIGINT AUTO_INCREMENT PRIMARY KEY,
                         `user_email`	varchar(100) NOT NULL,
                         `user_phone_number`	varchar(20)	NOT NULL,
                         `user_lat`	double	NOT NULL,
                         `user_lnt`	double	NOT NULL,
                         `user_role`	varchar(10)	NOT NULL,
                         `user_name`	varchar(100)	NOT NULL,
                         `user_created_at`	timestamp NOT	NULL,
                         `user_updated_at`	timestamp	NOT NULL
);

CREATE TABLE `walker_schedule` (
                                   `walker_sc_id`	BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   `walker_id`	bigint	NOT NULL,
                                   `unavailable_day`	varchar(4) NOT NULL,
                                   `unavailable_time_start`	int	NOT NULL,
                                   `unavailable_time_end`	int	NOT NULL
);

CREATE TABLE `walker_schedule_temporary` (
                                             `walker_sc_temp_id`	BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             `walker_Id`	bigint	NOT NULL,
                                             `unavailable_date`	timestamp	NOT NULL
);

CREATE TABLE `refresh_token` (
                                 `refresh_token`	varchar(40)	 PRIMARY KEY,
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
                                          `walker_reserve_service_id`	bigint AUTO_INCREMENT PRIMARY KEY,
                                          `walker_id`	bigint	NOT NULL,
                                          `customer_id`	bigint	NOT NULL,
                                          `walker_reserve_service_created_at`	timestamp NOT NULL,
                                          `walker_service_date`	timestamp NOT NULL,
                                          `walker_service_time_unit` int NOT NULL,
                                          `walker_service_status`	char(15)	NOT NULL,
                                          `walker_reserve_service_updated_at`	timestamp NOT NULL,
                                          `walker_reserve_service_price` int NOT NULL,
                                          `pay_history_id` bigint NOT NULL
);

CREATE TABLE `pay_history` (
                               `pay_history_id`	bigint  AUTO_INCREMENT	PRIMARY KEY,
                               `user_id`	bigint	NOT NULL,
                               `pay_price`	int NOT NULL,
                               `pay_status`	char(20) NOT NULL,
                               `pay_created_at`	timestamp NOT NULL,
                               `pay_updated_at`	timestamp NOT NULL,
                               `pay_method`	varchar(50) NOT NULL
);

CREATE TABLE BATCH_JOB_INSTANCE  (
         JOB_INSTANCE_ID BIGINT  NOT NULL PRIMARY KEY ,
         VERSION BIGINT ,
         JOB_NAME VARCHAR(100) NOT NULL,
         JOB_KEY VARCHAR(32) NOT NULL,
         constraint JOB_INST_UN unique (JOB_NAME, JOB_KEY)
) ENGINE=InnoDB;

CREATE TABLE BATCH_JOB_EXECUTION  (
          JOB_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
          VERSION BIGINT  ,
          JOB_INSTANCE_ID BIGINT NOT NULL,
          CREATE_TIME DATETIME(6) NOT NULL,
          START_TIME DATETIME(6) DEFAULT NULL ,
          END_TIME DATETIME(6) DEFAULT NULL ,
          STATUS VARCHAR(10) ,
          EXIT_CODE VARCHAR(2500) ,
          EXIT_MESSAGE VARCHAR(2500) ,
          LAST_UPDATED DATETIME(6),
          constraint JOB_INST_EXEC_FK foreign key (JOB_INSTANCE_ID)
              references BATCH_JOB_INSTANCE(JOB_INSTANCE_ID)
) ENGINE=InnoDB;

CREATE TABLE BATCH_JOB_EXECUTION_PARAMS  (
                 JOB_EXECUTION_ID BIGINT NOT NULL ,
                 PARAMETER_NAME VARCHAR(100) NOT NULL ,
                 PARAMETER_TYPE VARCHAR(100) NOT NULL ,
                 PARAMETER_VALUE VARCHAR(2500) ,
                 IDENTIFYING CHAR(1) NOT NULL ,
                 constraint JOB_EXEC_PARAMS_FK foreign key (JOB_EXECUTION_ID)
                     references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ENGINE=InnoDB;

CREATE TABLE BATCH_STEP_EXECUTION  (
           STEP_EXECUTION_ID BIGINT  NOT NULL PRIMARY KEY ,
           VERSION BIGINT NOT NULL,
           STEP_NAME VARCHAR(100) NOT NULL,
           JOB_EXECUTION_ID BIGINT NOT NULL,
           CREATE_TIME DATETIME(6) NOT NULL,
           START_TIME DATETIME(6) DEFAULT NULL ,
           END_TIME DATETIME(6) DEFAULT NULL ,
           STATUS VARCHAR(10) ,
           COMMIT_COUNT BIGINT ,
           READ_COUNT BIGINT ,
           FILTER_COUNT BIGINT ,
           WRITE_COUNT BIGINT ,
           READ_SKIP_COUNT BIGINT ,
           WRITE_SKIP_COUNT BIGINT ,
           PROCESS_SKIP_COUNT BIGINT ,
           ROLLBACK_COUNT BIGINT ,
           EXIT_CODE VARCHAR(2500) ,
           EXIT_MESSAGE VARCHAR(2500) ,
           LAST_UPDATED DATETIME(6),
           constraint JOB_EXEC_STEP_FK foreign key (JOB_EXECUTION_ID)
               references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ENGINE=InnoDB;

CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT  (
                   STEP_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                   SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                   SERIALIZED_CONTEXT TEXT ,
                   constraint STEP_EXEC_CTX_FK foreign key (STEP_EXECUTION_ID)
                       references BATCH_STEP_EXECUTION(STEP_EXECUTION_ID)
) ENGINE=InnoDB;

CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT  (
                  JOB_EXECUTION_ID BIGINT NOT NULL PRIMARY KEY,
                  SHORT_CONTEXT VARCHAR(2500) NOT NULL,
                  SERIALIZED_CONTEXT TEXT ,
                  constraint JOB_EXEC_CTX_FK foreign key (JOB_EXECUTION_ID)
                      references BATCH_JOB_EXECUTION(JOB_EXECUTION_ID)
) ENGINE=InnoDB;

CREATE TABLE BATCH_STEP_EXECUTION_SEQ (
              ID BIGINT NOT NULL,
              UNIQUE_KEY CHAR(1) NOT NULL,
              constraint UNIQUE_KEY_UN unique (UNIQUE_KEY)
) ENGINE=InnoDB;

INSERT INTO BATCH_STEP_EXECUTION_SEQ (ID, UNIQUE_KEY) select * from (select 0 as ID, '0' as UNIQUE_KEY) as tmp where not exists(select * from BATCH_STEP_EXECUTION_SEQ);

CREATE TABLE BATCH_JOB_EXECUTION_SEQ (
             ID BIGINT NOT NULL,
             UNIQUE_KEY CHAR(1) NOT NULL,
             constraint UNIQUE_KEY_UN unique (UNIQUE_KEY)
) ENGINE=InnoDB;

INSERT INTO BATCH_JOB_EXECUTION_SEQ (ID, UNIQUE_KEY) select * from (select 0 as ID, '0' as UNIQUE_KEY) as tmp where not exists(select * from BATCH_JOB_EXECUTION_SEQ);

CREATE TABLE BATCH_JOB_SEQ (
   ID BIGINT NOT NULL,
   UNIQUE_KEY CHAR(1) NOT NULL,
   constraint UNIQUE_KEY_UN unique (UNIQUE_KEY)
) ENGINE=InnoDB;

INSERT INTO BATCH_JOB_SEQ (ID, UNIQUE_KEY) select * from (select 0 as ID, '0' as UNIQUE_KEY) as tmp where not exists(select * from BATCH_JOB_SEQ);
