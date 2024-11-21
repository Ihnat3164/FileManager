CREATE TABLE USERS (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(100) NOT NULL,
                       role VARCHAR(10) NOT NULL CHECK (role IN ('ADMIN', 'USER'))
);

CREATE TABLE "meta-inf" (
                            id VARCHAR(255) PRIMARY KEY,
                            title VARCHAR(255),
                            type VARCHAR(100),
                            path VARCHAR(255),
                            author VARCHAR(100) REFERENCES USERS(email) ON DELETE CASCADE
);