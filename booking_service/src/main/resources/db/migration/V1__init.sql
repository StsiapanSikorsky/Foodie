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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP default CURRENT_TIMESTAMP
);

CREATE INDEX idx_booking_id ON booking(id);
CREATE INDEX idx_booking_user_id ON booking(user_id);
CREATE INDEX idx_booking_restaurant_id ON booking(restaurant_id);
CREATE INDEX idx_booking_conflict_check ON booking(restaurant_id, table_number, booking_from, booking_to);