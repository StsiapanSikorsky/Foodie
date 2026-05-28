CREATE TABLE booking (
    id BIGSERIAL PRIMARY KEY ,
    user_id INTEGER NOT NULL ,
    restaurant_id INTEGER NOT NULL ,
    table_number INTEGER NOT NULL ,
    guests INTEGER NOT NULL ,
    status VARCHAR(15) DEFAULT 'CREATED',
    description VARCHAR(500),
    booking_from TIMESTAMP NOT NULL ,
    booking_to TIMESTAMP NOT NULL ,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);