-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: localhost    Database: fishstore
-- ------------------------------------------------------
-- Server version	8.0.34


--
-- Table structure for table `fish`
--

DROP TABLE IF EXISTS `fish`;

CREATE TABLE `fish` (
  `id` int NOT NULL AUTO_INCREMENT,
  `catch_date` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` double NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `fish_images`;

CREATE TABLE `fish_images` (
    `id` int not NULL AUTO_INCREMENT,
    `fish_name` varchar(255) DEFAULT NULL,
    `fish_id` int NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`fish_id`) REFERENCES `fish` (`id`) ON DELETE CASCADE
    )ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
-- Dump completed on 2024-08-16 13:44:19
