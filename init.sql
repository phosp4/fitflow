DROP TABLE IF EXISTS trainers_have_specializations;
DROP TABLE IF EXISTS trainer_specializations;
DROP TABLE IF EXISTS trainers_intervals;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS reservation_statuses;
DROP TABLE IF EXISTS visits;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS transaction_types;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS roles
(
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    role           INTEGER   NOT NULL,
    email          TEXT      NOT NULL,
    password_hash  TEXT      NOT NULL,
    first_name     TEXT      NOT NULL,
    last_name      TEXT      NOT NULL,
    credit_balance REAL      NOT NULL,
    phone          TEXT,
    birth_date     DATE      NOT NULL,
    active         BOOLEAN   NOT NULL DEFAULT 1,
    created_at     TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    updated_at     TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (role) REFERENCES roles (id)
);

CREATE TABLE IF NOT EXISTS transaction_types
(
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions
(
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id             INT       NOT NULL,
    amount              REAL,
    transaction_type_id INTEGER   NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (transaction_type_id) REFERENCES transaction_types (id)
);

CREATE TABLE IF NOT EXISTS visits
(
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id        INTEGER   NOT NULL,
    check_in_time  TIMESTAMP NOT NULL,
    check_out_time TIMESTAMP,
    visit_secret   TEXT      NOT NULL,
    transaction_id INTEGER   NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (transaction_id) REFERENCES transactions (id)
);

CREATE TABLE IF NOT EXISTS reservation_statuses
(
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT
);

CREATE TABLE IF NOT EXISTS reservations
(
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    customer_id    INTEGER   NOT NULL,
    status         INTEGER   NOT NULL,
    note           TEXT,
    created_at     TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    updated_at     TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    transaction_id INTEGER   NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES users (id),
    FOREIGN KEY (status) REFERENCES reservation_statuses (id),
    FOREIGN KEY (transaction_id) REFERENCES transactions (id)
);

CREATE TABLE IF NOT EXISTS trainers_intervals
(
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    trainer_id     INTEGER NOT NULL,
    day            DATE    NOT NULL,
    start_time     TIME    NOT NULL,
    end_time       TIME    NOT NULL,
    reservation_id INTEGER,
    FOREIGN KEY (trainer_id) REFERENCES users (id),
    FOREIGN KEY (reservation_id) REFERENCES reservations (id)
);

CREATE TABLE IF NOT EXISTS trainer_specializations
(
    id    INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(45) NOT NULL
);

CREATE TABLE IF NOT EXISTS trainers_have_specializations
(
    trainer_id        INTEGER NOT NULL,
    specialization_id INTEGER NOT NULL,
    PRIMARY KEY (trainer_id, specialization_id),
    FOREIGN KEY (trainer_id) REFERENCES users (id),
    FOREIGN KEY (specialization_id) REFERENCES trainer_specializations (id)
);

INSERT INTO roles (name)
VALUES ('admin'),
       ('user'),
       ('trainer');

INSERT INTO transaction_types (name)
VALUES ('purchase'),
       ('refund');

INSERT INTO reservation_statuses (name)
VALUES ('pending'),
       ('confirmed'),
       ('cancelled');

INSERT INTO trainer_specializations (title)
VALUES ('Yoga'),
       ('Pilates'),
       ('CrossFit');