DELETE FROM comments;
DELETE FROM compilation_to_events;
DELETE FROM compilations;
DELETE FROM requests;
DELETE FROM events;
DELETE FROM categories;
DELETE FROM locations;
DELETE FROM users;

INSERT INTO users (name, email) VALUES ('user1', 'user1@mail.ru');
INSERT INTO users (name, email) VALUES ('user2', 'user2@mail.ru');

INSERT INTO categories (name) VALUES ('лето');

INSERT INTO locations (lat, lon) VALUES ('75.75', '36.66');

INSERT INTO events (
    annotation,
    category,
    description,
    event_date,
    initiator,
    location,
    paid,
    participant_limit,
    request_moderation,
    title,
    state
) VALUES (
    'шоу красочных фонтанов',
    1,
    'в сопровождении классической музыки',
    '2024-03-03 10:00:00',
    2,
    1,
    false,
    0,
    true,
    'event1',
    'PUBLISHED'
);

INSERT INTO events (
    annotation,
    category,
    description,
    event_date,
    initiator,
    location,
    paid,
    participant_limit,
    request_moderation,
    title,
    state
) VALUES (
    'прогулка по Москва-реке',
    1,
    'на современном речном электротрамвае',
    '2024-03-03 10:00:00',
    2,
    1,
    false,
    0,
    true,
    'event2',
    'PENDING'
);