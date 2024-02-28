
ALTER TABLE walker_adjust_detail
    ADD COLUMN pay_history_id bigint NOT NULL,
    ADD CONSTRAINT pay_history_id FOREIGN KEY (pay_history_id) REFERENCES  pay_history (pay_history_id);
