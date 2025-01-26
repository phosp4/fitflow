DROP TABLE IF EXISTS visits;
DROP TABLE IF EXISTS credit_transactions;
DROP TABLE IF EXISTS credit_transaction_types;
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
    role_id        INTEGER   NOT NULL,
    email          TEXT      NOT NULL,
    salt           TEXT      NOT NULL,
    password_hash  TEXT      NOT NULL,
    first_name     TEXT      NOT NULL,
    last_name      TEXT      NOT NULL,
    credit_balance INTEGER   NOT NULL,
    phone          TEXT,
    birth_date     DATE      NOT NULL,
    active         BOOLEAN   NOT NULL DEFAULT 1,
    created_at     TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    updated_at     TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE IF NOT EXISTS credit_transaction_types
(
    id   INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS credit_transactions
(
    id                          INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id                     INT       NOT NULL,
    amount                      INTEGER,
    credit_transaction_type_id  INTEGER   NOT NULL,
    created_at                  TIMESTAMP NOT NULL DEFAULT (datetime('now','localtime')), -- to include time shift
    updated_at                  TIMESTAMP NOT NULL DEFAULT (datetime('now','localtime')), -- to include time shift
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (credit_transaction_type_id) REFERENCES credit_transaction_types (id)
);

CREATE TABLE IF NOT EXISTS visits
(
    id             INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id        INTEGER   NOT NULL,
    check_in_time  TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    check_out_time TIMESTAMP,
    visit_secret   TEXT      NOT NULL,
    credit_transaction_id INTEGER, -- can be null, if the visit has not ended yet
    created_at     TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    updated_at     TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (credit_transaction_id) REFERENCES credit_transactions (id)
);

INSERT INTO roles (name)
VALUES ('admin'),
       ('user');

INSERT INTO credit_transaction_types (id, name)
VALUES (3, 'refund'),
       (1, 'visit'),
       (2, 'credit_purchase');

INSERT INTO users (id, role_id, email, salt, password_hash, first_name, last_name, credit_balance, phone, birth_date, active, created_at, updated_at) VALUES
(1, 1, 'user1@example.com', '123456', '$2b$10$xY/j5.25h8xL0t5c.jR7Bu.5x74j9.2gV9/0/j109.4e1Z9.3160', 'John', 'Doe', 10000, '123-456-7890', '1990-01-01 00:00:00.000', 1, '2023-11-23 12:34:56', '2023-11-23 12:34:56'),
(2, 2, 'user2@example.com', '123456', '$2b$10$xY/j5.25h8xL0t5c.jR7Bu.5x74j9.2gV9/0/j109.4e1Z9.3160', 'Jane', 'Smith', 5000, '987-654-3210', '1995-02-15 00:00:00.000', 1, '2023-11-23 12:34:56', '2023-11-23 12:34:56');