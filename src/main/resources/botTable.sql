-- liquibase formatted sql

-- changeset roman:create_table
CREATE TABLE notification_task
(
    id                     BIGINT generated by default as identity primary key,
    chat_id                BIGINT    NOT NULL,
    first_name             TEXT      NOT NULL,
    notification_text      TEXT      NOT NULL,
    notification_time      TIMESTAMP NOT NULL
);