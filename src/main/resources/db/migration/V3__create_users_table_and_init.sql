CREATE TABLE users
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);


CREATE TABLE user_roles
(
    user_id BIGINT       NOT NULL,
    roles   VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

INSERT INTO users (username, password)
VALUES ('admin', '$2a$10$PCH0aQHwIW3IRI.zCoNw7u6YKZgoE48f7TGupAyrfpz5pyyW0WWQ2'),
       ('user', '$2a$10$G.PZDlMIsKHwSqiWwNSwM.C1AVbQfot61TAhpd2PixRTY8r7axaFW');


INSERT INTO user_roles (user_id, roles)
VALUES ((SELECT id FROM users WHERE username = 'admin'), 'ADMIN'),
       ((SELECT id FROM users WHERE username = 'admin'), 'USER'),
       ((SELECT id FROM users WHERE username = 'user'), 'USER');
