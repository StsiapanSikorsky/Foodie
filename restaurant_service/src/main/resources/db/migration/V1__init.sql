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
    image_urls JSONB,
    deleted BOOLEAN DEFAULT false,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    owner_id INTEGER NOT NULL
);

CREATE TABLE restaurant_table (
    id BIGSERIAL PRIMARY KEY ,
    number_of_table INTEGER NOT NULL ,
    description TEXT ,
    capacity INTEGER NOT NULL ,
    restaurant_id BIGINT NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE ,
    CONSTRAINT unique_restaurant_table_number UNIQUE (restaurant_id, number_of_table)
);

CREATE INDEX idx_restaurants_id ON restaurants(id);
CREATE INDEX idx_restaurants_id_deleted ON restaurants(id, deleted);
CREATE INDEX idx_restaurants_owner_id ON restaurants(owner_id);
CREATE INDEX idx_restaurant_table_id ON restaurant_table(id);
CREATE INDEX idx_restaurant_table_number_of_table ON restaurant_table(number_of_table);
CREATE INDEX idx_restaurant_table_restaurant_id ON restaurant_table(restaurant_id);