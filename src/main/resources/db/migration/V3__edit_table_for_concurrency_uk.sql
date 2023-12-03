
ALTER TABLE walker_reserve_service ADD CONSTRAINT unique_walker_datetime UNIQUE (walker_id, walker_service_date);
