DROP TABLE IF EXISTS `fish`;

CREATE TABLE `fish`
(
    `id`              int NOT NULL AUTO_INCREMENT,
    `catch_date`      datetime(6) DEFAULT NULL,
    `image_file_name` varchar(255) DEFAULT NULL,
    `name`            varchar(255) DEFAULT NULL,
    `price` double NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;