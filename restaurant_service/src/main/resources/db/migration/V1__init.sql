CREATE TABLE restaurants (
    id BIGSERIAL PRIMARY KEY ,
    restaurant_name VARCHAR(255) NOT NULL ,
    description TEXT,
    city VARCHAR(100) NOT NULL ,
    address VARCHAR(255) NOT NULL ,
    type VARCHAR(50),
    work_from_at_weekend TIME,
    work_to_at_weekend TIME,
    work_from_at_workday TIME,
    work_to_at_workday TIME,
    restaurants_weekends JSONB,
    deleted BOOLEAN DEFAULT false,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    owner_id INTEGER NOT NULL
);
