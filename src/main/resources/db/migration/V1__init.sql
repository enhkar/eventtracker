CREATE TABLE users (
    id serial,
    created_at timestamp NOT NULL,
    name VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled boolean NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE roles (
    id serial,
    name VARCHAR(50) NOT NULL UNIQUE,

    PRIMARY KEY (id)
);

CREATE TABLE users_roles (
    id serial,
    user_id bigint,
    role_id bigint,

    PRIMARY KEY (id),

    FOREIGN KEY(user_id) REFERENCES users (id),
    FOREIGN KEY(role_id) REFERENCES roles (id)
);

CREATE TABLE categories (
    id serial,
    name VARCHAR(150) NOT NULL UNIQUE,
    alias VARCHAR(255) NOT NULL UNIQUE,
    PRIMARY KEY (id)
);

CREATE TABLE events (
    id serial,
    updated_at timestamp NOT NULL,
    name VARCHAR(150) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    started_at timestamp NOT NULL,
    image_data bytea NOT NULL,
    user_id bigint NOT NULL,
    category_id bigint NOT NULL,
    PRIMARY KEY (id),

    FOREIGN KEY(user_id) REFERENCES users (id),
    FOREIGN KEY(category_id) REFERENCES categories (id)
);

CREATE TABLE LOGS
   (USER_ID VARCHAR(20)    NOT NULL,
    DATED   DATE           NOT NULL,
    LOGGER  VARCHAR(50)    NOT NULL,
    LEVEL   VARCHAR(10)    NOT NULL,
    MESSAGE VARCHAR(1000)  NOT NULL
   );