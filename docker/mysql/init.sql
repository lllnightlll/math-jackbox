CREATE DATABASE IF NOT EXISTS quizdb
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'quizuser'@'%' IDENTIFIED BY 'quizpass';
GRANT ALL PRIVILEGES ON quizdb.* TO 'quizuser'@'%';
FLUSH PRIVILEGES;

USE quizdb;

CREATE TABLE IF NOT EXISTS players (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    nickname   VARCHAR(255),
    score      INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS questions (
    id                   BIGINT        NOT NULL AUTO_INCREMENT,
    text                 TEXT          NOT NULL,
    correct_option_index INT           NOT NULL,
    PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS question_option (
    question_id  BIGINT       NOT NULL,
    option_text  VARCHAR(500) NOT NULL,

    CONSTRAINT fk_question_option_question
    FOREIGN KEY (question_id)
    REFERENCES questions(id)
    ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_question_option_question_id
    ON question_option (question_id);