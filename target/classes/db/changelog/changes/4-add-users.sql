-- Insert admin user (login: admin, password: admin)
INSERT INTO users (username, password, role)
VALUES
    ('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ADMIN'),
    ('user', '$2a$10$Pd8ItQSRY5JLVsxLDv.1s.GZzlnH0qzIbHv4C/Sc0M/Ynb0YcGAyS', 'USER');