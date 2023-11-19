create table if not exists users (
    id BIGINT generated by default as identity primary key,
    name varchar(64),
    email varchar(128),
    CONSTRAINT UQ_EMAIL UNIQUE (email)
);

create table if not exists items (
    id BIGINT generated by default as identity primary key,
    name varchar(64),
    description varchar,
    available varchar,
    owner_id BIGINT references users(id) ON DELETE CASCADE
);

create table if not exists bookings (
    id BIGINT generated by default as identity primary key,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date TIMESTAMP WITHOUT TIME ZONE,
    item_id BIGINT REFERENCES items(id) ON DELETE CASCADE,
    booker_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    status varchar
    );

create TABLE IF NOT EXISTS comments (
    id BIGINT generated by default as identity primary key,
    text VARCHAR(1000) NOT NULL,
    item_id BIGINT REFERENCES items (id) ON DELETE CASCADE,
    author_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    created TIMESTAMP WITHOUT TIME ZONE
);

