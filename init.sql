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
    check_in_time  TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
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

INSERT INTO users (id, role, email, password_hash, first_name, last_name, credit_balance, phone, birth_date, active, created_at, updated_at) VALUES
(1, 1, 'user1@example.com', '$2b$10$xY/j5.25h8xL0t5c.jR7Bu.5x74j9.2gV9/0/j109.4e1Z9.3160', 'John', 'Doe', 100.00, '123-456-7890', '1990-01-01', 1, '2023-11-23 12:34:56', '2023-11-23 12:34:56'),
(2, 2, 'user2@example.com', '$2b$10$xY/j5.25h8xL0t5c.jR7Bu.5x74j9.2gV9/0/j109.4e1Z9.3160', 'Jane', 'Smith', 50.50, '987-654-3210', '1995-02-15', 1, '2023-11-23 12:34:56', '2023-11-23 12:34:56'),
(3, 3, 'admin@example.com', '$2b$10$xY/j5.25h8xL0t5c.jR7Bu.5x74j9.2gV9/0/j109.4e1Z9.3160', 'Alice', 'Johnson', 0.00, '555-555-5555', '2000-03-30', 1, '2023-11-23 12:34:56', '2023-11-23 12:34:56');

INSERT INTO reservations (id, customer_id, status, note, created_at, updated_at, transaction_id) VALUES
(1, 10, 'pending', 'Please focus on core strength exercises.', '2023-11-23 12:34:56', '2023-11-23 12:34:56', 100),
(2, 20, 'confirmed', 'No special requests.', '2023-11-24 10:00:00', '2023-11-24 10:00:00', 200),
(3, 10, 'canceled', 'Canceled due to illness.', '2023-11-25 14:00:00', '2023-11-25 14:00:00', 300);

INSERT INTO trainers_intervals (id, trainer_id, day, start_time, end_time, reservation_id) VALUES
(100, 1, '2023-11-23', '12:34:56', '13:45:00', 1),
(200, 2, '2023-11-24', '10:00:00', '11:30:00', 2),
(300, 1, '2023-11-25', '14:00:00', '15:15:00', 3);

INSERT INTO transactions (id, user_id, amount, transaction_type_id, created_at) VALUES
(100, 10, 10.00, 1, '2023-11-23 12:34:56'),
(200, 20, 20.00, 2, '2023-11-24 10:00:00'),
(300, 10, 30.00, 1, '2023-11-25 14:00:00');

INSERT INTO visits (id, user_id, check_in_time, check_out_time, visit_secret, transaction_id) VALUES
(1, 10, '2023-11-23 12:34:56', '2023-11-23 13:45:00', 'secret123', 100),
(2, 20, '2023-11-24 10:00:00', '2023-11-24 11:30:00', 'secret456', 200),
(3, 10, '2023-11-25 14:00:00', '2023-11-25 15:15:00', 'secret789', 300);