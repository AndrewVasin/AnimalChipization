CREATE TABLE IF NOT EXISTS accounts (
    id bigint PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    first_name varchar(255),
    last_name varchar(255),
    email varchar(255),
    password varchar(255),
    user_name varchar(255)
);