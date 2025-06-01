CREATE TABLE fish
(
    id               INT NOT NULL AUTO_INCREMENT,
    catch_date       DATETIME(6) DEFAULT NULL,
    image_file_names VARCHAR(255) DEFAULT NULL,
    name             VARCHAR(255) DEFAULT NULL,
    price DOUBLE NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;