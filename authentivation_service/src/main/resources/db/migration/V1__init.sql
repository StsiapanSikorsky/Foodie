CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY ,
    user_name VARCHAR(255),
    email VARCHAR(100) UNIQUE NOT NULL ,
    password VARCHAR(255) NOT NULL ,
    deleted BOOLEAN DEFAULT false,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
    id INTEGER PRIMARY KEY ,
    name VARCHAR(50)
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL ,
    role_id BIGINT NOT NULL ,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE refresh_token (
    id BIGSERIAL PRIMARY KEY ,
    refresh_token VARCHAR(100) NOT NULL UNIQUE ,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id INTEGER NOT NULL ,
    CONSTRAINT FK_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT INTO roles (id, name) VALUES
                                      (1, 'USER'),
                                      (2, 'OWNER'),
                                      (3, 'ADMIN');
