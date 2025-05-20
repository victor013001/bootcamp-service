CREATE TABLE bootcamp (
	id BIGSERIAL NOT NULL,
	name VARCHAR(64) NOT NULL,
	description VARCHAR(255),
	launch_date DATE,
    duration_in_weeks INTEGER,
	PRIMARY KEY(id)
);

-- REVERT
-- DROP TABLE IF EXISTS bootcamp;
-- DELETE FROM flyway_schema_history WHERE version = '1.0';