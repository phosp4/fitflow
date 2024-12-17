Here's a comprehensive README based on the codebase:

# FitFlow

FitFlow is a comprehensive fitness center management system built with JavaFX that integrates the management of members, trainers, equipment, and activities into a unified solution.

## Features

- **User Authentication & Authorization**
  - Secure login/signup system
  - Role-based access control (Admin, User, Trainer)
  - Password encryption using BCrypt

- **Member Management**
  - Member profiles with personal information
  - Credit balance system
  - Visit tracking with QR code generation
  - Reservation management

- **Trainer Management**
  - Trainer profiles and specializations
  - Availability scheduling
  - Reservation handling

- **Financial Management**
  - Credit transaction system
  - Payment processing
  - Transaction history tracking

- **Internationalization**
  - Support for multiple languages (English, German, Slovak)
  - Easy language switching in settings

## Technology Stack

- **Frontend**: JavaFX with MaterialFX components
- **Backend**: Java
- **Database**: SQLite
- **Build Tool**: Maven
- **Additional Libraries**:
  - Spring Security (Password encryption)
  - ZXing (QR code generation)
  - Lombok (Boilerplate reduction)
  - JUnit (Testing)

## Getting Started

### Prerequisites

- Java 22 or higher
- Maven

### Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/fitflow.git
```

2. Navigate to project directory:
```bash
cd fitflow
```

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn javafx:run
```

### Default Admin Account

The system automatically creates a default admin account on first run:
- Username: admin
- Password: admin

## Database Schema

The application uses SQLite with the following main tables:
- users
- roles
- credit_transactions
- visits
- reservations
- trainers_intervals
- trainer_specializations

Reference to schema:

```12:113:src/test/resources/init.sql
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
    credit_balance REAL      NOT NULL,
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
    amount                      REAL,
    credit_transaction_type_id  INTEGER   NOT NULL,
    created_at                  TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    updated_at                  TIMESTAMP NOT NULL DEFAULT (CURRENT_TIMESTAMP),
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
    credit_transaction_id INTEGER   NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES users (id),
    FOREIGN KEY (status) REFERENCES reservation_statuses (id),
    FOREIGN KEY (credit_transaction_id) REFERENCES credit_transactions (id)
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
    name VARCHAR(45) NOT NULL
);

CREATE TABLE IF NOT EXISTS trainers_have_specializations
(
    trainer_id        INTEGER NOT NULL,
    specialization_id INTEGER NOT NULL,
    PRIMARY KEY (trainer_id, specialization_id),
    FOREIGN KEY (trainer_id) REFERENCES users (id),
    FOREIGN KEY (specialization_id) REFERENCES trainer_specializations (id)
);
```


## Internationalization

The application supports multiple languages through resource bundles. Language files are located in:
```
src/main/resources/sk/upjs/ics/MyResources/
```

Supported languages:
- English (MyResources_en.properties)
- German (MyResources_de.properties)
- Slovak (MyResources_sk.properties)

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- MaterialFX for the modern UI components
- Spring Security for authentication handling
- ZXing for QR code functionality